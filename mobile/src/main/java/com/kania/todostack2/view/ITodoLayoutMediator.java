package com.kania.todostack2.view;

/**
 * Created by user on 2016-05-03.
 */
public interface ITodoLayoutMediator {
//    int _MODE_INITIAL_SETUP = 0;
    int MODE_NO_SELECTION = 1;
    int MODE_ADD_TODO = 2;
    int MODE_ADD_SUBJECT = 3;
    int MODE_VIEW_TODO = 4;
    int MODE_VIEW_SUBJECT = 5;

    public void changeMode(int mode);
}
