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

    private String licencePlate;
    private int distance, supervisorLicence, startOdometer, endOdometer;
    private Time totalTime, startTime, endTime;

    private Users student;
    /**
     * Default constructor for SugarOrm
     */
    public Lesson() {
    }

    /**
     * Lesson object constructor that initializes the object
     *
     * @param licencePlate licence plate of the vehicle used
     * @param distance  distance travelled during the lesson
     * @param supervisorLicence supervisor licence number
     * @param student student for this lesson
     * @param startOdometer odometer start number
     * @param endOdometer odometer end number
     * @param totalTime  total time driving for this lesson
     * @param startTime time started the lesson
     * @param endTime   time ended the lesson
     */
    public Lesson(String licencePlate, int distance, int supervisorLicence, Users student, int startOdometer, int endOdometer, Time totalTime, Time startTime, Time endTime) {
        this.licencePlate = licencePlate;
        this.distance = distance;
        this.supervisorLicence = supervisorLicence;
        this.student = student;
        this.startOdometer = startOdometer;
        this.endOdometer = endOdometer;
        this.totalTime = totalTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String LicencePlate) {
        this.licencePlate = LicencePlate;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int Distance) {
        this.distance = Distance;
    }

    public int getSupervisorLicence() {
        return supervisorLicence;
    }

    public void setSupervisorLicence(int supervisorLicence) {
        this.supervisorLicence = supervisorLicence;
    }

    public Users getStudent() {
        return student;
    }

    public void setStudent(Users student) {
        this.student = student;
    }

    public int getStartOdometer() {
        return startOdometer;
    }

    public void setStartOdometer(int startOdometer) {
        this.startOdometer = startOdometer;
    }

    public int getEndOdometer() {
        return endOdometer;
    }

    public void setEndOdometer(int endOdometer) {
        this.endOdometer = endOdometer;
    }

    public Time getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Time totalTime) {
        this.totalTime = totalTime;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
}
