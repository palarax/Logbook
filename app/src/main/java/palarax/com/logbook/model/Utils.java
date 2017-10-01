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

    public static String formatDate(String format, String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date formatedDate = dateFormat.parse(date);
        return new SimpleDateFormat(format).format(formatedDate);
    }

    //TODO: put into doc https://stackoverflow.com/questions/16713137/how-convert-string-to-datetime-in-android

    /**
     * Get local time
     *
     * @return local time
     */
    public static long getTimeDiffernce(String strStartDate, String strEndDate, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
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
    public static String convertDateToFormat(Long totalTime, int format) {
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = totalTime / daysInMilli;
        totalTime = totalTime % daysInMilli;

        long elapsedHours = totalTime / hoursInMilli;
        totalTime = totalTime % hoursInMilli;

        long elapsedMinutes = totalTime / minutesInMilli;

        switch (format) {
            case 1:
                return String.format("HOURS: %d Minutes: %d", elapsedHours, elapsedMinutes);
            default:
                return String.format("Days: %d Hours: %d Minutes: %d", elapsedDays, elapsedHours, elapsedMinutes);
        }

    }


    /**
     * Converts bytes to hex string
     *
     * @param bytes bytes to be converted
     * @return a string of hex values
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= bytes.length - 1; i++) {
            int b = bytes[i] & 0xff; //1111 1111
            if (b < 0x10)
                sb.append('0');

            sb.append(Integer.toHexString(b));
        }
        return sb.toString().toUpperCase();
    }
}