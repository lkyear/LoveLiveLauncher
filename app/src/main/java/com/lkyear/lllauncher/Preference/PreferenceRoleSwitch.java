package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lkyear.lllauncher.Launcher.Launcher;
import com.lkyear.lllauncher.Launcher.LauncherWebView;
import com.lkyear.lllauncher.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置标识：
 * SharedPreference:
 *      是否为内建立绘 - pref_use_builtin - Boolean - true(使用) false(不使用)
 *      内置立绘样式 - pref_role_builtin - int - 0-9
 *      立绘包立绘路径 - pref_role_package_path - String
 *      立绘包立绘名称 - pref_role_package_title - String
 *      立绘包立绘注释 - pref_role_package_mark - String
 */

public class PreferenceRoleSwitch extends PreferenceBaseActivity {

    private static final int CHOOSE_PICTURE = 1;

    private TextView tvHelp, tvSwitch, tvTitle, tvProvider;
    private ImageView ivRole;

    private SharedPreferences.Editor sharedPreferencesEditor;
    private SharedPreferences sharedPreferences;

    private Boolean needRestart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_role_switch_layout);
        setTitle("更换立绘");
        Intent intent = getIntent();
        needRestart = intent.getBooleanExtra("needRestart", false);
        Init();
    }

    private void Init() {
        sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(PreferenceRoleSwitch.this).edit();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PreferenceRoleSwitch.this);
        tvTitle = (TextView) findViewById(R.id.launcher_role_tv_title);
        tvProvider = (TextView) findViewById(R.id.launcher_role_tv_provider);
        ivRole = (ImageView) findViewById(R.id.launcher_role_iv_role);
        tvHelp = (TextView) findViewById(R.id.launcher_role_tv_help);
        tvSwitch = (TextView) findViewById(R.id.launcher_role_tv_switch);
        if (sharedPreferences.getBoolean("pref_use_builtin", true)) {
            switch (sharedPreferences.getInt("pref_role_builtin", 1)) {
                case 0:
                    ivRole.setImageResource(R.drawable.res_role_honoka);
                    tvTitle.setText("高坂穗乃果");
                    tvProvider.setText("内建 | 提供：佚名");
                    break;
                case 1:
                    ivRole.setImageResource(R.drawable.res_role_kotori);
                    tvTitle.setText("南小鸟");
                    tvProvider.setText("内建 | 提供：佚名");
                    break;
                case 2:
                    ivRole.setImageResource(R.drawable.res_role_umi);
                    tvTitle.setText("园田海未");
                    tvProvider.setText("内建 | 提供：佚名");
                    break;
                case 3:
                    ivRole.setImageResource(R.drawable.res_role_kke);
                    tvTitle.setText("绚濑绘里");
                    tvProvider.setText("内建 | 提供：佚名");
                    break;
                case 4:
                    ivRole.setImageResource(R.drawable.res_role_nozomi);
                    tvTitle.setText("东条希");
                    tvProvider.setText("内建 | 提供：佚名");
                    break;
                case 5:
                    ivRole.setImageResource(R.drawable.res_role_maki);
                    tvTitle.setText("西木野真姬");
                    tvProvider.setText("内建 | 提供：佚名");
                    break;
                case 6:
                    ivRole.setImageResource(R.drawable.res_role_rin);
                    tvTitle.setText("星空凛");
                    tvProvider.setText("内建 | 提供：佚名");
                    break;
                case 7:
                    ivRole.setImageResource(R.drawable.res_role_hanayo);
                    tvTitle.setText("小泉花阳");
                    tvProvider.setText("内建 | 提供：佚名");
                    break;
                case 8:
                    ivRole.setImageResource(R.drawable.res_role_nico);
                    tvTitle.setText("矢泽妮可");
                    tvProvider.setText("内建 | 提供：佚名");
                    break;
                case 9:
                    ivRole.setImageResource(R.drawable.res_role_yutong);
                    tvTitle.setText("雨桐");
                    tvProvider.setText("内建 | 提供：佚名");
                    break;
                case 10:
                    try {
                        FileInputStream fis = null;
                        fis = openFileInput("main.png");
                        Bitmap bit = BitmapFactory.decodeStream(fis);
                        ivRole.setImageBitmap(bit);
                        fis.close();
                        tvTitle.setText("图库自选立绘");
                        tvProvider.setText("图库 | 提供：无");
                    } catch (final Exception e) {
                        SnackbarManager.show(Snackbar.with(PreferenceRoleSwitch.this).text("意外的错误。")
                                .actionLabel("更多信息")
                                .actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {
                                        new AlertDialog.Builder(PreferenceRoleSwitch.this)
                                                .setTitle("错误信息")
                                                .setMessage("错误详情如下，如需反馈请截图：\n" + e.toString())
                                                .setPositiveButton(android.R.string.ok, null)
                                                .show();
                                    }
                                }));
                    }
                    break;
            }
        } else {
            try {
                File file = new File(sharedPreferences.getString("pref_role_package_path", null));
                if (file.exists()) {
                    ivRole.setImageBitmap(BitmapFactory.decodeFile(sharedPreferences.getString("pref_role_package_path", "")));
                    tvTitle.setText(sharedPreferences.getString("pref_role_package_title", "读取失败"));
                    tvProvider.setText(sharedPreferences.getString("pref_role_package_mark", "NULL"));
                } else {
                    ivRole.setImageResource(R.drawable.res_role_honoka);
                    tvTitle.setText("高坂穗乃果");
                    tvProvider.setText("内建 | 提供：佚名");
                    sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                    sharedPreferencesEditor.putInt("pref_role_builtin", 0).commit();
                }
            } catch (Exception e) {
                ivRole.setImageResource(R.drawable.res_role_honoka);
                tvTitle.setText("高坂穗乃果");
                tvProvider.setText("内建 | 提供：佚名");
                sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                sharedPreferencesEditor.putInt("pref_role_builtin", 0).commit();
            }
        }
        tvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PreferenceRoleSwitch.this, LauncherWebView.class).putExtra("open_html_type", LauncherWebView.HTML_ROLE));
            }
        });
        tvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(PreferenceRoleSwitch.this).setItems(new String[]{"内建立绘", "立绘包", "图库"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                roleChoiceBuiltin();
                                break;
                            case 1:
                                roleChoicePackage();
                                break;
                            case 2:
                                roleChoiceGallery();
                                break;
                        }
                    }
                }).show();
            }
        });
    }

    private void roleChoiceBuiltin() {
        new AlertDialog.Builder(PreferenceRoleSwitch.this).setItems(new String[]{"高坂穗乃果", "南小鸟", "园田海未",
        "绚濑绘里", "东条希", "西木野真姬", "星空凛", "小泉花阳", "矢泽妮可", "雨桐"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        //果果
                        sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                        sharedPreferencesEditor.putInt("pref_role_builtin", 0).commit();
                        ivRole.setImageResource(R.drawable.res_role_honoka);
                        tvTitle.setText("高坂穗乃果");
                        tvProvider.setText("内建 | 提供：佚名");
                        break;
                    case 1:
                        //小鸟
                        sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                        sharedPreferencesEditor.putInt("pref_role_builtin", 1).commit();
                        ivRole.setImageResource(R.drawable.res_role_kotori);
                        tvTitle.setText("南小鸟");
                        tvProvider.setText("内建 | 提供：佚名");
                        break;
                    case 2:
                        //海爷
                        sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                        sharedPreferencesEditor.putInt("pref_role_builtin", 2).commit();
                        ivRole.setImageResource(R.drawable.res_role_umi);
                        tvTitle.setText("园田海未");
                        tvProvider.setText("内建 | 提供：佚名");
                        break;
                    case 3:
                        //kke
                        sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                        sharedPreferencesEditor.putInt("pref_role_builtin", 3).commit();
                        ivRole.setImageResource(R.drawable.res_role_kke);
                        tvTitle.setText("绚濑绘里");
                        tvProvider.setText("内建 | 提供：佚名");
                        break;
                    case 4:
                        //东条希
                        sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                        sharedPreferencesEditor.putInt("pref_role_builtin", 4).commit();
                        ivRole.setImageResource(R.drawable.res_role_nozomi);
                        tvTitle.setText("东条希");
                        tvProvider.setText("内建 | 提供：佚名");
                        break;
                    case 5:
                        //maki
                        sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                        sharedPreferencesEditor.putInt("pref_role_builtin", 5).commit();
                        ivRole.setImageResource(R.drawable.res_role_maki);
                        tvTitle.setText("西木野真姬");
                        tvProvider.setText("内建 | 提供：佚名");
                        break;
                    case 6:
                        //rin
                        sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                        sharedPreferencesEditor.putInt("pref_role_builtin", 6).commit();
                        ivRole.setImageResource(R.drawable.res_role_rin);
                        tvTitle.setText("星空凛");
                        tvProvider.setText("内建 | 提供：佚名");
                        break;
                    case 7:
                        //hanayo
                        sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                        sharedPreferencesEditor.putInt("pref_role_builtin", 7).commit();
                        ivRole.setImageResource(R.drawable.res_role_hanayo);
                        tvTitle.setText("小泉花阳");
                        tvProvider.setText("内建 | 提供：佚名");
                        break;
                    case 8:
                        //nico
                        sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                        sharedPreferencesEditor.putInt("pref_role_builtin", 8).commit();
                        ivRole.setImageResource(R.drawable.res_role_nico);
                        tvTitle.setText("矢泽妮可");
                        tvProvider.setText("内建 | 提供：佚名");
                        break;
                    case 9:
                        //雨桐
                        sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                        sharedPreferencesEditor.putInt("pref_role_builtin", 9).commit();
                        ivRole.setImageResource(R.drawable.res_role_yutong);
                        tvTitle.setText("雨桐");
                        tvProvider.setText("内建 | 提供：佚名");
                        break;
                }
            }
        }).show();
    }

    private void roleChoicePackage() {
        try {
            final List<Role> roleList = new ArrayList<Role>();
            RoleDataBaseHelper roleDataBaseHelper = new RoleDataBaseHelper(this);
            Cursor cursor = roleDataBaseHelper.query();
            while (cursor.moveToNext()) {
                int Id = cursor.getInt(cursor.getColumnIndex("_id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String path = cursor.getString(cursor.getColumnIndex("path"));

                Role role = new Role();
                role.setId(Id);
                role.setTitle(title);
                role.setPath(path);

                roleList.add(role);
            }
            roleDataBaseHelper.close();
            if (roleList.isEmpty()) {
                Toast.makeText(this, "没有安装立绘包", Toast.LENGTH_SHORT).show();
            } else {
                String[] roleArray = new String[roleList.size()];
                for (int i = 0; i < roleList.size(); i++) {
                    Role role = roleList.get(i);
                    roleArray[i] = role.getTitle();
                }
                new AlertDialog.Builder(PreferenceRoleSwitch.this).setItems(roleArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Role role = roleList.get(i);
                        rolePackChoser(role.getPath());
                    }
                }).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "没有安装立绘包", Toast.LENGTH_SHORT).show();
        }
    }

    private void rolePackChoser(String configPath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(configPath + "config.json");
            int length = fileInputStream.available();
            byte[] buffer = new byte[length];
            fileInputStream.read(buffer);
            String text = EncodingUtils.getString(buffer, "UTF-8");
            fileInputStream.close();
            JSONObject jsonObject = new JSONObject(text);
            if (jsonObject.getInt("packlevel") > 0) {
                rolePackChooserModern(jsonObject, configPath);
                return;
            }
            final String title = jsonObject.getString("title");
            final String author = jsonObject.getString("author");
            String rolePath;
            String roleName;
            JSONArray jsonArray = jsonObject.getJSONArray("roles");
            final String[] roleNameArray = new String[jsonArray.length()];
            final String[] rolePathArray = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonArrayObject = jsonArray.getJSONObject(i);
                roleName = jsonArrayObject.getString("name");
                roleNameArray[i] = roleName;
                rolePath = jsonArrayObject.getString("path");
                rolePathArray[i] = configPath + rolePath;
            }
            new AlertDialog.Builder(PreferenceRoleSwitch.this).setItems(roleNameArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Boolean useBuiltIn = false;
                    String rolePath1 = rolePathArray[i];
                    String roleTitle = roleNameArray[i];
                    String roleMark = title + " | 立绘包 | 提供: " + author;
                    ivRole.setImageBitmap(BitmapFactory.decodeFile(rolePath1));
                    tvTitle.setText(roleTitle);
                    tvProvider.setText(roleMark);
                    sharedPreferencesEditor.putBoolean("pref_use_builtin", useBuiltIn).commit();
                    sharedPreferencesEditor.putString("pref_role_package_path", rolePath1).commit();
                    sharedPreferencesEditor.putString("pref_role_package_title", roleTitle).commit();
                    sharedPreferencesEditor.putString("pref_role_package_mark", roleMark).commit();
                    Snackbar.with(PreferenceRoleSwitch.this).text("设置成功").show(PreferenceRoleSwitch.this);
                }
            }).show();
        } catch (IOException IOE) {
            IOE.printStackTrace();
        } catch (JSONException JSONE) {
            JSONE.printStackTrace();
        }
    }

    private void rolePackChooserModern(JSONObject jsonObject, String confPath) {
        try {
            final String title = jsonObject.getString("title");
            final String author = jsonObject.getString("provider");
            String rolePath;
            String roleName;
            JSONArray jsonArray = jsonObject.getJSONArray("include");
            final String[] roleNameArray = new String[jsonArray.length()];
            final String[] rolePathArray = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonArrayObject = jsonArray.getJSONObject(i);
                roleName = jsonArrayObject.getString("head");
                roleNameArray[i] = roleName;
                rolePath = jsonArrayObject.getString("way");
                rolePathArray[i] = confPath + rolePath;
            }
            new AlertDialog.Builder(PreferenceRoleSwitch.this).setItems(roleNameArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Boolean useBuiltIn = false;
                    String rolePath1 = rolePathArray[i];
                    String roleTitle = roleNameArray[i];
                    String roleMark = title + " | 立绘包 | 提供: " + author;
                    ivRole.setImageBitmap(BitmapFactory.decodeFile(rolePath1));
                    tvTitle.setText(roleTitle);
                    tvProvider.setText(roleMark);
                    sharedPreferencesEditor.putBoolean("pref_use_builtin", useBuiltIn).commit();
                    sharedPreferencesEditor.putString("pref_role_package_path", rolePath1).commit();
                    sharedPreferencesEditor.putString("pref_role_package_title", roleTitle).commit();
                    sharedPreferencesEditor.putString("pref_role_package_mark", roleMark).commit();
                    Snackbar.with(PreferenceRoleSwitch.this).text("设置成功").show(PreferenceRoleSwitch.this);
                }
            }).show();
        } catch (JSONException JSONE) {
            JSONE.printStackTrace();
        }
    }

    private void roleChoiceGallery() {
        int REQUEST_CODE;
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        REQUEST_CODE = CHOOSE_PICTURE;
        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(openAlbumIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_PICTURE:
                    boolean isError = false;
                    ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        FileOutputStream out = openFileOutput("main.png", MODE_PRIVATE);
                        photo.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(PreferenceRoleSwitch.this, "找不到自定义立绘文件位置", Toast.LENGTH_LONG);
                        isError = true;
                    } catch (Exception e) {
                        Toast.makeText(PreferenceRoleSwitch.this, "产生意外 >>\n" + e.toString(), Toast.LENGTH_LONG);
                        isError = true;
                    }
                    if (!isError) {
                        try {
                            FileInputStream fis = null;
                            fis = openFileInput("main.png");
                            Bitmap bit = BitmapFactory.decodeStream(fis);
                            ivRole.setImageBitmap(bit);
                            fis.close();
                            tvTitle.setText("图库自选立绘");
                            tvProvider.setText("图库 | 提供：无");
                            sharedPreferencesEditor.putBoolean("pref_use_builtin", true).commit();
                            sharedPreferencesEditor.putInt("pref_role_builtin", 10).commit();
                        } catch (Exception e) {
                            Toast.makeText(PreferenceRoleSwitch.this, "产生意外 >>\n" + e.toString(), Toast.LENGTH_LONG);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (needRestart) {
                startActivity(new Intent(PreferenceRoleSwitch.this, Launcher.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (needRestart) {
                startActivity(new Intent(PreferenceRoleSwitch.this, Launcher.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            } else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
