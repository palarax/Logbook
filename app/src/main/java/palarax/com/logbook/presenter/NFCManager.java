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

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Manages NFC functionality and CallBacks
 * @author Ilya Thai (11972078)
 * @version 1.0
 *
 */
public class NFCManager implements NfcAdapter.ReaderCallback{

    private static final String TAG = NFCManager.class.getSimpleName();

    // Weak reference to prevent retain loop. mAccountCallback is responsible for exiting
    // foreground mode before it becomes invalid (e.g. during onPause() or onStop()).
    private WeakReference<AccountCallback> mAccountCallback;

    /**
     * NFC initializes WeakReference
     * @param accountCallback callback function
     */
    public NFCManager(AccountCallback accountCallback) {
        mAccountCallback = new WeakReference<>(accountCallback);
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
