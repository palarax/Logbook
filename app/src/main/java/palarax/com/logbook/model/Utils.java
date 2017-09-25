package palarax.com.logbook.model;

import android.location.Location;

/**
 * Utility values and Constants used in the entire application
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 */
public class Utils {
    public static final String BACKENDLESS_APPLICATION_ID = "F1D55E01-2495-D20A-FF0E-58AE50CB9700";
    public static final String BACKENDLESS_API_KEY = "5DB4EEBE-A054-CE7D-FFCD-F6F74B4FC500";
    public static final String BACKENDLESS_SERVER_URL = "http://api.backendless.com";
    public static final String UTF8 = "UTF-8";
    public static final String UTF16 = "UTF-16";

    //BACKENDLESS
    public static final String BACKENDLESS_NAME = "name";
    public static final String BACKENDLESS_SURNAME = "surname";
    public static final String BACKENDLESS_LICENSE = "licence_id";
    public static final String BACKENDLESS_DOB = "DOB";
    public static final String BACKENDLESS_STATE = "state";

    public static final String GOOGLE_MAPS_API_KEY = "AIzaSyC5UHViQYMaSrnkBes0YJn2bWIyPFVqhUM";


    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}