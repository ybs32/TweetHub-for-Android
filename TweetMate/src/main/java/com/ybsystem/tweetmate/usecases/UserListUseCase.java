package com.ybsystem.tweetmate.usecases;

import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.libs.eventbus.UserListEvent;
import com.ybsystem.tweetmate.models.entities.UserList;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUserList;
import com.ybsystem.tweetmate.databases.PrefSystem;
import com.ybsystem.tweetmate.utils.DialogUtils;
import com.ybsystem.tweetmate.utils.ExceptionUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.TwitterException;

import static com.ybsystem.tweetmate.models.enums.ConfirmAction.*;
import static com.ybsystem.tweetmate.resources.ResString.*;
import static com.ybsystem.tweetmate.resources.ResString.STR_SUCCESS_UNSUBSCRIBE;

public class UserListUseCase {

    private UserListUseCase() {
    }

    public static void subscribeList(TwitterUserList usetList) {
        // Create text
        boolean isSubscribing = usetList.isFollowing();
        String confirm = isSubscribing ? STR_CONFIRM_UNSUBSCRIBE : STR_CONFIRM_SUBSCRIBE_LIST;
        String success = isSubscribing ? STR_SUCCESS_UNSUBSCRIBE : STR_SUCCESS_SUBSCRIBE_LIST;

        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isSubscribing) {
                    TweetMateApp.getTwitter().destroyUserListSubscription(usetList.getId());
                } else {
                    TweetMateApp.getTwitter().createUserListSubscription(usetList.getId());
                }
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread());

        // Result
        DisposableObserver<Object> disp = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object obj) {
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
            }

            @Override
            public void onComplete() {
                // Success
                if (isSubscribing) {
                    usetList.setFollowing(false);
                } else {
                    usetList.setFollowing(true);
                }
                // Notify event
                EventBus.getDefault().post(new UserListEvent());
                ToastUtils.showShortToast(success);
            }
        };

        // Confirm
        if (PrefSystem.getConfirmSettings().contains(SUBSCRIBE)) {
            DialogUtils.showConfirm(
                    confirm,
                    (dialog, which) -> observable.subscribe(disp)
            );
        } else {
            observable.subscribe(disp);
        }
    }

    public static void updateListUser(UserList list, TwitterUser user, boolean isRegistering) {
        // Create text
        String success = isRegistering ? STR_SUCCESS_DESTROY_LIST : STR_SUCCESS_ADD_LIST;

        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isRegistering) {
                    TweetMateApp.getTwitter().destroyUserListMember(list.getId(), user.getId());
                } else {
                    TweetMateApp.getTwitter().createUserListMember(list.getId(), user.getId());
                }
                e.onComplete();
            } catch (TwitterException ex) {
                e.onError(ex);
            }
        });

        // Result
        DisposableObserver<Object> disposable = new DisposableObserver<Object>() {
            @Override
            public void onNext(Object obj) {
            }

            @Override
            public void onError(Throwable t) {
                // Failed...
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
            }

            @Override
            public void onComplete() {
                // Success
                ToastUtils.showShortToast(success);
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposable);
    }

}
