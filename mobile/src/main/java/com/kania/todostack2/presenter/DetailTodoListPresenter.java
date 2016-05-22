package com.kania.todostack2.presenter;

import android.content.Context;
import android.util.Log;

import com.kania.todostack2.R;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.provider.TodoProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by user on 2016-02-25.
 */
public class DetailTodoListPresenter {

    public static final int SUBJECT_ORDER_ALL = -1;

    public static final int TODO_TYPE_ALL = -1;
    public static final int TODO_TYPE_ALLDAY = TodoData.TODO_DB_TYPE_ALLDAY;
    public static final int TODO_TYPE_PERIOD = TodoData.TODO_DB_TYPE_PERIOD;
    public static final int TODO_TYPE_TASK = TodoData.TODO_DB_TYPE_TASK;

    private Context mContext;
    private TodoProvider mProvider;

    private ArrayList<TodoData> mAllTodoList;
    private ArrayList<TodoData> mDateTodoList;
    private ArrayList<TodoData> mTaskTodoList;

    public DetailTodoListPresenter(Context context, int subjectOrder) {
        mContext = context;
        mProvider = TodoProvider.getInstance(mContext);
        initTodoList(subjectOrder);
    }

    public SubjectData getSubjectDataFromOrder(int order) {
        if (order == SUBJECT_ORDER_ALL) {
            SubjectData defaultSub = new SubjectData();
            defaultSub.order = -1;
            defaultSub.color = mContext.getResources().getColor(R.color.color_normal_state);
            defaultSub.subjectName = mContext.getResources().getString(R.string.detail_all_title);
            return defaultSub;
        } else {
            return mProvider.getSubjectByOrder(order);
        }
    }

    public void initTodoList(int order) {
        mAllTodoList = new ArrayList<>();
        if (order == SUBJECT_ORDER_ALL) {
            for (TodoData td : mProvider.getAllTodo()) {
                mAllTodoList.add(td);
            }
        } else {
            for (TodoData td : mProvider.getTodoList(order)) {
                mAllTodoList.add(td);
            }
        }

        mDateTodoList = new ArrayList<>();
        mTaskTodoList = new ArrayList<>();

        for (TodoData td : mAllTodoList) {
            switch (td.type) {
                case TodoData.TODO_DB_TYPE_ALLDAY:
                case TodoData.TODO_DB_TYPE_PERIOD:
                    mDateTodoList.add(td);
                    break;
                case TodoData.TODO_DB_TYPE_TASK:
                    mTaskTodoList.add(td);
                    break;
                default:
                    Log.w("TodoStack", "[initTodoList] unknown type!");
            }
        }
    }

    public ArrayList<TodoData> getTodoList(int order, int todoType) {
        if (mAllTodoList == null || mDateTodoList == null || mTaskTodoList == null) {
            initTodoList(order);
        }
        switch (todoType) {
            case TODO_TYPE_ALL:
                return mAllTodoList;
            case TODO_TYPE_ALLDAY:
            case TODO_TYPE_PERIOD:
                return mDateTodoList;
            case TODO_TYPE_TASK:
                return mTaskTodoList;
            default:
                Log.w("TodoStack", "[getTodoList] unknown type!");
        }

        return null;
    }
}
