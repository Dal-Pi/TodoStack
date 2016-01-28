package com.kania.todostack2.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kania.todostack2.R;
import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.data.TodoStackSettingValues;
import com.kania.todostack2.provider.ColorProvider;
import com.kania.todostack2.provider.TodoProvider;
import com.kania.todostack2.view.IViewAction;
import com.kania.todostack2.view.TodoLayoutInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 2016-01-20.
 */
public class TodoStackPresenter implements IControllerMediator {
    private Context mContext;
    private TodoLayoutInfo mTodolayoutInfo;

    private Resources res;

    private IViewAction mTodoView;
    private int mViewMode;

    private boolean mIsFabTop = false;

    public TodoStackPresenter(Context context) {
        mContext = context;
        mViewMode = MODE_NO_SELECTION;

        res = mContext.getResources();
    }

    @Override
    public void setMediator(IViewAction mediator) {
        mTodoView = mediator;
    }

    @Override
    public void initTodoLayout(int layoutWidth, int layoutHeight) {
        Log.d("TodoStack", "[initTodoLayout] width/height = " + layoutWidth + "/" + layoutHeight);
        TodoProvider provider = TodoProvider.getInstance(mContext);

        if (provider.getSubjectCount() > 0) {
            refreshTodoLayout(layoutWidth, layoutHeight);
            setMode(MODE_NO_SELECTION);
        } else {
            setMode(MODE_INITIAL_SETUP);
        }
    }

    @Override
    public void refreshTodoLayout(int layoutWidth, int layoutHeight) {
        TodoProvider provider = TodoProvider.getInstance(mContext);
        if (mTodolayoutInfo == null) {
            mTodolayoutInfo = new TodoLayoutInfo(layoutWidth, layoutHeight,
                    provider.getSubjectCount(),
                    TodoStackSettingValues.getVisivleTaskCount(),
                    TodoStackSettingValues.getVisivleDateCount(),
                    TodoStackSettingValues.getVisivleDelayedCount());
        } else {
            mTodolayoutInfo.refreshEachViewSize(provider.getSubjectCount(),
                    TodoStackSettingValues.getVisivleTaskCount(),
                    TodoStackSettingValues.getVisivleDateCount(),
                    TodoStackSettingValues.getVisivleDelayedCount());
        }
    }

    /**
     * refer comment on interface that is step to set mode.
     * @param targetMode intented mode, in this method, mViewMode mean present mode
     */
    @Override
    public void setMode(int targetMode) {
        boolean needAnimation = false;
        switch (targetMode) {
            case MODE_INITIAL_SETUP:
                needAnimation = isFabTop();
                mTodoView.setAllControllerGone();
                mTodoView.setFabToBase(res.getString(R.string.fab_create_subject),
                        res.getColor(R.color.color_normal_state), needAnimation);
                mIsFabTop = false;
                mTodoView.setGuideText(res.getString(R.string.guide_text_mode_initial_setup));
                break;
            case MODE_NO_SELECTION:
                needAnimation = isFabTop();
                //set data to TodoLayout
                sendDataToTodoViewAfterConverting();
                mTodoView.setAllControllerGone();
                mTodoView.setFabToBase(res.getString(R.string.fab_create_todo),
                        res.getColor(R.color.color_normal_state), needAnimation);
                mIsFabTop = false;
                mTodoView.setGuideText(res.getString(R.string.guide_text_mode_no_selection));
                break;
            case MODE_ADD_TODO:
                needAnimation = !isFabTop();
                mTodoView.setInputTodoVisible();
                mTodoView.setFabToInputTodo(res.getString(R.string.fab_add),
                        res.getColor(R.color.color_normal_state), needAnimation);
                mIsFabTop = true;
                mTodoView.setGuideText(res.getString(R.string.guide_text_mode_input_todo));
                break;
            case MODE_ADD_SUBJECT:
                needAnimation = !isFabTop();
                mTodoView.setInputSubjectVisible();
                mTodoView.setFabToInputSubject(res.getString(R.string.fab_add),
                        res.getColor(R.color.color_normal_state), needAnimation);
                mIsFabTop = true;
                mTodoView.setGuideText(res.getString(R.string.guide_text_mode_input_subject));
                break;
            case MODE_VIEW_TODO_ONELINE:
            case MODE_VIEW_TODO_TWOLINE:
            case MODE_VIEW_TODO_THREELINE:
                break;
            case MODE_VIEW_SUBJECT:
                break;
            default:
                break;
        }
        mViewMode = targetMode;
        Log.d("TodoStack", "Now Mode = " + printMode(mViewMode));
    }

    private boolean isFabTop() {
        return mIsFabTop;
    }

    private void sendDataToTodoViewAfterConverting() {
        mTodoView.clearTodoLayout();
        sendDateTextData();
        sendSubjectData();
        sendTodoData();
        mTodoView.refreshTodoLayout();
    }

