package eu.sesma.peluco.bt

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import eu.sesma.peluco.R

class LeViewHolder(view: View,
                   val listener: ClickListener) : RecyclerView.ViewHolder(view) {
    @BindView(R.id.device_name)
    lateinit var deviceName: TextView
    @BindView(R.id.device_address)
    lateinit var deviceAddress: TextView

    init {
        ButterKnife.bind(this, view)
    }

    fun bind(bluetoothDevice: BluetoothDevice) {
        deviceName.text = bluetoothDevice.name
        deviceAddress.text = bluetoothDevice.address
    }

    @OnClick(R.id.row)
    fun onRowClick() {
        listener.onClick()
    }
}
