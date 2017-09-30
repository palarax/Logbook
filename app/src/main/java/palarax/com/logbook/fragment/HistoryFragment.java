package palarax.com.logbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import palarax.com.logbook.R;
import palarax.com.logbook.activity.MainActivity;
import palarax.com.logbook.presenter.LessonPresenter;
import palarax.com.logbook.presenter.LessonsAdapter;
import palarax.com.logbook.presenter.UserPresenter;

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

    private UserPresenter mUserPresenter;
    private LessonPresenter mLessonPresenter;

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
        mUserPresenter = new UserPresenter();
        mLessonPresenter = new LessonPresenter();

        mNameText = view.findViewById(R.id.txt_name);
        mLicenseText = view.findViewById(R.id.txt_license);
        mDobText = view.findViewById(R.id.txt_dob);
        mStateText = view.findViewById(R.id.txt_state);
        mProgressText = view.findViewById(R.id.txt_completed);

        mUserPresenter.populateUserData(mNameText,mLicenseText,mDobText,
                mStateText,mProgressText,getContext());

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mAdapter = new LessonsAdapter(getContext(), mLessonPresenter.getAllLessons());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mLessonPresenter.updateLessons(mAdapter);
    }

}
