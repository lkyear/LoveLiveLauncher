package com.lkyear.lllauncher.Setup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.lkyear.lllauncher.Launcher.LauncherWebView;
import com.lkyear.lllauncher.R;

/**
 * Created by lkyear on 2017/7/17.
 */

public class SetupPack extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_pack);
    }

    public void nextClickPack(View view) {
        startActivity(new Intent(SetupPack.this, SetupSupport.class));
        finish();
    }

    public void packDetail(View view) {
        startActivity(new Intent(SetupPack.this, LauncherWebView.class).putExtra("open_html_type", LauncherWebView.HTML_PACK));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
