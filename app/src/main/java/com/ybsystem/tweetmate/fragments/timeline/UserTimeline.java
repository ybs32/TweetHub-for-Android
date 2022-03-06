package com.ybsystem.tweetmate.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.recycler.UserRecyclerAdapter;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.models.entities.Column;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.utils.ExceptionUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class UserTimeline extends TimelineBase {

    private Column mColumn;
    private long mCursor;

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
        setRecyclerAdapter(new UserRecyclerAdapter());
        setUltraPullToRefresh(view);
        mPtrFrame.setEnabled(false);

        return view;
    }

    public void loadTweet(final boolean isPullLoad, final boolean isClickLoad) {
        // Change footer
        mFooterText.setText("読み込み中...");
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);

        UserRecyclerAdapter adapter = (UserRecyclerAdapter) mRecyclerAdapter;
        Observable<PagableResponseList<User>> observable = Observable.create(e -> {
            try {
                if (adapter.getObjCount() == 0) {
                    mCursor = -1;
                }
                Twitter twitter = TweetMateApp.getTwitter();
                switch (mColumn.getType()) {
                    case FOLLOW:
                        e.onNext(twitter.getFriendsList(mColumn.getId(), mCursor, 200));
                        break;
                    case FOLLOWER:
                        e.onNext(twitter.getFollowersList(mColumn.getId(), mCursor, 200));
                        break;
                    case MUTE:
                        e.onNext(twitter.getMutesList(mCursor));
                        break;
                    case BLOCK:
                        e.onNext(twitter.getBlocksList(mCursor));
                        break;
                    case LIST_MEMBER:
                        e.onNext(twitter.getUserListMembers(mColumn.getId(), 200, mCursor));
                        break;
                    case LIST_SUBSCRIBER:
                        e.onNext(twitter.getUserListSubscribers(mColumn.getId(), 200, mCursor));
                        break;
                }
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<PagableResponseList<User>>
                disposable = new DisposableObserver<PagableResponseList<User>>() {
            @Override
            public void onNext(PagableResponseList<User> userList) {
                if (userList.isEmpty()) {
                    // No user...
                    if (adapter.isEmpty()) {
                        mFooterText.setText("ユーザーなし");
                        mFooterProgress.setVisibility(View.GONE);
                    } else
                        mFooterView.setVisibility(View.GONE);
                } else {
                    // Success
                    for (User user4j : userList) {
                        TwitterUser user = new TwitterUser(user4j);
                        setRelation(user);
                        adapter.add(user);
                    }
                    adapter.notifyDataSetChanged();
                    // Check next
                    if (userList.hasNext()) {
                        mCursor = userList.getNextCursor();
                        mScrollLoad = true;
                    } else {
                        mFooterView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                mFooterText.setText("タップして読み込む");
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

            private void setRelation(TwitterUser user) {
                TwitterUser myUser = TweetMateApp.getMyUser();
                for (long id : myUser.getFriendIds()) {
                    if (user.getId() == id) {
                        user.setFriend(true);
                    }
                }
                for (long id : myUser.getFollowerIds()) {
                    if (user.getId() == id) {
                        user.setFollower(true);
                    }
                }
                for (long id : myUser.getMuteIds()) {
                    if (user.getId() == id) {
                        user.setMuting(true);
                    }
                }
                for (long id : myUser.getBlockIds()) {
                    if (user.getId() == id) {
                        user.setBlocking(true);
                    }
                }
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable);
    }

}
