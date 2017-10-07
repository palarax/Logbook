package com.mad.logbook.presenter;

import android.app.Activity;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.lang.ref.WeakReference;

/**
 * Manages Backendless functionality
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 */
public class BackendlessPresenter {

    private static final String BACKENDLESS_APPLICATION_ID = "F1D55E01-2495-D20A-FF0E-58AE50CB9700";
    private static final String BACKENDLESS_API_KEY = "5DB4EEBE-A054-CE7D-FFCD-F6F74B4FC500";
    private static final String BACKENDLESS_SERVER_URL = "http://api.backendless.com";

    private WeakReference<BackendlessPresenter.HandleResponse> mHandleResponse;

    /**
     * Backendless initializes WeakReference
     *
     * @param callback Backendless callback response
     */
    public BackendlessPresenter(BackendlessPresenter.HandleResponse callback) {
        mHandleResponse = new WeakReference<>(callback);
    }

    /**
     * Initializes Backendless Service
     */
    public void initialiseBackEndless(Activity activity) {
        Backendless.setUrl(BACKENDLESS_SERVER_URL);
        Backendless.initApp(activity,
                BACKENDLESS_APPLICATION_ID,
                BACKENDLESS_API_KEY);
    }

    /**
     * Login into the app using the information from NFC
     *
     * @param username username of user (Using ID of NFC)
     * @param password password of user (Stored in NFC as message)
     */
    public void loginBackEndless(String username, String password) {
        Backendless.UserService.login(username, password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                mHandleResponse.get().onHandleResponse(backendlessUser);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                mHandleResponse.get().onHandleFault(fault);
            }
        });
    }

    /**
     * Logout from Backendless
     */
    public void logoutBackEndless(Activity activity) {
        initialiseBackEndless(activity);
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                mHandleResponse.get().onHandleResponse(response);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                mHandleResponse.get().onHandleFault(fault);
            }
        });
    }

    /**
     * Interface to manage backendless responses
     */
    public interface HandleResponse {
        void onHandleResponse(Object response);

        void onHandleFault(BackendlessFault fault);
    }
}
