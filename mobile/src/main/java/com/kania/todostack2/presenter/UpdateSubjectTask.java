package com.kania.todostack2.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoStackDbHelper;
import com.kania.todostack2.provider.TodoProvider;

/**
 * Created by user on 2016-01-29.
 */
public class UpdateSubjectTask extends AsyncTask<Void, Void, Boolean> {

    public static final int SUBJECT_TASK_ADD_SUBJECT = 1;

    private Context mContext;
    private TodoProvider mTodoProvider;
    private TaskEndCallback mCallback;

    private TodoStackDbHelper dbHelper;
    private SQLiteDatabase todoStackDb;

    private int mTaskType = -1;
    private SubjectData mData;

    public interface TaskEndCallback {
        void loadFinished();
    }

    public UpdateSubjectTask(Context context, TaskEndCallback callback) {
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

    public void setData(SubjectData data, int taskType) {
        mData = data;
        mTaskType = taskType;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        dbHelper = new TodoStackDbHelper(mContext);
        todoStackDb = dbHelper.getReadableDatabase();

        if (mData == null || mTaskType == -1) {
            return false;
        }
        switch (mTaskType) {
            case SUBJECT_TASK_ADD_SUBJECT:
                ContentValues cvSub = new ContentValues();
                cvSub.put(TodoStackContract.SubjectEntry.SUBJECT_NAME, mData.subjectName);
                cvSub.put(TodoStackContract.SubjectEntry.COLOR, mData.color);
                cvSub.put(TodoStackContract.SubjectEntry.ORDER, mData.order);
                todoStackDb.insert(TodoStackContract.SubjectEntry.TABLE_NAME, null, cvSub);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result)
            mCallback.loadFinished();
        else
            Log.d("TodoStack", "[onPostExecute] fail to progress subject task");
    }
}
