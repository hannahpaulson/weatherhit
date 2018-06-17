package com.hp.weatherhit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

//        Context context = getApplicationContext();
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(get);
//        settings.registerOnSharedPreferenceChangeListener(this);
    }
}
