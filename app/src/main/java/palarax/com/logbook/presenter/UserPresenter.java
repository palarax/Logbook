package palarax.com.logbook.presenter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.txusballesteros.widgets.FitChart;

import palarax.com.logbook.R;
import palarax.com.logbook.model.Users;


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
        licenseText.setText(mStudent.getLicenseNumber().toString());
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

}
