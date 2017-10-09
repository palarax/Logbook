/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mad.logbook.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.mad.logbook.R;
import com.mad.logbook.Utils;
import com.mad.logbook.activity.DrivingLesson;
import com.mad.logbook.db.DatabaseHelper;
import com.mad.logbook.interfaces.HomeContract;
import com.mad.logbook.model.Lesson;
import com.mad.logbook.model.Users;
import com.mad.logbook.presenter.HomePresenter;
import com.txusballesteros.widgets.FitChart;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Home fragment that initiates a lesson
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 * @date 09-Sep-17
 */

public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        HomeContract.View {

    private TextView numberOfLessons, hoursDroveNight, hoursDroveDay, mNameText, mLicenseText,
            mStateText, mProgressText, mDobText, mHoursCompleted;

    private FitChart mFitChart;
    private BarChart chart;

    private HomeContract.Presenter mHomePresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLessonInformation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(getString(R.string.fragment_title_home));
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Set student
        mHomePresenter = new HomePresenter(this);

        chart = view.findViewById(R.id.chart);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                getUserData();

            }
        });
        numberOfLessons = view.findViewById(R.id.txt_drives);
        hoursDroveNight = view.findViewById(R.id.hours_drove_night);
        hoursDroveDay = view.findViewById(R.id.hours_drove_day);
        mNameText = view.findViewById(R.id.txt_name);
        mLicenseText = view.findViewById(R.id.txt_license);
        mDobText = view.findViewById(R.id.txt_dob);
        mStateText = view.findViewById(R.id.txt_state);
        mProgressText = view.findViewById(R.id.txt_completed);
        mFitChart = view.findViewById(R.id.fitChart);
        mHoursCompleted = view.findViewById(R.id.hours_completed);

        updateLessonInformation();
    }

    /**
     * Populate fit chart with data
     */
    private void populateFitChart() {
        mFitChart.setMinValue(0f);
        mFitChart.setMaxValue(120f);
        double hoursCompleted = DatabaseHelper.getCurrentUser().getHoursCompleted() / 1000 / 60 / 60;
        mHoursCompleted.setText(getString(R.string.chart_hours_completed, hoursCompleted));
        mFitChart.setValue((float) hoursCompleted);
    }

    /**
     * Populates view with user data
     */
    private void populateUserData() {
        Users student = DatabaseHelper.getCurrentUser();
        mNameText.setText(student.getUserName()
                + " " + student.getUserSurname());
        mLicenseText.setText(Long.toString(student.getLicenceNumber()));
        mDobText.setText(student.getDob());
        mStateText.setText(student.getState());

        if ((student.getHoursCompleted() / 1000 / 60 / 60) >= 120) {
            mProgressText.setText(this.getString(R.string.profile_completed));
            mProgressText.setTextColor(getResources().getColor(R.color.licence_color));
        } else {
            mProgressText.setText(this.getString(R.string.profile_in_progress));
            mProgressText.setTextColor(getResources().getColor(R.color.in_progress));
        }
    }

    /**
     * Populates bar graph with lesson data related to the current user
     */
    private void populateBarGraph() {
        ArrayList<ArrayList<BarEntry>> dataSet = new ArrayList<>();
        ArrayList<String> lessonMonths = new ArrayList<>();
        try {
            lessonMonths = mHomePresenter.getLessonMonths(DatabaseHelper.getUserLessons());
            dataSet = mHomePresenter.getGraphDataSet(DatabaseHelper.getUserLessons(), lessonMonths);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        BarDataSet barDataSet1 = new BarDataSet(dataSet.get(0), getString(R.string.distance_units));
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet2 = new BarDataSet(dataSet.get(1), getString(R.string.xAxis_day_hours));
        barDataSet2.setColor(Color.rgb(0, 0, 155));
        BarDataSet barDataSet3 = new BarDataSet(dataSet.get(2), getString(R.string.xAxis_night_hours));
        barDataSet2.setColor(Color.rgb(155, 0, 0));

        ArrayList<BarDataSet> lessonDataSet = new ArrayList<>();
        lessonDataSet.add(barDataSet1);
        lessonDataSet.add(barDataSet2);
        lessonDataSet.add(barDataSet3);

        chart.setData(new BarData(lessonMonths, lessonDataSet));
        chart.setDescription("");
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

    /**
     * Updates lesson information on GUI
     */
    private void updateLessonInformation() {
        List<Lesson> userLessons = DatabaseHelper.getUserLessons();
        numberOfLessons.setText(Integer.toString(userLessons.size()));
        double[] dayNightTime = new double[]{0,0};
        try {
            dayNightTime = mHomePresenter.getDayNightDroveLesson(userLessons);
        } catch (ParseException e) {
            Log.e("HomeFragment", "Formatting exception: " + e);
            Toast.makeText(getContext(), getString(R.string.generic_error, e), Toast.LENGTH_SHORT).show();
        }
        hoursDroveNight.setText(getString(R.string.profile_night_hours,dayNightTime[1]/1000/60/60));
        hoursDroveDay.setText(getString(R.string.profile_day_hours,dayNightTime[0]/1000/60/60));
        //Update user hours completed
        DatabaseHelper.getCurrentUser().setHoursCompleted(dayNightTime[0] + dayNightTime[1]);
        DatabaseHelper.getCurrentUser().save();

        populateBarGraph();
        populateUserData();
        populateFitChart();
    }

    /**
     * Get initial lesson data from user
     */
    public void getUserData() {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.input_form, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());
        alertDialogBuilder.setView(promptsView);

        final EditText editLpn = promptsView.findViewById(R.id.edit_lpn);
        final EditText editStartOdometer = promptsView.findViewById(R.id.edit_start_odometer);
        final EditText editSupervisorLicence = promptsView.findViewById(R.id.edit_supervisor_license);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do nothing here because we will override everything
                            }
                        })
                .setNegativeButton(getString(R.string.dialog_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        //override ok button to display errors instead of closing
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean editEmpty = true;
                //check if all data is complete
                if (TextUtils.isEmpty(editLpn.getText().toString())) {
                    editLpn.setError(getString(R.string.error_lpn_null));
                    editEmpty = false;
                }
                if (TextUtils.isEmpty(editStartOdometer.getText().toString())) {
                    editStartOdometer.setError(getString(R.string.error_odometer_null));
                    editEmpty = false;
                }
                if (TextUtils.isEmpty(editSupervisorLicence.getText().toString())) {
                    editSupervisorLicence.setError(getString(R.string.error_supervisor_null));
                    editEmpty = false;
                }

                if (editEmpty) {
                    alertDialog.dismiss();
                    Intent intent = new Intent(getActivity(), DrivingLesson.class);
                    Lesson lesson = new Lesson(
                            editLpn.getText().toString(),
                            0, Long.parseLong(editSupervisorLicence.getText().toString()),
                            DatabaseHelper.getCurrentUser().getLicenceNumber(),
                            Integer.parseInt(editStartOdometer.getText().toString()),
                            0, 0, Utils.getTime(), null);
                    lesson.save();
                    intent.putExtra(Utils.LESSON_ID, lesson.getId());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        mHomePresenter = presenter;
    }
}
