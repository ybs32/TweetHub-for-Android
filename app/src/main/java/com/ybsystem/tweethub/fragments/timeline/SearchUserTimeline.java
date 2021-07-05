package com.ybsystem.tweethub.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.adapters.recycler.UserRecyclerAdapter;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.utils.ExceptionUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class SearchUserTimeline extends TimelineBase {

    private int mPage;
    private String mSearchWord;

    public Fragment newInstance(String searchWord) {
        Bundle bundle = new Bundle();
        bundle.putString("SEARCH_WORD", searchWord);
        setArguments(bundle);
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        // Init
        mSearchWord = getArguments().getString("SEARCH_WORD");
        mScrollLoad = true;

        // Set view
        setRecyclerView(view);
        setRecyclerAdapter(new UserRecyclerAdapter());
        setUltraPullToRefresh(view);

        return view;
    }

    protected void loadTweet(final boolean isPullLoad, final boolean isClickLoad) {
        // Change footer
        mFooterText.setText("読み込み中...");
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);

        UserRecyclerAdapter adapter = (UserRecyclerAdapter) mRecyclerAdapter;
        Observable<ResponseList<User>> observable = Observable.create(e -> {
            try {
                if (isPullLoad || adapter.isEmpty()) {
                    mPage = 1;
                }
                e.onNext(TweetHubApp.getTwitter().searchUsers(mSearchWord, mPage));
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<ResponseList<User>> disposable = new DisposableObserver<ResponseList<User>>() {
            @Override
            public void onNext(ResponseList<User> userList) {
                if (userList.isEmpty()) {
                    // No tweet...
                    if (adapter.isEmpty()) {
                        mFooterText.setText("ユーザーなし");
                        mFooterProgress.setVisibility(View.GONE);
                    } else
                        mFooterView.setVisibility(View.GONE);
                } else {
                    // Success
                    if (isPullLoad) {
                        adapter.clear();
                    }
                    for (twitter4j.User user4j : userList) {
                        if (isDuplicated(user4j)) {
                            continue;
                        }
                        TwitterUser user = new TwitterUser(user4j);
                        setRelation(user);
                        adapter.add(user);
                    }
                    adapter.notifyDataSetChanged();

                    // Check next
                    if (userList.size() == 20) {
                        mPage++;
                        mScrollLoad = true;
                    } else
                        mFooterView.setVisibility(View.GONE);
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

            private boolean isDuplicated(User user) {
                for (int i = 0; i < adapter.getObjCount(); i++) {
                    if (adapter.getObj(i).getId() == user.getId()) {
                        return true;
                    }
                }
                return false;
            }

            private void setRelation(TwitterUser user) {
                TwitterUser myUser = TweetHubApp.getMyUser();
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
                .doFinally(() -> {
                    if (isPullLoad) {
                        mPtrFrame.refreshComplete();
                    }
                })
                .subscribe(disposable);
    }

}
