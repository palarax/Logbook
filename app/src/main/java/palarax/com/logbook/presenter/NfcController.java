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
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;

import java.util.ArrayList;

import palarax.com.logbook.model.NdefTag;


/**
 * Controls NFC functionality
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 */
public class NfcController {

    private static final String TAG = "NFcController";
    // Recommend NfcAdapter flags for reading from other Android devices. Indicates that this
    // activity is interested in NFC-A devices (including other Android devices), and that the
    // system should not check for the presence of NDEF-formatted data (e.g. Android Beam).
    //| NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B | NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V;

    private Activity mActivity;

    /**
     * Nfc Controller's constructor
     *
     * @param activity activity initiating the controller
     */
    public NfcController(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * Enables the devices to scan the tag
     */
    public void enableReaderMode(NfcManager cardReader) {
        Log.d(TAG, "Enabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(mActivity);
        if (nfc != null) {
            nfc.enableReaderMode(mActivity, cardReader, READER_FLAGS, null);
        }
    }

    /**
     * Disables the devices to scan the tag
     */
    public void disableReaderMode() {
        Log.d(TAG, "Disabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(mActivity);
        if (nfc != null) {
            nfc.disableReaderMode(mActivity);
        }
    }

    /**
     * Checks if NFC tag has a message
     *
     * @param tag nfc tag to be analysed
     * @return array of messages
     */
    public ArrayList hasMessage(Tag tag) {
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
}
