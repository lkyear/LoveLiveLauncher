package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lkyear.lllauncher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkyear on 2017/10/3.
 */

public class PreferenceBubbleText extends PreferenceBaseActivity {

    private ListView bubbleTextListView;
    private List<BubbleText> bubbleTexts = new ArrayList<>();
    private ArrayList<String> bubbleTextListArray = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.launcher_preference_bubble_text_layout);
        setTitle("动态气泡文本配置");
        bubbleTextListView = (ListView) findViewById(R.id.launcher_pref_bubble_text_list);
        refreshBubbleTextData();
    }

    private void refreshBubbleTextData() {
        bubbleTexts = new ArrayList<>();
        bubbleTexts.clear();
        BubbleTextDataBaseHelper bubbleTextDataBaseHelper = new BubbleTextDataBaseHelper(this);
        Cursor cursor = bubbleTextDataBaseHelper.query();
        while (cursor.moveToNext()) {
            int Id = cursor.getInt(cursor.getColumnIndex("_id"));
            String title = cursor.getString(cursor.getColumnIndex("BubbleContent"));
            BubbleText bubbleText = new BubbleText();
            bubbleText.setId(Id);
            bubbleText.setBubbleContent(title);
            bubbleTexts.add(bubbleText);
        }
        bubbleTextDataBaseHelper.close();
        if (!bubbleTexts.isEmpty()) {
            bubbleTextListView.setVisibility(View.VISIBLE);
            bubbleTextListArray.clear();
            for (int i = 0; i < bubbleTexts.size(); i++) {
                BubbleText bubbleText = bubbleTexts.get(i);
                bubbleTextListArray.add((i + 1) + "、" + bubbleText.getBubbleContent());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bubbleTextListArray);
            bubbleTextListView.setAdapter(arrayAdapter);
            bubbleTextListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final BubbleText btxs = bubbleTexts.get(position);
                    new AlertDialog.Builder(PreferenceBubbleText.this).setTitle("删除?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    BubbleTextDataBaseHelper bubbleTextDataBaseHelperInd = new BubbleTextDataBaseHelper(PreferenceBubbleText.this);
                                    bubbleTextDataBaseHelperInd.delete(btxs.getId());
                                    refreshBubbleTextData();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                    return false;
                }
            });
        } else {
            bubbleTextListView.setVisibility(View.INVISIBLE);
            Toast.makeText(PreferenceBubbleText.this, "为何不点右上角的加号添加文本呢？", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.launcher_preference_bubble_text, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_bubble_add_text:
                final EditText edit = new EditText(PreferenceBubbleText.this);
                edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
                new AlertDialog.Builder(PreferenceBubbleText.this).setTitle("气泡文本")
                        .setView(edit).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(edit.getText().toString().trim())) {
                            ContentValues values = new ContentValues();
                            values.put("BubbleContent", edit.getText().toString().trim());
                            BubbleTextDataBaseHelper bubbleTextDataBaseHelper = new BubbleTextDataBaseHelper(PreferenceBubbleText.this);
                            if (bubbleTextDataBaseHelper.insert(values)) {
                                bubbleTextDataBaseHelper.close();
                                Toast.makeText(PreferenceBubbleText.this, "添加成功", Toast.LENGTH_SHORT).show();
                                refreshBubbleTextData();
                            } else {
                                Toast.makeText(PreferenceBubbleText.this, "添加失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PreferenceBubbleText.this, "不可为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton(android.R.string.no, null).setCancelable(false).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
