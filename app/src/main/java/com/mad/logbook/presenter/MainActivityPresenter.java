package com.mad.logbook.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.mad.logbook.Utils;
import com.mad.logbook.db.DatabaseHelper;
import com.mad.logbook.interfaces.MainActivityContract;
import com.mad.logbook.model.Users;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * Created by Ithai on 9/10/2017.
 */

public class MainActivityPresenter implements MainActivityContract.Presenter {

    private static final String TAG = "MainActivity"; //used for debugging
    private final MainActivityContract.View mMainView;

    /**
     * Backendless initializer
     */
    public MainActivityPresenter(@NonNull MainActivityContract.View mainView) {
        mMainView = checkNotNull(mainView, "MainActivityView cannot be null");
        mMainView.setPresenter(this);
        //update user information from the cloud
        DatabaseHelper.updateLocalUserDetails(Backendless.UserService.CurrentUser());
    }

    /**
     * Logout from Backendless
     */
    public void logoutBackEndless(Activity activity) {
        Backendless.setUrl(Utils.BACKENDLESS_SERVER_URL);
        Backendless.initApp(activity,
                Utils.BACKENDLESS_APPLICATION_ID,
                Utils.BACKENDLESS_API_KEY);
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                mMainView.logout();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                mMainView.showLogoutError(fault);
            }
        });
    }

    public String getUserName() {
        if (Backendless.UserService.CurrentUser() != null) {
            try {
                Log.d(TAG, "SEARCHING FOR USER");
                Users currentUser = DatabaseHelper.getCurrentUser();
                return currentUser.getUserName() + " " + currentUser.getUserSurname();
            } catch (NullPointerException e) {
                Log.e(TAG, "User does not exist");
                return Utils.BACKENDLESS_ERROR_USER;
            }
        } else {
            return Utils.BACKENDLESS_ERROR_USER;
        }

    }
}
