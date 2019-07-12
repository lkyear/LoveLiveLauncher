package com.lkyear.lllauncher.Launcher;

import com.lkyear.lllauncher.Launcher.AppData;

import java.util.Comparator;

public class PinyinComparator implements Comparator<AppData> {

	public int compare(AppData o1, AppData o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
