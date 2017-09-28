package palarax.com.logbook.model;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    public static final String LESSON_ID = "LESSON_ID";

    //BACKENDLESS
    public static final String BACKENDLESS_NAME = "name";
    public static final String BACKENDLESS_SURNAME = "surname";
    public static final String BACKENDLESS_LICENSE = "licence_id";
    public static final String BACKENDLESS_DOB = "DOB";
    public static final String BACKENDLESS_STATE = "state";
    public static final String BACKENDLESS_HOURS_COMPLETED= "hours_completed";

    /**
     * Get local time
     *
     * @return local time
     */
    public static String getTime() {
        return new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date());
    }

    //TODO: put into doc https://stackoverflow.com/questions/16713137/how-convert-string-to-datetime-in-android

    /**
     * Get local time
     *
     * @return local time
     */
    public static long getTimeDiffernce(String strStartDate, String strEndDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date startDate = dateFormat.parse(strStartDate);
        Date endDate = dateFormat.parse(strEndDate);
        return endDate.getTime() - startDate.getTime();
    }

    //TODO: implement into gui

    /**
     * Converts total time to Hours Minutes Seconds
     * @param totalTime total time of lesson
     * @return returns formated string
     */
    public static String convertDateToHhMmSs(Long totalTime) {
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = totalTime / daysInMilli;
        totalTime = totalTime % daysInMilli;

        long elapsedHours = totalTime / hoursInMilli;
        totalTime = totalTime % hoursInMilli;

        long elapsedMinutes = totalTime / minutesInMilli;

        return String.format("D:%d H:%d M:%d", elapsedDays, elapsedHours, elapsedMinutes);

    }

}