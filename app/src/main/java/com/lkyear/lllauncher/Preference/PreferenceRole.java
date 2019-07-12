package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.lkyear.lllauncher.Launcher.Launcher;
import com.lkyear.lllauncher.R;

/**
 * Created by lkyear on 17/5/27.
 */

public class PreferenceRole extends PreferenceBaseActivity {

    private Boolean needRestart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.launcher_preference_layout);
        setTitle("立绘配置");
        Intent intent = getIntent();
        needRestart = intent.getBooleanExtra("needRestart", false);
        getFragmentManager().beginTransaction().replace(R.id.launcher_pref_root_layout,new PreferenceActivity()).commit();
    }


    public static class PreferenceActivity extends PreferenceFragment {

        private CheckBoxPreference cbNotShowRole;
        private Preference pChangeRole, pAddRolePack, pDeleteRolePack;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.launcher_preference_role);
            cbNotShowRole = (CheckBoxPreference) findPreference("pref_not_show_role");
            pChangeRole = findPreference("pref_change_role");
            pAddRolePack = findPreference("pref_add_pack");
            pDeleteRolePack = findPreference("pref_delete_pack");
            if (cbNotShowRole.isChecked()) {
                pChangeRole.setEnabled(false);
                pAddRolePack.setEnabled(false);
                pDeleteRolePack.setEnabled(false);
            } else {
                pChangeRole.setEnabled(true);
                pAddRolePack.setEnabled(true);
                pDeleteRolePack.setEnabled(true);
            }
            cbNotShowRole.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (cbNotShowRole.isChecked()) {
                        pChangeRole.setEnabled(false);
                        pAddRolePack.setEnabled(false);
                        pDeleteRolePack.setEnabled(false);
                    } else {
                        pChangeRole.setEnabled(true);
                        pAddRolePack.setEnabled(true);
                        pDeleteRolePack.setEnabled(true);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (needRestart) {
                startActivity(new Intent(PreferenceRole.this, Launcher.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (needRestart) {
                startActivity(new Intent(PreferenceRole.this, Launcher.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            } else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
