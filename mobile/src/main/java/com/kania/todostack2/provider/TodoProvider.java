package com.kania.todostack2.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.data.TodoStackDbHelper;

import static com.kania.todostack2.TodoStackContract.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016-01-11.
 */
public class TodoProvider {

    public static TodoProvider instance;

    private static Context context;

    private static TodoStackDbHelper dbHelper;
    private static SQLiteDatabase todoStackDb;

    private static HashMap<Integer, SubjectData> subjectMap;
    private static HashMap<Integer, TodoData> todoMap;

    private static ArrayList<SubjectData> subjectList;
    private static ArrayList<TodoData> todoList;

    private TodoProvider(Context context) {
        this.context = context;

        subjectMap = new HashMap<Integer, SubjectData>();
        subjectList = new ArrayList<SubjectData>();
        todoMap = new HashMap<Integer, TodoData>();
        todoList = new ArrayList<TodoData>();
    }

    public static void initData() {
        dbHelper = new TodoStackDbHelper(context);
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
        Cursor subjectCursor = todoStackDb.query(SubjectEntry.TABLE_NAME,
                projection, null, null, null, null, null);
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
            subjectMap.put(subject.id, subject);
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
                TodoEntry.LOCATION
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
            todo.subjectId = todoCursor.
                    getInt(todoCursor.getColumnIndexOrThrow(TodoEntry.SUBJECT_ORDER));
            todo.date = todoCursor.
                    getString(todoCursor.getColumnIndexOrThrow(TodoEntry.DATE));
            todo.type = todoCursor.
                    getInt(todoCursor.getColumnIndexOrThrow(TodoEntry.TYPE));
            switch (todo.type) {
                case TodoData.TODO_DB_TYPE_PERIOD:
                    todo.timeFrom = todoCursor.
                            getString(todoCursor.getColumnIndexOrThrow(TodoEntry.TIME_FROM));
                    todo.timeTo = todoCursor.
                            getString(todoCursor.getColumnIndexOrThrow(TodoEntry.TIME_TO));
                    break;
                case TodoData.TODO_DB_TYPE_ALLDAY:
                case TodoData.TODO_DB_TYPE_TASK:
                    todo.timeFrom = "";
                    todo.timeTo = "";
                    break;
            }
            todo.location = todoCursor.
                    getString(todoCursor.getColumnIndexOrThrow(TodoEntry.LOCATION));

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

    public static SubjectData getSubjectById(int subId) {
//        Log.d("TodoStack", "[getSubjectById] subId = " + subId);
        return subjectMap.get(subId);
    }

    public static TodoData getTodoById(int todoId) {
//        Log.d("TodoStack", "[getTodoById] subId = " + todoId);
        return todoMap.get(todoId);
    }

    public static int getSubjectCount() {
        return subjectMap.size();
    }
}
