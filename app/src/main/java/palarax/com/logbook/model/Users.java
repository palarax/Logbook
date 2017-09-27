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
 * Users table stores data of the app users
 *
 * @author Ilya Thai (11972078)
 * @date 22-Sep-17
 */
public class Users extends SugarRecord<Users> {

    private int mUserId, mLicenseNumber;
    private String mUserName, mUserSurname;

    /**
     * Default constructor for SugarOrm
     */
    public Users() {
    }


    /**
     * Users object constructor that initializes the object
     *
     * @param userId        User identification number
     * @param licenseNumber License number of the user
     * @param userName      Surname of the user
     * @param userSurname   Surname of the user
     */
    public Users(Integer userId, Integer licenseNumber, String userName, String userSurname) {
        this.mUserId = userId;
        this.mLicenseNumber = licenseNumber;
        this.mUserName = userName;
        this.mUserSurname = userSurname;
    }

    public Integer getUserId() {
        return mUserId;
    }

    public void setUserId(Integer userId) {
        this.mUserId = userId;
    }


    public Integer getLicenseNumber() {
        return mLicenseNumber;
    }

    public void setLicenseNumber(Integer licenseNumber) {
        this.mLicenseNumber = licenseNumber;
    }


    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public String getUserSurname() {
        return mUserSurname;
    }

    public void setUserSurname(String userSurname) {
        this.mUserSurname = userSurname;
    }
}
