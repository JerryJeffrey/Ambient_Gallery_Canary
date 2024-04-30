package com.ambientgallery.components;

import android.view.animation.Interpolator;


public class BezierInterpolator implements Interpolator {
    float a, b;

    public BezierInterpolator(float startVel, float endVel) {
        a = startVel;
        b = endVel;
    }

    @Override
    public float getInterpolation(float t) {
        return (0 * (1 - t) * (1 - t) * (1 - t))
                + (3 * a * t * (1 - t) * (1 - t))
                + (3 * b * t * t * (1 - t))
                + (1 * t * t * t);
    }
}
//to be used in graph drawers
//y=0*(1-t)*(1-t)*(1-t)+3*a*t*(1-t)*(1-t)+3*b*t*t*(1-t)+1*t*t*t
