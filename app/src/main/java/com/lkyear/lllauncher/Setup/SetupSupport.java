package com.lkyear.lllauncher.Setup;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;

import com.lkyear.lllauncher.Launcher.LauncherWebView;
import com.lkyear.lllauncher.Launcher.SupportPay;
import com.lkyear.lllauncher.R;

/**
 * Created by lkyear on 2017/7/17.
 */

public class SetupSupport extends Activity {

    public static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_support);
    }

    public void nextClickSupport(View view) {
        startActivity(new Intent(SetupSupport.this, SetupEnd.class));
    }

    public void supportWay(View view) {
        startActivity(new Intent(SetupSupport.this, SupportPay.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    new AlertDialog.Builder(SetupSupport.this).setMessage("请允许存储权限，否则无法使用桌面的全部功能。")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent ShowDetial = new Intent();
                                    ShowDetial.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", "com.lkyear.lllauncher", null);
                                    ShowDetial.setData(uri);
                                    startActivity(ShowDetial);
                                }
                            }).show();
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
