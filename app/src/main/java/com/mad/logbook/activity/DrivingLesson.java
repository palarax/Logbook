package com.mad.logbook.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mad.logbook.R;
import com.mad.logbook.Utils;
import com.mad.logbook.interfaces.DrivingLessonContract;
import com.mad.logbook.presenter.DrivingLessonPresenter;

/**
 * Activity that records and displays live lesson data
 * @author Ilya Thai (11972078)
 * @date 23-Sep-17
 * @version 1.0
 */

public class DrivingLesson extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DrivingLessonContract.View {

    private static final String TAG = DrivingLesson.class.getSimpleName(); //used for debugging


    private static final int LOCATION_PERMISSION_ID = 1001;
    private static final long INTERVAL = 1000 * 20; //Interval location will be found
    private static final long FASTEST_INTERVAL = 1000 * 10; //Interval if found sooner

    Polyline mPolyline;

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private DrivingLessonContract.Presenter mDrivingLessonPresenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Button btnEndStop = findViewById(R.id.btn_end_lesson);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mDrivingLessonPresenter = new DrivingLessonPresenter(this, extras.getLong(Utils.LESSON_ID));
        } else {
            finish();
        }

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        btnEndStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endLesson();
            }
        });
        createLocationRequest();
        //Set up google api client for locations services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Keep the screen always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Initiates location request with specific Intervals
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {

        if (mDrivingLessonPresenter.firstLocation(location)) {
            LatLng locl = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locl, 18));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
        }
        if (mDrivingLessonPresenter.updateLocation(location)) {
            updateMap(location.getLatitude(), location.getLongitude());
        }
    }


    /**
     * Updates Map with new location
     * @param currentLatitude latitude of current position
     * @param currentLongitude longitude of current position
     */
    private void updateMap(double currentLatitude, double currentLongitude) {
        Log.d(TAG,"updateMap");
        mMap.clear();  //clears all Markers and Polylines
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        mPolyline = mMap.addPolyline(mDrivingLessonPresenter.getPolyLine(options,
                mDrivingLessonPresenter.getActiveLessonCoordinates())); //add Polyline
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLatitude,currentLongitude)));
    }

    /**
     * Starts location updates, requests permissions if needed
     */
    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(DrivingLesson.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(DrivingLesson.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_ID);
        }else{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
            mMap.setMyLocationEnabled(true);
        }

    }

    /**
     * Pressing the back button alerts the user that the lesson will end
     */
    @Override
    public void onBackPressed() {
        endLesson();
    }

    /**
     * initiates the end of the lesson
     */
    public void endLesson() {
        stopLocationUpdates();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.alert_title));
        builder.setMessage(getString(R.string.backpress_alert));

        builder.setPositiveButton(getString(R.string.alert_stay), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                startLocationUpdates();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.alert_exit), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDrivingLessonPresenter.getActvieLesson().setEndTime(Utils.getTime());
                getEndOdometerFromUser();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Gets final Odometer reading from user
     */
    public void getEndOdometerFromUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.end_odometer_input));

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(input.getText().toString())) {
                    Toast.makeText(getBaseContext(), getString(R.string.error_odometer_null), Toast.LENGTH_SHORT).show();
                } else if (Long.parseLong(input.getText().toString()) < mDrivingLessonPresenter.getActvieLesson().getStartOdometer()) {
                    Toast.makeText(getBaseContext(), getString(R.string.error_odometer_small), Toast.LENGTH_SHORT).show();
                } else {
                    mDrivingLessonPresenter.getActvieLesson().setEndOdometer(Long.parseLong(input.getText().toString()));
                    if (!mDrivingLessonPresenter.checkLessonIntegrity(mDrivingLessonPresenter.getActvieLesson())) {
                        Toast.makeText(getBaseContext(), getString(R.string.bad_lesson), Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), getString(R.string.error_odometer_null), Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Called when activity becomes visible to the user
     */
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /**
     * Called when activity has been stopped and is restarting again
     */
    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
        if(this.mGoogleApiClient != null) this.mGoogleApiClient.connect();
    }

    /**
     * Called when activity starts interacting with user
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    /**
     * Called when current activity is being paused and the previous activity is being resumed
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    /**
     * Called when activity is no longer visible to user
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        mGoogleApiClient.disconnect();
    }

    /**
     * Called before the activity is destroyed by the system (either manually or by the system to conserve memory)
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            finish();
        }
    }

    /**
     * Checks if google play services are available
     * @return true if GMS is available
     */
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            Toast.makeText(getBaseContext(),getString(R.string.error_gms_connect),Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Show last location
        mMap = googleMap;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void setPresenter(DrivingLessonContract.Presenter presenter) {
        mDrivingLessonPresenter = presenter;
    }
}
