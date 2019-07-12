package com.lkyear.lllauncher.Launcher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.lkyear.lllauncher.Preference.BubbleText;
import com.lkyear.lllauncher.Preference.BubbleTextDataBaseHelper;
import com.lkyear.lllauncher.Preference.PreferenceBubble;
import com.lkyear.lllauncher.Preference.Role;
import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Setup.SetupHello;
import com.lkyear.lllauncher.Util.MessageBox;
import com.lkyear.lllauncher.Util.SoundKit;
import com.lkyear.lllauncher.Util.Utils;
import com.lkyear.lllauncher.View.IndexBar;
import com.lkyear.lllauncher.View.ShortcutView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by lkyear on 17/5/12.
 */

public class Launcher extends Activity {

    public static Launcher LAUNCHER_HOME;

    private ViewPager launcherViewPager;
    private View launcherHomeView, launcherAppsView, launcherShelvesView;
    private LinearLayout shelvesLinearLayout;
    private List<Shelves> shelvesList;
    private RelativeLayout launcherRootLayout, launcherWorkspaceLayout;
    private LinearLayout launcherDockRootLayout;
    private List<View> launcherViewList;

    private IntentFilter intentFilterBattery, intentFilterNetwork, intentFilterProfile, intentFilterHeadset,
                         intentFilterDate, intentFilterPackage, intentFilterHome;
    private LauncherReceiverBattery launcherReceiverBattery;
    private LauncherReceiverNetworkChanged launcherReceiverNetworkChanged;
    private LauncherReceiverRingModeChanged launcherReceiverProfileChanged;
    private LauncherReceiverHeadsetPlug launcherReceiverHeadsetPlug;
    private LauncherReceiverDateChanged launcherReceiverDateChanged;
    private LauncherReceiverPackageChanged launcherReceiverPackageChanged;
    private LauncherHomeButtonPress launcherHomeButtonPress;

    private List<AppData> appDataList;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private AppViewAdapter appViewAdapter;
    private LauncherApplicationViewTask launcherApplicationViewTask;
    private PackageManager pm;

    private boolean appUninstMode = false, appInfoMode = false;

    private RecyclerView rvApplication;
    private TextView ibTextView, tvStateDate, appLayoutMenuTitle;
    private IndexBar ibIndexBar;
    private ProgressBar pbProcessing;
    private ShortcutView scvDockDial, scvDockMsg, scvDockDrawer, scvDockPref, scvDockCam;

    private ShortcutView scvUserInfo, scvShortcutFirst, scvShortcutSecond, scvShortcutThird, scvShortcutForth;
    private TextView tvTargetText, tvPopboxText, appLayoutMenu, appLayoutBack;
    private RelativeLayout rlPopboxLayout;
    private ImageView ivRole;

    private SharedPreferences defaultSharedPreference;
    private SharedPreferences.Editor defaultSharedPreferenceEditor;
    private Level userLevel;

