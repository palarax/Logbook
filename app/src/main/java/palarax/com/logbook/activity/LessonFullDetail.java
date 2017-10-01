package palarax.com.logbook.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.PolylineOptions;

import palarax.com.logbook.R;
import palarax.com.logbook.model.Utils;
import palarax.com.logbook.presenter.LessonPresenter;

/**
 * @author Palarax
 * @date 02-Oct-17
 */
public class LessonFullDetail extends AppCompatActivity implements OnMapReadyCallback {

    TextView mTxtViewDistance, mTxtViewSupervisorLicence, mTxtViewStartOdometer, mTxtViewEndOdometer,
            mTxtViewTotalTime, mTxtViewStartTime, mTxtViewEndTime, mTxtViewSpeed, mTxtViewLpn;
    private LessonPresenter mLessonPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_full);
        mLessonPresenter = new LessonPresenter(LessonFullDetail.this);
        mTxtViewLpn = findViewById(R.id.txt_lpn);
        mTxtViewDistance = findViewById(R.id.txt_distance);
        mTxtViewSupervisorLicence = findViewById(R.id.txt_supervisorLicence);
        mTxtViewStartOdometer = findViewById(R.id.txt_start_odometer);
        mTxtViewEndOdometer = findViewById(R.id.txt_end_odometer);
        mTxtViewTotalTime = findViewById(R.id.txt_total_time);
        mTxtViewStartTime = findViewById(R.id.txt_start_time);
        mTxtViewEndTime = findViewById(R.id.txt_end_time);
        mTxtViewSpeed = findViewById(R.id.txt_speed);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mLessonPresenter.setCurrentLesson(extras.getLong(Utils.LESSON_ID));
            mLessonPresenter.setCurrentCoordinates(extras.getLong(Utils.LESSON_ID));
        }

        mTxtViewLpn.setText(mLessonPresenter.getCurrentLesson().getLicencePlate());
        mTxtViewDistance.setText(getString(R.string.txt_distance, mLessonPresenter.getCurrentLesson().getDistance() / 1000));
        mTxtViewTotalTime.setText(Utils.convertDateToFormat(mLessonPresenter.getCurrentLesson().getTotalTime(), 1));
        mTxtViewSupervisorLicence.setText(Long.toString(mLessonPresenter.getCurrentLesson().getSupervisorLicence()));
        mTxtViewStartOdometer.setText(Long.toString(mLessonPresenter.getCurrentLesson().getStartOdometer()));
        mTxtViewEndOdometer.setText(Long.toString(mLessonPresenter.getCurrentLesson().getEndOdometer()));
        mTxtViewStartTime.setText(mLessonPresenter.getCurrentLesson().getStartTime());
        mTxtViewEndTime.setText(mLessonPresenter.getCurrentLesson().getEndTime());
        mTxtViewSpeed.setText(getString(R.string.txt_speed, mLessonPresenter.getCurrentLesson().getSpeed()));

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
}
