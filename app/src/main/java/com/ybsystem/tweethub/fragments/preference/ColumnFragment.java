package com.ybsystem.tweethub.fragments.preference;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.adapters.array.ColumnArrayAdapter;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.fragments.dialog.ListDialog;
import com.ybsystem.tweethub.models.entities.Column;
import com.ybsystem.tweethub.models.entities.ColumnArray;
import com.ybsystem.tweethub.storages.PrefAppearance;
import com.ybsystem.tweethub.utils.ExceptionUtils;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UserList;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;
import static com.ybsystem.tweethub.models.enums.ColumnType.*;

public class ColumnFragment extends Fragment {

    private ListView mListView;
    private ColumnArrayAdapter mArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        // Set contents
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
        ColumnArray<Column> columns = TweetHubApp.getMyAccount().getColumns();
        for (Column column : columns) {
            mArrayAdapter.add(column);
        }
    }

    private void setListItemClickListener() {

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            // Create ListDialog
            String[] items = {"起動カラムに設定", "上に移動", "下に移動", "削除する"};
            ListDialog dialog = new ListDialog().newInstance(items);

            // Set click listener
            dialog.setOnItemClickListener((p, v, which, identity) -> {
                ColumnArray<Column> columns = TweetHubApp.getMyAccount().getColumns();
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
            if (TweetHubApp.getMyAccount().getColumns().size() >= 8) {
                ToastUtils.showShortToast("これ以上追加できません。");
                return;
            }
            // Create ListDialog
            String[] items = {"@Mentions", "ホーム", "リスト", "リスト (単体)", PrefAppearance.getLikeFavText()};
            ListDialog dialog = new ListDialog().newInstance(items);

            // Set click listener
            dialog.setOnItemClickListener((parent, view1, which, id) -> {
                ColumnArray<Column> columnList = TweetHubApp.getMyAccount().getColumns();
                switch (which) {
                    case 0:
                        columnList.add(
                                new Column(-1, "@Mentions", MENTIONS, false)
                        );
                        break;
                    case 1:
                        columnList.add(
                                new Column(-2, "ホーム", HOME, false)
                        );
                        break;
                    case 2:
                        columnList.add(
                                new Column(-3, "リスト", LIST, false)
                        );
                        break;
                    case 3:
                        showMyListDialog();
                        break;
                    case 4:
                        columnList.add(
                                new Column(-4, PrefAppearance.getLikeFavText(), FAVORITE, false)
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

    private void showMyListDialog() {
        DialogUtils.showProgressDialog("読み込み中...", getActivity());

        Observable<ResponseList<UserList>> observable = Observable.create(e -> {
            // Fetch
            ResponseList<UserList> lists;
            Twitter twitter = TweetHubApp.getTwitter();
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
                    ToastUtils.showShortToast("リストが存在しません。");
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
                    TweetHubApp.getMyAccount().getColumns().add(column);
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
                TwitterException e = (TwitterException) t;
                ToastUtils.showShortToast("リストの取得に失敗しました...");
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
            }

            @Override
            public void onComplete() {
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(DialogUtils::dismissProgressDialog)
                .subscribe(disposable);
    }

    private void refreshListView() {
        // Prepare reboot
        getActivity().setResult(REBOOT_PREPARATION);

        // Clear item
        mArrayAdapter.clear();

        // Add columns
        ColumnArray<Column> columns = TweetHubApp.getMyAccount().getColumns();
        for (Column column : columns) {
            mArrayAdapter.add(column);
        }
    }

}
