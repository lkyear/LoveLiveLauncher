package com.lkyear.lllauncher.Setup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.lkyear.lllauncher.Launcher.LauncherWebView;
import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.Utils;

/**
 * Created by lkyear on 2017/7/17.
 */

public class SetupEula extends Activity {

    private CheckBox cbAgreeEula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_eula);
        cbAgreeEula = (CheckBox) findViewById(R.id.launcher_setup_agree_eula);
    }

    public void nextClickEula(View view) {
        if (cbAgreeEula.isChecked()) {
            SharedPreferences preference = getSharedPreferences("settings", MODE_PRIVATE);
            int verCode = Utils.preferenceGetInt(SetupEula.this, "pref_installed_version_code");
            if (preference.getInt("VersionInfo", 0) > 200 && preference.getInt("VersionInfo", 0) <= 308) {
                startActivity(new Intent(SetupEula.this, SetupClear.class));
            } else if (verCode == 0){
                startActivity(new Intent(SetupEula.this, SetupUser.class));
            } else {
                startActivity(new Intent(SetupEula.this, SetupSupport.class));
            }
            finish();
        } else {
            Toast.makeText(SetupEula.this, "必须同意EULA后才可以继续", Toast.LENGTH_SHORT).show();
        }
    }

    public void readEULA(View view) {
        startActivity(new Intent(SetupEula.this, LauncherWebView.class).putExtra("open_html_type", LauncherWebView.HTML_LEGAL));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
