package com.lkyear.lllauncher.Launcher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import com.lkyear.lllauncher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkyear on 2016/12/3.
 */

public class ShelvesSelectViewAdapter extends RecyclerView.Adapter<ShelvesSelectViewHolder> implements SectionIndexer {

    private Context mContext;
    private List<AppData> appData;
    private Boolean isRoundView = false, blackFont = false;

    SparseBooleanArray mSelectedPositions = new SparseBooleanArray();

    public ShelvesSelectViewAdapter(Context context, List<AppData> appDatas, Boolean blackValue) {
        mContext = context;
        appData = appDatas;
        blackFont = blackValue;
    }

    @Override
    public ShelvesSelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.launcher_pages_shelves_item_layout, parent, false);
        return new ShelvesSelectViewHolder(view, isRoundView);
    }

    @Override
    public void onBindViewHolder(final ShelvesSelectViewHolder holder, final int position) {
        holder.iconView.setAppIcon(appData.get(position).getAppIcon());
        holder.iconView.setAppName(appData.get(position).getAppName().toString().trim());
        holder.iconView.setAppNameTextColor();
        holder.iconView.setTag(position);
        if (appData.get(position).getRequireSelect()) {
            setItemChecked(position, true);
            holder.checkBox.setChecked(true);
            appData.get(position).setRequireSelect(false);
        }
        holder.checkBox.setChecked(isItemChecked(position));
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isItemChecked(position)) {
                    setItemChecked(position, false);
                } else {
                    setItemChecked(position, true);
                }
            }
        });
        holder.iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isItemChecked(position)) {
                    setItemChecked(position, false);
                    holder.checkBox.setChecked(false);
                } else {
                    setItemChecked(position, true);
                    holder.checkBox.setChecked(true);
                }
            }
        });
    }

    private void setItemChecked(int position, boolean isChecked) {
        mSelectedPositions.put(position, isChecked);
    }

    //根据位置判断条目是否选中
    private boolean isItemChecked(int position) {
        return mSelectedPositions.get(position);
    }

    /**
     * 获取AppData数据
     * @return 列表数据
     */
    public List<AppData> getAppData() {
        return appData;
    }

    public ArrayList<String> getSelectedItem() {
        ArrayList<String> selectList = new ArrayList<>();
        for (int i = 0; i < appData.size(); i++) {
            if (isItemChecked(i)) {
                selectList.add(appData.get(i).getAppPackageName());
            }
        }
        return selectList;
    }

        @Override
    public int getItemCount() {
        return appData.size();
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        try {
            return appData.get(position).getSortLetters().charAt(0);
        } catch (Exception e) {

        }
        return position;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < appData.size(); i++) {
            String sortStr = appData.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

}
