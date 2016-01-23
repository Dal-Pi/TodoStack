package com.kania.todostack2.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.kania.todostack2.R;
import com.kania.todostack2.provider.TodoProvider;
import com.kania.todostack2.view.IViewAction;
import com.kania.todostack2.view.TodoLayoutInfo;

/**
 * Created by user on 2016-01-20.
 */
public class TodoStackPresenter implements IControllerMediator {
    Context mContext;
    TodoLayoutInfo mTodolayoutInfo;

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
        //if there is no subject, do not perform.
        if (TodoProvider.getSubjectCount() > 0) {
            //TODO implement this
//            mTodolayoutInfo = new TodoLayoutInfo();
            //after define all length, set mode to MODE_NO_SELECTION
            setMode(MODE_NO_SELECTION);
        } else {
            setMode(MODE_INITIAL_SETUP);
        }
    }

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
                break;
            case MODE_NO_SELECTION:
                if (mViewMode != MODE_INITIAL_SETUP && mViewMode != MODE_NO_SELECTION) {
                    needAnimation = true;
                }
                mTodoView.setAllControllerGone();
                mTodoView.setFabToBase(res.getString(R.string.fab_create_todo),
                        res.getColor(R.color.color_normal_state), needAnimation);
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
    public void clickFloatingActionButton() {
        if (mViewMode == MODE_INITIAL_SETUP) {
            setMode(MODE_ADD_SUBJECT);
        }
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
