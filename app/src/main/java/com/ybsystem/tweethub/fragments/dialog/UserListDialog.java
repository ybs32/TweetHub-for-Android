package com.ybsystem.tweethub.fragments.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.models.entities.UserList;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.usecases.UserUseCase;
import com.ybsystem.tweethub.utils.AnimationUtils;
import com.ybsystem.tweethub.utils.ExceptionUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class UserListDialog extends DialogFragment {

    private ListView mListView;
    private TwitterUser mTargetUser;

    private ResponseList<twitter4j.UserList> mUserLists;
    private ResponseList<twitter4j.UserList> mRegisteredLists;

    public UserListDialog newInstance(TwitterUser targetUser) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("TARGET_USER", targetUser);
        setArguments(bundle);
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_list, null);

        // Init
        mListView = (ListView) view.findViewById(R.id.list_view);
        mTargetUser = (TwitterUser) getArguments().getSerializable("TARGET_USER");

        // Fetch user list
        fetchUserList(view);

        // Create
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        return builder.create();
    }

    private void fetchUserList(View view) {
        // Async
        Observable<Object> observable = Observable.create(e -> {
            // Fetch
            Twitter twitter = TweetHubApp.getTwitter();
            mUserLists = twitter.getUserLists(twitter.getId());
            mRegisteredLists = twitter.getUserListMemberships(mTargetUser.getId(), -1, true);
            e.onComplete();
        });

        DisposableObserver<Object> disposable = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object obj) {
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                ToastUtils.showShortToast("リストの取得に失敗しました...");
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
                dismiss();
            }

            @Override
            public void onComplete() {
                // Check size
                if (mUserLists.isEmpty()) {
                    ToastUtils.showShortToast("リストが存在しません。");
                    dismiss();
                    return;
                }
                // Set adapter
                setUserListAdapter();
                AnimationUtils.fadeOut(view.findViewById(R.id.linear_loading), 0);
                AnimationUtils.fadeIn(view.findViewById(R.id.list_view), 400);
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable);
    }

    private void setUserListAdapter() {
        UserListAdapter adapter = new UserListAdapter(getContext());
        mListView.setAdapter(adapter);
        mListView.setDivider(null);
        mListView.setFocusable(false);

        for (twitter4j.UserList list : mUserLists) {
            boolean isRegistered = mRegisteredLists.stream()
                    .anyMatch(regList -> regList.getId() == list.getId());
            adapter.add(
                    new UserList(list.getId(), list.getName(), isRegistered));
        }
    }

    private class UserListAdapter extends ArrayAdapter<UserList> {

        public UserListAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Inflate
            View cv = convertView;
            if (cv == null) {
                cv = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item_checkbox, parent, false);
            }
            // Get item
            UserList userList = getItem(position);

            // Set checkbox
            CheckBox checkBox = cv.findViewById(R.id.checkbox);
            checkBox.setText(userList.getName());
            checkBox.setChecked(userList.isRegistered());

            // When check changed
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) {
                    UserUseCase.updateListUser(userList, mTargetUser, !isChecked);
                }
            });

            return cv;
        }
    }

}
