package com.lkyear.lllauncher.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by lkyear on 2017/7/17.
 */

public class Utils {

    public static final int PREF_DEFAULT_INT = 0;

    public static final String PREF_DEFAULT_STRING = "!-Err";

    /**
     * 判断是否为Android5.0+
     * @return 判断结果
     */
    public static Boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 判断是否为Android6.0+
     * @return 判断结果
     */
    public static Boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 获取整型偏好值
     * @param context 上下文
     * @param preferenceName 偏好名称
     * @return 偏好值
     */
    public static int preferenceGetInt(Context context, String preferenceName) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(preferenceName, 0);
    }

    /**
     * 获取字符串偏好值
     * @param context 上下文
     * @param preferenceName 偏好名称
     * @return 偏好值
     */
    public static String preferenceGetString(Context context, String preferenceName) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(preferenceName, "");
    }

    /**
     * 获取布尔型偏好值
     * @param context 上下文
     * @param preferenceName 偏好名称
     * @param defaultVal 初始值
     * @return 偏好值
     */
    public static Boolean preferenceGetBoolean(Context context, String preferenceName, Boolean defaultVal) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(preferenceName, defaultVal);
    }

    /**
     * 写入整型偏好值
     * @param context 上下文
     * @param preferenceName 片好名称
     * @param value 偏好值
     */
    public static void preferencePutData(Context context, String preferenceName, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(preferenceName, value).commit();
    }

    /**
     * 写入字符串偏好值
     * @param context 上下文
     * @param preferenceName 片好名称
     * @param value 偏好值
     */
    public static void preferencePutData(Context context, String preferenceName, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(preferenceName, value).commit();
    }

    /**
     * 写入布尔型偏好值
     * @param context 上下文
     * @param preferenceName 片好名称
     * @param value 偏好值
     */
    public static void preferencePutData(Context context, String preferenceName, Boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(preferenceName, value).commit();
    }

    /**
     * 清除所有SharedPreference数据
     * @param context 上下文
     */
    public static void preferenceClear(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.clear().commit();
    }

    /**
     * 根据Key清除指定的SharedPreference数据
     * @param context 上下文
     * @param key 偏好项
     */
    public static void preferenceRemove(Context context, String key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.remove(key).commit();
    }

    /**
     * 返回版本号
     * @param context 上下文
     * @return 版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            return PREF_DEFAULT_INT;
        }
    }

    /**
     * 返回版本名称
     * @param context 上下文
     * @return 版本号
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            return PREF_DEFAULT_STRING;
        }
    }

    /**
     * 返回一个包是否存在于手机里
     * @param context 上下文
     * @param packageName 包名
     * @return 是否存在
     */
    public static Boolean isPackageInstalled(Context context, String packageName) {
        ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }



}
