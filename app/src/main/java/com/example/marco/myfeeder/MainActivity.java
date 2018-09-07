package com.example.marco.myfeeder;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.marco.myfeeder.ble.BluetoothChatService;
import com.example.marco.myfeeder.bluetooth_ui.BluetoothConnect;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_GRANTED = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showBLE(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH},REQUEST_GRANTED);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_ADMIN},REQUEST_GRANTED);
        }
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                == PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                == PackageManager.PERMISSION_GRANTED)) {
            Intent intent = new Intent(this, BluetoothConnect.class);
            startActivity(intent);
        }

    }


    public void showFormatSelection(View view) {
        Intent intent = new Intent(this, FormatSelection.class);
        startActivity(intent);
    }

    public void sendReport(View view) {
        Intent intent = new Intent(this, SendReport.class);
        startActivity(intent);
        Log.d("SR", "going sr");
    }



}
