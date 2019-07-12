package com.lkyear.lllauncher.Launcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.lkyear.lllauncher.Util.Utils;

public class LauncherCommonActivity extends Activity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.isMarshmallow()) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

}
