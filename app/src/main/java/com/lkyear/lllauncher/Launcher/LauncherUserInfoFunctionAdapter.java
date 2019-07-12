package com.lkyear.lllauncher.Launcher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lkyear.lllauncher.R;

import java.util.List;

/**
 * Created by lkyear on 2017/7/7.
 */

public class LauncherUserInfoFunctionAdapter extends RecyclerView.Adapter<LauncherUserInfoHolder> implements View.OnClickListener {

    private Context mContext;
    private List<LauncherUserInfoFunction> funcData;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public LauncherUserInfoFunctionAdapter(Context context, List<LauncherUserInfoFunction> funcDatas) {
        mContext = context;
        funcData = funcDatas;
    }

    @Override
    public LauncherUserInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.launcher_user_info_item_layout, parent, false);
        return new LauncherUserInfoHolder(view, false);
    }

    @Override
    public void onBindViewHolder(LauncherUserInfoHolder holder, int position) {
        holder.iconView.setOnClickListener(this);
        holder.iconView.setAppIcon(funcData.get(position).getIcon());
        holder.iconView.setAppName(funcData.get(position).getTitle().toString().trim());
        holder.iconView.setAppNameTextColor();
        holder.iconView.setTag(position);
    }


    @Override
    public int getItemCount() {
        return funcData.size();
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    public List<LauncherUserInfoFunction> getFuncData() {
        return funcData;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    //define interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

}
