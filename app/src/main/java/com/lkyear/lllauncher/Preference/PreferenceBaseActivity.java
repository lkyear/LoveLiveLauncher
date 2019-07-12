package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lkyear.lllauncher.Util.Utils;

public class PreferenceBaseActivity extends Activity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.isMarshmallow()) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
