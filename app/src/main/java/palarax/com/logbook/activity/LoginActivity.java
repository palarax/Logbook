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

package palarax.com.logbook.activity;


import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.dd.morphingbutton.MorphingButton;
import com.dd.morphingbutton.impl.IndeterminateProgressButton;

import java.util.ArrayList;

import palarax.com.logbook.R;
import palarax.com.logbook.model.NdefTag;
import palarax.com.logbook.model.Users;
import palarax.com.logbook.model.Utils;
import palarax.com.logbook.presenter.NFCManager;

/**
 * Initial activity that requires the user to login using NFC
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 */
public class LoginActivity extends Activity implements NFCManager.AccountCallback {

    private static final String TAG = LoginActivity.class.getSimpleName(); //used for debugging
    // Recommend NfcAdapter flags for reading from other Android devices. Indicates that this
    // activity is interested in NFC-A devices (including other Android devices), and that the
    // system should not check for the presence of NDEF-formatted data (e.g. Android Beam).
    //| NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B | NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V;
    private NFCManager mCardReader;
    private IndeterminateProgressButton mBtnMorph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialiseBackEndless();
        mBtnMorph = findViewById(R.id.btnMorph);
        morphToFailure(mBtnMorph);
        mBtnMorph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextActivity();
            }
        });
        mBtnMorph.blockTouch();

        //Sneaky way of initializing DB for the first time without effecting UI performance
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Users.findById(Users.class,(long) 1);
                initialize();
            }
        });
    }

    /**
     * Initialize NFC
     */
    private void initialize(){
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, getString(R.string.error_noNfc), Toast.LENGTH_LONG).show();
            finish();
        } else if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.error_nfcDisabled), Toast.LENGTH_LONG).show();
        }

        //register NFC callback
        mCardReader = new NFCManager(this);
        enableReaderMode();
    }

    /**
     * Receives data after nfc card has been found
     *
     * @param tag tag scanned
     */
    @Override
    public void onAccountReceived(Tag tag) throws Exception {
        Log.d(TAG, "onAccountRecieved");
        String[] techList = tag.getTechList(); //list of all Tag techs
        String ID = bytesToHexString(tag.getId());
        ArrayList msgRecords;
        //Look through tech
        for (String singleTech : techList) {
            //selected only Ndef and NdefFormatable techs
            if (singleTech.equals(Ndef.class.getName()) || singleTech.equals(NdefFormatable.class.getName())) {
                NdefTag ndef = new NdefTag();
                msgRecords = ndef.read(tag);
                //if it's NdefFormatable, then don't try to read the message (there is none)
                if (msgRecords != null) {
                    Log.e(TAG, "ID: " + ID + " pwd: " + msgRecords.get(0));
                    loginBackEndless(ID, (String) msgRecords.get(0));
                    return;
                }
            }
        }
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(LoginActivity.this, getString(R.string.error_badTag), Toast.LENGTH_LONG).show();
                morphToFailure(mBtnMorph);
            }
        });
    }

    /**
     * Login into the app using the information from NFC
     *
     * @param username username of user (Using ID of NFC)
     * @param password password of user (Stored in NFC as message)
     */
    private void loginBackEndless(String username, String password) {

        runOnUiThread(new Runnable() {
            public void run()
            {
                morphToProgress(mBtnMorph);
            }
        });

        Backendless.UserService.login(username, password, new AsyncCallback<BackendlessUser>() {

            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                morphToSuccess(mBtnMorph);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                morphToFailure(mBtnMorph);
                Toast.makeText(LoginActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Morph button to SUCCESS state
     * @param btnMorph button to morph
     */
    private void morphToSuccess(final IndeterminateProgressButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius( (int) getResources().getDimension(R.dimen.morph_button))
                .width((int) getResources().getDimension(R.dimen.morph_button))
                .height((int) getResources().getDimension(R.dimen.morph_button))
                .color(ContextCompat.getColor(this, R.color.mb_green))
                .colorPressed(ContextCompat.getColor(this, R.color.mb_green_dark))
                .icon(R.drawable.ic_done);
        btnMorph.morph(circle);
        mBtnMorph.unblockTouch();

    }

    /**
     * Morph button to FAIL state
     * @param btnMorph button to morph
     */
    private void morphToFailure(final IndeterminateProgressButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius( (int) getResources().getDimension(R.dimen.morph_button))
                .width((int) getResources().getDimension(R.dimen.morph_button))
                .height((int) getResources().getDimension(R.dimen.morph_button))
                .color(ContextCompat.getColor(this, R.color.mb_red))
                .colorPressed(ContextCompat.getColor(this, R.color.mb_red_dark))
                .icon(R.drawable.ic_lock);
        btnMorph.morph(circle);
        mBtnMorph.blockTouch();
    }

    /**
     * Morph to PROGRESS state
     * @param button button to morph
     */
    private void morphToProgress(@NonNull final IndeterminateProgressButton button) {
        int progressColor1 = ContextCompat.getColor(this, R.color.holo_blue_bright);
        int progressColor2 = ContextCompat.getColor(this, R.color.mb_green);
        int progressColor3 = ContextCompat.getColor(this, R.color.holo_orange_light);
        int progressColor4 = ContextCompat.getColor(this, R.color.holo_red_light);
        int color = ContextCompat.getColor(this, R.color.mb_green);
        int progressCornerRadius = 4;
        int width = 800;
        int height = 32;
        int duration = 500;

        button.blockTouch(); // prevent user from clicking while button is in progress
        button.morphToProgress(color, progressCornerRadius, width, height, duration, progressColor1, progressColor2,
                progressColor3, progressColor4);
    }

    /**
     * Converts bytes to hex string
     *
     * @param bytes bytes to be converted
     * @return a string of hex values
     */
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= bytes.length - 1; i++) {
            int b = bytes[i] & 0xff; //1111 1111
            if (b < 0x10)
                sb.append('0');

            sb.append(Integer.toHexString(b));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * Enables the devices to scan the tag
     */
    private void enableReaderMode() {
        Log.d(TAG, "Enabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            nfc.enableReaderMode(this, mCardReader, READER_FLAGS, null);
        }
    }

    /**
     * Initializes Backendless Service
     */
    private void initialiseBackEndless() {
        Backendless.setUrl(Utils.BACKENDLESS_SERVER_URL);
        Backendless.initApp(this,
                Utils.BACKENDLESS_APPLICATION_ID,
                Utils.BACKENDLESS_API_KEY);
    }

    /**
     * Disables the devices to scan the tag
     */
    private void disableReaderMode() {
        Log.d(TAG, "Disabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            nfc.disableReaderMode(this);
        }
    }


    /**
     * Start new Activity
     */
    private void nextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    /**
     * Called when activity becomes visible to the user
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        initialiseBackEndless();
        enableReaderMode();
    }

    /**
     * Called when activity has been stopped and is restarting again
     */
    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
        enableReaderMode();
    }

    /**
     * Called when activity starts interacting with user
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        enableReaderMode();
    }

    /**
     * Called when current activity is being paused and the previous activity is being resumed
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        disableReaderMode();
    }

    /**
     * Called when activity is no longer visible to user
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        disableReaderMode();
        morphToFailure(mBtnMorph);
    }

    /**
     * Called before the activity is destroyed by the system (either manually or by the system to conserve memory)
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

}
