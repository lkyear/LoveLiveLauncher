package com.lkyear.lllauncher.Launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.lkyear.lllauncher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkyear on 2018/2/14.
 */

public class ShelvesManageActivity extends LauncherCommonActivity {

    private ListView listView;

    private List<Shelves> shelvesList;
    private ArrayList<String> shelvesListArray = new ArrayList<String>();

    private Boolean needToReload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.launcher_pages_shelves_manage_layout);
        setTitle("卡片管理");
        listView = (ListView) findViewById(R.id.shelves_manage_listview);
        Init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needToReload) {
            Init();
        }
        needToReload = true;
    }

    private void Init() {
        try {
            shelvesList = new ArrayList<Shelves>();
            final ShelvesDataBaseHelper dataBaseHelper = new ShelvesDataBaseHelper(ShelvesManageActivity.this);
            Cursor cursor = dataBaseHelper.query();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String packages = cursor.getString(cursor.getColumnIndex("packages"));
                Shelves shelves = new Shelves();
                shelves.setId(id);
                shelves.setTitle(title);
                shelves.setPackages(packages);

                shelvesList.add(shelves);
            }
            dataBaseHelper.close();
            if (!shelvesList.isEmpty()) {
                listView.setVisibility(View.VISIBLE);
                shelvesListArray.clear();
                for (int i = 0; i < shelvesList.size(); i++) {
                    Shelves shelves = shelvesList.get(i);
                    shelvesListArray.add((i + 1) + "、" + shelves.getTitle());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, shelvesListArray);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                        PopupMenu popupMenu = new PopupMenu(ShelvesManageActivity.this, view);
                        Menu menu = popupMenu.getMenu();
                        menu.add("编辑");
                        menu.add("删除");
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                if (menuItem.getTitle().equals("编辑")) {
                                    //编辑
                                    Intent intent = new Intent(ShelvesManageActivity.this, ShelvesAppSelectActivity.class);
                                    intent.putExtra("Id", shelvesList.get(position).getId());
                                    intent.putExtra("title", shelvesList.get(position).getTitle());
                                    intent.putExtra("packages", shelvesList.get(position).getPackages());
                                    startActivity(intent);
                                } else {
                                    //删除
                                    new AlertDialog.Builder(ShelvesManageActivity.this)
                                            .setMessage("是否删除该卡片?")
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    ShelvesDataBaseHelper delHelper = new ShelvesDataBaseHelper(ShelvesManageActivity.this);
                                                    delHelper.delete(shelvesList.get(position).getId());
                                                    delHelper.close();
                                                    Init();
                                                }
                                            }).setNegativeButton(android.R.string.no, null)
                                            .setCancelable(false)
                                            .show();
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            } else {
                listView.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            new AlertDialog.Builder(ShelvesManageActivity.this)
                    .setTitle("错误")
                    .setMessage("请将下列信息反馈给开发者\n" + e.toString())
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }

    public void createNewCard(View view) {
        startActivity(new Intent(ShelvesManageActivity.this, ShelvesAppSelectActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(ShelvesManageActivity.this, Launcher.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(ShelvesManageActivity.this, Launcher.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
