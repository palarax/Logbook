package palarax.com.logbook.db;

import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.activeandroid.query.Select;
import com.backendless.BackendlessUser;

import java.util.List;

import palarax.com.logbook.model.Users;
import palarax.com.logbook.model.Utils;

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
        Users learner = new Users((Integer) user.getProperty(Utils.BACKENDLESS_LICENSE),
                (String) user.getProperty(Utils.BACKENDLESS_NAME),
                (String) user.getProperty(Utils.BACKENDLESS_SURNAME),
                (String) user.getProperty(Utils.BACKENDLESS_STATE),
                (String) user.getProperty(Utils.BACKENDLESS_DOB),
                (int) user.getProperty(Utils.BACKENDLESS_HOURS_COMPLETED));
        learner.save();
        setCurrentUser(learner.getId());
        return learner;
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


}
