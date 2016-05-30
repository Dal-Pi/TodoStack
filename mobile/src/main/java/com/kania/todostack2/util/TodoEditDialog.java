package com.kania.todostack2.util;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.kania.todostack2.R;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.provider.TodoProvider;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 2016-05-30.
 */
public class TodoEditDialog extends DialogFragment {

    private Callback mCallback;
    private int mSelectedSubjectOrder;
    private int mId;
    private Date mSelectedTodoDate;


    private TextView mTextBtnSubjectName;
    private EditText mEditTodoName;
    private TextView mTextBtnCalendar;
    private CheckBox mChkIsTask;

    private Button mBtnEdit;
    private Button mBtnCancel;

    public interface Callback {
        void onEditTodo(TodoData editedTodo);
    }

    public TodoEditDialog() {
        setCallback(new TodoEditDialog.Callback() {
            @Override
            public void onEditTodo(TodoData editedTodo) {//empty callback
            }
        });
    }

    public static TodoEditDialog newInstance(int id, Callback callback) {
        TodoEditDialog dialog = new TodoEditDialog();
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
        setCancelable(false);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View todoEditView = inflater.inflate(R.layout.dialog_edit_todo, container, false);
        TodoProvider provider = TodoProvider.getInstance(getActivity());
        TodoData td = provider.getTodoById(mId);
        mSelectedSubjectOrder = td.subjectOrder;
        mSelectedTodoDate = new Date(td.date);

        mTextBtnSubjectName =
                (TextView)todoEditView.findViewById(R.id.dialog_edit_todo_select_subject);
        mTextBtnSubjectName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubjectSelectDialog();
            }
        });

        mEditTodoName = (EditText)todoEditView.findViewById(R.id.dialog_edit_todo_subject_name);
        mEditTodoName.setText(td.todoName);

        mTextBtnCalendar = (TextView)todoEditView.findViewById(R.id.dialog_edit_todo_select_date);
        mTextBtnCalendar.setText(TodoStackUtil.getFomatedDateSimple(
                getActivity().getApplicationContext(), new Date(td.date)));
        mTextBtnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateFromDatePicker();
            }
        });

        mChkIsTask = (CheckBox)todoEditView.findViewById(R.id.dialog_edit_todo_istask);
        mChkIsTask.setChecked(td.type == TodoData.TODO_DB_TYPE_TASK);

        mBtnEdit = (Button)todoEditView.findViewById(R.id.dialog_edit_btn_edit);
        mBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInputMethod(mEditTodoName);
                mCallback.onEditTodo(makeTodoData());
                dismiss();
            }
        });

        mBtnCancel = (Button) todoEditView.findViewById(R.id.dialog_edit_btn_cancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setSubject(mSelectedSubjectOrder);

        return todoEditView;
    }

    private void showSubjectSelectDialog() {
        final String dialogTag = SubjectSelectDialog.class.getSimpleName();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment dialog = SubjectSelectDialog.newInstance(
                new SubjectSelectDialog.Callback() {
                    @Override
                    public void onSelectSubject(int order) {
                        setSubject(order);
                    }
                });
        dialog.show(ft, dialogTag);
    }

    private void setSubject(int order) {
        mSelectedSubjectOrder = order;
        TodoProvider provider = TodoProvider.getInstance(getActivity());
        SubjectData sb = provider.getSubjectByOrder(mSelectedSubjectOrder);
        mTextBtnSubjectName.setText(sb.subjectName);
        mTextBtnSubjectName.setTextColor(sb.color);
        mEditTodoName.setTextColor(sb.color);
        mTextBtnCalendar.setTextColor(sb.color);
        mChkIsTask.setTextColor(sb.color);
        mBtnEdit.setTextColor(sb.color);
        mBtnCancel.setTextColor(sb.color);
    }

    private void getDateFromDatePicker() {
        final String dialogTag = TodoDatePickerDialog.class.getSimpleName();
        DialogFragment dialog =
                TodoDatePickerDialog.newInstance(new TodoDatePickerDialog.Callback() {
                    @Override
                    public void onDateSet(Date date) {
                        mSelectedTodoDate = date;
                        mTextBtnCalendar.setText(TodoStackUtil.getFomatedDateSimple(
                                getActivity().getApplicationContext(), mSelectedTodoDate));
                    }
                });
        dialog.show(getFragmentManager(), dialogTag);
    }

    private TodoData makeTodoData() {
        TodoData todo = new TodoData();

        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(mSelectedTodoDate);
        targetDate.set(Calendar.HOUR_OF_DAY, 0);
        targetDate.set(Calendar.MINUTE, 0);
        targetDate.set(Calendar.SECOND, 0);
        targetDate.set(Calendar.MILLISECOND, 0);

        todo.id = mId;
        todo.todoName = mEditTodoName.getText().toString();
        todo.subjectOrder = mSelectedSubjectOrder;
        todo.date = targetDate.getTimeInMillis();
        todo.type = mChkIsTask.isChecked() ?
                TodoData.TODO_DB_TYPE_TASK : TodoData.TODO_DB_TYPE_ALLDAY;
        todo.timeFrom = TodoData.TIME_NOT_EXIST;
        todo.timeTo = TodoData.TIME_NOT_EXIST;
        todo.location = "";

        return todo;
    }

    private void hideInputMethod(EditText edit) {
        InputMethodManager inputManager = (InputMethodManager)getActivity()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(edit.getWindowToken(),0);
    }
}
