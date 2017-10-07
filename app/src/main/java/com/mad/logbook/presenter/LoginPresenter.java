package com.mad.logbook.presenter;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.mad.logbook.Utils;
import com.mad.logbook.interfaces.LoginContract;
import com.mad.logbook.model.NdefTag;

import java.util.ArrayList;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * Created by Ithai on 9/10/2017.
 */

public class LoginPresenter implements NfcAdapter.ReaderCallback, LoginContract.Presenter {

    private static final String TAG = "LoginActivity"; //used for debugging

    private final LoginContract.View mLoginView;

    /**
     * Backendless initializes WeakReference
     */
    public LoginPresenter(@NonNull LoginContract.View loginView) {

        mLoginView = checkNotNull(loginView, "loginView cannot be null");
        mLoginView.setPresenter(this);
    }

    /**
     * Checks if NFC is supported and is enabled
     *
     * @param adapter NFC adapter from activity
     * @return true if supported and on
     */
    public boolean nfcSupported(NfcAdapter adapter) {
        if (adapter == null) {
            // Stop here, we definitely need NFC
            return false;
        } else if (!adapter.isEnabled()) {
            return false;
        }
        return true;
    }

    /**
     * Initializes Backendless Service.
     */
    public void initialiseBackEndless(Activity activity) {
        Backendless.setUrl(Utils.BACKENDLESS_SERVER_URL);
        Backendless.initApp(activity,
                Utils.BACKENDLESS_APPLICATION_ID,
                Utils.BACKENDLESS_API_KEY);
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

                mLoginView.loginApplication();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                mLoginView.lockUser(fault);
            }
        });
    }


    private void onAccountReceived(Tag tag) throws Exception {
        ArrayList nfcMessage = hasMessage(tag);
        if (nfcMessage != null) {
            mLoginView.morphToProgress();
            loginBackEndless(Utils.bytesToHexString(tag.getId()),
                    (String) nfcMessage.get(0));
        } else {
            mLoginView.lockUserBadNfc();
        }
    }

    /**
     * Checks if NFC tag has a message
     *
     * @param tag nfc tag to be analysed
     * @return array of messages
     */
    private ArrayList hasMessage(Tag tag) {
        ArrayList msgRecords;
        String[] techList = tag.getTechList(); //list of all Tag techs
        //Look through tech
        for (String singleTech : techList) {
            //selected only Ndef and NdefFormatable techs
            if (singleTech.equals(Ndef.class.getName()) || singleTech.equals(NdefFormatable.class.getName())) {
                NdefTag ndef = new NdefTag();
                msgRecords = ndef.read(tag);
                //if it's NdefFormatable, then don't try to read the message (there is none)
                if (msgRecords != null) {
                    return msgRecords;
                }
            }
        }
        return null;
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.i(TAG, "New tag discovered");
        try {
            onAccountReceived(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
