package com.kania.todostack2.presenter;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kania.todostack2.R;
import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.data.TodoStackSettingValues;
import com.kania.todostack2.provider.ColorProvider;
import com.kania.todostack2.provider.TodoProvider;
import com.kania.todostack2.view.IViewAction;
import com.kania.todostack2.view.TextViewInfo;
import com.kania.todostack2.view.TodoLayoutInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by user on 2016-01-20.
 */
public class TodoStackPresenter implements IControllerMediator, View.OnClickListener {
    public static final int NOT_SELECTED_SUBJECT = -1;

    public static final String TAG_DIALOG_SELECT_SUBJECT = "select_subject";
    private Context mContext;
    private TodoLayoutInfo mTodolayoutInfo;

    private Resources res;

    private IViewAction mTodoView;
    private int mViewMode;

    private int mNowSelectSubjectOrder = NOT_SELECTED_SUBJECT;
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
                mNowSelectSubjectOrder = NOT_SELECTED_SUBJECT;
                mTodoView.setActionBarText(res.getString(R.string.app_name),
                        res.getColor(R.color.colorAccent));
                needAnimation = isFabTop();
                mTodoView.setAllControllerGone();
                mTodoView.setFabToBase(res.getString(R.string.fab_create_subject),
                        res.getColor(R.color.color_normal_state), needAnimation);
                mIsFabTop = false;
                mTodoView.setGuideText(res.getString(R.string.guide_text_mode_initial_setup));
                break;
            case MODE_NO_SELECTION:
                mNowSelectSubjectOrder = NOT_SELECTED_SUBJECT;
                mTodoView.setActionBarText(res.getString(R.string.app_name),
                        res.getColor(R.color.color_normal_state));
                needAnimation = isFabTop();
                //set data to TodoLayout
                sendDataToTodoViewAfterConverting();
                mTodoView.setAllControllerGone();
                mTodoView.setFabToBase(res.getString(R.string.fab_create_todo),
                        res.getColor(R.color.color_normal_state), needAnimation);
                mIsFabTop = false;
                mTodoView.setGuideText(res.getString(R.string.guide_text_suggest_select_subject));
                break;
            case MODE_ADD_TODO:
                TodoProvider provider = TodoProvider.getInstance(mContext);
                int targetSubjectColor;
                String subjectName = "";
                if (mNowSelectSubjectOrder == NOT_SELECTED_SUBJECT) {
                    targetSubjectColor = res.getColor(R.color.color_normal_state);
                    mTodoView.setActionBarText(
                            res.getString(R.string.adding_text_new_todo), targetSubjectColor);
                } else {
                    SubjectData subject = provider.getSubjectByOrder(mNowSelectSubjectOrder);
                    targetSubjectColor = subject.color;
                    subjectName = subject.subjectName;
                    mTodoView.setActionBarText(
                            res.getString(R.string.adding_text_on_new_todo) + " " + subjectName,
                            targetSubjectColor);
                }
                needAnimation = !isFabTop();
                mTodoView.setInputTodoVisible(targetSubjectColor);
                mTodoView.setFabToInputTodo(res.getString(R.string.fab_add), targetSubjectColor,
                        needAnimation);
                mIsFabTop = true;
                mTodoView.setGuideText(res.getString(R.string.guide_text_mode_input_todo));
                break;
            case MODE_ADD_SUBJECT:
                mTodoView.setActionBarText(res.getString(R.string.title_text_on_new_subject),
                        res.getColor(R.color.colorAccent));
                needAnimation = !isFabTop();
                mTodoView.setInputSubjectVisible();
                mTodoView.setFabToInputSubject(res.getString(R.string.fab_add),
                        res.getColor(R.color.colorAccent), needAnimation);
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

        mTodoView.setTextViewsOnTodoLayout(sendData);
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
            TextViewInfo info = mTodolayoutInfo.getSubjectPosition(sd.order);
            info.type = TextViewInfo.TYPE_SUBJECT;
            info.id = sd.order + "";
            tv.setTag(info);
            tv.setOnClickListener(this);

