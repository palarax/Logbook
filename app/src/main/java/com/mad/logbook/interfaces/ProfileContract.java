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

import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;
import com.mad.logbook.BaseView;
import com.mad.logbook.model.Users;

/**
 * Interface between Profile presenter and profile fragment
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 * @date 09-Oct-17
 */

public interface ProfileContract {
    interface View extends BaseView<ProfileContract.Presenter> {
        void onUpdateSuccess();

        void onUpdateFail(BackendlessFault fault);
    }

    interface Presenter {
        void updateBackendlessUser(Users student, BackendlessUser user, String name, String surname, String dob, String state,
                                   String contactNumber);
    }
}
