package com.kania.todostack2.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kania.todostack2.R;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.provider.TodoListWidgetProvider;
import com.kania.todostack2.provider.TodoStackSettingValues;
import com.kania.todostack2.provider.ColorProvider;
import com.kania.todostack2.provider.TodoLayoutInfoProvider;
import com.kania.todostack2.provider.TodoProvider;
import com.kania.todostack2.util.TodoStackUtil;
import com.kania.todostack2.view.TodoLayout;
import com.kania.todostack2.view.TodoViewInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by user on 2016-05-07.
 */
public class TodoStackAdapter {

    public final String SUBJECT_ADD_PREFIX = "+";

    private Context mContext;
    private Resources mRes;
    private TodoProvider mProvider;
    private TodoStackSettingValues mSettings;
    private TodoLayout mTodoLayout;
    private View.OnClickListener mClickListener;
    private View.OnLongClickListener mLongClickListener;

    private TodoLayoutInfoProvider mLayoutInfoProvier;

    public TodoStackAdapter(Context context, TodoLayout todoLayout,
                            View.OnClickListener clickListener,
                            View.OnLongClickListener longClickListener) {
        mContext = context;
        mRes = mContext.getResources();
        mTodoLayout = todoLayout;
        mClickListener = clickListener;
        mLongClickListener = longClickListener;

        mProvider = TodoProvider.getInstance(mContext.getApplicationContext());
        mSettings = TodoStackSettingValues.getInstance(mContext.getApplicationContext());
        //debug
        Log.d("TodoStack", "TodoStackAdapter : width = "
                + mTodoLayout.getWidth() + "height = " + mTodoLayout.getHeight());

        resetLayoutSize(mTodoLayout.getWidth(), mTodoLayout.getHeight());
    }

    public void resetLayoutSize(int width, int height) {
        Log.d("TodoStack", "TodoStackAdapter reset : width = "
                + mTodoLayout.getWidth() + "height = " + mTodoLayout.getHeight());
        if (mProvider.getSubjectCount() > 0) {
            mLayoutInfoProvier = new TodoLayoutInfoProvider(width, height,
                    mProvider.getSubjectCount(),
                    mSettings.getVisivleTaskCount(),
                    mSettings.getVisivleDateCount(),
                    mSettings.getVisivleDelayedCount());
        } else {
            mLayoutInfoProvier = null;
        }
    }

    public void notifyDataSetChanged() {
        Log.d("TodoStack", "notifyDataSetChanged : mLayoutInfoProvier = "
                + mLayoutInfoProvier);
        mTodoLayout.removeAllViews();
        if (mLayoutInfoProvier != null) {
            addDateTextData(mTodoLayout);
            addSubjectData(mTodoLayout);
            addTodoData(mTodoLayout);
        }
        mTodoLayout.invalidate();

        //TODO navi
        //for navigation drawer
//        sendNavSubjectItem();

        //TODO widget
        //for widget (import from TodoStack1)

        Intent intent = new Intent();
        intent.setAction(TodoListWidgetProvider.WIDGET_UPDATE_ACTION);
        mContext.sendBroadcast(intent);
    }

