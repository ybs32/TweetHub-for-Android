package com.ybsystem.tweethub.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.adapters.recycler.TweetRecyclerAdapter;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.models.entities.EntityArray;
import com.ybsystem.tweethub.models.entities.UserList;
import com.ybsystem.tweethub.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweethub.utils.ExceptionUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class MainListTimeline extends TimelineBase {

    private long mSelectedListId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.fragment_timeline_list, container, false);

        // Set contents
        setRecyclerView(view);
        setRecyclerAdapter(new TweetRecyclerAdapter());
        setUltraPullToRefresh(view);

        // Fetch my list
        fetchMyList(view);

        return view;
    }

    protected void loadTweet(final boolean isPullLoad, final boolean isClickLoad) {
        // Change footer
        mFooterText.setText("読み込み中...");
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);

        // Connect async
        TweetRecyclerAdapter adapter = (TweetRecyclerAdapter) mRecyclerAdapter;
        Observable<List<Status>> observable = Observable.create(e -> {
            try {
                Paging paging;
                if (isPullLoad || adapter.getObjCount() == 0) {
                    paging = mFirstPaging;
                } else {
                    paging = mNextPaging;
                    paging.setMaxId(adapter.getLastObj().getId() - 1);
                }
                Twitter twitter = TweetHubApp.getTwitter();
                e.onNext(twitter.getUserListStatuses(mSelectedListId, paging));
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<List<Status>> disposable = new DisposableObserver<List<Status>>() {
            @Override
            public void onNext(List<Status> statusList) {
                if (statusList.size() == 0) {
                    // No tweet...
                    if (adapter.getObjCount() == 0) {
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
                    TwitterException e = (TwitterException) t;
                    ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(e));
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

    private void fetchMyList(View view) {
        // Connect async
        Observable<ResponseList<twitter4j.UserList>> observable = Observable.create(e -> {
            try {
                ResponseList<twitter4j.UserList> lists;
                Twitter twitter = TweetHubApp.getTwitter();
                lists = twitter.getUserLists(twitter.getScreenName());
                if (lists != null) {
                    e.onNext(lists);
                }
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        DisposableObserver<ResponseList<twitter4j.UserList>> disposable =
                new DisposableObserver<ResponseList<twitter4j.UserList>>() {
            @Override
            public void onNext(ResponseList<twitter4j.UserList> lists) {
                // Clear list
                ArrayList<UserList> listArray = TweetHubApp.getMyAccount().getLists();
                listArray.clear();

                // If user has no list
                if (lists.size() == 0) {
                    processError(view);
                    return;
                }
                // Save list
                for (twitter4j.UserList list : lists) {
                    listArray.add(
                            new UserList(list.getId(), list.getName())
                    );
                }
                // Set pull down
                setDropDownMenu(view);
            }

            @Override
            public void onError(Throwable t) {
                // Failed... Use cached data
                ArrayList<UserList> listArray = TweetHubApp.getMyAccount().getLists();
                if (listArray.size() != 0) {
                    setDropDownMenu(view);
                } else {
                    processError(view);
                }
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

    private void setDropDownMenu(View view) {
        // Get my twitter list
        EntityArray<UserList> listArray = TweetHubApp.getMyAccount().getLists();

        // Create dropdown items
        final String[] items = new String[listArray.size()];
        for (int i = 0; i < listArray.size(); i++) {
            items[i] = listArray.get(i).getName();
        }

        // Set to spinner
        Spinner dropDown = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item, items
        );
        dropDown.setAdapter(adapter);

        // Set listener
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Disable
                mScrollLoad = false;

                // Clear timeline
                TweetRecyclerAdapter adapter = (TweetRecyclerAdapter) mRecyclerAdapter;
                adapter.clear();

                // Load timeline
                mSelectedListId = listArray.get(position).getId();
                loadTweet(true, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void processError(View view) {
        // Set footer
        setFooterView();

        //　Change footer text
        mFooterText.setText("リストなし");
        mFooterProgress.setVisibility(View.GONE);

        // Disable footer click
        mFooterView.setOnClickListener(null);

        // Disable pull to refresh
        mPtrFrame.setEnabled(false);

        // Hide dropdown
        Spinner dropDown = view.findViewById(R.id.spinner);
        dropDown.setVisibility(View.GONE);
    }

}
