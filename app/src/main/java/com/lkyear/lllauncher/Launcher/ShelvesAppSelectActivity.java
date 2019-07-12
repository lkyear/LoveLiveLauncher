package com.lkyear.lllauncher.Launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lkyear.lllauncher.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lkyear on 2018/2/14.
 */

public class ShelvesAppSelectActivity extends LauncherCommonActivity {

    private RecyclerView recyclerView;
    private List<AppData> appDataList;
    private ShelvesSelectViewAdapter shelvesSelectViewAdapter;
    private Boolean requireUpdate = false;

    private Shelves updateShelves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.launcher_pages_apps_shelevs_select);
        setTitle("创建/编辑卡片");
        recyclerView = (RecyclerView) findViewById(R.id.launcher_shelevs_recycler);
        Intent intent = getIntent();
        int Id = intent.getIntExtra("Id", -100);
        if (Id != -100) {
            Shelves shelves = new Shelves();
            shelves.setId(Id);
            shelves.setTitle(intent.getStringExtra("title"));
            shelves.setPackages(intent.getStringExtra("packages"));
            updateShelves = shelves;
            requireUpdate = true;
            setTitle("Edit card");
        }
        initData();
    }

    private class GetAppsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setTitle("应用程序加载中...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDataList = new ArrayList<AppData>();
            appDataList.clear();
            PackageManager pm = getPackageManager();
            List<String> packages = null;
            if (requireUpdate) {
                packages = Arrays.asList(updateShelves.getPackages().split(","));
            }
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> packs = pm.queryIntentActivities(intent, 0);
            for (ResolveInfo ri : packs) {
                AppData appData = new AppData(ri.loadLabel(pm), ri.activityInfo.packageName, ri.loadIcon(pm));
                if (ri.activityInfo.packageName.equals("com.lkyear.lllauncher") ||
                        ri.activityInfo.packageName.equals("com.mimikko.mimikkoui") ||
                        ri.activityInfo.packageName.equals("com.meizu.flyme.launcher")) {
                    continue;
                }
                if (packages != null) {
                    if (packages.contains(appData.getAppPackageName())) {
                        appData.setRequireSelect(true);
                    }
                }
                appDataList.add(appData);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (requireUpdate) {
                setTitle("编辑卡片");
            } else {
                setTitle("创建新卡片");
            }
            recyclerView.setLayoutManager(new GridLayoutManager(ShelvesAppSelectActivity.this, 4));
            shelvesSelectViewAdapter = new ShelvesSelectViewAdapter(ShelvesAppSelectActivity.this,
                    appDataList, false);
            recyclerView.setAdapter(shelvesSelectViewAdapter);
        }

    }

    private void initData() {
        new GetAppsTask().execute();
    }

    public void showSelected(View view) {
        if (shelvesSelectViewAdapter.getSelectedItem().size() <= 0 || shelvesSelectViewAdapter.getSelectedItem().size() > 20) {
            alertDialog("每个卡牌只能容纳1~20个应用");
            initData();
            return;
        }
        final EditText editText = new EditText(ShelvesAppSelectActivity.this);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
        if (requireUpdate) {
            editText.setText(updateShelves.getTitle());
        }
        new AlertDialog.Builder(ShelvesAppSelectActivity.this)
                .setTitle("卡片标题")
                .setView(editText)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String content = "";
                        for (String s : shelvesSelectViewAdapter.getSelectedItem()) {
                            content += s + ",";
                        }
                        content = content.substring(0, content.length() - 1);
                        ContentValues values = new ContentValues();
                        if (TextUtils.isEmpty(editText.getText().toString())) {
                            values.put("title", "未命名卡片");
                        } else {
                            values.put("title", editText.getText().toString());
                        }
                        values.put("packages", content);
                        ShelvesDataBaseHelper dataBaseHelper = new ShelvesDataBaseHelper(ShelvesAppSelectActivity.this);
                        if (requireUpdate) {
                            dataBaseHelper.update(values, updateShelves.getId());
                            dataBaseHelper.close();
                            Toast.makeText(ShelvesAppSelectActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            if (dataBaseHelper.insert(values)) {
                                Toast.makeText(ShelvesAppSelectActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShelvesAppSelectActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                            }
                            dataBaseHelper.close();
                            finish();
                        }
                    }
                })
                .show();
    }

    private void alertDialog(String message) {
        new AlertDialog.Builder(ShelvesAppSelectActivity.this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
