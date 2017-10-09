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
package com.mad.logbook.presenter;

import android.support.annotation.NonNull;

import com.github.mikephil.charting.data.BarEntry;
import com.mad.logbook.Utils;
import com.mad.logbook.interfaces.HomeContract;
import com.mad.logbook.model.Lesson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI, retrieves the data and updates the UI
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 * @date 09-Oct-17
 */

public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View mHomeView;

    /**
     * Backendless initializes WeakReference
     */
    public HomePresenter(@NonNull HomeContract.View view) {

        mHomeView = checkNotNull(view, "HomeView cannot be null");
        mHomeView.setPresenter(this);
    }

    /**
     * Get months and Year to put on x axis
     *
     * @return list of months and year
     */
    public ArrayList<String> getLessonMonths(List<Lesson> userLessons) throws ParseException {
        ArrayList<String> dates = new ArrayList<>();
        for (Lesson lesson : userLessons) {
            //start month may not be end month
            if (!dates.contains(Utils.formatDate("MM/yy", lesson.getStartTime()))) {
                dates.add(Utils.formatDate("MM/yy", lesson.getStartTime()));

            }
            if (!dates.contains(Utils.formatDate("MM/yy", lesson.getEndTime()))) {
                dates.add(Utils.formatDate("MM/yy", lesson.getEndTime()));

            }
        }
        return dates;
    }

    /**
     * Get data set for summary graph
     *
     * @param userLessons a list of lessons to get data from
     * @return a list of Bar data sets
     * @throws ParseException
     */
    public ArrayList<ArrayList<BarEntry>> getGraphDataSet(
            List<Lesson> userLessons, ArrayList<String> xAxisValues) throws ParseException {
        ArrayList<ArrayList<BarEntry>> dataSet = new ArrayList<>();

        ArrayList<BarEntry> distanceList = new ArrayList<>();
        ArrayList<BarEntry> dayHoursList = new ArrayList<>();
        ArrayList<BarEntry> nightHoursList = new ArrayList<>();
        double totalMonthDistance = 0;
        double totalDayHours = 0;
        double totalNightHours = 0;
        double[] monthData = new double[]{0, 0, 0};

        for (String mmyy : xAxisValues) {
            //reset all values for new month
            totalMonthDistance = 0;
            totalDayHours = 0;
            totalNightHours = 0;
            for (Lesson lesson : userLessons) {
                monthData = getMonthStats(mmyy, lesson);
                totalMonthDistance += monthData[0];
                totalDayHours += monthData[1];
                totalNightHours += monthData[2];
            }
            //convert values to km and hours
            totalDayHours = totalDayHours / 1000 / 60 / 60;
            totalNightHours = totalNightHours / 1000 / 60 / 60;
            totalMonthDistance = totalMonthDistance / 1000;
            distanceList.add(new BarEntry((float) totalMonthDistance, xAxisValues.indexOf(mmyy)));
            dayHoursList.add(new BarEntry((float) totalDayHours, xAxisValues.indexOf(mmyy)));
            nightHoursList.add(new BarEntry((float) totalNightHours, xAxisValues.indexOf(mmyy)));
        }
        dataSet.add(distanceList);
        dataSet.add(dayHoursList);
        dataSet.add(nightHoursList);

        return dataSet;
    }


    /**
     * Get distance, night and day hours for that lesson in specific month
     *
     * @param monthYear month and year selected in format "mmyy"
     * @param lesson    lesson to analyse
     * @return an array with distance, day and night hours
     * @throws ParseException
     */
    private double[] getMonthStats(String monthYear, Lesson lesson) throws ParseException {
        double distance = 0;
        double dayHours = 0;
        double nightHours = 0;
        double[] dayNightTime = new double[]{0, 0};

        if (Utils.formatDate("MM/yy", lesson.getStartTime()).equals(monthYear) &&
                Utils.formatDate("MM/yy", lesson.getEndTime()).equals(monthYear)) {
            distance = lesson.getDistance();
            dayNightTime = getLessonDayNightTime(lesson);
            dayHours = dayNightTime[0];
            nightHours = dayNightTime[1];
        } else if (Utils.formatDate("MM/yy", lesson.getStartTime()).equals(monthYear)) {
            //instance where the lesson start and end are not in the same month
            distance = lesson.getDistance();
            dayNightTime = getLessonDayNightTime(lesson);
            dayHours = dayNightTime[0];
            nightHours = dayNightTime[1] - Utils.getTimeDiffernce("00:00",
                    Utils.formatDate("HH:mm", lesson.getEndTime()), "HH:mm");

        } else if (Utils.formatDate("MM/yy", lesson.getEndTime()).equals(monthYear)) {
            //get remaining night hours drove
            nightHours = Utils.getTimeDiffernce("00:00",
                    Utils.formatDate("HH:mm", lesson.getEndTime()), "HH:mm");
        }

        return new double[]{distance, dayHours, nightHours};
    }

    /**
     * Get day or night time in milliseconds for the lesson
     *
     * @param lesson lesson to be analysed
     * @return hours for day and night
     * @throws ParseException
     */
    private double[] getLessonDayNightTime(Lesson lesson) throws ParseException {
        double totalDayTime = 0;
        double totalNightTime = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date formatedEndDate = dateFormat.parse(Utils.formatDate("HH:mm", lesson.getEndTime()));
        Date formatedStartDate = dateFormat.parse(Utils.formatDate("HH:mm", lesson.getStartTime()));

        if ((formatedStartDate.before(dateFormat.parse("07:00")) ||
                (formatedStartDate.after(dateFormat.parse("19:00"))))) {
            if ((formatedEndDate.before(dateFormat.parse("07:00")) ||
                    (formatedEndDate.after(dateFormat.parse("19:00"))))) {
                totalNightTime += lesson.getTotalTime();
            } else {
                totalDayTime = Utils.getTimeDiffernce("07:00",
                        Utils.formatDate("HH:mm", lesson.getEndTime()), "HH:mm");
                totalNightTime += (lesson.getTotalTime() - totalDayTime);
            }
        } else {
            if ((formatedEndDate.before(dateFormat.parse("19:00")))) {
                totalDayTime += lesson.getTotalTime();
            } else if (formatedEndDate.after(dateFormat.parse("19:00"))) {
                totalNightTime = Utils.getTimeDiffernce("19:00",
                        Utils.formatDate("HH:mm", lesson.getEndTime()), "HH:mm");
                totalDayTime += (lesson.getTotalTime() - totalNightTime);
            }
        }

        return new double[]{totalDayTime, totalNightTime};
    }

    /**
     * Gets day and night milliseconds drove in total
     *
     * @param lessons a list of lessons to get data from
     * @return day or night time in milliseconds
     * @throws ParseException
     */
    public double[] getDayNightDroveLesson(List<Lesson> lessons) throws ParseException {
        double totalDayHours = 0;
        double totalNightHours = 0;
        double[] dayNightTime;

        for (Lesson lesson : lessons) {
            dayNightTime = getLessonDayNightTime(lesson);
            totalDayHours += dayNightTime[0];
            totalNightHours += dayNightTime[1];
        }

        return new double[]{totalDayHours, totalNightHours};
    }

}
