package com.example.lcadmin.simpleui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        String note = getIntent().getStringExtra("note");
        String storeInfo = getIntent().getStringExtra("storeInfo");

        Log.d("SimpleUI debug", note);
        Log.d("SimpleUI debug", storeInfo);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url =
                        "https://maps.googleapis.com/maps/api/geocode/json?address=taipei101";
                byte[] bytes = Utils.urlToBytes(url);
                String result = new String(bytes);
                Log.d("SimpleUI debug", result);
            }
        });
        thread.start();
    }
}
