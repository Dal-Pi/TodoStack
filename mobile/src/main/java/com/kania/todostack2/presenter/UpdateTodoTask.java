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

import java.util.Calendar;

/**
 * Created by user on 2016-01-29.
 */
public class UpdateTodoTask extends AsyncTask<Void, Void, Boolean> {

    public static final int TODO_TASK_ADD_TODO = 1;
    public static final int TODO_TASK_DELETE_TODO = 2;

//    public static final int TODO_TASK_UPDATE_TODO = 3;

    public static final int TODO_MOVE_OPTION_TOMORROW = 4;
    public static final int TODO_MOVE_OPTION_NEXT_WEEK = 5;
    public static final int TODO_MOVE_OPTION_NEXT_MONTH = 6;
    public static final int TODO_MOVE_OPTION_NEXT_YEAR = 7;
    public static final int TODO_MOVE_OPTION_TODAY = 8;
    public static final int TODO_MOVE_OPTION_TASK = 9;

    private ProgressDialog mProgressDialog;

    private Context mContext;
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
                    deleteTodo();
                    break;
                case TODO_MOVE_OPTION_TOMORROW:
                case TODO_MOVE_OPTION_NEXT_WEEK:
                case TODO_MOVE_OPTION_NEXT_MONTH:
                case TODO_MOVE_OPTION_NEXT_YEAR:
                case TODO_MOVE_OPTION_TODAY:
                    moveTodo();
                    break;
                case TODO_MOVE_OPTION_TASK:
                    moveToTask();
                    break;
                default:
                    return false;
            }
            todoStackDb.close();
            TodoProvider.getInstance(mContext.getApplicationContext()).initData();
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
//        String timeStamp = getUpdatedDateString();

        long nowDate = getUpdatedDateMils();
        cvTodo.put(TodoStackContract.TodoEntry.CREATED_DATE, nowDate);
        cvTodo.put(TodoStackContract.TodoEntry.LAST_UPDATED_DATE, nowDate);
        todoStackDb.insert(TodoStackContract.TodoEntry.TABLE_NAME, null, cvTodo);
    }

    private void deleteTodo() {
        String selectionDeleteTodo =
                TodoStackContract.TodoEntry._ID + " LIKE " + mData.id;
        todoStackDb.delete(TodoStackContract.TodoEntry.TABLE_NAME, selectionDeleteTodo, null);
    }

    private void moveTodo() {
        Calendar calendar = Calendar.getInstance(); //today
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        switch (mTaskType) {
            case TODO_MOVE_OPTION_TODAY:
                //do nothing
                break;
            case TODO_MOVE_OPTION_TOMORROW:
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case TODO_MOVE_OPTION_NEXT_WEEK:
                calendar.add(Calendar.WEEK_OF_MONTH, 1);
                break;
            case TODO_MOVE_OPTION_NEXT_MONTH:
                calendar.add(Calendar.MONTH, 1);
                break;
            case TODO_MOVE_OPTION_NEXT_YEAR:
                calendar.add(Calendar.YEAR, 1);
                break;
        }
        ContentValues cvTodo = new ContentValues();
        cvTodo.put(TodoStackContract.TodoEntry.DATE, calendar.getTimeInMillis());
        if (mData.type == TodoData.TODO_DB_TYPE_TASK) {
            cvTodo.put(TodoStackContract.TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        } // else case : allday or period
        cvTodo.put(TodoStackContract.TodoEntry.LAST_UPDATED_DATE, getUpdatedDateMils());
        String selectionMoveTodo =
                TodoStackContract.TodoEntry._ID + " LIKE " + mData.id;

        todoStackDb.update(TodoStackContract.TodoEntry.TABLE_NAME, cvTodo, selectionMoveTodo, null);
    }

    private void moveToTask() {
        ContentValues cvTodo = new ContentValues();
        cvTodo.put(TodoStackContract.TodoEntry.DATE, getUpdatedDateMils());
        cvTodo.put(TodoStackContract.TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoStackContract.TodoEntry.LAST_UPDATED_DATE, getUpdatedDateMils());
        String selectionMoveTodo =
                TodoStackContract.TodoEntry._ID + " LIKE " + mData.id;
        todoStackDb.update(TodoStackContract.TodoEntry.TABLE_NAME, cvTodo, selectionMoveTodo, null);
    }

    private String getUpdatedDateString() {
        Calendar today = Calendar.getInstance();
        String updateString = String.format("%4s%2s%2s",
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH) + 1,
                today.get(Calendar.DAY_OF_MONTH));
        return updateString;
    }

    private long getUpdatedDateMils() {
        Calendar now = Calendar.getInstance();
        return now.getTimeInMillis();
    }
}
