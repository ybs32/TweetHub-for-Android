package com.ybsystem.tweetmate.utils;

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
     *
     * @param view Target view
     * @param duration Duration time
     */
    public static void fadeIn(View view, int duration) {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(duration);
        view.startAnimation(fadeIn);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Fade out a view at the specified duration
     *
     * @param view Target view
     * @param duration Duration time
     */
    public static void fadeOut(View view, int duration) {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(duration);
        view.startAnimation(fadeOut);
        view.setVisibility(View.GONE);
    }

}
