package com.example.lcadmin.simpleui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView addressTextView;
    private ImageView staticMapImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        addressTextView = (TextView) findViewById(R.id.address);
        staticMapImage = (ImageView) findViewById(R.id.staticMapImage);

        String note = getIntent().getStringExtra("note");
        String storeInfo = getIntent().getStringExtra("storeInfo");

        Log.d("SimpleUI debug", note);
        Log.d("SimpleUI debug", storeInfo);

        String address = storeInfo.split(",")[1];
        addressTextView.setText(address);

        GeoCodingTask task = new GeoCodingTask();
        task.execute(address);
    }


    private class GeoCodingTask extends AsyncTask<String, Void, byte[]> {
        @Override
        protected byte[] doInBackground(String... params) {
            String address = params[0];
            double[] latLng = Utils.addressToLatLng(address);
            String url = Utils.getStaticMapUrl(latLng, 17);
            return Utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            staticMapImage.setImageBitmap(bm);
        }
    }
}
