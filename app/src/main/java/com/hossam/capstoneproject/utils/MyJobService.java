package com.hossam.capstoneproject.utils;

/**
 * Created by hossamonsy on 15/01/18.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.hossam.capstoneproject.R;

public class MyJobService extends JobService {
    private static final String TAG = MyJobService.class.getSimpleName();
    public static boolean previousFlag = false;

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.v(TAG, "MyJobService");
        Log.v("JobScheduler", "onStartJob");
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
                if (!previousFlag)
                    Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
            } else {
                connected = false;
                if (previousFlag)
                    Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();

            }
        }

        previousFlag = connected;


        return false; // Answers the question: "Is there still work going on?"
    }

    private void codeYouWantToRun() {

        try {

            Log.d(TAG, "completeJob: " + "jobStarted");
            //This task takes 2 seconds to complete.

            Thread.sleep(2000);

            Log.d(TAG, "completeJob: " + "jobFinished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.v("JobScheduler", "onStopJob");
        return false; // Answers the question: "Should this job be retried?"
    }


}
