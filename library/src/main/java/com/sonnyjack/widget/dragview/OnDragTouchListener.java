package com.sonnyjack.widget.dragview;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

/**
 * time :2019-04-25
 * author:DevRen
 * view 注册使用 new OnDragTouchListener();
 */

public class OnDragTouchListener implements View.OnTouchListener {

    private Context mContext;
    private boolean hasAutoPullToBorder;//标记是否开启自动拉到边缘功能

    private int mStatusBarHeight;
    private int mScreenWidth, mScreenHeight;//屏幕宽高
    //手指按下位置
    private int mStartX, mStartY, mLastX, mLastY;
    private boolean mTouchResult = false;

    public OnDragTouchListener() {
    }

    public OnDragTouchListener(boolean isAutoPullToBorder, Context context) {
        mContext = context;
        //屏幕宽高
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (null != windowManager) {
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            mScreenWidth = displayMetrics.widthPixels;
            mScreenHeight = displayMetrics.heightPixels;
        }
        //状态栏高度
        Rect frame = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        mStatusBarHeight = frame.top;
        if (mStatusBarHeight <= 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                mStatusBarHeight = mContext.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        hasAutoPullToBorder = isAutoPullToBorder;
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchResult = false;
                mStartX = mLastX = (int) event.getRawX();
                mStartY = mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int left, top, right, bottom;
                int dx = (int) event.getRawX() - mLastX;
                int dy = (int) event.getRawY() - mLastY;
                left = v.getLeft() + dx;
                if (left < 0) {
                    left = 0;
                }
                right = left + v.getWidth();
                if (right > mScreenWidth) {
                    right = mScreenWidth;
                    left = right - v.getWidth();
                }
                top = v.getTop() + dy;
                if (top < mStatusBarHeight + 56) {
                    top = mStatusBarHeight + 56;
                }
                bottom = top + v.getHeight();
                if (bottom > mScreenHeight - 49) {
                    bottom = mScreenHeight - 49;
                    top = bottom - v.getHeight();
                }
                v.layout(left, top, right, bottom);
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();

                break;
            case MotionEvent.ACTION_UP:
                //这里需设置LayoutParams，不然按home后回再到页面等view会回到原来的地方
                v.setLayoutParams(createLayoutParams(v.getLeft(), v.getTop(), 0, 0));

                float endX = event.getRawX();
                float endY = event.getRawY();
                if (Math.abs(endX - mStartX) > 5 || Math.abs(endY - mStartY) > 5) {
                    //防止点击的时候稍微有点移动点击事件被拦截了
                    mTouchResult = true;
                }
                if (mTouchResult) {
                    //是否每次都移至屏幕边沿
                    moveNearEdge(v);
                }
                break;
        }
        return mTouchResult;
    }

    private RelativeLayout.LayoutParams createLayoutParams(int left, int top, int right, int bottom) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    public boolean isHasAutoPullToBorder() {
        return hasAutoPullToBorder;
    }

    public void setHasAutoPullToBorder(boolean hasAutoPullToBorder) {
        this.hasAutoPullToBorder = hasAutoPullToBorder;
    }

    /**
     * 移至最近的边沿
     */
    private void moveNearEdge(final View v) {
        int left = v.getLeft();
        int lastX;
//        if (left + getDragView().getWidth() / 2 <= mScreenWidth / 2) {
//            lastX = 0;
//        } else {
//            lastX = mScreenWidth - getDragView().getWidth();
//        }
        lastX = 1;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(left, lastX);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(0);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int left = (int) animation.getAnimatedValue();
                v.setLayoutParams(createLayoutParams(left, v.getTop(), 0, 0));
            }
        });
        valueAnimator.start();
    }
}
