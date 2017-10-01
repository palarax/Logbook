package palarax.com.logbook.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import palarax.com.logbook.R;
import palarax.com.logbook.db.DatabaseHelper;
import palarax.com.logbook.model.Coordinates;
import palarax.com.logbook.model.Lesson;
import palarax.com.logbook.model.Users;
import palarax.com.logbook.model.Utils;

/**
 * Presents and manages lesson data
 * @author Ilya Thai (11972078)
 * @date 29-Sep-17
 * @version 1.0
 */

public class LessonPresenter {

    private static final String TAG = LessonPresenter.class.getSimpleName(); //used for debugging
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private List<Lesson> mLessonList;
    private Lesson mCurrentLesson;
    private Activity mCurrentActivity;
    private Location mCurrentLocation;
    private List<LatLng> mCoordinates = new ArrayList<>();  //store coordinates

    public LessonPresenter(Activity activity) {
        Users currentUser = new Select().from(Users.class).where("active = ?", "1").executeSingle();
        this.mLessonList = new Select().from(Lesson.class).where("studentLicence = ?",
                currentUser.getLicenseNumber()).execute();
        this.mCurrentActivity = activity;
    }

    public List<Lesson> getAllLessons(){
        return mLessonList;
    }

    /**
     * Get day or night hours for the lesson
     * @param isDay are day night hours needed
     * @param lesson lesson to be analysed
     * @return hours for day or night
     * @throws ParseException
     */
    private long getLessonDayNightHours(boolean isDay, Lesson lesson) throws ParseException {
        long totalDayHours = 0;
        long totalNightHours = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date formatedEndDate = dateFormat.parse(Utils.formatDate("HH:mm", lesson.getEndTime()));
        Date formatedStartDate = dateFormat.parse(Utils.formatDate("HH:mm", lesson.getStartTime()));

        if ((formatedStartDate.before(dateFormat.parse("07:00")) ||
                (formatedStartDate.after(dateFormat.parse("19:00"))))) {
            if ((formatedEndDate.before(dateFormat.parse("07:00")) ||
                    (formatedEndDate.after(dateFormat.parse("19:00"))))) {
                totalNightHours += lesson.getTotalTime() / 1000 / 60 / 60;
            } else if (formatedEndDate.before(dateFormat.parse("07:00"))) {
                totalDayHours = Utils.getTimeDiffernce("07:00",
                        Utils.formatDate("HH:mm", lesson.getEndTime()), "HH:mm") / 1000 / 60;
                totalNightHours += lesson.getTotalTime() / 1000 / 60 - totalDayHours;
            }
        } else {
            if ((formatedEndDate.before(dateFormat.parse("19:00")))) {
                totalDayHours += lesson.getTotalTime() / 1000 / 60 / 60;
            } else if (formatedEndDate.after(dateFormat.parse("19:00"))) {
                totalNightHours = Utils.getTimeDiffernce("19:00",
                        Utils.formatDate("HH:mm", lesson.getEndTime()), "HH:mm") / 1000 / 60;
                totalDayHours += lesson.getTotalTime() / 1000 / 60 - totalNightHours;
            }
        }

        if (isDay) {
            return totalDayHours;
        } else {
            return totalNightHours;
        }
    }

    public long getDayNightDroveLesson(boolean isDay, List<Lesson> lessons) throws ParseException {
        long totalDayHours = 0;
        long totalNightHours = 0;

        for (Lesson lesson : lessons) {
            totalDayHours = +getLessonDayNightHours(true, lesson);
            totalNightHours = +getLessonDayNightHours(false, lesson);
        }

        if (isDay) {
            return totalDayHours;
        } else {
            return totalNightHours;
        }

    }

    /**
     * Get coordinates for Polyline on the map
     * @param options coordinates to put on the map
     * @return coordinates to draw
     */
    public PolylineOptions getCoordinates(PolylineOptions options) {
        for (int i = 0; i < mCoordinates.size(); i++) {
            LatLng point = mCoordinates.get(i);
            options.add(point);
        }
        return options;
    }


    /**
     * Get months and Year to put on x axis
     *
     * @return list of months and year
     */
    public ArrayList<String> getLessonMonths() throws ParseException {
        ArrayList dates = new ArrayList();
        for (Lesson lesson : mLessonList) {
            if (!dates.contains(Utils.formatDate("MM/yy", lesson.getStartTime()))) {
                dates.add(Utils.formatDate("MM/yy", lesson.getStartTime()));
            }
        }
        return dates;
    }


