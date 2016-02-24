package com.kania.todostack2.presenter;

import android.content.Context;

import com.kania.todostack2.R;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.provider.TodoProvider;

/**
 * Created by user on 2016-02-25.
 */
public class DetailTodoListPresenter {

    public static final int SUBJECT_ORDER_ALL = -1;

    private Context mContext;
    private TodoProvider mProvider;

    public DetailTodoListPresenter(Context context) {
        mContext = context;
        mProvider = TodoProvider.getInstance(mContext);
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

}
