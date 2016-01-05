package com.example.lcadmin.simpleui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView addressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        addressTextView = (TextView) findViewById(R.id.address);

        String note = getIntent().getStringExtra("note");
        String storeInfo = getIntent().getStringExtra("storeInfo");

        Log.d("SimpleUI debug", note);
        Log.d("SimpleUI debug", storeInfo);

        String address = storeInfo.split(",")[1];
        addressTextView.setText(address);

        GeoCodingTask task = new GeoCodingTask();
        task.execute(address);
    }


    private class GeoCodingTask extends AsyncTask<String, Void, double[]> {
        @Override
        protected double[] doInBackground(String... params) {
            String address = params[0];
            return Utils.addressToLatLng(address);
        }

        @Override
        protected void onPostExecute(double[] latLng) {
            addressTextView.setText(latLng[0] + "," + latLng[1]);
        }
    }
}
