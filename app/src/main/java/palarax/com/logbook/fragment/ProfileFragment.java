package palarax.com.logbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import palarax.com.logbook.R;
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
        mUserPresenter = new UserPresenter();

        final EditText nameEdit = (EditText) view.findViewById(R.id.edit_name);
        final EditText sureNameEdit = (EditText) view.findViewById(R.id.edit_surname);

        //setup and populate spinner
        final Spinner statesSpinner = (Spinner) view.findViewById(R.id.spinner_state);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.states, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        statesSpinner.setAdapter(adapter);

        final TextView mNameText = view.findViewById(R.id.txt_name);
        final TextView mLicenseText = view.findViewById(R.id.txt_license);
        final TextView mDobText = view.findViewById(R.id.txt_dob);
        final TextView mStateText = view.findViewById(R.id.txt_state);
        final TextView mProgressText = view.findViewById(R.id.txt_completed);
        final Button btnDeleteAll = view.findViewById(R.id.btn_submit);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserPresenter.deleteAllUsers();
            }
        });
        mUserPresenter.populateUserData(mNameText, mLicenseText, mDobText,
                mStateText, mProgressText, getContext());

        //TODO: update backendless data
    }

}
