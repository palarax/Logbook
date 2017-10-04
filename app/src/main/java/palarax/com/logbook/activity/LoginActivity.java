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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.backendless.Backendless;
import com.backendless.exceptions.BackendlessFault;
import com.dd.morphingbutton.impl.IndeterminateProgressButton;

import java.util.ArrayList;

import palarax.com.logbook.R;
import palarax.com.logbook.db.DatabaseHelper;
import palarax.com.logbook.Utils;
import palarax.com.logbook.presenter.BackendlessPresenter;
import palarax.com.logbook.presenter.MorphBtnPresenter;
import palarax.com.logbook.presenter.NfcController;
import palarax.com.logbook.presenter.NfcManager;

/**
 * Initial activity that requires the user to login using NFC
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 */
public class LoginActivity extends Activity implements NfcManager.AccountCallback, BackendlessPresenter.HandleResponse {

    private static final String TAG = LoginActivity.class.getSimpleName(); //used for debugging

    private NfcManager mCardReader;
    private NfcController mNfcController;
    private IndeterminateProgressButton mBtnMorph;

    private MorphBtnPresenter mMorphPresenter;
    private BackendlessPresenter mBackendlessPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mCardReader = new NfcManager(this, this);
        mNfcController = new NfcController(this);
        mBackendlessPresenter = new BackendlessPresenter(this);
        mMorphPresenter = new MorphBtnPresenter(getBaseContext());

        mBackendlessPresenter.initialiseBackEndless(this);
        mBtnMorph = findViewById(R.id.btnMorph);
        mMorphPresenter.morphToFailure(mBtnMorph);
        mBtnMorph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextActivity();
            }
        });
        mBtnMorph.blockTouch();

        //Initialize DB and NFC
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ActiveAndroid.initialize(LoginActivity.this);
                DatabaseHelper.clearActiveUsers();
                if (mCardReader.nfcSupported(NfcAdapter.getDefaultAdapter(LoginActivity.this))) {
                    mNfcController.enableReaderMode(mCardReader);
                } else {
                    finish();
                }

            }
        });
    }

    /**
     * Receives data after nfc card has been found
     * @param tag tag scanned
     */
    @Override
    public void onAccountReceived(Tag tag) throws Exception {
        Log.d(TAG, "onAccountRecieved");
        ArrayList nfcMessage = mNfcController.hasMessage(tag);
        if (nfcMessage != null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mMorphPresenter.morphToProgress(mBtnMorph);
                }
            });
            mBackendlessPresenter.loginBackEndless(Utils.bytesToHexString(tag.getId()),
                    (String) nfcMessage.get(0));
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_badTag), Toast.LENGTH_LONG).show();
                    mMorphPresenter.morphToFailure(mBtnMorph);
                }
            });
        }
    }


    //TODO: pause, then auto login
    @Override
    public void onHandleResponse(Object response) {
        mMorphPresenter.morphToSuccess(mBtnMorph);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 500);

    }

    @Override
    public void onHandleFault(BackendlessFault fault) {
        mMorphPresenter.morphToFailure(mBtnMorph);
        Toast.makeText(LoginActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();

    }

    /**
     * Start new Activity
     */
    private void nextActivity() {
        //Update local user details
        DatabaseHelper.clearActiveUsers();
        DatabaseHelper.updateLocalUserDetails(Backendless.UserService.CurrentUser());
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.transition.fadein, R.transition.fadeout);
    }


    /**
     * Called when activity becomes visible to the user
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mBackendlessPresenter.initialiseBackEndless(this);
        mNfcController.enableReaderMode(mCardReader);
    }

    /**
     * Called when activity has been stopped and is restarting again
     */
    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
        mNfcController.enableReaderMode(mCardReader);
    }

    /**
     * Called when activity starts interacting with user
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mNfcController.enableReaderMode(mCardReader);
    }

    /**
     * Called when current activity is being paused and the previous activity is being resumed
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mNfcController.disableReaderMode();
    }

    /**
     * Called when activity is no longer visible to user
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        mNfcController.disableReaderMode();
        mMorphPresenter.morphToFailure(mBtnMorph);
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
