package com.lkyear.lllauncher.Preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.lkyear.lllauncher.R;

public class PreferenceFunctionsFragment extends PreferenceFragment {

    public static final int TAG = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.launcher_preference_functions);
    }


}
