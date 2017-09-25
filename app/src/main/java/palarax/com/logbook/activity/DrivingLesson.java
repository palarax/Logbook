package palarax.com.logbook.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

import palarax.com.logbook.R;

/**
 * Activity that records lesson data
 * @author Ilya Thai (11972078)
 * @date 23-Sep-17
 * @version 1.0
 */

public class DrivingLesson extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = DrivingLesson.class.getSimpleName(); //used for debugging

    //TODO: Location tracker https://github.com/mrmans0n/smart-location-lib

    //TODO: https://github.com/googlesamples/android-SpeedTracker/blob/master/Application/src/main/java/com/example/android/wearable/speedtracker/PhoneMainActivity.java
    //Looks like a path builder
    private static final int LOCATION_PERMISSION_ID = 1001;
    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    LocationManager manager;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Button btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Location permission not granted
                if (ContextCompat.checkSelfPermission(DrivingLesson.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DrivingLesson.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
                    finish();
                }
                getLastLocation();
            }
        });

        // Keep the screen always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        try {
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            } else {
                mMapFragment.getMapAsync(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    updateMap(location.getLatitude(), location.getLongitude());
                }
            }
        };
    }

    private void updateMap(double currentLatitude, double currentLongitude) {
        LatLng loc1 = new LatLng(currentLatitude, currentLongitude);
        mMap.addMarker(new MarkerOptions().position(loc1).title("Your Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            String location = String.format(Locale.ENGLISH, "Latitude %f, Longitude %f", mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            Log.e(TAG, location);
                            updateMap(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }




    /*private void showTrack() {
        new AsyncTask<Void, Void, Void>() {

            private List<LatLng> coordinates;
            private LatLngBounds bounds;

            @Override
            protected Void doInBackground(Void... params) {
                LocationDataManager dataManager = ((PhoneApplication) getApplicationContext()).getDataManager();
                List<LocationEntry> entries = dataManager.getPoints(params[0]);
                if (entries != null && !entries.isEmpty()) {
                    coordinates = new ArrayList<LatLng>();
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (LocationEntry entry : entries) {
                        LatLng latLng = new LatLng(entry.latitude, entry.longitude);
                        builder.include(latLng);
                        coordinates.add(latLng);
                    }
                    bounds = builder.build();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mMap.clear();
                if (coordinates == null || coordinates.isEmpty()) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "No Entries found for that date");
                    }
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                            BOUNDING_BOX_PADDING_PX));
                    mMap.addPolyline(new PolylineOptions().geodesic(true).addAll(coordinates));
                }
            }
        }.execute(calendar);
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }



    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Marker"));
    }*/

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    /*@Override
    public void onBackPressed() {
        //Super not called to disable back press
    }*/



    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            finish();
        }
    }


}
