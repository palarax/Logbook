/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mad.logbook.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.exceptions.BackendlessFault;
import com.mad.logbook.R;
import com.mad.logbook.Utils;
import com.mad.logbook.db.DatabaseHelper;
import com.mad.logbook.interfaces.ProfileContract;
import com.mad.logbook.model.Users;
import com.mad.logbook.presenter.ProfilePresenter;

import java.util.Calendar;
import java.util.Locale;

import dmax.dialog.SpotsDialog;


/**
 * Profile fragment that allows user to update their information
 *
 * @author Ilya Thai (11972078 )
 * @version 1.0
 * @date 10-Sep-17
 */
public class ProfileFragment extends Fragment implements ProfileContract.View {

    TextView mNameText, mLicenseText, mDobText, mStateText, mProgressText;
    /**
     * Date picker listener. Fills edit box if the date is before today
     */
    DatePickerDialog.OnDateSetListener OnDate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            if (Utils.isDobCorrect(String.valueOf(dayOfMonth) + "/" +
                    String.valueOf(monthOfYear + 1) + "/" + String.valueOf(year))) {
                mEditDob.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1)
                        + "/" + String.valueOf(year));
            } else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.error_incorrect_date_input),
                        Toast.LENGTH_LONG).show();
            }
        }
    };
    private ProfileContract.Presenter mProfilePresenter;
    private AlertDialog mProgress;
    private EditText nameEdit, mSurnameEdit, mContactEdit, mEditDob;
    private Spinner mStatesSpinner;

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

        mProfilePresenter = new ProfilePresenter(this);

        nameEdit = view.findViewById(R.id.edit_name);
        mSurnameEdit = view.findViewById(R.id.edit_surname);
        mContactEdit = view.findViewById(R.id.edit_contact_number);
        mEditDob = view.findViewById(R.id.edit_dob);

        //setup and populate spinner
        mStatesSpinner = view.findViewById(R.id.spinner_state);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.states, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mStatesSpinner.setAdapter(adapter);

        mNameText = view.findViewById(R.id.txt_name);
        mLicenseText = view.findViewById(R.id.txt_license);
        mDobText = view.findViewById(R.id.txt_dob);
        mStateText = view.findViewById(R.id.txt_state);
        mProgressText = view.findViewById(R.id.txt_completed);
        final Button btnSubmit = view.findViewById(R.id.btn_submit);

        mEditDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress = new SpotsDialog(getActivity(), R.style.CustomUpdateUserDialog);
                mProgress.show();
                mProfilePresenter.updateBackendlessUser(DatabaseHelper.getCurrentUser(), Backendless.UserService.CurrentUser(), nameEdit.getText().toString(),
                        mSurnameEdit.getText().toString(), mEditDob.getText().toString(),
                        mStatesSpinner.getSelectedItem().toString(), mContactEdit.getText().toString());
            }
        });
        populateUserData();

    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        date.setCallBack(OnDate);
        date.show(getFragmentManager(), "Date Picker");
    }

    /**
     * Populates view with user data
     */
    public void populateUserData() {
        Users student = DatabaseHelper.getCurrentUser();
        mNameText.setText(student.getUserName()
                + " " + student.getUserSurname());

        mLicenseText.setText(String.format(Locale.getDefault(), "%d", student.getLicenceNumber()));
        mDobText.setText(student.getDob());
        mStateText.setText(student.getState());

        if ((student.getHoursCompleted() / 1000 / 60 / 60) >= 120) {
            mProgressText.setText(this.getString(R.string.profile_completed));
            mProgressText.setTextColor(getResources().getColor(R.color.licence_color));
        } else {
            mProgressText.setText(this.getString(R.string.profile_in_progress));
            mProgressText.setTextColor(getResources().getColor(R.color.in_progress));
        }
    }

    public void onUpdateSuccess() {
        mProgress.dismiss();
        Toast.makeText(getActivity(), getActivity().getString(R.string.user_updated),
                Toast.LENGTH_LONG).show();
    }

    public void onUpdateFail(BackendlessFault fault) {
        mProgress.dismiss();
        Log.e("UserPresenter", "Error: " + fault.getMessage());
        Toast.makeText(getActivity(), getActivity().getString(R.string.error_not_updated),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void setPresenter(ProfileContract.Presenter presenter) {
        mProfilePresenter = presenter;
    }
}
