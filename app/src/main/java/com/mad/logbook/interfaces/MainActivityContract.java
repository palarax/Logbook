package com.mad.logbook.interfaces;

import android.app.Activity;

import com.backendless.exceptions.BackendlessFault;
import com.mad.logbook.BaseView;

/**
 * Created by Ithai on 9/10/2017.
 */


public interface MainActivityContract {
    interface View extends BaseView<Presenter> {
        void logout();

        void showLogoutError(BackendlessFault fault);
    }

    interface Presenter {
        void logoutBackEndless(Activity activity);

        String getUserName();
    }
}