package com.kania.todostack2.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kania.todostack2.R;
import com.kania.todostack2.util.TodoDatePickerDialog;

/**
 * Created by user on 2016-01-14.
 * Activity that present all todos
 */
public class MainActivity extends Activity implements View.OnClickListener{

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
        editYear = (EditText) findViewById(R.id.main_iib_edit_year);
        editYear.setOnClickListener(this);
        editMonth = (EditText) findViewById(R.id.main_iib_edit_month);
        editMonth.setOnClickListener(this);
        editDay = (EditText) findViewById(R.id.main_iib_edit_day);
        editDay.setOnClickListener(this);

        btnCalendar = (Button) findViewById(R.id.main_iib_btn_calendar);
        btnCalendar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_iib_btn_calendar:
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
}
