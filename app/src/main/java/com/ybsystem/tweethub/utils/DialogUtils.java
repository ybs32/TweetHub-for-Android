package com.ybsystem.tweethub.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

import androidx.fragment.app.FragmentManager;

import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.dialog.ChoiceDialog;
import com.ybsystem.tweethub.fragments.dialog.ConfirmDialog;
import com.ybsystem.tweethub.models.entities.Account;
import com.ybsystem.tweethub.models.entities.AccountArray;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;

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
    public static void showProgress(String message, Context context) {
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
        FragmentManager fm = TweetHubApp.getActivity().getSupportFragmentManager();
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
        FragmentManager fm = TweetHubApp.getActivity().getSupportFragmentManager();
        if (fm.findFragmentByTag("ChoiceDialog") == null) {
            dialog.show(fm, "ChoiceDialog");
        }
    }

    /**
     * Show choice dialog for switching accounts
     */
    public static void showAccountChoice() {
        // Create
        AccountArray<Account> accounts = TweetHubApp.getData().getAccounts();
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
                    TweetHubApp.getInstance().init();
                    ToastUtils.showShortToast("アカウントを切り替えました。");
                    ActivityUtils.rebootActivity(TweetHubApp.getActivity(), 0, 0);
                }
        );
    }

}
