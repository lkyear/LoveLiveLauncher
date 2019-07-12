package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lkyear.lllauncher.R;
import com.nispok.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkyear on 17/5/31.
 */

public class PreferenceRolePackDelete extends PreferenceBaseActivity {

    private ListView lvRoleList;
    private ArrayList<String> roleListArray = new ArrayList<String>();
    private List<Role> roleList = new ArrayList<Role>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.launcher_role_packdel_layout);
        setTitle("管理立绘包");
        Init();
    }

    private void Init() {
        lvRoleList = (ListView) findViewById(R.id.launcher_role_lv_list);
        try {
            roleList = new ArrayList<Role>();
            roleList.clear();
            final RoleDataBaseHelper roleDataBaseHelper = new RoleDataBaseHelper(this);
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
                finish();
            } else {
                roleListArray.clear();
                for (int i = 0; i < roleList.size(); i++) {
                    Role role = roleList.get(i);
                    roleListArray.add((i + 1) + "、" + role.getTitle());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roleListArray);
                lvRoleList.setAdapter(arrayAdapter);
                lvRoleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                        final Role role = roleList.get(i);
                        new AlertDialog.Builder(PreferenceRolePackDelete.this).setTitle("立绘包详情")
                                .setMessage("名称: " + role.getTitle() + "\n本地路径: " + role.getPath())
                                .setPositiveButton("好", null)
                                .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        RecursionDeleteFile(new File(role.getPath()));
                                        RoleDataBaseHelper roleDataBaseHelper1 = new RoleDataBaseHelper(PreferenceRolePackDelete.this);
                                        roleDataBaseHelper1.delete(role.getId());
                                        roleDataBaseHelper1.close();
                                        Snackbar.with(PreferenceRolePackDelete.this).text("立绘包已删除").show(PreferenceRolePackDelete.this);
                                        Init();
                                    }
                                }).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(this, "没有安装立绘包", Toast.LENGTH_SHORT).show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
