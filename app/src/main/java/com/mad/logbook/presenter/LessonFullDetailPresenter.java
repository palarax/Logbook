package com.mad.logbook.presenter;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mad.logbook.db.DatabaseHelper;
import com.mad.logbook.interfaces.LessonFullDetailContract;
import com.mad.logbook.model.Coordinates;
import com.mad.logbook.model.Lesson;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * Created by Ithai on 9/10/2017.
 */

public class LessonFullDetailPresenter implements LessonFullDetailContract.Presenter {

    private LessonFullDetailContract.View mLessonFullView;
    private List<LatLng> mCoordinates = new ArrayList<>();
    private Lesson mCurrentLesson;

    /**
     * Backendless initializes WeakReference
     */
    public LessonFullDetailPresenter(@NonNull LessonFullDetailContract.View view, Long lesson_id) {

        mLessonFullView = checkNotNull(view, "mLessonFullView cannot be null");
        mLessonFullView.setPresenter(this);
        mCurrentLesson = Lesson.load(Lesson.class, lesson_id);
        setCurrentCoordinates(lesson_id);
    }


    /**
     * Set coordinates linked to the active lesson that are saved in the DB
     *
     * @param lesson_id active lesson id
     */
    private void setCurrentCoordinates(Long lesson_id) {
        List<Coordinates> lessonCoordinates = DatabaseHelper.getLessonCoordinates(lesson_id);
        for (Coordinates coordinates : lessonCoordinates) {
            mCoordinates.add(new LatLng(coordinates.getLatitude(), coordinates.getLongitude()));
        }
    }

    public Lesson getCurrentLesson() {
        return mCurrentLesson;
    }

    public List<LatLng> getActiveLessonCoordinates() {
        return mCoordinates;
    }

    /**
     * Get coordinates for Polyline on the map
     *
     * @param options coordinates to put on the map
     * @return coordinates to draw
     */
    public PolylineOptions getPolyLine(PolylineOptions options, List<LatLng> coordinates) {
        for (int i = 0; i < coordinates.size(); i++) {
            LatLng point = coordinates.get(i);
            options.add(point);
        }
        return options;
    }
}
