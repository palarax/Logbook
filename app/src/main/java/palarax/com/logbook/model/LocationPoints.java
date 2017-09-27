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

    private Lesson lesson;
    private double latitude, longitude;

    /**
     * Default constructor for SugarOrm
     */
    public LocationPoints() {
    }

    /**
     * Location Points object constructor that initializes the object
     *
     * @param lesson   Lesson linked to this location points
     * @param latitude   Latitude recorded during the lesson
     * @param longitude  Longitude recorded during the lesson
     */
    public LocationPoints(Lesson lesson, double latitude, double longitude) {
        this.lesson = lesson;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Lesson getLessonId() {
        return lesson;
    }

    public void setLessonId(Lesson lesson) {
        this.lesson = lesson;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
