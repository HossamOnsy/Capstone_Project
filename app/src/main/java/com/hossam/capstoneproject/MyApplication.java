package com.hossam.capstoneproject;

import android.app.Application;

import com.hossam.capstoneproject.utils.ConnectivityReceiver;

/**
 * Created by hossamonsy on 10/12/17.
 */

public class MyApplication extends Application {

    private static MyApplication mInstance;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}