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

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Driving location points table stores data of the location during the lesson
 *
 * @author Ilya Thai (11972078)
 * @date 09-Sep-17
 */
@Table(name = "Coordinates")
public class Coordinates extends Model {

    @Column(name = "lessonId")
    private long lessonId;
    @Column(name = "longitude")
    private double longitude;
    @Column(name = "latitude")
    private double latitude;
    @Column(name = "userId")
    private long userId;

    /**
     * Default constructor
     */
    public Coordinates() {
        super();
    }

    /**
     * Location Points object constructor that initializes the object
     *
     * @param lessonId  Lesson linked to this location points
     * @param longitude longitude linked to lesson
     * @param latitude  latitude linked to lesson
     */
    public Coordinates(long lessonId, double longitude, double latitude, long userId) {
        this.lessonId = lessonId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userId = userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getLessonId() {
        return lessonId;
    }

    public void setLessonId(long lesson) {
        this.lessonId = lesson;
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

