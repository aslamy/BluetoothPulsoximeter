package com.benjamin.bluetoothpulse;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Benjamin on 2015-12-08.
 */
public class PushDataToServer extends AsyncTask<Void, String, String>
{
    private  String serverIp;
    private int serverPort;
    private  String  fileName;
    private Context context ;
    private SharedPreferences preferences;
    public PushDataToServer(Context context){
        this.context = context ;
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        serverIp = preferences.getString("serverIp", "130.229.170.13");
        serverPort = preferences.getInt("serverPort", 6667);
        fileName = preferences.getString("fileName", "pleth_data.txt");
    }


    @Override
    protected String doInBackground(Void... params) {

        FileResource resource = new FileResource(context);

        File file = resource.getFileResource(fileName);


        if(file==null){
            publishProgress("File not find! Try again!");
            return null;
        }

        byte[] data = new byte[(int) file.length()];


        Socket socket = null;
        DataOutputStream dataOutputStream = null;

        try {

            new FileInputStream(file).read(data);

            socket = new Socket(InetAddress.getByName(serverIp), serverPort);

            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.write(data, 0, data.length);
            dataOutputStream.flush();
            publishProgress("File is send!");

        } catch (Exception e) {

            publishProgress("Error with sending file!,Check your internet connection!");
        } finally {
            try {
                if (dataOutputStream != null) dataOutputStream.close();
                if (socket != null) socket.close();
            } catch (IOException e) {}
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(context,values[0],Toast.LENGTH_LONG).show();


    }
}
