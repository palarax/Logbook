package palarax.com.logbook.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.PolylineOptions;

import palarax.com.logbook.R;
import palarax.com.logbook.Utils;
import palarax.com.logbook.presenter.LessonPresenter;

/**
 * Activity that displays singular lesson data
 * @author Ilya Thai (11972078)
 * @date 02-Oct-17
 * @version 1.0
 */

public class LessonFullDetail extends AppCompatActivity implements OnMapReadyCallback {

    private LessonPresenter mLessonPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_full);

        mLessonPresenter = new LessonPresenter(LessonFullDetail.this);
        TextView mTxtViewLpn = findViewById(R.id.txt_lpn);
        TextView mTxtViewDistance = findViewById(R.id.txt_distance);
        TextView mTxtViewSupervisorLicence = findViewById(R.id.txt_supervisorLicence);
        TextView mTxtViewStartOdometer = findViewById(R.id.txt_start_odometer);
        TextView mTxtViewEndOdometer = findViewById(R.id.txt_end_odometer);
        TextView mTxtViewTotalTime = findViewById(R.id.txt_total_time);
        TextView mTxtViewStartTime = findViewById(R.id.txt_start_time);
        TextView mTxtViewEndTime = findViewById(R.id.txt_end_time);
        TextView mTxtViewSpeed = findViewById(R.id.txt_speed);

        final Button btnExit = findViewById(R.id.btn_lesson_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mLessonPresenter.setCurrentLesson(extras.getLong(Utils.LESSON_ID));
            mLessonPresenter.setCurrentCoordinates(extras.getLong(Utils.LESSON_ID));
        }
        double speed = (mLessonPresenter.getCurrentLesson().getDistance()) /
                (mLessonPresenter.getCurrentLesson().getTotalTime() / 60 / 60);

        mTxtViewLpn.setText(mLessonPresenter.getCurrentLesson().getLicencePlate());
        mTxtViewDistance.setText(getString(R.string.txt_distance, mLessonPresenter.getCurrentLesson().getDistance() / 1000));
        mTxtViewTotalTime.setText(Utils.convertDateToFormat(mLessonPresenter.getCurrentLesson().getTotalTime(), 1));
        mTxtViewSupervisorLicence.setText(Long.toString(mLessonPresenter.getCurrentLesson().getSupervisorLicence()));
        mTxtViewStartOdometer.setText(Long.toString(mLessonPresenter.getCurrentLesson().getStartOdometer()));
        mTxtViewEndOdometer.setText(Long.toString(mLessonPresenter.getCurrentLesson().getEndOdometer()));
        mTxtViewStartTime.setText(mLessonPresenter.getCurrentLesson().getStartTime());
        mTxtViewEndTime.setText(mLessonPresenter.getCurrentLesson().getEndTime());
        mTxtViewSpeed.setText(getString(R.string.txt_speed, speed));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();  //clears all Markers and Polylines
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        googleMap.addPolyline(mLessonPresenter.getCoordinates(options)); //add Polyline
        googleMap.moveCamera(CameraUpdateFactory.
                newLatLngZoom(mLessonPresenter.getFirstCoordinates(), 18));
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

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
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
