package com.ybsystem.tweetmate.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.recycler.UserListRecyclerAdapter;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.models.entities.Column;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUserList;
import com.ybsystem.tweetmate.utils.ExceptionUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UserList;

import static com.ybsystem.tweetmate.models.enums.ColumnType.*;
import static com.ybsystem.tweetmate.resources.ResString.*;

public class UserListTimeline extends TimelineBase {

    private Column mColumn;

    public Fragment newInstance(Column column) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("COLUMN", column);
        setArguments(bundle);
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        // Init
        mColumn = (Column) getArguments().getSerializable("COLUMN");
        mScrollLoad = true;

        // Set view
        setRecyclerView(view);
        setRecyclerAdapter(new UserListRecyclerAdapter());
        setUltraPullToRefresh(view);
        mPtrFrame.setEnabled(false);

        return view;
    }

    @Override
    protected void loadTweet(boolean isPullLoad, boolean isClickLoad) {
        // Change footer
        mFooterText.setText(STR_LOADING);
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);

        UserListRecyclerAdapter adapter = (UserListRecyclerAdapter) mRecyclerAdapter;
        Observable<ResponseList<UserList>> observable = Observable.create(e -> {
            try {
                Twitter twitter = TweetMateApp.getTwitter();
                switch (mColumn.getType()) {
                    case LIST_CREATED:
                        e.onNext(twitter.getUserListsOwnerships(mColumn.getId(), 200, -1));
                        break;
                    case LIST_SUBSCRIBING:
                        e.onNext(twitter.getUserListSubscriptions(mColumn.getId(), 200, -1));
                        break;
                    case LIST_BELONGING:
                        e.onNext(twitter.getUserListMemberships(mColumn.getId(), 200, -1));
                        break;
                }
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<ResponseList<UserList>>
                disposable = new DisposableObserver<ResponseList<UserList>>() {
            @Override
            public void onNext(ResponseList<UserList> userLists) {
                // Clear
                adapter.clear();

                if (userLists.isEmpty()) {
                    // No list...
                    if (adapter.isEmpty()) {
                        mFooterText.setText(STR_LIST_NONE);
                        mFooterProgress.setVisibility(View.GONE);
                    } else
                        mFooterView.setVisibility(View.GONE);
                } else {
                    // Success
                    for (UserList list4j : userLists) {
                        TwitterUserList list = new TwitterUserList(list4j);
                        setOwner(list);
                        adapter.add(list);
                    }
                    adapter.notifyDataSetChanged();
                    mFooterView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                mFooterText.setText(STR_TAP_TO_LOAD);
                mFooterProgress.setVisibility(View.GONE);

                // Show error message if loaded by user action
                if (isClickLoad) {
                    ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
                }
                mFooterClick = true;
            }

            @Override
            public void onComplete() {
            }

            private void setOwner(TwitterUserList list) {
                if (mColumn.getType().equals(LIST_CREATED)
                        && mColumn.getId() == TweetMateApp.getMyUser().getId()) {
                    list.setOwner(true);
                }
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable);
    }

}
