package com.mad.logbook;


import android.app.Activity;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.text.format.DateUtils;
import android.util.Log;

import com.mad.logbook.db.DatabaseHelper;
import com.mad.logbook.model.Coordinates;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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
    public static final String BACKENDLESS_CONTACT_NUMBER = "contact_number";

    /**
     * Get local time
     *
     * @return local time
     */
    public static String getTime() {
        return new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public static String formatDate(String format, String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault());
        Date formatedDate = dateFormat.parse(date);
        return new SimpleDateFormat(format, Locale.getDefault()).format(formatedDate);
    }

    /**
     * Get time difference in milliseconds
     *
     * @return local time
     */
    public static long getTimeDiffernce(String strStartDate, String strEndDate, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date startDate = dateFormat.parse(strStartDate);
        Date endDate = dateFormat.parse(strEndDate);
        return endDate.getTime() - startDate.getTime();
    }

    /**
     * Checks if the date selected is before today
     * @param date date to compare
     * @return true if date is before today
     */
    public static boolean isDobCorrect(String date) {
        Date currentDate = new Date();
        Date formattedDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            formattedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return !DateUtils.isToday(formattedDate.getTime()) && formattedDate.before(currentDate);
    }

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
                return String.format(Locale.getDefault(), "HOURS: %d Minutes: %d", elapsedHours, elapsedMinutes);
            default:
                return String.format(Locale.getDefault(), "Days: %d Hours: %d Minutes: %d", elapsedDays, elapsedHours, elapsedMinutes);
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

    /**
     * Get start and end address of the lesson route
     * @param lessonId lesson id
     * @param activity activity that calls will display address
     * @return start and end addresses
     */
    public static String[] getAddress(long lessonId, Activity activity){
        Geocoder geocoder;
        List<Address> addresses;
        String address[] = new String[]{activity.getString(R.string.error_address_not_found),
                activity.getString(R.string.error_address_not_found)};
        geocoder = new Geocoder(activity, Locale.getDefault());
        List<Coordinates> coordinates = DatabaseHelper.getLessonCoordinates(lessonId);
        try {
            addresses = geocoder.getFromLocation(coordinates.get(0).getLatitude(), coordinates.get(0).getLongitude(), 1);
            address[0] = addresses.get(0).getAddressLine(0);
            addresses = geocoder.getFromLocation(coordinates.get(coordinates.size()-1).getLatitude(), coordinates.get(coordinates.size()-1).getLongitude(), 1);
            address[1] = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            Log.e("UtilsError","ERROR: "+e.getMessage());
        } catch (IndexOutOfBoundsException e){
            Log.e("UtilsError","ERROR: "+e.getMessage());
        }
        return address;
    }


    //TODO: https://stackoverflow.com/questions/11567212/how-to-set-background-to-a-random-color-at-button-press-in-android
    /**
     * Get random color
     * @return color integer
     */
    public static int getRandomColor(){
        Random color = new Random();
        return Color.argb(255, color.nextInt(255), color.nextInt(255), color.nextInt(255));
    }
}