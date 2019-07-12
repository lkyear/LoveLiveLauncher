package com.lkyear.lllauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.lkyear.lllauncher.Launcher.Launcher;
import com.lkyear.lllauncher.Setup.SetupEula;
import com.lkyear.lllauncher.Setup.SetupHello;
import com.lkyear.lllauncher.Util.Utils;

/**
 * 此类已废弃
 */

@Deprecated
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.preferenceGetInt(SplashActivity.this, "pref_installed_version_code") == Utils.getVersionCode(SplashActivity.this)) {
            startActivity(new Intent(SplashActivity.this, Launcher.class));
        } else if (Utils.preferenceGetInt(SplashActivity.this, "pref_installed_version_code") > Utils.getVersionCode(SplashActivity.this)) {
            Toast.makeText(SplashActivity.this, "降级可能会导致错误。", Toast.LENGTH_LONG).show();
            Utils.preferencePutData(SplashActivity.this, "pref_installed_version_code", 1);
            startActivity(new Intent(SplashActivity.this, SetupHello.class));
        } else {
            startActivity(new Intent(SplashActivity.this, SetupHello.class));
        }
        finish();
    }
}
