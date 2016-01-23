package com.kania.todostack2.view;

/**
 * Created by user on 2016-01-18.
 */
public interface IViewAction {

    void setAllControllerGone();
    void setInputSubjectVisible();

    void setFabToInputTodo(String action, int color, boolean needMove);
    void setFabToInputSubject(String action, int color, boolean needMove);

    void setFabToBase(String action, int color, boolean needMove);

    void finishActivity();
}
