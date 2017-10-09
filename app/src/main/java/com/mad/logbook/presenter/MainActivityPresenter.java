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
 * Listens to user actions from the UI, retrieves the data and updates the UI
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 * @date 09-Oct-17
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

    /**
     * Gets username to populate header
     *
     * @return username
     */
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
