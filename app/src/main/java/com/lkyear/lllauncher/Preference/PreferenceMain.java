package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.BottomNavigationView;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lkyear.lllauncher.Launcher.Launcher;
import com.lkyear.lllauncher.Launcher.Level;
import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.Utils;
import com.nispok.snackbar.Snackbar;

/**
 * Created by lkyear on 17/5/23.
 */

public class PreferenceMain extends PreferenceBaseActivity {

    private PreferenceGeneralFragment preferenceMain;
    private PreferenceFunctionsFragment preferenceFunctions;
    private PreferenceAboutFragment preferenceAbout;

    private int currentFragment = 1;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_preference_main);
        setTitle("桌面设置");
        //getFragmentManager().beginTransaction().replace(R.id.launcher_pref_root_layout,new PreferenceActivity()).commit();
        manager = getFragmentManager();
        transaction = manager.beginTransaction();
        preferenceMain = new PreferenceGeneralFragment();
        preferenceFunctions = new PreferenceFunctionsFragment();
        preferenceAbout = new PreferenceAboutFragment();
        transaction.add(R.id.frame, preferenceMain);
        transaction.commit();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            transaction = manager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.launcher_pref_menu_general:
                    if (currentFragment != 1) {
                        transaction.remove((Fragment) getCurrentFragment());
                        transaction.add(R.id.frame, preferenceMain).show(preferenceMain).commit();
                        currentFragment = 1;
                        return true;
                    }
                    break;
                case R.id.launcher_pref_menu_function:
                    if (currentFragment != 2) {
                        transaction.remove((Fragment) getCurrentFragment());
                        transaction.add(R.id.frame, preferenceFunctions).show(preferenceFunctions).commit();
                        currentFragment = 2;
                        return true;
                    }
                    break;
                case R.id.launcher_pref_menu_about:
                    if (currentFragment != 3) {
                        transaction.remove((Fragment) getCurrentFragment());
                        transaction.add(R.id.frame, preferenceAbout).show(preferenceAbout).commit();
                        currentFragment = 3;
                        return true;
                    }
                    break;
            }
            return false;
        }
    };

    private Object getCurrentFragment() {
        switch (currentFragment) {
            case 1:
                return preferenceMain;
            case 2:
                return preferenceFunctions;
            case 3:
                return preferenceAbout;
            default:
                return preferenceMain;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(PreferenceMain.this, Launcher.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(PreferenceMain.this, Launcher.class));
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
