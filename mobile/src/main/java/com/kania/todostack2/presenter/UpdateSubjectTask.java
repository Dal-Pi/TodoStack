package com.kania.todostack2.presenter;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kania.todostack2.R;
import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoStackDbHelper;
import com.kania.todostack2.provider.TodoProvider;

/**
 * Created by user on 2016-01-29.
 */
public class UpdateSubjectTask extends AsyncTask<Void, Void, Boolean> {

    public static final int SUBJECT_TASK_ADD_SUBJECT = 1;
    public static final int SUBJECT_TASK_MODIFY_NAME = 2;
    public static final int SUBJECT_TASK_MODIFY_COLOR = 3;
    public static final int SUBJECT_TASK_MOVE_LEFT = 4;
    public static final int SUBJECT_TASK_MOVE_RIGHT = 5;
    public static final int SUBJECT_TASK_DELETE_SUBJECT = 6;

    public static final int TEMP_ORDER = -1;

    public static final int DIRECTION_LEFT = -1;
    public static final int DIRECTION_RIGHT = 1;

    private ProgressDialog mProgressDialog;

    private Context mContext;
    private TaskEndCallback mCallback;

    private TodoStackDbHelper dbHelper;
    private SQLiteDatabase todoStackDb;

    private int mTaskType = -1;
    private SubjectData mData;

    public interface TaskEndCallback {
        void updateFinished();
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
                public void updateFinished() {
                }
            };
        }
    }

    public void setData(SubjectData data, int taskType) {
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
                case SUBJECT_TASK_ADD_SUBJECT:
                    addSubject();
                    break;
                case SUBJECT_TASK_MODIFY_NAME:
                    modifySubjectName();
                    break;
                case SUBJECT_TASK_MODIFY_COLOR:
                    modifySubjectcolor();
                    break;
                case SUBJECT_TASK_MOVE_LEFT:
                    moveSubject(true);
                    break;
                case SUBJECT_TASK_MOVE_RIGHT:
                    moveSubject(false);
                    break;
                case SUBJECT_TASK_DELETE_SUBJECT:
                    deleteSubject();
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
            Log.d("TodoStack", "[onPostExecute] fail to progress subject task");
    }

    private void addSubject() {
        ContentValues cvAddSub = new ContentValues();
        cvAddSub.put(TodoStackContract.SubjectEntry.SUBJECT_NAME, mData.subjectName);
        cvAddSub.put(TodoStackContract.SubjectEntry.COLOR, mData.color);
        cvAddSub.put(TodoStackContract.SubjectEntry.ORDER, mData.order);
        todoStackDb.insert(TodoStackContract.SubjectEntry.TABLE_NAME, null, cvAddSub);
    }

    private void modifySubjectName() {
        ContentValues cvUpdateSubName = new ContentValues();
        cvUpdateSubName.put(TodoStackContract.SubjectEntry.SUBJECT_NAME,
                mData.subjectName);
        String updateSelectionSubName =
                TodoStackContract.SubjectEntry._ID + " LIKE " + mData.id;
        todoStackDb.update(TodoStackContract.SubjectEntry.TABLE_NAME,
                cvUpdateSubName, updateSelectionSubName, null);
    }

    private void modifySubjectcolor() {
        ContentValues cvUpdateSubColor = new ContentValues();
        cvUpdateSubColor.put(TodoStackContract.SubjectEntry.COLOR, mData.color);
        String updateSelectionSubColor =
                TodoStackContract.SubjectEntry._ID + " LIKE " + mData.id;
        todoStackDb.update(TodoStackContract.SubjectEntry.TABLE_NAME,
                cvUpdateSubColor, updateSelectionSubColor, null);
    }

    private void moveSubject(boolean isLeft) {
        int direction = isLeft ? DIRECTION_LEFT : DIRECTION_RIGHT;
        ContentValues cvUpdateSubOrder = new ContentValues();
        ContentValues cvUpdateTodoOrder = new ContentValues();

        //set target to temp
        cvUpdateTodoOrder.put(TodoStackContract.TodoEntry.SUBJECT_ORDER, TEMP_ORDER);
        cvUpdateSubOrder.put(TodoStackContract.SubjectEntry.ORDER, TEMP_ORDER);
        String updateSelectionTodoSetTemp =
                TodoStackContract.TodoEntry.SUBJECT_ORDER + " LIKE " + mData.order;
        String updateSelectionSubSetTemp =
                TodoStackContract.SubjectEntry.ORDER + " LIKE " + mData.order;
        todoStackDb.update(TodoStackContract.TodoEntry.TABLE_NAME,
                cvUpdateTodoOrder, updateSelectionTodoSetTemp, null);
        todoStackDb.update(TodoStackContract.SubjectEntry.TABLE_NAME,
                cvUpdateSubOrder, updateSelectionSubSetTemp, null);

        //move left one to right
        cvUpdateTodoOrder.put(TodoStackContract.TodoEntry.SUBJECT_ORDER, mData.order);
        cvUpdateSubOrder.put(TodoStackContract.SubjectEntry.ORDER, mData.order);
        String updateSelectionTodoPull =
                TodoStackContract.TodoEntry.SUBJECT_ORDER + " LIKE " + (mData.order + direction);
        String updateSelectionSubPull =
                TodoStackContract.SubjectEntry.ORDER + " LIKE " + (mData.order + direction);
        todoStackDb.update(TodoStackContract.TodoEntry.TABLE_NAME,
                cvUpdateTodoOrder, updateSelectionTodoPull, null);
        todoStackDb.update(TodoStackContract.SubjectEntry.TABLE_NAME,
                cvUpdateSubOrder, updateSelectionSubPull, null);

        //return target to left
        cvUpdateTodoOrder.put(TodoStackContract.TodoEntry.SUBJECT_ORDER, mData.order + direction);
        cvUpdateSubOrder.put(TodoStackContract.SubjectEntry.ORDER, mData.order + direction);
        String updateSelectionTodoMove =
                TodoStackContract.TodoEntry.SUBJECT_ORDER + " LIKE " + TEMP_ORDER;
        String updateSelectionSubMove =
                TodoStackContract.SubjectEntry.ORDER + " LIKE " + TEMP_ORDER;
        todoStackDb.update(TodoStackContract.TodoEntry.TABLE_NAME,
                cvUpdateTodoOrder, updateSelectionTodoMove, null);
        todoStackDb.update(TodoStackContract.SubjectEntry.TABLE_NAME,
                cvUpdateSubOrder, updateSelectionSubMove, null);
    }

    private void deleteSubject() {
        //prevent case when remain todo on subject
        String selectionDeleteTodosOnSubject =
                TodoStackContract.TodoEntry.SUBJECT_ORDER + " LIKE " + mData.order;
        todoStackDb.delete(TodoStackContract.TodoEntry.TABLE_NAME,
                selectionDeleteTodosOnSubject, null);
        String selectionDeleteSubject =
                TodoStackContract.SubjectEntry.ORDER + " LIKE " + mData.order;
        todoStackDb.delete(TodoStackContract.SubjectEntry.TABLE_NAME, selectionDeleteSubject, null);
    }
}
