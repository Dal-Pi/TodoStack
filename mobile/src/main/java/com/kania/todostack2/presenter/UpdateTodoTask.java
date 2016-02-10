package com.kania.todostack2.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.data.TodoStackDbHelper;
import com.kania.todostack2.provider.TodoProvider;

/**
 * Created by user on 2016-01-29.
 */
public class UpdateTodoTask extends AsyncTask<Void, Void, Boolean> {

    public static final int TODO_TASK_ADD_TODO = 1;

    private Context mContext;
    private TodoProvider mTodoProvider;
    private TaskEndCallback mCallback;

    private TodoStackDbHelper dbHelper;
    private SQLiteDatabase todoStackDb;

    private int mTaskType = -1;
    private TodoData mData;

    public interface TaskEndCallback {
        void updateFinished();
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
                public void updateFinished() {
                }
            };
        }
    }

    public void setData(TodoData data, int taskType) {
        mData = data;
        mTaskType = taskType;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        if (mData == null || mTaskType == -1) {
            return false;
        } else {
            dbHelper = new TodoStackDbHelper(mContext);
            todoStackDb = dbHelper.getReadableDatabase();
            switch (mTaskType) {
                case TODO_TASK_ADD_TODO:
                    ContentValues cvTodo = new ContentValues();
                    cvTodo.put(TodoStackContract.TodoEntry.TODO_NAME, mData.todoName);
                    cvTodo.put(TodoStackContract.TodoEntry.SUBJECT_ORDER, mData.subjectOrder);
                    cvTodo.put(TodoStackContract.TodoEntry.DATE, mData.date);
                    cvTodo.put(TodoStackContract.TodoEntry.TYPE, mData.type);
                    cvTodo.put(TodoStackContract.TodoEntry.TIME_FROM, mData.timeFrom);
                    cvTodo.put(TodoStackContract.TodoEntry.TIME_TO, mData.timeTo);
                    cvTodo.put(TodoStackContract.TodoEntry.LOCATION, mData.location);
                    todoStackDb.insert(TodoStackContract.TodoEntry.TABLE_NAME, null, cvTodo);
                    break;
                default:
                    return false;
            }
            todoStackDb.close();
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result)
            mCallback.updateFinished();
        else
            Log.d("TodoStack", "[onPostExecute] fail to progress todo task");
    }
}
