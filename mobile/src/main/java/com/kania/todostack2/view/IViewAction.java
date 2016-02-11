package com.kania.todostack2.view;

import android.text.Spannable;
import android.text.SpannableString;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by user on 2016-01-18.
 */
public interface IViewAction {
    void setActionBarText(String title, int color);

    void setNowBusy(boolean nowBusy);
    boolean getNowBusy();

    void setAllControllerGone();
    void setInputSubjectVisible();
    void setInputTodoVisible(int color);
    void setViewSubjectVisible(int color, boolean leftEnable, boolean RightEnable);
    void setViewTodoVisible(SpannableString spannableString);

    void setTagOnTodoTextView(TextViewInfo info);

    void setFab(String action, int color, boolean needMove);

    void setFabToBase(String action, int color, boolean needMove);

    void clearTodoLayout();
    void setTextViewsOnTodoLayout(ArrayList<TextView> alTextView);
    void setTextViewOnTodoLayout(TextView textView);
    void refreshTodoLayout();

    void setGuideText(String guideText);
    void setGuideText(String guideText, int color);

    void finishActivity();
}
