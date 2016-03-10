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
    public static final int DATABASE_VERSION = 2;
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
                    SubjectEntry.ORDER + INTEGER_TYPE +
                    " )";

    private static final String SQL_CREATE_TODO_ENTRIES =
            "CREATE TABLE " + TodoEntry.TABLE_NAME + " (" +
                    TodoEntry._ID + " INTEGER PRIMARY KEY," +
                    TodoEntry.TODO_NAME + TEXT_TYPE + COMMA_SEP +
                    TodoEntry.SUBJECT_ORDER + INTEGER_TYPE + COMMA_SEP +
                    TodoEntry.DATE + INTEGER_TYPE + COMMA_SEP +
                    TodoEntry.TYPE + TEXT_TYPE + COMMA_SEP +
                    TodoEntry.TIME_FROM + INTEGER_TYPE + COMMA_SEP +
                    TodoEntry.TIME_TO + INTEGER_TYPE + COMMA_SEP +
                    TodoEntry.LOCATION + TEXT_TYPE + COMMA_SEP +
                    TodoEntry.CREATED_DATE + INTEGER_TYPE + COMMA_SEP +
                    TodoEntry.LAST_UPDATED_DATE + INTEGER_TYPE +
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SUBJECT_ENTRIES);
        db.execSQL(SQL_DELETE_TODO_ENTRIES);
        onCreate(db);
    }
}
