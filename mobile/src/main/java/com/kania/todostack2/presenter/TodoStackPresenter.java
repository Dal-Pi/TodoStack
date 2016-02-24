package com.kania.todostack2.presenter;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
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
import com.kania.todostack2.provider.TodoListWidgetProvider;
import com.kania.todostack2.provider.TodoProvider;
import com.kania.todostack2.util.SubjectDeleteDialog;
import com.kania.todostack2.util.SubjectSelectDialog;
import com.kania.todostack2.util.TodoDoneDialog;
import com.kania.todostack2.util.TodoSelectDialog;
import com.kania.todostack2.util.TodoStackUtil;
import com.kania.todostack2.view.DetailTodoListActivity;
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
public class TodoStackPresenter implements IControllerMediator, View.OnClickListener,
        View.OnLongClickListener {
    public final int NOT_SELECTED_SUBJECT = -1;

    public final String TAG_DIALOG_SELECT_SUBJECT = "select_subject";
    public final String TAG_DIALOG_DELETE_SUBJECT = "delete_subject";
    public final String TAG_DIALOG_SELECT_TODO = "select_todo";
    public final String TAG_DIALOG_DONE_TODO = "done_todo";

    public final String TODO_DIVIDER = " / ";
    private Context mContext;
    private TodoLayoutInfo mTodolayoutInfo;

    private Resources res;

    private IViewAction mTodoView;
    private int mViewMode;

    private int mNowSelectSubjectOrder = NOT_SELECTED_SUBJECT;
    private boolean mIsFabTop = false;

    private String mTodoIdNowViewing;
    private DialogFragment mDialogNowViewing;

    public TodoStackPresenter(Context context) {
        mContext = context;
        mViewMode = MODE_NO_SELECTION;

        res = mContext.getResources();
    }

    @Override
    public void setTargetView(IViewAction targetView) {
        mTodoView = targetView;
    }

    @Override
    public void setTodoIdNowViewing(String todoId) {
        mTodoIdNowViewing = todoId;
    }

    private void setNowBusy(boolean nowBusy) {
        mTodoView.setNowBusy(nowBusy);
    }

    @Override
    public void initTodoLayout(int layoutWidth, int layoutHeight) {
//        Log.d("TodoStack", "[initTodoLayout] width/height = " + layoutWidth + "/" + layoutHeight);
        TodoProvider provider = TodoProvider.getInstance(mContext);

        if (provider.getSubjectCount() > 0) {
            refreshTodoLayout(layoutWidth, layoutHeight);
            sendDataToTodoViewAfterConverting();
            setModeByOwnInfo();
        } else {
            setMode(MODE_INITIAL_SETUP, null);
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

    @Override
    public void setModeByOwnInfo() {
        //[160217] add condition for id from widget
        if (mTodoIdNowViewing != null && !"".equalsIgnoreCase(mTodoIdNowViewing)) {
            //entering from widget, if previous task showing dialog, become dismiss
            if(mDialogNowViewing != null && mDialogNowViewing.isVisible()) {
                mDialogNowViewing.dismiss();
                mDialogNowViewing = null;
            }
            TextViewInfo infoFromWidget = new TextViewInfo(TextViewInfo.TYPE_TODO,
                    mTodoIdNowViewing, true);
            mNowSelectSubjectOrder = getSelectedSubjectOrderFromTag(infoFromWidget);
            setMode(MODE_VIEW_TODO, infoFromWidget);
        } else {
            if (mViewMode != MODE_VIEW_TODO) {
                setMode(mViewMode, null); //initial : MODE_NO_SELECTION(in constructor), newIntent : previous one
            } else {
                Log.e("TodoStack", "[setModeByOwnInfo] try to MODE_VIEW_TODO " +
                        "with null mTodoIdNowViewing!");
                setMode(MODE_NO_SELECTION, null);
            }
        }
    }

    /**
     * refer comment on interface that is step to set mode.
     * @param targetMode intented mode, in this method, mViewMode mean present mode
     */
    @Override
    public void setMode(int targetMode, Object info) {
        TodoProvider provider = TodoProvider.getInstance(mContext);
        boolean needAnimation = false;
        setTodoIdNowViewing(null);
        switch (targetMode) {
            case MODE_INITIAL_SETUP:
                mNowSelectSubjectOrder = NOT_SELECTED_SUBJECT;
                mTodoView.setActionBarText(res.getString(R.string.app_name),
                        res.getColor(R.color.colorAccent));
                needAnimation = isFabTop();
                mTodoView.setAllControllerGone();
                mTodoView.setFabToBase(res.getString(R.string.fab_create_subject),
                        res.getColor(R.color.colorAccent), needAnimation);
                mIsFabTop = false;
                mTodoView.setGuideText(res.getString(R.string.guide_text_mode_initial_setup));
                break;
            case MODE_NO_SELECTION:
                mNowSelectSubjectOrder = NOT_SELECTED_SUBJECT;
                mTodoView.setActionBarText(res.getString(R.string.app_name),
                        res.getColor(R.color.colorAccent));
                needAnimation = isFabTop();
                mTodoView.setAllControllerGone();
                mTodoView.setFabToBase(res.getString(R.string.fab_create_todo),
                        res.getColor(R.color.colorAccent), needAnimation);
                mIsFabTop = false;
                mTodoView.setGuideText(res.getString(R.string.guide_text_suggest_select_subject));
                break;
            case MODE_ADD_TODO:
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
                mTodoView.setFab(res.getString(R.string.fab_add), targetSubjectColor,
                        needAnimation);
                mIsFabTop = true;
                mTodoView.setGuideText(res.getString(R.string.guide_text_mode_input_todo));
                break;
            case MODE_ADD_SUBJECT:
                mTodoView.setActionBarText(res.getString(R.string.title_text_on_new_subject),
                        res.getColor(R.color.color_normal_state));
                needAnimation = !isFabTop();
                mTodoView.setInputSubjectVisible();
                mTodoView.setFab(res.getString(R.string.fab_add),
                        res.getColor(R.color.color_normal_state), needAnimation);
                mIsFabTop = true;
                mTodoView.setGuideText(res.getString(R.string.guide_text_mode_input_subject));
                break;
            case MODE_VIEW_TODO:
                if (mNowSelectSubjectOrder == NOT_SELECTED_SUBJECT) {
                    //mixed subject case
                } else {
                    SubjectData sd = provider.getSubjectByOrder(mNowSelectSubjectOrder);
                    mTodoView.setActionBarText(
                            res.getString(R.string.adding_text_view_todo) + " " + sd.subjectName,
                            sd.color);
                    needAnimation = !isFabTop();
                    mTodoIdNowViewing = ((TextViewInfo) info).id;
                    mTodoView.setTagOnTodoTextView((TextViewInfo) info);
                    mTodoView.setViewTodoVisible(getSpannableStringFromTodos((TextViewInfo) info));
                    mTodoView.setFab(res.getString(R.string.todo_done),
                            sd.color, needAnimation);
                    mIsFabTop = true;
                    mTodoView.setGuideText(res.getString(R.string.guide_text_mode_view_todo));
                }
                break;
            case MODE_VIEW_SUBJECT:
                if (mNowSelectSubjectOrder == NOT_SELECTED_SUBJECT) {
                    Log.e("TodoStack",
                            "[setMode] error on MODE_VIEW_SUBJECT, mNowSelectSubjectOrder is -1");
                } else {
                    SubjectData sd = provider.getSubjectByOrder(mNowSelectSubjectOrder);
                    mTodoView.setActionBarText(sd.subjectName, sd.color);
                    needAnimation = !isFabTop();
                    boolean leftEnable = !(mNowSelectSubjectOrder <= 0);
                    boolean rightEnable =
                            !(mNowSelectSubjectOrder >= (provider.getSubjectCount() - 1));
//                    Log.d("TodoStack", "[setMode] provider.getSubjectCount() = "
//                            + provider.getSubjectCount());
//                    Log.d("TodoStack", "[setMode] leftEnable = " + leftEnable
//                            + " / rightEnable = " + rightEnable);
                    mTodoView.setViewSubjectVisible(sd.color, leftEnable, rightEnable);
                    mTodoView.setFab(res.getString(R.string.subject_view_all_todo),
                            sd.color, needAnimation);
                    mIsFabTop = true;
                    mTodoView.setGuideText(res.getString(R.string.guide_text_mode_view_subject));
                }
                break;
            default:
                break;
        }
        setNowBusy(false);
        mViewMode = targetMode;
        Log.d("TodoStack", "Now Mode = " + printMode(mViewMode));
    }

    private boolean isFabTop() {
        return mIsFabTop;
    }

    private void sendDataToTodoViewAfterConverting() {
        mTodoView.clearTodoLayout();
        addDateTextData();
        addSubjectData();
        addTodoData();
        mTodoView.refreshTodoLayout();

        //for navigation drawer
        sendNavSubjectItem();

        //for widget (import from TodoStack1)

        Intent intent = new Intent();
        intent.setAction(TodoListWidgetProvider.WIDGET_UPDATE_ACTION);
        mContext.sendBroadcast(intent);
    }

    private void addDateTextData() {
        ArrayList<TextView> sendData = new ArrayList<TextView>();
        int dateCount = TodoStackSettingValues.getInstance(mContext).getVisivleDateCount();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < dateCount; ++i) {
            TextView tv = new TextView(mContext);
            tv.setIncludeFontPadding(false);
            tv.setText(getFormatedDateTextFromDate(calendar.getTime()));
            tv.setTextColor(res.getColor(R.color.color_black));
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
    private void addSubjectData() {
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
            tv.setSingleLine();
            TextViewInfo info = mTodolayoutInfo.getSubjectPosition(sd.order);
            info.type = TextViewInfo.TYPE_SUBJECT;
            info.id = sd.order + "";
            tv.setTag(info);
            tv.setOnClickListener(this);
            tv.setOnLongClickListener(this);

            sendData.add(tv);

            //debug
//            Log.d("TodoStack", "[addSubjectData] subject id = " + sd.order);
        }
        mTodoView.setTextViewsOnTodoLayout(sendData);
    }
    private void addTodoData() {
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
                Log.e("TodoStack", "[addTodoData] subjectdata is null!");
                continue;
            } else {
                if (td.type == TodoData.TODO_DB_TYPE_TASK) {
                    subjectdata.taskCount++;
                    if (subjectdata.taskCount < settingValues.getVisivleTaskCount()) {
                        TextView tv = getTaskTodoTextView(td, subjectdata);
                        sendData.add(tv);
                    } else if (subjectdata.taskCount == settingValues.getVisivleTaskCount()) {
                        TextView moreTv = getMoreTaskTextView(subjectdata);
                        sendData.add(moreTv);
                    } else {
                        continue;
                    }

                } else {
                    int cmpDiffDays = settingValues.getVisivleDateCount() + 1;
                    Calendar targetCalendar = Calendar.getInstance();
                    try {
                        targetCalendar.setTime(sdf.parse(td.date));
                        cmpDiffDays = TodoStackUtil.campareDate(targetCalendar, calendarToday);
                    } catch (ParseException e) {
                        Log.e("TodoStack", "[addTodoData] parse error!!");
                        e.printStackTrace();
                    }

                    if (cmpDiffDays < 0) { //delayed
                        subjectdata.delayedTodoCount++;
                        if (subjectdata.delayedTodoCount < settingValues.getVisivleDelayedCount()) {
                            TextView tv = getDelayedTodoTextView(td, subjectdata);
                            sendData.add(tv);
                        } else if (subjectdata.delayedTodoCount == settingValues.getVisivleDelayedCount()) {
                            TextView moreTv = getMoreDelayedTextView(subjectdata);
                            sendData.add(moreTv);
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
                            info.type = TextViewInfo.TYPE_TODO;
                            info.id = td.id + "";
                            if (cmpDiffDays == 0) { //today
                                info.isToday = true;
                            } else {
                                info.isToday = false;
                            }
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
                    if (combinedInfo.isToday) {
                        subjectColor = ColorProvider.getInstance().getTodayColor();
                    } else {
                        subjectColor = todoProvider.getSubjectByOrder(firstTodoData.subjectOrder).color;
                    }
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
        info.type = TextViewInfo.TYPE_TODO;
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
        info.type = TextViewInfo.TYPE_TODO;
        info.id = td.id + "";
        tv.setTag(info);

        return tv;
    }

    private TextView getMoreTaskTextView(SubjectData sd) {
        TextView moreTaskTextView = getMoreCommonTextViwe(sd);
        TextViewInfo info = mTodolayoutInfo.getTaskTodoPosition(sd.order, sd.taskCount);
        info.type = TextViewInfo.TYPE_VIEW_ALL_TASK;
        info.id = sd.order + "";
        moreTaskTextView.setTag(info);

        return moreTaskTextView;
    }

    private TextView getMoreDelayedTextView(SubjectData sd) {
        TextView moreDelayedTextView = getMoreCommonTextViwe(sd);
        TextViewInfo info = mTodolayoutInfo.getDelayedTodoPosition(sd.order, sd.delayedTodoCount);
        info.type = TextViewInfo.TYPE_VIEW_ALL_DELAYED_TODO;
        info.id = sd.order + "";
        moreDelayedTextView.setTag(info);

        return moreDelayedTextView;
    }

    private TextView getMoreCommonTextViwe(SubjectData sd) {
        TextView moreTextView;
        String moreString = res.getString(R.string.todo_view_more);
        int moreColor = Color.argb(Color.alpha(sd.color)/2 ,
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
        tv.setTextColor(res.getColor(R.color.color_todo_text));
        tv.setBackgroundColor(subjectColor);
        tv.setOnClickListener(this);
        tv.setOnLongClickListener(this);

        return tv;
    }

    private SpannableString getSpannableStringFromTodos(TextViewInfo info) {
        final TodoProvider provider = TodoProvider.getInstance(mContext);
        String todoString = "";
        String[] ids = info.id.split(TextViewInfo.DELIMITER_ID);
//        int[] lengths = new int[ids.length];
//        ClickableSpan[] clickableSpans = new ClickableSpan[ids.length];

        for (int i = 0; i < ids.length; ++i) {
            TodoData td = provider.getTodoById(Integer.parseInt(ids[i]));
            if (i == 0)
                todoString += td.todoName;
            else
                todoString += TODO_DIVIDER + td.todoName;
        }
        SpannableString ret = new SpannableString(todoString);
        int pos = 0;
        for (int i = 0; i < ids.length; ++i) {
            final TodoData td = provider.getTodoById(Integer.parseInt(ids[i]));
            final SubjectData sd = provider.getSubjectByOrder(td.subjectOrder);
            ret.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    //TODO launch detail info dialog ? temp launch done dialog
                    showTodoDoneDialog(td.id);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
//                    Log.d("TodoStack", "sd color = " + sd.color);
                    ds.setColor(sd.color);
                    ds.setUnderlineText(false);
                }
            }, pos, pos + td.todoName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            pos += td.todoName.length() + TODO_DIVIDER.length();
        }


        return ret;
    }

    private void sendNavSubjectItem() {
        ArrayList<SubjectData> subjectsWithViewAll = new ArrayList<SubjectData>();
//        SubjectData subjectViewAll = new SubjectData();

//        subjectViewAll.subjectName = res.getString(R.string.nav_menu_all_todo);
//        subjectViewAll.color = res.getColor(R.color.colorAccent);
//        subjectViewAll.order = NOT_SELECTED_SUBJECT;
//        subjectsWithViewAll.add(subjectViewAll);
        for (SubjectData sd : TodoProvider.getInstance(mContext).getAllSubject()) {
            subjectsWithViewAll.add(sd);
        }

        mTodoView.putSubjectsOnDrawer(subjectsWithViewAll);
    }

    @Override
    public void clickBackPressSoftButton() {
        if (mViewMode != MODE_INITIAL_SETUP && mViewMode != MODE_NO_SELECTION) {
            //check now busy
            if (mTodoView.getNowBusy())
                return;
            //any mode
            if (TodoProvider.getSubjectCount() > 0) {
                setMode(MODE_NO_SELECTION, null);
            } else {
                setMode(MODE_INITIAL_SETUP, null);
            }
        } else {
            mTodoView.finishActivity();
        }
    }

    @Override
    public void clickFloatingActionButton(Bundle bundle) {
        switch (mViewMode) {
            case MODE_INITIAL_SETUP:
                setMode(MODE_ADD_SUBJECT, null);
                break;
            case MODE_NO_SELECTION:
                if (mNowSelectSubjectOrder == NOT_SELECTED_SUBJECT) {
                    showSubjectSelectDialog();
                } else { //it never used
                    setMode(MODE_ADD_TODO, null);
                }
                break;
            case MODE_ADD_TODO:
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
            case MODE_VIEW_TODO:
                showTodoSelectDialog(bundle.getString(TodoStackContract.TodoEntry._ID));
                break;
            case MODE_VIEW_SUBJECT:
                //TODO will remove Handler. It is bad code
                final int savedNowSelectSubjectOrder = mNowSelectSubjectOrder;
                setMode(MODE_NO_SELECTION, null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        launchDetailTodoListActivity(savedNowSelectSubjectOrder);
                    }
                }, 500);
                break;
            default:
                break;
        }
    }

    private void insertSubject(Bundle bundle) {
        setNowBusy(true);
        UpdateSubjectTask insertSubjectTask =
                new UpdateSubjectTask(mContext, new UpdateSubjectTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        reloadTodoDataToView(MODE_NO_SELECTION);
                    }
                });
        insertSubjectTask.setData(
                makeNewSubjectDataFromBundle(bundle), UpdateSubjectTask.SUBJECT_TASK_ADD_SUBJECT);
        insertSubjectTask.execute();
    }

    private void insertTodo(Bundle bundle) {
        UpdateTodoTask insertTodoTask =
                new UpdateTodoTask(mContext, new UpdateTodoTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        reloadTodoDataToView(MODE_NO_SELECTION);
                    }
                });
        insertTodoTask.setData(
                makeTodoData(bundle), UpdateTodoTask.TODO_TASK_ADD_TODO);
        insertTodoTask.execute();
    }

    @Override
    public void changeSubjectName(String name) {
        setNowBusy(true);
        UpdateSubjectTask updateSubjectTask = new UpdateSubjectTask(mContext,
                new UpdateSubjectTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        reloadTodoDataToView(MODE_VIEW_SUBJECT);
                    }
                });
        SubjectData sd = TodoProvider.getInstance(mContext).
                getSubjectByOrder(mNowSelectSubjectOrder);
        sd.subjectName = name;
        updateSubjectTask.setData(sd, UpdateSubjectTask.SUBJECT_TASK_MODIFY_NAME);
        updateSubjectTask.execute();
    }

    @Override
    public void changeSubjectColor(int color) {
        setNowBusy(true);
        UpdateSubjectTask updateSubjectTask = new UpdateSubjectTask(mContext,
                new UpdateSubjectTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        reloadTodoDataToView(MODE_VIEW_SUBJECT);
                    }
                });
        SubjectData sd = TodoProvider.getInstance(mContext).
                getSubjectByOrder(mNowSelectSubjectOrder);
        sd.color = color;
        updateSubjectTask.setData(sd, UpdateSubjectTask.SUBJECT_TASK_MODIFY_COLOR);
        updateSubjectTask.execute();
    }

    @Override
    public void moveSubjectOrder(final boolean isLeft) {
//        Log.d("TodoStack", "[moveSubjectOrder] target order = " + mNowSelectSubjectOrder
//                + "direction = " + (isLeft ? "left" : "right"));
        setNowBusy(true);
        UpdateSubjectTask updateSubjectTask = new UpdateSubjectTask(mContext,
                new UpdateSubjectTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        mNowSelectSubjectOrder += isLeft ? UpdateSubjectTask.DIRECTION_LEFT :
                                UpdateSubjectTask.DIRECTION_RIGHT;
                        reloadTodoDataToView(MODE_VIEW_SUBJECT);
                    }
                });
        SubjectData sd = TodoProvider.getInstance(mContext).
                getSubjectByOrder(mNowSelectSubjectOrder);
        if (isLeft) {
            updateSubjectTask.setData(sd, UpdateSubjectTask.SUBJECT_TASK_MOVE_LEFT);
        } else {
            updateSubjectTask.setData(sd, UpdateSubjectTask.SUBJECT_TASK_MOVE_RIGHT);
        }
        updateSubjectTask.execute();
    }

    @Override
    public void deleteSubject() {
        TodoProvider provider = TodoProvider.getInstance(mContext);
        if (mNowSelectSubjectOrder >= 0
                && mNowSelectSubjectOrder < (provider.getSubjectCount() - 1)) {
            Toast.makeText(mContext, res.getString(R.string.toast_waring_not_end_subject),
                    Toast.LENGTH_LONG).show();
        } else if (mNowSelectSubjectOrder == (provider.getSubjectCount() - 1)) {
            showSubjectDeleteDialog();
        } else {
            Log.e("TodoStack", "[deleteSubject] Invalid subject number : "
                    + mNowSelectSubjectOrder);
        }
    }

    private SubjectData makeNewSubjectDataFromBundle(Bundle bundle) {
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
        setMode(MODE_ADD_SUBJECT, null);
    }

    private void showSubjectSelectDialog() {
        FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();
        DialogFragment dialog = SubjectSelectDialog.newInstance(
                new SubjectSelectDialog.Callback() {
            @Override
            public void onSelectSubject(int order) {
                mNowSelectSubjectOrder = order;
                setMode(MODE_ADD_TODO, null);
            }
        });
        dialog.show(ft, TAG_DIALOG_SELECT_SUBJECT);
        mDialogNowViewing = dialog;
    }

    private void showSubjectDeleteDialog() {
        FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();
        DialogFragment dialog = SubjectDeleteDialog.newInstance(mNowSelectSubjectOrder,
                new SubjectDeleteDialog.Callback() {
                    @Override
                    public void onSelectDelete(int order) {
                        TodoProvider provider = TodoProvider.getInstance(mContext);
                        //TODO because "view all todo" is not implemented, do not count todoCount yet
                        if (true /*provider.getTodoCount(mNowSelectSubjectOrder) == 0*/) {
                            UpdateSubjectTask deletesubjectTask = new UpdateSubjectTask(mContext,
                                    new UpdateSubjectTask.TaskEndCallback() {
                                        @Override
                                        public void updateFinished() {
                                            reloadTodoDataToView(MODE_NO_SELECTION);
                                        }
                                    });
                            deletesubjectTask.setData(
                                    provider.getSubjectByOrder(mNowSelectSubjectOrder),
                                    UpdateSubjectTask.SUBJECT_TASK_DELETE_SUBJECT);
                            deletesubjectTask.execute();
                        } else {
                            Toast.makeText(mContext,
                                    res.getString(R.string.toast_waring_remained_todos),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        dialog.show(ft, TAG_DIALOG_DELETE_SUBJECT);
        mDialogNowViewing = dialog;
    }

    private void showTodoSelectDialog(String ids) {
        //if id has only one id, skip dialog
        String[] sIds = ids.split(TextViewInfo.DELIMITER_ID);
        if (sIds.length == 1) {
            showTodoDoneDialog(Integer.parseInt(sIds[0]));
        } else {
            FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();
            DialogFragment dialog = TodoSelectDialog.newInstance(ids,
                    new TodoSelectDialog.Callback() {
                @Override
                public void onSelectTodo(int id) {
                    showTodoDoneDialog(id);
                }
            });
            dialog.show(ft, TAG_DIALOG_SELECT_TODO);
            mDialogNowViewing = dialog;
        }
    }

    private void showTodoDoneDialog(int todoId) {
        FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();
        DialogFragment dialog = TodoDoneDialog.newInstance(todoId, new TodoDoneDialog.Callback() {
            @Override
            public void onDeleteTodo(int id) {
                UpdateTodoTask deleteTodoTask = new UpdateTodoTask(mContext,
                        new UpdateTodoTask.TaskEndCallback() {
                            @Override
                            public void updateFinished() {
                                reloadTodoDataToView(MODE_NO_SELECTION);
                            }
                        });
                deleteTodoTask.setData(TodoProvider.getInstance(mContext).getTodoById(id),
                        UpdateTodoTask.TODO_TASK_DELETE_TODO);
                deleteTodoTask.execute();
            }

            @Override
            public void onMoveTodo(int id, int moveType) {
                UpdateTodoTask moveTodoTask = new UpdateTodoTask(mContext,
                        new UpdateTodoTask.TaskEndCallback() {
                            @Override
                            public void updateFinished() {
                                reloadTodoDataToView(MODE_NO_SELECTION);
                            }
                        });
                moveTodoTask.setData(TodoProvider.getInstance(mContext).getTodoById(id), moveType);
                moveTodoTask.execute();
            }
        });
        dialog.show(ft, TAG_DIALOG_DONE_TODO);
        mDialogNowViewing = dialog;
    }

    private void reloadTodoDataToView(final int afterMode) {
        LoadingTodoTask refreshTask = new LoadingTodoTask(
                mContext, new LoadingTodoTask.TaskEndCallback() {
            @Override
            public void loadFinished() {
                sendDataToTodoViewAfterConverting();
                setMode(afterMode, null);
            }
        });
        refreshTask.execute();
    }

    @Override
    public void onClick(View v) {
        if (mTodoView.getNowBusy())
            return;
        Object tag = v.getTag();
        if (tag != null && tag instanceof TextViewInfo) {
            mNowSelectSubjectOrder = getSelectedSubjectOrderFromTag(tag);
            int type = ((TextViewInfo) tag).type;
            if (type == TextViewInfo.TYPE_SUBJECT) {
                setMode(MODE_ADD_TODO, null);
            } else if (type == TextViewInfo.TYPE_TODO) {
                setMode(MODE_VIEW_TODO, tag);
            } else if (type == TextViewInfo.TYPE_VIEW_ALL_TASK) {
                //TODO launch fragment
                Toast.makeText(mContext, "not implemented yet", Toast.LENGTH_SHORT).show();
            } else if (type == TextViewInfo.TYPE_VIEW_ALL_DELAYED_TODO) {
                //TODO launch fragment
                Toast.makeText(mContext, "not implemented yet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mTodoView.getNowBusy())
            return false;
        Object tag = v.getTag();
        if (tag != null && tag instanceof TextViewInfo) {
            mNowSelectSubjectOrder = getSelectedSubjectOrderFromTag(tag);
            setMode(MODE_VIEW_SUBJECT, null);
            return true;
        } else {
            return false;
        }
    }

    private int getSelectedSubjectOrderFromTag(Object tag) {
        int ret = NOT_SELECTED_SUBJECT;
        if (tag != null && tag instanceof TextViewInfo) {
            int type = ((TextViewInfo) tag).type;
            if (type == TextViewInfo.TYPE_SUBJECT
                    || type == TextViewInfo.TYPE_VIEW_ALL_TASK
                    || type == TextViewInfo.TYPE_VIEW_ALL_DELAYED_TODO) {
                ret = Integer.parseInt(((TextViewInfo) tag).id);
            } else if (type == TextViewInfo.TYPE_TODO) {
                String combinedId = ((TextViewInfo) tag).id;
                String[] stringIds = combinedId.split(TextViewInfo.DELIMITER_ID);
                TodoData td = TodoProvider.getInstance(mContext).getTodoById(
                        Integer.parseInt(stringIds[0]));
                ret = td.subjectOrder;
            }
        }
//        Log.d("TodoStack", "[getSelectedSubjectOrderFromTag] ret = " + ret);
        return ret;
    }

    @Override
    public void clickNavigationDrawerItem(final int order) {
        if (mViewMode == MODE_VIEW_SUBJECT || mViewMode == MODE_ADD_SUBJECT
                || mViewMode == MODE_VIEW_TODO || mViewMode == MODE_ADD_TODO) {
            setMode(MODE_NO_SELECTION, null);
            //TODO will remove Handler. It is bad code
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchDetailTodoListActivity(order);
                }
            }, 500);
        } else {
            launchDetailTodoListActivity(order);
        }
    }

    private void launchDetailTodoListActivity(int order) {
        Intent detailIntent = new Intent(mContext, DetailTodoListActivity.class);
        detailIntent.putExtra(TodoStackContract.SubjectEntry.ORDER, order);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(detailIntent);
        ((Activity)mContext).overridePendingTransition(R.anim.right_in, R.anim.left_half_out);
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
            case MODE_VIEW_TODO:
                ret = "MODE_VIEW_TODO"; break;
            case MODE_VIEW_SUBJECT:
                ret = "MODE_VIEW_SUBJECT"; break;
            default:
                ret = "UNKNOWN_MODE!!!"; break;
        }
        return ret;
    }
}
