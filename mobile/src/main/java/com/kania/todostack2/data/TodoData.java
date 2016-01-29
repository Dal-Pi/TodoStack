package com.kania.todostack2.data;

import android.app.AlertDialog;

import java.util.Date;

/**
 * Created by user on 2016-01-11.
 * for performance, do not use getter or setter method
 */
public class TodoData {
    public static final int TODO_DB_TYPE_ALLDAY = 1;
    public static final int TODO_DB_TYPE_PERIOD = 2;
    public static final int TODO_DB_TYPE_TASK = 3;

//    public static final int TODO_REALTIME_TYPE_TASK = 1;
    public static final int TODO_REALTIME_TYPE_DATE = 2;
    public static final int TODO_REALTIME_TYPE_DELAYED = 3;

    //from DB
    public int id;
    public String todoName;
    public int subjectOrder;
    public String date;
    public int type;
    public String timeFrom;
    public String timeTo;
    public String location;

    //from real time
    public int realtimeType;

    //TODO change structure to bilder
//    public class Builder {
//        public Builder setName(String name) {
//        }
//    }

}
