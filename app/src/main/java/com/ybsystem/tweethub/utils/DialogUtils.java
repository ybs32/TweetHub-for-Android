package com.ybsystem.tweethub.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

import androidx.fragment.app.FragmentManager;

import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.dialog.ConfirmDialog;

/**
 * Utils class for progress dialog creation
 */
public class DialogUtils {

    private static ProgressDialog sProgressDialog;

    private DialogUtils() {
    }

    /**
     * Show confirm dialog with message
     *
     * @param text     Message to display in dialog
     * @param listener Positive click listener
     */
    public static void showConfirmDialog(String text,
                                         DialogInterface.OnClickListener listener) {
        // Create
        ConfirmDialog dialog = new ConfirmDialog().newInstance(text);
        dialog.setOnPositiveClickListener(listener);

        // Show
        FragmentManager fm = TweetHubApp.getActivity().getSupportFragmentManager();
        if (fm.findFragmentByTag("ConfirmDialog") == null) {
            dialog.show(fm, "ConfirmDialog");
        }
    }

    /**
     * Show progress dialog with message
     *
     * @param message Message to display in dialog
     */
    public static void showProgressDialog(String message, Context context) {
        // Increase text size
        SpannableString spanMessage = new SpannableString(message);
        spanMessage.setSpan(new RelativeSizeSpan(1.2f), 0, spanMessage.length(), 0);

        // Create
        sProgressDialog = new ProgressDialog(context);
        sProgressDialog.setMessage(spanMessage);

        // Show
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
