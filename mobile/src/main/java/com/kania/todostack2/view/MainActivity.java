package com.kania.todostack2.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kania.todostack2.R;
import com.kania.todostack2.util.TodoDatePickerDialog;

/**
 * Created by user on 2016-01-14.
 * Activity that present all todos
 */
public class MainActivity extends Activity implements IControllerMediator, View.OnClickListener{

    RelativeLayout controllerInputTodo;
    RelativeLayout controllerInputSubject;
    LinearLayout controllerViewTodo;
    RelativeLayout controllerViewSubject;

    private EditText editYear;
    private EditText editMonth;
    private EditText editDay;

    private Button btnCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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
        switch (mode) {
            case IControllerMediator.MODE_NO_SELECTION:
                controllerInputTodo.setVisibility(View.GONE);
                controllerInputSubject.setVisibility(View.GONE);
                controllerViewTodo.setVisibility(View.GONE);
                controllerViewSubject.setVisibility(View.GONE);
                break;
            case IControllerMediator.MODE_ADD_TODO:
                //TODO
                break;
            case IControllerMediator.MODE_ADD_SUBJECT:
                //TODO
                break;
            case IControllerMediator.MODE_VIEW_TODO:
                //TODO
                break;
            case IControllerMediator.MODE_VIEW_SUBJECT:
                //TODO
                break;
        }
    }
}
