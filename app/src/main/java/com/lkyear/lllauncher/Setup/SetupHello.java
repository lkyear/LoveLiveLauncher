package com.lkyear.lllauncher.Setup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lkyear.lllauncher.R;

/**
 * Created by lkyear on 2017/7/17.
 */

public class SetupHello extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_hello);
    }

    public void goClick(View view) {
        startActivity(new Intent(SetupHello.this, SetupEula.class));
        finish();
    }
}
