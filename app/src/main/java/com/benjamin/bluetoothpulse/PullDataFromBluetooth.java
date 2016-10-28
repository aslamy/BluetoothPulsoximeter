package com.benjamin.bluetoothpulse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;


/**
 * Created by Benjamin on 2015-12-07.
 */
public class PullDataFromBluetooth extends AsyncTask<Void, Integer, Void> {

    //byte number 7 if 0x76 options is enabled, if 0x78 options is disabled.

    private static final UUID UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final byte[] FORMAT = { 0x02, 0x70, 0x04, 0x02, 0x02, 0x00, 0x78, 0x03 };
    private static final byte ACK = 0x06;
    private  String fileName;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;
    private MainActivity mainActivity ;
    TextView textView ;
    private File file ;
    private int measureTime ;
    private SharedPreferences preferences;
    int pleth ;
    int pulse;
    long startTime ;


    PullDataFromBluetooth(Context context, BluetoothDevice bluetoothDevice){
        this.bluetoothDevice = bluetoothDevice;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mainActivity = (MainActivity)context ;
        textView =(TextView)mainActivity.findViewById(R.id.data_text);
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        fileName = preferences.getString("fileName", "pleth_data.txt");
        measureTime = preferences.getInt("measure", 1000000000)*1000;
        FileResource fileResource = new FileResource(context);
        file = fileResource.createFileResource(fileName);
    }


    @Override
    protected Void doInBackground(Void... params) {
        bluetoothAdapter.cancelDiscovery();

        BluetoothSocket bluetoothSocket = null;
        FileWriter fileWriter = null ;

        try {
            fileWriter = new FileWriter(file,true);
            fileWriter.flush();
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID);
            bluetoothSocket.connect();

            InputStream is = bluetoothSocket.getInputStream();
            OutputStream os = bluetoothSocket.getOutputStream();

            os.write(FORMAT);
            os.flush();
            byte[] reply = new byte[1];
            int pulseDate = 0;

            do {
                is.read(reply);
                if (reply[0] == ACK) {
                    startTime = System.currentTimeMillis();

                    while (!isCancelled() && System.currentTimeMillis() - startTime < measureTime) {
                        byte[] frame = new byte[5];
                        // 5 bytes per frame
                        is.read(frame);

                        pleth = unsignedByteToInt(frame[2]);
                        fileWriter.write(String.valueOf(pleth) + "\n");
                        fileWriter.flush();
                        mainActivity.getQueue().add(pleth);


                        //Check bit 0 status. if 1 this is first frame!
                        //Is frame ok! then frame[0] is set to 1.
                        if ((frame[1] & 0x01) == 1 && frame[0]==1) {

                            int pulseMSB =(frame[3] & 0x03) * 128;
                            is.read(frame);
                            int pulseLSB = unsignedByteToInt(frame[3]);
                            pulse = pulseMSB + pulseLSB;

                            pleth = unsignedByteToInt(frame[2]);
                            fileWriter.write(String.valueOf(pleth) + "\n");
                            fileWriter.flush();
                            mainActivity.getQueue().add(pleth);

                            if(pulse<511) {
                                pulseDate = pulse;
                            }
                        }
                        publishProgress(pulseDate);
                    }
                }
            }while (reply[0]!=ACK && !isCancelled());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bluetoothSocket != null)
                    bluetoothSocket.close();
                if(fileWriter!=null)
                    fileWriter.close();
            } catch (Exception e) {
            }
        }

    return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mainActivity.updateGraph();
        textView.setText(String.valueOf("Heart rate: "+values[0]));
    }


    private int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

}
