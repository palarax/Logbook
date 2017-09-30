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
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.dd.morphingbutton.MorphingButton;
import com.dd.morphingbutton.impl.IndeterminateProgressButton;

import palarax.com.logbook.R;

/**
 * Manages Morphing button functionality
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 */
public class MorphBtnPresenter {

    private Context mContext;

    /**
     * Morphing button initializer
     */
    public MorphBtnPresenter(Context context) {
        this.mContext = context;
    }

    /**
     * Morph button to FAIL state
     *
     * @param btnMorph button to morph
     */
    public void morphToFailure(final IndeterminateProgressButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius((int) mContext.getResources().getDimension(R.dimen.morph_button))
                .width((int) mContext.getResources().getDimension(R.dimen.morph_button))
                .height((int) mContext.getResources().getDimension(R.dimen.morph_button))
                .color(ContextCompat.getColor(mContext, R.color.mb_red))
                .colorPressed(ContextCompat.getColor(mContext, R.color.mb_red_dark))
                .icon(R.drawable.ic_lock);
        btnMorph.morph(circle);
        btnMorph.blockTouch();
    }

    /**
     * Morph to PROGRESS state
     *
     * @param button button to morph
     */
    public void morphToProgress(@NonNull final IndeterminateProgressButton button) {
        int progressColor1 = ContextCompat.getColor(mContext, R.color.holo_blue_bright);
        int progressColor2 = ContextCompat.getColor(mContext, R.color.mb_green);
        int progressColor3 = ContextCompat.getColor(mContext, R.color.holo_orange_light);
        int progressColor4 = ContextCompat.getColor(mContext, R.color.holo_red_light);
        int color = ContextCompat.getColor(mContext, R.color.mb_green);
        int progressCornerRadius = 4;
        int width = 800;
        int height = 32;
        int duration = 500;

        button.blockTouch(); // prevent user from clicking while button is in progress
        button.morphToProgress(color, progressCornerRadius, width, height, duration, progressColor1, progressColor2,
                progressColor3, progressColor4);
    }

    /**
     * Morph button to SUCCESS state
     *
     * @param btnMorph button to morph
     */
    public void morphToSuccess(final IndeterminateProgressButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius((int) mContext.getResources().getDimension(R.dimen.morph_button))
                .width((int) mContext.getResources().getDimension(R.dimen.morph_button))
                .height((int) mContext.getResources().getDimension(R.dimen.morph_button))
                .color(ContextCompat.getColor(mContext, R.color.mb_green))
                .colorPressed(ContextCompat.getColor(mContext, R.color.mb_green_dark))
                .icon(R.drawable.ic_done);
        btnMorph.morph(circle);
        btnMorph.unblockTouch();
    }

}
