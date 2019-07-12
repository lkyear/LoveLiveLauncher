package com.lkyear.lllauncher.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lkyear.lllauncher.R;

/**
 * lllauncher
 * com.lkyear.lllauncher.CostumView.IconView
 *
 * @author lkyear
 * @date 2017/1/19
 * @use 单个图标View，Launcher基本控件
 *
 * 2016 Frank Wang 版权所有
 */

public class UserInfoIconView extends LinearLayout implements GestureDetector.OnGestureListener {

    /**
     * 显示程序图标的的ImageView
     */
    private ImageView appIcon;

    /**
     * 显示程序图标的TextView
     */
    private TextView appName;

    /**
     * 软件包名，接受XML值的字符串型变量
     */
    private String appPackageName, nameXML;

    /**
     * 接受XML值表示软件图标资源ID的整型变量
     */
    private int redId;

    /**   监听手势*/
    private GestureDetector mGestureDetector;

    /**
     * 默认构造函数
     *
     * @param context 上下文
     */
    public UserInfoIconView(Context context) {
        super(context);
    }

    /**
     * 默认构造函数
     * @param context 上下文
     * @param attrs attrs
     */
    public UserInfoIconView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //读取从XML里传来的值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.iconViewAttr);
        redId = typedArray.getResourceId(R.styleable.iconViewAttr_app_icon, -1);
        nameXML = typedArray.getString(R.styleable.iconViewAttr_app_name);
        typedArray.recycle();

        //从布局文件中读取布局并查找控件
        LayoutInflater.from(context).inflate(R.layout.icon_view_user_info_layout, this, true);
        appIcon = (ImageView) findViewById(R.id.applicationIcon);
        appName = (TextView) findViewById(R.id.applicationName);

        SharedPreferences iconSize = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean smallSize = iconSize.getBoolean("pref_small_icon", false);
        if (smallSize) {
            LayoutParams layoutParams = (LayoutParams) appIcon.getLayoutParams();
            layoutParams.height = dip2px(context, 50);
            layoutParams.weight = dip2px(context, 50);
            appIcon.setLayoutParams(layoutParams);
        }

        //设置从XML里传来的数据
        appIcon.setImageResource(redId);
        appName.setText(nameXML);

        mGestureDetector = new GestureDetector(context, this);
    }

    /**
     * dp2px
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取程序图标
     * @return 程序的图标Drawable
     */
    public Drawable getAppIcon() {
        return appIcon.getDrawable();
    }

    /**
     * 设置程序图标
     * @param drawable 图标
     */
    public void setAppIcon(Drawable drawable) {
        appIcon.setImageDrawable(drawable);
    }

    /**
     * 获取程序名称
     * @return 名称
     */
    public String getAppName() {
        return appName.getText().toString().trim();
    }

    /**
     * 设置程序名称
     * @param applicationName 名称
     */
    public void setAppName(String applicationName) {
        appName.setText(applicationName);
    }

    /**
     * 获取程序包名
     * @return 包名
     */
    public String getAppPackageName() {
        if (TextUtils.isEmpty(appPackageName)) {
            return "No Package Name";
        } else {
            return appPackageName;
        }
    }

    /**
     * 设置程序包名
     * @param packageName 包名
     */
    public void setAppPackageName(String packageName) {
        appPackageName = packageName;
    }

    /**
     * 设置图标滤镜
     */
    private void setFilter() {
        Drawable drawable = getAppIcon();
        if (drawable == null) {
            drawable = getBackground();
        } else if (drawable != null) {
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);;
        }
    }

    /**
     * 移除图标滤镜
     */
    private void removeFilter() {
        Drawable drawable = getAppIcon();
        if (drawable == null) {
            drawable = getBackground();
        } else if (drawable != null){
            drawable.clearColorFilter();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_CANCEL:
                removeFilter();
                break;
            case MotionEvent.ACTION_OUTSIDE:
                removeFilter();
                break;
            case MotionEvent.ACTION_UP:
                removeFilter();
                break;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    public void setAppNameTextColor() {
        appName.setTextColor(Color.BLACK);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        setFilter();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        removeFilter();
        performClick();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        removeFilter();
        performLongClick();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

}