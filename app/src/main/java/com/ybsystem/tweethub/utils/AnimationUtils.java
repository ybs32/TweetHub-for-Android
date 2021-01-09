package com.ybsystem.tweethub.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * Utils class for animation
 */
public class AnimationUtils {

    private AnimationUtils() {
    }

    /**
     * Fade in a view at the specified duration
     * @param view Target view
     * @param duration Duration time
     */
    public static void fadeIn(View view, int duration) {
        AlphaAnimation alphaFadein = new AlphaAnimation(0.0f, 1.0f);
        alphaFadein.setDuration(duration);
        view.startAnimation(alphaFadein);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Fade out a view at the specified duration
     * @param view Target view
     * @param duration Duration time
     */
    public static void fadeOut(View view, int duration) {
        AlphaAnimation alphaFadeout = new AlphaAnimation(1.0f, 0.0f);
        alphaFadeout.setDuration(duration);
        view.startAnimation(alphaFadeout);
        view.setVisibility(View.GONE);
    }

}
