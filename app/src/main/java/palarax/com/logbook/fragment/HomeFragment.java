package palarax.com.logbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.List;

import palarax.com.logbook.R;
import palarax.com.logbook.activity.DrivingLesson;
import palarax.com.logbook.model.Lesson;
import palarax.com.logbook.model.Users;
import palarax.com.logbook.model.Utils;

/**
 * Home fragment that initiates a lesson
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 * @date 09-Sep-17
 */

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName(); //used for debugging

    // distance the button travels down the screen vertically
    private static final int BTN_Y_DISTANCE = 400;
    private TextView mNameText, mLicenseText, mDobText, mStateText, mProgressText;

    private EditText mLpn, mStartOdometer, mSupervisorLicence;

    private Button mStart;
    private RelativeLayout.LayoutParams mLayoutParams;
    private boolean mBtnAnimated = false;

    private Users student = null;
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
        //TODO: pass data from login
        BackendlessUser user = Backendless.UserService.CurrentUser();
        int licence = ((Integer) user.getProperty(Utils.BACKENDLESS_LICENSE));
        List<Users> users = Users.findWithQuery(Users.class, "Select * from Users where license_number = ?", Integer.toString(licence));
        student = users.get(0);

        mNameText = view.findViewById(R.id.txt_name);
        mLicenseText = view.findViewById(R.id.txt_license);
        mDobText = view.findViewById(R.id.txt_dob);
        mStateText = view.findViewById(R.id.txt_state);
        mProgressText = view.findViewById(R.id.txt_completed);
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
                        Lesson lesson = new Lesson(
                                mLpn.getText().toString(),
                                0, Integer.parseInt(mSupervisorLicence.getText().toString()),
                                student, Integer.parseInt(mStartOdometer.getText().toString()),
                                0, 0, Utils.getTime(), null, 0);
                        lesson.save();
                        intent.putExtra(Utils.LESSON_ID, lesson.getId());
                        startActivity(intent);
                    }
                }
            }
        });
        populateUserData();
    }

    /**
     * Populates view with user data
     */
    private void populateUserData() {
        mNameText.setText(student.getUserName() + " " + student.getUserSurname());
        mLicenseText.setText(student.getLicenseNumber().toString());
        mDobText.setText(student.getDob());
        mStateText.setText(student.getState());
        if (student.getHoursCompleted() < 120) {
            mProgressText.setText(getString(R.string.profile_in_progress));
            mProgressText.setTextColor(ContextCompat.getColor(getActivity(), R.color.in_progress));
        }else{
            mProgressText.setText(getString(R.string.profile_completed));
            mProgressText.setTextColor(ContextCompat.getColor(getActivity(), R.color.licence_color));
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
