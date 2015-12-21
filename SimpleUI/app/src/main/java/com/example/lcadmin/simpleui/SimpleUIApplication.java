package com.example.lcadmin.simpleui;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by lcadmin on 2015/12/20.
 */
public class SimpleUIApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}