            sendData.add(tv);

            //debug
            Log.d("TodoStack", "[sendSubjectData] subject id = " + sd.order);
        }
        mTodoView.setTextViewsOnTodoLayout(sendData);
    }
    private void sendTodoData() {
        ArrayList<TextView> sendData = new ArrayList<TextView>();
        ArrayList<TodoData> todoData = TodoProvider.getInstance(mContext).getAllTodo();
        TodoProvider todoProvider = TodoProvider.getInstance(mContext);
        SimpleDateFormat sdf = new SimpleDateFormat(TodoStackContract.TodoEntry.DATAFORMAT_DATE);
        Calendar calendarToday = Calendar.getInstance();
        TodoStackSettingValues settingValues = TodoStackSettingValues.getInstance(mContext);

        //for duplicate date todos
        HashMap<String, TextViewInfo> dateTodoMap = new HashMap<String, TextViewInfo>();

        for(TodoData td : todoData) {
            SubjectData subjectdata = todoProvider.getSubjectByOrder(td.subjectOrder);
            if (subjectdata == null) {
                Log.e("TodoStack", "[sendTodoData] subjectdata is null!");
                continue;
            } else {
                if (td.type == TodoData.TODO_DB_TYPE_TASK) {
                    subjectdata.taskCount++;
                    if (subjectdata.taskCount < settingValues.getVisivleTaskCount()) {
                        TextView tv = getTaskTodoTextView(td, subjectdata);
                        sendData.add(tv);
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
                            TextView tv = getDelayedTodoTextView(td, subjectdata);
                            sendData.add(tv);
                        } else if (subjectdata.delayedTodoCount == settingValues.getVisivleDelayedCount()) {
                            //TODO more option
                        } else {
                            continue;
                        }
                    }
                    else if (cmpDiffDays >= 0
                            && cmpDiffDays < settingValues.getVisivleDateCount()) { //ranged
                        String key = td.date + subjectdata.order;
                        //first, check if todos on target date exist.
                        TextViewInfo info = dateTodoMap.get(key);
                        if (info == null) {
                            info = mTodolayoutInfo.getDateTodoPosition(
                                    subjectdata.order, cmpDiffDays);
                            info.type = TextViewInfo.TYPE_DATE_TODO;
                            info.id = td.id + "";
                        } else {
                            info.id += TextViewInfo.DELIMITER_ID + td.id;
                        }
                        dateTodoMap.put(key, info);
                    } else {
                        continue;
                    }
                }
            }
        }
        //post make textview of date
        Iterator<String> it = dateTodoMap.keySet().iterator();
        while(it.hasNext()) {
            TextViewInfo combinedInfo = dateTodoMap.get(it.next());
            int subjectColor = 0;
            String ids[] = combinedInfo.id.split(TextViewInfo.DELIMITER_ID);
            String combinedText = "";
            for (int i = 0; i < ids.length; ++i) {
                if (i == 0) {
                    TodoData firstTodoData = todoProvider.getTodoById(Integer.parseInt(ids[i]));
                    combinedText = firstTodoData.todoName;
                    subjectColor = todoProvider.getSubjectByOrder(firstTodoData.subjectOrder).color;
                } else {
                    combinedText += TextViewInfo.DELIMITER_ID +
                            todoProvider.getTodoById(Integer.parseInt(ids[i])).todoName;
                }
            }
            TextView tv = getCommonTodoTextView(combinedText, subjectColor);
            tv.setTag(combinedInfo);
            sendData.add(tv);
        }
        mTodoView.setTextViewsOnTodoLayout(sendData);
    }

    private TextView getTaskTodoTextView(TodoData td, SubjectData sd) {
        TextView tv = getCommonTodoTextView(td.todoName, sd.color);
        TextViewInfo info = mTodolayoutInfo.getTaskTodoPosition(sd.order, sd.taskCount);
        info.type = TextViewInfo.TYPE_TASK_TODO;
        info.id = td.id + "";
        tv.setTag(info);

        return tv;
    }

