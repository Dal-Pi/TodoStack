package com.kania.todostack2.presenter;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kania.todostack2.R;
import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.data.TodoStackDbHelper;
import com.kania.todostack2.provider.TodoProvider;

/**
 * Created by user on 2016-01-29.
 */
public class UpdateTodoTask extends AsyncTask<Void, Void, Boolean> {

    public static final int TODO_TASK_ADD_TODO = 1;
    public static final int TODO_TASK_DELETE_TODO = 2;
    public static final int TODO_TASK_UPDATE_TODO = 3;

    public static final int TODO_MOVE_OPTION_TOMORROW = 1;
    public static final int TODO_MOVE_OPTION_NEXT_WEEK = 2;
    public static final int TODO_MOVE_OPTION_NEXT_MONTH = 3;
    public static final int TODO_MOVE_OPTION_NEXT_YEAR = 4;
    public static final int TODO_MOVE_OPTION_TODAY = 5;
    public static final int TODO_MOVE_OPTION_TASK = 6;

    private ProgressDialog mProgressDialog;

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
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mContext, null,
                mContext.getResources().getString(R.string.dialog_progress_updating));
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
                    addTodo();
                    break;
                case TODO_TASK_DELETE_TODO:
                    //TODO
                    deleteTodo();
                    break;
                case TODO_TASK_UPDATE_TODO:
                    //TODO
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
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        if (result)
            mCallback.updateFinished();
        else
            Log.d("TodoStack", "[onPostExecute] fail to progress todo task");
    }

    private void addTodo() {
        ContentValues cvTodo = new ContentValues();
        cvTodo.put(TodoStackContract.TodoEntry.TODO_NAME, mData.todoName);
        cvTodo.put(TodoStackContract.TodoEntry.SUBJECT_ORDER, mData.subjectOrder);
        cvTodo.put(TodoStackContract.TodoEntry.DATE, mData.date);
        cvTodo.put(TodoStackContract.TodoEntry.TYPE, mData.type);
        cvTodo.put(TodoStackContract.TodoEntry.TIME_FROM, mData.timeFrom);
        cvTodo.put(TodoStackContract.TodoEntry.TIME_TO, mData.timeTo);
        cvTodo.put(TodoStackContract.TodoEntry.LOCATION, mData.location);
        todoStackDb.insert(TodoStackContract.TodoEntry.TABLE_NAME, null, cvTodo);
    }
    private void deleteTodo() {
        String selectionDeleteTodo =
                TodoStackContract.TodoEntry._ID + " LIKE " + mData.id;
        todoStackDb.delete(TodoStackContract.TodoEntry.TABLE_NAME, selectionDeleteTodo, null);
    }
}
