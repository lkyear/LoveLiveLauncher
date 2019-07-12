package com.lkyear.lllauncher.Preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;

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

public class PreferenceBackgroundUpdate extends AsyncTask<String, Integer, String> {

    private int connect;
    private String version, updateInfo, size, link, pwd;
    private int code, sdk;
    private Context mContext;

    public PreferenceBackgroundUpdate(Context context) {
        mContext = context;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskWrites().detectDiskReads().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(String... url) {
        try {
            Thread.sleep(1000);
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
        }
        return myString;
    }

    protected void onPostExecute(String myString) {
        if (connect == 1) {
            setUpdate();
        }
    }

    private void setUpdate() {
        if (getVersionCode() != 0 & getVersionCode() < code & Build.VERSION.SDK_INT >= sdk) {
            new AlertDialog.Builder(mContext).setMessage("LoveLive桌面现已提供更新的版本，点击 " + mContext.getString(android.R.string.ok) + " 开始更新。")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(mContext, PreferenceUpdate.class);
                            intent.putExtra("from_background", true);
                            intent.putExtra("code", code);
                            intent.putExtra("updateInfo", updateInfo);
                            intent.putExtra("size", size);
                            intent.putExtra("sdk", sdk);
                            intent.putExtra("link", link);
                            intent.putExtra("pwd", pwd);
                            intent.putExtra("version", version);
                            mContext.startActivity(intent);
                        }
                    }).setCancelable(false).show();
        }
    }

    private int getVersionCode() {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            return 0;
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
