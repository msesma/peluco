package eu.sesma.peluco.bt


interface ConnectionListener {

    fun onConnectionStateChange(connectionStateEnum: ConnectionState)

    fun onSerialReceived(text: String)
}
