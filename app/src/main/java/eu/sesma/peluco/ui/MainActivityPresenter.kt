package eu.sesma.peluco.ui

import android.content.Intent
import android.util.Log
import eu.sesma.peluco.bt.BlunoLibrary
import java.util.*
import javax.inject.Inject

class MainActivityPresenter
@Inject
constructor(val blunoLibrary: BlunoLibrary) {

    companion object {
        private val TAG = MainActivityPresenter::class.simpleName
        private const val BAUDRATE = 9600
    }

    private var decorator: MainActivityUserInterface? = null

    private val delegate = object : MainActivityUserInterface.Delegate {
        override fun onScan() {
            blunoLibrary.buttonScanOnClickProcess()
        }

        override fun onSend() {
            val json = "{\"time\":${Date().time / 1000}}"
            Log.d(TAG, json)
            blunoLibrary.serialSend(json)
        }

        override fun onRefresh() {}
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

    fun onConnectionStateChange(connectionStateEnum: BlunoLibrary.connectionStateEnum) {
        decorator?.onConnectionStateChange(connectionStateEnum)
    }

    fun onSerialReceived(text: String) {
        decorator?.onSerialReceived(text)
    }

}
