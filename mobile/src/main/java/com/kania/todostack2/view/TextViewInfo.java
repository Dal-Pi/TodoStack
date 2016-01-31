package com.kania.todostack2.view;

/**
 * Created by user on 2016-01-20.
 */
public class TextViewInfo {
    public static final int TYPE_SUBJECT = 1;
    public static final int TYPE_TASK_TODO = 2;
    public static final int TYPE_DATE_TODO = 3;

    public static final String DELIMITER_ID = "/";

    public int left;
    public int top;
    public int bottom;
    public int right;

    public int type;
    public String id;

    public TextViewInfo(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

        this.type = 0;
        this.id = "";
    }
}
