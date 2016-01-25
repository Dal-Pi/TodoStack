package com.kania.todostack2.view;

import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 2016-01-18.
 */
public interface IViewAction {

    void setAllControllerGone();
    void setInputSubjectVisible();

    void setFabToInputTodo(String action, int color, boolean needMove);
    void setFabToInputSubject(String action, int color, boolean needMove);

    void setFabToBase(String action, int color, boolean needMove);

    void setTextViewOnTodoLayout(ArrayList<TextView> alTextView);
    void refreshTodoLayout();

    void setGuideText(String guideText);
    void setGuideText(String guideText, int color);

    void finishActivity();
}
