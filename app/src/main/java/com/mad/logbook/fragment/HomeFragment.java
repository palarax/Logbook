package com.mad.logbook.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.mad.logbook.R;
import com.mad.logbook.Utils;
import com.mad.logbook.activity.DrivingLesson;
import com.mad.logbook.db.DatabaseHelper;
import com.mad.logbook.model.Lesson;
import com.mad.logbook.presenter.LessonPresenter;
import com.mad.logbook.presenter.UserPresenter;
import com.txusballesteros.widgets.FitChart;

import java.text.ParseException;

/**
 * Home fragment that initiates a lesson
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 * @date 09-Sep-17
 */

public class HomeFragment extends Fragment {

    private TextView numberOfLessons, hoursDroveNight, hoursDroveDay;

    private UserPresenter mUserPresenter;
    private LessonPresenter mLessonPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLessonPresenter = new LessonPresenter(getActivity());  //update lesson presenter
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
        mUserPresenter = new UserPresenter();
        mLessonPresenter = new LessonPresenter(getActivity());
        DatabaseHelper.testString();
        BarChart chart = view.findViewById(R.id.chart);
        BarData data = null;
        try {
            data = new BarData(mLessonPresenter.getLessonMonths(),
                    mLessonPresenter.getGraphDataSet(mLessonPresenter.getLessonMonths()));
        } catch (ParseException e) {
            Log.e("HomeFragment", "ERROR: " + e);
            //TODO: what can i do here
        }
        chart.setData(data);
        chart.setDescription("");
        chart.animateXY(2000, 2000);
        chart.invalidate();

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
        TextView mNameText = view.findViewById(R.id.txt_name);
        TextView mLicenseText = view.findViewById(R.id.txt_license);
        TextView mDobText = view.findViewById(R.id.txt_dob);
        TextView mStateText = view.findViewById(R.id.txt_state);
        TextView mProgressText = view.findViewById(R.id.txt_completed);
        final FitChart fitChart = view.findViewById(R.id.fitChart);
        final TextView mHoursCompleted = view.findViewById(R.id.hours_completed);

        mUserPresenter.populateUserData(mNameText, mLicenseText, mDobText,
                mStateText, mProgressText, getContext());

        mUserPresenter.createDataSeries(fitChart, mHoursCompleted, getActivity());
        updateLessonInformation();

    }

    /**
     * Updates lesson information on GUI
     */
    private void updateLessonInformation() {
        numberOfLessons.setText(Integer.toString(mLessonPresenter.getAllLessons().size()));
        double[] dayNightTime = new double[]{0,0};
        try {
            dayNightTime = mLessonPresenter.getDayNightDroveLesson(mLessonPresenter.getAllLessons());
        } catch (ParseException e) {
            Log.e("HomeFragment", "Formatting exception: " + e);
            Toast.makeText(getContext(), getString(R.string.generic_error, e), Toast.LENGTH_SHORT).show();
        }
        hoursDroveNight.setText(getString(R.string.profile_night_hours,dayNightTime[1]/1000/60/60));
        hoursDroveDay.setText(getString(R.string.profile_day_hours,dayNightTime[0]/1000/60/60));
        //Update user hours completed
        mUserPresenter.getStudent().setHoursCompleted(dayNightTime[0]+dayNightTime[1]);
        mUserPresenter.getStudent().save();
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
                            mUserPresenter.getStudent().getLicenceNumber(),
                            Integer.parseInt(editStartOdometer.getText().toString()),
                            0, 0, Utils.getTime(), null);
                    lesson.save();
                    intent.putExtra(Utils.LESSON_ID, lesson.getId());
                    startActivity(intent);
                }
            }
        });
    }

}
