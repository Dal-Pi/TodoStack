package com.kania.todostack2.view;

/**
 * Created by user on 2016-01-18.
 */
public interface IControllerMediator {
    public static final int MODE_NO_SELECTION = 1;
    public static final int MODE_ADD_TODO = 2;
    public static final int MODE_ADD_SUBJECT = 3;
    public static final int MODE_VIEW_TODO = 4;
    public static final int MODE_VIEW_SUBJECT = 5;

    public void setMode(int mode);
}
