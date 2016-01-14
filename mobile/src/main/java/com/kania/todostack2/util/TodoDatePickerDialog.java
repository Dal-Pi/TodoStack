package com.kania.todostack2.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by user on 2015-11-10.
 */
public class TodoDatePickerDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private Callback mCallback;

    public interface Callback {
        void onDateSet(int year, int monthOfYear, int dayOfMonth);
    }

    public TodoDatePickerDialog() {
        //avoid to call default constructor
        setCallback(new TodoDatePickerDialog.Callback() {
            @Override
            public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
                //empty callback
            }
        });
    }

    public static TodoDatePickerDialog newInstance(Callback callback) {
        TodoDatePickerDialog dialog = new TodoDatePickerDialog();
        dialog.setCallback(callback);
        return dialog;
    }

    private void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCallback.onDateSet(year, monthOfYear + 1, dayOfMonth);
    }
}