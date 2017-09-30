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
 * Users table stores data of the app users
 *
 * @author Ilya Thai (11972078)
 * @date 22-Sep-17
 */
@Table(name = "Users")
public class Users extends Model {

    @Column(name = "userSurname")
    private String userSurname;

    @Column(name = "licenseNumber")
    private int licenseNumber;

    @Column(name = "hoursCompleted")
    private int hoursCompleted;

    @Column(name = "userName")
    private String userName;
    @Column(name = "state")

    private String state;

    @Column(name = "dob")
    private String dob;


    /**
     * Default constructor for SugarOrm
     */
    public Users() {
        super();
    }


    /**
     * Users object constructor that initializes the object
     *
     * @param licenseNumber License number of the user
     * @param userName      Surname of the user
     * @param userSurname   Surname of the user
     */
    public Users(Integer licenseNumber, String userName, String userSurname,String state,String dob,int hours_completed) {
        this.licenseNumber = licenseNumber;
        this.userName = userName;
        this.userSurname = userSurname;
        this.state = state;
        this.dob = dob;
        this.hoursCompleted = hours_completed;
    }

    public int getHoursCompleted() {
        return hoursCompleted;
    }

    public void setHoursCompleted(int hours_completed) {
        this.hoursCompleted = hours_completed;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(Integer licenseNumber) {
        this.licenseNumber = licenseNumber;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }
}
