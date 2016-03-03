package com.kania.todostack2.util;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kania.todostack2.R;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.presenter.UpdateTodoTask;
import com.kania.todostack2.provider.TodoProvider;

/**
 * Created by user on 2016-02-12.
 */
public class TodoDoneDialog extends DialogFragment {

    private Callback mCallback;
    private int mId;

    public interface Callback {
        void onDeleteTodo(int id);
        void onMoveTodo(int id, int moveType);
        void onCancelSelected();
    }

    public TodoDoneDialog() {
        setCallback(new TodoDoneDialog.Callback() {
            @Override
            public void onDeleteTodo(int id) {//empty callback
            }
            @Override
            public void onMoveTodo(int id, int moveType) {//empty callback
            }
            @Override
            public void onCancelSelected() {//empty callback
            }
        });
    }

    public static TodoDoneDialog newInstance(int id, Callback callback) {
        TodoDoneDialog dialog = new TodoDoneDialog();
        dialog.setCallback(callback);
        dialog.setIds(id);
        return dialog;
    }

    private void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void setIds(int id) {
        mId = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
//        setCancelable(false);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View todoDoneView = inflater.inflate(R.layout.dialog_done_todo, container, false);
        TodoProvider provider = TodoProvider.getInstance(getActivity());
        TodoData td = provider.getTodoById(mId);
        int subjectColor = provider.getSubjectByOrder(td.subjectOrder).color;
        TextView textTodoName =
                (TextView) todoDoneView.findViewById(R.id.dialog_done_todo_todoname);
        textTodoName.setText(td.todoName);
        textTodoName.setTextColor(subjectColor);

        Button btnDelete =
                (Button) todoDoneView.findViewById(R.id.dialog_btn_done_todo_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onDeleteTodo(mId);
                dismiss();
            }
        });
        btnDelete.setTextColor(subjectColor);

        Button btnCancel =
                (Button) todoDoneView.findViewById(R.id.dialog_btn_done_todo_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onCancelSelected();
                dismiss();
            }
        });
        btnCancel.setTextColor(subjectColor);

        TextView textBtnTomorrow =
                (TextView) todoDoneView.findViewById(R.id.dialog_done_todo_move_tomorrow);
        textBtnTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMoveTodo(mId, UpdateTodoTask.TODO_MOVE_OPTION_TOMORROW);
                dismiss();
            }
        });
        textBtnTomorrow.setTextColor(subjectColor);

        TextView textBtnNextWeek =
                (TextView) todoDoneView.findViewById(R.id.dialog_done_todo_move_next_week);
        textBtnNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMoveTodo(mId, UpdateTodoTask.TODO_MOVE_OPTION_NEXT_WEEK);
                dismiss();
            }
        });
        textBtnNextWeek.setTextColor(subjectColor);

        TextView textBtnNextMonth =
                (TextView) todoDoneView.findViewById(R.id.dialog_done_todo_move_next_month);
        textBtnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMoveTodo(mId, UpdateTodoTask.TODO_MOVE_OPTION_NEXT_MONTH);
                dismiss();
            }
        });
        textBtnNextMonth.setTextColor(subjectColor);

        TextView textBtnNextYear =
                (TextView) todoDoneView.findViewById(R.id.dialog_done_todo_move_next_year);
        textBtnNextYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMoveTodo(mId, UpdateTodoTask.TODO_MOVE_OPTION_NEXT_YEAR);
                dismiss();
            }
        });
        textBtnNextYear.setTextColor(subjectColor);

        TextView textBtnToday =
                (TextView) todoDoneView.findViewById(R.id.dialog_done_todo_move_today);
        textBtnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMoveTodo(mId, UpdateTodoTask.TODO_MOVE_OPTION_TODAY);
                dismiss();
            }
        });
        textBtnToday.setTextColor(subjectColor);

        TextView textBtnTask =
                (TextView) todoDoneView.findViewById(R.id.dialog_done_todo_move_task);
        textBtnTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMoveTodo(mId, UpdateTodoTask.TODO_MOVE_OPTION_TASK);
                dismiss();
            }
        });
        if (td.type == TodoData.TODO_DB_TYPE_TASK) {
            textBtnTask.setTextColor(
                    getActivity().getResources().getColor(R.color.color_lightgray));
            textBtnTask.setEnabled(false);
        } else {
            textBtnTask.setTextColor(subjectColor);
        }

        return todoDoneView;
    }
}