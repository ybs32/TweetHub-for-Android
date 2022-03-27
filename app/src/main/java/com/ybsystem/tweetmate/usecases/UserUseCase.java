package com.ybsystem.tweetmate.usecases;

import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.libs.eventbus.UserEvent;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;
import com.ybsystem.tweetmate.storages.PrefSystem;
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
import static com.ybsystem.tweetmate.resources.ResString.STR_SUCCESS_UNBLOCK;
import static com.ybsystem.tweetmate.resources.ResString.STR_SUCCESS_UNFOLLOW;
import static com.ybsystem.tweetmate.resources.ResString.STR_SUCCESS_UNMUTE;

public class UserUseCase {

    private UserUseCase() {
    }

    public static void follow(TwitterUser user) {
        // Create text
        boolean isFriend = user.isFriend();
        String confirm = isFriend ? STR_CONFIRM_UNFOLLOW : STR_CONFIRM_FOLLOW;
        String success = isFriend ? STR_SUCCESS_UNFOLLOW : STR_SUCCESS_FOLLOW;

        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isFriend) {
                    TweetMateApp.getTwitter().destroyFriendship(user.getId());
                } else {
                    TweetMateApp.getTwitter().createFriendship(user.getId());
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
                if (isFriend) {
                    user.setFriend(false);
                    TweetMateApp.getMyUser().getFriendIds().remove(user.getId());
                } else {
                    user.setFriend(true);
                    TweetMateApp.getMyUser().getFriendIds().add(user.getId());
                }
                // Notify event
                EventBus.getDefault().post(new UserEvent());
                ToastUtils.showShortToast(success);
            }
        };

        // Confirm
        if (PrefSystem.getConfirmSettings().contains(FOLLOW)) {
            DialogUtils.showConfirm(
                    confirm,
                    (dialog, which) -> observable.subscribe(disp)
            );
        } else {
            observable.subscribe(disp);
        }
    }

    public static void mute(TwitterUser user) {
        // Create text
        boolean isMuting = user.isMuting();
        String confirm = isMuting ? STR_CONFIRM_UNMUTE : STR_CONFIRM_MUTE;
        String success = isMuting ? STR_SUCCESS_UNMUTE : STR_SUCCESS_MUTE;

        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isMuting) {
                    TweetMateApp.getTwitter().destroyMute(user.getId());
                } else {
                    TweetMateApp.getTwitter().createMute(user.getId());
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
                if (isMuting) {
                    user.setMuting(false);
                    TweetMateApp.getMyUser().getMuteIds().remove(user.getId());
                } else {
                    user.setMuting(true);
                    TweetMateApp.getMyUser().getMuteIds().add(user.getId());
                }
                // Notify event
                EventBus.getDefault().post(new UserEvent());
                ToastUtils.showShortToast(success);
            }
        };

        // Confirm
        if (PrefSystem.getConfirmSettings().contains(MUTE)) {
            DialogUtils.showConfirm(
                    confirm,
                    (dialog, which) -> observable.subscribe(disp)
            );
        } else {
            observable.subscribe(disp);
        }
    }

    public static void block(TwitterUser user) {
        // Create text
        boolean isBlocking = user.isBlocking();
        String confirm = isBlocking ? STR_CONFIRM_UNBLOCK : STR_CONFIRM_BLOCK;
        String success = isBlocking ? STR_SUCCESS_UNBLOCK : STR_SUCCESS_BLOCK;

        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isBlocking) {
                    TweetMateApp.getTwitter().destroyBlock(user.getId());
                } else {
                    TweetMateApp.getTwitter().createBlock(user.getId());
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
                if (isBlocking) {
                    user.setBlocking(false);
                    TweetMateApp.getMyUser().getBlockIds().remove(user.getId());
                } else {
                    user.setBlocking(true);
                    user.setFriend(false);
                    TweetMateApp.getMyUser().getBlockIds().add(user.getId());
                    TweetMateApp.getMyUser().getFriendIds().remove(user.getId());
                }
                // Notify event
                EventBus.getDefault().post(new UserEvent());
                ToastUtils.showShortToast(success);
            }
        };

        // Confirm
        if (PrefSystem.getConfirmSettings().contains(BLOCK)) {
            DialogUtils.showConfirm(
                    confirm,
                    (dialog, which) -> observable.subscribe(disp)
            );
        } else {
            observable.subscribe(disp);
        }
    }

}
