package palarax.com.logbook.presenter;

import com.activeandroid.query.Select;

import java.util.List;

import palarax.com.logbook.model.Lesson;

/**
 * Created by Ithai on 29/09/2017.
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
        this.mLessonList = new Select().all().from(Lesson.class).execute();
        adapter.notifyDataSetChanged();

        /*List<Lesson> lessons = Lesson.listAll(Lesson.class);
        for (Lesson lesson : lessons) {
            mLessonList.add(lesson);
        }*/
    }
}
