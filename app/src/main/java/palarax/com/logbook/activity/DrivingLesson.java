package palarax.com.logbook.activity;

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
import com.google.maps.android.SphericalUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import palarax.com.logbook.R;
import palarax.com.logbook.model.Coordinates;
import palarax.com.logbook.model.Lesson;
import palarax.com.logbook.model.Utils;

/**
 * Activity that records and displays live lesson data
 * @author Ilya Thai (11972078)
 * @date 23-Sep-17
 * @version 1.0
 */

public class DrivingLesson extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final int TWO_MINUTES = 1000 * 60 * 2;

    //Looks like a path builder
    private static final String TAG = DrivingLesson.class.getSimpleName(); //used for debugging
    private static final int LOCATION_PERMISSION_ID = 1001;
    private static final long INTERVAL = 1000 * 20; //Interval location will be found
    private static final long FASTEST_INTERVAL = 1000 * 10; //Interval if found sooner

    List<LatLng> coordinates;
    Polyline mPolyline;

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Lesson mLesson;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Button btnEndStop = findViewById(R.id.btn_end_lesson);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        //Set up google api client for locations services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        btnEndStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endLesson();
            }
        });
        // Keep the screen always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        coordinates = new ArrayList<>();  //store coordinates

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            long lesson_id = extras.getLong(Utils.LESSON_ID);
            mLesson = Lesson.findById(Lesson.class, lesson_id);
        }
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
        if (mCurrentLocation == null) {
            mCurrentLocation = location;
            LatLng locl = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locl, 18));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
        }

        showLocation(location);
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
        for (int i = 0; i < coordinates.size(); i++) {
            LatLng point = coordinates.get(i);
            options.add(point);
        }
        mPolyline = mMap.addPolyline(options); //add Polyline
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLatitude,currentLongitude)));
    }

    //TODO: update to just store location and updateMap
    private void showLocation(Location newLocation) {
        if (isBetterLocation(newLocation,mCurrentLocation)) {
            mCurrentLocation = newLocation;
            //Update lesson object
            coordinates.add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            //Store coordinates to db
            Coordinates locationPoints = new Coordinates(mLesson.getId(),
                    mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            locationPoints.save();

            updateMap(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
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
     * Checks lesson integrity. Lesson has to be longer than 10 minutes and
     * distance travelled should be more than 500m, start odometer bigger than end odometer
     */
    private void checkLessonIntegrity() {
        double distanceTravelled = SphericalUtil.computeLength(coordinates);//calculate distance travelled
        long totalTime = 0;
        try {
            totalTime = Utils.getTimeDiffernce(mLesson.getStartTime(), mLesson.getEndTime());
        } catch (ParseException e) {
            Log.e(TAG, "Error: " + e);
            //This error should never occur
        }
        long totalTimeMinutes = totalTime / 1000 / 60;
        Toast.makeText(this, "Dist: " + distanceTravelled + "Ttime: " + Long.toString(totalTime), Toast.LENGTH_SHORT).show();
        if (distanceTravelled > 500 && totalTimeMinutes > 10 && (
                mLesson.getEndOdometer() > mLesson.getStartOdometer())) {

            mLesson.setDistance(distanceTravelled);
            mLesson.setTotalTime(Long.toString(totalTime));
            mLesson.setSpeed(distanceTravelled / totalTime);
            //save lesson
            mLesson.save();
        } else {
            //Lesson is too short or didn't travel enough
            //Remove coordinates
            Toast.makeText(this, getString(R.string.bad_lesson), Toast.LENGTH_SHORT).show();
            List<Coordinates> coordinates = Coordinates.findWithQuery(Coordinates.class, "Select * from Coordinates where lesson_id = ?", mLesson.getId().toString());
            for (Coordinates geoPoints : coordinates) {
                geoPoints.delete();
            }
            //remove lesson
            mLesson.delete();
        }
    }


    //TODO: put into doc: https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android

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
    private void endLesson() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        stopLocationUpdates();

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
                mLesson.setEndTime(Utils.getTime());
                getEndOdometerFromUser();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Gets final Odometer reading from user
     */
    private void getEndOdometerFromUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.end_odometer_input));

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(input.getText().toString())) {
                    Toast.makeText(DrivingLesson.this, getString(R.string.error_odometer_null), Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(input.getText().toString()) < mLesson.getStartOdometer()) {
                    Toast.makeText(DrivingLesson.this, getString(R.string.error_odometer_small), Toast.LENGTH_SHORT).show();
                } else {
                    mLesson.setEndOdometer(Integer.parseInt(input.getText().toString()));
                    finish();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(DrivingLesson.this, getString(R.string.error_odometer_null), Toast.LENGTH_SHORT).show();
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
        Log.d(TAG, "onStart");
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
        checkLessonIntegrity();
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


    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     * TODO: Code from: https://developer.android.com/guide/topics/location/strategies.html add to Software doc
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     * TODO: Code from: https://developer.android.com/guide/topics/location/strategies.html add to Software doc
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
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

}
