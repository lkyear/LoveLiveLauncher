package com.lkyear.lllauncher.Launcher;

import android.graphics.drawable.Drawable;

public class AppData {

	public CharSequence appName;
	public String appPackageName;
	public Drawable appIcon;
	public String sortLetters;  //显示数据拼音的首字母

	public Boolean requireSelect = false;

	public AppData(CharSequence appName, String packageName, Drawable icon) {
		this.appName = appName;
		this.appPackageName = packageName;
		this.appIcon = icon;
	}

	public CharSequence getAppName() {
		return appName;
	}

	public void setAppName(CharSequence appName) {
		this.appName = appName;
	}

	public String getAppPackageName() {
		return appPackageName;
	}

	public void setAppPackageName(String appPackageName) {
		this.appPackageName = appPackageName;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public void setRequireSelect(Boolean requireSelect) {
		this.requireSelect = requireSelect;
	}

	public Boolean getRequireSelect() {
		return requireSelect;
	}
}
