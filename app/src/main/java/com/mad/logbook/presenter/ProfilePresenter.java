package com.mad.logbook.presenter;

import android.support.annotation.NonNull;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.mad.logbook.Utils;
import com.mad.logbook.interfaces.ProfileContract;
import com.mad.logbook.model.Users;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * Created by Ithai on 9/10/2017.
 */

public class ProfilePresenter implements ProfileContract.Presenter {

    private ProfileContract.View mProfileView;

    /**
     * Backendless initializes WeakReference
     */
    public ProfilePresenter(@NonNull ProfileContract.View view) {

        mProfileView = checkNotNull(view, "loginView cannot be null");
        mProfileView.setPresenter(this);
    }

    /**
     * Update backendless user in the cloud
     *
     * @param user          backendless user object
     * @param name          new name of the user
     * @param surname       new surname of the user
     * @param dob           new date of birth of the user
     * @param state         new state of the user
     * @param contactNumber new contact number of the user
     */
    public void updateBackendlessUser(Users student, BackendlessUser user, String name, String surname, String dob, String state,
                                      String contactNumber) {

        if (name.isEmpty()) {
            name = student.getUserName();
        }
        if (surname.isEmpty()) {
            surname = student.getUserSurname();
        }
        if (dob.isEmpty()) {
            dob = student.getDob();
        }
        if (state.isEmpty()) {
            state = student.getState();
        }
        if (contactNumber.isEmpty()) {
            contactNumber = null;
        }

        user.setProperty(Utils.BACKENDLESS_NAME, name);
        user.setProperty(Utils.BACKENDLESS_SURNAME, surname);
        user.setProperty(Utils.BACKENDLESS_DOB, dob);
        user.setProperty(Utils.BACKENDLESS_LICENSE, student.getLicenceNumber());
        user.setProperty(Utils.BACKENDLESS_STATE, state);
        user.setProperty(Utils.BACKENDLESS_CONTACT_NUMBER, contactNumber);

        Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
            public void handleResponse(BackendlessUser user) {
                mProfileView.onUpdateSuccess();
            }

            public void handleFault(BackendlessFault fault) {
                mProfileView.onUpdateFail(fault);
            }
        });
    }
}
