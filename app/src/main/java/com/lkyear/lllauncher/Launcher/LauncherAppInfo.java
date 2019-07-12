package com.lkyear.lllauncher.Launcher;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.SuUtil;
import com.lkyear.lllauncher.Util.Utils;

/**
 * Created by lkyear on 17/5/25.
 */

public class LauncherAppInfo extends LauncherCommonActivity {

    private ImageView ivIcon;
    private TextView tvName, tvVersionNameTitle, tvPackageName, tvVersionCode,
                        tvTargetAPI, tvEmbedded, tvFirstInstall, tvLastUpdate, tvLocation, tvPermissions;
    private TextView btDetail, btUninstall, btForceClose;
    private String packageName, appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setElevation(0);
        setContentView(R.layout.launcher_app_info_layout);
        setTitle("应用程序信息");
        InitViews();
        InitButtons();
        InitInfo();
    }

    private void InitViews() {
        Intent intent = getIntent();
        packageName = intent.getStringExtra("PkgName");
        appName = intent.getStringExtra("AppName");
        btDetail = (TextView) findViewById(R.id.launcher_app_info_bt_detail);
        btUninstall = (TextView) findViewById(R.id.launcher_app_info_bt_uninstall);
        btForceClose = (TextView) findViewById(R.id.launcher_app_info_bt_force_close);
        ivIcon = (ImageView) findViewById(R.id.launcher_app_info_iv_icon);
        tvName = (TextView) findViewById(R.id.launcher_app_info_tv_name);
        tvVersionNameTitle = (TextView) findViewById(R.id.launcher_app_info_tv_version_name_title);
        tvPackageName = (TextView) findViewById(R.id.launcher_app_info_tv_package_name);
        tvPackageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, tvPackageName.getText().toString()));
                Toast.makeText(LauncherAppInfo.this, "已复制包名到剪切板", Toast.LENGTH_SHORT).show();
            }
        });
        tvVersionCode = (TextView) findViewById(R.id.launcher_app_info_tv_version_code);
        tvTargetAPI = (TextView) findViewById(R.id.launcher_app_info_tv_target_api);
        tvEmbedded = (TextView) findViewById(R.id.launcher_app_info_tv_embedded);
        tvFirstInstall = (TextView) findViewById(R.id.launcher_app_info_tv_first_install);
        tvLastUpdate = (TextView) findViewById(R.id.launcher_app_info_tv_last_update);
        tvLocation = (TextView) findViewById(R.id.launcher_app_info_tv_location);
        tvPermissions = (TextView) findViewById(R.id.launcher_app_info_tv_permission);
    }

    private void InitButtons() {
        btDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ShowDetial = new Intent();
                ShowDetial.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", packageName, null);
                ShowDetial.setData(uri);
                ShowDetial.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    ActivityOptions options = ActivityOptions.makeClipRevealAnimation(v, 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
                    startActivity(ShowDetial, options.toBundle());
                } else {
                    ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
                    startActivity(ShowDetial, options.toBundle());
                }
            }
        });
        btUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent unInstall = new Intent();
                unInstall.setAction(Intent.ACTION_DELETE);
                unInstall.setData(Uri.parse("package:" + packageName));
                unInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(unInstall);
            }
        });
        btForceClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(LauncherAppInfo.this);
                if (defaultSharedPreferences.getBoolean("pref_kill_by_root", false)) {
                    try {
                        SuUtil.kill(packageName);
                        Toast.makeText(LauncherAppInfo.this, "am force-stop " + packageName + "\n已执行", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(LauncherAppInfo.this, "am force-stop " + packageName + "\n执行失败，因为找不到输出流。", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    am.killBackgroundProcesses(packageName);
                    Toast.makeText(LauncherAppInfo.this, "已发送终止命令", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void InitInfo() {
        AppInfo appInfo = new AppInfo(LauncherAppInfo.this, packageName);
        ivIcon.setImageDrawable(appInfo.getIconDrawable());
        /*ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LauncherAppInfo.this).edit();
                editor.putString("pref_browser_app_package_name", packageName).commit();
                Toast.makeText(LauncherAppInfo.this, appName + " is the browser app!", Toast.LENGTH_SHORT).show();
            }
        });*/
        tvName.setText(appName);
        tvVersionNameTitle.setText("版本 " + appInfo.getVersionName());
        tvPackageName.setText(packageName);
        tvVersionCode.setText(appInfo.getVersionCode());
        tvTargetAPI.setText(appInfo.getTargetVersion());
        tvEmbedded.setText(appInfo.getIsSystemApp());
        if (appInfo.getIsSystemApp().equals("是")) {
            btUninstall.setVisibility(View.GONE);
        }
        tvFirstInstall.setText(appInfo.getFirstInstallTime());
        tvLastUpdate.setText(appInfo.getLastUpdateTime());
        tvLocation.setText(appInfo.getAppLocation());
        tvPermissions.setText(appInfo.getPermissions());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
