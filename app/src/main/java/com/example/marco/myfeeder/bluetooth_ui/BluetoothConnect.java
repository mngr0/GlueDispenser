package com.example.marco.myfeeder.bluetooth_ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.example.marco.myfeeder.Configuration;
import com.example.marco.myfeeder.QRActivity;
import com.example.marco.myfeeder.R;

import java.util.Set;


public class BluetoothConnect extends AppCompatActivity {

    private BluetoothAdapter mBtAdapter;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    RecyclerViewFragment mFragment;
    TextView mStateBT;
    TextView mStateQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connect);

        mStateBT=(TextView) findViewById(R.id.textStateBT);
        mStateQR=(TextView) findViewById(R.id.textStateQR);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mFragment = new RecyclerViewFragment();
            transaction.replace(R.id.sample_content_fragment, mFragment);
            transaction.commit();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBtAdapter.isEnabled()) {
            mStateBT.setText("BT enabled");
        }
        else{
            mStateBT.setText("BT disabled");
        }

        mStateQR.setText("press qr to read");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null) {
            Log.d("onActivityResult",data.toString());
            String address = data.getData().toString();

            Log.d("onActivityResult", address);
            mStateQR.setText("connecting to "+address);
            //TODO connect to that macaddress
            mFragment.connectToAddress(address);

        }
    }

    public void search(View view) {
        //populate list in RecyclerviewFragment fragment

        if (mBtAdapter == null) {

            Toast toast = Toast.makeText(this, "bluetooth not found on this device", Toast.LENGTH_LONG);
            toast.show();
        }


        if (!mBtAdapter.isEnabled()) {
            mBtAdapter.enable();
            Toast toast = Toast.makeText(this, "bluetooth enabled", Toast.LENGTH_SHORT);
            toast.show();
            Log.d("BT CNT", "enabled");
        }


        //check for bt
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
            Log.d("BT CNT", "cancelled");
        }

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        mFragment.reset();
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            //findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                Log.d("bt paired",device.toString());
                BluetoothDevice tmpDevice = mBtAdapter.getRemoteDevice(device.toString());
                Log.d("bt paired",device.getName());
                Log.d("bt paired",device.getAddress());
                Log.d("bt paired",""+(device==null));
                mFragment.addElementIn(tmpDevice.getName() , tmpDevice.getAddress(), tmpDevice);
            }
        }


        // Request discover from BluetoothAdapter

        mBtAdapter.startDiscovery();
        Toast toast1 = Toast.makeText(this, "bluetooth searching", Toast.LENGTH_SHORT);
        toast1.show();
        mStateBT.setText("searching");
        Log.d("BT CNT", "started");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    public static final int REQUEST_GRANTED = 1;

    public void qr(View view) {
        Log.d("BT QR", "qr called");
        //activate qr rec, try to connect, and send error if unable to
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},REQUEST_GRANTED);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(this, QRActivity.class);
            Log.d("BT QR", "intent created");
            startActivityForResult(intent, 43);
        }
    }


    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent\
                Log.d("BT CNT", "FOUND!!!");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already

                mFragment.addElementIn(device.getName() , device.getAddress(), device);
                //mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());

                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("BT CNT", "end");
                Toast toast = Toast.makeText(context, "bluetooth search completed", Toast.LENGTH_SHORT);
                toast.show();
                mStateBT.setText("search completed");
                if ((mFragment.getItemCount() == 0 )&& (!mBtAdapter.isDiscovering())) {
                    //  String noDevices = getResources().getText(R.string.none_found).toString();
                    //  mNewDevicesArrayAdapter.add(noDevices);
                    mFragment.addElementIn("nothing found","", null);
                }
            }
        }
    };

    private final BroadcastReceiver mQRReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("QRR",intent.getData().toString());
            String action = intent.getAction();

            // When discovery finds a device
            if (EXTRA_DEVICE_ADDRESS.equals(action)) {
                Log.d("QRRi",intent.getData().toString());
            }
        }
    };
}
