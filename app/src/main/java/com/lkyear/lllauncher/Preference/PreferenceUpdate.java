package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lkyear.lllauncher.R;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by lkyear on 2017/7/14.
 */

public class PreferenceUpdate extends PreferenceBaseActivity {

    private TextView tvLocalVersion, tvServerVersion, tvUpdateInfo, tvCheckUpdate;

    private int connect;
    private String version, updateInfo, size, link, pwd;
    private int code, sdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setElevation(0);
        setContentView(R.layout.launcher_preference_update);
        setTitle("检查更新");
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskWrites().detectDiskReads().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        tvLocalVersion = (TextView) findViewById(R.id.launcher_pref_update_local_version);
        tvServerVersion = (TextView) findViewById(R.id.launcher_pref_update_server_version);
        tvUpdateInfo = (TextView) findViewById(R.id.launcher_pref_update_info);
        tvCheckUpdate = (TextView) findViewById(R.id.launcher_pref_update_check);
        tvLocalVersion.setText("本地版本：" + getVersionName());
        tvCheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CheckUpdateTask().execute();
            }
        });
        Intent reslove = getIntent();
        if (reslove.getBooleanExtra("from_background", false)) {
            connect = 1;
            version = reslove.getStringExtra("version");
            updateInfo = reslove.getStringExtra("updateInfo");
            size = reslove.getStringExtra("size");
            link = reslove.getStringExtra("link");
            pwd = reslove.getStringExtra("pwd");
            code = reslove.getIntExtra("code", 0);
            sdk = reslove.getIntExtra("sdk", 0);
            setUpdate();
        }
    }

    private class CheckUpdateTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            tvUpdateInfo.setText("查询中...");
            tvCheckUpdate.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... url) {
            try {
                Thread.sleep(1500);
            } catch (Exception e) { }
            String myString = "";
            String updateText = "";
            try {
                URL uri = new URL("https://lkyear.github.io/lllw/launcher/update.html");
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[] {new LauncherTrustManager()} , new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new LauncherHostnameVerifier());
                HttpsURLConnection conn = (HttpsURLConnection) uri.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayBuffer baf = new ByteArrayBuffer(100);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }
                myString = new String(baf.toByteArray(), "UTF-8");
                bis.close();
                JSONObject UpdateJSON;
                UpdateJSON = (new JSONObject(myString).getJSONObject("LauncherUpdate"));
                code = UpdateJSON.getInt("code");
                updateText = UpdateJSON.getString("info").replaceAll("<#NL>", "\n");
                updateInfo = updateText;
                size = UpdateJSON.getString("size");
                sdk = UpdateJSON.getInt("sdk");
                link = UpdateJSON.getString("link");
                pwd = UpdateJSON.getString("password");
                version = UpdateJSON.getString("name");
                connect = 1;
            } catch (Exception e) {
                connect = 0;
                e.printStackTrace();
            }
            return myString;
        }

        protected void onPostExecute(String myString) {
            if (connect == 0) {
                tvUpdateInfo.setText("无法连接至更新服务器，请检查您的网络连接。");
            } else if (connect == 1) {
                setUpdate();
            }
        }

        private class LauncherHostnameVerifier implements HostnameVerifier {

            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }

        private class LauncherTrustManager implements X509TrustManager {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }

    }

    private void setUpdate() {
        if (getVersionCode() == 0) {
            tvUpdateInfo.setText("内部错误。");
            tvCheckUpdate.setEnabled(false);
        } else if (getVersionCode() >= code) {
            tvUpdateInfo.setText("已安装最新版本。");
            tvServerVersion.setText("远程版本：" + version);
            tvCheckUpdate.setEnabled(false);
        } else {
            if (Build.VERSION.SDK_INT < sdk) {
                tvUpdateInfo.setText("新版本需要更新版本的Android，您的设备不符合要求。");
                tvServerVersion.setText("远程版本：" + version);
                tvCheckUpdate.setEnabled(false);
            } else {
                tvUpdateInfo.setText(updateInfo + "\n更新大小：" + size);
                tvServerVersion.setText("远程版本：" + version);
                tvCheckUpdate.setEnabled(true);
                tvCheckUpdate.setText("下载更新");
                tvCheckUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!pwd.startsWith("不")) {
                            new AlertDialog.Builder(PreferenceUpdate.this).setMessage("本次更新需要通过密码下载\n" +
                                    "下载密码：" + pwd + "\n点击 " + getString(android.R.string.ok) + " 将复制密码到剪切板")
                                    .setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, pwd));
                                            Toast.makeText(PreferenceUpdate.this, "已复制密码", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            Uri content_url = Uri.parse(link);
                                            intent.setData(content_url);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).show();
                        } else {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            Uri content_url = Uri.parse(link);
                            intent.setData(content_url);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }
    }

    private int getVersionCode() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    private String getVersionName() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            return "NULL";
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