    /**
     * Get data set for summary graph
     *
     * @param xAxisValues x axis of the graph
     * @return a list of Bar data sets
     * @throws ParseException
     */
    public ArrayList<BarDataSet> getGraphDataSet(ArrayList<String> xAxisValues) throws ParseException {
        ArrayList<BarDataSet> lessonDataSet = new ArrayList<>();
        ArrayList<BarEntry> distanceList = new ArrayList<>();
        ArrayList<BarEntry> dayHoursList = new ArrayList<>();
        ArrayList<BarEntry> nightHoursList = new ArrayList<>();
        double totalMonthDistance = 0.0;
        long totalDayHours = 0;
        long totalNightHours = 0;

        for (String mmyy : xAxisValues) {
            for (Lesson lesson : mLessonList) {
                totalDayHours = 0;
                totalNightHours = 0;
                totalMonthDistance += lesson.getDistance();
                if (Utils.formatDate("MM/yy", lesson.getStartTime()).equals(mmyy) &&
                        Utils.formatDate("MM/yy", lesson.getEndTime()).equals(mmyy)) {
                    totalDayHours = +getLessonDayNightHours(true, lesson);
                    totalNightHours = +getLessonDayNightHours(false, lesson);
                } else if (Utils.formatDate("MM/yy", lesson.getStartTime()).equals(mmyy)) {
                    totalDayHours = +getLessonDayNightHours(true, lesson);
                    totalNightHours = +Utils.getTimeDiffernce("19:00",
                            Utils.formatDate("HH:mm", lesson.getEndTime()), "HH:mm");

                }
            }
            totalDayHours = totalDayHours / 1000 / 60 / 60;
            totalNightHours = totalNightHours / 1000 / 60 / 60;
            totalMonthDistance = totalMonthDistance / 1000;
            distanceList.add(new BarEntry((float) totalMonthDistance, xAxisValues.indexOf(mmyy)));
            dayHoursList.add(new BarEntry((float) totalDayHours, xAxisValues.indexOf(mmyy)));
            nightHoursList.add(new BarEntry((float) totalNightHours, xAxisValues.indexOf(mmyy)));
        }

        BarDataSet barDataSet1 = new BarDataSet(distanceList, "Kms");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet2 = new BarDataSet(dayHoursList, "Day hours");
        barDataSet2.setColor(Color.rgb(155, 0, 0));
        BarDataSet barDataSet3 = new BarDataSet(nightHoursList, "Day hours");
        barDataSet2.setColor(Color.rgb(0, 0, 155));

        lessonDataSet.add(barDataSet1);
        lessonDataSet.add(barDataSet2);
        lessonDataSet.add(barDataSet3);

        return lessonDataSet;
    }


    public Lesson getCurrentLesson() {
        return mCurrentLesson;
    }

    public void setCurrentLesson(Long lesson_id) {
        mCurrentLesson = Lesson.load(Lesson.class, lesson_id);
    }

    /**
     * Get first coordinates for a specific lesson
     *
     * @return Latitude /Longitude
     */
    public LatLng getFirstCoordinates() {
        return mCoordinates.get(0);
    }

    public void setCurrentCoordinates(Long lesson_id) {
        List<Coordinates> lessonCoordinates = DatabaseHelper.getLessonCoordinates(lesson_id);
        for (Coordinates coordinates : lessonCoordinates) {
            mCoordinates.add(new LatLng(coordinates.getLatitude(), coordinates.getLongitude()));
        }
    }

    /**
     * Verify that all lesson data has been completed
     */
    public boolean isCurrentLessonComplete() {
        if (mCurrentLesson.getLicencePlate().isEmpty() || mCurrentLesson.getSpeed() == 0 ||
                mCurrentLesson.getDistance() == 0 || mCurrentLesson.getSupervisorLicence() == 0 ||
                mCurrentLesson.getStartOdometer() == 0 || mCurrentLesson.getEndOdometer() == 0 ||
                mCurrentLesson.getTotalTime() == 0 || mCurrentLesson.getStartTime().isEmpty() ||
                mCurrentLesson.getEndTime().isEmpty() || mCurrentLesson.getDistance() == 0) {
            mCurrentLesson.delete();
            return false;
        } return true;

    }

