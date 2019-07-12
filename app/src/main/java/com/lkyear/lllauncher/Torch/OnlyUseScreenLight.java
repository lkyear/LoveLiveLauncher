package com.lkyear.lllauncher.Torch;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;

import com.lkyear.lllauncher.R;

/**
 * Created by lkyear on 16-2-21.
 */
public class OnlyUseScreenLight extends Activity {
    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_torch_empty);
        powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);
        setBrightness(255);
    }

    private void setBrightness(int brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        getWindow().setAttributes(lp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

}
