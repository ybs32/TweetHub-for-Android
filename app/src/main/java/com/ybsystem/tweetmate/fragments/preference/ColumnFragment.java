package com.ybsystem.tweetmate.fragments.preference;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.array.ColumnArrayAdapter;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.fragments.dialog.ListDialog;
import com.ybsystem.tweetmate.libs.eventbus.ColumnEvent;
import com.ybsystem.tweetmate.models.entities.Column;
import com.ybsystem.tweetmate.models.entities.ColumnArray;
import com.ybsystem.tweetmate.storages.PrefAppearance;
import com.ybsystem.tweetmate.utils.ExceptionUtils;
import com.ybsystem.tweetmate.utils.DialogUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.UserList;

import static com.ybsystem.tweetmate.activities.preference.SettingActivity.*;
import static com.ybsystem.tweetmate.models.enums.ColumnType.*;
import static com.ybsystem.tweetmate.resources.ResString.*;

public class ColumnFragment extends Fragment {

    private ListView mListView;
    private ColumnArrayAdapter mArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        // Set
        setColumnList(view);
        setListItemClickListener();
        setAddButtonClickListener(view);

        return view;
    }

    private void setColumnList(View view) {
        mListView = view.findViewById(R.id.list_view);
        mArrayAdapter = new ColumnArrayAdapter(getActivity());

        // Set adapter and remove divider
        mListView.setAdapter(mArrayAdapter);
        mListView.setDivider(null);

        // Add columns
        ColumnArray<Column> columns = TweetMateApp.getMyAccount().getColumns();
        for (Column column : columns) {
            mArrayAdapter.add(column);
        }
    }

    private void setListItemClickListener() {

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            // Create ListDialog
            String[] items = {STR_SET_BOOT_COLUMN, STR_MOVE_UP, STR_MOVE_DOWN, STR_DELETE};
            ListDialog dialog = new ListDialog().newInstance(items);

            // Set click listener
            dialog.setOnItemClickListener((p, v, which, identity) -> {
                ColumnArray<Column> columns = TweetMateApp.getMyAccount().getColumns();
                switch (which) {
                    case 0:
                        columns.setBootColumn(position);
                        break;
                    case 1:
                        columns.movePrev(position);
                        break;
                    case 2:
                        columns.moveNext(position);
                        break;
                    case 3:
                        columns.remove(position);
                        break;
                }
                refreshListView();
                dialog.dismiss();
            });

            // Show ListDialog
            if (getFragmentManager().findFragmentByTag("ColumnDialog") == null) {
                dialog.show(getFragmentManager(), "ColumnDialog");
            }
        });
    }

    private void setAddButtonClickListener(View view) {

        view.findViewById(R.id.button_add).setOnClickListener(v -> {
            //　Unavailable over 8 column
            if (TweetMateApp.getMyAccount().getColumns().size() >= 8) {
                ToastUtils.showShortToast(STR_FAIL_NO_MORE_ADD);
                return;
            }
            // Create ListDialog
            String[] items = {STR_MENTIONS, STR_HOME, STR_LIST, STR_LIST_SINGLE, PrefAppearance.getLikeFavText(), STR_SEARCH};
            ListDialog dialog = new ListDialog().newInstance(items);

            // Set click listener
            dialog.setOnItemClickListener((parent, view1, which, id) -> {
                ColumnArray<Column> columns = TweetMateApp.getMyAccount().getColumns();
                switch (which) {
                    case 0:
                        columns.add(
                                new Column(-1, STR_MENTIONS, MENTIONS, false)
                        );
                        break;
                    case 1:
                        columns.add(
                                new Column(-2, STR_HOME, HOME, false)
                        );
                        break;
                    case 2:
                        columns.add(
                                new Column(-3, STR_LIST, LIST, false)
                        );
                        break;
                    case 3:
                        fetchUserList();
                        break;
                    case 4:
                        columns.add(
                                new Column(-4, PrefAppearance.getLikeFavText(), FAVORITE, false)
                        );
                        break;
                    case 5:
                        columns.add(
                                new Column(-5, STR_SEARCH, SEARCH, false)
                        );
                        break;
                }
                refreshListView();
                dialog.dismiss();
            });

            // Show ListDialog
            if (getFragmentManager().findFragmentByTag("AddColumnDialog") == null) {
                dialog.show(getFragmentManager(), "AddColumnDialog");
            }
        });
    }

    private void fetchUserList() {
        DialogUtils.showProgress(STR_LOADING, getContext());

        Observable<ResponseList<UserList>> observable = Observable.create(e -> {
            // Fetch
            ResponseList<UserList> lists;
            Twitter twitter = TweetMateApp.getTwitter();
            lists = twitter.getUserLists(twitter.getId());

            // Check
            if (lists != null) {
                e.onNext(lists);
            }
            e.onComplete();
        });

        DisposableObserver<ResponseList<UserList>> disposable
                = new DisposableObserver<ResponseList<UserList>>() {
            @Override
            public void onNext(ResponseList<UserList> lists) {
                // Check size
                if (lists.isEmpty()) {
                    ToastUtils.showShortToast(STR_FAIL_NO_LIST);
                    return;
                }
                // Create ListDialog
                final String[] items = new String[lists.size()];
                for (int i = 0; i < lists.size(); i++) {
                    items[i] = lists.get(i).getName();
                }
                ListDialog dialog = new ListDialog().newInstance(items);
                dialog.setOnItemClickListener((parent, view, position, id) -> {
                    Column column = new Column(
                            lists.get(position).getId(),
                            lists.get(position).getName(),
                            LIST_SINGLE,
                            false);
                    TweetMateApp.getMyAccount().getColumns().add(column);
                    refreshListView();
                    dialog.dismiss();
                });
                // Show ListDialog
                if (getFragmentManager().findFragmentByTag("ListDialog") == null) {
                    dialog.show(getFragmentManager(), "ListDialog");
                }
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                ToastUtils.showShortToast(STR_FAIL_GET_LIST);
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
            }

            @Override
            public void onComplete() {
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(DialogUtils::dismissProgress)
                .subscribe(disposable);
    }

    private void refreshListView() {
        // Prepare reboot
        getActivity().setResult(REBOOT_PREPARATION);

        // Clear item
        mArrayAdapter.clear();

        // Add columns
        ColumnArray<Column> columns = TweetMateApp.getMyAccount().getColumns();
        for (Column column : columns) {
            mArrayAdapter.add(column);
        }

        // Notify event
        EventBus.getDefault().postSticky(new ColumnEvent());
    }

}
