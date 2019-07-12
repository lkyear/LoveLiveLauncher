package com.lkyear.lllauncher.Launcher;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.View.IconView;
import com.lkyear.lllauncher.View.UserInfoIconView;

/**
 * Created by lkyear on 2016/12/3.
 */

public class LauncherUserInfoHolder extends RecyclerView.ViewHolder {

    UserInfoIconView iconView;

    public LauncherUserInfoHolder(View itemView, Boolean roundView) {
        super(itemView);
        setIsRecyclable(false);
        iconView = (UserInfoIconView) itemView.findViewById(R.id.app_IconView);
    }
}
