package com.ybsystem.tweetmate.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Utils class for keyboard events
 */
public class KeyboardUtils {

    private KeyboardUtils() {
    }

    /**
     * Close software keyboard
     * @param view View
     */
    public static void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)
                view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
