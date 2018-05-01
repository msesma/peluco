package eu.sesma.peluco.ui

import eu.sesma.peluco.bt.BlunoLibrary

interface MainActivityUserInterface {

    fun initialize(delegate: Delegate)

    fun onConnectionStateChange(connectionStateEnum: BlunoLibrary.connectionStateEnum)

    fun onSerialReceived(text: String)

    fun showData(text: String)

    fun showError(error: Exception)

    interface Delegate {

        fun onRefresh()

        fun onScan()

        fun onSend()
    }
}
