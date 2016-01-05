package com.example.lcadmin.simpleui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class OrderDetailActivity extends AppCompatActivity {

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        String note = getIntent().getStringExtra("note");
        String storeInfo = getIntent().getStringExtra("storeInfo");

        Log.d("SimpleUI debug", note);
        Log.d("SimpleUI debug", storeInfo);

        address = storeInfo.split(",")[1];

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Utils.addressToLatLng(address);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

            }
        };
        task.execute();
    }
}
