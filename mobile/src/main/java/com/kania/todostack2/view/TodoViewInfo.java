package com.kania.todostack2.view;

/**
 * Created by user on 2016-01-20.
 */
public class TodoViewInfo {
    public static final int TYPE_DATE_TEXT = 0;
    public static final int TYPE_SUBJECT = 1;
    public static final int TYPE_DATE_TODO = 2;
    public static final int TYPE_DELAYED_TODO = 3;
    public static final int TYPE_TASK = 4;
    public static final int TYPE_VIEW_ALL_TASK = 5; //more task option case
    public static final int TYPE_VIEW_ALL_DELAYED_TODO = 6; //more delayed option case
    public static final int TYPE_VIEW_SPECIFIC_DATE_TODO = 7;

    public static final String DELIMITER_ID = "/";

    public int left;
    public int top;
    public int bottom;
    public int right;

    public int type;
    public String id;
    public boolean isToday;

    public TodoViewInfo(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

        this.type = 0;
        this.id = "";
        this.isToday = false;
    }

    public TodoViewInfo(int type, String id, boolean isToday) {
        this.left = 0;
        this.top = 0;
        this.right = 0;
        this.bottom = 0;

        this.type = type;
        this.id = id;
        this.isToday = isToday;
    }
}
