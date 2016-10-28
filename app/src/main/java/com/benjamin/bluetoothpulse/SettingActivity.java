package com.benjamin.bluetoothpulse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String serverIp = preferences.getString("serverIp", "130.229.170.13");
        int serverPort = preferences.getInt("serverPort", 6667);
        String fileName = preferences.getString("fileName", "pleth_data.txt");
        int measure = preferences.getInt("measure", 10000000);

        final EditText editTextIp = (EditText)findViewById(R.id.editTextIp);
        final EditText editTextPort = (EditText)findViewById(R.id.editTextPort);
        final EditText editTextMeasure = (EditText)findViewById(R.id.editTextMeasure);
        final EditText editTextFileName = (EditText)findViewById(R.id.editTextFileName);
        Button saveButton = (Button)findViewById(R.id.save);


        editTextIp.setText(serverIp);
        editTextPort.setText(String.valueOf(serverPort));
        editTextFileName.setText(fileName);
        editTextMeasure.setText(String.valueOf(measure));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("serverIp",editTextIp.getText().toString());
                editor.putString("fileName",editTextFileName.getText().toString());
                editor.putInt("serverPort", Integer.valueOf(editTextPort.getText().toString()));
                editor.putInt("measure", Integer.valueOf(editTextMeasure.getText().toString()));
                editor.apply();
                Toast.makeText(SettingActivity.this, "Setting is Saved!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

}
