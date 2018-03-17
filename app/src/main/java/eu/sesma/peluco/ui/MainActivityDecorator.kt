package eu.sesma.peluco.ui

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import eu.sesma.peluco.R
import eu.sesma.peluco.bt.BlunoLibrary
import eu.sesma.peluco.bt.BlunoLibrary.connectionStateEnum.*
import javax.inject.Inject

class MainActivityDecorator
@Inject
constructor(
        val activity: AppCompatActivity
) : MainActivityUserInterface {

    private val TAG = MainActivityDecorator::class.simpleName

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.buttonScan)
    lateinit var buttonScan: Button
     @BindView(R.id.serialReceivedText)
    lateinit var serialReceivedText: TextView

    private var delegate: MainActivityUserInterface.Delegate? = null
    private lateinit var context: Context

    fun bind(view: View) {
        ButterKnife.bind(this, view)
        context = view.context
        initToolbar()
    }

    fun dispose() {
        delegate = null
    }

    override fun initialize(delegate: MainActivityUserInterface.Delegate) {
        this.delegate = delegate
        toolbar.title = ""
    }

    override fun onConnectionStateChange(connectionStateEnum: BlunoLibrary.connectionStateEnum) {
        buttonScan.text = when (connectionStateEnum) {
            isConnected -> context.getString(R.string.connected)
            isConnecting -> context.getString(R.string.connecting)
            isToScan -> context.getString(R.string.scan)
            isScanning -> context.getString(R.string.scanning)
            isDisconnecting -> context.getString(R.string.disconnecting)
            else -> ""
        }
    }

    override fun onSerialReceived(text: String) {
        serialReceivedText.append(text)
        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
        (serialReceivedText.getParent() as ScrollView).fullScroll(View.FOCUS_DOWN)
    }

    override fun showError(error: Exception) {
    }

    @OnClick(R.id.buttonScan)
    fun onScanClick(){
        delegate?.onScan()
    }

    @OnClick(R.id.buttonSerialSend)
    fun onSendClick(){
        delegate?.onSend()
    }

    private fun initToolbar() {
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setIcon(R.mipmap.ic_launcher)
    }
}
