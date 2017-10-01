package palarax.com.logbook.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        mUserPresenter = new UserPresenter();
        LessonPresenter mLessonPresenter = new LessonPresenter(this);
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

        final TextView numberOfLessons = findViewById(R.id.txt_drives);
        final TextView hoursDroveNight = findViewById(R.id.hours_drove_night);
        final TextView hoursDroveDay = findViewById(R.id.hours_drove_day);
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

        numberOfLessons.setText(Integer.toString(mLessonPresenter.getAllLessons().size()));
        try {
            hoursDroveNight.setText(getString(R.string.profile_day_hours,
                    mLessonPresenter.getDayNightDroveLesson(true, mLessonPresenter.getAllLessons())));
            hoursDroveDay.setText(getString(R.string.profile_night_hours,
                    mLessonPresenter.getDayNightDroveLesson(false, mLessonPresenter.getAllLessons())));
        } catch (ParseException e) {
            Log.e(TAG, "Formatting exception: " + e);
        }

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
