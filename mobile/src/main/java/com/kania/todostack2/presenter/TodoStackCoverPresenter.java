package com.kania.todostack2.presenter;

import android.content.Context;
import android.content.Intent;

import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.provider.ColorProvider;
import com.kania.todostack2.view.MainActivity;

/**
 * Created by user on 2016-01-13.
 * It loads all data on DB to TodoProvider
 * While loading, cover provides progress bar to user
 */
public class TodoStackCoverPresenter {

    private Context mContext;
    private ProgressFinishCallback mCallback;

    private String todoIdFromWidget;

    public interface ProgressFinishCallback {
        void finishProgress();
    }

    //prevent to call default constructor
    private TodoStackCoverPresenter() {
    }

    public TodoStackCoverPresenter(Context context, ProgressFinishCallback callback) {
        mContext = context;
        setCallback(callback);
    }

    public void notifyStartCoverProgress() {
        LoadingTodoTask loadingTodoTask = new LoadingTodoTask(mContext,
                new LoadingTodoTask.TaskEndCallback() {
            @Override
            public void loadFinished() {
                //TODO save allTextView for next activity
                Intent intent = new Intent(mContext, MainActivity.class);
                if (todoIdFromWidget != null && !"".equalsIgnoreCase(todoIdFromWidget)) {
                    intent.putExtra(TodoStackContract.TodoEntry._ID, todoIdFromWidget);
                }
                mContext.startActivity(intent);
                mCallback.finishProgress();
            }
        });
        loadingTodoTask.execute();
    }

    private void setCallback(ProgressFinishCallback callback) {
        if (callback != null) {
            mCallback = callback;
        } else {
            mCallback = new ProgressFinishCallback() {
                //empty callback
                @Override
                public void finishProgress() {}
            };
        }
    }

    public void setTodoIdFromWidget(Intent intent) {
        todoIdFromWidget = intent.getStringExtra(TodoStackContract.TodoEntry._ID);
    }

    public int getCoverColorFromIntent(Intent intent) {
        int color = intent.getIntExtra(TodoStackContract.SubjectEntry.COLOR,
                ColorProvider.getInstance().getRandomColor());

        return color;
    }
}
