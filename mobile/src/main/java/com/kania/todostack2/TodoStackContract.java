package com.kania.todostack2;

import android.provider.BaseColumns;

/**
 * Created by user on 2016-01-10.
 * Defines all names that using on this App
 */
public final class TodoStackContract {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public TodoStackContract() {}

    public static class SubjectEntry implements BaseColumns {
        public static final String TABLE_NAME = "subject";
        public static final String SUBJECT_NAME = "name";
        public static final String COLOR = "color";
        public static final String ORDER = "order";
    }

    public static class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo";
        public static final String TODO_NAME = "name";
        public static final String SUBJECT_ID = "sub_id";
        public static final String DATE = "date";
        public static final String TYPE = "type";
        public static final String TIME_FROM = "time_from";
        public static final String TIME_TO = "time_to";
        public static final String LOCATION = "location";
    }
}
