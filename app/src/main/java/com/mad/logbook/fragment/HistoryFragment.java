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
package com.mad.logbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.logbook.R;
import com.mad.logbook.db.DatabaseHelper;
import com.mad.logbook.model.Lesson;
import com.mad.logbook.presenter.LessonsAdapter;

import java.util.List;

/**
 * History fragment that displays historic data and allows user to go to "history detail screen"
 *
 * @author Ilya Thai (11972078 )
 * @version 1.0
 * @date 10-Sep-17
 */
public class HistoryFragment extends Fragment {

    private List<Lesson> userLessons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(getString(R.string.fragment_title_history));
        return inflater.inflate(R.layout.history_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        userLessons = DatabaseHelper.getUserLessons();
        LessonsAdapter mAdapter = new LessonsAdapter(getActivity(), userLessons);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        updateLessons(mAdapter);
    }

    /**
     * Get data from db and load into GUI
     */
    public void updateLessons(LessonsAdapter adapter) {
        //clear them
        userLessons.clear();
        //add lessons
        List<Lesson> newLessons = DatabaseHelper.getUserLessons();
        for (Lesson lesson : newLessons) {
            userLessons.add(lesson);
        }
        adapter.notifyDataSetChanged();
    }

}
