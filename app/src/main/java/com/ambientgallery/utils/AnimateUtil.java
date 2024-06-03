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
    public static final HashMap<String, Animator> ongoingAnimators = new HashMap<>();

    public static void viewOpacity(View view, float alpha, float c1, float c2, int duration,
                                   AnimatorListenerAdapter... listener) {
        String prop = "opacity";
        clearPreviousAnimator(view, prop);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", alpha)
                .setDuration(duration);
        animator.setInterpolator(new BezierInterpolator(c1, c2));
        addDefaultListener(animator, view, prop);
        addCustomListener(animator, listener);
        animator.start();
    }

    public static void viewOpacity(View[] views, float alpha, float c1, float c2, int duration,
                                   AnimatorListenerAdapter... listener) {
        String prop = "opacity";
        List<Animator> sets = new ArrayList<>();
        for (View view : views) clearPreviousAnimator(view, prop);
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
        String prop = "rotation";
        clearPreviousAnimator(view, prop);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", deg)
                .setDuration(duration);
        animator.setInterpolator(new BezierInterpolator(c1, c2));
        addDefaultListener(animator, view, prop);
        addCustomListener(animator, listener);
        animator.start();

    }

    public static void viewRotation(View[] views, float deg, float c1, float c2, int duration,
                                    AnimatorListenerAdapter... listener) {
        String prop = "rotation";
        List<Animator> sets = new ArrayList<>();
        for (View view : views) clearPreviousAnimator(view, prop);
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
        String prop = "position";
        clearPreviousAnimator(view, prop);
        ObjectAnimator mainX = ObjectAnimator.ofFloat(view, "translationX", x)
                .setDuration(duration);
        ObjectAnimator mainY = ObjectAnimator.ofFloat(view, "translationY", y)
                .setDuration(duration);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(mainX, mainY);
        set.setInterpolator(new BezierInterpolator(c1, c2));
        addDefaultListener(set, view, prop);
        addCustomListener(set, listener);
        set.start();
    }

    public static void viewPosition(View[] views, float x, float y, float c1, float c2, int duration,
                                    AnimatorListenerAdapter... listener) {
        String prop = "position";
        List<Animator> sets = new ArrayList<>();
        for (View view : views) clearPreviousAnimator(view, prop);
        for (View view : views) {
            ObjectAnimator mainX = ObjectAnimator.ofFloat(view, "translationX", x)
                    .setDuration(duration);
            ObjectAnimator mainY = ObjectAnimator.ofFloat(view, "translationY", y)
                    .setDuration(duration);
            AnimatorSet singleSet = new AnimatorSet();
            singleSet.playTogether(mainX, mainY);
            addDefaultListener(singleSet, view, prop);
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
        String prop = "scale";
        clearPreviousAnimator(view, prop);
        ObjectAnimator mainX = ObjectAnimator.ofFloat(view, "scaleX", x)
                .setDuration(duration);
        ObjectAnimator mainY = ObjectAnimator.ofFloat(view, "scaleY", y)
                .setDuration(duration);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(mainX, mainY);
        set.setInterpolator(new BezierInterpolator(c1, c2));
        addDefaultListener(set, view, prop);
        addCustomListener(set, listener);
        set.start();
    }

    public static void viewScale(View[] views, float x, float y, float c1, float c2,
                                 int duration, AnimatorListenerAdapter... listener) {
        String prop = "scale";
        List<Animator> sets = new ArrayList<>();
        for (View view : views) clearPreviousAnimator(view, prop);
        for (View view : views) {
            ObjectAnimator mainX = ObjectAnimator.ofFloat(view, "scaleX", x)
                    .setDuration(duration);
            ObjectAnimator mainY = ObjectAnimator.ofFloat(view, "scaleY", y)
                    .setDuration(duration);
            AnimatorSet singleSet = new AnimatorSet();
            singleSet.playTogether(mainX, mainY);
            addDefaultListener(singleSet, view, prop);
            sets.add(singleSet);
        }
        AnimatorSet sequence = new AnimatorSet();
        sequence.playTogether(sets);
        sequence.setInterpolator(new BezierInterpolator(c1, c2));
        addCustomListener(sequence, listener);
        sequence.start();

    }

    public static void animPercentage(int c1, int c2, int duration, ValueAnimator.AnimatorUpdateListener updateListener) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(duration);
        animator.setInterpolator(new BezierInterpolator(c1, c2));
        animator.addUpdateListener(updateListener);
        animator.start();
    }

    public static void animColor(int startColor, int endColor, int c1, int c2, int duration, ValueAnimator.AnimatorUpdateListener updateListener) {
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        animator.setDuration(duration);
        animator.setInterpolator(new BezierInterpolator(c1, c2));
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
