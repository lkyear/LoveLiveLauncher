package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lkyear.lllauncher.Launcher.AppData;
import com.lkyear.lllauncher.Launcher.AppViewAdapter;
import com.lkyear.lllauncher.Launcher.CharacterParser;
import com.lkyear.lllauncher.Launcher.PinyinComparator;
import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lkyear on 2017/7/27.
 */

public class PreferenceBrowser extends PreferenceBaseActivity {

    private List<AppData> appDataList;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private AppViewAdapter appViewAdapter;
    private RecyclerView rvAppList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.launcher_preference_browser);
        rvAppList = (RecyclerView) findViewById(R.id.launcher_pref_browser_rc);
        new LauncherApplicationViewTask().execute();
    }

    /***
     * 获取应用信息的新线程
     */
    private class LauncherApplicationViewTask extends AsyncTask<Void, Void, List<AppData>> {

        @Override
        protected void onPreExecute() {
            setTitle("应用列表加载中...");
        }

        @Override
        protected List<AppData> doInBackground(Void... params) {
            appDataList = new ArrayList<AppData>();
            appDataList.clear();
            PackageManager pm = getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            if (isCancelled()) {
                return null;
            }
            List<ResolveInfo> packs = pm.queryIntentActivities(intent, 0);
            for (ResolveInfo ri : packs) {
                AppData appData = new AppData(ri.loadLabel(pm), ri.activityInfo.packageName, ri.loadIcon(pm));
                if (ri.activityInfo.packageName.equals("com.lkyear.lllauncher") || ri.activityInfo.packageName.equals("com.mimikko.mimikkoui")
                        || ri.activityInfo.packageName.equals("com.meizu.flyme.launcher")) {
                    continue;
                } else {
                    if (isCancelled()) {
                        return null;
                    } else {
                        appDataList.add(appData);
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
            if (!isCancelled()) {
                setTitle("选择浏览器应用");
                InitApplicationList();
            } else {
                finish();
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
        appViewAdapter = new AppViewAdapter(PreferenceBrowser.this, appDataList, true);
        rvAppList.setLayoutManager(new GridLayoutManager(this, 4));
        rvAppList.setAdapter(appViewAdapter);
        appViewAdapter.setOnItemClickListener(new AppViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Utils.preferencePutData(PreferenceBrowser.this, "pref_browser_app_package_name",
                        appViewAdapter.getAppData().get(position).getAppPackageName().toString());
                Toast.makeText(PreferenceBrowser.this, "默认浏览器已更改", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
