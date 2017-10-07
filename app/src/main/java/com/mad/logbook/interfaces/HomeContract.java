package com.mad.logbook.interfaces;

import com.github.mikephil.charting.data.BarEntry;
import com.mad.logbook.BaseView;
import com.mad.logbook.model.Lesson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ithai on 9/10/2017.
 */

public interface HomeContract {
    interface View extends BaseView<Presenter> {

    }

    interface Presenter {
        ArrayList<String> getLessonMonths(List<Lesson> userLessons) throws ParseException;

        ArrayList<ArrayList<BarEntry>> getGraphDataSet(
                List<Lesson> userLessons, ArrayList<String> xAxisValues) throws ParseException;

        double[] getDayNightDroveLesson(List<Lesson> lessons) throws ParseException;
    }
}
