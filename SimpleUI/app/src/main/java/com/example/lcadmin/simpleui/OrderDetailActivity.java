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
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView addressTextView;
    private ImageView staticMapImage;
    private Switch mapSwitch;
    private WebView staticMapWeb;

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        addressTextView = (TextView) findViewById(R.id.address);
        staticMapImage = (ImageView) findViewById(R.id.staticMapImage);
        staticMapWeb = (WebView) findViewById(R.id.webView);
        staticMapWeb.setVisibility(View.GONE);

        mapSwitch = (Switch) findViewById(R.id.mapSwitch);
        mapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    staticMapImage.setVisibility(View.GONE);
                    staticMapWeb.setVisibility(View.VISIBLE);
                } else {
                    staticMapImage.setVisibility(View.VISIBLE);
                    staticMapWeb.setVisibility(View.GONE);
                }
            }
        });

        mapFragment =(SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.googleMap);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
            }
        });

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

        private String url;
        private double[] latLng;

        @Override
        protected byte[] doInBackground(String... params) {
            String address = params[0];
            latLng = Utils.addressToLatLng(address);
            url = Utils.getStaticMapUrl(latLng, 17);
            return Utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {

            staticMapWeb.loadUrl(url);
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            staticMapImage.setImageBitmap(bm);

            LatLng storeAddress = new LatLng(latLng[0], latLng[1]);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeAddress, 20));
        }
    }
}
