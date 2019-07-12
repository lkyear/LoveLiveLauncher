package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lkyear.lllauncher.Launcher.LauncherWebView;
import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.View.ShortcutView;

import java.util.ArrayList;
import java.util.List;

/**
 * 主屏幕快捷方式配置页面
 * @author lkyear
 *
 * 配置标识：
 * SharedPreference - pref_home_shortcut_first/second/third/fourth - Integer
 *
 * SharedPreference values
 * 0: 百度贴吧 - com.baidu.tieba
 * 1: 哔哩哔哩 - tv.danmaku.bili
 * 2: 冰箱 - com.catchingnow.icebox
 * 3: 酷市场 - com.coolapk.market
 * 4: 浏览器 - Android System
 * 5: 淘宝 - com.taobao.taobao
 * 6: 腾讯QQ - com.tencent.mobileqq
 * 7: TIM - com.tencent.tim
 * 8: 腾讯QQ轻聊版 - com.tencent.qqlite
 * 9: 网易云音乐 - com.netease.cloudmusic
 * 10: 微信 - com.tencent.mm
 * 11: 支付宝 - com.eg.android.AlipayGphone
 * 12: IT之家 - com.ruanmei.ithome
 *
 */

public class PreferenceShortcut extends PreferenceBaseActivity {

    private ShortcutView scvShortcutFirst, scvShortcutSecond, scvShortcutThird, scvShortcutForth;
    private TextView tvTargetText, tvPopboxText, tvHelp, tvRestore;

