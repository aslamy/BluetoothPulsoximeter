package com.benjamin.bluetoothpulse;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Benjamin on 2015-12-07.
 */
public class ButtonListener implements View.OnClickListener {

   PullDataFromBluetooth pullDataFromBluetooth ;
    Context context ;
    BluetoothDevice bluetoothDevice ;
    PushDataToServer pushDataToServer ;

    public ButtonListener(Context context,BluetoothDevice bluetoothDevice, PullDataFromBluetooth pullDataFromBluetooth){
        this.pullDataFromBluetooth = pullDataFromBluetooth;
        this.context = context ;
        this.bluetoothDevice = bluetoothDevice ;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.store_data_btn:
                if(pullDataFromBluetooth!=null){
                    if(pullDataFromBluetooth.getStatus()== AsyncTask.Status.FINISHED){
                        pullDataFromBluetooth = new PullDataFromBluetooth(context,bluetoothDevice);
                        pullDataFromBluetooth.execute();
                        Toast.makeText(context, "Please waite for data!", Toast.LENGTH_SHORT).show();

                    }else if(pullDataFromBluetooth.getStatus()== AsyncTask.Status.PENDING){
                        pullDataFromBluetooth.execute();
                        Toast.makeText(context, "Please waite for data!", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.stop_btn:
                if(pullDataFromBluetooth!=null){
                    pullDataFromBluetooth.cancel(true);
                    Toast.makeText(context, "Data storing is stopped!", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.send_data_btn:
                if(pullDataFromBluetooth!=null && pullDataFromBluetooth.getStatus()== AsyncTask.Status.RUNNING){
                    Toast.makeText(context,"Please stop reading data!",Toast.LENGTH_SHORT).show();
                    break;
                }else if(pushDataToServer==null || pushDataToServer.getStatus()== AsyncTask.Status.FINISHED){
                    pushDataToServer = new PushDataToServer(context);
                    pushDataToServer.execute();
                    break;

                }else if (pushDataToServer.getStatus()== AsyncTask.Status.RUNNING){

                    Toast.makeText(context,"Please waite!",Toast.LENGTH_SHORT).show();
                    break;

            }


                break;
        }

    }
}
