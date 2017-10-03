package palarax.com.logbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

import palarax.com.logbook.R;
import palarax.com.logbook.activity.DrivingLesson;
import palarax.com.logbook.model.Lesson;
import palarax.com.logbook.model.Utils;
import palarax.com.logbook.presenter.LessonPresenter;
import palarax.com.logbook.presenter.UserPresenter;

/**
 * Home fragment that initiates a lesson
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 * @date 09-Sep-17
 */

public class HomeFragment extends Fragment {

    // distance the button travels down the screen vertically
    private static final int BTN_Y_DISTANCE = 400;

    private EditText mLpn, mStartOdometer, mSupervisorLicence;
    private TextView numberOfLessons, hoursDroveNight, hoursDroveDay;
    private Button mStart;
    private RelativeLayout.LayoutParams mLayoutParams;
    private boolean mBtnAnimated = false;

    private UserPresenter mUserPresenter;
    private LessonPresenter mLessonPresenter;
    /**
     * Handles sliding button animation. Used to transfer the button view, as the original animation
     * only transfers the pixels
     */
    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mLayoutParams.topMargin = mLayoutParams.topMargin + BTN_Y_DISTANCE;
            mStart.setLayoutParams(mLayoutParams);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Fixes the animation not resetting
        super.onCreate(savedInstanceState);
        //reset animation
        mBtnAnimated = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLessonInformation();
        //clear edit
        mLpn.setText("");
        mStartOdometer.setText("");
        mSupervisorLicence.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Set student
        mUserPresenter = new UserPresenter();
        mLessonPresenter = new LessonPresenter(getActivity());

        numberOfLessons = view.findViewById(R.id.txt_drives);
        hoursDroveNight = view.findViewById(R.id.hours_drove_night);
        hoursDroveDay = view.findViewById(R.id.hours_drove_day);
        TextView mNameText = view.findViewById(R.id.txt_name);
        TextView mLicenseText = view.findViewById(R.id.txt_license);
        TextView mDobText = view.findViewById(R.id.txt_dob);
        TextView mStateText = view.findViewById(R.id.txt_state);
        TextView mProgressText = view.findViewById(R.id.txt_completed);
        mStart = view.findViewById(R.id.start_lesson);

        mLpn = view.findViewById(R.id.edit_lpn);
        mStartOdometer = view.findViewById(R.id.edit_start_odometer);
        mSupervisorLicence = view.findViewById(R.id.edit_supervisor_license);

        mLayoutParams = (RelativeLayout.LayoutParams) mStart.getLayoutParams();
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mBtnAnimated){
                    //TODO: use morphing button
                    TranslateAnimation anim = new TranslateAnimation(0, 0, 0, BTN_Y_DISTANCE);
                    anim.setDuration(1000); // 1000 ms = 1second
                    anim.setAnimationListener(mAnimationListener);
                    view.startAnimation(anim);
                    mBtnAnimated = true;
                }else{
                    if(checkIfAllComplete()) {
                        Intent intent = new Intent(getActivity(), DrivingLesson.class);
                        //Save lesson to db and send lesson id to lesson activity
                        //TODO: check if i should put it in presenter /db manager
                        Lesson lesson = new Lesson(
                                mLpn.getText().toString(),
                                0, Long.parseLong(mSupervisorLicence.getText().toString()),
                                mUserPresenter.getStudent().getLicenseNumber(), Integer.parseInt(mStartOdometer.getText().toString()),
                                0, 0, Utils.getTime(), null);
                        lesson.save();
                        intent.putExtra(Utils.LESSON_ID, lesson.getId());
                        startActivity(intent);
                    }
                }
            }
        });
        mUserPresenter.populateUserData(mNameText, mLicenseText, mDobText,
                mStateText, mProgressText, getContext());
        updateLessonInformation();
    }

    /**
     * Updates lesson information on GUI
     */
    private void updateLessonInformation() {
        numberOfLessons.setText(Integer.toString(mLessonPresenter.getAllLessons().size()));
        try {
            hoursDroveNight.setText(getString(R.string.profile_day_hours,
                    mLessonPresenter.getDayNightDroveLesson(true, mLessonPresenter.getAllLessons())));
            hoursDroveDay.setText(getString(R.string.profile_night_hours,
                    mLessonPresenter.getDayNightDroveLesson(false, mLessonPresenter.getAllLessons())));
        } catch (ParseException e) {
            Log.e("HomeFragment", "Formatting exception: " + e);
            Toast.makeText(getContext(), getString(R.string.generic_error, e), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks if edit text have data
     * @return true if user has added values
     */
    private boolean checkIfAllComplete(){
        Boolean editEmpty = true;

        if(TextUtils.isEmpty(mLpn.getText().toString())) {
            mLpn.setError(getString(R.string.error_lpn_null));
            editEmpty = false;
        }
        if(TextUtils.isEmpty(mStartOdometer.getText().toString())) {
            mStartOdometer.setError(getString(R.string.error_odometer_null));
            editEmpty = false;
        }
        if(TextUtils.isEmpty(mSupervisorLicence.getText().toString())) {
            mSupervisorLicence.setError(getString(R.string.error_supervisor_null));
            editEmpty = false;
        }
        return editEmpty;
    }


}
