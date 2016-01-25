package com.kania.todostack2.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by user on 2016-01-20.
 */
public class TodoStackPresenter implements IControllerMediator {
    private Context mContext;
    private TodoLayoutInfo mTodolayoutInfo;

    private Resources res;

    private IViewAction mTodoView;
    private int mViewMode;

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
        //if there is no subject, do not perform.
        if (provider.getSubjectCount() > 0) {
            //TODO implement this
            mTodolayoutInfo = new TodoLayoutInfo(layoutWidth, layoutHeight,
                    provider.getSubjectCount(),
                    TodoStackSettingValues.getVisivleTaskCount(),
                    TodoStackSettingValues.getVisivleDateCount(),
                    TodoStackSettingValues.getVisivleDelayedCount());
            //after define all length, set mode to MODE_NO_SELECTION
            setMode(MODE_NO_SELECTION);
        } else {
            setMode(MODE_INITIAL_SETUP);
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
                if (mViewMode == MODE_ADD_SUBJECT || mViewMode == MODE_VIEW_SUBJECT) {
                    needAnimation = true;
                }
                mTodoView.setAllControllerGone();
                mTodoView.setFabToBase(res.getString(R.string.fab_create_subject),
                        res.getColor(R.color.color_normal_state), needAnimation);
                mTodoView.setGuideText(res.getString(R.string.guide_text_mode_initial_setup));
                break;
            case MODE_NO_SELECTION:
                if (mViewMode != MODE_INITIAL_SETUP && mViewMode != MODE_NO_SELECTION) {
                    needAnimation = true;
                }
                //set data to TodoLayout
                sendDataToTodoViewAfterConverting();
                mTodoView.setAllControllerGone();
                mTodoView.setFabToBase(res.getString(R.string.fab_create_todo),
                        res.getColor(R.color.color_normal_state), needAnimation);
                mTodoView.setGuideText(res.getString(R.string.guide_text_mode_no_selection));
                break;
            case MODE_ADD_TODO:
                break;
            case MODE_ADD_SUBJECT:
                if (mViewMode != MODE_ADD_SUBJECT) {
                    needAnimation = true;
                }
                mTodoView.setInputSubjectVisible();
                mTodoView.setFabToInputSubject(res.getString(R.string.fab_add),
                        res.getColor(R.color.color_normal_state), needAnimation);
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

    private void sendDataToTodoViewAfterConverting() {
        ArrayList<TextView> sendData = new ArrayList<TextView>();
        ArrayList<SubjectData> subData = TodoProvider.getInstance(mContext).getAllSubject();
        ArrayList<TodoData> todoData = TodoProvider.getInstance(mContext).getAllTodo();

        for(SubjectData sd : subData) {
            TextView tv = new TextView(mContext);
            tv.setIncludeFontPadding(false);
            tv.setText(sd.subjectName);
            tv.setTextColor(sd.color);
            tv.setBackgroundColor(res.getColor(R.color.color_subject_background));
            tv.setTag(mTodolayoutInfo.getSubjectPosition(sd.order));

            sendData.add(tv);
        }
        mTodoView.setTextViewOnTodoLayout(sendData);
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
                break;
            case MODE_ADD_TODO:
                break;
            case MODE_ADD_SUBJECT:
                //TODO request add subject using asynctask
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

    private TextView getTextViewTemplet() {
        TextView textView = new TextView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);

        return textView;
    }

    private TextView setSubjectTodoView(TextView textView, Bundle bundle) {
        String subjectName =
                bundle.getString(TodoStackContract.SubjectEntry.SUBJECT_NAME, "ERROR");
        int color = bundle.getInt(
                TodoStackContract.SubjectEntry.COLOR, res.getColor(R.color.color_normal_state));
        textView.setText(subjectName);
        textView.setTextColor(color);

        return textView;
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
