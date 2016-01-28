package com.kania.todostack2.presenter;

import android.content.Context;
import android.os.AsyncTask;

import com.kania.todostack2.provider.TodoProvider;

/**
 * Created by user on 2016-01-29.
 */
public class UpdateTodoTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;
    private TodoProvider mTodoProvider;
    private TaskEndCallback mCallback;

    public interface TaskEndCallback {
        void loadFinished();
    }

    public UpdateTodoTask(Context context, TaskEndCallback callback) {
        mContext = context;
        setCallback(callback);
    }

    private void setCallback(TaskEndCallback callback) {
        if (callback != null) {
            mCallback = callback;
        } else {
            mCallback = new TaskEndCallback() {
                //empty callback
                @Override
                public void loadFinished() {
                }
            };
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mCallback.loadFinished();
    }
}
