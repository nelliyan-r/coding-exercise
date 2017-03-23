package com.gm.androidcodingexercise;

import android.app.Application;

import com.gm.androidcodingexercise.util.DiskLruMediaCache;

/**
 * AndroidCodingExercise
 */
public class App extends Application {
    private static final String TAG = Constants.BASE_TAG + App.class.getSimpleName();

    public static DiskLruMediaCache mediaCache;

    @Override public void onCreate() {
        super.onCreate();
        mediaCache = new DiskLruMediaCache(getApplicationContext());
    }

    @Override public void onTerminate() {
        mediaCache = null;
        super.onTerminate();
    }
}
