package com.lkyear.lllauncher.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lkyear.lllauncher.R;

public class IndexBar extends View {
	// 触摸事件
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	// 26个字母
	public static String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "#" };
	private int choose = -1;// 选中
	private Paint paint = new Paint();

	private TextView mTextDialog;

	/**
	 * 为SideBar设置显示字母的TextView
	 * 
	 * @param mTextDialog
	 */
	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}

	public IndexBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public IndexBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndexBar(Context context) {
		super(context);
	}

	/**
	 * 重写这个方法
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 获取焦点改变背景颜色.
		int height = getHeight();// 获取对应高度
		int width = getWidth(); // 获取对应宽度
		int singleHeight = height / b.length;// 获取每一个字母的高度

		for (int i = 0; i < b.length; i++) {
			paint.setColor(Color.parseColor("#505050"));
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize((int)this.getResources().getDimension(R.dimen.IndexBarTextSize));
			// 选中的状态
			if (i == choose) {
				paint.setColor(Color.rgb(0,0,0));
				paint.setFakeBoldText(true);
			}
			// x坐标等于中间-字符串宽度的一半.
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(b[i], xPos, yPos, paint);
			paint.reset();// 重置画笔
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();// 点击y坐标
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * b.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

		switch (action) {
			case MotionEvent.ACTION_UP:
				//setBackgroundDrawable(new ColorDrawable(0x00000000));
				choose = -1;//
				invalidate();
				if (mTextDialog != null) {
					Animation AnimationOut = animationOutAlpha();
					mTextDialog.startAnimation(AnimationOut);
					AnimationOut.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {}

						@Override
						public void onAnimationEnd(Animation animation) {
							mTextDialog.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {}
					});
				}
			break;
			case MotionEvent.ACTION_OUTSIDE:
				choose = -1;//
				invalidate();
				if (mTextDialog != null) {
					Animation AnimationOut = animationOutAlpha();
					mTextDialog.startAnimation(AnimationOut);
					AnimationOut.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {}

						@Override
						public void onAnimationEnd(Animation animation) {
							mTextDialog.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {}
					});
				}
			break;
			case MotionEvent.ACTION_CANCEL:
				choose = -1;//
				invalidate();
				if (mTextDialog != null) {
					Animation AnimationOut = animationOutAlpha();
					mTextDialog.startAnimation(AnimationOut);
					AnimationOut.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {}

						@Override
						public void onAnimationEnd(Animation animation) {
							mTextDialog.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {}
					});
				}
				break;
			default:
				//setBackgroundResource(R.drawable.sidebar_background);
				//setLayoutY(mTextDialog, (int) event.getY());
				if (oldChoose != c) {
					if (c >= 0 && c < b.length) {
						if (listener != null) {
							listener.onTouchingLetterChanged(b[c]);
						}
						if (mTextDialog != null) {
							mTextDialog.setText(b[c]);
							mTextDialog.setVisibility(View.VISIBLE);
						}
						choose = c;
						invalidate();
					}
				}
			break;
		}
		return true;
	}

	/*
     * 设置控件所在的位置Y，并且不改变宽高，
     * Y为绝对位置，此时X可能归0
     */
	public void setLayoutY(View view, int y)
	{
		ViewGroup.MarginLayoutParams margin=new ViewGroup.MarginLayoutParams(view.getLayoutParams());
		if (y < 200) y = 200;
		margin.setMargins(margin.leftMargin,y, margin.rightMargin, y+margin.height);
		Log.e("!----", "margin.leftMargin = " + margin.leftMargin + ", margin.rightMargin = " +
				margin.rightMargin + ", margin.height = " + (y + margin.height));
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
		view.setLayoutParams(layoutParams);
	}

	private Animation animationOutAlpha() {
		Animation animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setInterpolator(new DecelerateInterpolator());
		animation.setStartOffset(100);
		animation.setDuration(300);
		return animation;
	}

	/**
	 * 向外公开的方法
	 * 
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	/**
	 * 接口
	 * 
	 * @author coder
	 * 
	 */
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}