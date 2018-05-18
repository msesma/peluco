package eu.sesma.peluco.bt

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import eu.sesma.peluco.R
import eu.sesma.peluco.bt.ConnectionState.*
import java.util.*
import javax.inject.Inject

class BlunoLibrary @Inject
constructor(private val activity: AppCompatActivity) {

    companion object {
        private val TAG = BlunoLibrary::class.java.simpleName

        private const val SERIAL_PORT_UUID = "0000dfb1-0000-1000-8000-00805f9b34fb"
        private const val COMMAND_UUID = "0000dfb2-0000-1000-8000-00805f9b34fb"
        private const val MODEL_NUMBER_UUID = "00002a24-0000-1000-8000-00805f9b34fb"
        private const val REQUEST_ENABLE_BT = 1

        private var characteristic: BluetoothGattCharacteristic? = null
        private var modelNumberCharacteristic: BluetoothGattCharacteristic? = null
        private var serialPortCharacteristic: BluetoothGattCharacteristic? = null
        private var commandCharacteristic: BluetoothGattCharacteristic? = null

        private fun makeGattUpdateIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
            return intentFilter
        }
    }

    private var mConnectionState = NULL
    var mBluetoothLeService: BluetoothLeService? = null
    var mScanDeviceDialog: AlertDialog? = null
    // Code to manage Service lifecycle.
    var mServiceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            Log.d(TAG, "mServiceConnection onServiceConnected")
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service

            if (!(mBluetoothLeService?.initialize() ?: false)) {
                Log.e(TAG, "Unable to initialize Bluetooth")
                activity.finish()
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(TAG, "mServiceConnection onServiceDisconnected")
            mBluetoothLeService = null
        }
    }
    private var mBaudrate = 115200    //set the default baud rate to 115200
    private val mPassword = "AT+PASSWOR=DFRobot\r\n"
    private var mBaudrateBuffer = "AT+CURRUART=$mBaudrate\r\n"
    private var mGattCharacteristics = ArrayList<ArrayList<BluetoothGattCharacteristic>>()
    private var mLeDeviceListAdapter: LeDeviceListAdapter? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mScanning = false
    private var mDeviceName: String? = null
    private var mDeviceAddress: String? = null
    private val mHandler = Handler()
    private val mConnectingOverTimeRunnable = Runnable {
        if (mConnectionState == CONNECTING) {
            mConnectionState = TO_SCAN
        }
        (activity as ConnectionListener).onConnectionStateChange(mConnectionState)
        mBluetoothLeService?.close()
    }
    private val mDisonnectingOverTimeRunnable = Runnable {
        if (mConnectionState == ConnectionState.DISCONNECTING) {
            mConnectionState = TO_SCAN
        }
        (activity as ConnectionListener).onConnectionStateChange(mConnectionState)
        mBluetoothLeService?.close()
    }
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        @SuppressLint("DefaultLocale")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.d(TAG, "mGattUpdateReceiver->onReceive->action=" + action)
            if (BluetoothLeService.ACTION_GATT_CONNECTED == action) {
                mHandler.removeCallbacks(mConnectingOverTimeRunnable)
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED == action) {
                mConnectionState = TO_SCAN
                (activity as ConnectionListener).onConnectionStateChange(mConnectionState)
                mHandler.removeCallbacks(mDisonnectingOverTimeRunnable)
                mBluetoothLeService?.close()
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED == action) {
                // Show all the supported services and characteristics on the user interface.
                mBluetoothLeService?.supportedGattServices?.let {
                    for (gattService in it) {
                        Log.d(TAG, "ACTION_GATT_SERVICES_DISCOVERED  " + gattService.uuid.toString())
                    }
                }

                getGattServices(mBluetoothLeService?.supportedGattServices)
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE == action) {
                if (characteristic === modelNumberCharacteristic) {
                    if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toUpperCase()
                                    .startsWith("DF BLUNO")) {
                        mBluetoothLeService?.setCharacteristicNotification(characteristic, false)
                        characteristic = commandCharacteristic
                        characteristic?.setValue(mPassword)
                        mBluetoothLeService?.writeCharacteristic(characteristic)
                        characteristic?.setValue(mBaudrateBuffer)
                        mBluetoothLeService?.writeCharacteristic(characteristic)
                        characteristic = serialPortCharacteristic
                        mBluetoothLeService?.setCharacteristicNotification(characteristic, true)
                        mConnectionState = CONNECTED
                        (activity as ConnectionListener).onConnectionStateChange(mConnectionState)
                    } else {
                        Toast.makeText(activity, "Please select DFRobot devices",
                                Toast.LENGTH_SHORT).show()
                        mConnectionState = TO_SCAN
                        (activity as ConnectionListener).onConnectionStateChange(mConnectionState)
                    }
                } else if (characteristic === serialPortCharacteristic) {
                    (activity as ConnectionListener)
                            .onSerialReceived(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
                }

                Log.d(TAG, "displayData " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
            }
        }
    }

    // Device scan callback.
    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        activity.runOnUiThread {
            Log.d(TAG, "mLeScanCallback onLeScan run ")
            mLeDeviceListAdapter?.addDevice(device)
            mLeDeviceListAdapter?.notifyDataSetChanged()
        }
    }

    fun serialSend(theString: String) {
        if (mConnectionState == CONNECTED) {
            characteristic?.setValue(theString)
            mBluetoothLeService?.writeCharacteristic(characteristic)
        }
    }

    fun serialBegin(baud: Int) {
        mBaudrate = baud
        mBaudrateBuffer = "AT+CURRUART=$mBaudrate\r\n"
    }

    fun onCreateProcess() {
        if (!initiate()) {
            Toast.makeText(activity, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT)
                    .show()
            activity.finish()
        }

        val gattServiceIntent = Intent(activity, BluetoothLeService::class.java)
        activity.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)

        // Initializes list view adapter.
        mLeDeviceListAdapter = LeDeviceListAdapter(clickListener)
        // Initializes and show the scan Device Dialog
        mScanDeviceDialog = AlertDialog.Builder(activity)
                .setTitle("BLE Device Scan...")
                .setAdapter(mLeDeviceListAdapter, DialogInterface.OnClickListener { dialog, which ->
                    val device = mLeDeviceListAdapter?.getDevice(which) ?: return@OnClickListener
                    scanLeDevice(false)

                    if (device.name == null || device.address == null) {
                        mConnectionState = TO_SCAN
                        (activity as ConnectionListener)
                                .onConnectionStateChange(mConnectionState)
                    } else {

                        Log.d(TAG, "onListItemClick " + device.name.toString())

                        Log.d(TAG, "Device Name:" + device.name + "   " + "Device Name:"
                                + device.address)

                        mDeviceName = device.name.toString()
                        mDeviceAddress = device.address.toString()

                        if (mBluetoothLeService?.connect(mDeviceAddress) ?: false) {
                            Log.d(TAG, "Connect request success")
                            mConnectionState = CONNECTING
                            (activity as ConnectionListener)
                                    .onConnectionStateChange(mConnectionState)
                            mHandler.postDelayed(mConnectingOverTimeRunnable, 10000)
                        } else {
                            Log.d(TAG, "Connect request fail")
                            mConnectionState = TO_SCAN
                            (activity as ConnectionListener)
                                    .onConnectionStateChange(mConnectionState)
                        }
                    }
                })
                .setOnCancelListener {
                    Log.d(TAG, "mBluetoothAdapter.stopLeScan")

                    mConnectionState = TO_SCAN
                    (activity as ConnectionListener).onConnectionStateChange(mConnectionState)
                    mScanDeviceDialog?.dismiss()

                    scanLeDevice(false)
                }.create()
    }

    fun onResumeProcess() {
        Log.d(TAG, "BlUNOActivity onResume")
        // Ensures Bluetooth is enabled on the device. If Bluetooth is not
        // currently enabled,
        // fire an intent to display a dialog asking the user to grant
        // permission to enable it.
        if (!(mBluetoothAdapter?.isEnabled ?: false)) {
            val enableBtIntent = Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        activity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
    }

    fun onPauseProcess() {
        Log.d(TAG, "BLUNOActivity onPause")
        scanLeDevice(false)
        activity.unregisterReceiver(mGattUpdateReceiver)
        mLeDeviceListAdapter?.clear()
        mConnectionState = TO_SCAN
        (activity as ConnectionListener).onConnectionStateChange(mConnectionState)
        mScanDeviceDialog?.dismiss()
        if (mBluetoothLeService != null) {
            mBluetoothLeService?.disconnect()
            mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000)
            //			mBluetoothLeService.close();
        }
        characteristic = null
    }

    fun onStopProcess() {
        Log.d(TAG, "MiUnoActivity onStop")
        if (mBluetoothLeService != null) {
            //			mBluetoothLeService.disconnect();
            //            mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000);
            mHandler.removeCallbacks(mDisonnectingOverTimeRunnable)
            mBluetoothLeService?.close()
        }
        characteristic = null
    }

    fun onDestroyProcess() {
        activity.unbindService(mServiceConnection)
        mBluetoothLeService = null
    }

    fun onActivityResultProcess(requestCode: Int, resultCode: Int, data: Intent) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            activity.finish()
            return
        }
    }

    fun initiate(): Boolean {
        // Use this check to determine whether BLE is supported on the device.
        // Then you can
        // selectively disable BLE-related features.
        if (!activity.packageManager.hasSystemFeature(
                        PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to
        // BluetoothAdapter through BluetoothManager.
        val bluetoothManager = activity
                .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        // Checks if Bluetooth is supported on the device.
        return if (mBluetoothAdapter == null) {
            false
        } else true
    }

    fun buttonScanOnClickProcess() {
        when (mConnectionState) {
            NULL -> {
                mConnectionState = SCANNING
                (activity as ConnectionListener).onConnectionStateChange(mConnectionState)
                scanLeDevice(true)
                mScanDeviceDialog?.show()
            }
            TO_SCAN -> {
                mConnectionState = SCANNING
                (activity as ConnectionListener).onConnectionStateChange(mConnectionState)
                scanLeDevice(true)
                mScanDeviceDialog?.show()
            }
            CONNECTED -> {
                mBluetoothLeService?.disconnect()
                mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000)

                //			mBluetoothLeService.close();
                mConnectionState = DISCONNECTING
                (activity as ConnectionListener).onConnectionStateChange(mConnectionState)
            }
            SCANNING, CONNECTING, DISCONNECTING -> {
            }
        }
    }

    fun scanLeDevice(enable: Boolean) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.

            Log.d(TAG, "mBluetoothAdapter.startLeScan")

            if (mLeDeviceListAdapter != null) {
                mLeDeviceListAdapter?.clear()
                mLeDeviceListAdapter?.notifyDataSetChanged()
            }

            if (!mScanning) {
                mScanning = true
                mBluetoothAdapter?.startLeScan(mLeScanCallback)
            }
        } else {
            if (mScanning) {
                mScanning = false
                mBluetoothAdapter?.stopLeScan(mLeScanCallback)
            }
        }
    }

    private fun getGattServices(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) {
            return
        }
        var uuid: String? = null
        modelNumberCharacteristic = null
        serialPortCharacteristic = null
        commandCharacteristic = null
        mGattCharacteristics = ArrayList()

        // Loops through available GATT Services.
        for (gattService in gattServices) {
            uuid = gattService.uuid.toString()
            Log.d(TAG, "displayGattServices + uuid=$uuid")

            val gattCharacteristics = gattService.characteristics
            val charas = ArrayList<BluetoothGattCharacteristic>()

            // Loops through available Characteristics.
            for (gattCharacteristic in gattCharacteristics) {
                charas.add(gattCharacteristic)
                uuid = gattCharacteristic.uuid.toString()
                if (uuid == MODEL_NUMBER_UUID) {
                    modelNumberCharacteristic = gattCharacteristic
                    Log.d(TAG, "mModelNumberCharacteristic  " + modelNumberCharacteristic?.uuid
                            .toString())
                } else if (uuid == SERIAL_PORT_UUID) {
                    serialPortCharacteristic = gattCharacteristic
                    Log.d(TAG, "mSerialPortCharacteristic  " + serialPortCharacteristic?.uuid
                            .toString())
                    //                    updateConnectionState(R.string.comm_establish);
                } else if (uuid == COMMAND_UUID) {
                    commandCharacteristic = gattCharacteristic
                    Log.d(TAG, "mSerialPortCharacteristic  " + serialPortCharacteristic?.uuid
                            .toString())
                    //                    updateConnectionState(R.string.comm_establish);
                }
            }
            mGattCharacteristics.add(charas)
        }

        if (modelNumberCharacteristic == null || serialPortCharacteristic == null
                || commandCharacteristic == null) {
            Toast.makeText(activity, "Please select DFRobot devices", Toast.LENGTH_SHORT).show()
            mConnectionState = TO_SCAN
            (activity as ConnectionListener).onConnectionStateChange(mConnectionState)
        } else {
            characteristic = modelNumberCharacteristic
            mBluetoothLeService?.setCharacteristicNotification(characteristic, true)
            mBluetoothLeService?.readCharacteristic(characteristic)
        }
    }
}
