package com.mymusic.orvai.travel_with.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;


public class MyFirebaseInstanceIDService extends JobService {
    public MyFirebaseInstanceIDService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("GB", "Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}
