package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.lkyear.lllauncher.Launcher.Level;
import com.lkyear.lllauncher.R;

/**
 * Created by lkyear on 2017/8/7.
 */

public class PreferenceLevelUnlock extends PreferenceBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.launcher_preference_layout);
        setTitle("高级功能");
        getFragmentManager().beginTransaction().replace(R.id.launcher_pref_root_layout,new PreferenceActivity()).commit();
    }

    public static class PreferenceActivity extends PreferenceFragment {

        private Level userLevel;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.launcher_preference_level);
            userLevel = new Level(getActivity());
            PreferenceCategory preferenceCategoryTwo = (PreferenceCategory) findPreference("unlock_menu_two");
            if (userLevel.LEVEL_USER_VALUE >= 600) {
                preferenceCategoryTwo.setEnabled(true);
            } else {
                preferenceCategoryTwo.setEnabled(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
