package com.lkyear.lllauncher.Preference;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.Toast;

import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.Utils;
import com.nispok.snackbar.Snackbar;

public class PreferenceGeneralFragment extends PreferenceFragment {

    private EditTextPreference TargetText;

    public static final int TAG = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.launcher_preference_main);
        TargetText = (EditTextPreference) findPreference("pref_target_text");
        EditText targetEditText = TargetText.getEditText();
        targetEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
        findPreference("pref_hide_app_restore").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utils.preferenceRemove(getActivity(), "pref_hide_app");
                Snackbar.with(getActivity()).text("隐藏的应用已经显示").show(getActivity());
                return true;
            }
        });

        findPreference("pref_long_press_samsung_pay").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (Build.MANUFACTURER.equals("samsung")) {
                    String Message = "此功能为解决部分Samsung设备在开启主屏幕显示Samsung Pay时手势和导航栏冲突的问题。" +
                            "\n\n为更好展现效果，请按照下面顺序进行设置：" +
                            "\nSamsung Pay应用程序->设置->使用常用卡->关闭主屏幕开关。" +
                            "\n\n关闭在主屏幕显示Samsung Pay后，长按应用抽屉按钮即可打开Samsung Pay。";
                    new AlertDialog.Builder(getActivity()).setMessage(Message)
                            .setPositiveButton(android.R.string.ok, null).show();
                    return true;
                } else {
                    new AlertDialog.Builder(getActivity()).setMessage("此功能要求设备制造商为Samsung，您的设备无法开启此选项。")
                            .setPositiveButton(android.R.string.ok, null).show();
                    return false;
                }
            }
        });
    }



}
