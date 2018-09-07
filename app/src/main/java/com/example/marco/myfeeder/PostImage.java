package com.example.marco.myfeeder;


import android.os.AsyncTask;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostImage extends AsyncTask<Void, Void, Void> {
    String target;
    String log;
    PostImage( String arg,String mLog){
        target=arg;
        log=mLog;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String response = postImage(target);
            Log.i("altadns", response);
        } catch (Exception e) {
            Log.d("BACKGROUND", e.toString());
        }
        return null;
    }




    public String postImage(String filepath) throws Exception {

        Log.d("upload", "starting, received " + filepath);
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "-----------------------------";
        String boundary =  Long.toString(System.currentTimeMillis());
        String lineEnd = "\r\n";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        File file = new File(filepath);

        if(file.exists()){

            URL url = new URL("http://altaserver.ddns.net/Marco/upload.php");
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);

            Log.d("UPLOAD", "name = "+q[idx]);
            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"fileToUpload\"; filename=\"" + q[idx] +"\"" + lineEnd);
            outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
            outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes("Log: "+ lineEnd + log + lineEnd);



            FileInputStream fileInputStream = new FileInputStream(file);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            Log.d("UPLOAD", "first buffersize="+ bufferSize);
            Log.d("UPLOAD", "next bytesAvailable="+bytesAvailable);
            buffer = new byte[bufferSize];
            Log.d("UPLOAD", "outputstream ready");
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            Log.d("UPLOAD", "bytes read "+bytesRead);
            while(bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                Log.d("UPLOAD", "next buffersize="+ bufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            fileInputStream.close();

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"submit\"" + lineEnd+lineEnd);
            outputStream.writeBytes("Upload Image"+lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + "--");
            inputStream = connection.getInputStream();

            outputStream.flush();
            outputStream.close();

            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                inputStream.close();
                connection.disconnect();
                Log.d("upload", "done");
                return response.toString();
            }else{
                connection.disconnect();
                return "";
            }
        }else{
            throw new FileNotFoundException();
        }

    }



    @Override
    protected void onPostExecute(Void result) {
    }
}