package palarax.com.logbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.ArrayList;
import java.util.List;

import palarax.com.logbook.R;
import palarax.com.logbook.activity.MainActivity;
import palarax.com.logbook.model.Lesson;
import palarax.com.logbook.model.Users;
import palarax.com.logbook.model.Utils;
import palarax.com.logbook.presenter.LessonsAdapter;

/**
 * History fragment that displays historic data and allows user to go to "history detail screen"
 *
 * @author Ilya Thai (11972078 )
 * @version 1.0
 * @date 10-Sep-17
 */
public class HistoryFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName(); //used for debugging

    private RecyclerView mRecyclerView;
    private LessonsAdapter mAdapter;
    private List<Lesson> mLessonList;

    private Users student = null;

    private TextView mNameText, mLicenseText, mDobText, mStateText, mProgressText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.history_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        populateUserData();

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLessonList = new ArrayList<>();
        mAdapter = new LessonsAdapter(getContext(), mLessonList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        prepareLessons();
    }

    //TODO: ask why i need to re-add them

    /**
     * Get data from db and load into GUI
     */
    private void prepareLessons() {
        //clear them
        mLessonList.clear();
        //add lessons
        List<Lesson> lessons = Lesson.listAll(Lesson.class);
        for (Lesson lesson : lessons) {
            mLessonList.add(lesson);
        }
        mAdapter.notifyDataSetChanged();
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
        } else {
            mProgressText.setText(getString(R.string.profile_completed));
            mProgressText.setTextColor(ContextCompat.getColor(getActivity(), R.color.licence_color));
        }
    }
}
