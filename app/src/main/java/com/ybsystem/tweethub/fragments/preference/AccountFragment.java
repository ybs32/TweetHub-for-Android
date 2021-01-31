package com.ybsystem.tweethub.fragments.preference;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.preference.BrowserAuthActivity;
import com.ybsystem.tweethub.adapters.array.AccountArrayAdapter;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.dialog.ConfirmDialog;
import com.ybsystem.tweethub.fragments.dialog.ListDialog;
import com.ybsystem.tweethub.models.entities.Account;
import com.ybsystem.tweethub.models.entities.AccountArray;
import com.ybsystem.tweethub.utils.ToastUtils;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;

public class AccountFragment extends Fragment {

    private ListView mListView;
    private AccountArrayAdapter mArrayAdapter;

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_manage, container, false);

        // Set contents
        setAccountList(view);
        setListItemClickListener();
        setAddButtonClickListener(view);

        return view;
    }

    private void setAccountList(View view) {
        mListView = view.findViewById(R.id.list_view);
        mArrayAdapter = new AccountArrayAdapter(getActivity());

        // Set adapter and remove divider
        mListView.setAdapter(mArrayAdapter);
        mListView.setDivider(null);

        // Add accounts
        AccountArray<Account> accounts = TweetHubApp.getData().getAccounts();
        for (Account account : accounts) {
            mArrayAdapter.add(account);
        }
    }

    private void setListItemClickListener() {
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            // Create ListDialog
            String[] items = {"上に移動", "下に移動", "解除する"};
            ListDialog dialog = new ListDialog().newInstance(items);

            // Set click listener
            dialog.setOnItemClickListener((p, v, which, identity) -> {
                AccountArray<Account> accounts = TweetHubApp.getData().getAccounts();
                switch (which) {
                    case 0:
                        accounts.movePrev(position);
                        break;
                    case 1:
                        accounts.moveNext(position);
                        break;
                    case 2:
                        showConfirmDialog(accounts, position);
                        break;
                }
                refreshListView();
                dialog.dismiss();
            });

            // Show ListDialog
            if (getFragmentManager().findFragmentByTag("ListDialog") == null) {
                dialog.show(getFragmentManager(), "ListDialog");
            }
        });
    }

    private void setAddButtonClickListener(View view) {
        view.findViewById(R.id.button_add).setOnClickListener(v -> {
            // Intent to BrowserAuthActivity
            Intent intent = new Intent(getActivity(), BrowserAuthActivity.class);
            startActivityForResult(intent, 0);
            getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
        });
    }

    private void showConfirmDialog(AccountArray<Account> accounts, int position) {
        // Check
        if (position == accounts.getCurrentAccountNum()) {
            ToastUtils.showShortToast("使用中のアカウントです。");
            return;
        }
        // Create
        ConfirmDialog confirmDialog = new ConfirmDialog().newInstance("アカウントを解除しますか？");
        confirmDialog.setOnPositiveClickListener((dialog, which) -> {
            accounts.remove(position);
            refreshListView();
            ToastUtils.showShortToast("アカウントを解除しました。");
        });
        // Show
        if (getFragmentManager().findFragmentByTag("ConfirmDialog") == null) {
            confirmDialog.show(getFragmentManager(), "ConfirmDialog");
        }
    }

    private void refreshListView() {
        // Prepare reboot
        getActivity().setResult(REBOOT_PREPARATION);
        mArrayAdapter.clear();

        // Add accounts
        AccountArray<Account> accounts = TweetHubApp.getData().getAccounts();
        for (Account account : accounts) {
            mArrayAdapter.add(account);
        }
    }

}
