package com.mad.logbook.presenter;

import com.activeandroid.ActiveAndroid;
import com.mad.logbook.Utils;
import com.mad.logbook.interfaces.HomeContract;
import com.mad.logbook.model.Lesson;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ithai on 9/10/2017.
 */
public class HomePresenterTest extends com.activeandroid.app.Application implements HomeContract.View {


    HomePresenter mHomePresenter;

    @Before
    public void setup() {
        mHomePresenter = new HomePresenter(this);
    }

    @Test
    public void getLessonMonths() throws Exception {
        ActiveAndroid.initialize(this);
        List<String> mmyy = Arrays.asList("10/17", "11/17", "12/17", "01/18");
        List<Lesson> lessons = createLessons();
        ArrayList<String> result = mHomePresenter.getLessonMonths(lessons);

        assertEquals(mmyy, result);
    }

    @Test
    public void getGraphDataSet() throws Exception {

    }

    public List<Lesson> createLessons() throws ParseException {
        List<Lesson> lessons = new ArrayList<>();

        String startT = "05/10/17 15:00:00";
        String endT = "05/10/17 18:00:00";
        lessons.add(new Lesson(
                "TEST1",
                10000, 11111,
                00000001, 101021,
                283111, Utils.getTimeDiffernce(startT, endT, "dd/MM/yy HH:mm:ss")
                , startT, endT));


        startT = "10/10/17 17:00:00";
        endT = "10/10/17 22:00:00";
        lessons.add(new Lesson(
                "TEST2",
                12000, 11111,
                00000001, 101021,
                283111, Utils.getTimeDiffernce(startT, endT, "dd/MM/yy HH:mm:ss")
                , startT, endT));

        startT = "15/10/17 04:00:00";
        endT = "15/10/17 08:00:00";
        lessons.add(new Lesson(
                "TEST3",
                13000, 11111,
                00000001, 101021,
                283111, Utils.getTimeDiffernce(startT, endT, "dd/MM/yy HH:mm:ss")
                , startT, endT));

        startT = "01/11/17 15:00:00";
        endT = "01/11/17 18:00:00";
        lessons.add(new Lesson(
                "TEST4",
                10000, 11111,
                00000001, 101021,
                283111, Utils.getTimeDiffernce(startT, endT, "dd/MM/yy HH:mm:ss")
                , startT, endT));


        startT = "10/11/17 17:00:00";
        endT = "10/11/17 22:00:00";
        lessons.add(new Lesson(
                "TEST5",
                12000, 11111,
                00000001, 101021,
                283111, Utils.getTimeDiffernce(startT, endT, "dd/MM/yy HH:mm:ss")
                , startT, endT));


        startT = "15/11/17 04:00:00";
        endT = "15/11/17 08:00:00";
        lessons.add(new Lesson(
                "TEST6",
                13000, 11111,
                00000001, 101021,
                283111, Utils.getTimeDiffernce(startT, endT, "dd/MM/yy HH:mm:ss")
                , startT, endT));

        startT = "07/12/17 04:00:00";
        endT = "07/12/17 14:00:00";
        lessons.add(new Lesson(
                "TEST7",
                13000, 11111,
                00000001, 101021,
                283111, Utils.getTimeDiffernce(startT, endT, "dd/MM/yy HH:mm:ss")
                , startT, endT));

        startT = "11/12/17 13:00:00";
        endT = "11/12/17 16:00:00";
        lessons.add(new Lesson(
                "TEST8",
                10000, 11111,
                00000001, 101021,
                283111, Utils.getTimeDiffernce(startT, endT, "dd/MM/yy HH:mm:ss")
                , startT, endT));

        startT = "14/12/17 23:00:00";
        endT = "15/12/17 02:30:00";
        lessons.add(new Lesson(
                "TEST9",
                12000, 11111,
                00000001, 101021,
                283111, Utils.getTimeDiffernce(startT, endT, "dd/MM/yy HH:mm:ss")
                , startT, endT));

        startT = "23/12/17 05:00:00";
        endT = "23/12/17 10:00:00";
        lessons.add(new Lesson(
                "TEST10",
                13000, 11111,
                00000001, 101021,
                283111, Utils.getTimeDiffernce(startT, endT, "dd/MM/yy HH:mm:ss")
                , startT, endT));

        startT = "31/12/17 22:00:00";
        endT = "01/01/18 04:00:00";
        lessons.add(new Lesson(
                "TEST11",
                13000, 11111,
                00000001, 101021,
                283111, Utils.getTimeDiffernce(startT, endT, "dd/MM/yy HH:mm:ss")
                , startT, endT));

        return lessons;
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {

    }
}