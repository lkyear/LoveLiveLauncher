package com.lkyear.lllauncher.Setup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lkyear.lllauncher.Preference.PreferenceBrowser;
import com.lkyear.lllauncher.Preference.PreferenceUserLogoAdapter;
import com.lkyear.lllauncher.Preference.Role;
import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.Utils;

/**
 * Created by lkyear on 2017/7/17.
 */

public class SetupUser extends Activity {

    private EditText tvUserName;
    private ImageView ivUserLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_user);
        tvUserName = (EditText) findViewById(R.id.launcher_setup_user_name);
        ivUserLogo = (ImageView) findViewById(R.id.launcher_setup_user_logo);
        tvUserName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        tvUserName.setText(Utils.preferenceGetString(SetupUser.this, "pref_user_name"));
        setHeadLogo();
    }

    public void changeLogo(View view) {
        LinearLayout linearLayoutMain = new LinearLayout(SetupUser.this);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GridView gridView = new GridView(SetupUser.this);//this为获取当前的上下文
        gridView.setNumColumns(4);
        int[] iconId = new int[] {R.drawable.res_user_logo_honoka, R.drawable.res_user_logo_kotori,
                R.drawable.res_user_logo_umi, R.drawable.res_user_logo_kke, R.drawable.res_user_logo_nozomi,
                R.drawable.res_user_logo_maki, R.drawable.res_user_logo_rin, R.drawable.res_user_logo_hanayo,
                R.drawable.res_user_logo_nico, R.drawable.res_user_logo_yutong, R.drawable.res_about_icon};
        gridView.setAdapter(new PreferenceUserLogoAdapter(SetupUser.this, iconId));
        linearLayoutMain.addView(gridView);
        final AlertDialog alertDialog = new AlertDialog.Builder(SetupUser.this).setTitle("选择头像").setView(linearLayoutMain).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Utils.preferencePutData(SetupUser.this, "pref_user_logo", Role.LOGO_HONOKA);
                        break;
                    case 1:
                        Utils.preferencePutData(SetupUser.this, "pref_user_logo", Role.LOGO_KOTORI);
                        break;
                    case 2:
                        Utils.preferencePutData(SetupUser.this, "pref_user_logo", Role.LOGO_UMI);
                        break;
                    case 3:
                        Utils.preferencePutData(SetupUser.this, "pref_user_logo", Role.LOGO_KKE);
                        break;
                    case 4:
                        Utils.preferencePutData(SetupUser.this, "pref_user_logo", Role.LOGO_NOZOMI);
                        break;
                    case 5:
                        Utils.preferencePutData(SetupUser.this, "pref_user_logo", Role.LOGO_MAKI);
                        break;
                    case 6:
                        Utils.preferencePutData(SetupUser.this, "pref_user_logo", Role.LOGO_RIN);
                        break;
                    case 7:
                        Utils.preferencePutData(SetupUser.this, "pref_user_logo", Role.LOGO_HANAYO);
                        break;
                    case 8:
                        Utils.preferencePutData(SetupUser.this, "pref_user_logo", Role.LOGO_NICO);
                        break;
                    case 9:
                        Utils.preferencePutData(SetupUser.this, "pref_user_logo", Role.LOGO_YUTONG);
                        break;
                    case 10:
                        Utils.preferencePutData(SetupUser.this, "pref_user_logo", Role.LOGO_DEFAULT);
                        break;
                }
                setHeadLogo();
                alertDialog.dismiss();
            }
        });
    }

    private void setHeadLogo() {
        switch (Utils.preferenceGetInt(SetupUser.this, "pref_user_logo")) {
            case Role.LOGO_DEFAULT:
                ivUserLogo.setImageResource(R.drawable.res_about_icon);
                break;
            case Role.LOGO_HONOKA:
                ivUserLogo.setImageResource(R.drawable.res_user_logo_honoka);
                break;
            case Role.LOGO_KOTORI:
                ivUserLogo.setImageResource(R.drawable.res_user_logo_kotori);
                break;
            case Role.LOGO_UMI:
                ivUserLogo.setImageResource(R.drawable.res_user_logo_umi);
                break;
            case Role.LOGO_KKE:
                ivUserLogo.setImageResource(R.drawable.res_user_logo_kke);
                break;
            case Role.LOGO_NOZOMI:
                ivUserLogo.setImageResource(R.drawable.res_user_logo_nozomi);
                break;
            case Role.LOGO_MAKI:
                ivUserLogo.setImageResource(R.drawable.res_user_logo_maki);
                break;
            case Role.LOGO_RIN:
                ivUserLogo.setImageResource(R.drawable.res_user_logo_rin);
                break;
            case Role.LOGO_HANAYO:
                ivUserLogo.setImageResource(R.drawable.res_user_logo_hanayo);
                break;
            case Role.LOGO_NICO:
                ivUserLogo.setImageResource(R.drawable.res_user_logo_nico);
                break;
            case Role.LOGO_YUTONG:
                ivUserLogo.setImageResource(R.drawable.res_user_logo_yutong);
                break;
        }
    }

    public void changeBrowser(View view) {
        startActivity(new Intent(SetupUser.this, PreferenceBrowser.class));
    }

    public void nextClickUser(View view) {
        if (TextUtils.isEmpty(tvUserName.getText().toString().trim())) {
            tvUserName.setError("此项不能为空");
            return;
        }

        if (TextUtils.isEmpty(Utils.preferenceGetString(SetupUser.this, "pref_browser_app_package_name"))) {
            Toast.makeText(SetupUser.this, "没有设置默认浏览器！", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(Utils.preferenceGetString(SetupUser.this, "pref_browser_app_package_name"))) {
            Toast.makeText(SetupUser.this, "没有设置默认浏览器！", Toast.LENGTH_LONG).show();
            return;
        }

        if (tvUserName.getText().equals("年糕") || tvUserName.getText().equals("lkyear")) {
            final EditText editText = new EditText(SetupUser.this);
            new AlertDialog.Builder(SetupUser.this)
                    .setTitle("请输入授权码:")
                    .setView(editText)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!editText.getText().equals("sakura!")) {
                                Toast.makeText(SetupUser.this, "授权码错误，您不可以使用此用户名。", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(SetupUser.this, "授权码错误，您不可以使用此用户名。", Toast.LENGTH_LONG).show();
                            return;
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

        Utils.preferencePutData(SetupUser.this, "pref_user_name", tvUserName.getText().toString().trim());
        startActivity(new Intent(SetupUser.this, SetupPack.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
