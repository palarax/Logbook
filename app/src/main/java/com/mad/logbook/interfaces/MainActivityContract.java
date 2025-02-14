/*
 * Copyright 2016, The Android Open Source Project
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
package com.mad.logbook.interfaces;

import android.app.Activity;

import com.backendless.exceptions.BackendlessFault;
import com.mad.logbook.BaseView;

/**
 * Interface between MainActivity presenter and view
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 * @date 09-Oct-17
 */


public interface MainActivityContract {
    interface View extends BaseView<Presenter> {
        void logout();

        void showLogoutError(BackendlessFault fault);
    }

    interface Presenter {
        void logoutBackEndless(Activity activity);

        String getUserName();
    }
}