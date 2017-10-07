package com.mad.logbook.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

//TODO: put code into software doc
//https://stackoverflow.com/questions/20673609/implement-a-datepicker-inside-a-fragment

/**
 * Creates a date picker dialog
 *
 * @author Amritha
 * @date 19-Dec-13
 */
public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener ondateSet;
    private int year, month, day;

    public DatePickerFragment() {
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        ondateSet = ondate;
    }

    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), ondateSet, year, month, day);
    }
}