package com.lkyear.lllauncher.Launcher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.lkyear.lllauncher.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lkyear on 17/5/25.
 */

public class AppInfo {

    private PackageManager packageManager;
    private String packageName;
    private Context context;

    public AppInfo(Context ctx, String pkgName) {
        context = ctx;
        packageManager = context.getPackageManager();
        packageName = pkgName;
    }

    public Drawable getIconDrawable() {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return applicationInfo.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException NNFE) {
            return context.getDrawable(R.drawable.res_about_icon);
        }
    }

    public String getVersionName() {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException NNFE) {
            return "无法获取";
        }
    }

    public String getVersionCode() {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException NNFE) {
            return 0 + "";
        }
    }

    public String getTargetVersion() {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return "API " + applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException NNFE) {
            return "无法获取";
        }
    }

    public String getIsSystemApp() {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            if ((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM) <= 0) {
                return "否";
            } else {
                return "是";
            }
        } catch (PackageManager.NameNotFoundException NNFE) {
            return "无法获取";
        }
    }

    public String getFirstInstallTime() {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            long installTime = packageInfo.firstInstallTime;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(installTime);
            return simpleDateFormat.format(date);
        } catch (PackageManager.NameNotFoundException NNFE) {
            return "无法获取";
        }
    }

    public String getLastUpdateTime() {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            long installTime = packageInfo.lastUpdateTime;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(installTime);
            return simpleDateFormat.format(date);
        } catch (PackageManager.NameNotFoundException NNFE) {
            return "无法获取";
        }
    }

    public String getAppLocation() {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.applicationInfo.publicSourceDir.toString();
        } catch (PackageManager.NameNotFoundException NNFE) {
            return "无法获取";
        }
    }

    public String getPermissions() {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < packageInfo.requestedPermissions.length; i++) {
                stringBuilder.append(packageInfo.requestedPermissions[i].toString() + "\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            return "无";
        }
    }

}
