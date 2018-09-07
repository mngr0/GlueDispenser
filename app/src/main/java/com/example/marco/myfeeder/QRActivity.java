package com.example.marco.myfeeder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.marco.myfeeder.bluetooth_ui.BluetoothConnect;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QRActivity extends AppCompatActivity {


    private BarcodeDetector detector;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("QRA", "on create started");
        setContentView(R.layout.activity_qr);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        if (!detector.isOperational()) {
            Log.e("QRA", "Detector di codici a barre non attivabile");
            Intent intent = new Intent();
            intent.putExtra(BluetoothConnect.EXTRA_DEVICE_ADDRESS, "permission error");
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        }

        cameraSource = new CameraSource
                .Builder(this, detector)
                .setAutoFocusEnabled(true)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                activateCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> items = detections.getDetectedItems();

                if (items.size() != 0)
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String barcode = items.valueAt(0).displayValue;
                            Log.d("QRA",barcode);
                            Intent intent = new Intent();
                            intent.setData(Uri.parse(barcode));
                            //intent.putExtra("qrcode", barcode); maybe a day may be usefull
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    });

            }
        });

    }

    private void activateCamera() {

        // verifichiamo che sia stata concessa la permission CAMERA

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        try {
            cameraSource.start(surfaceView.getHolder());
        } catch (IOException e) {
            Log.e("QRA", "Errore nell'avvio della fotocamera");
        }


    }
}