//    private TextView getDateTodoTextView(TodoData td, SubjectData sd, int diffDays) {
//        TextView tv = getCommonTodoTextView(td, sd);
//        TextViewInfo info = mTodolayoutInfo.getDateTodoPosition(sd.order, diffDays);
//        info.type = TextViewInfo.TYPE_DATE_TODO;
//        info.id = td.id;
//        tv.setTag(info);
//
//        return tv;
//    }
//
    private TextView getDelayedTodoTextView(TodoData td, SubjectData sd) {
        TextView tv = getCommonTodoTextView(td.todoName, sd.color);
        TextViewInfo info = mTodolayoutInfo.getDelayedTodoPosition(sd.order, sd.delayedTodoCount);
        info.type = TextViewInfo.TYPE_DATE_TODO;
        info.id = td.id + "";
        tv.setTag(info);

        return tv;
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
        tv.setTextColor(res.getColor(R.color.color_todo_text));
        tv.setBackgroundColor(subjectColor);

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
                if (mNowSelectSubjectOrder == NOT_SELECTED_SUBJECT) {
                    showSubjectSelectDialog(new SelectSubjectDialog.Callback() {
                        @Override
                        public void onSelectSubject(int order) {
                            mNowSelectSubjectOrder = order;
                            setMode(MODE_ADD_TODO);
                        }
                    });
                } else { //it never used
                    setMode(MODE_ADD_TODO);
                }
                break;
            case MODE_ADD_TODO:
                //request add todo using asynctask
                //TODO check subject is verified
                if (mNowSelectSubjectOrder == NOT_SELECTED_SUBJECT) {
                    Toast.makeText(mContext,
                            res.getText(R.string.guide_text_suggest_select_subject),
                            Toast.LENGTH_SHORT).show();
                } else {
                    insertTodo(bundle);
                }
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

    private void insertTodo(Bundle bundle) {
        UpdateTodoTask insertTodoTask =
                new UpdateTodoTask(mContext, new UpdateTodoTask.TaskEndCallback() {
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
        insertTodoTask.setData(
                makeTodoData(bundle), UpdateTodoTask.TODO_TASK_ADD_TODO);
        insertTodoTask.execute();
    }

    private SubjectData makeSubjectData(Bundle bundle) {
        SubjectData subject = new SubjectData();
        subject.subjectName = bundle.getString(TodoStackContract.SubjectEntry.SUBJECT_NAME);
        subject.color = bundle.getInt(TodoStackContract.SubjectEntry.COLOR);
        subject.order = TodoProvider.getInstance(mContext).getSubjectCount();

        return subject;
    }

    private TodoData makeTodoData(Bundle bundle) {
        TodoData todo = new TodoData();
        todo.todoName = bundle.getString(TodoStackContract.TodoEntry.TODO_NAME);
        todo.subjectOrder = mNowSelectSubjectOrder;
        todo.date = bundle.getString(TodoStackContract.TodoEntry.DATE);
        todo.type = bundle.getInt(TodoStackContract.TodoEntry.TYPE);
        todo.timeFrom = bundle.getString(TodoStackContract.TodoEntry.TIME_FROM);
        todo.timeTo = bundle.getString(TodoStackContract.TodoEntry.TIME_TO);
        todo.location = bundle.getString(TodoStackContract.TodoEntry.LOCATION);

        return todo;
    }

    @Override
    public void selectMenuAddSubject() {
        setMode(MODE_ADD_SUBJECT);
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


    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof TextViewInfo) {
            if (((TextViewInfo) tag).type == TextViewInfo.TYPE_SUBJECT) {
                mNowSelectSubjectOrder = Integer.parseInt(((TextViewInfo) tag).id);
                setMode(MODE_ADD_TODO);
            }
        }
    }

    private void showSubjectSelectDialog(SelectSubjectDialog.Callback callback) {
        FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();
        DialogFragment dialog = SelectSubjectDialog.newInstance(callback);
        dialog.show(ft, TAG_DIALOG_SELECT_SUBJECT);

    }
}
