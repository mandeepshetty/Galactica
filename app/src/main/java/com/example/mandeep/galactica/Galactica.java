package com.example.mandeep.galactica;

import android.app.Application;

import timber.log.Timber;


public class Galactica extends Application {

    @Override public void onCreate() {        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