    private List<BubbleText> bubbleTexts = new ArrayList<>();
    private ArrayList<String> bubbleTextListArray = new ArrayList<String>();
    private int bubbleShowNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_layout);
        LAUNCHER_HOME = this;
        defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(Launcher.this);
        defaultSharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(Launcher.this).edit();
        if (!defaultSharedPreference.getBoolean("pref_show_notification_bar", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        userLevel = new Level(Launcher.this);
        Init();
    }

    /**
     * 初始化方法
     */
    private void Init() {
        splash();
        InitBasicControl();
        InitHomeIconClick();
        InitViewPager();
        setFistUseCase();
        InitReceiver();
        InitApplicationPage();
    }

    /**
     * 应用
     */
    private void splash() {
        if (Utils.preferenceGetInt(Launcher.this, "pref_installed_version_code") == Utils.getVersionCode(Launcher.this)) {
            return;
        } else if (Utils.preferenceGetInt(Launcher.this, "pref_installed_version_code") > Utils.getVersionCode(Launcher.this)) {
            Toast.makeText(Launcher.this, "降级可能会导致错误。", Toast.LENGTH_LONG).show();
            Utils.preferencePutData(Launcher.this, "pref_installed_version_code", 1);
            startActivity(new Intent(Launcher.this, SetupHello.class));
        } else {
            startActivity(new Intent(Launcher.this, SetupHello.class));
        }
        finish();
        this.onDestroy();
    }

    /**
     * 初始化基本控制
     */
    private void InitBasicControl() {
        launcherRootLayout = (RelativeLayout) findViewById(R.id.launcher_home_root_layout);
        launcherWorkspaceLayout = (RelativeLayout) findViewById(R.id.launcher_home_workspace_layout);
        launcherDockRootLayout = (LinearLayout) findViewById(R.id.launcher_home_dock_root_layout);
    }

    /**
     * 初始化ViewPager
     */
    private void InitViewPager() {
        launcherViewPager = (ViewPager) findViewById(R.id.launcher_home_pages_viewpager);
        LayoutInflater layoutInflater = getLayoutInflater();
        if (defaultSharedPreference.getBoolean("pref_h_home_layout_override", false)) {
            launcherHomeView = layoutInflater.inflate(R.layout.launcher_pages_main_override_layout, null);
        } else {
            launcherHomeView = layoutInflater.inflate(R.layout.launcher_pages_main_layout, null);
        }
        launcherAppsView = (View) findViewById(R.id.launcher_home_apps_drawer_layout);
        launcherShelvesView = layoutInflater.inflate(R.layout.launcher_pages_shelves_layout, null);
        launcherViewList = new ArrayList<View>();
        launcherViewList.add(launcherHomeView);
        launcherViewList.add(launcherShelvesView);
        LauncherPagerAdapter pagerAdapter = new LauncherPagerAdapter(launcherViewList);
        launcherViewPager.setAdapter(pagerAdapter);
        launcherViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (!defaultSharedPreference.getBoolean("pref_low_visual", false)) {
                    int scrollValue = (int) (positionOffset * 255);
                    int alphaInt, shortcutAlphaInt;
                    if (scrollValue <= 254 & position == 0) {
                        try {
                            alphaInt = scrollValue;
                            shortcutAlphaInt = (-scrollValue) + 255;
                            if (alphaInt > 150) {
                                alphaInt = 150;
                            }
                            launcherRootLayout.setBackgroundColor(Color.argb(alphaInt, 0, 0, 0));
                            if (shortcutAlphaInt >= 20) {
                                if (launcherDockRootLayout.getVisibility() == View.GONE) {
                                    launcherDockRootLayout.setVisibility(View.VISIBLE);
                                }
                                scvDockDial.setImageAlpha(shortcutAlphaInt);
                                scvDockMsg.setImageAlpha(shortcutAlphaInt);
                                scvDockDrawer.setImageAlpha(shortcutAlphaInt);
                                scvDockPref.setImageAlpha(shortcutAlphaInt);
                                scvDockCam.setImageAlpha(shortcutAlphaInt);
                            } else {
                                launcherDockRootLayout.setVisibility(View.GONE);
                            }
                        } catch (Exception e) { }
                    } else if (255 - scrollValue <= 254 & position == 1) {
                        try {
                            alphaInt = 255 - scrollValue;
                            if (alphaInt > 150) {
                                alphaInt = 150;
                            }
                            launcherRootLayout.setBackgroundColor(Color.argb(alphaInt, 0, 0, 0));
                        } catch (Exception e) { }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        if (defaultSharedPreference.getBoolean("pref_low_visual", false)) {
                            launcherRootLayout.setBackgroundColor(Color.argb(0, 0 , 0, 0));
                        }
                        launcherDockRootLayout.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        if (defaultSharedPreference.getBoolean("pref_low_visual", false)) {
                            launcherRootLayout.setBackgroundColor(Color.argb(150, 0 , 0, 0));
                        }
                        launcherDockRootLayout.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        scvUserInfo = (ShortcutView) launcherHomeView.findViewById(R.id.launcher_home_pages_iv_head);
        scvShortcutFirst = (ShortcutView) launcherHomeView.findViewById(R.id.launcher_home_pages_iv_fist);
        scvShortcutSecond = (ShortcutView) launcherHomeView.findViewById(R.id.launcher_home_pages_iv_second);
        scvShortcutThird = (ShortcutView) launcherHomeView.findViewById(R.id.launcher_home_pages_iv_third);
        scvShortcutForth = (ShortcutView) launcherHomeView.findViewById(R.id.launcher_home_pages_iv_fourth);
        setUserLogo();
        setShortcutApp(defaultSharedPreference.getInt("pref_home_shortcut_first", 6), scvShortcutFirst);
        setShortcutApp(defaultSharedPreference.getInt("pref_home_shortcut_second", 1), scvShortcutSecond);
        setShortcutApp(defaultSharedPreference.getInt("pref_home_shortcut_third", 4), scvShortcutThird);
        setShortcutApp(defaultSharedPreference.getInt("pref_home_shortcut_fourth", 10), scvShortcutForth);
        tvTargetText = (TextView) launcherHomeView.findViewById(R.id.launcher_home_pages_tv_target);
        tvPopboxText = (TextView) launcherHomeView.findViewById(R.id.launcher_home_pages_tv_popbox);
        rlPopboxLayout = (RelativeLayout) launcherHomeView.findViewById(R.id.launcher_home_pages_rl_popbox);
        ivRole = (ImageView) launcherHomeView.findViewById(R.id.launcher_home_pages_iv_role);
        scvUserInfo.setOnClickListener(new HomeIconClickListener());
        scvShortcutFirst.setOnClickListener(new HomeIconClickListener());
        scvShortcutSecond.setOnClickListener(new HomeIconClickListener());
        scvShortcutThird.setOnClickListener(new HomeIconClickListener());
        scvShortcutForth.setOnClickListener(new HomeIconClickListener());
        if (defaultSharedPreference.getString("pref_target_text", "").equals("")) {
            if (!defaultSharedPreference.getBoolean("pref_not_show_text_tip", false)) {
                tvTargetText.setText("还没有设置目标哦~ 点击右边的圆形按钮进入桌面设置，选择目标文本就可以自定义啦！");
            } else {
                tvTargetText.setText("");
            }
        } else {
            tvTargetText.setText(defaultSharedPreference.getString("pref_target_text", ""));
        }
        if (defaultSharedPreference.getBoolean("pref_popup_box_show_visibility", false)) {
            rlPopboxLayout.setVisibility(View.INVISIBLE);
        } else {
            if (defaultSharedPreference.getBoolean("pref_popup_box_show_dynamic_text", false)) {
                bubbleTexts = new ArrayList<>();
                bubbleTexts.clear();
                BubbleTextDataBaseHelper bubbleTextDataBaseHelper = new BubbleTextDataBaseHelper(this);
                Cursor cursor = bubbleTextDataBaseHelper.query();
                while (cursor.moveToNext()) {
                    int Id = cursor.getInt(cursor.getColumnIndex("_id"));
                    String title = cursor.getString(cursor.getColumnIndex("BubbleContent"));
                    BubbleText bubbleText = new BubbleText();
                    bubbleText.setId(Id);
                    bubbleText.setBubbleContent(title);
                    bubbleTexts.add(bubbleText);
                }
                bubbleTextDataBaseHelper.close();
                if (bubbleTexts.isEmpty()) {
                    tvPopboxText.setText("还没有设置动态气泡文本哦~ 点击右上角的圆形按钮进入桌面设置，选择气泡配置就可以设置啦！");
                } else {
                    bubbleTextListArray.clear();
                    for (int i = 0; i < bubbleTexts.size(); i++) {
                        BubbleText bubbleText = bubbleTexts.get(i);
                        bubbleTextListArray.add(bubbleText.getBubbleContent());
                    }
                    tvPopboxText.setText(bubbleTextListArray.get(bubbleShowNum));
                    tvPopboxText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (defaultSharedPreference.getBoolean("pref_popup_box_dynamic_text_by_order", false)) {
                                bubbleShowNum++;
                                if (bubbleShowNum > bubbleTextListArray.size() - 1) {
                                    bubbleShowNum = 0;
                                }
                                tvPopboxText.setText(bubbleTextListArray.get(bubbleShowNum));
                            } else {
                                tvPopboxText.setText(bubbleTextListArray.get(new Random().nextInt(bubbleTextListArray.size())));
                            }
                        }
                    });
                }
            } else {
                if (defaultSharedPreference.getString("pref_popbox_text", "").equals("")) {
                    if (!defaultSharedPreference.getBoolean("pref_not_show_text_tip", false)) {
                        tvPopboxText.setText("还没有设置气泡文本哦~ 点击右上角的圆形按钮进入桌面设置，选择气泡文本就可以自定义啦！");
                    } else {
                        tvPopboxText.setText("");
                    }
                } else {
                    if (defaultSharedPreference.getBoolean("pref_show_welcome_message", false)) {
                        setWelcomeMessage();
                    } else {
                        tvPopboxText.setText(defaultSharedPreference.getString("pref_popbox_text", ""));
                    }
                }
            }
            if (defaultSharedPreference.getBoolean("pref_popup_box_long_press_modify", false)) {
                tvPopboxText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        startActivity(new Intent(Launcher.this, PreferenceBubble.class).putExtra("requireRestart", true));
                        finish();
                        return true;
                    }
                });
            }
        }
        if (defaultSharedPreference.getBoolean("pref_not_show_role", false)) {
            ivRole.setVisibility(View.INVISIBLE);
        } else {
            if (defaultSharedPreference.getBoolean("pref_use_builtin", true)) {
                switch (defaultSharedPreference.getInt("pref_role_builtin", 1)) {
                    case 0:
                        ivRole.setImageResource(R.drawable.res_role_honoka);
                        break;
                    case 1:
                        ivRole.setImageResource(R.drawable.res_role_kotori);
                        break;
                    case 2:
                        ivRole.setImageResource(R.drawable.res_role_umi);
                        break;
                    case 3:
                        ivRole.setImageResource(R.drawable.res_role_kke);
                        break;
                    case 4:
                        ivRole.setImageResource(R.drawable.res_role_nozomi);
                        break;
                    case 5:
                        ivRole.setImageResource(R.drawable.res_role_maki);
                        break;
                    case 6:
                        ivRole.setImageResource(R.drawable.res_role_rin);
                        break;
                    case 7:
                        ivRole.setImageResource(R.drawable.res_role_hanayo);
                        break;
                    case 8:
                        ivRole.setImageResource(R.drawable.res_role_nico);
                        break;
                    case 9:
                        ivRole.setImageResource(R.drawable.res_role_yutong);
                        break;
                    case 10:
                        try {
                            FileInputStream fis = null;
                            fis = openFileInput("main.png");
                            Bitmap bit = BitmapFactory.decodeStream(fis);
                            ivRole.setImageBitmap(bit);
                            fis.close();
                        } catch (Exception e) {
                            Snackbar.with(Launcher.this).text("加载自定义立绘失败！").show(Launcher.this);
                        }
                        break;
                }
            } else {
                try {
                    File file = new File(defaultSharedPreference.getString("pref_role_package_path", null));
                    if (file.exists()) {
                        ivRole.setImageBitmap(BitmapFactory.decodeFile(defaultSharedPreference.getString("pref_role_package_path", null)));
                    } else {
                        ivRole.setImageResource(R.drawable.res_role_kotori);
                    }
                } catch (Exception e) {
                    ivRole.setImageResource(R.drawable.res_role_kotori);
                }
            }
        }
    }

    /**
     * 设置用户头像
     */
    private void setUserLogo() {
        switch (defaultSharedPreference.getInt("pref_user_logo", Role.LOGO_DEFAULT)) {
            case Role.LOGO_DEFAULT:
                scvUserInfo.setImageResource(R.drawable.res_about_icon);
                break;
            case Role.LOGO_HONOKA:
                scvUserInfo.setImageResource(R.drawable.res_user_logo_honoka);
                break;
            case Role.LOGO_KOTORI:
                scvUserInfo.setImageResource(R.drawable.res_user_logo_kotori);
                break;
            case Role.LOGO_UMI:
                scvUserInfo.setImageResource(R.drawable.res_user_logo_umi);
                break;
            case Role.LOGO_KKE:
                scvUserInfo.setImageResource(R.drawable.res_user_logo_kke);
                break;
            case Role.LOGO_NOZOMI:
                scvUserInfo.setImageResource(R.drawable.res_user_logo_nozomi);
                break;
            case Role.LOGO_MAKI:
                scvUserInfo.setImageResource(R.drawable.res_user_logo_maki);
                break;
            case Role.LOGO_RIN:
                scvUserInfo.setImageResource(R.drawable.res_user_logo_rin);
                break;
            case Role.LOGO_HANAYO:
                scvUserInfo.setImageResource(R.drawable.res_user_logo_hanayo);
                break;
            case Role.LOGO_NICO:
                scvUserInfo.setImageResource(R.drawable.res_user_logo_nico);
                break;
            case Role.LOGO_YUTONG:
                scvUserInfo.setImageResource(R.drawable.res_user_logo_yutong);
                break;
        }
    }

    /**
     * 初始化广播器
     */
    private void InitReceiver() {
        //注册电池监听
        intentFilterBattery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        launcherReceiverBattery = new LauncherReceiverBattery();
        registerReceiver(launcherReceiverBattery, intentFilterBattery);
        //注册网络状态监听
        intentFilterNetwork = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        launcherReceiverNetworkChanged = new LauncherReceiverNetworkChanged();
        registerReceiver(launcherReceiverNetworkChanged, intentFilterNetwork);
        //注册模式改变监听
        intentFilterProfile = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        launcherReceiverProfileChanged = new LauncherReceiverRingModeChanged();
        registerReceiver(launcherReceiverProfileChanged, intentFilterProfile);
        //注册耳机插拔监听
        intentFilterHeadset = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        launcherReceiverHeadsetPlug = new LauncherReceiverHeadsetPlug();
        registerReceiver(launcherReceiverHeadsetPlug, intentFilterHeadset);
        //注册日期变更监听
        intentFilterDate = new IntentFilter(Intent.ACTION_DATE_CHANGED);
        launcherReceiverDateChanged = new LauncherReceiverDateChanged();
        registerReceiver(launcherReceiverDateChanged, intentFilterDate);
        //注册包改变监听
        launcherReceiverPackageChanged = new LauncherReceiverPackageChanged();
        intentFilterPackage = new IntentFilter();
        intentFilterPackage.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilterPackage.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilterPackage.addDataScheme("package");
        registerReceiver(launcherReceiverPackageChanged, intentFilterPackage);
        //注册Home按键监听
        launcherHomeButtonPress = new LauncherHomeButtonPress();
        intentFilterHome = new IntentFilter();
        intentFilterHome.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(launcherHomeButtonPress, intentFilterHome);
    }

    /**
     * 初始化主屏幕点击
     */
    private void InitHomeIconClick() {
        scvDockDial = (ShortcutView) findViewById(R.id.launcher_home_dock_dial);
        scvDockMsg = (ShortcutView) findViewById(R.id.launcher_home_dock_msg);
        scvDockDrawer = (ShortcutView) findViewById(R.id.launcher_home_dock_drawer);
        scvDockPref = (ShortcutView) findViewById(R.id.launcher_home_dock_pref);
        scvDockCam = (ShortcutView) findViewById(R.id.launcher_home_dock_camera);
        scvDockDial.setOnClickListener(new HomeIconClickListener());
        scvDockMsg.setOnClickListener(new HomeIconClickListener());
        scvDockDrawer.setOnClickListener(new HomeIconClickListener());
        scvDockPref.setOnClickListener(new HomeIconClickListener());
        scvDockCam.setOnClickListener(new HomeIconClickListener());
        scvDockDrawer.setOnTouchListener(new View.OnTouchListener() {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vibrator.vibrate(15);
                        break;
                    case MotionEvent.ACTION_UP:
                        vibrator.vibrate(10);
                        break;
                }
                return false;
            }
        });
        if (Utils.preferenceGetBoolean(Launcher.this, "pref_long_press_samsung_pay", false)) {
            scvDockDrawer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    startPackageSafely("com.samsung.android.spay", "Samsung Pay", view);
                    return true;
                }
            });
        }
    }

    /**
     * 主屏幕按钮点击监听器
     */
    private class HomeIconClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.launcher_home_dock_dial:
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        ActivityOptions options = ActivityOptions.makeClipRevealAnimation(scvDockDial, 0, 0, scvDockDial.getMeasuredWidth(), scvDockDial.getMeasuredHeight());
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        if (defaultSharedPreference.getBoolean("pref_low_visual", false)) {
                            startActivity(intent);
                        } else {
                            startActivity(intent, options.toBundle());
                        }
                    } else {
                        ActivityOptions options = ActivityOptions.makeScaleUpAnimation(scvDockDial, 0, 0, scvDockDial.getMeasuredWidth(), scvDockDial.getMeasuredHeight());
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        if (defaultSharedPreference.getBoolean("pref_low_visual", false)) {
                            startActivity(intent);
                        } else {
                            startActivity(intent, options.toBundle());
                        }
                    }
                    break;
                case R.id.launcher_home_dock_msg:
                    startPackageSafely(Telephony.Sms.getDefaultSmsPackage(Launcher.this), "短信", v);
                    break;
                case R.id.launcher_home_dock_drawer:
                    ObjectAnimator workSpaceAnim = ObjectAnimator.ofFloat(launcherWorkspaceLayout, "alpha", 1f, 0.0f);
                    workSpaceAnim.setDuration(250);
                    workSpaceAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            launcherWorkspaceLayout.setVisibility(View.GONE);
                        }
                    });
                    workSpaceAnim.start();
                    ObjectAnimator appsViewAnim = ObjectAnimator.ofFloat(launcherAppsView, "alpha", 0.0f, 1f);
                    appsViewAnim.setDuration(250);
                    appsViewAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            launcherAppsView.setVisibility(View.VISIBLE);
                        }
                    });
                    appsViewAnim.start();
                    break;
                case R.id.launcher_home_dock_pref:
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        ActivityOptions options = ActivityOptions.makeClipRevealAnimation(scvDockPref, 0, 0, scvDockPref.getMeasuredWidth(), scvDockPref.getMeasuredHeight());
                        if (defaultSharedPreference.getBoolean("pref_low_visual", false)) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        } else {
                            startActivity(new Intent(Settings.ACTION_SETTINGS), options.toBundle());
                        }
                    } else {
                        ActivityOptions options = ActivityOptions.makeScaleUpAnimation(scvDockPref, 0, 0, scvDockPref.getMeasuredWidth(), scvDockPref.getMeasuredHeight());
                        if (defaultSharedPreference.getBoolean("pref_low_visual", false)) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        } else {
                            startActivity(new Intent(Settings.ACTION_SETTINGS), options.toBundle());
                        }
                    }
                    break;
                case R.id.launcher_home_dock_camera:
                    try {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                            ActivityOptions options = ActivityOptions.makeClipRevealAnimation(scvDockCam, 0, 0, scvDockCam.getMeasuredWidth(), scvDockCam.getMeasuredHeight());
                            Intent intent = new Intent();
                            intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (defaultSharedPreference.getBoolean("pref_low_visual", false)) {
                                startActivity(intent);
                            } else {
                                startActivity(intent, options.toBundle());
                            }
                        } else {
                            ActivityOptions options = ActivityOptions.makeScaleUpAnimation(scvDockCam, 0, 0, scvDockCam.getMeasuredWidth(), scvDockCam.getMeasuredHeight());
                            Intent intent = new Intent();
                            intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (defaultSharedPreference.getBoolean("pref_low_visual", false)) {
                                startActivity(intent);
                            } else {
                                startActivity(intent, options.toBundle());
                            }
                        }
                    } catch (Exception e) {
                        Snackbar.with(Launcher.this).text("无法打开相机，此设备可能不支持。").show(Launcher.this);
                    }
                    break;
                case R.id.launcher_home_pages_iv_fist:
                    openShorcutApp(defaultSharedPreference.getInt("pref_home_shortcut_first", 6), v);
                    break;
                case R.id.launcher_home_pages_iv_second:
                    openShorcutApp(defaultSharedPreference.getInt("pref_home_shortcut_second", 1), v);
                    break;
                case R.id.launcher_home_pages_iv_third:
                    openShorcutApp(defaultSharedPreference.getInt("pref_home_shortcut_third", 4), v);
                    break;
                case R.id.launcher_home_pages_iv_fourth:
                    openShorcutApp(defaultSharedPreference.getInt("pref_home_shortcut_fourth", 10), v);
                    break;
                case R.id.launcher_home_pages_iv_head:
                    startActivity(new Intent(Launcher.this, LauncherUserInfo.class));
                    break;
            }
        }
    }

    /**
     * 设置主屏幕快捷方式图标
     * @param appCode 图标代号
     * @param imageView 被设置图标的View
     */
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

    private void openShorcutApp(int appCode, View v) {
        switch (appCode) {
            case 0:
                startPackageSafely("com.baidu.tieba", "百度贴吧", v);
                break;
            case 1:
                startPackageSafely("tv.danmaku.bili", "哔哩哔哩", v);
                break;
            case 2:
                startPackageSafely("com.catchingnow.icebox", "冰箱", v);
                break;
            case 3:
                startPackageSafely("com.coolapk.market", "酷市场", v);
                break;
            case 4:
                startPackageSafely(defaultSharedPreference.getString("pref_browser_app_package_name", "com.android.browser"), "浏览器", v);
                break;
            case 5:
                startPackageSafely("com.taobao.taobao", "淘宝", v);
                break;
            case 6:
                startPackageSafely("com.tencent.mobileqq", "QQ", v);
                break;
            case 7:
                startPackageSafely("com.tencent.tim", "TIM", v);
                break;
            case 8:
                startPackageSafely("com.tencent.qqlite", "QQ", v);
                break;
            case 9:
                startPackageSafely("com.netease.cloudmusic", "网易云音乐", v);
                break;
            case 10:
                startPackageSafely("com.tencent.mm", "微信", v);
                break;
            case 11:
                startPackageSafely("com.eg.android.AlipayGphone", "支付宝", v);
                break;
            case 12:
                startPackageSafely("com.ruanmei.ithome", "IT之家", v);
                break;
        }
    }

    /**
     * 初始化应用程序页面
     */
    private void InitApplicationPage() {
        //定义控件
        rvApplication = (RecyclerView) launcherAppsView.findViewById(R.id.launcher_home_apps_rl);
        ibIndexBar = (IndexBar) launcherAppsView.findViewById(R.id.launcher_home_apps_index_bar);
        ibTextView = (TextView) launcherAppsView.findViewById(R.id.launcher_home_apps_text_view);
        pbProcessing = (ProgressBar) launcherAppsView.findViewById(R.id.launcher_home_apps_progressbar);
        //初始化菜单
        appLayoutMenuTitle = (TextView) launcherAppsView.findViewById(R.id.launcher_home_apps_title);
        appLayoutMenu = (TextView) launcherAppsView.findViewById(R.id.launcher_home_apps_menu);
        appLayoutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAppLayoutMenu(view);
            }
        });
        appLayoutBack = launcherAppsView.findViewById(R.id.launcher_home_apps_back);
        appLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backFunction();
            }
        });
        //加载应用列表
        launcherApplicationViewTask = new LauncherApplicationViewTask();
        launcherApplicationViewTask.execute();
    }

    /**
     * 初始化应用列表菜单按钮点击
     */
    private void setAppLayoutMenu(View view) {
        PopupMenu appLayoutMenu = new PopupMenu(Launcher.this, view);
        Menu menu = appLayoutMenu.getMenu();
        menu.add(appUninstMode ? "退出卸载模式" : "卸载模式");
        menu.add(appInfoMode ? "退出查看信息模式" : "查看信息模式");
        appLayoutMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                new SoundKit(Launcher.this).play(SoundKit.NOTIFY);
                if (menuItem.getTitle().equals("卸载模式")) {
                    if (appInfoMode) { appInfoMode = false; }
                    appUninstMode = true;
                    appLayoutMenuTitle.setText("卸载模式");
                } else if (menuItem.getTitle().equals("查看信息模式")) {
                    if (appUninstMode) { appUninstMode = false; }
                    appInfoMode = true;
                    appLayoutMenuTitle.setText("查看信息模式");
                } else if (menuItem.getTitle().equals("退出卸载模式")) {
                    appUninstMode = false;
                    appLayoutMenuTitle.setText("应用列表");
                } else if (menuItem.getTitle().equals("退出查看信息模式")) {
                    appInfoMode = false;
                    appLayoutMenuTitle.setText("应用列表");
                }
                return false;
            }
        });
        appLayoutMenu.show();
    }

    /***
     * 获取应用信息的新线程
     */
    private class LauncherApplicationViewTask extends AsyncTask<Void, Void, List<AppData>> {

        @Override
        protected void onPreExecute() {
            pbProcessing.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<AppData> doInBackground(Void... params) {
            appDataList = new ArrayList<AppData>();
            appDataList.clear();
            pm = getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            if (isCancelled()) {
                return null;
            }
            //读取隐藏应用列表
            Set<String> strings = defaultSharedPreference.getStringSet("pref_hide_app", new HashSet<String>());
            List<ResolveInfo> packs = pm.queryIntentActivities(intent, 0);
            for (ResolveInfo ri : packs) {
                AppData appData = new AppData(ri.loadLabel(pm), ri.activityInfo.packageName, ri.loadIcon(pm));
                if (ri.activityInfo.packageName.equals("com.lkyear.lllauncher")
                        //|| ri.activityInfo.packageName.equals("com.mimikko.mimikkoui")
                        || ri.activityInfo.packageName.equals("com.meizu.flyme.launcher")) {
                    continue;
                } else {
                    if (isCancelled()) {
                        return null;
                    } else {
                        //判断是否存在隐藏应用
                        if (strings.size() > 0) {
                            //定义变量，用于当没有匹配的隐藏应用时是否继续添加应用到appDataList里
                            Boolean hasHide = false;
                            //遍历找出隐藏的应用
                            for (String pname : strings) {
                                if (pname.equals(ri.activityInfo.packageName)) {
                                    hasHide = true;
                                    break;
                                }
                            }
                            if (!hasHide) {
                                appDataList.add(appData);
                            }
                        } else {
                            appDataList.add(appData);
                        }
                    }
                }
            }
            if (isCancelled()) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<AppData> DataList) {
            pbProcessing.setVisibility(View.GONE);
            if (!isCancelled()) {
                InitApplicationList();
            }
        }
    }

    /**
     * 初始化应用程序列表
     */
    private void InitApplicationList() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        appDataList = sortDataByPinying(null, null, null);
        Collections.sort(appDataList, pinyinComparator);
        appViewAdapter = new AppViewAdapter(Launcher.this, appDataList, false);
        if (defaultSharedPreference.getBoolean("pref_five_column", false)) {
            rvApplication.setLayoutManager(new GridLayoutManager(this, 5));
        } else {
            rvApplication.setLayoutManager(new GridLayoutManager(this, 4));
        }
        rvApplication.setAdapter(appViewAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(rvApplication, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        appViewAdapter.setOnItemClickListener(new AppViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startPackageSafely(appViewAdapter.getAppData().get(position).getAppPackageName().toString(),
                        appViewAdapter.getAppData().get(position).getAppName().toString(), view);
            }
        });
        appViewAdapter.setOnItemLongClickListener(new AppViewAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                showApplicationMenu(appViewAdapter.getAppData().get(position), view);
            }
        });
        ibIndexBar.setOnTouchingLetterChangedListener(new IndexBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = appViewAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    rvApplication.smoothScrollToPosition(position);
                }
            }
        });
        ibIndexBar.setTextView(ibTextView);
        loadShelvesLayout();
    }

    public void EditLayout(View v) {
        startActivity(new Intent(Launcher.this, ShelvesManageActivity.class));
        finish();
    }

    private void loadShelvesLayout() {
        shelvesLinearLayout = (LinearLayout) launcherShelvesView.findViewById(R.id.shelves_root);
        new LoadShelvesAsyncTask().execute("");
    }

    private class LoadShelvesAsyncTask extends AsyncTask<String, Object, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            shelvesLinearLayout.removeAllViews();
        }

        @Override
        protected Void doInBackground(String... voids) {
            checkDataBase();
            try {
                shelvesList = new ArrayList<Shelves>();
                final ShelvesDataBaseHelper dataBaseHelper = new ShelvesDataBaseHelper(Launcher.this);
                Cursor cursor = dataBaseHelper.query();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex("_id"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String packages = cursor.getString(cursor.getColumnIndex("packages"));
                    Shelves shelves = new Shelves();
                    shelves.setId(id);
                    shelves.setTitle(title);
                    shelves.setPackages(packages);
                    shelvesList.add(shelves);
                }
                dataBaseHelper.close();
                if (!shelvesList.isEmpty()) {
                    for (Shelves shelves : shelvesList) {
                        appDataList = new ArrayList<AppData>();
                        appDataList.clear();
                        List<String> packages = Arrays.asList(shelves.getPackages().split(","));
                        for (String pkgName : packages) {
                            String appName = pm.getApplicationInfo(pkgName, 0).loadLabel(pm).toString();
                            Drawable appIcon = pm.getApplicationInfo(pkgName, 0).loadIcon(pm);
                            appDataList.add(new AppData(appName, pkgName, appIcon));
                        }
                        View rootView = LayoutInflater.from(Launcher.this).inflate(R.layout.shelves_item, null);
                        LinearLayout addedLinearLayout = (LinearLayout) rootView.findViewById(R.id.shelves_item_root);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView title = (TextView) addedLinearLayout.findViewById(R.id.shelves_item_title);
                        title.setText(shelves.getTitle());
                        RecyclerView recyclerView = (RecyclerView) addedLinearLayout.findViewById(R.id.shelves_item_recycler_view);
                        recyclerView.setLayoutManager(new GridLayoutManager(Launcher.this, 4) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        });
                        recyclerView.setHasFixedSize(true);
                        final AppViewAdapter appViewAdapter = new AppViewAdapter(Launcher.this, appDataList, true);
                        recyclerView.setAdapter(appViewAdapter);
                        appViewAdapter.setOnItemClickListener(new AppViewAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                startPackageSafely(appViewAdapter.getAppData().get(position).getAppPackageName(),
                                        appViewAdapter.getAppData().get(position).getAppName().toString(), view);
                            }
                        });
                        final TextView small = (TextView) addedLinearLayout.findViewById(R.id.shelves_item_hide);
                        final RelativeLayout contentLayout = (RelativeLayout)  addedLinearLayout.findViewById(R.id.shelves_item_content);
                        final LinearLayout titleLayout = (LinearLayout) addedLinearLayout.findViewById(R.id.shelves_item_title_layout);
                        small.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new SoundKit(Launcher.this).play(SoundKit.DEFAULT);
                                if (contentLayout.getVisibility() == View.GONE) {
                                    contentLayout.setVisibility(View.VISIBLE);
                                    small.setText("缩小");
                                    titleLayout.setBackgroundResource(R.drawable.shelves_title_background);
                                } else {
                                    contentLayout.setVisibility(View.GONE);
                                    small.setText("还原");
                                    titleLayout.setBackgroundResource(R.drawable.shelves_title_closed_background);
                                }
                            }
                        });
                        publishProgress(addedLinearLayout, layoutParams);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            shelvesLinearLayout.addView(((LinearLayout) values[0]), ((LinearLayout.LayoutParams)values[1]));
        }
    }

    private void checkDataBase() {
        try {
            ShelvesDataBaseHelper dataBaseHelper = new ShelvesDataBaseHelper(Launcher.this);
            Cursor cursor = dataBaseHelper.query();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String packages = cursor.getString(cursor.getColumnIndex("packages"));
                List<String> list = Arrays.asList(packages.split(","));
                list = new ArrayList<String>(list);
                Boolean needUpdate = false;
                for (int i = 0; i < list.size(); i++) {
                    if (!Utils.isPackageInstalled(Launcher.this, list.get(i))) {
                        list.remove(i);
                        needUpdate = true;
                    }
                }
                if (needUpdate) {
                    String updateValue = "";
                    for (String s : list) {
                        updateValue += s + ",";
                    }
                    updateValue = updateValue.substring(0, updateValue.length() - 1);
                    ContentValues values = new ContentValues();
                    values.put("title", title);
                    values.put("packages", updateValue);
                    dataBaseHelper.update(values, id);
                }
            }
            dataBaseHelper.close();
        } catch (Exception e) { }
    }

    private void setFistUseCase() {
        if (!Utils.preferenceGetBoolean(Launcher.this, "pref_first_showcase", false)) {
            Utils.preferencePutData(Launcher.this, "pref_first_showcase", true);
            new ShowcaseView.Builder(Launcher.this)
                    .setTarget(new ViewTarget(scvUserInfo))
                    .setStyle(R.style.LauncherShowcaseTheme)
                    .setContentTitle("功能按钮")
                    .setContentText("点击此处打开功能页，在功能页里，你可以进行各种桌面功能操作，包括帮助、升级、进入桌面设置等。")
                    .hideOnTouchOutside()
                    .build().show();
        }
    }

    /**
     *
     * 应用列表按照拼音顺序排序的方法
     *
     * @param appName 应用名称
     * @param packageName 包名
     * @param icon 图标
     * @return 列表数据
     */
    private List<AppData> sortDataByPinying(CharSequence appName,String packageName, Drawable icon) {
        List<AppData> mSortList = new ArrayList<AppData>();
        mSortList.clear();
        for (int i = 0; i < appDataList.size(); i++) {
            AppData sortModel = new AppData(appName, packageName, icon);
            sortModel.setAppName(appDataList.get(i).appName);
            sortModel.setAppIcon(appDataList.get(i).getAppIcon());
            sortModel.setAppPackageName(appDataList.get(i).getAppPackageName());
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(appDataList.get(i).appName.toString());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);
        }
        return mSortList;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void sortDataByParameter(String filterStr) {
        List<AppData> filterDateListSearch = new ArrayList<AppData>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateListSearch = appDataList;
        } else {
            filterDateListSearch.clear();
            for (AppData sortModelSearch : appDataList) {
                String name = sortModelSearch.getAppName().toString();
                String packageName = sortModelSearch.getAppPackageName().toString();
                Drawable icon = sortModelSearch.getAppIcon();
                if (name.toUpperCase().indexOf(filterStr.toString().toUpperCase()) != -1|| characterParser.getSelling(name).toUpperCase().startsWith(filterStr.toString().toUpperCase())) {
                    sortModelSearch.setAppPackageName(packageName);
                    sortModelSearch.setAppName(name);
                    sortModelSearch.setAppIcon(icon);
                    filterDateListSearch.add(sortModelSearch);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateListSearch, pinyinComparator);
        appViewAdapter = new AppViewAdapter(Launcher.this, filterDateListSearch, false);
        appViewAdapter.updateListView(filterDateListSearch);
        rvApplication.setAdapter(appViewAdapter);
        appViewAdapter.setOnItemClickListener(new AppViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startPackageSafely(appViewAdapter.getAppData().get(position).getAppPackageName().toString(),
                        appViewAdapter.getAppData().get(position).getAppName().toString(), view);
            }
        });
    }

    /**
     * 安全启动其他应用程序方法
     * @param appPackageName 应用程序包名
     * @param appName 应用程序名称
     * @param appsView 应用程序父View
     */
    private void startPackageSafely(String appPackageName, String appName, View appsView) {
        //卸载模式
        if (appUninstMode) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(appPackageName, 0);
                if (!((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)) {
                    new SoundKit(Launcher.this).play(SoundKit.DEFAULT);
                    Snackbar.with(Launcher.this).text("秘籍！禁止卸载！").show(Launcher.this);
                    return;
                }
            } catch (Exception e) {
                new SoundKit(Launcher.this).play(SoundKit.DEFAULT);
                Snackbar.with(Launcher.this).text("产生了一个错误").show(Launcher.this);
            }
            Intent unInstall = new Intent();
            unInstall.setAction(Intent.ACTION_DELETE);
            unInstall.setData(Uri.parse("package:" + appPackageName));
            unInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(unInstall);
            return;
        }
        //信息模式
        if (appInfoMode) {
            Intent intent = new Intent();
            intent.putExtra("PkgName", appPackageName);
            intent.putExtra("AppName", appName);
            intent.setClass(Launcher.this, LauncherAppInfo.class);
            startActivity(intent);
            return;
        }
        setUserLevel();
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(appPackageName, 0);
            if (packageinfo == null) {
                return;
            }
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(packageinfo.packageName);
            List<ResolveInfo> resolveinfoList = getPackageManager().queryIntentActivities(resolveIntent, 0);
            ResolveInfo resolveinfo = resolveinfoList.iterator().next();
            if (resolveinfo != null) {
                String packageName = resolveinfo.activityInfo.packageName;
                String className = resolveinfo.activityInfo.name;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    ActivityOptions options = ActivityOptions.makeClipRevealAnimation(appsView, 0, 0, appsView.getMeasuredWidth(), appsView.getMeasuredHeight());
                    if (defaultSharedPreference.getBoolean("pref_low_visual", false)) {
                        startActivity(intent);
                    } else {
                        startActivity(intent, options.toBundle());
                    }
                } else {
                    ActivityOptions options = ActivityOptions.makeScaleUpAnimation(appsView, 0, 0, appsView.getMeasuredWidth(), appsView.getMeasuredHeight());
                    if (defaultSharedPreference.getBoolean("pref_low_visual", false)) {
                        startActivity(intent);
                    } else {
                        startActivity(intent, options.toBundle());
                    }
                }
            }
        } catch (final PackageManager.NameNotFoundException nnfe) {
            SnackbarManager.show(Snackbar.with(Launcher.this).text(appName + " 无法启动，找不到活动。")
                .actionLabel("更多信息")
                .actionListener(new ActionClickListener() {
                @Override
                public void onActionClicked(Snackbar snackbar) {
                    new MessageBox(Launcher.this)
                        .show("错误详情如下，如需反馈请截图：\n" + nnfe.toString(), "错误信息", SoundKit.DEFAULT);
                }
            }));
        } catch (final Exception e) {
            SnackbarManager.show(Snackbar.with(Launcher.this).text(appName + " 无法启动，意外的错误。")
                .actionLabel("更多信息")
                .actionListener(new ActionClickListener() {
                        @Override
                        public void onActionClicked(Snackbar snackbar) {
                    new MessageBox(Launcher.this)
                        .show("错误详情如下，如需反馈请截图：\n" + e.toString(), "错误信息", SoundKit.DEFAULT);
                }
                    }));
        }
    }

    /**
     * 设置用户点数
     */
    private void setUserLevel() {
        String lastDate = defaultSharedPreference.getString("pref_level_last_add", "");
        int todayTimes = defaultSharedPreference.getInt("pref_level_add_times", 0);
        if (getDateWeek().equals(lastDate)) {
            if (todayTimes < 60) {
                userLevel.addPoint(1);
                todayTimes++;
                defaultSharedPreferenceEditor.putInt("pref_level_add_times", todayTimes).commit();
                defaultSharedPreferenceEditor.putString("pref_level_last_add", getDateWeek()).commit();
            }
        } else {
            userLevel.addPoint(1);
            todayTimes = 1;
            defaultSharedPreferenceEditor.putInt("pref_level_add_times", todayTimes).commit();
            defaultSharedPreferenceEditor.putString("pref_level_last_add", getDateWeek()).commit();
        }
    }

    /**
     * 长按应用列表图标所弹出的菜单
     * @param appData
     */
    private void showApplicationMenu(final AppData appData, View view) {
        PopupMenu applicationMenu = new PopupMenu(Launcher.this, view);
        Menu menu = applicationMenu.getMenu();
        if (appData.getAppPackageName().equals("com.eg.android.AlipayGphone")) {
            if (defaultSharedPreference.getBoolean("pref_alipay_shortcuts", false)) {
                menu.add("扫一扫");
                menu.add("付款码");
            }
        }
        menu.add("隐藏");
        menu.add("应用信息");
        final String uninstallTitle = "卸载 " + appData.getAppName();
        try {
            PackageInfo packageInfo = Launcher.this.getPackageManager().getPackageInfo(appData.getAppPackageName(), 0);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                menu.add(uninstallTitle);
            }
        } catch (Exception e) {
            new SoundKit(Launcher.this).play(SoundKit.DEFAULT);
            Snackbar.with(Launcher.this).text("添加菜单失败").show(Launcher.this);
        }
        applicationMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle().equals("应用信息")) {
                    Intent intent = new Intent();
                    intent.putExtra("PkgName", appData.getAppPackageName());
                    intent.putExtra("AppName", appData.getAppName());
                    intent.setClass(Launcher.this, LauncherAppInfo.class);
                    startActivity(intent);
                } else if (menuItem.getTitle().equals(uninstallTitle)) {
                    Intent unInstall = new Intent();
                    unInstall.setAction(Intent.ACTION_DELETE);
                    unInstall.setData(Uri.parse("package:" + appData.getAppPackageName()));
                    unInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(unInstall);
                } else if (menuItem.getTitle().equals("扫一扫")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("alipayqr://platformapi/startapp?saId=10000007")).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } catch (Exception e) {
                        Snackbar.with(Launcher.this).text("调起支付宝失败").show(Launcher.this);
                    }
                } else if (menuItem.getTitle().equals("付款码")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("alipayqr://platformapi/startapp?saId=20000056")).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } catch (Exception e) {
                        Snackbar.with(Launcher.this).text("调起支付宝失败").show(Launcher.this);
                    }
                } else if (menuItem.getTitle().equals("隐藏")) {
                    //添加隐藏应用的包名到偏好里
                    Set<String> strings = defaultSharedPreference.getStringSet("pref_hide_app", new HashSet<String>());
                    strings.add(appData.appPackageName);
                    defaultSharedPreferenceEditor.putStringSet("pref_hide_app", strings).commit();

                    //重新加载应用列表
                    launcherApplicationViewTask = new LauncherApplicationViewTask();
                    launcherApplicationViewTask.execute();

                    //弹出还原提示框
                    if (!defaultSharedPreference.getBoolean("pref_hide_app_tips_viewed", false)) {
                        new SoundKit(Launcher.this).play(SoundKit.DEFAULT);
                        new AlertDialog.Builder(Launcher.this).setTitle("嘿，你隐藏了一个应用！")
                                .setMessage("如果你希望隐藏的应用再次显示，需要进入桌面设置点击 还原隐藏的应用 选项！")
                                .setPositiveButton(android.R.string.ok, null)
                                .setNeutralButton("不再显示", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        defaultSharedPreferenceEditor.putBoolean("pref_hide_app_tips_viewed", true).commit();
                                    }
                                }).show();
                    }
                }
                return true;
            }
        });
        applicationMenu.show();
    }

    /**
     * 设置欢迎语
     */
    private void setWelcomeMessage() {
        if (!defaultSharedPreference.getBoolean("pref_popup_box_show_dynamic_text", false)) {
            tvPopboxText.setText(defaultSharedPreference.getString("pref_user_name", "用户") + " , 欢迎回来！");
            tvPopboxText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (defaultSharedPreference.getString("pref_popbox_text", "").equals("")) {
                        if (!defaultSharedPreference.getBoolean("pref_not_show_text_tip", false)) {
                            tvPopboxText.setText("还没有设置气泡文本哦~ 点击右上角的圆形按钮进入桌面设置，选择气泡文本就可以自定义啦！");
                        } else {
                            tvPopboxText.setText("");
                        }
                    } else {
                        tvPopboxText.setText(defaultSharedPreference.getString("pref_popbox_text", ""));
                    }
                }
            }, 2000);
        }
    }

    /***
     * 返回当前的日期
     * @return x年x月x日 星期x
     */
    private String getDateWeek() {
        final Calendar c = Calendar.getInstance();
        String mWay, mYear, mMonth, mDay;
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        String returnString = mYear + "年" + mMonth + "月" + mDay + "日\n星期" + mWay;
        return returnString;
    }

    /**
     * 判断是否为WiFi环境
     * @param mContext 上下文
     * @return 是否为WiFi
     */
    private boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 抽屉返回功能
     */
    private void backFunction() {
        if (launcherAppsView.getAlpha() == 1f) {
            ObjectAnimator workSpaceAnim = ObjectAnimator.ofFloat(launcherAppsView, "alpha", 1f, 0.0f);
            workSpaceAnim.setDuration(250);
            workSpaceAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    launcherAppsView.setVisibility(View.GONE);
                    appUninstMode = false;
                    appInfoMode = false;
                    appLayoutMenuTitle.setText("应用列表");
                }
            });
            workSpaceAnim.start();
            ObjectAnimator appsViewAnim = ObjectAnimator.ofFloat(launcherWorkspaceLayout, "alpha", 0.0f, 1f);
            appsViewAnim.setDuration(250);
            appsViewAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    launcherWorkspaceLayout.setVisibility(View.VISIBLE);
                }
            });
            appsViewAnim.start();
        } else {
            if (!defaultSharedPreference.getBoolean("pref_key_back", false)) {
                launcherViewPager.setCurrentItem(0, true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvStateDate = (TextView) findViewById(R.id.launcher_home_status_tv_date);
        tvStateDate.setText(getDateWeek());
        try {
            if (defaultSharedPreference.getBoolean("pref_auto_back", false)) {
                launcherViewPager.setCurrentItem(0, false);
            }
            if (defaultSharedPreference.getBoolean("pref_show_welcome_message", false)) {
                setWelcomeMessage();
            }
            if (isWifi(Launcher.this)) {
                final Calendar c = Calendar.getInstance();
                String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
                if ("7".equals(mWay)) {
                    int updateInt =(int)(1+Math.random()*(10-1+1));
                    if (updateInt == 1) {
                        //new PreferenceBackgroundUpdate(Launcher.this).execute();
                    }
                }
            }

            //防止出现返回界面时应用背景色不对头的问题，判断当前的页面，强制设置元素的背景色和隐藏值
            if (launcherViewPager.getCurrentItem() > 0) {
                //如果不在第一页，则设置为半透明背景色和隐藏Dock
                launcherRootLayout.setBackgroundColor(Color.argb(150, 0, 0, 0));
                launcherDockRootLayout.setVisibility(View.INVISIBLE);
            } else {
                //在第一页，设置为透明背景和显示Dock
                launcherRootLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                launcherDockRootLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) { }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(launcherReceiverBattery);
            unregisterReceiver(launcherReceiverNetworkChanged);
            unregisterReceiver(launcherReceiverDateChanged);
            unregisterReceiver(launcherReceiverHeadsetPlug);
            unregisterReceiver(launcherReceiverPackageChanged);
            unregisterReceiver(launcherReceiverProfileChanged);
            unregisterReceiver(launcherHomeButtonPress);
        } catch (Exception e) { }
    }

    /**
     * 电量广播
     */
    class LauncherReceiverBattery extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                TextView tvBattery = (TextView) findViewById(R.id.launcher_home_status_tv_battery);
                ImageView ivStateUsb = (ImageView) findViewById(R.id.launcher_home_status_iv_usb);
                tvBattery.setText("" + ((level * 100) / scale) + "");
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        ivStateUsb.setVisibility(View.VISIBLE);
                        int pluged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1);
                        switch (pluged) {
                            case BatteryManager.BATTERY_PLUGGED_AC:
                                ivStateUsb.setImageDrawable(getDrawable(R.drawable.res_status_bar_charge));
                                break;
                            case BatteryManager.BATTERY_PLUGGED_USB:
                                ivStateUsb.setImageDrawable(getDrawable(R.drawable.res_status_bar_usb));
                                break;
                            default:
                                ivStateUsb.setImageDrawable(getDrawable(R.drawable.res_status_bar_charge));
                                break;
                        }
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        ivStateUsb.setVisibility(View.INVISIBLE);
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        ivStateUsb.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 数据状态广播
     */
    class LauncherReceiverDateChanged extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
                tvStateDate = (TextView) findViewById(R.id.launcher_home_status_tv_date);
                tvStateDate.setText(getDateWeek());
            }
        }
    }

    /**
     * 包改变广播
     */
    class LauncherReceiverPackageChanged extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent){
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                if (launcherApplicationViewTask != null && launcherApplicationViewTask.getStatus() == AsyncTask.Status.RUNNING) {
                    launcherApplicationViewTask.cancel(true);
                    appDataList.clear();
                    rvApplication.removeAllViews();
                    appViewAdapter.clearListData();
                    while (launcherApplicationViewTask.isCancelled()) {
                        launcherApplicationViewTask = new LauncherApplicationViewTask();
                        launcherApplicationViewTask.execute();
                    }
                } else {
                    appDataList.clear();
                    rvApplication.removeAllViews();
                    appViewAdapter.clearListData();
                    launcherApplicationViewTask = new LauncherApplicationViewTask();
                    launcherApplicationViewTask.execute();
                }
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                if (launcherApplicationViewTask != null && launcherApplicationViewTask.getStatus() == AsyncTask.Status.RUNNING) {
                    launcherApplicationViewTask.cancel(true);
                    appDataList.clear();
                    rvApplication.removeAllViews();
                    appViewAdapter.clearListData();
                    launcherApplicationViewTask = new LauncherApplicationViewTask();
                    launcherApplicationViewTask.execute();
                } else {
                    appDataList.clear();
                    rvApplication.removeAllViews();
                    appViewAdapter.clearListData();
                    launcherApplicationViewTask = new LauncherApplicationViewTask();
                    launcherApplicationViewTask.execute();
                }
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
                if (launcherApplicationViewTask != null && launcherApplicationViewTask.getStatus() == AsyncTask.Status.RUNNING) {
                    launcherApplicationViewTask.cancel(true);
                    appDataList.clear();
                    rvApplication.removeAllViews();
                    appViewAdapter.clearListData();
                    launcherApplicationViewTask = new LauncherApplicationViewTask();
                    launcherApplicationViewTask.execute();
                } else {
                    appDataList.clear();
                    rvApplication.removeAllViews();
                    appViewAdapter.clearListData();
                    launcherApplicationViewTask = new LauncherApplicationViewTask();
                    launcherApplicationViewTask.execute();
                }
            }
        }
    }

    /**
     * 耳机广播
     */
    class LauncherReceiverHeadsetPlug extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ImageView headsetIcon = (ImageView) findViewById(R.id.launcher_home_status_iv_headset);
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        headsetIcon.setVisibility(View.INVISIBLE);
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        headsetIcon.setVisibility(View.VISIBLE);
                    }
                } else {
                    headsetIcon.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 情景模式广播
     */
    class LauncherReceiverRingModeChanged extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ImageView modeIcon = (ImageView) findViewById(R.id.launcher_home_status_iv_profile);
            if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
                AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                final int ringerMode = am.getRingerMode();
                switch (ringerMode) {
                    case AudioManager.RINGER_MODE_NORMAL:
                        modeIcon.setVisibility(View.INVISIBLE);
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        modeIcon.setVisibility(View.VISIBLE);
                        modeIcon.setImageDrawable(getDrawable(R.drawable.res_status_bar_ringer_vibrate));
                        break;
                    case AudioManager.RINGER_MODE_SILENT:
                        modeIcon.setVisibility(View.VISIBLE);
                        modeIcon.setImageDrawable(getDrawable(R.drawable.res_status_bar_ringer_silent));
                        break;
                }
            }
        }
    }

    /**
     * 网络状态改变广播
     */
    class LauncherReceiverNetworkChanged extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ImageView wifiSingalIcon = (ImageView) findViewById(R.id.launcher_home_status_iv_wifi);
            ImageView dataAccessIcon = (ImageView) findViewById(R.id.launcher_home_status_iv_data);
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();
            try {
                if (activeInfo.getTypeName().toString().equals("WIFI")) {
                    wifiSingalIcon.setVisibility(View.VISIBLE);
                    dataAccessIcon.setVisibility(View.INVISIBLE);
                }else if (activeInfo.getTypeName().toString().equals("MOBILE")) {
                    wifiSingalIcon.setVisibility(View.INVISIBLE);
                    dataAccessIcon.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                wifiSingalIcon.setVisibility(View.INVISIBLE);
                dataAccessIcon.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Home键按键广播
     */
    class LauncherHomeButtonPress extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra("reason");
                if (reason != null) {
                    if (reason.equals("homekey")) {
                        backFunction();
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backFunction();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
