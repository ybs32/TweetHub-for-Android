package com.ybsystem.tweetmate.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

import androidx.fragment.app.FragmentManager;

import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.fragments.dialog.ChoiceDialog;
import com.ybsystem.tweetmate.fragments.dialog.ConfirmDialog;
import com.ybsystem.tweetmate.models.entities.Account;
import com.ybsystem.tweetmate.models.entities.AccountArray;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;

import static com.ybsystem.tweetmate.resources.ResString.*;

/**
 * Utils class for progress dialog creation
 */
public class DialogUtils {

    private static ProgressDialog sProgressDialog;

    private DialogUtils() {
    }

    /**
     * Show progress dialog with message
     *
     * @param message Message to display in dialog
     */
    public static void showProgress(String message) {
        Activity activity = TweetMateApp.getActivity();

        // Increase text size
        SpannableString spanMessage = new SpannableString(message);
        spanMessage.setSpan(new RelativeSizeSpan(1.2f), 0, spanMessage.length(), 0);

        // Create
        sProgressDialog = new ProgressDialog(activity);
        sProgressDialog.setMessage(spanMessage);

        // Prevent accidental closing by screen tap (Back button is available)
        sProgressDialog.setCanceledOnTouchOutside(false);

        // Show
        sProgressDialog.show();
    }

    /**
     * Show progress dialog with message
     *
     * @param message Message to display in dialog
     */
    public static void showPersistentProgress(String message) {
        Activity activity = TweetMateApp.getActivity();

        // Increase text size
        SpannableString spanMessage = new SpannableString(message);
        spanMessage.setSpan(new RelativeSizeSpan(1.2f), 0, spanMessage.length(), 0);

        // Create
        sProgressDialog = new ProgressDialog(activity);
        sProgressDialog.setMessage(spanMessage);

        // Prevent hide
        sProgressDialog.setCanceledOnTouchOutside(false);
        sProgressDialog.setCancelable(false);

        // Show
        sProgressDialog.show();
    }

    /**
     * Close progress dialog
     */
    public static void dismissProgress() {
        if (sProgressDialog != null && sProgressDialog.isShowing()) {
            sProgressDialog.dismiss();
        }
    }

    /**
     * Show confirm dialog with message
     *
     * @param text     Message to display in dialog
     * @param listener Positive click listener
     */
    public static void showConfirm(String text, OnClickListener listener) {
        // Create
        ConfirmDialog dialog = new ConfirmDialog().newInstance(text);
        dialog.setOnPositiveClickListener(listener);

        // Show
        FragmentManager fm = TweetMateApp.getActivity().getSupportFragmentManager();
        if (fm.findFragmentByTag("ConfirmDialog") == null) {
            dialog.show(fm, "ConfirmDialog");
        }
    }

    /**
     * Show a selective dialog
     *
     * @param items    Choice items
     * @param selected Selected position
     * @param listener Item click listener
     */
    public static void showChoice(String[] items, int selected, OnClickListener listener) {
        // Create
        ChoiceDialog dialog = new ChoiceDialog().newInstance(items, selected);
        dialog.setOnItemClickListener(listener);

        // Show
        FragmentManager fm = TweetMateApp.getActivity().getSupportFragmentManager();
        if (fm.findFragmentByTag("ChoiceDialog") == null) {
            dialog.show(fm, "ChoiceDialog");
        }
    }

    /**
     * Show choice dialog for switching accounts
     */
    public static void showAccountChoice() {
        // Create
        AccountArray<Account> accounts = TweetMateApp.getData().getAccounts();
        String[] items = new String[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            TwitterUser user = accounts.get(i).getUser();
            items[i] = user.getName() + " (@" + user.getScreenName() + ")";
        }
        // Show
        showChoice(
                items, accounts.getCurrentAccountNum(),
                (dialog, which) -> {
                    // Change account and reboot
                    accounts.setCurrentAccount(which);
                    TweetMateApp.getInstance().initTwitter();
                    ToastUtils.showShortToast(STR_SUCCESS_SWITCH_ACCOUNT);
                    ActivityUtils.rebootActivity(TweetMateApp.getActivity());
                }
        );
    }

}
