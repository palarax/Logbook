package com.mad.logbook.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mad.logbook.R;
import com.mad.logbook.Utils;
import com.mad.logbook.interfaces.LessonFullDetailContract;
import com.mad.logbook.presenter.LessonFullDetailPresenter;

/**
 * Activity that displays singular lesson data
 * @author Ilya Thai (11972078)
 * @date 02-Oct-17
 * @version 1.0
 */

public class LessonFullDetail extends AppCompatActivity implements OnMapReadyCallback,
        LessonFullDetailContract.View {


    LessonFullDetailContract.Presenter mLessonFullDetailPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_full);


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
            mLessonFullDetailPresenter = new LessonFullDetailPresenter(this, extras.getLong(Utils.LESSON_ID));
        }

        double speed = (mLessonFullDetailPresenter.getCurrentLesson().getDistance()) /
                (mLessonFullDetailPresenter.getCurrentLesson().getTotalTime() / 60 / 60);

        mTxtViewLpn.setText(mLessonFullDetailPresenter.getCurrentLesson().getLicencePlate());
        mTxtViewDistance.setText(getString(R.string.txt_distance, mLessonFullDetailPresenter.getCurrentLesson().getDistance() / 1000));
        mTxtViewTotalTime.setText(Utils.convertDateToFormat(mLessonFullDetailPresenter.getCurrentLesson().getTotalTime(), 1));
        mTxtViewSupervisorLicence.setText(Long.toString(mLessonFullDetailPresenter.getCurrentLesson().getSupervisorLicence()));
        mTxtViewStartOdometer.setText(Long.toString(mLessonFullDetailPresenter.getCurrentLesson().getStartOdometer()));
        mTxtViewEndOdometer.setText(Long.toString(mLessonFullDetailPresenter.getCurrentLesson().getEndOdometer()));
        mTxtViewStartTime.setText(mLessonFullDetailPresenter.getCurrentLesson().getStartTime());
        mTxtViewEndTime.setText(mLessonFullDetailPresenter.getCurrentLesson().getEndTime());
        mTxtViewSpeed.setText(getString(R.string.txt_speed, speed));


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //googleMap.clear();  //clears all Markers and Polylines
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        googleMap.addPolyline(mLessonFullDetailPresenter
                .getPolyLine(options, mLessonFullDetailPresenter.getActiveLessonCoordinates())); //add Polyline
        try {
            googleMap.moveCamera(CameraUpdateFactory.
                    newLatLngZoom(mLessonFullDetailPresenter.getActiveLessonCoordinates().get(0), 18));
        } catch (IndexOutOfBoundsException e) {
            Log.e("LessonFullDetail", "no coordinates found");
            Toast.makeText(this, getString(R.string.error_no_coordinates), Toast.LENGTH_LONG).show();
            googleMap.moveCamera(CameraUpdateFactory.
                    newLatLngZoom(new LatLng(0, 0), 18));
        }

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

    @Override
    public void setPresenter(LessonFullDetailContract.Presenter presenter) {
        mLessonFullDetailPresenter = presenter;
    }
}
