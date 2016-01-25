package com.kania.todostack2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kania.todostack2.provider.ColorProvider;

import java.util.ArrayList;

/**
 * Created by user on 2016-01-25.
 */
public class TodoLayout extends ViewGroup {
    private ArrayList<Integer> alSolidPathPos;
    private ArrayList<Integer> alDashPathPos;

    public TodoLayout(Context context) {
        super(context);
        initArrays();
    }

    public TodoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initArrays();
    }

    private void initArrays() {
        alSolidPathPos = new ArrayList<Integer>();
        alDashPathPos = new ArrayList<Integer>();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();

        for(int i = 0; i < childCount; ++i) {
            View tv = getChildAt(i);
            if (tv instanceof TextView) {
                setLayoutFromTag((TextView) tv);
            }
        }
    }

    private void setLayoutFromTag(TextView tv) {
        ViewPosition pos = (ViewPosition) tv.getTag();
        if (pos == null)
            return;
        Log.d("TodoStack", "[setLayoutFromTag] l/t/r/b = "
                + pos.left + "/" + pos.top + "/" + pos.right + "/" + pos.bottom);
        tv.layout(pos.left, pos.top, pos.right, pos.bottom);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, pos.bottom - pos.top);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        //dash path
        paint.setColor(ColorProvider.getColor(ColorProvider.COLOR_DASH_PATH));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{2, 5}, 2));
        paint.setStrokeWidth(TodoLayoutInfo.DATEDIVIDERLINE_HEIGHT);
        for (int pos : alDashPathPos) {
            canvas.drawLine(0, pos, this.getWidth(), pos, paint);
        }

        //soild path
        paint.setPathEffect(null);
        paint.setStrokeWidth(TodoLayoutInfo.TASKBASELINE_HEIGHT);
        for (int pos : alSolidPathPos) {
            canvas.drawLine(0, pos, this.getWidth(), pos, paint);
        }
    }

    public void setSolidPathPosArray(ArrayList<Integer> sppa) {
        alSolidPathPos = sppa;
    }

    public void setDashLinePathArray(ArrayList<Integer> dppa) {
        alSolidPathPos = dppa;
    }
}
