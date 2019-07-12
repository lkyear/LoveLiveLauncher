package com.lkyear.lllauncher.Launcher;

import android.graphics.drawable.Drawable;

/**
 * Created by lkyear on 2017/7/7.
 */

public class LauncherUserInfoFunction {

    public Drawable icon;
    public String title;

    public LauncherUserInfoFunction(Drawable iconResources, String funcTitle) {
        icon = iconResources;
        title = funcTitle;
    }

    public void setIcon(Drawable iconResources) {
        icon = iconResources;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setTitle(String funcTitle) {
        title = funcTitle;
    }

    public String getTitle() {
        return title;
    }

}
