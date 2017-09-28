package palarax.com.logbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.List;

import palarax.com.logbook.R;
import palarax.com.logbook.model.Lesson;
import palarax.com.logbook.model.Users;
import palarax.com.logbook.model.Utils;

/**
 * Profile fragment that allows user to update their information
 *
 * @author Ilya Thai (11972078 )
 * @version 1.0
 * @date 10-Sep-17
 */
public class ProfileFragment extends Fragment {

    private TextView mNameText, mLicenseText, mDobText, mStateText, mProgressText;

    private Users student = null;

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
        Button btnDeleteAll = view.findViewById(R.id.btn_delete_all);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Lesson.deleteAll(Lesson.class);
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
        } else {
            mProgressText.setText(getString(R.string.profile_completed));
            mProgressText.setTextColor(ContextCompat.getColor(getActivity(), R.color.licence_color));
        }
    }
}
