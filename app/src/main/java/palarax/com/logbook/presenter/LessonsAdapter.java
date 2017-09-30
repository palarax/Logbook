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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import palarax.com.logbook.R;
import palarax.com.logbook.model.Lesson;
import palarax.com.logbook.model.Utils;

/**
 * Sets up and managesRecycler View with lesson objects
 *
 * @author Ilya Thai (11972078 )
 * @version 1.0
 * @date 29-Sep-17
 */
public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.ViewHolder> {
    private Context mAdapterContext;
    private List<Lesson> mLessonsList;

    /**
     * Initializes LessonsAdapter
     *
     * @param context context of the activity
     * @param lessons lesson list that contains lesson data
     */
    public LessonsAdapter(Context context, List<Lesson> lessons) {
        this.mAdapterContext = context;
        this.mLessonsList = lessons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Lesson lesson = mLessonsList.get(position);

        holder.mTxtViewLpn.setText("LPN: " + lesson.getLicencePlate());
        holder.mTxtViewDistance.setText("Distance: " + mAdapterContext.getString(R.string.txt_distance, lesson.getDistance()));
        holder.mTxtViewSupervisorLicence.setText("SupervisorLicence: " + Long.toString(lesson.getSupervisorLicence()));
        holder.mTxtViewStartOdometer.setText("Start Od: " + Long.toString(lesson.getStartOdometer()));
        holder.mTxtViewEndOdometer.setText("End od: " + Long.toString(lesson.getEndOdometer()));
        holder.mTxtViewTotalTime.setText("Total t: " + Utils.convertDateToHhMmSs(lesson.getTotalTime()));
        holder.mTxtViewStartTime.setText("Start t: " + lesson.getStartTime());
        holder.mTxtViewEndTime.setText("End t: " + lesson.getEndTime());
        holder.mTxtViewSpeed.setText("Speed: " + mAdapterContext.getString(R.string.txt_speed, lesson.getSpeed()));

    }

    @Override
    public int getItemCount() {
        return mLessonsList.size();
    }

    /**
     * Describess item view and metadata about its place within the RecyclerView
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTxtViewDistance, mTxtViewSupervisorLicence, mTxtViewStartOdometer, mTxtViewEndOdometer,
                mTxtViewTotalTime, mTxtViewStartTime, mTxtViewEndTime, mTxtViewSpeed, mTxtViewLpn;

        ViewHolder(View view) {
            super(view);
            mTxtViewLpn = view.findViewById(R.id.txt_lpn);
            mTxtViewDistance = view.findViewById(R.id.txt_distance);
            mTxtViewSupervisorLicence = view.findViewById(R.id.txt_supervisorLicence);
            mTxtViewStartOdometer = view.findViewById(R.id.txt_start_odometer);
            mTxtViewEndOdometer = view.findViewById(R.id.txt_end_odometer);
            mTxtViewTotalTime = view.findViewById(R.id.txt_total_time);
            mTxtViewStartTime = view.findViewById(R.id.txt_start_time);
            mTxtViewEndTime = view.findViewById(R.id.txt_end_time);
            mTxtViewSpeed = view.findViewById(R.id.txt_speed);
        }
    }
}
