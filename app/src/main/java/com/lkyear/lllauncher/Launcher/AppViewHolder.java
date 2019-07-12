package com.lkyear.lllauncher.Launcher;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.View.IconView;

/**
 * Created by lkyear on 2016/12/3.
 */

public class AppViewHolder extends RecyclerView.ViewHolder {

    IconView iconView;

    public AppViewHolder(View itemView, Boolean roundView) {
        super(itemView);
        setIsRecyclable(false);
        iconView = (IconView) itemView.findViewById(R.id.app_IconView);
    }
}
