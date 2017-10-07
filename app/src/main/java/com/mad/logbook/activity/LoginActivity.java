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

package com.mad.logbook.activity;


import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.backendless.Backendless;
import com.backendless.exceptions.BackendlessFault;
import com.dd.morphingbutton.MorphingButton;
import com.dd.morphingbutton.impl.IndeterminateProgressButton;
import com.mad.logbook.R;
import com.mad.logbook.Utils;
import com.mad.logbook.db.DatabaseHelper;
import com.mad.logbook.interfaces.LoginContract;
import com.mad.logbook.presenter.LoginPresenter;

/**
 * Initial activity that requires the user to login using NFC
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 */
public class LoginActivity extends Activity implements LoginContract.View {

    private static final String TAG = LoginActivity.class.getSimpleName(); //used for debugging

    private IndeterminateProgressButton mBtnMorph;
    private LoginContract.Presenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginPresenter = new LoginPresenter(this);

        mBtnMorph = findViewById(R.id.btnMorph);
        morphToFailure();
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
            }
        });

        if (mLoginPresenter.nfcSupported(NfcAdapter.getDefaultAdapter(LoginActivity.this))) {
            //enableReader
            enableReaderMode();
        } else {
            Toast.makeText(this, getString(R.string.error_nfcDisabled), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void setPresenter(@NonNull LoginContract.Presenter presenter) {
        mLoginPresenter = presenter;
    }


    /**
     * Enables the devices to scan the tag
     */
    private void enableReaderMode() {
        Log.d(TAG, "Enabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            nfc.enableReaderMode(this, mLoginPresenter, Utils.NFC_READER_FLAGS, null);
        }
    }

    /**
     * Disables the devices to scan the tag
     */
    public void disableReaderMode() {
        Log.d(TAG, "Disabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            nfc.disableReaderMode(this);
        }
    }


    public void loginApplication() {
        morphToSuccess();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 200);
    }

    public void lockUser(BackendlessFault fault) {
        Toast.makeText(LoginActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
        morphToFailure();
    }

    public void lockUserBadNfc() {
        Toast.makeText(LoginActivity.this, getString(R.string.error_badTag), Toast.LENGTH_LONG).show();
        morphToFailure();
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
     * Morph button to FAIL state
     */
    public void morphToFailure() {
        runOnUiThread(new Runnable() {
            public void run() {
                MorphingButton.Params circle = MorphingButton.Params.create()
                        .duration(500)
                        .cornerRadius((int) getResources().getDimension(R.dimen.morph_button))
                        .width((int) getResources().getDimension(R.dimen.morph_button))
                        .height((int) getResources().getDimension(R.dimen.morph_button))
                        .color(ContextCompat.getColor(getBaseContext(), R.color.mb_red))
                        .colorPressed(ContextCompat.getColor(getBaseContext(), R.color.mb_red_dark))
                        .icon(R.drawable.ic_lock);
                mBtnMorph.morph(circle);
                mBtnMorph.blockTouch();
            }
        });
    }

    /**
     * Morph to PROGRESS state
     *
     */
    public void morphToProgress() {
        runOnUiThread(new Runnable() {
            public void run() {
                int progressColor1 = ContextCompat.getColor(getBaseContext(), R.color.mb_blue);
                int progressColor2 = ContextCompat.getColor(getBaseContext(), R.color.mb_green);
                int progressColor3 = ContextCompat.getColor(getBaseContext(), R.color.holo_orange_light);
                int progressColor4 = ContextCompat.getColor(getBaseContext(), R.color.holo_red_light);
                int color = ContextCompat.getColor(getBaseContext(), R.color.mb_green);
                int progressCornerRadius = 4;
                int width = 800;
                int height = 32;
                int duration = 500;

                mBtnMorph.blockTouch(); // prevent user from clicking while button is in progress
                mBtnMorph.morphToProgress(color, progressCornerRadius, width, height, duration, progressColor1, progressColor2,
                        progressColor3, progressColor4);
            }
        });

    }

    /**
     * Morph button to SUCCESS state
     */
    public void morphToSuccess() {
        runOnUiThread(new Runnable() {
            public void run() {
                MorphingButton.Params circle = MorphingButton.Params.create()
                        .duration(500)
                        .cornerRadius((int) getResources().getDimension(R.dimen.morph_button))
                        .width((int) getResources().getDimension(R.dimen.morph_button))
                        .height((int) getResources().getDimension(R.dimen.morph_button))
                        .color(ContextCompat.getColor(getBaseContext(), R.color.mb_green))
                        .colorPressed(ContextCompat.getColor(getBaseContext(), R.color.mb_green_dark))
                        .icon(R.drawable.ic_done);
                mBtnMorph.morph(circle);
                mBtnMorph.unblockTouch();
            }
        });

    }


    /**
     * Called when activity becomes visible to the user
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mLoginPresenter.initialiseBackEndless(this);
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
        morphToFailure();
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