    /**
     * Get data from db and load into GUI
     */
    public void updateLessons(LessonsAdapter adapter, long studentLicence) {
        //clear them
        mLessonList.clear();
        //add lessons
        List<Lesson> newLessons = new Select().from(Lesson.class).where("studentLicence = ?", studentLicence).execute();
        for (Lesson lesson : newLessons) {
            mLessonList.add(lesson);
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     *                            TODO: Code from: https://developer.android.com/guide/topics/location/strategies.html add to Software doc
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
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
     * TODO: Code from: https://developer.android.com/guide/topics/location/strategies.html add to Software doc
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Checks lesson integrity. Lesson has to be longer than 10 minutes and
     * distance travelled should be more than 500m, start odometer bigger than end odometer
     */
    public void checkLessonIntegrity() {
        double distanceTravelled = SphericalUtil.computeLength(mCoordinates);//calculate distance travelled
        long totalTime = 0;
        try {
            totalTime = Utils.getTimeDiffernce(getCurrentLesson().getStartTime(), mCurrentLesson.getEndTime(),"dd/MM/yy HH:mm:ss");
        } catch (ParseException e) {
            Log.e(TAG, "Error: " + e);
            //This error should never occur
        }
        long totalTimeMinutes = totalTime / 1000 / 60;
        if ((
                mCurrentLesson.getEndOdometer() > getCurrentLesson().getStartOdometer())) {
            //TODO: once finished testing, put it back
        /*
        if (distanceTravelled > 500 && totalTimeMinutes > 10 && (
                mCurrentLesson.getEndOdometer() > getCurrentLesson().getStartOdometer())) {
*/
            getCurrentLesson().setDistance(distanceTravelled);
            getCurrentLesson().setTotalTime(totalTime);
            getCurrentLesson().setSpeed(distanceTravelled / totalTime);
            //save lesson
            mCurrentLesson.save();
        } else {
            Log.e(TAG, "" + distanceTravelled);
            Log.e(TAG, "" + totalTimeMinutes);
            Log.e(TAG, "" + mCurrentLesson.getEndOdometer());
            Log.e(TAG, "" + getCurrentLesson().getStartOdometer());
            //Lesson is too short or didn't travel enough
            //Remove coordinates
            Toast.makeText(mCurrentActivity, mCurrentActivity.getString(R.string.bad_lesson), Toast.LENGTH_SHORT).show();

            new Delete().from(Coordinates.class).where("lessonId = ?", mCurrentLesson.getId().toString()).execute();
            //remove lesson
            mCurrentLesson.delete();
        }
    }

    /**
     * Gets final Odometer reading from user
     */
    public void getEndOdometerFromUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mCurrentActivity);
        builder.setTitle(mCurrentActivity.getString(R.string.end_odometer_input));

        // Set up the input
        final EditText input = new EditText(mCurrentActivity);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(input.getText().toString())) {
                    Toast.makeText(mCurrentActivity, mCurrentActivity.getString(R.string.error_odometer_null), Toast.LENGTH_SHORT).show();
                } else if (Long.parseLong(input.getText().toString()) < getCurrentLesson().getStartOdometer()) {
                    Toast.makeText(mCurrentActivity, mCurrentActivity.getString(R.string.error_odometer_small), Toast.LENGTH_SHORT).show();
                } else {
                    getCurrentLesson().setEndOdometer(Integer.parseInt(input.getText().toString()));
                    mCurrentActivity.finish();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mCurrentActivity, mCurrentActivity.getString(R.string.error_odometer_null), Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Check if location is accurate enough
     *
     * @param newLocation new location found
     * @return true if an update is required
     */
    public boolean updateLocation(Location newLocation) {
        if (isBetterLocation(newLocation, mCurrentLocation)) {
            mCurrentLocation = newLocation;
            //Update lesson object
            mCoordinates.add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            //Store coordinates to db
            Coordinates locationPoints = new Coordinates(mCurrentLesson.getId(),
                    mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), mCurrentLesson.getStudentLicence());
            locationPoints.save();
            return true;
        }
        return false;
    }

    /**
     * Check if coordinates got was the first location found
     *
     * @param newLocation new location found
     * @return true if it is the first location found
     */
    public boolean firstLocation(Location newLocation) {
        if (mCurrentLocation == null) {
            mCurrentLocation = newLocation;
            return true;
        }
        return false;
    }
}
