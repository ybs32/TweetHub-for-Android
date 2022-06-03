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

import static com.ybsystem.tweetmate.resources.ResString.*;

public class ProfileTimeline extends TimelineBase {

    // Bundle
    private Column mColumn;
    private boolean mIsPrivate;

    // For user media
    private long mLastTweetId;

    public Fragment newInstance(Column column, boolean isPrivate) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("COLUMN", column);
        bundle.putSerializable("PRIVATE", isPrivate);
        setArguments(bundle);
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        // Init
        mColumn = (Column) getArguments().getSerializable("COLUMN");
        mIsPrivate = (boolean) getArguments().getSerializable("PRIVATE");
        mScrollLoad = true;

        // Set view
        setRecyclerView(view);
        setRecyclerAdapter(new TweetRecyclerAdapter());
        setUltraPullToRefresh(view);
        mPtrFrame.setEnabled(false);

        return view;
    }

    protected void loadTweet(boolean isPullLoad, boolean isClickLoad) {
        // Check
        if (mIsPrivate) {
            mFooterText.setText(STR_ACCOUNT_PRIVATE);
            mFooterProgress.setVisibility(View.GONE);
            return;
        }

        // Change footer
        mFooterText.setText(STR_LOADING);
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);

        // Load timeline
        switch (mColumn.getType()) {
            case USER_TWEET:
            case USER_FAVORITE:
                loadUserTweetFavorite(isClickLoad);
                break;
            case USER_MEDIA:
                loadUserMedia(isClickLoad, 1);
                break;
        }
    }

    private void loadUserTweetFavorite(boolean isClickLoad) {
        // Create request
        TweetRecyclerAdapter adapter = (TweetRecyclerAdapter) mRecyclerAdapter;
        Observable<List<Status>> observable = Observable.create(e -> {
            try {
                Paging paging;
                if (adapter.getObjCount() == 0) {
                    paging = mFirstPaging;
                } else {
                    paging = mNextPaging;
                    paging.setMaxId(adapter.getLastObj().getId() - 1);
                }
                Twitter twitter = TweetMateApp.getTwitter();
                switch (mColumn.getType()) {
                    case USER_TWEET:
                        e.onNext(twitter.getUserTimeline(mColumn.getId(), paging));
                        break;
                    case USER_FAVORITE:
                        e.onNext(twitter.getFavorites(mColumn.getId(), paging));
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
                        mFooterText.setText(STR_TWEET_NONE);
                        mFooterProgress.setVisibility(View.GONE);
                    } else
                        mFooterView.setVisibility(View.GONE);
                } else {
                    // Success
                    for (Status status4j : statusList) {
                        adapter.add(new TwitterStatus(status4j));
                    }
                    adapter.notifyDataSetChanged();
                    mScrollLoad = true;
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
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable);
    }

    private void loadUserMedia(boolean isClickLoad, int count) {
        // Create request
        TweetRecyclerAdapter adapter = (TweetRecyclerAdapter) mRecyclerAdapter;
        Observable<List<Status>> observable = Observable.create(e -> {
            try {
                Paging paging;
                if (mLastTweetId == 0) {
                    paging = mFirstPaging;
                    paging.setCount(200);
                } else {
                    paging = mNextPaging;
                    paging.setCount(200);
                    paging.setMaxId(mLastTweetId - 1);
                }
                Twitter twitter = TweetMateApp.getTwitter();
                e.onNext(twitter.getUserTimeline(mColumn.getId(), paging));
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
                        mFooterText.setText(STR_TWEET_NONE);
                        mFooterProgress.setVisibility(View.GONE);
                    } else
                        mFooterView.setVisibility(View.GONE);
                } else {
                    // Success
                    boolean exist = false;
                    for (Status status4j : statusList) {
                        TwitterStatus status = new TwitterStatus(status4j);
                        if (!status.isRetweet() && status.isTwiMedia()) {
                            adapter.add(status);
                            exist = true;
                        }
                    }
                    mLastTweetId = statusList.get(statusList.size() - 1).getId();
                    if (exist) {
                        adapter.notifyDataSetChanged();
                        mScrollLoad = true;
                    } else {
                        if (count >= 4) {
                            mFooterText.setText(STR_TAP_TO_LOAD);
                            mFooterProgress.setVisibility(View.GONE);
                            mFooterClick = true;
                        } else {
                            loadUserMedia(isClickLoad, count + 1);
                        }
                    }
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
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable);
    }

}
