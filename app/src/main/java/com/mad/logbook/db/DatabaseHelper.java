/*
 * Copyright 2016, The Android Open Source Project
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
package com.mad.logbook.db;

import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.backendless.BackendlessUser;
import com.mad.logbook.Utils;
import com.mad.logbook.model.Coordinates;
import com.mad.logbook.model.Lesson;
import com.mad.logbook.model.Users;

import java.util.List;

/**
 * Class that handles Database calls
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 */

public class DatabaseHelper {

    /**
     * Update user details or save new user
     * @param user user object
     * @return user object
     */
    public static Users updateLocalUserDetails(BackendlessUser user) {
        String query = String.format(
                "userSurname = \"%s\"," +
                        "userName = \"%s\" ," +
                        "state = \"%s\" , dob = \"%s\"",
                user.getProperty(Utils.BACKENDLESS_SURNAME),
                user.getProperty(Utils.BACKENDLESS_NAME),
                user.getProperty(Utils.BACKENDLESS_STATE),
                user.getProperty(Utils.BACKENDLESS_DOB)
        );
        Users student;
        try {
            student = new Select().from(Users.class).
                    where("licenceNumber = ?", user.getProperty(Utils.BACKENDLESS_LICENSE).toString()).executeSingle();
            //user exists so update them
            new Update(Users.class).set(query)
                    .where("licenceNumber = ?", student.getLicenceNumber()).execute();
        } catch (NullPointerException e) {
            //user doesn't exist so create new

            student = new Users((Integer) user.getProperty(Utils.BACKENDLESS_LICENSE),
                    (String) user.getProperty(Utils.BACKENDLESS_NAME),
                    (String) user.getProperty(Utils.BACKENDLESS_SURNAME),
                    (String) user.getProperty(Utils.BACKENDLESS_STATE),
                    (String) user.getProperty(Utils.BACKENDLESS_DOB), 0);
            student.save();
        }
        setCurrentUser(student.getId());

        return student;
    }

    public static Users getCurrentUser() {
        return new Select().from(Users.class).where("active = ?", "1").executeSingle();
    }

    /**
     * Set current user boolean to true
     *
     * @param userId user id in the database
     */
    public static void setCurrentUser(Long userId) {
        Users user = Users.load(Users.class, userId);
        user.setActiveUser(1);
        user.save();
    }

    /**
     * Set all users "activeUsers" column to false
     */
    public static void clearActiveUsers() {
        try {
            List<Users> users = new Select().all().from(Users.class).execute();
            for (Users student : users) {
                student.setActiveUser(0);
                student.save();
            }
        } catch (SQLiteException e) {
            Log.e("DatabaseHelper", "Clearing user exception: " + e);
        }
    }

    /**
     * Get Coordinates for the a specific lesson
     *
     * @param lessonId lesson id of required coordinates
     * @return coordiantes linked to that lesson
     */
    public static List<Coordinates> getLessonCoordinates(long lessonId) {
        return new Select().from(Coordinates.class).
                where("lessonId = ?", Long.toString(lessonId)).execute();
    }

    public static List<Lesson> getUserLessons() {
        return new Select().from(Lesson.class).where("studentLicence = ?",
                getCurrentUser().getLicenceNumber()).execute();
    }

}
