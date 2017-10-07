package com.mad.logbook.interfaces;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mad.logbook.BaseView;
import com.mad.logbook.model.Lesson;

import java.util.List;

/**
 * Created by Ithai on 9/10/2017.
 */

public interface DrivingLessonContract {
    interface View extends BaseView<Presenter> {

    }

    interface Presenter {
        boolean firstLocation(Location newLocation);

        boolean updateLocation(Location newLocation);

        List<LatLng> getActiveLessonCoordinates();

        PolylineOptions getPolyLine(PolylineOptions options, List<LatLng> coordinates);

        Lesson getActvieLesson();

        boolean checkLessonIntegrity(Lesson currentLesson);
    }
}