    private SharedPreferences defaultPreference;
    private SharedPreferences.Editor defaultPreferenceEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_preference_shortcut_layout);
        setTitle("快捷按钮配置");
        defaultPreference = PreferenceManager.getDefaultSharedPreferences(PreferenceShortcut.this);
        defaultPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(PreferenceShortcut.this).edit();
        Init();
    }

    private void Init() {
        scvShortcutFirst = (ShortcutView) findViewById(R.id.launcher_home_pages_iv_fist);
        scvShortcutSecond = (ShortcutView) findViewById(R.id.launcher_home_pages_iv_second);
        scvShortcutThird = (ShortcutView) findViewById(R.id.launcher_home_pages_iv_third);
        scvShortcutForth = (ShortcutView) findViewById(R.id.launcher_home_pages_iv_fourth);
        scvShortcutFirst.setOnClickListener(new ShortcutOnClickListener());
        scvShortcutSecond.setOnClickListener(new ShortcutOnClickListener());
        scvShortcutThird.setOnClickListener(new ShortcutOnClickListener());
        scvShortcutForth.setOnClickListener(new ShortcutOnClickListener());
        tvTargetText = (TextView) findViewById(R.id.launcher_home_pages_tv_target);
        tvPopboxText = (TextView) findViewById(R.id.launcher_home_pages_tv_popbox);
        tvHelp = (TextView) findViewById(R.id.launcher_pref_shortcut_help);
        tvRestore = (TextView) findViewById(R.id.launcher_pref_shortcut_restore);
        tvHelp.setOnClickListener(new ShortcutOnClickListener());
        tvRestore.setOnClickListener(new ShortcutOnClickListener());
        InitText();
    }

    private void InitText() {
        tvTargetText.setText("配置主屏幕四个快捷方式的功能，如需更改请点击任意一个按钮。");
        InitImage();
    }

    private void InitImage() {
        setShortcutApp(defaultPreference.getInt("pref_home_shortcut_first", 6), scvShortcutFirst);
        setShortcutApp(defaultPreference.getInt("pref_home_shortcut_second", 1), scvShortcutSecond);
        setShortcutApp(defaultPreference.getInt("pref_home_shortcut_third", 4), scvShortcutThird);
        setShortcutApp(defaultPreference.getInt("pref_home_shortcut_fourth", 10), scvShortcutForth);
    }

    private class ShortcutOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.launcher_home_pages_iv_fist:
                    getShorcutApp("pref_home_shortcut_first", scvShortcutFirst);
                    break;
                case R.id.launcher_home_pages_iv_second:
                    getShorcutApp("pref_home_shortcut_second", scvShortcutSecond);
                    break;
                case R.id.launcher_home_pages_iv_third:
                    getShorcutApp("pref_home_shortcut_third", scvShortcutThird);
                    break;
                case R.id.launcher_home_pages_iv_fourth:
                    getShorcutApp("pref_home_shortcut_fourth", scvShortcutForth);
                    break;
                case R.id.launcher_pref_shortcut_help:
                    startActivity(new Intent(PreferenceShortcut.this, LauncherWebView.class).putExtra("open_html_type", LauncherWebView.HTML_SHORTCUT));
                    break;
                case R.id.launcher_pref_shortcut_restore:
                    setShortcutApp(6, "pref_home_shortcut_first", scvShortcutFirst);
                    setShortcutApp(1, "pref_home_shortcut_second", scvShortcutSecond);
                    setShortcutApp(4, "pref_home_shortcut_third", scvShortcutThird);
                    setShortcutApp(10, "pref_home_shortcut_fourth", scvShortcutForth);
                    break;
            }
        }
    }

    private void getShorcutApp(final String preferenceName, final ImageView imageView) {
        List<String> appArrayList = new ArrayList<>();
        appArrayList.add("浏览器");
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> packs = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo ri : packs) {
            String packageName = ri.activityInfo.packageName;
            switch (packageName) {
                case "com.baidu.tieba":
                    appArrayList.add("百度贴吧");
                    break;
                case "tv.danmaku.bili":
                    appArrayList.add("哔哩哔哩");
                    break;
                case "com.catchingnow.icebox":
                    appArrayList.add("冰箱");
                    break;
                case "com.coolapk.market":
                    appArrayList.add("酷市场");
                    break;
                case "com.taobao.taobao":
                    appArrayList.add("淘宝");
                    break;
                case "com.tencent.mobileqq":
                    appArrayList.add("腾讯QQ");
                    break;
                case "com.tencent.qqlite":
                    appArrayList.add("腾讯QQ轻聊版");
                    break;
                case "com.tencent.tim":
                    appArrayList.add("TIM");
                    break;
                case "com.netease.cloudmusic":
                    appArrayList.add("网易云音乐");
                    break;
                case "com.tencent.mm":
                    appArrayList.add("微信");
                    break;
                case "com.eg.android.AlipayGphone":
                    appArrayList.add("支付宝");
                    break;
                case "com.ruanmei.ithome":
                    appArrayList.add("IT之家");
                    break;
            }
        }
        final String[] arrayApps = new String[appArrayList.size()];
        for (int i = 0; i < appArrayList.size(); i++) {
            arrayApps[i] = appArrayList.get(i);
        }
        new AlertDialog.Builder(PreferenceShortcut.this).setItems(arrayApps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int appTuneCode = 0;
                switch (arrayApps[i]) {
                    case "百度贴吧":
                        appTuneCode = 0;
                        break;
                    case "哔哩哔哩":
                        appTuneCode = 1;
                        break;
                    case "冰箱":
                        appTuneCode = 2;
                        break;
                    case "酷市场":
                        appTuneCode = 3;
                        break;
                    case "浏览器":
                        appTuneCode = 4;
                        break;
                    case "淘宝":
                        appTuneCode = 5;
                        break;
                    case "腾讯QQ":
                        appTuneCode = 6;
                        break;
                    case "TIM":
                        appTuneCode = 7;
                        break;
                    case "腾讯QQ轻聊版":
                        appTuneCode = 8;
                        break;
                    case "网易云音乐":
                        appTuneCode = 9;
                        break;
                    case "微信":
                        appTuneCode = 10;
                        break;
                    case "支付宝":
                        appTuneCode = 11;
                        break;
                    case "IT之家":
                        appTuneCode = 12;
                        break;
                }
                setShortcutApp(appTuneCode, preferenceName, imageView);
            }
        }).show();
    }

    private void setShortcutApp(int appCode, String preferenceName, ImageView imageView) {
        defaultPreferenceEditor.putInt(preferenceName, appCode).commit();
        switch (appCode) {
            case 0:
                imageView.setImageResource(R.drawable.res_home_tieba);
                break;
            case 1:
                imageView.setImageResource(R.drawable.res_home_bilibili);
                break;
            case 2:
                imageView.setImageResource(R.drawable.res_home_icebox);
                break;
            case 3:
                imageView.setImageResource(R.drawable.res_home_coolapk);
                break;
            case 4:
                imageView.setImageResource(R.drawable.res_home_browser);
                break;
            case 5:
                imageView.setImageResource(R.drawable.res_home_taobao);
                break;
            case 6:
                imageView.setImageResource(R.drawable.res_home_qq);
                break;
            case 7:
                imageView.setImageResource(R.drawable.res_home_qq);
                break;
            case 8:
                imageView.setImageResource(R.drawable.res_home_qq);
                break;
            case 9:
                imageView.setImageResource(R.drawable.res_home_netmusic);
                break;
            case 10:
                imageView.setImageResource(R.drawable.res_home_wechat);
                break;
            case 11:
                imageView.setImageResource(R.drawable.res_home_alipay);
                break;
            case 12:
                imageView.setImageResource(R.drawable.res_home_ithome);
                break;
        }
    }

    private void setShortcutApp(int appCode, ImageView imageView) {
        switch (appCode) {
            case 0:
                imageView.setImageResource(R.drawable.res_home_tieba);
                break;
            case 1:
                imageView.setImageResource(R.drawable.res_home_bilibili);
                break;
            case 2:
                imageView.setImageResource(R.drawable.res_home_icebox);
                break;
            case 3:
                imageView.setImageResource(R.drawable.res_home_coolapk);
                break;
            case 4:
                imageView.setImageResource(R.drawable.res_home_browser);
                break;
            case 5:
                imageView.setImageResource(R.drawable.res_home_taobao);
                break;
            case 6:
                imageView.setImageResource(R.drawable.res_home_qq);
                break;
            case 7:
                imageView.setImageResource(R.drawable.res_home_qq);
                break;
            case 8:
                imageView.setImageResource(R.drawable.res_home_qq);
                break;
            case 9:
                imageView.setImageResource(R.drawable.res_home_netmusic);
                break;
            case 10:
                imageView.setImageResource(R.drawable.res_home_wechat);
                break;
            case 11:
                imageView.setImageResource(R.drawable.res_home_alipay);
                break;
            case 12:
                imageView.setImageResource(R.drawable.res_home_ithome);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
