package com.tutosoftware.manavoz2;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {


    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public ProgressDialog progress;
    public boolean isBtConnected = false;
    String address = null;
    Button speak;
    TextView VoiceToWordText;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent newint = getIntent();
        address = newint.getStringExtra(BtDevicesActivity.EXTRA_ADDRESS); //receive the address of the bluetooth device

        speak = (Button) findViewById(R.id.SpeakButton);
        VoiceToWordText = (TextView) findViewById(R.id.SpokenText);


        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();

            }
        });

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth == null) {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if (!myBluetooth.isEnabled()) {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }


    }
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, "Mano voz...", "Espera un poco!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Conexion cortada.");
                finish();
            } else {
                msg("Cihaza Baðlandý.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    VoiceToWordText.setText(result.get(0));

                    String input = VoiceToWordText.getText().toString();


                    if (input.equals("dispositivos")) {
                        Intent i = new Intent(MainActivity.this,BtDevicesActivity.class);
                        startActivity(i);

                    }

                    if (input.equals("conectar")) {
                        if(!isBtConnected ){
                            new ConnectBT().execute();}
                        else {

                            Toast.makeText(getApplicationContext(),"Dispositivo emparejado",Toast.LENGTH_LONG).show();

                        }}

                    if (input.equals("1")) {
                        if (btSocket != null) {
                            try {
                                btSocket.getOutputStream().write("1".getBytes());
                            } catch (IOException e) {
                                msg("Error");
                            }
                        }
                        //textoPulgar.setText("Dedo pulgar");
                    }

                    if (input.equals("2")) {
                        if (btSocket != null) {
                            try {
                                btSocket.getOutputStream().write("2".getBytes());
                            } catch (IOException e) {
                                msg("Error");
                            }
                        }
                        //textoIndice.setText("Dedo Indice");
                    }

                    if (input.equals("3")) {
                        if (btSocket != null) {
                            try {
                                btSocket.getOutputStream().write("3".getBytes());
                            } catch (IOException e) {
                                msg("Error");
                            }
                        }
                        //textoMedio.setText("Dedo medio");
                    }

                    if (input.equals("4")) {
                        if (btSocket != null) {
                            try {
                                btSocket.getOutputStream().write("5".getBytes());
                            } catch (IOException e) {
                                msg("Error");
                            }
                        }
                       // textoAnular.setText("Dedo anular");
                    }





                }

                break;
            }

        }
    }
}
