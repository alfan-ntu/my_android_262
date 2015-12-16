package com.example.lcadmin.simpleui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Created by lcadmin on 2015/12/16.
 * added a new activity class "DrinkMenuActivity"
 */
public class DrinkMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
    applies layout resource activity_drink_menu.xml
 */
        setContentView(R.layout.activity_drink_menu);

    }

    public void add(View view){
        Button button = (Button) view;
        int number = Integer.parseInt(button.getText().toString());
        number++;
        button.setText(String.valueOf(number));
    }
}
