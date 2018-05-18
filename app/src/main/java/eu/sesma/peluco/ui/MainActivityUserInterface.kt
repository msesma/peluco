package eu.sesma.peluco.ui

import eu.sesma.peluco.bt.ConnectionState

interface MainActivityUserInterface {

    fun initialize(delegate: Delegate)

    fun onConnectionStateChange(connectionStateEnum: ConnectionState)

    fun onSerialReceived(text: String)

    fun showData(text: String)

    fun showError(error: Exception)

    interface Delegate {

        fun onRefresh()

        fun onScan()

        fun onSend()
    }
}
