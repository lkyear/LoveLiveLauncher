package com.lkyear.lllauncher.View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by lkyear on 17/5/18.
 */

public class ShortcutView extends ImageView implements GestureDetector.OnGestureListener {

    /**
     * 定义包名
     */
    private String packageName;

    /**
     * 监听手势
     */
    private GestureDetector mGestureDetector;

    /**
     * 构造函数
     * @param context 上下文
     * @param attrs 参数
     */
    public ShortcutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, this);
    }

    /**
     * 设置包名
     * @param pkgName 包名
     */
    public void setPackageName(String pkgName) {
        packageName = pkgName.toString().trim();
    }

    /**
     * 获取包名
     * @return 包名
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * 设置图标滤镜
     */
    private void setFilter() {
        Drawable drawable = this.getDrawable();
        if (drawable == null) {
            drawable = getBackground();
        } else if (drawable != null) {
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }
    }

    /**
     * 移除图标滤镜
     */
    private void removeFilter() {
        Drawable drawable = this.getDrawable();
        if (drawable == null) {
            drawable = getBackground();
        } else if (drawable != null) {
            drawable.clearColorFilter();
        }
    }

    /**
     * 点击事件
     * @param event
     * @return
     */
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

    /**
     * 按下图标
     * @param e
     * @return
     */
    @Override
    public boolean onDown(MotionEvent e) {
        setFilter();
        return true;
    }

    /**
     * 显示触点
     * @param e
     */
    @Override
    public void onShowPress(MotionEvent e) { }

    /**
     * 轻按图标后抬起方法
     * @param e
     * @return
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        removeFilter();     //移除滤镜
        performClick();     //匹配点击事件
        return false;       //返回值
    }

    /**
     * 滑动时方法
     * @param e1
     * @param e2
     * @param distanceX
     * @param distanceY
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    /**
     * 长按时抬起手指方法
     * @param e
     */
    @Override
    public void onLongPress(MotionEvent e) {
        removeFilter();         //移除滤镜
        performLongClick();     //匹配长按时间
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

}
