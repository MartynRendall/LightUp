package com.rendall.martyn.lightup.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.rendall.martyn.lightup.R;

/**
 * Created by Martyn on 08/05/2016.
 */
//public class SettingsFragment extends PreferenceFragmentCompat {
public class SettingsFragment extends PreferenceFragment {

//    @Override
//    public void onCreatePreferences(Bundle savedInstanceState, String s) {
//
//        addPreferencesFromResource(R.xml.pref_remote);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_remote);
    }
}
