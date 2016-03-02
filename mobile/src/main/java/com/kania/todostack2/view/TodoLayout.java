package com.kania.todostack2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
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

    public static final int RATE_DATE_TEXT_SCALE = 8;
    public static final int RATE_TODO_TEXT_SCALE = 9;
    private ArrayList<Integer> alSolidPathPos;
    private ArrayList<Integer> alDashPathPos;

    private int mostHighTopPos = 10000; //for solid line (top has minimum window position)

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

        addSolidPathPosArray(mostHighTopPos);
    }

    private void setLayoutFromTag(TextView tv) {
        TodoViewInfo pos = (TodoViewInfo) tv.getTag();
        if (pos == null)
            return;
//        Log.d("TodoStack", "[setLayoutFromTag] l/t/r/b = "
//                + pos.left + "/" + pos.top + "/" + pos.right + "/" + pos.bottom);
        tv.layout(pos.left, pos.top, pos.right, pos.bottom);
        if (pos.type == TodoViewInfo.TYPE_DATE_TEXT) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    ((pos.bottom - pos.top) * RATE_DATE_TEXT_SCALE) / 10 );
        } else {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    ((pos.bottom - pos.top) * RATE_TODO_TEXT_SCALE) / 10 );
        }


        if (pos.type == TodoViewInfo.TYPE_DATE_TEXT) {
            addDashLinePathArray(pos.top);
            if (pos.top < mostHighTopPos){
                mostHighTopPos = pos.top;
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint = new Paint();

        int dashLinePaddingTop = TodoLayoutInfo.DATEDIVIDERLINE_HEIGHT / 2 + 1;
        int solidLinePaddingTop = TodoLayoutInfo.TASKBASELINE_HEIGHT / 2 + 1;

        //dash path
        paint.setColor(ColorProvider.getColor(ColorProvider.COLOR_DASH_PATH));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{2, 5}, 2));
        paint.setStrokeWidth(1);
        for (int pos : alDashPathPos) {
            if (pos != mostHighTopPos) {
                Path path = new Path();
                path.moveTo(0, pos - dashLinePaddingTop);
                path.lineTo(this.getWidth(), pos - dashLinePaddingTop);
                canvas.drawPath(path, paint);
            }
        }

        //soild path
        paint.setColor(ColorProvider.getColor(ColorProvider.COLOR_SOLID_PATH));
        paint.setPathEffect(null);
        paint.setStrokeWidth(2);
        for (int pos : alSolidPathPos) {
            Path path = new Path();
            path.moveTo(0, pos - solidLinePaddingTop);
            path.lineTo(this.getWidth(), pos - solidLinePaddingTop);
            canvas.drawPath(path, paint);
        }

        //draw child above todoview
        super.dispatchDraw(canvas);
    }

    public void addSolidPathPosArray(int line) {
        alSolidPathPos.add(line);
    }

    public void addDashLinePathArray(int line) {
        alDashPathPos.add(line);
    }
}
