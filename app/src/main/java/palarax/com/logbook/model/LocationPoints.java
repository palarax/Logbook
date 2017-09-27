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
package palarax.com.logbook.model;

import com.orm.SugarRecord;

/**
 * Driving location points table stores data of the location during the lesson
 *
 * @author Ilya Thai (11972078)
 * @date 09-Sep-17
 */
public class LocationPoints extends SugarRecord<LocationPoints> {

    private int mLocationId, mLessonId;
    private double mLatitude, mLongitude;

    /**
     * Default constructor for SugarOrm
     */
    public LocationPoints() {
    }

    /**
     * Location Points object constructor that initializes the object
     *
     * @param locationId Location identification number
     * @param lessonId   Lesson identification number
     * @param latitude   Latitude recorded during the lesson
     * @param longitude  Longitude recorded during the lesson
     */
    public LocationPoints(Integer locationId, Integer lessonId, double latitude, double longitude) {
        this.mLocationId = locationId;
        this.mLessonId = lessonId;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    public Integer getLocationId() {
        return mLocationId;
    }

    public void setLocationId(Integer locationId) {
        this.mLocationId = locationId;
    }


    public Integer getLessonId() {
        return mLessonId;
    }

    public void setLessonId(Integer lessonId) {
        this.mLessonId = lessonId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }
}
