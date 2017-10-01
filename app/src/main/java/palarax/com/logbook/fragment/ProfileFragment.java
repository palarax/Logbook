package palarax.com.logbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import palarax.com.logbook.R;
import palarax.com.logbook.presenter.LessonPresenter;
import palarax.com.logbook.presenter.UserPresenter;

/**
 * Profile fragment that allows user to update their information
 *
 * @author Ilya Thai (11972078 )
 * @version 1.0
 * @date 10-Sep-17
 */
public class ProfileFragment extends Fragment {

    private UserPresenter mUserPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        mUserPresenter = new UserPresenter();
        LessonPresenter mLessonPresenter = new LessonPresenter(getActivity());

        TextView mNameText = view.findViewById(R.id.txt_name);
        TextView mLicenseText = view.findViewById(R.id.txt_license);
        TextView mDobText = view.findViewById(R.id.txt_dob);
        TextView mStateText = view.findViewById(R.id.txt_state);
        TextView mProgressText = view.findViewById(R.id.txt_completed);
        Button btnDeleteAll = view.findViewById(R.id.btn_delete_all);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserPresenter.deleteAllUsers();
            }
        });
        mUserPresenter.populateUserData(mNameText, mLicenseText, mDobText,
                mStateText, mProgressText, getContext());
    }

}
