package com.example.marco.myfeeder.bluetooth_ui;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.marco.myfeeder.R;
import com.example.marco.myfeeder.ble.BluetoothChatService;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private static final String TAG = "CustomAdapter";
    private ArrayList<String> mNames;
    private ArrayList<String> mAddresses;
    private ArrayList<BluetoothDevice> mDevices;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextName;
        private final TextView mTextAddress;
        private final Button mButtonConnect;

        private ViewHolder(View v) {
            super(v);
            mTextName = (TextView) v.findViewById(R.id.textBTName);
            mTextAddress = (TextView) v.findViewById(R.id.textBTAddress);
            mButtonConnect = (Button) v.findViewById(R.id.buttonBTConnect);
        }

        public void setName(String name) {
            mTextName.setText(name);
        }
        private void setAddress(String name) {
            mTextAddress.setText(name);
        }
        private void setOnButtonClickListener(View.OnClickListener listener) {
            mButtonConnect.setOnClickListener(listener);
        }
    }

    public CustomAdapter() {
        mNames = new ArrayList<>();
        mAddresses = new ArrayList<>();
        mDevices = new ArrayList<>();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_text_row, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        viewHolder.setName(mNames.get(position));
        viewHolder.setAddress(mAddresses.get(position));
        viewHolder.setOnButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BTC", "connect" + mAddresses.get(position));
                if (mDevices.get(position) != null) {
                    BluetoothChatService.getInstance().connect(mDevices.get(position));
                } else {
                    Log.e("BTC", "device null");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public void addElement(String name, String address, BluetoothDevice mDevice) {
        mNames.add(name);
        mAddresses.add(address);
        mDevices.add(mDevice);
        notifyItemInserted(mNames.size() - 1);
    }

    public boolean isListed(BluetoothDevice bd){
        return mDevices.contains(bd);
    }

    public void connectToAddress(String address){
        int position = mAddresses.indexOf(address);
        if(position > -1) {
            BluetoothChatService.getInstance().connect(mDevices.get(position));
            Log.d("CSTM-ADAP","connecting");
        }else{
            Log.d("CSTM-ADAP","address not found");
        }
    }

    public void clear() {
        mAddresses = new ArrayList<>();
        mNames = new ArrayList<>();
        mDevices = new ArrayList<>();
        notifyDataSetChanged();
    }


}
