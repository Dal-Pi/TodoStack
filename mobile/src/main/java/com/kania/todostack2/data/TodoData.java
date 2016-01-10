package com.kania.todostack2.data;

import android.app.AlertDialog;

import java.util.Date;

/**
 * Created by user on 2016-01-11.
 * for performance, do not use getter or setter method
 */
public class TodoData {
    public static final int TYPE_ALLDAY = 1;
    public static final int TYPE_PERIOD = 2;
    public static final int TYPE_TASK = 3;

    public int id;
    public String todoName;
    public int subjectId;
    public String date;
    public int type;
    public String timeFrom;
    public String timeTo;
    public String location;

    //TODO change structure to bilder
//    public class Builder {
//        public Builder setName(String name) {
//        }
//    }

}
