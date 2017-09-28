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
 * Lesson data ( used for Lesson History)
 *
 * @author Ilya Thai (11972078)
 * @date 09-Sep-17
 */
public class Lesson extends SugarRecord<Lesson> {


    //totalTime in milliseconds, start and end time in date format ( dd/MM/yy HH:mm:ss )
    private String licencePlate, totalTime, startTime, endTime;
    private int supervisorLicence, startOdometer, endOdometer;
    private double distance, speed;
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
     * @param speed average speed
     */
    public Lesson(String licencePlate, double distance, int supervisorLicence, Users student,
                  int startOdometer, int endOdometer, String totalTime, String startTime,
                  String endTime, double speed) {
        this.licencePlate = licencePlate;
        this.distance = distance;
        this.supervisorLicence = supervisorLicence;
        this.student = student;
        this.startOdometer = startOdometer;
        this.endOdometer = endOdometer;
        this.totalTime = totalTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.speed = speed;
    }


    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String LicencePlate) {
        this.licencePlate = LicencePlate;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double Distance) {
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

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

}
