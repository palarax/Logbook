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

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;

import java.text.ParseException;

import dmax.dialog.SpotsDialog;
import palarax.com.logbook.R;
import palarax.com.logbook.Utils;
import palarax.com.logbook.fragment.HistoryFragment;
import palarax.com.logbook.fragment.HomeFragment;
import palarax.com.logbook.fragment.ProfileFragment;
import palarax.com.logbook.model.Lesson;
import palarax.com.logbook.presenter.BackendlessPresenter;
import palarax.com.logbook.presenter.UserPresenter;


/**
 * Initial activity that allows the user to get train timetable
 * @author Ilya Thai (11972078)
 * @version 1.0
 *
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BackendlessPresenter.HandleResponse {

    private static final String TAG = MainActivity.class.getSimpleName(); //used for debugging

    private BackendlessPresenter mBackendlessPresenter;
    private UserPresenter mUserPresenter;
    private HomeFragment mHomeFragment = new HomeFragment();
    private HistoryFragment mHistoryFragment = new HistoryFragment();
    private ProfileFragment mProfileFragment = new ProfileFragment();

    private AlertDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mBackendlessPresenter = new BackendlessPresenter(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mUserPresenter = new UserPresenter();

        final View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        populateNavHeader(headerView, Backendless.UserService.CurrentUser());

        //Open HomeFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, mHomeFragment).commit();

        try{
            testCreateData();
        }catch (ParseException e){
            Log.e(TAG,""+e);
        }

    }


    private void testCreateData() throws ParseException {

        String startT = "05/10/17 15:00:00";
        String endT = "05/10/17 18:00:00";
        Lesson lesson = new Lesson(
                "TEST1",
                10000, 11111,
                mUserPresenter.getStudent().getLicenceNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "10/10/17 17:00:00";
        endT = "10/10/17 22:00:00";
        lesson = new Lesson(
                "TEST2",
                12000, 11111,
                mUserPresenter.getStudent().getLicenceNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "15/10/17 04:00:00";
        endT = "15/10/17 08:00:00";
        lesson = new Lesson(
                "TEST3",
                13000, 11111,
                mUserPresenter.getStudent().getLicenceNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "01/11/17 15:00:00";
        endT = "01/11/17 18:00:00";
        lesson = new Lesson(
                "TEST4",
                10000, 11111,
                mUserPresenter.getStudent().getLicenceNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "10/11/17 17:00:00";
        endT = "10/11/17 22:00:00";
        lesson = new Lesson(
                "TEST5",
                12000, 11111,
                mUserPresenter.getStudent().getLicenceNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "15/11/17 04:00:00";
        endT = "15/11/17 08:00:00";
        lesson = new Lesson(
                "TEST6",
                13000, 11111,
                mUserPresenter.getStudent().getLicenceNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "07/12/17 04:00:00";
        endT = "07/12/17 14:00:00";
        lesson = new Lesson(
                "TEST7",
                13000, 11111,
                mUserPresenter.getStudent().getLicenceNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "11/12/17 13:00:00";
        endT = "11/12/17 16:00:00";
        lesson = new Lesson(
                "TEST8",
                10000, 11111,
                mUserPresenter.getStudent().getLicenceNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "14/12/17 23:00:00";
        endT = "15/12/17 02:30:00";
        lesson = new Lesson(
                "TEST9",
                12000, 11111,
                mUserPresenter.getStudent().getLicenceNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "23/12/17 05:00:00";
        endT = "23/12/17 10:00:00";
        lesson = new Lesson(
                "TEST10",
                13000, 11111,
                mUserPresenter.getStudent().getLicenceNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

        startT = "31/12/17 22:00:00";
        endT = "01/01/18 04:00:00";
        lesson = new Lesson(
                "TEST11",
                13000, 11111,
                mUserPresenter.getStudent().getLicenceNumber(), 101021,
                283111, Utils.getTimeDiffernce(startT, endT,"dd/MM/yy HH:mm:ss")
                , startT, endT);
        lesson.save();

    }

    /**
     * Populates nav header with user data
     *
     * @param headView header view object
     * @param user     Backendless user object
     */
    private void populateNavHeader(View headView, BackendlessUser user) {
        String nameAndSurname="";
        if (user != null) {
            try {
                Log.d(TAG,"SEARCHING FOR USER");
                nameAndSurname = mUserPresenter.getStudent().getUserName() + " "
                        + mUserPresenter.getStudent().getUserSurname();
            } catch (NullPointerException e) {
                Log.e(TAG, "User does not exist");
                nameAndSurname = getString(R.string.guest_user);
                Toast.makeText(this, getString(R.string.user_not_exist), Toast.LENGTH_LONG).show();
            }finally {
                ((TextView) headView.findViewById(R.id.nav_header_name)).setText(nameAndSurname);
            }
        } else {
            Toast.makeText(this, getString(R.string.backendless_user_null), Toast.LENGTH_LONG).show();
            finish();   //this should NEVER happen
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = mHomeFragment;
                break;
            case R.id.nav_history:
                fragment = mHistoryFragment;
                break;
            case R.id.nav_profile:
                fragment = mProfileFragment;
                break;
            case R.id.nav_logout:
                fragment = mHomeFragment; //make sure not to get null object error
                logout();
                break;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Logout from the app and destroy activity
     */
    private void logout() {
        mProgress = new SpotsDialog(this, R.style.Custom);
        mProgress.show();
        mBackendlessPresenter.logoutBackEndless(this);
    }

    @Override
    public void onHandleResponse(Object response) {
        mProgress.dismiss();
        finish();
        overridePendingTransition(R.transition.fadein, R.transition.fadeout);
    }

    @Override
    public void onHandleFault(BackendlessFault fault) {
        Log.e(TAG, "Backendless error: " + fault.getMessage());
        mProgress.dismiss();
        Toast.makeText(this, getString(R.string.error_logout), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            logout();
        }
    }

    /**
     * Called when activity becomes visible to the user
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    /**
     * Called when activity has been stopped and is restarting again
     */
    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    /**
     * Called when activity starts interacting with user
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /**
     * Called when current activity is being paused and the previous activity is being resumed
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    /**
     * Called when activity is no longer visible to user
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
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
