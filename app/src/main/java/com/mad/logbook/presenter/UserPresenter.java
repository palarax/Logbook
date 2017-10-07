package com.mad.logbook.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.mad.logbook.R;
import com.mad.logbook.Utils;
import com.mad.logbook.model.Users;
import com.txusballesteros.widgets.FitChart;


/**
 * Presents and manages user data
 * @author Ilya Thai (11972078)
 * @date 29-Sep-17
 * @version 1.0
 */

public class UserPresenter {

    private Users mStudent;

    public UserPresenter(){
        this.mStudent = new Select().from(Users.class).where("active = ?", "1").executeSingle();
    }

    public Users getStudent(){
        return mStudent;
    }

    public void deleteAllUsers(){
        new Delete().from(Users.class).execute();
    }


    /**
     * Populates view with user data
     */
    public void populateUserData(TextView nameText, TextView licenseText,TextView dobText,
                                  TextView stateText, TextView progressText,Context context) {
        nameText.setText(mStudent.getUserName()
                + " " + mStudent.getUserSurname());
        licenseText.setText(Long.toString(mStudent.getLicenceNumber()));
        dobText.setText(mStudent.getDob());
        stateText.setText(mStudent.getState());

        if ((mStudent.getHoursCompleted()/1000/60/60) >= 120) {
            progressText.setText(context.getString(R.string.profile_completed));
            progressText.setTextColor(ContextCompat.getColor(context, R.color.licence_color));
        } else {
            progressText.setText(context.getString(R.string.profile_in_progress));
            progressText.setTextColor(ContextCompat.getColor(context, R.color.in_progress));
        }
    }

    /**
     * Create a data series for hours completion chart
     *
     * @param fitChart chart to be populated
     */
    public void createDataSeries(FitChart fitChart, TextView chartView, Context context) {
        fitChart.setMinValue(0f);
        fitChart.setMaxValue(120f);
        double hoursCompleted = mStudent.getHoursCompleted()/1000/60/60;
        chartView.setText(context.getString(R.string.chart_hours_completed, hoursCompleted));
        fitChart.setValue((float) hoursCompleted);
    }

    /**
     * Update backendless user in the cloud
     *
     * @param user          backendless user object
     * @param name          new name of the user
     * @param surname       new surname of the user
     * @param dob           new date of birth of the user
     * @param state         new state of the user
     * @param contactNumber new contact number of the user
     */
    public void updateBackendlessUser(Activity activity, BackendlessUser user, String name, String surname, String dob, String state,
                                      String contactNumber) {
        final Activity mActivity = activity;
        if (name.isEmpty()) {
            name = getStudent().getUserName();
        }
        if (surname.isEmpty()) {
            surname = getStudent().getUserSurname();
        }
        if (dob.isEmpty()) {
            dob = getStudent().getDob();
        }
        if (state.isEmpty()) {
            state = getStudent().getState();
        }
        if (contactNumber.isEmpty()) {
            contactNumber = null;
        }

        user.setProperty(Utils.BACKENDLESS_NAME, name);
        user.setProperty(Utils.BACKENDLESS_SURNAME, surname);
        user.setProperty(Utils.BACKENDLESS_DOB, dob);
        user.setProperty(Utils.BACKENDLESS_LICENSE, mStudent.getLicenceNumber());
        user.setProperty(Utils.BACKENDLESS_STATE, state);
        user.setProperty(Utils.BACKENDLESS_CONTACT_NUMBER, contactNumber);

        Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
            public void handleResponse(BackendlessUser user) {
                Toast.makeText(mActivity, mActivity.getString(R.string.user_updated),
                        Toast.LENGTH_LONG).show();
            }

            public void handleFault(BackendlessFault fault) {
                Toast.makeText(mActivity, mActivity.getString(R.string.error_not_updated),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
