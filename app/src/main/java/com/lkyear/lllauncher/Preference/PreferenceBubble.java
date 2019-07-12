package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;

import com.lkyear.lllauncher.Launcher.Launcher;
import com.lkyear.lllauncher.Launcher.Level;
import com.lkyear.lllauncher.R;

/**
 * Created by lkyear on 2017/8/25.
 */

public class PreferenceBubble extends PreferenceBaseActivity {

    private Boolean requireRes = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.launcher_preference_layout);
        setTitle("气泡配置");
        requireRes = getIntent().getBooleanExtra("requireRestart", false);
        getFragmentManager().beginTransaction().replace(R.id.launcher_pref_root_layout,new PreferenceActivity()).commit();
    }

    public static class PreferenceActivity extends PreferenceFragment {

        private CheckBoxPreference showWelcomeMessage, showPopbox, longPressModify, dynamicText,
                                    dynamicByOrder;
        private EditTextPreference PopboxText;
        private PreferenceCategory prefAdv;
        private Preference dynamicTextSet;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.launcher_preference_bubble);
            showWelcomeMessage = (CheckBoxPreference) findPreference("pref_show_welcome_message");
            longPressModify = (CheckBoxPreference) findPreference("pref_popup_box_long_press_modify");
            PopboxText = (EditTextPreference) findPreference("pref_popbox_text");
            showPopbox = (CheckBoxPreference) findPreference("pref_popup_box_show_visibility");
            prefAdv = (PreferenceCategory) findPreference("pref_bubble_adv_set");
            dynamicText = (CheckBoxPreference) findPreference("pref_popup_box_show_dynamic_text");
            dynamicByOrder = (CheckBoxPreference) findPreference("pref_popup_box_dynamic_text_by_order");
            dynamicTextSet = findPreference("pref_popup_box_dynamic_text");

            showWelcomeMessage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (showWelcomeMessage.isChecked()) {
                        new Level(getActivity().getApplicationContext()).setAchievement(Level.ACHIEVEMENT_WELCOME_BACK, true);
                    }
                    return false;
                }
            });

            if (showPopbox.isChecked()) {
                PopboxText.setEnabled(false);
                showWelcomeMessage.setEnabled(false);
                prefAdv.setEnabled(false);
                longPressModify.setEnabled(false);
                dynamicText.setEnabled(false);
                dynamicByOrder.setEnabled(false);
                dynamicTextSet.setEnabled(false);
            } else {
                PopboxText.setEnabled(true);
                showWelcomeMessage.setEnabled(true);
                prefAdv.setEnabled(true);
                longPressModify.setEnabled(true);
                dynamicText.setEnabled(true);
                if (dynamicText.isChecked()) {
                    showPopbox.setEnabled(false);
                    showWelcomeMessage.setEnabled(false);
                    PopboxText.setEnabled(false);
                    dynamicByOrder.setEnabled(true);
                    dynamicTextSet.setEnabled(true);
                } else {
                    showPopbox.setEnabled(true);
                    showWelcomeMessage.setEnabled(true);
                    PopboxText.setEnabled(true);
                    dynamicByOrder.setEnabled(false);
                    dynamicTextSet.setEnabled(false);
                }
            }

            showPopbox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (showPopbox.isChecked()) {
                        PopboxText.setEnabled(false);
                        showWelcomeMessage.setEnabled(false);
                        prefAdv.setEnabled(false);
                        longPressModify.setEnabled(false);
                        dynamicText.setEnabled(false);
                        dynamicByOrder.setEnabled(false);
                        dynamicTextSet.setEnabled(false);
                    } else {
                        PopboxText.setEnabled(true);
                        showWelcomeMessage.setEnabled(true);
                        prefAdv.setEnabled(true);
                        longPressModify.setEnabled(true);
                        dynamicText.setEnabled(true);
                        if (dynamicText.isChecked()) {
                            showPopbox.setEnabled(false);
                            showWelcomeMessage.setEnabled(false);
                            PopboxText.setEnabled(false);
                            dynamicByOrder.setEnabled(true);
                            dynamicTextSet.setEnabled(true);
                        } else {
                            showPopbox.setEnabled(true);
                            showWelcomeMessage.setEnabled(true);
                            PopboxText.setEnabled(true);
                            dynamicByOrder.setEnabled(false);
                            dynamicTextSet.setEnabled(false);
                        }
                    }
                    return false;
                }
            });
            dynamicText.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (dynamicText.isChecked()) {
                        showPopbox.setEnabled(false);
                        showWelcomeMessage.setEnabled(false);
                        PopboxText.setEnabled(false);
                        dynamicTextSet.setEnabled(true);
                        dynamicByOrder.setEnabled(true);
                    } else {
                        showPopbox.setEnabled(true);
                        showWelcomeMessage.setEnabled(true);
                        PopboxText.setEnabled(true);
                        dynamicTextSet.setEnabled(false);
                        dynamicByOrder.setEnabled(false);
                    }
                    return false;
                }
            });

            EditText popboxEditText = PopboxText.getEditText();
            popboxEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (requireRes) {
                startActivity(new Intent(PreferenceBubble.this, Launcher.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (requireRes) {
                startActivity(new Intent(PreferenceBubble.this, Launcher.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
