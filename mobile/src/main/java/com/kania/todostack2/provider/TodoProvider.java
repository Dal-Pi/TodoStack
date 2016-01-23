package com.kania.todostack2.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.data.TodoStackDbHelper;

import static com.kania.todostack2.TodoStackContract.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by user on 2016-01-11.
 */
public class TodoProvider {

    public static TodoProvider instance;

    private Context context;

    private TodoStackDbHelper dbHelper;
    private SQLiteDatabase todoStackDb;

    private static HashMap<Integer, SubjectData> subjectMap;
    private static HashMap<Integer, TodoData> todoMap;

    private TodoProvider(Context context) {
        this.context = context;

        subjectMap = new HashMap<Integer, SubjectData>();
        todoMap = new HashMap<Integer, TodoData>();

        //get data from DB
        initData();
    }

    private void initData() {
        dbHelper = new TodoStackDbHelper(context);
        todoStackDb = dbHelper.getReadableDatabase();

        getSubjectFromDb();
        getTodoFromDb();
    }

    private void getSubjectFromDb() {
        final String[] projection = {
                SubjectEntry._ID,
                SubjectEntry.SUBJECT_NAME,
                SubjectEntry.COLOR,
                SubjectEntry.SEQUENCE
        };
        Cursor subjectCursor = todoStackDb.query(SubjectEntry.TABLE_NAME,
                projection, null, null, null, null, null);
        subjectCursor.moveToFirst();
        while (subjectCursor.moveToNext()) {
            SubjectData subject = new SubjectData();
            subject.id = subjectCursor.
                    getInt(subjectCursor.getColumnIndexOrThrow(SubjectEntry._ID));
            subject.subjectName = subjectCursor.
                    getString(subjectCursor.getColumnIndexOrThrow(SubjectEntry.SUBJECT_NAME));
            try {
                int color = Color.parseColor(subjectCursor.
                        getString(subjectCursor.getColumnIndexOrThrow(SubjectEntry.COLOR)));
                subject.color = color;
            } catch (IllegalArgumentException e) {
                Log.e("TodoStck", "error on getting color from Subject DB");
                subject.color = ColorProvider.getDefaultColor();
            }
            subject.order = subjectCursor.
                    getInt(subjectCursor.getColumnIndexOrThrow(SubjectEntry.SEQUENCE));

            subjectMap.put(subject.id, subject);
        }
    }

    private void getTodoFromDb() {
        final String[] projection = {
                TodoEntry._ID,
                TodoEntry.TODO_NAME,
                TodoEntry.SUBJECT_ID,
                TodoEntry.DATE,
                TodoEntry.TYPE,
                TodoEntry.TIME_FROM,
                TodoEntry.TIME_TO,
                TodoEntry.LOCATION
        };
        Cursor todoCursor = todoStackDb.query(TodoEntry.TABLE_NAME,
                projection, null, null, null, null, null);
        todoCursor.moveToFirst();
        while (todoCursor.moveToNext()) {
            TodoData todo = new TodoData();
            todo.id = todoCursor.
                    getInt(todoCursor.getColumnIndexOrThrow(TodoEntry._ID));
            todo.todoName = todoCursor.
                    getString(todoCursor.getColumnIndexOrThrow(TodoEntry.TODO_NAME));
            todo.subjectId = todoCursor.
                    getInt(todoCursor.getColumnIndexOrThrow(TodoEntry.SUBJECT_ID));
            todo.date = todoCursor.
                    getString(todoCursor.getColumnIndexOrThrow(TodoEntry.DATE));
            todo.type = todoCursor.
                    getInt(todoCursor.getColumnIndexOrThrow(TodoEntry.TYPE));
            switch (todo.type) {
                case TodoData.TYPE_PERIOD:
                    todo.timeFrom = todoCursor.
                            getString(todoCursor.getColumnIndexOrThrow(TodoEntry.TIME_FROM));
                    todo.timeTo = todoCursor.
                            getString(todoCursor.getColumnIndexOrThrow(TodoEntry.TIME_TO));
                    break;
                case TodoData.TYPE_ALLDAY:
                case TodoData.TYPE_TASK:
                    todo.timeFrom = "";
                    todo.timeTo = "";
                    break;
            }
            todo.location = todoCursor.
                    getString(todoCursor.getColumnIndexOrThrow(TodoEntry.LOCATION));

            todoMap.put(todo.id, todo);
        }
    }

    public static TodoProvider getInstance(Context applicationContext) {
        if (instance == null) {
            Log.d("TodoStack", "In case of creation TodoProvider");
            instance = new TodoProvider(applicationContext);
        }
        return instance;
    }

    public static ArrayList<SubjectData> getAllSubject() {
        ArrayList<SubjectData> allSubjectData = new ArrayList<SubjectData>();
        Iterator<Integer> it = subjectMap.keySet().iterator();
        while(it.hasNext()) {
            allSubjectData.add(subjectMap.get(it.next()));
        }
        return allSubjectData;
    }

    public static ArrayList<TodoData> getAllTodo() {
        ArrayList<TodoData> allTodoData = new ArrayList<TodoData>();
        Iterator<Integer> it = todoMap.keySet().iterator();
        while(it.hasNext()) {
            allTodoData.add(todoMap.get(it.next()));
        }
        return allTodoData;
    }

    public static int getSubjectCount() {
        return subjectMap.size();
    }
}
