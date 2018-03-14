package eu.sesma.peluco

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ScrollView
import eu.sesma.peluco.bt.BlunoLibrary
import eu.sesma.peluco.bt.BlunoLibrary.connectionStateEnum.*
import eu.sesma.peluco.bt.ConnectionListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ConnectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onConnectionStateChange(connectionStateEnum: BlunoLibrary.connectionStateEnum) {
        when (connectionStateEnum) {
            isConnected -> buttonScan.text = "Connected"
            isConnecting -> buttonScan.text = "Connecting"
            isToScan -> buttonScan.text = "Scan"
            isScanning -> buttonScan.text = "Scanning"
            isDisconnecting -> buttonScan.text = "isDisconnecting"
            else -> {
            }
        }
    }

    override fun onSerialReceived(text: String) {
        serialReceivedText.append(text)
        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
        (serialReceivedText.getParent() as ScrollView).fullScroll(View.FOCUS_DOWN)
    }
}
