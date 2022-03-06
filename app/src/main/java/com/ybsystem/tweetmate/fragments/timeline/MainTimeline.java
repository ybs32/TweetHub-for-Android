package com.ybsystem.tweetmate.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.recycler.TweetRecyclerAdapter;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.models.entities.Column;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweetmate.utils.ExceptionUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class MainTimeline extends TimelineBase {

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
        setRecyclerAdapter(new TweetRecyclerAdapter());
        setUltraPullToRefresh(view);

        return view;
    }

    @Override
    protected void loadTweet(boolean isPullLoad, boolean isClickLoad) {
        // Change footer
        mFooterText.setText("読み込み中...");
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);

        // Connect async
        TweetRecyclerAdapter adapter = (TweetRecyclerAdapter) mRecyclerAdapter;
        Observable<List<Status>> observable = Observable.create(e -> {
            try {
                Paging paging;
                if (isPullLoad || adapter.isEmpty()) {
                    paging = mFirstPaging;
                } else {
                    paging = mNextPaging;
                    paging.setMaxId(adapter.getLastObj().getId() - 1);
                }
                Twitter twitter = TweetMateApp.getTwitter();
                switch (mColumn.getType()) {
                    case HOME:
                        e.onNext(twitter.getHomeTimeline(paging));
                        break;
                    case MENTIONS:
                        e.onNext(twitter.getMentionsTimeline(paging));
                        break;
                    case FAVORITE:
                        e.onNext(twitter.getFavorites(paging));
                        break;
                    case LIST_SINGLE:
                        e.onNext(twitter.getUserListStatuses(mColumn.getId(), paging));
                        break;
                }
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<List<Status>> disposable = new DisposableObserver<List<Status>>() {
            @Override
            public void onNext(List<Status> statusList) {
                if (statusList.isEmpty()) {
                    // No tweet...
                    if (adapter.isEmpty()) {
                        mFooterText.setText("ツイートなし");
                        mFooterProgress.setVisibility(View.GONE);
                    } else
                        mFooterView.setVisibility(View.GONE);
                } else {
                    // Success
                    if (isPullLoad) {
                        adapter.clear();
                    }
                    for (twitter4j.Status status : statusList) {
                        adapter.add(new TwitterStatus(status));
                    }
                    adapter.notifyDataSetChanged();
                    mScrollLoad = true;
                }
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                mFooterText.setText("タップして読み込む");
                mFooterProgress.setVisibility(View.GONE);

                // Show error message if loaded by user action
                if (isPullLoad || isClickLoad) {
                    ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
                }
                mFooterClick = true;
            }

            @Override
            public void onComplete() {
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (isPullLoad) {
                        mPtrFrame.refreshComplete();
                    }
                })
                .subscribe(disposable);
    }

}
