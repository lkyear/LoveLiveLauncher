package com.lkyear.lllauncher.Setup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.Utils;

/**
 * Created by lkyear on 2017/7/17.
 */

public class SetupClear extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_clear);
    }

    public void nextClickClear(View view) {
        Utils.preferenceClear(SetupClear.this);
        startActivity(new Intent(SetupClear.this, SetupVideo.class));
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
