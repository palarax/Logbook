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
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.List;

import dmax.dialog.SpotsDialog;
import palarax.com.logbook.R;
import palarax.com.logbook.db.DatabaseHelper;
import palarax.com.logbook.fragment.GoalsFragment;
import palarax.com.logbook.fragment.HistoryFragment;
import palarax.com.logbook.fragment.HomeFragment;
import palarax.com.logbook.fragment.ProfileFragment;
import palarax.com.logbook.model.Users;
import palarax.com.logbook.model.Utils;


/**
 * Initial activity that allows the user to get train timetable
 * @author Ilya Thai (11972078)
 * @version 1.0
 *
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName(); //used for debugging
    private AlertDialog mProgressDialog;


    private HomeFragment mHomeFragment = new HomeFragment();
    private HistoryFragment mHistoryFragment = new HistoryFragment();
    private GoalsFragment mGoalsFragment = new GoalsFragment();
    private ProfileFragment mProfileFragment = new ProfileFragment();

    private Users mStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Update local user details
        //TODO: don't add same student
        Users.deleteAll(Users.class);
        mStudent = DatabaseHelper.updateLocalUserDetails(Backendless.UserService.CurrentUser());

        List<Users> users = Users.findWithQuery(Users.class, "Select * from Users where dob <> ?", "-1");
        for (Users user : users) {
            Log.e(TAG, "NAME: " + user.getUserName());
        }


        final View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                intent.putExtra(Utils.BACKENDLESS_LICENSE, mStudent.getLicenseNumber());
                startActivity(intent);
            }
        });
        populateNavHeader(headerView, Backendless.UserService.CurrentUser());

        //Open HomeFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, mHomeFragment).commit();
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
                nameAndSurname = mStudent.getUserName() + " " + mStudent.getUserSurname();
                ((TextView) headView.findViewById(R.id.nav_header_name)).setText(nameAndSurname);

            } catch (NullPointerException e) {
                Log.e(TAG, "User does not exist");
                nameAndSurname = getString(R.string.guest_user);
                Toast.makeText(this, getString(R.string.user_not_exist), Toast.LENGTH_LONG).show();
            }finally {
                ((TextView) headView.findViewById(R.id.nav_header_name)).setText(nameAndSurname);
            }
        } else {
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
            case R.id.nav_goals:
                fragment = mGoalsFragment;
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
        mProgressDialog = new SpotsDialog(this, R.style.Custom);
        mProgressDialog.show();
        Backendless.initApp(this,
                Utils.BACKENDLESS_APPLICATION_ID,
                Utils.BACKENDLESS_API_KEY);
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                finish(); //done with this activity
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Logout error: " + fault.getCode());
                mProgressDialog.dismiss();
                Toast.makeText(getBaseContext(), getString(R.string.error_logout), Toast.LENGTH_LONG).show();
            }
        });
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
        mProgressDialog.dismiss();
    }
}
