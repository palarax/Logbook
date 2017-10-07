package com.mad.logbook.interfaces;

import android.app.Activity;
import android.nfc.NfcAdapter;

import com.backendless.exceptions.BackendlessFault;
import com.mad.logbook.BaseView;

/**
 * Created by Ithai on 9/10/2017.
 */

public interface LoginContract {
    interface View extends BaseView<Presenter> {
        void loginApplication();

        void lockUser(BackendlessFault fault);

        void lockUserBadNfc();

        void morphToProgress();

        void morphToFailure();
    }

    interface Presenter extends NfcAdapter.ReaderCallback {
        void initialiseBackEndless(Activity activity);

        void loginBackEndless(String username, String password);

        boolean nfcSupported(NfcAdapter adapter);

    }
}
