package com.mad.logbook.interfaces;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mad.logbook.BaseView;
import com.mad.logbook.model.Lesson;

import java.util.List;

/**
 * Created by Ithai on 9/10/2017.
 */

public interface LessonFullDetailContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter {
        Lesson getCurrentLesson();

        List<LatLng> getActiveLessonCoordinates();

        PolylineOptions getPolyLine(PolylineOptions options, List<LatLng> coordinates);

    }
}
