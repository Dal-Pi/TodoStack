package com.kania.todostack2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kania.todostack2.provider.ColorProvider;

import static com.kania.todostack2.TodoStackContract.*;

/**
 * Created by user on 2016-01-10.
 */
public class TodoStackDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "todostack.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String NOT_NULL = " NOT NULL";

    private static final String SQL_CREATE_SUBJECT_ENTRIES =
            "CREATE TABLE " + SubjectEntry.TABLE_NAME + " (" +
                    SubjectEntry._ID + " INTEGER PRIMARY KEY," +
                    SubjectEntry.SUBJECT_NAME + TEXT_TYPE + COMMA_SEP +
                    SubjectEntry.COLOR + TEXT_TYPE + COMMA_SEP +
                    SubjectEntry.SEQUENCE + INTEGER_TYPE +
                    " )";

    private static final String SQL_CREATE_TODO_ENTRIES =
            "CREATE TABLE " + TodoEntry.TABLE_NAME + " (" +
                    TodoEntry._ID + " INTEGER PRIMARY KEY," +
                    TodoEntry.TODO_NAME + TEXT_TYPE + COMMA_SEP +
                    TodoEntry.SUBJECT_ID + INTEGER_TYPE + COMMA_SEP +
                    TodoEntry.DATE + TEXT_TYPE + COMMA_SEP +
                    TodoEntry.TYPE + TEXT_TYPE + COMMA_SEP +
                    TodoEntry.TIME_FROM + TEXT_TYPE + COMMA_SEP +
                    TodoEntry.TIME_TO + TEXT_TYPE + COMMA_SEP +
                    TodoEntry.LOCATION + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_SUBJECT_ENTRIES =
            "DROP TABLE IF EXISTS " + SubjectEntry.TABLE_NAME;

    private static final String SQL_DELETE_TODO_ENTRIES =
            "DROP TABLE IF EXISTS " + TodoEntry.TABLE_NAME;

    public TodoStackDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SUBJECT_ENTRIES);
        db.execSQL(SQL_CREATE_TODO_ENTRIES);

        //TODO this is stub, will be deleted
        ContentValues cvSub = new ContentValues();
        cvSub.put(SubjectEntry.SUBJECT_NAME, "first subject");
        cvSub.put(SubjectEntry.COLOR, ColorProvider.getRandomColor());
        cvSub.put(SubjectEntry.SEQUENCE, 0);
        db.insert(SubjectEntry.TABLE_NAME, null, cvSub);
        cvSub.put(SubjectEntry.SUBJECT_NAME, "second subject");
        cvSub.put(SubjectEntry.COLOR, ColorProvider.getRandomColor());
        cvSub.put(SubjectEntry.SEQUENCE, 1);
        db.insert(SubjectEntry.TABLE_NAME, null, cvSub);
        cvSub.put(SubjectEntry.SUBJECT_NAME, "third subject");
        cvSub.put(SubjectEntry.COLOR, ColorProvider.getRandomColor());
        cvSub.put(SubjectEntry.SEQUENCE, 2);
        db.insert(SubjectEntry.TABLE_NAME, null, cvSub);
        cvSub.put(SubjectEntry.SUBJECT_NAME, "fourth subject");
        cvSub.put(SubjectEntry.COLOR, ColorProvider.getRandomColor());
        cvSub.put(SubjectEntry.SEQUENCE, 3);
        db.insert(SubjectEntry.TABLE_NAME, null, cvSub);
        cvSub.put(SubjectEntry.SUBJECT_NAME, "fifth subject");
        cvSub.put(SubjectEntry.COLOR, ColorProvider.getRandomColor());
        cvSub.put(SubjectEntry.SEQUENCE, 4);
        db.insert(SubjectEntry.TABLE_NAME, null, cvSub);

        ContentValues cvTodo = new ContentValues();
        cvTodo.put(TodoEntry.TODO_NAME, "todo1 on first");
        cvTodo.put(TodoEntry.SUBJECT_ID, 1);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo6 on first");
        cvTodo.put(TodoEntry.SUBJECT_ID, 1);
        cvTodo.put(TodoEntry.DATE, "20160125");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo2 on second");
        cvTodo.put(TodoEntry.SUBJECT_ID, 2);
        cvTodo.put(TodoEntry.DATE, "20160127");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo3 on third");
        cvTodo.put(TodoEntry.SUBJECT_ID, 3);
        cvTodo.put(TodoEntry.DATE, "20160201");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo4 on second");
        cvTodo.put(TodoEntry.SUBJECT_ID, 2);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo5 on second");
        cvTodo.put(TodoEntry.SUBJECT_ID, 2);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);

        cvTodo.put(TodoEntry.TODO_NAME, "todo11 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo12 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160127");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo13 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo14 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo15 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo16 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo17 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo18 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo19 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo20 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo21 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160101");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_TASK);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);

        cvTodo.put(TodoEntry.TODO_NAME, "todo31 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160121");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo32 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160122");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo33 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160123");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo34 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160124");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo35 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160125");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo36 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160126");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo37 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160127");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo38 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160128");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo39 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160129");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo40 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160130");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
        cvTodo.put(TodoEntry.TODO_NAME, "todo41 on fifth");
        cvTodo.put(TodoEntry.SUBJECT_ID, 4);
        cvTodo.put(TodoEntry.DATE, "20160131");
        cvTodo.put(TodoEntry.TYPE, TodoData.TODO_DB_TYPE_ALLDAY);
        cvTodo.put(TodoEntry.TIME_FROM, "00:00");
        cvTodo.put(TodoEntry.TIME_TO, "24:00");
        cvTodo.put(TodoEntry.LOCATION, "");
        db.insert(TodoEntry.TABLE_NAME, null, cvTodo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SUBJECT_ENTRIES);
        db.execSQL(SQL_DELETE_TODO_ENTRIES);
        onCreate(db);
    }
}
