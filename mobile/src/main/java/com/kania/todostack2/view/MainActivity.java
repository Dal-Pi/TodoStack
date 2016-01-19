package com.kania.todostack2.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kania.todostack2.R;
import com.kania.todostack2.presenter.TodoStackPresenter;
import com.kania.todostack2.util.TodoDatePickerDialog;

/**
 * Created by user on 2016-01-14.
 * Activity that present all todos
 */
public class MainActivity extends Activity implements IControllerMediator, View.OnClickListener{

    private TodoStackPresenter presenter;

    private RelativeLayout controllerInputTodo;
    private RelativeLayout controllerInputSubject;
    private LinearLayout controllerViewTodo;
    private RelativeLayout controllerViewSubject;

    private EditText editYear;
    private EditText editMonth;
    private EditText editDay;

    private Button btnCalendar;

    private FrameLayout todoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        presenter = new TodoStackPresenter(this);

        initControlView();
    }

    private void initControlView() {
        controllerInputTodo = (RelativeLayout) findViewById(R.id.main_layout_todo_input_mode);
        controllerInputSubject = (RelativeLayout) findViewById(R.id.main_layout_subject_input_mode);
        controllerViewTodo = (LinearLayout) findViewById(R.id.main_layout_todo_viewer_mode);
        controllerViewSubject = (RelativeLayout) findViewById(R.id.main_layout_subject_viewer_mode);

        editYear = (EditText) findViewById(R.id.main_edit_input_year);
        editYear.setOnClickListener(this);
        editMonth = (EditText) findViewById(R.id.main_edit_input_month);
        editMonth.setOnClickListener(this);
        editDay = (EditText) findViewById(R.id.main_edit_input_day);
        editDay.setOnClickListener(this);

        btnCalendar = (Button) findViewById(R.id.main_btn_input_calendar);
        btnCalendar.setOnClickListener(this);

        todoLayout = (FrameLayout) findViewById(R.id.main_lf_todo_layout);
        presenter.initTodoLayoutInfo(todoLayout.getWidth(), todoLayout.getHeight());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_input_calendar:
                getDateFromDatePicker();
                break;
        }
    }

    private void getDateFromDatePicker() {
        final String dialogTag = TodoDatePickerDialog.class.getSimpleName();
        DialogFragment dialog =
                TodoDatePickerDialog.newInstance(new TodoDatePickerDialog.Callback() {
                    @Override
                    public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
                        editYear.setText("" + year);
                        editMonth.setText("" + monthOfYear);
                        editDay.setText("" + dayOfMonth);
                    }
                });
        dialog.show(getFragmentManager(), dialogTag);
    }

    @Override
    public void setMode(int mode) {
        //checking
        if (controllerInputTodo == null
                || controllerInputSubject == null
                || controllerViewTodo == null
                || controllerViewSubject == null) {
            Log.e("TodoStack", "there is(are) null controller");
            return;
        }
        setEachControllerVisibility(controllerInputTodo, MODE_ADD_TODO, mode);
        setEachControllerVisibility(controllerInputSubject, MODE_ADD_SUBJECT, mode);
        setEachControllerVisibility(controllerViewTodo, MODE_VIEW_TODO, mode);
        setEachControllerVisibility(controllerViewSubject, MODE_VIEW_SUBJECT, mode);
    }

    private void setEachControllerVisibility(View view ,int ownMode, int targetMode){
        boolean isVisible = (view.getVisibility() == View.VISIBLE);
        boolean isTarget = (ownMode == targetMode);

        if (isVisible && !isTarget) {
            view.setVisibility(View.INVISIBLE);
        } else if (!isVisible && isTarget) {
            view.setVisibility(View.VISIBLE);
        }
    }
}
