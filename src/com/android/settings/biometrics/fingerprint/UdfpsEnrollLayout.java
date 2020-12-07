/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.settings.biometrics.fingerprint;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.android.settings.R;

public class UdfpsEnrollLayout extends LinearLayout {

    private static final String TAG = "UdfpsEnrollLayout";

    private final FingerprintSensorPropertiesInternal mSensorProps;

    public UdfpsEnrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSensorProps = context.getSystemService(FingerprintManager.class)
                .getSensorPropertiesInternal().get(0);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        final View animation = findViewById(R.id.fingerprint_progress_bar);
        final WindowManager wm = getContext().getSystemService(WindowManager.class);
        final int statusbarHeight = Math.abs(wm.getCurrentWindowMetrics().getWindowInsets()
                .getInsets(WindowInsets.Type.statusBars()).toRect().height());

        // Calculate the amount of translation required. This is just re-arranged from
        // animation.setY(mSensorProps.sensorLocationY-statusbarHeight-mSensorProps.sensorRadius)
        // The translationY is the amount of extra height that should be added to the spacer
        // above the animation
        final int spaceHeight = mSensorProps.sensorLocationY - statusbarHeight
                - mSensorProps.sensorRadius - animation.getTop();
         animation.setTranslationY(spaceHeight);
    }



    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final View animation = findViewById(R.id.fingerprint_progress_bar);
        final int sensorDiameter = mSensorProps.sensorRadius * 2;
        // Multiply it slightly so that the progress bar is outside the UDFPS affordance, and that
        // the animation is within the UDFPS affordance.
        final int animationDiameter = (int) (sensorDiameter * 1);
        animation.measure(MeasureSpec.makeMeasureSpec(animationDiameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(animationDiameter, MeasureSpec.EXACTLY));
    }
}