package com.lkyear.lllauncher.Launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lkyear.lllauncher.Preference.PreferenceMain;
import com.lkyear.lllauncher.Preference.PreferenceRole;
import com.lkyear.lllauncher.Preference.PreferenceRoleSwitch;
import com.lkyear.lllauncher.Preference.PreferenceUpdate;
import com.lkyear.lllauncher.Preference.PreferenceUserInfo;
import com.lkyear.lllauncher.Preference.Role;
import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Torch.Splash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkyear on 17/5/23.
 */

public class LauncherUserInfo extends Activity {

    private TextView btPreference, btHelp, btUpdate, headName, headLevelTitle;
    private ImageView ivHeadLogo;
    private RecyclerView rvFunction;
    private ProgressBar headLevel;

    private WifiManager wifiManager;

    private SharedPreferences defaultSharedPreference;
    private Level userLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_user_info_layout);
        defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(LauncherUserInfo.this);
        Init();
        InitClick();
        InitData();
    }

    /**
     * 初始化
     */
    private void Init() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        rvFunction = (RecyclerView) findViewById(R.id.launcher_user_info_rv_func);
        btPreference = (TextView) findViewById(R.id.launcher_user_info_tv_preference);
        btHelp = (TextView) findViewById(R.id.launcher_user_info_tv_help);
        btUpdate = (TextView) findViewById(R.id.launcher_user_info_tv_update);
        headName = (TextView) findViewById(R.id.launcher_user_info_head_name);
        ivHeadLogo = (ImageView) findViewById(R.id.launcher_user_info_head_logo);
        setHeadLogo();
        headName.setText(defaultSharedPreference.getString("pref_user_name", "未设置昵称"));
        headLevel = (ProgressBar) findViewById(R.id.launcher_user_info_head_level);
        headLevelTitle = (TextView) findViewById(R.id.launcher_user_info_head_level_text);
        userLevel = new Level(LauncherUserInfo.this);
        headLevel.setMax(userLevel.LEVEL_MAX_VALUE);
        int ProgressValue = userLevel.LEVEL_USER_VALUE;
        if (ProgressValue < 0) {
            ProgressValue = 0;
        }
        headLevel.setProgress(ProgressValue);
        headLevelTitle.setText(userLevel.LEVEL_USER_STRING + "(" + userLevel.LEVEL_USER_VALUE + "/" + userLevel.LEVEL_MAX_VALUE + ")");
    }

    /**
     * 初始化点击事件
     */
    private void InitClick() {
        btPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Launcher.LAUNCHER_HOME.finish();
                Launcher.LAUNCHER_HOME.onDestroy();
                startActivity(new Intent(LauncherUserInfo.this, PreferenceMain.class));
                finish();
            }
        });
        btHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LauncherUserInfo.this, LauncherWebView.class).putExtra("open_html_type", LauncherWebView.HTML_USER));
            }
        });
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LauncherUserInfo.this, PreferenceUpdate.class));
                finish();
            }
        });
        ivHeadLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherUserInfo.this, PreferenceUserInfo.class));
                finish();
            }
        });
    }

    /**
     * 初始化列表数据
     */
    private void InitData() {
        List<LauncherUserInfoFunction> funcList = new ArrayList<>();
        int[] resId = new int[] {/*R.drawable.res_user_info_control_light, R.drawable.res_user_info_control_profile,*/
        R.drawable.res_user_info_control_role, R.drawable.res_user_info_control_wallpaper,
                R.drawable.res_user_info_control_wlan, R.drawable.res_user_info_control_donate};
        String[] resStr = new String[] {/*"手电筒", "标准/静音",*/ "立绘", "壁纸", "WLAN", "支持作者"};
        for (int i = 0; i < resId.length; i++) {
            LauncherUserInfoFunction luif = new LauncherUserInfoFunction(getDrawable(resId[i]), resStr[i]);
            funcList.add(luif);
        }
        final LauncherUserInfoFunctionAdapter adapter = new LauncherUserInfoFunctionAdapter(LauncherUserInfo.this, funcList);
        rvFunction.setLayoutManager(new GridLayoutManager(this, 4));
        rvFunction.setAdapter(adapter);
        adapter.setOnItemClickListener(new LauncherUserInfoFunctionAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (adapter.getFuncData().get(position).getTitle().toString().trim()) {
                    /*case "手电筒":
                        startActivity(new Intent(LauncherUserInfo.this, Splash.class));
                        finish();
                        break;
                    case "标准/静音":
                        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                        int ringerMode = audioManager.getRingerMode();
                        if (ringerMode == AudioManager.RINGER_MODE_SILENT || ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            Toast.makeText(LauncherUserInfo.this, "标准", Toast.LENGTH_SHORT).show();
                        } else {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            Toast.makeText(LauncherUserInfo.this, "静音", Toast.LENGTH_SHORT).show();
                        }
                        break;*/
                    case "立绘":
                        Launcher.LAUNCHER_HOME.finish();
                        Launcher.LAUNCHER_HOME.onDestroy();
                        if (defaultSharedPreference.getBoolean("pref_not_show_role", false)) {
                            startActivity(new Intent(LauncherUserInfo.this, PreferenceRole.class).putExtra("needRestart", true));
                        } else {
                            startActivity(new Intent(LauncherUserInfo.this, PreferenceRoleSwitch.class).putExtra("needRestart", true));
                        }
                        finish();
                        break;
                    case "壁纸":
                        //生成一个设置壁纸的请求
                        final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
                        Intent chooser = Intent.createChooser(pickWallpaper,"设置壁纸");
                        //发送设置壁纸的请求
                        startActivity(chooser);
                        finish();
                        break;
                    case "WLAN":
                        int WLANStatus = wifiManager.getWifiState();
                        switch (WLANStatus) {
                            case WifiManager.WIFI_STATE_DISABLING:
                                Toast.makeText(LauncherUserInfo.this, "WLAN关闭中", Toast.LENGTH_SHORT).show();
                                break;
                            case WifiManager.WIFI_STATE_ENABLING:
                                Toast.makeText(LauncherUserInfo.this, "WLAN开启中", Toast.LENGTH_SHORT).show();
                                break;
                            case WifiManager.WIFI_STATE_DISABLED:
                                wifiManager.setWifiEnabled(true);
                                Toast.makeText(LauncherUserInfo.this, "WLAN已启用", Toast.LENGTH_SHORT).show();
                                break;
                            case WifiManager.WIFI_STATE_ENABLED:
                                wifiManager.setWifiEnabled(false);
                                Toast.makeText(LauncherUserInfo.this, "WLAN已禁用", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(LauncherUserInfo.this, "WLAN错误", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        break;
                    case "支持作者":
                        startActivity(new Intent(LauncherUserInfo.this, SupportPay.class));
                        finish();
                        break;
                }
            }
        });
    }

    /**
     * 设置用户头像
     */
    private void setHeadLogo() {
        switch (defaultSharedPreference.getInt("pref_user_logo", Role.LOGO_DEFAULT)) {
            case Role.LOGO_DEFAULT:
                ivHeadLogo.setImageResource(R.drawable.res_about_icon);
                break;
            case Role.LOGO_HONOKA:
                ivHeadLogo.setImageResource(R.drawable.res_user_logo_honoka);
                break;
            case Role.LOGO_KOTORI:
                ivHeadLogo.setImageResource(R.drawable.res_user_logo_kotori);
                break;
            case Role.LOGO_UMI:
                ivHeadLogo.setImageResource(R.drawable.res_user_logo_umi);
                break;
            case Role.LOGO_KKE:
                ivHeadLogo.setImageResource(R.drawable.res_user_logo_kke);
                break;
            case Role.LOGO_NOZOMI:
                ivHeadLogo.setImageResource(R.drawable.res_user_logo_nozomi);
                break;
            case Role.LOGO_MAKI:
                ivHeadLogo.setImageResource(R.drawable.res_user_logo_maki);
                break;
            case Role.LOGO_RIN:
                ivHeadLogo.setImageResource(R.drawable.res_user_logo_rin);
                break;
            case Role.LOGO_HANAYO:
                ivHeadLogo.setImageResource(R.drawable.res_user_logo_hanayo);
                break;
            case Role.LOGO_NICO:
                ivHeadLogo.setImageResource(R.drawable.res_user_logo_nico);
                break;
            case Role.LOGO_YUTONG:
                ivHeadLogo.setImageResource(R.drawable.res_user_logo_yutong);
                break;
        }
    }
}
