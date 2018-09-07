package com.example.marco.myfeeder.format_edit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.app.AlertDialog;


import com.example.marco.myfeeder.Configuration;
import com.example.marco.myfeeder.R;

public class FormatEdit extends AppCompatActivity {
    RecyclerEditFragment mFragment;

    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_format_edit);
        Intent intent = getIntent();
        Uri data = intent.getData();
        index=Integer.parseInt(data.toString());
        Log.d("test",data.toString());
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mFragment = new RecyclerEditFragment();
            mFragment.setIndex(index);
            transaction.replace(R.id.edit_content_fragment, mFragment);
            transaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void addElement(View view){
        mFragment.addElementTail();
    }


    public void returnResult(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save as");
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.text_name_input, (ViewGroup)this.mFragment.getView(), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        input.setText(Configuration.getName(index));
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Configuration.setTimes(index,mFragment.getTimes());
                Configuration.setName(index, input.getText().toString());
                Intent result = new Intent();
                result.putExtra("index",index);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
