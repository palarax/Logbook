package palarax.com.logbook.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.MarkerOptions;

import palarax.com.logbook.R;

/**
 * Activity that records lesson data
 * @author Ilya Thai (11972078)
 * @date 23-Sep-17
 * @version 1.0
 */

public class DrivingLesson extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final int TWO_MINUTES = 1000 * 60 * 2;

    //TODO: https://github.com/googlesamples/android-SpeedTracker/blob/master/Application/src/main/java/com/example/android/wearable/speedtracker/PhoneMainActivity.java
    //Looks like a path builder
    private static final String TAG = DrivingLesson.class.getSimpleName(); //used for debugging
    private static final int LOCATION_PERMISSION_ID = 1001;
    private static final long INTERVAL = 1000 * 30; //Interval location will be found
    private static final long FASTEST_INTERVAL = 1000 * 15; //Interval if found sooner
    private GoogleMap mMap;
    private TextView mLocationText;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;

    private String mLastUpdateTime; //TODO: figure out if needed

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Button btnStart = (Button) findViewById(R.id.btn_start);
        mLocationText = (TextView) findViewById(R.id.start_location);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        //Set up google api client for locaition services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Location permission not granted
                if (ContextCompat.checkSelfPermission(DrivingLesson.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DrivingLesson.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_ID);
                } else showLocation(mCurrentLocation);
            }
        });

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


    /**
     * Updates Map with new location
     * @param currentLatitude latitude of current position
     * @param currentLongitude longitude of current position
     */
    private void updateMap(double currentLatitude, double currentLongitude) {
        Log.d(TAG,"updateMap");
        LatLng location = new LatLng(currentLatitude, currentLongitude);
        mMap.addMarker(new MarkerOptions().position(location).title("Your Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
    }

    //TODO: remove once done, used for testing
    private void showLocation(Location newLocation) {
        if (isBetterLocation(newLocation,mCurrentLocation)) {
            mCurrentLocation = newLocation;
            final String text = String.format("Latitude %.6f, Longitude %.6f",
                    mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude());
            mLocationText.setText(text);
            updateMap(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        } else mLocationText.setText("Null location");
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

    protected void startLocationUpdates() {
        if(ContextCompat.checkSelfPermission(DrivingLesson.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(DrivingLesson.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_ID);
        }else{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }




    /*@Override
    public void onBackPressed() {
        //Super not called to disable back press
    }*/


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
    public void onLocationChanged(Location location) {
        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if(mCurrentLocation == null){
            mCurrentLocation = location;
            LatLng locl = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(locl).title("Your Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locl, 18));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
        }

        showLocation(location);
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

}