    private void addDateTextData(TodoLayout todoLayout) {
        int dateCount = mSettings.getVisivleDateCount();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < dateCount; ++i) {
            TextView tv = new TextView(mContext);
            tv.setIncludeFontPadding(false);
            tv.setText(TodoStackUtil.getFomatedDateWithoutYear(mContext, calendar.getTime()));
            tv.setTextColor(mRes.getColor(R.color.color_black));
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundColor(mRes.getColor(R.color.color_date_text_background));
            TodoViewInfo info = mLayoutInfoProvier.getDateTextPosition(i);
            info.type = TodoViewInfo.TYPE_DATE_TEXT;
            tv.setTag(info);

            todoLayout.addView(tv);

            calendar.add(Calendar.DATE, 1);
        }
    }

    private void addSubjectData(TodoLayout todoLayout) {
        ArrayList<SubjectData> subData = mProvider.getAllSubject();

        for(SubjectData sd : subData) {
            //before send TodoDatas, initialize counts
            sd.taskCount = 0;
            sd.dateTodoCount = 0;
            sd.delayedTodoCount = 0;

            TextView tv = new TextView(mContext);
            tv.setIncludeFontPadding(false);
            tv.setText(SUBJECT_ADD_PREFIX + sd.subjectName);
            tv.setTextColor(sd.color);
            tv.setBackgroundColor(mRes.getColor(R.color.color_subject_background));
            tv.setSingleLine();
            TodoViewInfo info = mLayoutInfoProvier.getSubjectPosition(sd.order);
            info.type = TodoViewInfo.TYPE_SUBJECT;
            info.id = sd.order + "";
            tv.setTag(info);
            tv.setOnClickListener(mClickListener);
            tv.setOnLongClickListener(mLongClickListener);

            todoLayout.addView(tv);

            //debug
//            Log.d("TodoStack", "[addSubjectData] subject id = " + sd.order);
        }
    }
    private void addTodoData(TodoLayout todoLayout) {
        ArrayList<TodoData> todoData = mProvider.getAllTodo();
        Calendar calendarToday = Calendar.getInstance();

        //for duplicate date todos
        HashMap<String, TodoViewInfo> dateTodoMap = new HashMap<String, TodoViewInfo>();

        for(TodoData td : todoData) {
            SubjectData subjectdata = mProvider.getSubjectByOrder(td.subjectOrder);
            if (td.type == TodoData.TODO_DB_TYPE_TASK) {
                subjectdata.taskCount++;
                if (subjectdata.taskCount < mSettings.getVisivleTaskCount()) {
                    TextView tv = getTaskTodoTextView(td, subjectdata);
                    todoLayout.addView(tv);
                } else if (subjectdata.taskCount == mSettings.getVisivleTaskCount()) {
                    TextView moreTv = getMoreTaskTextView(subjectdata);
                    todoLayout.addView(moreTv);
                } else {
                    continue;
                }

            } else {
                int cmpDiffDays = mSettings.getVisivleDateCount() + 1;
                Calendar targetCalendar = Calendar.getInstance();
                targetCalendar.setTime(new Date(td.date));
                cmpDiffDays = TodoStackUtil.campareDate(targetCalendar, calendarToday);

                if (cmpDiffDays < 0) { //delayed
                    subjectdata.delayedTodoCount++;
                    if (subjectdata.delayedTodoCount < mSettings.getVisivleDelayedCount()) {
                        TextView tv = getDelayedTodoTextView(td, subjectdata);
                        todoLayout.addView(tv);
                    } else if (subjectdata.delayedTodoCount == mSettings.getVisivleDelayedCount()) {
                        TextView moreTv = getMoreDelayedTextView(subjectdata);
                        todoLayout.addView(moreTv);
                    } else {
                        continue;
                    }
                }
                else if (cmpDiffDays >= 0
                        && cmpDiffDays < mSettings.getVisivleDateCount()) { //ranged
                    String key = "" + subjectdata.order + td.date;
                    //first, check if todos on target date exist.
                    TodoViewInfo info = dateTodoMap.get(key);
                    if (info == null) {
                        info = mLayoutInfoProvier.getDateTodoPosition(
                                subjectdata.order, cmpDiffDays);
                        info.type = TodoViewInfo.TYPE_DATE_TODO;
                        info.id = td.id + "";
                        if (cmpDiffDays == 0) { //today
                            info.isToday = true;
                        } else {
                            info.isToday = false;
                        }
                    } else {
                        info.id += TodoViewInfo.DELIMITER_ID + td.id;
                    }
                    dateTodoMap.put(key, info);
                } else {
                    continue;
                }
            }
        }
        //post make textview of date
        Iterator<String> it = dateTodoMap.keySet().iterator();
        while(it.hasNext()) {
            TodoViewInfo combinedInfo = dateTodoMap.get(it.next());
            int subjectColor = 0;
            String ids[] = combinedInfo.id.split(TodoViewInfo.DELIMITER_ID);
            String combinedText = "";
            for (int i = 0; i < ids.length; ++i) {
                if (i == 0) {
                    TodoData firstTodoData = mProvider.getTodoById(Integer.parseInt(ids[i]));
                    combinedText = firstTodoData.todoName;
                    if (combinedInfo.isToday) {
                        subjectColor = ColorProvider.getInstance().getTodayColor();
                    } else {
                        subjectColor = mProvider.getSubjectByOrder(firstTodoData.subjectOrder).color;
                    }
                } else {
                    combinedText += TodoViewInfo.DELIMITER_ID +
                            mProvider.getTodoById(Integer.parseInt(ids[i])).todoName;
                }
            }
            TextView tv = getCommonTodoTextView(combinedText, subjectColor);
            tv.setTag(combinedInfo);
            todoLayout.addView(tv);
        }
    }

    private TextView getTaskTodoTextView(TodoData td, SubjectData sd) {
        TextView tv = getCommonTodoTextView(td.todoName, sd.color);
        TodoViewInfo info = mLayoutInfoProvier.getTaskTodoPosition(sd.order, sd.taskCount);
        info.type = TodoViewInfo.TYPE_TASK;
        info.id = td.id + "";
        tv.setTag(info);

        return tv;
    }

    private TextView getDelayedTodoTextView(TodoData td, SubjectData sd) {
        TextView tv = getCommonTodoTextView(td.todoName, sd.color);
        TodoViewInfo info = mLayoutInfoProvier.
                getDelayedTodoPosition(sd.order, sd.delayedTodoCount);
        info.type = TodoViewInfo.TYPE_DELAYED_TODO;
        info.id = td.id + "";
        tv.setTag(info);

        return tv;
    }

    private TextView getMoreTaskTextView(SubjectData sd) {
        TextView moreTaskTextView = getMoreCommonTextViwe(sd);
        TodoViewInfo info = mLayoutInfoProvier.getTaskTodoPosition(sd.order, sd.taskCount);
        info.type = TodoViewInfo.TYPE_VIEW_ALL_TASK;
        info.id = sd.order + "";
        moreTaskTextView.setTag(info);

        return moreTaskTextView;
    }

    private TextView getMoreDelayedTextView(SubjectData sd) {
        TextView moreDelayedTextView = getMoreCommonTextViwe(sd);
        TodoViewInfo info = mLayoutInfoProvier.getDelayedTodoPosition(sd.order, sd.delayedTodoCount);
        info.type = TodoViewInfo.TYPE_VIEW_ALL_DELAYED_TODO;
        info.id = sd.order + "";
        moreDelayedTextView.setTag(info);

        return moreDelayedTextView;
    }

    private TextView getMoreCommonTextViwe(SubjectData sd) {
        TextView moreTextView;
        String moreString = mRes.getString(R.string.todo_view_more);
        int moreColor = Color.argb(Color.alpha(sd.color) / 2,
                Color.red(sd.color),
                Color.green(sd.color),
                Color.blue(sd.color));
        moreTextView = getCommonTodoTextView(moreString, moreColor);

        return moreTextView;
    }

    private TextView getCommonTodoTextView(String todoName, int subjectColor) {
        TextView tv = new TextView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(params);
        tv.setIncludeFontPadding(false);
        tv.setSingleLine();
        tv.setText(todoName);
        tv.setTextColor(mRes.getColor(R.color.color_todo_text));
        tv.setBackgroundColor(subjectColor);
        tv.setOnClickListener(mClickListener);
        tv.setOnLongClickListener(mLongClickListener);

        return tv;
    }
}
