package com.lkyear.lllauncher.Preference;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.lkyear.lllauncher.Util.PixelUtils;

/**
 * Created by lkyear on 2017/7/13.
 */

public class PreferenceUserLogoAdapter extends BaseAdapter {

    private Context mContext;
    private int[] mResId;

    public PreferenceUserLogoAdapter(Context context, int[] resId) {
        mContext = context;
        mResId = resId;
    }

    @Override
    public int getCount() {
        return mResId.length;
    }

    @Override
    public Object getItem(int position) {
        return mResId[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if(view == null){
            imageView = new ImageView(mContext);
            int layoutHeight = PixelUtils.dp2px(mContext, 75);
            int layoutPadding = PixelUtils.dp2px(mContext, 8);
            imageView.setLayoutParams(new GridView.LayoutParams(layoutHeight, layoutHeight));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(layoutPadding, layoutPadding, layoutPadding, layoutPadding);
        }else{
            imageView = (ImageView) view;
        }
        imageView.setImageResource(mResId[position]);
        return imageView;
    }
}
