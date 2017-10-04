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
package palarax.com.logbook.presenter;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import palarax.com.logbook.R;

/**
 * Manages NFC functionality and their callbacks
 * @author Ilya Thai (11972078)
 * @version 1.0
 *
 */
public class NfcManager implements NfcAdapter.ReaderCallback {

    private static final String TAG = NfcManager.class.getSimpleName();

    // Weak reference to prevent retain loop. mAccountCallback is responsible for exiting
    // foreground mode before it becomes invalid (e.g. during onPause() or onStop()).
    private WeakReference<AccountCallback> mAccountCallback;

    private Activity mActivity;

    /**
     * NFC initializes WeakReference
     * @param accountCallback callback function
     */
    public NfcManager(AccountCallback accountCallback, Activity activity) {
        mAccountCallback = new WeakReference<>(accountCallback);
        mActivity = activity;
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
            Toast.makeText(mActivity, mActivity.getString(R.string.error_noNfc), Toast.LENGTH_LONG).show();
            return false;
        } else if (!adapter.isEnabled()) {
            Toast.makeText(mActivity, mActivity.getString(R.string.error_nfcDisabled), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Callback when a new tag is discovered by the system.
     *
     * <p>Communication with the card should take place here.
     *
     * @param tag Discovered tag
     */
    @Override
    public void onTagDiscovered(Tag tag) {
        Log.i(TAG, "New tag discovered");
        try {
            mAccountCallback.get().onAccountReceived(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * AccountCallback interface function
     */
    public interface AccountCallback {
        void onAccountReceived(Tag tag) throws Exception;
    }

}
