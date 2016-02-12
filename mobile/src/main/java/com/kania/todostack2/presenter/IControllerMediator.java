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
    int MODE_VIEW_TODO = 4;
    int MODE_VIEW_SUBJECT = 5;

    void setTargetView(IViewAction targetView);

    void initTodoLayout(int layoutWidth, int layoutHeight);
    void refreshTodoLayout(int layoutWidth, int layoutHeight);

    void setMode(int targetMode, Object info);
    void clickBackPressSoftButton();
    void clickFloatingActionButton(Bundle bundle);

    void selectMenuAddSubject();

    void changeSubjectName(String name);
    void changeSubjectColor(int color);
    void moveSubjectOrder(boolean isLeft);
    void deleteSubject();
}
