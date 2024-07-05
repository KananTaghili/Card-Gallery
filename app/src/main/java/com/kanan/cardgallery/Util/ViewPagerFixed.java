package com.kanan.cardgallery.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.view.MotionEventCompat;

/**
 * Created by Kanan on 01.08.2023.
 */

public class ViewPagerFixed extends androidx.viewpager.widget.ViewPager {

    float mStartDragX;
    OnSwipeOutListener mOnSwipeOutListener;

    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        mOnSwipeOutListener = listener;
    }

    private void onSwipeOutAtStart() {
        if (mOnSwipeOutListener != null) {
            mOnSwipeOutListener.onSwipeOutAtStart();
        }
    }

    private void onSwipeOutAtEnd() {
        if (mOnSwipeOutListener != null) {
            mOnSwipeOutListener.onSwipeOutAtEnd();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if ((ev.getAction() & MotionEventCompat.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            mStartDragX = ev.getX();
        }
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (getCurrentItem() == 0 || getCurrentItem() == getAdapter().getCount() - 1) {
            float x = ev.getX();
            if ((ev.getAction() & MotionEventCompat.ACTION_MASK) == MotionEvent.ACTION_UP) {
                if (getCurrentItem() == 0 && x > mStartDragX) {
                    onSwipeOutAtStart();
                }
                if (getCurrentItem() == getAdapter().getCount() - 1 && x < mStartDragX) {
                    onSwipeOutAtEnd();
                }
            }
        } else {
            mStartDragX = 0;
        }
        return super.onTouchEvent(ev);
    }

    public interface OnSwipeOutListener {
        void onSwipeOutAtStart();

        void onSwipeOutAtEnd();
    }
}