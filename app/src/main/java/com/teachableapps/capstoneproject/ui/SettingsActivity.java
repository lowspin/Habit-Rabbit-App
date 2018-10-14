package com.teachableapps.capstoneproject.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.teachableapps.capstoneproject.DefaultApplication;
import com.teachableapps.capstoneproject.R;

public class SettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Google Analytics
    Tracker mTracker;
    String screenName = "SettingsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Get Google Analytics Tracker instance.
        DefaultApplication application = (DefaultApplication) getApplication();
        mTracker = application.getDefaultTracker();


        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        // Send Analytics Tracking
        mTracker.setScreenName("Image~" + screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }
}
