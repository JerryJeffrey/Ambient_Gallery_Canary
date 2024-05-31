package com.ambientgallery.viewables;

import static com.ambientgallery.utils.AnimateUtil.ANIM_TYPE_OPACITY;
import static com.ambientgallery.utils.AnimateUtil.ongoingAnimators;
import static com.ambientgallery.utils.AnimateUtil.viewOpacity;
import static com.ambientgallery.utils.AnimateUtil.viewPosition;
import static com.ambientgallery.utils.AnimateUtil.viewRotation;
import static com.ambientgallery.utils.DimensUtil.dp2px;
import static com.ambientgallery.utils.DimensUtil.px2dp;
import static com.ambientgallery.utils.SharedPrefsUtil.ANIMATION_DURATION_INSTANT;
import static com.ambientgallery.utils.SharedPrefsUtil.DRAG_END_SENSITIVITY;
import static com.ambientgallery.utils.SharedPrefsUtil.DRAG_START_SENSITIVITY;
import static com.ambientgallery.utils.SharedPrefsUtil.MAIN_PREFS;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;

public class SettingsSensitivityCardFragment extends Fragment {
    View rootView, refreshContainer, refreshIcon;
    ProgressBar startIndicatorProgress, endIndicatorProgress;
    SharedPreferences prefs;
    float touchStartY;
    boolean dragStarted = false, dragEnded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefs = requireActivity().getSharedPreferences(MAIN_PREFS, Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_settings_sensitivity_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View.OnTouchListener onTouchListener = (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //initialize touch start position
                    touchStartY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //if not in image switching process
                    if (!ongoingAnimators.containsKey(refreshContainer.getId() + ANIM_TYPE_OPACITY)) {
                        //get touch position
                        float currentY = event.getRawY();
                        float y = px2dp(requireContext(), currentY - touchStartY);
                        //update start position to minimum value of single touch
                        if (touchStartY > currentY) touchStartY = currentY;
                        //calculate drag percentages
                        float startPercent = y / prefsInt(prefs, DRAG_START_SENSITIVITY);
                        float endPercent = (y - prefsInt(prefs, DRAG_START_SENSITIVITY)) / prefsInt(prefs, DRAG_END_SENSITIVITY);
                        int startProgress = (int) (fixPercentRange(startPercent) * 100);
                        int endProgress = (int) (fixPercentRange(endPercent) * 100);
                        startIndicatorProgress.setProgress(startProgress);
                        endIndicatorProgress.setProgress(endProgress);

                        //actions when drag percentages changed
                        if (endPercent >= 1) {
                            dragEnded = true;
                            refreshIcon.setRotation(15 * (endPercent - 1));
                            refreshContainer.setAlpha(1f);
                            refreshIcon.setAlpha(1f);
                            refreshContainer.setTranslationY(dp2px(requireContext(), 6 * (endPercent - 1)));
                        } else if (startPercent >= 1) {
                            dragStarted = true;
                            dragEnded = false;
                            refreshIcon.setRotation(45 * (endPercent - 1));
                            refreshContainer.setAlpha(endPercent);
                            refreshIcon.setAlpha(0.5f);
                            refreshContainer.setTranslationY(dp2px(requireContext(), 12 * (endPercent - 1)));
                        } else {
                            refreshIcon.setRotation(0);
                            refreshContainer.setAlpha(0);
                            refreshContainer.setTranslationY(0);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (dragEnded || dragStarted) {
                        //play reset refresh button animation
                        viewRotation(refreshIcon, -45, 1, 1, prefsInt(prefs, ANIMATION_DURATION_INSTANT));
                        viewOpacity(refreshContainer, 0, 1, 1, prefsInt(prefs, ANIMATION_DURATION_INSTANT));
                        viewPosition(refreshContainer, 0, dp2px(requireContext(), -12), 1, 1, prefsInt(prefs, ANIMATION_DURATION_INSTANT));
                    } else {
                        v.performClick();
                    }
                    //reset touch event status
                    dragStarted = false;
                    dragEnded = false;
            }
            //whether the touch event is consumed
            return true;
        };
        rootView = requireActivity().findViewById(R.id.settings_sensitivity_card_root);
        refreshContainer = requireActivity().findViewById(R.id.settings_sensitivity_card_refresh_container);
        refreshIcon = requireActivity().findViewById(R.id.settings_sensitivity_card_refresh_icon);
        startIndicatorProgress = requireActivity().findViewById(R.id.settings_sensitivity_card_start_indicator_progress);
        endIndicatorProgress = requireActivity().findViewById(R.id.settings_sensitivity_card_end_indicator_progress);
        refreshContainer.setTranslationY(dp2px(requireContext(), -12));
        refreshContainer.setAlpha(0);
        //solve nested view flashing
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            refreshIcon.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        rootView.setOnTouchListener(onTouchListener);

    }

    private static float fixPercentRange(float percent) {
        return Math.max(Math.min(percent, 1f), 0f);
    }
}
