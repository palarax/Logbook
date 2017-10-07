package com.mad.logbook.fragment;

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

import com.backendless.Backendless;
import com.mad.logbook.R;
import com.mad.logbook.presenter.UserPresenter;

/**
 * Profile fragment that allows user to update their information
 *
 * @author Ilya Thai (11972078 )
 * @version 1.0
 * @date 10-Sep-17
 */
public class ProfileFragment extends Fragment {

    private UserPresenter mUserPresenter;

    private EditText nameEdit, surnameEdit, contactEdit, editDob;
    private Spinner statesSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(getString(R.string.fragment_title_profile));
        return inflater.inflate(R.layout.profile_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserPresenter = new UserPresenter();
        nameEdit = view.findViewById(R.id.edit_name);
        surnameEdit = view.findViewById(R.id.edit_surname);
        contactEdit = view.findViewById(R.id.edit_contact_number);
        editDob = view.findViewById(R.id.edit_dob);

        //setup and populate spinner
        statesSpinner = view.findViewById(R.id.spinner_state);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.states, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        statesSpinner.setAdapter(adapter);

        final TextView mNameText = view.findViewById(R.id.txt_name);
        final TextView mLicenseText = view.findViewById(R.id.txt_license);
        final TextView mDobText = view.findViewById(R.id.txt_dob);
        final TextView mStateText = view.findViewById(R.id.txt_state);
        final TextView mProgressText = view.findViewById(R.id.txt_completed);
        final Button btnSubmit = view.findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserPresenter.updateBackendlessUser(getActivity(), Backendless.UserService.CurrentUser(), nameEdit.getText().toString(),
                        surnameEdit.getText().toString(), editDob.getText().toString(),
                        statesSpinner.getSelectedItem().toString(), contactEdit.getText().toString());
            }
        });
        mUserPresenter.populateUserData(mNameText, mLicenseText, mDobText,
                mStateText, mProgressText, getContext());
    }

}
