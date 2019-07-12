package com.lkyear.lllauncher.Setup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.lkyear.lllauncher.Launcher.Launcher;
import com.lkyear.lllauncher.Launcher.Level;
import com.lkyear.lllauncher.Launcher.SupportPay;
import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.Utils;

/**
 * Created by lkyear on 2017/7/17.
 */

public class SetupEnd extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_end);
        Utils.preferencePutData(SetupEnd.this, "pref_installed_version_code", Utils.getVersionCode(SetupEnd.this));
    }

    public void endClick(View view) {
        Level userLevel = new Level(SetupEnd.this);
        userLevel.setAchievement(Level.ACHIEVEMENT_BEGINNING, true);
        startActivity(new Intent(SetupEnd.this, Launcher.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
