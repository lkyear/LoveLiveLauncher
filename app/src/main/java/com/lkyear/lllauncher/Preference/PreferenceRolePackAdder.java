package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.lkyear.lllauncher.Launcher.Level;
import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.FileUtils;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by lkyear on 17/5/30.
 */

public class PreferenceRolePackAdder extends PreferenceBaseActivity {

    private static final int REQUEST_FILE = 1;
    private static final int PACK_LEVEL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_role_packdel_layout);
        setTitle("安装立绘包");
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
                InitFileChoser();
                /*Intent intent = getIntent();
                if (intent.ACTION_VIEW.equals(intent.getAction())) {
                    Uri uri = (Uri) intent.getData();
                    installRolePack(uri);
                } else {
                    InitFileChoser();
                }*/
            } else {
                if (roleList.size() == 20) {
                    Toast.makeText(this, "安装失败，因为最大安装数为20", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    InitFileChoser();
                    /*Intent intent = getIntent();
                    if (intent.ACTION_VIEW.equals(intent.getAction())) {
                        Uri uri = (Uri) intent.getData();
                        installRolePack(uri);
                    } else {
                        InitFileChoser();
                    }*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "安装失败，因为 " + e.toString(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void InitFileChoser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri selectContent = data.getData();
            if (requestCode == REQUEST_FILE) {
                installRolePack(selectContent);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void installRolePack(Uri pathUri) {
        try {
            final String zipFilePath = FileUtils.getPath(PreferenceRolePackAdder.this, pathUri);
            if (zipFilePath.endsWith(".blr") | zipFilePath.endsWith(".BLR")) {
                Unzip(zipFilePath, getFilesDir().getPath() + "/Temp/");
                FileInputStream fileInputStream = new FileInputStream(getFilesDir().getPath() + "/Temp/config.json");
                int length = fileInputStream.available();
                byte[] buffer = new byte[length];
                fileInputStream.read(buffer);
                String text = EncodingUtils.getString(buffer, "UTF-8");
                fileInputStream.close();
                JSONObject jsonObject = new JSONObject(text);
                int packlevel = jsonObject.getInt("packlevel");
                if (packlevel == 0) {
                    installLevel0(jsonObject, packlevel, zipFilePath);
                } else if (packlevel > PACK_LEVEL) {
                    Toast.makeText(PreferenceRolePackAdder.this, "此立绘包的等级为" + packlevel + "，需要" + PACK_LEVEL + "。", Toast.LENGTH_LONG).show();
                } else {
                    installLevelCurrent(jsonObject, packlevel, zipFilePath);
                }

            } else {
                Toast.makeText(this, "安装失败，因为此文件无效。", Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
            Toast.makeText(this, "安装失败，因为未知错误。\n" + e.toString(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void installLevel0(JSONObject jsonObject, int packlevel, final String zipFilePath) {
        try {
            Boolean isValidate = true;
            String failReason = "配置文件非法。";
            final String packid = jsonObject.getString("packid");
            final String title = jsonObject.getString("title");
            final String author = jsonObject.getString("author");
            JSONArray jsonArray = jsonObject.getJSONArray("roles");
            final int packLength = jsonArray.length();
            if (packid.matches("^[a-zA-Z][a-zA-Z_]{4,}$") && packid.length() >= 5 && packid.length() <= 30) {
                if (packlevel <= PACK_LEVEL) {
                    if (title.matches("^[A-Za-z0-9\\u4e00-\\u9fa5-_]+$") && title.length() >= 1 && title.length() <= 20) {
                        if (author.matches("^[A-Za-z0-9\\u4e00-\\u9fa5-_\\s]+$") && author.length() >= 1 && author.length() <= 20) {
                            if (jsonArray.length() < 14) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonArrayObject = jsonArray.getJSONObject(i);
                                    String roleName = jsonArrayObject.getString("name");
                                    String rolePath = jsonArrayObject.getString("path");
                                    if (roleName.matches("^[A-Za-z0-9\\u4e00-\\u9fa5-_]+$") && roleName.length() >= 1 && roleName.length() <= 10) {
                                        if (rolePath.matches("^[a-zA-Z][a-zA-Z-_/]+\\.(webp)$") && rolePath.length() >= 6 && rolePath.length() <= 30) {
                                            File file = new File(getFilesDir().getPath() + "/Temp/" + jsonArrayObject.getString("path"));
                                            if (file.exists()) {
                                                if (!isValidate) {
                                                    isValidate = true;
                                                }
                                            } else {
                                                isValidate = false;
                                                failReason = "文件不存在。";
                                                break;
                                            }
                                        } else {
                                            isValidate = false;
                                            failReason = "文件名非法。";
                                        }
                                    } else {
                                        isValidate = false;
                                        failReason = "立绘名称非法。";
                                    }
                                }
                            } else {
                                isValidate = false;
                                failReason = "长度超过约束";
                            }
                        } else {
                            isValidate = false;
                            failReason = "提供者名称非法。";
                        }
                    } else {
                        isValidate = false;
                        failReason = "标题非法。";
                    }
                } else {
                    isValidate = false;
                    failReason = "此立绘包的等级为" + packlevel + "，需要" + PACK_LEVEL + "。";
                }
            } else {
                isValidate = false;
                failReason = "ID非法。";
            }
            if (isValidate) {
                final int roleNumber = packLength;
                new AlertDialog.Builder(PreferenceRolePackAdder.this).setTitle("是否安装此立绘包?")
                        .setMessage(title + "\n" + "提供：" + author + "\n包含 " + packLength + " 个立绘")
                        .setPositiveButton("好", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String fileSavePath = getFilesDir().getPath() + "/Roles/" + packid + "/";
                                ContentValues values = new ContentValues();
                                values.put("title", title);
                                values.put("path", fileSavePath);
                                RoleDataBaseHelper roleDataBaseHelper = new RoleDataBaseHelper(PreferenceRolePackAdder.this);
                                if (roleDataBaseHelper.insert(values)) {
                                    roleDataBaseHelper.close();
                                    Unzip(zipFilePath, fileSavePath);
                                    RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                                    Toast.makeText(PreferenceRolePackAdder.this, "安装成功\n" + title, Toast.LENGTH_LONG).show();
                                    Level userLevel = new Level(PreferenceRolePackAdder.this);
                                    if (roleNumber > 1 & roleNumber < 6) {
                                        userLevel.setAchievement(Level.ACHIEVEMENT_SHURA_FIELD, true);
                                    } else if (roleNumber > 5 & roleNumber < 9) {
                                        userLevel.setAchievement(Level.ACHIEVEMENT_SUPERNATURAL, true);
                                    } else if (roleNumber > 8) {
                                        userLevel.setAchievement(Level.ACHIEVEMENT_TOBE_IDOL, true);
                                    }
                                    finish();
                                } else {
                                    roleDataBaseHelper.close();
                                    RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                                    Toast.makeText(PreferenceRolePackAdder.this, "安装失败，因为重复约束。", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        }).setNegativeButton("不", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            } else {
                RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                Toast.makeText(this, "安装失败，因为" + failReason, Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception e) {
            finish();
        }
    }

    private void installLevelCurrent(JSONObject jsonObject, int packlevel, final String zipFilePath) {
        try {
            Boolean isValidate = true;
            String failReason = "配置文件非法。";
            final String packid = jsonObject.getString("bundle");
            final int ver = jsonObject.getInt("ver");
            final String vname = jsonObject.getString("vname");
            final String desc = jsonObject.getString("desc");
            final String title = jsonObject.getString("title");
            final String author = jsonObject.getString("provider");
            JSONArray jsonArray = jsonObject.getJSONArray("include");
            final int packLength = jsonArray.length();
            if (packid.matches("^[a-zA-Z][a-zA-Z-]{4,29}$") && packid.split("-").length == 3) {
                if (ver >= 0) {
                    if (vname.matches("^[A-Za-z0-9-_.]+$") && vname.length() > 0 && vname.length() <= 20) {
                        if (packlevel <= PACK_LEVEL) {
                            if (title.matches("^[A-Za-z0-9\\u4e00-\\u9fa5-_]+$") && title.length() >= 1 && title.length() <= 20) {
                                if (author.matches("^[A-Za-z0-9\\u4e00-\\u9fa5-_\\s]+$") && author.length() >= 1 && author.length() <= 20) {
                                    if (jsonArray.length() < 21) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonArrayObject = jsonArray.getJSONObject(i);
                                            String roleName = jsonArrayObject.getString("head");
                                            String rolePath = jsonArrayObject.getString("way");
                                            if (roleName.matches("^[A-Za-z0-9\\u4e00-\\u9fa5-_]+$") && roleName.length() >= 1 && roleName.length() <= 10) {
                                                if (rolePath.matches("^[a-zA-Z][a-zA-Z-_/]+\\.(webp)$") && rolePath.length() >= 6 && rolePath.length() <= 30) {
                                                    File file = new File(getFilesDir().getPath() + "/Temp/" + jsonArrayObject.getString("way"));
                                                    if (file.exists()) {
                                                        if (!isValidate) {
                                                            isValidate = true;
                                                        }
                                                    } else {
                                                        isValidate = false;
                                                        failReason = "文件不存在。";
                                                        break;
                                                    }
                                                } else {
                                                    isValidate = false;
                                                    failReason = "文件名非法。";
                                                }
                                            } else {
                                                isValidate = false;
                                                failReason = "立绘名称非法。";
                                            }
                                        }
                                    } else {
                                        isValidate = false;
                                        failReason = "长度超过约束";
                                    }
                                } else {
                                    isValidate = false;
                                    failReason = "提供者名称非法。";
                                }
                            } else {
                                isValidate = false;
                                failReason = "标题非法。";
                            }
                        } else {
                            isValidate = false;
                            failReason = "此立绘包的等级为" + packlevel + "，需要" + PACK_LEVEL + "。";
                        }
                    } else {
                        isValidate = false;
                        failReason = "版本名称非法";
                    }
                } else {
                    isValidate = false;
                    failReason = "版本号非法。";
                }
            } else {
                isValidate = false;
                failReason = "ID非法。";
            }
            if (isValidate) {
                final int roleNumber = packLength;
                new AlertDialog.Builder(PreferenceRolePackAdder.this).setTitle("是否安装此立绘包?")
                        .setMessage(title + "\n" + "提供：" + author + "\n数量：" + packLength + "\n版本：" + vname + "\n\n说明：" + desc)
                        .setPositiveButton("好", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String fileSavePath = getFilesDir().getPath() + "/Roles/" + packid + "/";
                                ContentValues values = new ContentValues();
                                values.put("title", title);
                                values.put("path", fileSavePath);
                                RoleDataBaseHelper roleDataBaseHelper = new RoleDataBaseHelper(PreferenceRolePackAdder.this);
                                final List<Role> roleList = new ArrayList<Role>();
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
                                if (roleList.isEmpty()) {
                                    if (roleDataBaseHelper.insert(values)) {
                                        roleDataBaseHelper.close();
                                        Unzip(zipFilePath, fileSavePath);
                                        RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                                        Toast.makeText(PreferenceRolePackAdder.this, "安装成功\n" + title, Toast.LENGTH_LONG).show();
                                        Level userLevel = new Level(PreferenceRolePackAdder.this);
                                        if (roleNumber > 1 & roleNumber < 6) {
                                            userLevel.setAchievement(Level.ACHIEVEMENT_SHURA_FIELD, true);
                                        } else if (roleNumber > 5 & roleNumber < 9) {
                                            userLevel.setAchievement(Level.ACHIEVEMENT_SUPERNATURAL, true);
                                        } else if (roleNumber > 8) {
                                            userLevel.setAchievement(Level.ACHIEVEMENT_TOBE_IDOL, true);
                                        }
                                        finish();
                                    } else {
                                        roleDataBaseHelper.close();
                                        RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                                        Toast.makeText(PreferenceRolePackAdder.this, "安装失败。", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                } else {
                                    for (int ia = 0; ia < roleList.size(); ia++) {
                                        Role role = roleList.get(ia);
                                        if (fileSavePath.equals(role.getPath())) {
                                            roleDataBaseHelper.close();
                                            try {
                                                FileInputStream fileInputStream = new FileInputStream(role.getPath() + "config.json");
                                                int length = fileInputStream.available();
                                                byte[] buffer = new byte[length];
                                                fileInputStream.read(buffer);
                                                String text = EncodingUtils.getString(buffer, "UTF-8");
                                                fileInputStream.close();
                                                JSONObject jsonObject = new JSONObject(text);
                                                int verInstalled = jsonObject.getInt("ver");
                                                if (ver >= verInstalled) {
                                                    RecursionDeleteFile(new File(getFilesDir().getPath() + "/Roles/" + packid + "/"));
                                                    Unzip(zipFilePath, fileSavePath);
                                                    RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                                                    Toast.makeText(PreferenceRolePackAdder.this, "更新成功\n" + title, Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(PreferenceRolePackAdder.this, "安装失败，已安装此立绘包的更高版本。", Toast.LENGTH_LONG).show();
                                                }
                                                finish();
                                                return;
                                            } catch (Exception e) {
                                                Toast.makeText(PreferenceRolePackAdder.this, "更新失败。", Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                                finish();
                                                return;
                                            }
                                        }
                                    }
                                    if (roleDataBaseHelper.insert(values)) {
                                        roleDataBaseHelper.close();
                                        Unzip(zipFilePath, fileSavePath);
                                        RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                                        Toast.makeText(PreferenceRolePackAdder.this, "安装成功\n" + title, Toast.LENGTH_LONG).show();
                                        Level userLevel = new Level(PreferenceRolePackAdder.this);
                                        if (roleNumber > 1 & roleNumber < 6) {
                                            userLevel.setAchievement(Level.ACHIEVEMENT_SHURA_FIELD, true);
                                        } else if (roleNumber > 5 & roleNumber < 9) {
                                            userLevel.setAchievement(Level.ACHIEVEMENT_SUPERNATURAL, true);
                                        } else if (roleNumber > 8) {
                                            userLevel.setAchievement(Level.ACHIEVEMENT_TOBE_IDOL, true);
                                        }
                                        finish();
                                    } else {
                                        roleDataBaseHelper.close();
                                        RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                                        Toast.makeText(PreferenceRolePackAdder.this, "安装失败。", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }
                            }
                        }).setNegativeButton("不", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            } else {
                RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                Toast.makeText(this, "安装失败，因为" + failReason, Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "安装失败，因为" + e.toString(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private static void Unzip(String zipFile, String targetDir) {
        int BUFFER = 4096; //这里缓冲区我们使用4KB，
        String strEntry; //保存每个zip的条目名称

        try {
            BufferedOutputStream dest = null; //缓冲输出流
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry; //每个zip条目的实例

            while ((entry = zis.getNextEntry()) != null) {

                try {
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();

                    File entryFile = new File(targetDir + strEntry);
                    File entryDir = new File(entryFile.getParent());
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }

                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zis.close();
        } catch (Exception cwj) {
            cwj.printStackTrace();
        }
    }

    private void RecursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

}
