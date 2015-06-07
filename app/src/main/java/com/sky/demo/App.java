package com.sky.demo;

import android.app.Application;
import android.content.Context;

/**
 * Created by sky on 15-5-30.
 */
public class App extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return sContext;
    }
}
