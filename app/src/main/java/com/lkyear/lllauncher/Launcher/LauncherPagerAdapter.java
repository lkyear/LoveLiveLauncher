package com.lkyear.lllauncher.Launcher;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by lkyear on 17/5/18.
 */

public class LauncherPagerAdapter extends android.support.v4.view.PagerAdapter {

    private List<View> viewList;

    public LauncherPagerAdapter(List<View> launcherViewList) {
        super();
        viewList = launcherViewList;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }
}
