package com.mad.logbook.interfaces;

import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;
import com.mad.logbook.BaseView;
import com.mad.logbook.model.Users;

/**
 * Created by Ithai on 9/10/2017.
 */

public interface ProfileContract {
    interface View extends BaseView<ProfileContract.Presenter> {
        void onUpdateSuccess();

        void onUpdateFail(BackendlessFault fault);
    }

    interface Presenter {
        void updateBackendlessUser(Users student, BackendlessUser user, String name, String surname, String dob, String state,
                                   String contactNumber);
    }
}
