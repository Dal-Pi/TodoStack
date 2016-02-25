package com.kania.todostack2.presenter;

import android.content.Context;
import android.util.Log;

import com.kania.todostack2.R;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.provider.TodoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016-02-25.
 */
public class DetailTodoListPresenter {

    public static final int SUBJECT_ORDER_ALL = -1;

    public static final int TODO_TYPE_ALL = -1;

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
        if (order == SUBJECT_ORDER_ALL) {
            mAllTodoList = mProvider.getAllTodo();
        } else {
            mAllTodoList = mProvider.getTodoList(order);
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
        //TODO need to sort
    }

    public ArrayList<TodoData> getTodoList(int order, int todoType) {
        if (mAllTodoList == null || mDateTodoList == null || mTaskTodoList == null) {
            initTodoList(order);
        }
        switch (todoType) {
            case TODO_TYPE_ALL:
                return mAllTodoList;
            case TodoData.TODO_DB_TYPE_ALLDAY:
            case TodoData.TODO_DB_TYPE_PERIOD:
                return mDateTodoList;
            case TodoData.TODO_DB_TYPE_TASK:
                return mTaskTodoList;
            default:
                Log.w("TodoStack", "[getTodoList] unknown type!");
        }

        return null;
    }
}
