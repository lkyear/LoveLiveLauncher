package com.lkyear.lllauncher.Launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.lkyear.lllauncher.R;

/**
 * Created by lkyear on 2017/7/14.
 */

public class LauncherWebView extends LauncherCommonActivity {

    private WebView webView;

    private int reqInt = 0;

    public static final int HTML_ABOUT = 0;

    public static final int HTML_ROLE = 1;

    public static final int HTML_SHORTCUT = 2;

    public static final int HTML_THANKS = 3;

    public static final int HTML_USER = 4;

    public static final int HTML_PACK = 5;

    public static final int HTML_LEGAL = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.launcher_webview);
        setTitle("加载中...");
        webView = (WebView) findViewById(R.id.launcher_web_view);
        Intent intent = getIntent();
        reqInt = intent.getIntExtra("open_html_type", HTML_ABOUT);
        switch (reqInt) {
            case HTML_ABOUT:
                webView.loadUrl("file:///android_asset/html/About.html");
                break;
            case HTML_ROLE:
                webView.loadUrl("file:///android_asset/html/RoleHelp.html");
                break;
            case HTML_SHORTCUT:
                webView.loadUrl("file:///android_asset/html/ShortcutHelp.html");
                break;
            case HTML_THANKS:
                webView.loadUrl("file:///android_asset/html/Thanks.html");
                break;
            case HTML_USER:
                webView.loadUrl("file:///android_asset/html/UserHelp.html");
                break;
            case HTML_PACK:
                webView.loadUrl("file:///android_asset/html/PackHelp.html");
                break;
            case HTML_LEGAL:
                webView.loadUrl("file:///android_asset/html/Legal.html");
        }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    setTitle(view.getTitle());
                }
            }
        });
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
