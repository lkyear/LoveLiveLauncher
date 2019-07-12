package com.lkyear.lllauncher.Preference;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lkyear.lllauncher.Launcher.Level;
import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.FileUtils;
import com.lkyear.lllauncher.Util.Utils;

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
 * Created by lkyear on 2018/1/26.
 *
 * 立绘包的安装部分，我已经看不下去了...有能man请优化优化吧
 */

public class PreferenceRolePackInstall extends Activity {

    private static final int REQUEST_FILE = 1;
    private static final int PACK_LEVEL = 1;

    private TextView mainTitle, subTitle, despContent;
    private Button buttonAccept, buttonCancel;

    private LinearLayout headLayout;

    private ObjectAnimator objectAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.isMarshmallow()) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.launcher_role_packins_layout);
        setTitle("安装立绘包");

        //连接各个控件
        mainTitle = (TextView) findViewById(R.id.launcher_pref_role_install_title);
        subTitle = (TextView) findViewById(R.id.launcher_pref_role_install_subtitle);
        despContent = (TextView) findViewById(R.id.launcher_pref_role_install_desp);
        buttonAccept = (Button) findViewById(R.id.launcher_pref_role_install_accept);
        buttonCancel = (Button) findViewById(R.id.launcher_pref_role_install_cancel);
        headLayout = (LinearLayout) findViewById(R.id.launcher_role_install_ly);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //初始化方法
        Init();
    }

    private void Init() {
        //检查是否符合安装条件，如果符合则弹出文件选择其
        try {
            final List<Role> roleList = new ArrayList<Role>();
            RoleDataBaseHelper roleDataBaseHelper = new RoleDataBaseHelper(this);
            Cursor cursor = roleDataBaseHelper.query();
            while (cursor.moveToNext()) {
                int Id = cursor.getInt(cursor.getColumnIndex("_id"));
                Role role = new Role();
                role.setId(Id);
                roleList.add(role);
            }
            roleDataBaseHelper.close();
            if (roleList.isEmpty()) {
                InitFileChoser();
            } else {
                if (roleList.size() == 20) {
                    Toast.makeText(this, "安装失败，因为最大安装数为20", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    InitFileChoser();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "安装失败，因为 " + e.toString(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 弹出文件选择器方法
     */
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
                new analyzePack().execute(selectContent);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private class analyzePack extends AsyncTask<Uri, String, String> {

        private JSONObject jsonObject;
        private int packlevel;
        private String zipFilePath;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mainTitle.setText("立绘包解析中...");
            subTitle.setText("");
            despContent.setText("");
            buttonAccept.setEnabled(false);
            buttonCancel.setEnabled(false);
        }

        @Override
        protected String doInBackground(Uri... pathUri) {
            try {
                Thread.sleep(600);
                zipFilePath = FileUtils.getPath(PreferenceRolePackInstall.this, pathUri[0]);
                if (zipFilePath.endsWith(".blr") | zipFilePath.endsWith(".BLR")) {
                    publishProgress("正在解包...");
                    Unzip(zipFilePath, getFilesDir().getPath() + "/Temp/");
                    Thread.sleep(1000);
                    publishProgress("\n正在解析立绘包...");
                    FileInputStream fileInputStream = new FileInputStream(getFilesDir().getPath() + "/Temp/config.json");
                    int length = fileInputStream.available();
                    byte[] buffer = new byte[length];
                    fileInputStream.read(buffer);
                    String text = EncodingUtils.getString(buffer, "UTF-8");
                    fileInputStream.close();
                    Thread.sleep(400);
                    jsonObject = new JSONObject(text);
                    packlevel = jsonObject.getInt("packlevel");
                    if (packlevel == 0) {
                        return "安装被中断，因为立绘包版本过旧。";
                    } else if (packlevel > PACK_LEVEL) {
                        return "安装被中断，因为此立绘包的等级为" + packlevel + "，需要" + PACK_LEVEL + "。";
                    } else {
                        return "年糕!";
                    }
                } else {
                    return "解析失败，此文件已损坏或不是立绘包。";
                }
            } catch (Exception e) {
                e.printStackTrace();
                RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                return "解析失败，请确保立绘包在设备内置存储里而不是SD卡。\n" + e.toString();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            despContent.append(values[0]);
        }

        @Override
        protected void onPostExecute(String vals) {
            super.onPostExecute(vals);
            buttonAccept.setEnabled(true);
            buttonCancel.setEnabled(true);
            if (vals.startsWith("解析失败") || vals.startsWith("安装被中断")) {
                despContent.setText(vals);
                mainTitle.setText("安装取消");
                subTitle.setText("详情见下方介绍区域内");
                buttonAccept.setText("完成");
                rotateyAnimRun(headLayout, 0xff607d8b, 0xffef5350);
                buttonAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            } else {
                installLevelCurrent(jsonObject, packlevel, zipFilePath);
            }
        }
    }

    private void installLevelCurrent(JSONObject jsonObject, int packlevel, final String zipFilePath) {
        try {
            Boolean isValidate = true;
            String failReason = "配置文件非法。";

            //定义各个需要判断的变量
            final String packid = jsonObject.getString("bundle");
            final int ver = jsonObject.getInt("ver");
            final String vname = jsonObject.getString("vname");
            final String desc = jsonObject.getString("desc");
            final String title = jsonObject.getString("title");
            final String author = jsonObject.getString("provider");
            JSONArray jsonArray = jsonObject.getJSONArray("include");
            final int packLength = jsonArray.length();

            //判断ID是否合法
            if (!(packid.matches("^[a-zA-Z][a-zA-Z-]{4,29}$")
                    && packid.split("-").length == 3)) {
                isValidate = false;
                failReason = "ID非法。";
            }

            //判断版本号是否合法
            if (ver < 0) {
                isValidate = false;
                failReason = "版本号非法。";
            }

            //判断版本名称是否合法
            if (!(vname.matches("^[A-Za-z0-9-_.]+$") &&
                    vname.length() > 0 && vname.length() <= 20)) {
                isValidate = false;
                failReason = "版本名称非法";
            }

            //判断是否为高等级的立绘包
            if (packlevel > PACK_LEVEL) {
                isValidate = false;
                failReason = "此立绘包的等级为" + packlevel + "，需要" + PACK_LEVEL + "。";
            }

            //判断标题是否合法
            if (!(title.matches("^[A-Za-z0-9\\u4e00-\\u9fa5-_]+$") &&
                    title.length() >= 1 && title.length() <= 20)) {
                isValidate = false;
                failReason = "标题非法。";
            }

            //判断提供者名称是否合法
            if (!(author.matches("^[A-Za-z0-9\\u4e00-\\u9fa5-_\\s]+$") &&
                    author.length() >= 1 && author.length() <= 20)) {
                isValidate = false;
                failReason = "提供者名称非法。";
            }

            //判断立绘包所包含立绘的长度是否合法
            if (jsonArray.length() < 21) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonArrayObject = jsonArray.getJSONObject(i);
                    String roleName = jsonArrayObject.getString("head");
                    String rolePath = jsonArrayObject.getString("way");
                    if (!(roleName.matches("^[A-Za-z0-9\\u4e00-\\u9fa5-_]+$") &&
                            roleName.length() >= 1 && roleName.length() <= 10)) {
                        isValidate = false;
                        failReason = "立绘名称非法。";
                    }
                    if (rolePath.matches("^[a-zA-Z][a-zA-Z-_/]+\\.(webp)$")
                            && rolePath.length() >= 6 && rolePath.length() <= 30) {
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
                }
            } else {
                isValidate = false;
                failReason = "长度超过约束";
            }

            if (isValidate) {
                final int roleNumber = packLength;
                mainTitle.setText(title);
                subTitle.setText("版本" + vname + " | " + packLength + "个立绘" + " | 由" + author + "提供");
                despContent.setText(desc);
                buttonAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new installPack().execute(packid, title, zipFilePath, ver + "", roleNumber +"");
                    }
                });
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

    private class installPack extends AsyncTask<String, String, String> {

        private String packid, title, zipFilePath;
        private int ver, roleNumber;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mainTitle.setText("安装中...");
            subTitle.setText("");
            despContent.setText("");
            buttonAccept.setEnabled(false);
            buttonCancel.setEnabled(false);
            rotateyAnimRun(headLayout, 0xff607d8b, 0xff448aff);
        }

        @Override
        protected String doInBackground(String... strings) {
            //设置必须的变量值
            packid = strings[0];
            title = strings[1];
            zipFilePath = strings[2];
            ver = Integer.parseInt(strings[3]);
            roleNumber = Integer.parseInt(strings[4]);
            publishProgress("正在读取属性...");

            /**
             * 延迟几秒
             */
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //开始安装立绘包
            publishProgress("\n正在展开立绘包...");
            String fileSavePath = getFilesDir().getPath() + "/Roles/" + packid + "/";
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("path", fileSavePath);
            RoleDataBaseHelper roleDataBaseHelper = new RoleDataBaseHelper(PreferenceRolePackInstall.this);
            final List<Role> roleList = new ArrayList<Role>();
            Cursor cursor = roleDataBaseHelper.query();
            publishProgress("\n正在写入数据...");
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    Level userLevel = new Level(PreferenceRolePackInstall.this);
                    if (roleNumber > 1 & roleNumber < 6) {
                        userLevel.setAchievement(Level.ACHIEVEMENT_SHURA_FIELD, true);
                    } else if (roleNumber > 5 & roleNumber < 9) {
                        userLevel.setAchievement(Level.ACHIEVEMENT_SUPERNATURAL, true);
                    } else if (roleNumber > 8) {
                        userLevel.setAchievement(Level.ACHIEVEMENT_TOBE_IDOL, true);
                    }
                    cleanUpdate();
                    return "安装成功已成功安装立绘包 " + title;
                } else {
                    roleDataBaseHelper.close();
                    RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                    cleanUpdate();
                    return "安装过程因为无法插入数据而被中断，请检查立绘包的完整性。\n\n错误类型：Type ANF\n错误信息：无 -> 插入 -> 假。";
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
                                cleanUpdate();
                                return "安装成功已成功更新立绘包 " + title;
                            } else {
                                return "已安装此立绘包的更高版本，不可以降级立绘包。\n\n错误类型：Type 352";
                            }
                        } catch (Exception e) {
                            return "安装更新的过程因为一个未知的错误而被中断，请向开发者反馈下面的错误：\n" + e.toString() + "\n\n错误类型：Type AUF";
                        }
                    }
                }
                if (roleDataBaseHelper.insert(values)) {
                    roleDataBaseHelper.close();
                    Unzip(zipFilePath, fileSavePath);
                    RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                    Level userLevel = new Level(PreferenceRolePackInstall.this);
                    if (roleNumber > 1 & roleNumber < 6) {
                        userLevel.setAchievement(Level.ACHIEVEMENT_SHURA_FIELD, true);
                    } else if (roleNumber > 5 & roleNumber < 9) {
                        userLevel.setAchievement(Level.ACHIEVEMENT_SUPERNATURAL, true);
                    } else if (roleNumber > 8) {
                        userLevel.setAchievement(Level.ACHIEVEMENT_TOBE_IDOL, true);
                    }
                    cleanUpdate();
                    return "安装成功已成功安装立绘包 " + title;
                } else {
                    roleDataBaseHelper.close();
                    RecursionDeleteFile(new File(getFilesDir().getPath() + "/Temp/"));
                    cleanUpdate();
                    return "安装过程因为无法插入数据而被中断，请检查立绘包的完整性。\n\n错误类型：AEF\n错误信息：无 !-> 插入 -> 假。";
                }
            }
        }

        private void cleanUpdate() {
            publishProgress("\n正在清理临时文件...");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            despContent.append(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            buttonAccept.setEnabled(true);
            buttonCancel.setEnabled(true);
            buttonAccept.setText("完成");
            if (s.startsWith("安装成功")) {
                mainTitle.setText("安装成功");
                subTitle.setText("详情见下方介绍区域内");
                despContent.setText(s.substring(4, s.length()));
                rotateyAnimRun(headLayout, 0xff448aff, 0xff43a047);
            } else {
                mainTitle.setText("安装失败");
                subTitle.setText("详情见下方介绍区域内");
                despContent.setText(s);
                rotateyAnimRun(headLayout, 0xff448aff, 0xffef5350);
            }
            buttonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    private void rotateyAnimRun(View view, int lastColor, int nextColor) {
        objectAnimator = ObjectAnimator.ofInt(view,"backgroundColor", lastColor, nextColor);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setEvaluator(new ArgbEvaluator());
        objectAnimator.setDuration(600);
        objectAnimator.start();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
