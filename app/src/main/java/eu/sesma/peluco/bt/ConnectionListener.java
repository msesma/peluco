package eu.sesma.peluco.bt;


public interface ConnectionListener {

    void onConnectionStateChange(BlunoLibrary.connectionStateEnum connectionStateEnum);

    void onSerialReceived(String text);
}
