package palarax.com.logbook.db;

import com.backendless.BackendlessUser;
import com.orm.SugarApp;

import palarax.com.logbook.model.Users;
import palarax.com.logbook.model.Utils;

/**
 * Class that handles Database calls
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 */

public class DatabaseHelper extends SugarApp{

    /**
     * Save a new user to the database
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
        return learner;
    }


}
