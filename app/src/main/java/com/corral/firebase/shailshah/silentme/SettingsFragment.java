package com.corral.firebase.shailshah.silentme;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;


/**
 * Created by shailshah on 10/27/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        // Add visualizer preferences, defined in the XML file in res->xml->pref_visualizer
        addPreferencesFromResource(R.xml.silent_pref);
    }

}