package palarax.com.logbook.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.txusballesteros.widgets.FitChart;

import java.text.ParseException;

import palarax.com.logbook.R;
import palarax.com.logbook.presenter.LessonPresenter;
import palarax.com.logbook.presenter.UserPresenter;

/**
 * Profile Activity that gives a summary of user progress
 * @author Ilya Thai (11972078)
 * @date 09-Sep-17
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName(); //used for debugging
    private UserPresenter mUserPresenter;
    private LessonPresenter mLessonPresenter;

    private TextView numberOfLessons,hoursDroveNight,hoursDroveDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        mUserPresenter = new UserPresenter();
        mLessonPresenter = new LessonPresenter(this);
        BarChart chart = findViewById(R.id.chart);

        BarData data = null;
        try {
            data = new BarData(mLessonPresenter.getLessonMonths(),
                    mLessonPresenter.getGraphDataSet(mLessonPresenter.getLessonMonths()));
        } catch (ParseException e) {
            Log.e(TAG, "ERROR: " + e);
            //TODO: what can i do here
        }
        chart.setData(data);
        chart.setDescription("");
        chart.animateXY(2000, 2000);
        chart.invalidate();

        numberOfLessons = findViewById(R.id.txt_drives);
        hoursDroveNight = findViewById(R.id.hours_drove_night);
        hoursDroveDay = findViewById(R.id.hours_drove_day);
        final TextView nameText = findViewById(R.id.txt_name);
        final TextView licenseText = findViewById(R.id.txt_license);
        final TextView dobText = findViewById(R.id.txt_dob);
        final TextView stateText = findViewById(R.id.txt_state);
        final TextView progressText = findViewById(R.id.txt_completed);
        final TextView mHoursCompleted = findViewById(R.id.hours_completed);
        final FitChart fitChart = findViewById(R.id.fitChart);

        mUserPresenter.populateUserData(nameText,licenseText,dobText,
                stateText,progressText,getBaseContext());
        mUserPresenter.createDataSeries(fitChart, mHoursCompleted, this);

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
            Toast.makeText(this, getString(R.string.generic_error, e), Toast.LENGTH_SHORT).show();
        }
        hoursDroveNight.setText(getString(R.string.profile_night_hours,dayNightTime[1]/1000/60/60));
        hoursDroveDay.setText(getString(R.string.profile_day_hours,dayNightTime[0]/1000/60/60));

        //Update user hours completed
        mUserPresenter.getStudent().setHoursCompleted(dayNightTime[0]+dayNightTime[1]);
        mUserPresenter.getStudent().save();
    }


    //https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/LineChartActivity1.java
    //test data


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
