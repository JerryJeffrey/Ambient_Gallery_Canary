/**
 * This class offers:
 * Methods to apply view animations.
 * Ability to detect instanced Animators and take actions to implement animations that can be interrupted.
 */
package com.ambientgallery.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import com.ambientgallery.components.BezierInterpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnimateUtil {
    public static final String ANIM_TYPE_OPACITY = "opacity";
    public static final String ANIM_TYPE_ROTATION = "rotation";
    public static final String ANIM_TYPE_POSITION = "position";
    public static final String ANIM_TYPE_SCALE = "scale";
    public static final String ANIM_TYPE_COLOR = "color";
    public static final String ANIM_TYPE_PERCENTAGE = "percentage";

    public static final HashMap<String, Animator> ongoingAnimators = new HashMap<>();

    public static void viewOpacity(View view, float alpha, float c1, float c2, int duration,
                                   AnimatorListenerAdapter... listener) {
        clearPreviousAnimator(view, ANIM_TYPE_OPACITY);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", alpha)
                .setDuration(duration);
        animator.setInterpolator(new BezierInterpolator(c1, c2));
        addDefaultListener(animator, view, ANIM_TYPE_OPACITY);
        addCustomListener(animator, listener);
        animator.start();
    }

    public static void viewOpacity(View[] views, float alpha, float c1, float c2, int duration,
                                   AnimatorListenerAdapter... listener) {
        List<Animator> sets = new ArrayList<>();
        for (View view : views) clearPreviousAnimator(view, ANIM_TYPE_OPACITY);
        for (View view : views) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", alpha)
                    .setDuration(duration);
            sets.add(animator);
        }
        AnimatorSet sequence = new AnimatorSet();
        sequence.playTogether(sets);
        sequence.setInterpolator(new BezierInterpolator(c1, c2));
        addCustomListener(sequence, listener);
        sequence.start();

    }

    public static void viewRotation(View view, float deg, float c1, float c2, int duration,
                                    AnimatorListenerAdapter... listener) {
        clearPreviousAnimator(view, ANIM_TYPE_ROTATION);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", deg)
                .setDuration(duration);
        animator.setInterpolator(new BezierInterpolator(c1, c2));
        addDefaultListener(animator, view, ANIM_TYPE_ROTATION);
        addCustomListener(animator, listener);
        animator.start();

    }

    public static void viewRotation(View[] views, float deg, float c1, float c2, int duration,
                                    AnimatorListenerAdapter... listener) {
        List<Animator> sets = new ArrayList<>();
        for (View view : views) clearPreviousAnimator(view, ANIM_TYPE_ROTATION);
        for (View view : views) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", deg)
                    .setDuration(duration);
            sets.add(animator);
        }
        AnimatorSet sequence = new AnimatorSet();
        sequence.playTogether(sets);
        sequence.setInterpolator(new BezierInterpolator(c1, c2));
        addCustomListener(sequence, listener);
        sequence.start();
    }

    public static void viewPosition(View view, float x, float y, float c1, float c2, int duration,
                                    AnimatorListenerAdapter... listener) {
        clearPreviousAnimator(view, ANIM_TYPE_POSITION);
        ObjectAnimator mainX = ObjectAnimator.ofFloat(view, "translationX", x)
                .setDuration(duration);
        ObjectAnimator mainY = ObjectAnimator.ofFloat(view, "translationY", y)
                .setDuration(duration);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(mainX, mainY);
        set.setInterpolator(new BezierInterpolator(c1, c2));
        addDefaultListener(set, view, ANIM_TYPE_POSITION);
        addCustomListener(set, listener);
        set.start();
    }

    public static void viewPosition(View[] views, float x, float y, float c1, float c2, int duration,
                                    AnimatorListenerAdapter... listener) {
        List<Animator> sets = new ArrayList<>();
        for (View view : views) clearPreviousAnimator(view, ANIM_TYPE_POSITION);
        for (View view : views) {
            ObjectAnimator mainX = ObjectAnimator.ofFloat(view, "translationX", x)
                    .setDuration(duration);
            ObjectAnimator mainY = ObjectAnimator.ofFloat(view, "translationY", y)
                    .setDuration(duration);
            AnimatorSet singleSet = new AnimatorSet();
            singleSet.playTogether(mainX, mainY);
            addDefaultListener(singleSet, view, ANIM_TYPE_POSITION);
            sets.add(singleSet);
        }
        AnimatorSet sequence = new AnimatorSet();
        sequence.playTogether(sets);
        sequence.setInterpolator(new BezierInterpolator(c1, c2));
        addCustomListener(sequence, listener);
        sequence.start();
    }

    public static void viewScale(View view, float x, float y, float c1, float c2, int duration,
                                 AnimatorListenerAdapter... listener) {
        clearPreviousAnimator(view, ANIM_TYPE_SCALE);
        ObjectAnimator mainX = ObjectAnimator.ofFloat(view, "scaleX", x)
                .setDuration(duration);
        ObjectAnimator mainY = ObjectAnimator.ofFloat(view, "scaleY", y)
                .setDuration(duration);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(mainX, mainY);
        set.setInterpolator(new BezierInterpolator(c1, c2));
        addDefaultListener(set, view, ANIM_TYPE_SCALE);
        addCustomListener(set, listener);
        set.start();
    }

    public static void viewScale(View[] views, float x, float y, float c1, float c2,
                                 int duration, AnimatorListenerAdapter... listener) {
        List<Animator> sets = new ArrayList<>();
        for (View view : views) clearPreviousAnimator(view, ANIM_TYPE_SCALE);
        for (View view : views) {
            ObjectAnimator mainX = ObjectAnimator.ofFloat(view, "scaleX", x)
                    .setDuration(duration);
            ObjectAnimator mainY = ObjectAnimator.ofFloat(view, "scaleY", y)
                    .setDuration(duration);
            AnimatorSet singleSet = new AnimatorSet();
            singleSet.playTogether(mainX, mainY);
            addDefaultListener(singleSet, view, ANIM_TYPE_SCALE);
            sets.add(singleSet);
        }
        AnimatorSet sequence = new AnimatorSet();
        sequence.playTogether(sets);
        sequence.setInterpolator(new BezierInterpolator(c1, c2));
        addCustomListener(sequence, listener);
        sequence.start();

    }

    public static void animPercentage(View view, int c1, int c2, int duration, ValueAnimator.AnimatorUpdateListener updateListener) {
        clearPreviousAnimator(view, ANIM_TYPE_PERCENTAGE);
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(duration);
        animator.setInterpolator(new BezierInterpolator(c1, c2));
        addDefaultListener(animator, view, ANIM_TYPE_PERCENTAGE);
        animator.addUpdateListener(updateListener);
        animator.start();
    }

    public static void animColor(View view, int startColor, int endColor, int c1, int c2, int duration, ValueAnimator.AnimatorUpdateListener updateListener) {
        clearPreviousAnimator(view, ANIM_TYPE_COLOR);
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        animator.setDuration(duration);
        animator.setInterpolator(new BezierInterpolator(c1, c2));
        addDefaultListener(animator, view, ANIM_TYPE_COLOR);
        animator.addUpdateListener(updateListener);
        animator.start();
    }

    private static void clearPreviousAnimator(View view, String prop) {
        if (ongoingAnimators.containsKey(view.getId() + prop)) {
            Animator animator = ongoingAnimators.get(view.getId() + prop);
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
        }
    }

    private static void addCustomListener(Animator animator, AnimatorListenerAdapter... listener) {
        for (AnimatorListenerAdapter l : listener) {
            if (l != null) {
                animator.addListener(l);
            }
        }
    }

    private static void addDefaultListener(Animator animator, View view, String prop) {
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                ongoingAnimators.put(view.getId() + prop, animator);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ongoingAnimators.remove(view.getId() + prop);
            }

        });
    }
}
