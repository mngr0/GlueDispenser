package com.example.marco.myfeeder;


import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.marco.myfeeder.ble.BluetoothChatService;

public class SendReport extends AppCompatActivity {
    private boolean logReceived =false;
    private boolean imageChosen=false;
    private String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast toast = Toast.makeText(getApplicationContext(), "photo taken!", Toast.LENGTH_SHORT);
        toast.show();
        if(resultCode == RESULT_OK) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            imageChosen=true;
        }
        else{
            Log.e("LOADING", "return code not ok");
        }
    }

    public static final int GET_FROM_GALLERY = 3;
    public void dispatchTakePictureIntent(View view) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    private class LogReceivedListenerClass implements LogReceivedListener{
        @Override
        public void onEvent() {
            Toast toast = Toast.makeText(getApplicationContext(), "log received", Toast.LENGTH_SHORT);
            toast.show();
            logReceived =true;
        }
    }


    public void requestLog(View view){
        if (BluetoothChatService.getInstance().isConnected()) {
            Toast toast = Toast.makeText(getApplicationContext(), "log requested", Toast.LENGTH_SHORT);
            toast.show();
            Configuration.readLog(new LogReceivedListenerClass());
            logReceived = false;
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "not connected to any device", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void send(View view){
        PostImage pi= new PostImage(picturePath,Configuration.getLog());
        pi.execute();
    }


}