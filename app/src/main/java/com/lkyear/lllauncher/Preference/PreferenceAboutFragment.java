package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.lkyear.lllauncher.Launcher.LauncherWebView;
import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.Utils;

/**
 * Created by lkyear on 2017/7/13.
 */

public class PreferenceAboutFragment extends PreferenceFragment {

    public static final int TAG = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.launcher_preference_about);
        findPreference("pref_about_thanks").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity().getApplicationContext(), LauncherWebView.class).putExtra("open_html_type", LauncherWebView.HTML_THANKS));
                return false;
            }
        });
        findPreference("pref_about_group").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                joinQQGroup("REPLACE_BY_YOUR_CODE");
                return false;
            }
        });
    }

    /**
     * 来自腾讯官方的加群方法
     * @param key 群key
     */
    public void joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            Toast.makeText(getActivity().getApplicationContext(), "正在调起QQ客户端...", Toast.LENGTH_LONG).show();
            getActivity().startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "未找到QQ客户端", Toast.LENGTH_LONG).show();

        }
    }

}
