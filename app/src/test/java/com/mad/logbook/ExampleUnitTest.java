package palarax.com.logbook;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    //TODO: use this to test distance and time statistics
    /*
    private void testCreateData() throws ParseException {

        String startT = "05/10/17 15:00:00";
        String endT = "05/10/17 18:00:00";
        Lesson lesson = new Lesson(
                "TEST1",
                10000, 11111,
                mUserPresenter.getStudent().getLicenseNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "10/10/17 17:00:00";
        endT = "10/10/17 22:00:00";
        lesson = new Lesson(
                "TEST2",
                12000, 11111,
                mUserPresenter.getStudent().getLicenseNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "15/10/17 04:00:00";
        endT = "15/10/17 08:00:00";
        lesson = new Lesson(
                "TEST3",
                13000, 11111,
                mUserPresenter.getStudent().getLicenseNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "01/11/17 15:00:00";
        endT = "01/11/17 18:00:00";
        lesson = new Lesson(
                "TEST4",
                10000, 11111,
                mUserPresenter.getStudent().getLicenseNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "10/11/17 17:00:00";
        endT = "10/11/17 22:00:00";
        lesson = new Lesson(
                "TEST5",
                12000, 11111,
                mUserPresenter.getStudent().getLicenseNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "15/11/17 04:00:00";
        endT = "15/11/17 08:00:00";
        lesson = new Lesson(
                "TEST6",
                13000, 11111,
                mUserPresenter.getStudent().getLicenseNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "07/12/17 04:00:00";
        endT = "07/12/17 14:00:00";
        lesson = new Lesson(
                "TEST7",
                13000, 11111,
                mUserPresenter.getStudent().getLicenseNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "11/12/17 13:00:00";
        endT = "11/12/17 16:00:00";
        lesson = new Lesson(
                "TEST8",
                10000, 11111,
                mUserPresenter.getStudent().getLicenseNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "14/12/17 23:00:00";
        endT = "15/12/17 02:30:00";
        lesson = new Lesson(
                "TEST9",
                12000, 11111,
                mUserPresenter.getStudent().getLicenseNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "23/12/17 05:00:00";
        endT = "23/12/17 10:00:00";
        lesson = new Lesson(
                "TEST10",
                13000, 11111,
                mUserPresenter.getStudent().getLicenseNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "31/12/17 22:00:00";
        endT = "01/01/18 04:00:00";
        lesson = new Lesson(
                "TEST11",
                13000, 11111,
                mUserPresenter.getStudent().getLicenseNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();
    }*/

}