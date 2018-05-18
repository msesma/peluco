package eu.sesma.peluco.ui

import android.content.Intent
import android.util.Log
import eu.sesma.peluco.bt.BlunoLibrary
import eu.sesma.peluco.bt.ConnectionState
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

class MainActivityPresenter
@Inject
constructor(val blunoLibrary: BlunoLibrary) {

    companion object {
        private val TAG = MainActivityPresenter::class.simpleName
        private const val BAUDRATE = 19200
    }

    private var decorator: MainActivityUserInterface? = null

    private val delegate = object : MainActivityUserInterface.Delegate {
        override fun onScan() {
            blunoLibrary.buttonScanOnClickProcess()
        }

        override fun onSend() {
            sendData()
        }

        override fun onRefresh() {}
    }

    private fun MainActivityPresenter.sendData() {
        val json = JSONObject(mapOf(
                "bugfix" to "12345678901234567890", //For a unknown reason the character 17 is lost in transmission, so this is a workaround until I find the issue
                "time" to Date().time / 1000
        )).toString(0)
        Log.d(TAG, json)
        blunoLibrary.serialSend(json)
    }

    fun initialize(decorator: MainActivityUserInterface) {
        this.decorator = decorator
        this.decorator?.initialize(delegate)

        blunoLibrary.onCreateProcess()
        blunoLibrary.serialBegin(BAUDRATE)
    }

    fun onResume() {
        blunoLibrary.onResumeProcess()
    }

    fun onPause() {
        blunoLibrary.onPauseProcess()
    }

    fun onStop() {
        blunoLibrary.onStopProcess()
    }

    fun dispose() {
        this.decorator = null
        blunoLibrary.onDestroyProcess()
    }

    fun onEnableBtResult(requestCode: Int, resultCode: Int, data: Intent) {
        blunoLibrary.onActivityResultProcess(requestCode, resultCode, data)
    }

    fun onConnectionStateChange(connectionStateEnum: ConnectionState) {
        decorator?.onConnectionStateChange(connectionStateEnum)
    }

    fun onSerialReceived(text: String) {
        decorator?.onSerialReceived(text)
    }

}
