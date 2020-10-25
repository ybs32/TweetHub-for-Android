package com.ybsystem.tweethub.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

/**
 * Utils class for progress dialog creation
 */
public class DialogUtils {

    private static ProgressDialog sProgressDialog;

    private DialogUtils() {
    }

    /**
     * Show progress dialog with message
     * @param message Message to display in dialog
     */
    public static void showProgressDialog(String message, Context context) {
        // Increase text size
        SpannableString spanMessage = new SpannableString(message);
        spanMessage.setSpan(new RelativeSizeSpan(1.2f), 0, spanMessage.length(), 0);

        // Create dialog
        sProgressDialog = new ProgressDialog(context);
        sProgressDialog.setMessage(spanMessage);

        // Prevent accidental closing by screen tap （※Back button is available）
        sProgressDialog.setCanceledOnTouchOutside(false);
        sProgressDialog.show();
    }

    /**
     * Close progress dialog
     */
    public static void dismissProgressDialog() {
        if (sProgressDialog != null && sProgressDialog.isShowing()) {
            sProgressDialog.dismiss();
        }
    }

}
