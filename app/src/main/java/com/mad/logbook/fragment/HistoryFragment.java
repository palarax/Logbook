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
import com.mad.logbook.presenter.LessonPresenter;
import com.mad.logbook.presenter.LessonsAdapter;
import com.mad.logbook.presenter.UserPresenter;

/**
 * History fragment that displays historic data and allows user to go to "history detail screen"
 *
 * @author Ilya Thai (11972078 )
 * @version 1.0
 * @date 10-Sep-17
 */
public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.history_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserPresenter mUserPresenter = new UserPresenter();
        LessonPresenter mLessonPresenter = new LessonPresenter(getActivity());

        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        LessonsAdapter mAdapter = new LessonsAdapter(getActivity(), mLessonPresenter.getAllLessons());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mLessonPresenter.updateLessons(mAdapter, mUserPresenter.getStudent().getLicenceNumber());
    }

}
