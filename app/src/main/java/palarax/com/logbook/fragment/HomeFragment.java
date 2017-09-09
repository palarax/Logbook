package palarax.com.logbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import palarax.com.logbook.R;
import palarax.com.logbook.model.Utils;

/**
 * Home fragment that initiates a lesson
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 * @date 09-Sep-17
 */

public class HomeFragment extends Fragment {

    private TextView nameText;
    private TextView licenseText;
    private TextView dobText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameText = view.findViewById(R.id.txt_name);
        licenseText = view.findViewById(R.id.txt_license);
        dobText = view.findViewById(R.id.txt_dob);
        populateUserData();
    }

    /**
     * Populates view with user data
     */
    private void populateUserData() {
        //TODO: should be saved in the local DB
        BackendlessUser user = Backendless.UserService.CurrentUser();
        String nameAndSurname = user.getProperty(Utils.BACKENDLESS_NAME) + " " + user.getProperty(Utils.BACKENDLESS_SURNAME);
        nameText.setText(nameAndSurname);
        licenseText.setText(String.format("%d", ((Integer) user.getProperty(Utils.BACKENDLESS_LICENSE))));
        dobText.setText((String) user.getProperty(Utils.BACKENDLESS_DOB));
    }


}
