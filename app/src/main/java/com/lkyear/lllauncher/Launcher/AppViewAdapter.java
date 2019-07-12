package com.lkyear.lllauncher.Launcher;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import com.lkyear.lllauncher.R;

import java.util.List;

/**
 * Created by lkyear on 2016/12/3.
 */

public class AppViewAdapter extends RecyclerView.Adapter<AppViewHolder> implements SectionIndexer, View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private List<AppData> appData;
    private Boolean isRoundView = false, blackFont = false;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;

    public AppViewAdapter(Context context, List<AppData> appDatas, Boolean blackValue) {
        mContext = context;
        appData = appDatas;
        blackFont = blackValue;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.launcher_pages_apps_item_layout, parent, false);
        return new AppViewHolder(view, isRoundView);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        holder.iconView.setOnClickListener(this);
        holder.iconView.setOnLongClickListener(this);
        holder.iconView.setAppIcon(appData.get(position).getAppIcon());
        holder.iconView.setAppName(appData.get(position).getAppName().toString().trim());
        if (blackFont) {
            holder.iconView.setAppNameTextColor();
        }
        holder.iconView.setTag(position);
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener.onItemLongClick(view, (int) view.getTag());
        }
        return true;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<AppData> list){
        appData = list;
        notifyDataSetChanged();
    }

    /**
     * 清除List里的数据
     */
    public void clearListData() {
        try {
            appData.clear();
        } catch (Exception e) {}
    }

    /**
     * 获取AppData数据
     * @return 列表数据
     */
    public List<AppData> getAppData() {
        return appData;
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

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    //define interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    //define interface
    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

}
