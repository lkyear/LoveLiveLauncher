package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lkyear.lllauncher.Launcher.LauncherAppInfo;
import com.lkyear.lllauncher.Launcher.Level;
import com.lkyear.lllauncher.R;

/**
 * Created by lkyear on 2017/7/9.
 */

public class PreferenceUserInfo extends PreferenceBaseActivity {

    private SharedPreferences defaultSharedPreference;
    private SharedPreferences.Editor defaultSharedPreferenceEditor;

    private TextView tvHeadName, tvHeadLevelTitle, tvDetailLevel, tvDetailPoint, tvDetailUpPoint;
    private ProgressBar pbHeadLevel;
    private ImageView ivAchievementBegining, ivAchievementShuraField, ivAchievementSupernatural,
                        ivAchievementTobeIdol, ivAchievementWelcomeBack, ivAchievementUseConsole,
                        ivHeadLogo;

    private Level userLevel;

    private String extendCode, extendPwd;

    private Boolean canUse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setElevation(0);
        setContentView(R.layout.launcher_preference_user);
        setTitle("我的信息");
        defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(PreferenceUserInfo.this);
        defaultSharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(PreferenceUserInfo.this).edit();
        userLevel = new Level(PreferenceUserInfo.this);
        Init();
    }

    private void Init() {
        tvHeadName = (TextView) findViewById(R.id.launcher_pref_user_head_name);
        pbHeadLevel = (ProgressBar) findViewById(R.id.launcher_pref_user_head_level);
        tvHeadLevelTitle = (TextView) findViewById(R.id.launcher_pref_user_head_level_text);
        tvDetailLevel = (TextView) findViewById(R.id.launcher_pref_user_info_detail_level);
        tvDetailPoint = (TextView) findViewById(R.id.launcher_pref_user_info_detail_point);
        tvDetailUpPoint = (TextView) findViewById(R.id.launcher_pref_user_info_detail_up_point);
        ivAchievementBegining = (ImageView) findViewById(R.id.launcher_pref_user_info_achievement_beginning);
        ivAchievementShuraField = (ImageView) findViewById(R.id.launcher_pref_user_info_achievement_shura_field);
        ivAchievementSupernatural = (ImageView) findViewById(R.id.launcher_pref_user_info_achievement_supernatural);
        ivAchievementTobeIdol = (ImageView) findViewById(R.id.launcher_pref_user_info_achievement_tobe_idol);
        ivAchievementUseConsole = (ImageView) findViewById(R.id.launcher_pref_user_info_achievement_use_console);
        ivAchievementWelcomeBack = (ImageView) findViewById(R.id.launcher_pref_user_info_achievement_welcome_back);
        ivHeadLogo = (ImageView) findViewById(R.id.launcher_pref_user_head_logo);
        setHeadLogo();
        setAchievement();
        tvHeadName.setText(defaultSharedPreference.getString("pref_user_name", "未设置昵称"));
        pbHeadLevel.setMax(userLevel.LEVEL_MAX_VALUE);
        int ProgressValue = userLevel.LEVEL_USER_VALUE;
        if (ProgressValue < 0) {
            ProgressValue = 0;
        }
        pbHeadLevel.setProgress(ProgressValue);
        tvHeadLevelTitle.setText(userLevel.LEVEL_USER_STRING + "(" + userLevel.LEVEL_USER_VALUE + "/" + userLevel.LEVEL_MAX_VALUE + ")");
        tvDetailLevel.setText("" + userLevel.LEVEL_USER_STRING);
        tvDetailPoint.setText("点数：" + userLevel.LEVEL_USER_VALUE);
        int maxValue = userLevel.LEVEL_MAX_VALUE;
        int uValue = userLevel.LEVEL_USER_VALUE;
        tvDetailUpPoint.setText("升级还需点数：" + (maxValue - uValue));
    }

    private void setAchievement() {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        if (!userLevel.getAchievement(Level.ACHIEVEMENT_BEGINNING)) {
            ivAchievementBegining.setColorFilter(filter);
        }
        if (!userLevel.getAchievement(Level.ACHIEVEMENT_SHURA_FIELD)) {
            ivAchievementShuraField.setColorFilter(filter);
        }
        if (!userLevel.getAchievement(Level.ACHIEVEMENT_SUPERNATURAL)) {
            ivAchievementSupernatural.setColorFilter(filter);
        }
        if (!userLevel.getAchievement(Level.ACHIEVEMENT_TOBE_IDOL)) {
            ivAchievementTobeIdol.setColorFilter(filter);
        }
        if (!userLevel.getAchievement(Level.ACHIEVEMENT_WELCOME_BACK)) {
            ivAchievementWelcomeBack.setColorFilter(filter);
        }
        if (!userLevel.getAchievement(Level.ACHIEVEMENT_USE_CONSOLE)) {
            ivAchievementUseConsole.setColorFilter(filter);
        }
    }

    public void changeName(View view) {
        String userName = defaultSharedPreference.getString("pref_user_name", "");
        final EditText edit = new EditText(PreferenceUserInfo.this);
        edit.setText(userName);
        edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        new AlertDialog.Builder(PreferenceUserInfo.this).setTitle("设置用户名")
                .setView(edit).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!TextUtils.isEmpty(edit.getText().toString().trim())) {
                    if (checkUserName(edit.getText().toString().trim())) {
                        defaultSharedPreferenceEditor.putString("pref_user_name", edit.getText().toString().trim()).commit();
                        tvHeadName.setText(edit.getText().toString().trim());
                    }
                } else {
                    Toast.makeText(PreferenceUserInfo.this, "昵称不可为空", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton(android.R.string.no, null).setCancelable(false).show();
    }

    private Boolean checkUserName(String userName) {
        if (userName.equals("年糕") || userName.equals("lkyear")) {
            final EditText editTexta = new EditText(PreferenceUserInfo.this);
            new AlertDialog.Builder(PreferenceUserInfo.this)
                    .setTitle("请输入授权码:")
                    .setView(editTexta)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (editTexta.getText().equals("sakura")) {
                                canUse = true;
                            } else {
                                Toast.makeText(PreferenceUserInfo.this, "授权码错误，您不可以使用此用户名。", Toast.LENGTH_LONG).show();
                                canUse = true;
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(PreferenceUserInfo.this, "授权码错误，您不可以使用此用户名。", Toast.LENGTH_LONG).show();
                            canUse = false;
                        }
                    })
                    .setCancelable(false)
                    .show();
            return canUse;
        } else {
            return true;
        }
    }

    public void changeIcon(View view) {
        LinearLayout linearLayoutMain = new LinearLayout(PreferenceUserInfo.this);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GridView gridView = new GridView(PreferenceUserInfo.this);//this为获取当前的上下文
        gridView.setNumColumns(4);
        int[] iconId = new int[] {R.drawable.res_user_logo_honoka, R.drawable.res_user_logo_kotori,
        R.drawable.res_user_logo_umi, R.drawable.res_user_logo_kke, R.drawable.res_user_logo_nozomi,
        R.drawable.res_user_logo_maki, R.drawable.res_user_logo_rin, R.drawable.res_user_logo_hanayo,
        R.drawable.res_user_logo_nico, R.drawable.res_user_logo_yutong, R.drawable.res_about_icon};
        gridView.setAdapter(new PreferenceUserLogoAdapter(PreferenceUserInfo.this, iconId));
        linearLayoutMain.addView(gridView);
        final AlertDialog alertDialog = new AlertDialog.Builder(PreferenceUserInfo.this).setTitle("选择头像").setView(linearLayoutMain).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        defaultSharedPreferenceEditor.putInt("pref_user_logo", Role.LOGO_HONOKA).commit();
                        break;
                    case 1:
                        defaultSharedPreferenceEditor.putInt("pref_user_logo", Role.LOGO_KOTORI).commit();
                        break;
                    case 2:
                        defaultSharedPreferenceEditor.putInt("pref_user_logo", Role.LOGO_UMI).commit();
                        break;
                    case 3:
                        defaultSharedPreferenceEditor.putInt("pref_user_logo", Role.LOGO_KKE).commit();
                        break;
                    case 4:
                        defaultSharedPreferenceEditor.putInt("pref_user_logo", Role.LOGO_NOZOMI).commit();
                        break;
                    case 5:
                        defaultSharedPreferenceEditor.putInt("pref_user_logo", Role.LOGO_MAKI).commit();
                        break;
                    case 6:
                        defaultSharedPreferenceEditor.putInt("pref_user_logo", Role.LOGO_RIN).commit();
                        break;
                    case 7:
                        defaultSharedPreferenceEditor.putInt("pref_user_logo", Role.LOGO_HANAYO).commit();
                        break;
                    case 8:
                        defaultSharedPreferenceEditor.putInt("pref_user_logo", Role.LOGO_NICO).commit();
                        break;
                    case 9:
                        defaultSharedPreferenceEditor.putInt("pref_user_logo", Role.LOGO_YUTONG).commit();
                        break;
                    case 10:
                        defaultSharedPreferenceEditor.putInt("pref_user_logo", Role.LOGO_DEFAULT).commit();
                        break;
                }
                setHeadLogo();
                alertDialog.dismiss();
            }
        });
    }

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

    /**
     * 提示获取到的继承码
     * @param pwd 继承密码
     */
    private void getExtendCode(String pwd) {
        final String extendCode = userLevel.getExtendCode(pwd);
        if (extendCode.equals("ERROR")) {
            Toast.makeText(PreferenceUserInfo.this, "生成失败", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this).setMessage("继承码已经生成：\n" + extendCode + "\n请记好继承密码 " + pwd + " 。")
                .setPositiveButton(android.R.string.ok, null)
                .setNeutralButton(android.R.string.copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, extendCode));
                        Toast.makeText(PreferenceUserInfo.this, "继承码已复制", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void getOtherExtendCode(String pwd, String snCode) {
        final String extendCode = userLevel.getExtendCodeWithSn(pwd, snCode);
        if (extendCode.equals("ERROR")) {
            Toast.makeText(PreferenceUserInfo.this, "生成失败", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this).setMessage("继承码已经生成：\n" + extendCode + "\n请记好继承密码 " + pwd + " 。")
                .setPositiveButton(android.R.string.ok, null)
                .setNeutralButton(android.R.string.copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, extendCode));
                        Toast.makeText(PreferenceUserInfo.this, "继承码已复制", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private class UseCodeTask extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(PreferenceUserInfo.this, "正在校验继承码...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return userLevel.setExtend(extendCode, extendPwd);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (bool) {
                Toast.makeText(PreferenceUserInfo.this, "继承成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PreferenceUserInfo.this, "继承失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.launcher_preference_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_create_extend_code:
                new AlertDialog.Builder(this).setMessage("生成的继承码仅适用于此设备，并且有效期只有 1 自然日，可以重复使用，使用继承码时请保持互联网连接。" +
                        "\n生成成功后，现有的用户数据会被立刻清除。\n\n此功能仍处于实验期间，具有不确定因素。")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final EditText editText = new EditText(PreferenceUserInfo.this);
                                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                                new AlertDialog.Builder(PreferenceUserInfo.this).setTitle("输入继承密码(长度50以内)")
                                        .setView(editText).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getExtendCode(editText.getText().toString().trim());
                                    }
                                }).setNegativeButton(android.R.string.no, null).show();
                            }
                        })
                        .setNeutralButton("其他设备", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(PreferenceUserInfo.this).setMessage("本机的转移码是：\n" + userLevel.getThisDeviceBuild() + "\n从其他设备" +
                                        "继承数据时要用到此转移码。")
                                        .setPositiveButton(android.R.string.ok, null)
                                        .setNeutralButton("生成转移继承码", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final EditText editTexta = new EditText(PreferenceUserInfo.this);
                                                editTexta.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                new AlertDialog.Builder(PreferenceUserInfo.this).setTitle("输入转移码")
                                                        .setView(editTexta).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        final EditText editText = new EditText(PreferenceUserInfo.this);
                                                        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                                                        new AlertDialog.Builder(PreferenceUserInfo.this).setTitle("输入继承密码(长度50以内)")
                                                                .setView(editText).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                getOtherExtendCode(editText.getText().toString().trim(), editTexta.getText().toString().trim());
                                                            }
                                                        }).setNegativeButton(android.R.string.no, null).show();
                                                    }
                                                }).setNegativeButton(android.R.string.no, null).show();
                                            }
                                        }).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case R.id.menu_use_extend_code:
                final EditText editText = new EditText(PreferenceUserInfo.this);
                new AlertDialog.Builder(PreferenceUserInfo.this).setTitle("输入继承码")
                        .setView(editText).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(editText.getText().toString().trim())) {
                            final EditText editTextPwd = new EditText(PreferenceUserInfo.this);
                            editTextPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            editTextPwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                            new AlertDialog.Builder(PreferenceUserInfo.this).setTitle("输入继承密码")
                                    .setView(editTextPwd).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    extendCode = editText.getText().toString().trim();
                                    extendPwd = editTextPwd.getText().toString().trim();
                                    new UseCodeTask().execute();
                                }
                            }).setNegativeButton(android.R.string.no, null).show();
                        } else {
                            Toast.makeText(PreferenceUserInfo.this, "继承码无效", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton(android.R.string.no, null).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
