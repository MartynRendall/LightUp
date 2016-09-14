package com.rendall.martyn.lightup;

import android.preference.PreferenceManager;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Martyn on 06/04/2016.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);

        setupDefaultPreferences();
    }

    private void setupDefaultPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_remote, false);
    }

}
