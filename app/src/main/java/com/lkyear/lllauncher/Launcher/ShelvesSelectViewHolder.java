package com.lkyear.lllauncher.Launcher;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.View.IconView;

/**
 * Created by lkyear on 2016/12/3.
 */

public class ShelvesSelectViewHolder extends RecyclerView.ViewHolder {

    IconView iconView;
    CheckBox checkBox;

    public ShelvesSelectViewHolder(View itemView, Boolean roundView) {
        super(itemView);
        setIsRecyclable(false);
        iconView = (IconView) itemView.findViewById(R.id.app_IconView);
        checkBox = (CheckBox) itemView.findViewById(R.id.app_CheckBox);
    }
}
