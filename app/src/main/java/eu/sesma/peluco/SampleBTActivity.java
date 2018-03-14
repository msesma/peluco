package eu.sesma.peluco;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import eu.sesma.peluco.bt.BlunoLibrary;
import eu.sesma.peluco.bt.ConnectionListener;

public class SampleBTActivity extends AppCompatActivity implements ConnectionListener {
	private Button buttonScan;
	private Button buttonSerialSend;
	private EditText serialSendText;
	private TextView serialReceivedText;

	private BlunoLibrary blunoLibrary = new BlunoLibrary(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_bt);
		blunoLibrary.onCreateProcess();														//onCreate Process by BlunoLibrary


		blunoLibrary.serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

        serialReceivedText=(TextView) findViewById(R.id.serialReveicedText);	//initial the EditText of the received data
        serialSendText=(EditText) findViewById(R.id.serialSendText);			//initial the EditText of the sending data

        buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);		//initial the button for sending the data
        buttonSerialSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				blunoLibrary.serialSend(serialSendText.getText().toString());				//send the data to the BLUNO
			}
		});

        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				blunoLibrary.buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
			}
		});
	}

	@Override
	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		blunoLibrary.onResumeProcess();														//onResume Process by BlunoLibrary
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		blunoLibrary.onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
		blunoLibrary.onPauseProcess();														//onPause Process by BlunoLibrary
    }
	
	@Override
	protected void onStop() {
		super.onStop();
		blunoLibrary.onStopProcess();														//onStop Process by BlunoLibrary
	}
    
	@Override
    protected void onDestroy() {
        super.onDestroy();
		blunoLibrary.onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

	@Override
	public void onConnectionStateChange(BlunoLibrary.connectionStateEnum connectionStateEnum) {//Once connection state changes, this function will be called
		switch (connectionStateEnum) {											//Four connection state
		case isConnected:
			buttonScan.setText("Connected");
			break;
		case isConnecting:
			buttonScan.setText("Connecting");
			break;
		case isToScan:
			buttonScan.setText("Scan");
			break;
		case isScanning:
			buttonScan.setText("Scanning");
			break;
		case isDisconnecting:
			buttonScan.setText("isDisconnecting");
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(String text) {							//Once connection data received, this function will be called
		// TODO Auto-generated method stub
		serialReceivedText.append(text);							//append the text into the EditText
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
		((ScrollView)serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);
	}

}