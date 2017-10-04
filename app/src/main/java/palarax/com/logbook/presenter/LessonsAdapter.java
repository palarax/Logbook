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
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import palarax.com.logbook.R;
import palarax.com.logbook.activity.LessonFullDetail;
import palarax.com.logbook.model.Lesson;
import palarax.com.logbook.Utils;

/**
 * Sets up and managesRecycler View with lesson objects
 *
 * @author Ilya Thai (11972078 )
 * @version 1.0
 * @date 29-Sep-17
 */
public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.ViewHolder> {
    private Activity mActivity;
    private List<Lesson> mLessonsList;

    /**
     * Initializes LessonsAdapter
     *
     * @param activity activity initializing this adapter
     * @param lessons lesson list that contains lesson data
     */
    public LessonsAdapter(Activity activity, List<Lesson> lessons) {
        this.mActivity = activity;
        this.mLessonsList = lessons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_front, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int lessonPosition = position;
        final Lesson lesson = mLessonsList.get(position);
        holder.mTxtViewLessonId.setText(mActivity.getString(R.string.title_lesson_id, position + 1));
        holder.mTxtViewLpn.setText(lesson.getLicencePlate());
        holder.mTxtViewDistance.setText(mActivity.getString(R.string.txt_distance, lesson.getDistance() / 1000));
        holder.mTxtViewTotalTime.setText(Utils.convertDateToFormat(lesson.getTotalTime(), 1));

        holder.mBtnAdvancedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, LessonFullDetail.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Utils.LESSON_ID, mLessonsList.get(lessonPosition).getId());
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.transition.slide_down, R.transition.slide_up);
                } else {
                    intent.putExtra(Utils.LESSON_ID, lesson.getId());
                    mActivity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLessonsList.size();
    }

    /**
     * Describess item view and metadata about its place within the RecyclerView
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTxtViewDistance, mTxtViewTotalTime, mTxtViewLpn,
                mTxtViewLessonId;
        Button mBtnAdvancedInfo;
        View mView;
        ViewHolder(View view) {
            super(view);
            mView = view;
            mTxtViewLessonId = view.findViewById(R.id.txt_lesson_id);
            mTxtViewLpn = view.findViewById(R.id.txt_lpn);
            mTxtViewDistance = view.findViewById(R.id.txt_distance);
            mTxtViewTotalTime = view.findViewById(R.id.txt_total_time);
            mBtnAdvancedInfo = view.findViewById(R.id.btn_advanced_info);
        }
    }
}
