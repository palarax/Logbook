package palarax.com.logbook.presenter;

import com.activeandroid.query.Select;

import java.util.List;

import palarax.com.logbook.model.Lesson;

/**
 * Presents and manages lesson data
 * @author Ilya Thai (11972078)
 * @date 29-Sep-17
 * @version 1.0
 */

public class LessonPresenter {

    private List<Lesson> mLessonList;

    public LessonPresenter(){
        this.mLessonList = new Select().all().from(Lesson.class).execute();
    }

    public List<Lesson> getAllLessons(){
        return mLessonList;
    }

    /**
     * Get data from db and load into GUI
     */
    public void updateLessons(LessonsAdapter adapter){
        //clear them
        mLessonList.clear();
        //add lessons
        //TODO: check if you need to cycle through array
        mLessonList = new Select().all().from(Lesson.class).execute();
        /*List<Lesson> lessons = new Select().all().from(Lesson.class).execute();
        for (Lesson lesson : lessons) {
            mLessonList.add(lesson);
        }*/
        adapter.notifyDataSetChanged();
    }
}
