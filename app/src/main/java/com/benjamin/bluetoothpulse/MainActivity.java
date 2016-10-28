package com.benjamin.bluetoothpulse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Queue<Integer> queue;
    BluetoothAdapter bluetoothAdapter ;
    ButtonListener buttonListener ;
    PullDataFromBluetooth pullData;
    private BluetoothDevice bluetoothDevice ;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor ;
    private LineGraphSeries<DataPoint> series;

    private long lastX;
    GraphView graph;
    Viewport viewport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        queue = new ArrayDeque<Integer>();

        preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = preferences.edit();

        graph = (GraphView)findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();
        setGraphViewSettings();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            showToast("This device do not support Bluetooth");
            this.finish();
        }
    }


    public void updateGraph(){
        if(queue.size()>0){
        series.appendData(new DataPoint(lastX++, queue.poll()), true, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pullData!=null){
            pullData.cancel(true);
         }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initBluetooth();
        pullData = new PullDataFromBluetooth(this,bluetoothDevice);
        intUi();
    }

    private void initBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, 1);
        } else {
            getPulseDevice();
        }
    }

    private  void intUi(){

        buttonListener = new ButtonListener(this,bluetoothDevice,pullData);
        Button btnStoreDate = (Button) findViewById(R.id.store_data_btn);
        Button btnStop = (Button) findViewById(R.id.stop_btn);
        Button btnSendData = (Button) findViewById(R.id.send_data_btn);

        btnStoreDate.setOnClickListener(buttonListener);
        btnStop.setOnClickListener(buttonListener);
        btnSendData.setOnClickListener(buttonListener);
    }

    private void setGraphViewSettings(){
        graph.addSeries(series);
        viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinY(50);
        viewport.setMaxY(150);
        viewport.setMinX(0);
        viewport.setMaxX(300);
       viewport.setScrollable(true);
    }




    private void getPulseDevice() {
        bluetoothDevice = null;
        Set<BluetoothDevice> pairedBTDevices = bluetoothAdapter
                .getBondedDevices();
        if (pairedBTDevices.size() > 0) {
            for (BluetoothDevice device : pairedBTDevices) {
                String name = device.getName();
                if (name.contains("Nonin")) {
                    bluetoothDevice = device;
                    showToast("Paired device: " + name);
                    return;
                }
            }
        }
        if (bluetoothDevice == null) {
            showToast("No paired Nonin devices found!\r\n"
                    + "Please pair a Nonin BT device with this device.");
            Intent pairDevice = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivityForResult(pairDevice, 30);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void showToast(final CharSequence msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public Queue<Integer> getQueue() {
        return queue;
    }

}
