package com.kania.todostack2.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by user on 2016-02-29.
 */
public class TodoViewPager extends ViewPager {
    public TodoViewPager(Context context) {
        super(context);
    }

    public TodoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //block swipe event
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //block swipe event
        return false;
    }
}
