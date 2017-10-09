/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mad.logbook.presenter;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.mad.logbook.Utils;
import com.mad.logbook.interfaces.DrivingLessonContract;
import com.mad.logbook.model.Coordinates;
import com.mad.logbook.model.Lesson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI, retrieves the data and updates the UI
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 * @date 09-Oct-17
 */

public class DrivingLessonPresenter implements DrivingLessonContract.Presenter {

    private static final String TAG = "Driving Lesson";

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private DrivingLessonContract.View mHomeView;
    private Lesson mCurrentLesson;
    private Location mCurrentLocation;
    private List<LatLng> mCoordinates = new ArrayList<>();  //store coordinates

    /**
     * Backendless initializes WeakReference
     */
    public DrivingLessonPresenter(@NonNull DrivingLessonContract.View view, Long lesson_id) {

        mHomeView = checkNotNull(view, "loginView cannot be null");
        mHomeView.setPresenter(this);
        mCurrentLesson = Lesson.load(Lesson.class, lesson_id);
    }

    /**
     * Check if coordinates got was the first location found
     *
     * @param newLocation new location found
     * @return true if it is the first location found
     */
    public boolean firstLocation(Location newLocation) {
        if (mCurrentLocation == null) {
            mCurrentLocation = newLocation;
            return true;
        }
        return false;
    }

    /**
     * Check if location is accurate enough
     *
     * @param newLocation new location found
     * @return true if an update is required
     */
    public boolean updateLocation(Location newLocation) {
        if (isBetterLocation(newLocation, mCurrentLocation)) {
            mCurrentLocation = newLocation;
            //Update lesson object
            mCoordinates.add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            //Store coordinates to db
            Coordinates locationPoints = new Coordinates(mCurrentLesson.getId(),
                    mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            locationPoints.save();
            return true;
        }
        return false;
    }

    public List<LatLng> getActiveLessonCoordinates() {
        return mCoordinates;
    }

    public Lesson getActvieLesson() {
        return mCurrentLesson;
    }

    /**
     * Get coordinates for Polyline on the map
     *
     * @param options coordinates to put on the map
     * @return coordinates to draw
     */
    public PolylineOptions getPolyLine(PolylineOptions options, List<LatLng> coordinates) {
        for (int i = 0; i < coordinates.size(); i++) {
            LatLng point = coordinates.get(i);
            options.add(point);
        }
        return options;
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
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
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Checks lesson integrity. Lesson has to be longer than 10 minutes and
     * distance travelled should be more than 500m, start odometer bigger than end odometer
     *
     * @return return true if lesson data was correct
     */
    public boolean checkLessonIntegrity(Lesson currentLesson) {
        double distanceTravelled = SphericalUtil.computeLength(mCoordinates);//calculate distance travelled
        long totalTime = 0;
        try {
            totalTime = Utils.getTimeDiffernce(currentLesson.getStartTime(), currentLesson.getEndTime(), "dd/MM/yy HH:mm:ss");
        } catch (ParseException e) {
            Log.e(TAG, "Error: " + e);
            //This error should never occur
        }
        //if (currentLesson.getEndOdometer() > currentLesson.getStartOdometer()) {
        if (isCurrentLessonComplete(currentLesson)) {
            //TODO: once finished testing, replace with isCurrentLessonComplete()
            currentLesson.setDistance(distanceTravelled);
            currentLesson.setTotalTime(totalTime);
            //save lesson
            currentLesson.save();
            return true;
        } else {
            return false;
        }
    }


    /**
     * Verify that all lesson data has been completed
     */
    private boolean isCurrentLessonComplete(Lesson currentLesson) {

        if (currentLesson.getLicencePlate().isEmpty() ||
                currentLesson.getDistance() < 500 || currentLesson.getSupervisorLicence() == 0 ||
                currentLesson.getStartOdometer() == 0 || currentLesson.getStartTime().isEmpty() ||
                (currentLesson.getTotalTime() / 1000 / 60) < 10 ||
                currentLesson.getEndTime().isEmpty() || currentLesson.getDistance() == 0) {
            currentLesson.delete();
            //Lesson is too short or didn't travel enough
            //Remove coordinates
            //remove lesson
            currentLesson.delete();
            return false;
        }
        return true;

    }


}
