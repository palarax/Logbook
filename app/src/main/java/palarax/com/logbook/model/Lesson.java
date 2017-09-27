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

import java.sql.Time;

/**
 * Lesson data ( used for Lesson History)
 *
 * @author Ilya Thai (11972078)
 * @date 09-Sep-17
 */
public class Lesson extends SugarRecord<Lesson> {

    private String mLicencePlate;
    private int mLessonId, mDistance, mSupervisorLicence, mLearnerId, mStartOdometer, mEndOdometer;
    private Time mTotalTime, mStartTime, mEndTime;

    /**
     * Default constructor for SugarOrm
     */
    public Lesson() {
    }

    /**
     * Lesson object constructor that initializes the object
     *
     * @param licencePlate licence plate of the vehicle used
     * @param lessonId lesson identification number
     * @param distance  distance travelled during the lesson
     * @param supervisorLicence supervisor licence number
     * @param learnerId  learner identification number
     * @param startOdometer odometer start number
     * @param endOdometer odometer end number
     * @param totalTime  total time driving for this lesson
     * @param startTime time started the lesson
     * @param endTime   time ended the lesson
     */
    public Lesson(String licencePlate, int lessonId, int distance, int supervisorLicence, int learnerId, int startOdometer, int endOdometer, Time totalTime, Time startTime, Time endTime) {
        this.mLicencePlate = licencePlate;
        this.mLessonId = lessonId;
        this.mDistance = distance;
        this.mSupervisorLicence = supervisorLicence;
        this.mLearnerId = learnerId;
        this.mStartOdometer = startOdometer;
        this.mEndOdometer = endOdometer;
        this.mTotalTime = totalTime;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
    }


    public String getLicencePlate() {
        return mLicencePlate;
    }

    public void setLicencePlate(String LicencePlate) {
        this.mLicencePlate = LicencePlate;
    }

    public int getLessonId() {
        return mLessonId;
    }

    public void setLessonId(int lessonId) {
        this.mLessonId = lessonId;
    }

    public int getDistance() {
        return mDistance;
    }

    public void setDistance(int Distance) {
        this.mDistance = Distance;
    }

    public int getSupervisorLicence() {
        return mSupervisorLicence;
    }

    public void setSupervisorLicence(int supervisorLicence) {
        this.mSupervisorLicence = supervisorLicence;
    }

    public int getLearnerId() {
        return mLearnerId;
    }

    public void setLearnerId(int learnerId) {
        this.mLearnerId = learnerId;
    }

    public int getStartOdometer() {
        return mStartOdometer;
    }

    public void setStartOdometer(int startOdometer) {
        this.mStartOdometer = startOdometer;
    }

    public int getEndOdometer() {
        return mEndOdometer;
    }

    public void setEndOdometer(int endOdometer) {
        this.mEndOdometer = endOdometer;
    }

    public Time getTotalTime() {
        return mTotalTime;
    }

    public void setTotalTime(Time totalTime) {
        this.mTotalTime = totalTime;
    }

    public Time getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Time startTime) {
        this.mStartTime = startTime;
    }

    public Time getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Time endTime) {
        this.mEndTime = endTime;
    }
}
