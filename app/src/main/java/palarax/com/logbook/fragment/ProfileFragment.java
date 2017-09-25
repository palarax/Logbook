package palarax.com.logbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import palarax.com.logbook.R;
import palarax.com.logbook.model.Utils;

/**
 * Profile fragment that allows user to update their information
 *
 * @author Ilya Thai (11972078 )
 * @version 1.0
 * @date 10-Sep-17
 */
public class ProfileFragment extends Fragment {

    private TextView mNameText;
    private TextView mLicenseText;
    private TextView mDobText;
    private TextView mStateText;
    private TextView mProgressText;

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
        mNameText = view.findViewById(R.id.txt_name);
        mLicenseText = view.findViewById(R.id.txt_license);
        mDobText = view.findViewById(R.id.txt_dob);
        mStateText = view.findViewById(R.id.txt_state);
        mProgressText = view.findViewById(R.id.txt_completed);
        populateUserData();
    }

    /**
     * Populates view with user data
     */
    private void populateUserData() {
        //TODO: should be saved in the local DB
        BackendlessUser user = Backendless.UserService.CurrentUser();
        String nameAndSurname = user.getProperty(Utils.BACKENDLESS_NAME) + " " + user.getProperty(Utils.BACKENDLESS_SURNAME);
        mNameText.setText(nameAndSurname);
        mLicenseText.setText(String.format("%d", ((Integer) user.getProperty(Utils.BACKENDLESS_LICENSE))));
        mDobText.setText((String) user.getProperty(Utils.BACKENDLESS_DOB));
        mStateText.setText((String) user.getProperty(Utils.BACKENDLESS_STATE));
        mProgressText.setText(getString(R.string.profile_in_progress));
        mProgressText.setTextColor(ContextCompat.getColor(getActivity(), R.color.in_progress));
    }
}
