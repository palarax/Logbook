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
 * Listens to user actions from the UI, retrieves the data and updates the UI
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 * @date 09-Oct-17
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