    private void sendDateTextData() {
        ArrayList<TextView> sendData = new ArrayList<TextView>();
        int dateCount = TodoStackSettingValues.getInstance(mContext).getVisivleDateCount();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < dateCount; ++i) {
            TextView tv = new TextView(mContext);
            tv.setIncludeFontPadding(false);
            tv.setText(getFormatedDateTextFromDate(calendar.getTime()));
            tv.setTextColor(ColorProvider.getInstance().getDefaultColor());
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundColor(res.getColor(R.color.color_date_text_background));
            tv.setTag(mTodolayoutInfo.getDateTextPosition(i));

            sendData.add(tv);
            calendar.add(Calendar.DATE, 1);
        }

        mTodoView.setTextViewOnTodoLayout(sendData);
    }

    private String getFormatedDateTextFromDate(Date date) {
        SimpleDateFormat month = new SimpleDateFormat("M");
        SimpleDateFormat day = new SimpleDateFormat("d");
        return String.format("%2s-%2s", month.format(date), day.format(date));
    }
    private void sendSubjectData() {
        ArrayList<TextView> sendData = new ArrayList<TextView>();
        ArrayList<SubjectData> subData = TodoProvider.getInstance(mContext).getAllSubject();

        for(SubjectData sd : subData) {
            //before send TodoDatas, initialize counts
            sd.taskCount = 0;
            sd.dateTodoCount = 0;
            sd.delayedTodoCount = 0;

            TextView tv = new TextView(mContext);
            tv.setIncludeFontPadding(false);
            tv.setText(sd.subjectName);
            tv.setTextColor(sd.color);
            tv.setBackgroundColor(res.getColor(R.color.color_subject_background));
            tv.setTag(mTodolayoutInfo.getSubjectPosition(sd.order));

            sendData.add(tv);

            //debug
            Log.d("TodoStack", "[sendSubjectData] subject id = " + sd.order);
        }
        mTodoView.setTextViewOnTodoLayout(sendData);
    }
    private void sendTodoData() {
        ArrayList<TextView> sendData = new ArrayList<TextView>();
        ArrayList<TodoData> todoData = TodoProvider.getInstance(mContext).getAllTodo();
        TodoProvider todoProvider = TodoProvider.getInstance(mContext);
        SimpleDateFormat sdf = new SimpleDateFormat(TodoStackContract.TodoEntry.DATAFORMAT_DATE);
        Calendar calendarToday = Calendar.getInstance();
        TodoStackSettingValues settingValues = TodoStackSettingValues.getInstance(mContext);

        for(TodoData td : todoData) {
            SubjectData subjectdata = todoProvider.getSubjectById(td.subjectId);
            if (subjectdata == null) {
                Log.e("TodoStack", "[sendTodoData] subjectdata is null!");
                continue;
            } else {
                TextView tv = null;
                if (td.type == TodoData.TODO_DB_TYPE_TASK) {
                    subjectdata.taskCount++;
                    if (subjectdata.taskCount < settingValues.getVisivleTaskCount()) {
                        tv = getTaskTodoTextView(td, subjectdata);
                    } else if (subjectdata.taskCount == settingValues.getVisivleTaskCount()) {
                        //TODO more option
                    } else {
                        continue;
                    }
                } else {
                    int cmpDiffDays = settingValues.getVisivleDateCount() + 1;
                    Calendar targetCalendar = Calendar.getInstance();
                    try {
                        targetCalendar.setTime(sdf.parse(td.date));
                        cmpDiffDays = campareDate(targetCalendar, calendarToday);
                    } catch (ParseException e) {
                        Log.e("TodoStack", "[sendTodoData] parse error!!");
                        e.printStackTrace();
                    }

                    if (cmpDiffDays < 0) { //delayed
                        subjectdata.delayedTodoCount++;
                        if (subjectdata.delayedTodoCount < settingValues.getVisivleDelayedCount()) {
                            tv = getDelayedTodoTextView(td, subjectdata);
                        } else if (subjectdata.delayedTodoCount == settingValues.getVisivleDelayedCount()) {
                            //TODO more option
                        } else {
                            continue;
                        }
                    }
                    else if (cmpDiffDays >= 0
                            && cmpDiffDays < settingValues.getVisivleDateCount()) { //ranged
                        Log.d("TodoStack", "[sendTodoData] delyed case, cmpDiffDays of "
                                + td.todoName + " = " + cmpDiffDays);
                        subjectdata.dateTodoCount++;
                        tv = getDateTodoTextView(td, subjectdata, cmpDiffDays);
                        if (cmpDiffDays == 0) { //today
                            tv.setBackgroundColor(ColorProvider.getInstance().getTodayColor());
                        }
                    } else {
                        continue;
                    }
                }
                if (tv != null)
                    sendData.add(tv);
            }
        }
        mTodoView.setTextViewOnTodoLayout(sendData);
    }

    private TextView getTaskTodoTextView(TodoData td, SubjectData sd) {
        TextView tv = getCommonTodoTextView(td, sd);
        tv.setTag(mTodolayoutInfo.getTaskTodoPosition(sd.order, sd.taskCount));

        return tv;
    }

    private TextView getDateTodoTextView(TodoData td, SubjectData sd, int diffDays) {
        TextView tv = getCommonTodoTextView(td, sd);
        tv.setTag(mTodolayoutInfo.getDateTodoPosition(sd.order, diffDays));

        return tv;
    }

    private TextView getDelayedTodoTextView(TodoData td, SubjectData sd) {
        TextView tv = getCommonTodoTextView(td, sd);
        tv.setTag(mTodolayoutInfo.getDelayedTodoPosition(sd.order, sd.delayedTodoCount));

        return tv;
    }

    private TextView getCommonTodoTextView(TodoData td, SubjectData sd) {
        TextView tv = new TextView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(params);
        tv.setIncludeFontPadding(false);
        tv.setText(td.todoName);
        tv.setTextColor(res.getColor(R.color.color_todo_text));
        tv.setBackgroundColor(sd.color);

        return tv;
    }

    /**
     *
     * @param target
     * @param today
     * @param range -1:delayed, 0:today, 1:ranged, 2:out of ranged
     * @return
     */
    private int campareDate(Calendar target, Calendar today) {
        int diffDays;
        target.set(Calendar.HOUR_OF_DAY, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        diffDays = (int) ((target.getTimeInMillis() - today.getTimeInMillis())
                / (1000 * 60 * 60 * 24));

        return diffDays;
    }

    @Override
    public void clickBackPressSoftButton() {
        if (mViewMode != MODE_INITIAL_SETUP && mViewMode != MODE_NO_SELECTION) {
            //any mode
            if (TodoProvider.getSubjectCount() > 0) {
                setMode(MODE_NO_SELECTION);
            } else {
                setMode(MODE_INITIAL_SETUP);
            }
        } else {
            mTodoView.finishActivity();
        }
    }

    @Override
    public void clickFloatingActionButton(Bundle bundle) {
        switch (mViewMode) {
            case MODE_INITIAL_SETUP:
                setMode(MODE_ADD_SUBJECT);
                break;
            case MODE_NO_SELECTION:
                //for debug
//                setMode(MODE_ADD_TODO);
                setMode(MODE_ADD_SUBJECT);
                break;
            case MODE_ADD_TODO:
                //TODO request add todo using asynctask
                break;
            case MODE_ADD_SUBJECT:
                // request add subject using asynctask
                insertSubject(bundle);
                break;
            case MODE_VIEW_TODO_ONELINE:
            case MODE_VIEW_TODO_TWOLINE:
            case MODE_VIEW_TODO_THREELINE:
                break;
            case MODE_VIEW_SUBJECT:
                break;
            default:
                break;
        }
    }

    private void insertSubject(Bundle bundle) {
        UpdateSubjectTask insertSubjectTask =
                new UpdateSubjectTask(mContext, new UpdateSubjectTask.TaskEndCallback() {
                    @Override
                    public void loadFinished() {
                        LoadingTodoTask refreshTask = new LoadingTodoTask(
                                mContext, new LoadingTodoTask.TaskEndCallback() {
                            @Override
                            public void loadFinished() {
                                setMode(MODE_NO_SELECTION);
                            }
                        });
                        refreshTask.execute();
                    }
                });
        insertSubjectTask.setData(
                makeSubjectData(bundle), UpdateSubjectTask.SUBJECT_TASK_ADD_SUBJECT);
        insertSubjectTask.execute();
    }



    private SubjectData makeSubjectData(Bundle bundle) {
        SubjectData subject = new SubjectData();
        subject.subjectName = bundle.getString(TodoStackContract.SubjectEntry.SUBJECT_NAME);
        subject.color = bundle.getInt(TodoStackContract.SubjectEntry.COLOR);
        subject.order = TodoProvider.getInstance(mContext).getSubjectCount();

        return subject;
    }

    //for debug
    public String printMode(int mode) {
        String ret;
        switch (mode) {
            case MODE_INITIAL_SETUP:
                ret = "MODE_INITIAL_SETUP"; break;
            case MODE_NO_SELECTION:
                ret = "MODE_NO_SELECTION"; break;
            case MODE_ADD_TODO:
                ret = "MODE_ADD_TODO"; break;
            case MODE_ADD_SUBJECT:
                ret = "MODE_ADD_SUBJECT"; break;
            case MODE_VIEW_TODO_ONELINE:
                ret = "MODE_VIEW_TODO_ONELINE"; break;
            case MODE_VIEW_TODO_TWOLINE:
                ret = "MODE_VIEW_TODO_TWOLINE"; break;
            case MODE_VIEW_TODO_THREELINE:
                ret = "MODE_VIEW_TODO_THREELINE"; break;
            case MODE_VIEW_SUBJECT:
                ret = "MODE_VIEW_SUBJECT"; break;
            default:
                ret = "UNKNOWN_MODE!!!"; break;
        }
        return ret;
    }
}
