package com.kania.todostack2.presenter;

import android.os.Bundle;

import com.kania.todostack2.view.IViewAction;

/**
 * Created by user on 2016-01-13.
 */
public interface IControllerMediator {

    int MODE_INITIAL_SETUP = 0;
    int MODE_NO_SELECTION = 1;
    int MODE_ADD_TODO = 2;
    int MODE_ADD_SUBJECT = 3;
    int MODE_VIEW_TODO_ONELINE = 4;
    int MODE_VIEW_TODO_TWOLINE = 5;
    int MODE_VIEW_TODO_THREELINE = 6;
    int MODE_VIEW_SUBJECT = 7;

    //first, change mode
    //second, notify to View
    //third, notify to FAB
    //fourth, change guide text

    void setMediator(IViewAction mediator);

    void initTodoLayout(int layoutWidth, int layoutHeight);

    void setMode(int targetMode);
    void clickBackPressSoftButton();
    void clickFloatingActionButton(Bundle bundle);
}
