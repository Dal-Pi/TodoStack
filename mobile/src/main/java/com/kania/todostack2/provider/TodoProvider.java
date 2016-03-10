package com.kania.todostack2.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.data.TodoStackDbHelper;
import com.kania.todostack2.util.TodoStackUtil;

import static com.kania.todostack2.TodoStackContract.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by user on 2016-01-11.
 */
public class TodoProvider {

    public static TodoProvider instance;

    private static Context mContext;

    private static TodoStackDbHelper dbHelper;
    private static SQLiteDatabase todoStackDb;

    private static HashMap<Integer, SubjectData> subjectMap;
    private static HashMap<Integer, TodoData> todoMap;

    private static ArrayList<SubjectData> subjectList;
    private static ArrayList<TodoData> todoList;

    private TodoProvider(Context context) {
        mContext = context;

        subjectMap = new HashMap<Integer, SubjectData>();
        subjectList = new ArrayList<SubjectData>();
        todoMap = new HashMap<Integer, TodoData>();
        todoList = new ArrayList<TodoData>();
    }

    public static void initData() {
        dbHelper = new TodoStackDbHelper(mContext);
        todoStackDb = dbHelper.getReadableDatabase();

        getSubjectFromDb();
        getTodoFromDb();

        todoStackDb.close();
    }

    private static void getSubjectFromDb() {
        if (subjectMap == null)
            subjectMap = new HashMap<Integer, SubjectData>();
        if (subjectList == null)
            subjectList = new ArrayList<SubjectData>();
        subjectMap.clear();
        subjectList.clear();


        final String[] projection = {
                SubjectEntry._ID,
                SubjectEntry.SUBJECT_NAME,
                SubjectEntry.COLOR,
                SubjectEntry.ORDER
        };
        final String sortOrder = SubjectEntry.ORDER + " ASC";
        Cursor subjectCursor = todoStackDb.query(SubjectEntry.TABLE_NAME,
                projection, null, null, null, null, sortOrder);
//        subjectCursor.moveToFirst();
        while (subjectCursor.moveToNext()) {
            SubjectData subject = new SubjectData();
            subject.id = subjectCursor.
                    getInt(subjectCursor.getColumnIndexOrThrow(SubjectEntry._ID));
            subject.subjectName = subjectCursor.
                    getString(subjectCursor.getColumnIndexOrThrow(SubjectEntry.SUBJECT_NAME));
            try {
                int color = subjectCursor.
                        getInt(subjectCursor.getColumnIndexOrThrow(SubjectEntry.COLOR));
                subject.color = color;
            } catch (IllegalArgumentException e) {
                Log.e("TodoStck", "error on getting color from Subject DB");
                subject.color = ColorProvider.getDefaultColor();
            }
            subject.order = subjectCursor.
                    getInt(subjectCursor.getColumnIndexOrThrow(SubjectEntry.ORDER));

            subjectList.add(subject);
            subjectMap.put(subject.order, subject);
        }
    }

    private static void getTodoFromDb() {
        if (todoMap == null)
            todoMap = new HashMap<Integer, TodoData>();
        if (todoList == null)
            todoList = new ArrayList<TodoData>();
        todoMap.clear();
        todoList.clear();

        final String[] projection = {
                TodoEntry._ID,
                TodoEntry.TODO_NAME,
                TodoEntry.SUBJECT_ORDER,
                TodoEntry.DATE,
                TodoEntry.TYPE,
                TodoEntry.TIME_FROM,
                TodoEntry.TIME_TO,
                TodoEntry.LOCATION,
                TodoEntry.CREATED_DATE,
                TodoEntry.LAST_UPDATED_DATE
        };
        Cursor todoCursor = todoStackDb.query(TodoEntry.TABLE_NAME,
                projection, null, null, null, null, null);
//        todoCursor.moveToFirst();
        while (todoCursor.moveToNext()) {
            TodoData todo = new TodoData();
            todo.id = todoCursor.
                    getInt(todoCursor.getColumnIndexOrThrow(TodoEntry._ID));
            todo.todoName = todoCursor.
                    getString(todoCursor.getColumnIndexOrThrow(TodoEntry.TODO_NAME));
            todo.subjectOrder = todoCursor.
                    getInt(todoCursor.getColumnIndexOrThrow(TodoEntry.SUBJECT_ORDER));
            todo.date = todoCursor.
                    getLong(todoCursor.getColumnIndexOrThrow(TodoEntry.DATE));
            todo.type = todoCursor.
                    getInt(todoCursor.getColumnIndexOrThrow(TodoEntry.TYPE));
            todo.timeFrom = todoCursor.
                    getLong(todoCursor.getColumnIndexOrThrow(TodoEntry.TIME_FROM));
            todo.timeTo = todoCursor.
                    getLong(todoCursor.getColumnIndexOrThrow(TodoEntry.TIME_TO));
            todo.location = todoCursor.
                    getString(todoCursor.getColumnIndexOrThrow(TodoEntry.LOCATION));
            todo.created = todoCursor.getLong(
                    todoCursor.getColumnIndexOrThrow(TodoEntry.CREATED_DATE));
            todo.lastUpdated = todoCursor.getLong(
                    todoCursor.getColumnIndexOrThrow(TodoEntry.LAST_UPDATED_DATE));

            todoList.add(todo);
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
//        Iterator<Integer> it = subjectMap.keySet().iterator();
//        while(it.hasNext()) {
//            allSubjectData.add(subjectMap.get(it.next()));
//        }
        for (SubjectData subject : subjectList){
            allSubjectData.add(subject);
        }
        return allSubjectData;
    }

    public static ArrayList<TodoData> getAllTodo() {
        ArrayList<TodoData> allTodoData = new ArrayList<TodoData>();
//        Iterator<Integer> it = todoMap.keySet().iterator();
//        while(it.hasNext()) {
//            allTodoData.add(todoMap.get(it.next()));
//        }
        for (TodoData todo : todoList){
            allTodoData.add(todo);
        }

        return allTodoData;
    }

    public static ArrayList<TodoData> getTodoList(int subjectOrder) {
        ArrayList<TodoData> todos = new ArrayList<TodoData>();
        for (TodoData todo : todoList){
            if (todo.subjectOrder == subjectOrder) {
                todos.add(todo);
            }
        }

        return todos;
    }

    public static SubjectData getSubjectByOrder(int subOrder) {
//        Log.d("TodoStack", "[getSubjectByOrder] subOrder = " + subOrder);
        return subjectMap.get(subOrder);
    }

    public static TodoData getTodoById(int todoId) {
//        Log.d("TodoStack", "[getTodoById] subId = " + todoId);
        return todoMap.get(todoId);
    }

    public static int getSubjectCount() {
        return subjectMap.size();
    }

    public static int getTodoCount(int subjectOrder) {
        int ret = 0;
        for (TodoData td : todoList) {
            if (td.subjectOrder == subjectOrder)
                ret++;
        }

        return ret;
    }
}
