package com.ybsystem.tweethub.usecases;

import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.libs.eventbus.UserEvent;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.storages.PrefSystem;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.ExceptionUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import twitter4j.TwitterException;

import static com.ybsystem.tweethub.models.enums.ConfirmAction.*;

public class UserUseCase {

    private UserUseCase() {
    }

    public static void follow(TwitterUser user) {
        // Create text
        boolean isFriend = user.isFriend();
        String confirm = isFriend ? "フォローを解除しますか？" : "フォローしますか？";
        String success = isFriend ? "フォローを解除しました。" : "フォローしました。";
        String fail = isFriend ? "フォロー解除に失敗しました..." : "フォローに失敗しました...";

        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isFriend) {
                    TweetHubApp.getTwitter().destroyFriendship(user.getId());
                } else {
                    TweetHubApp.getTwitter().createFriendship(user.getId());
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
                ToastUtils.showShortToast(fail);
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
            }

            @Override
            public void onComplete() {
                // Success
                if (isFriend) {
                    user.setFriend(false);
                    TweetHubApp.getMyUser().getFriendIds().remove(user.getId());
                } else {
                    user.setFriend(true);
                    TweetHubApp.getMyUser().getFriendIds().add(user.getId());
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
        String confirm = isMuting ? "ミュートを解除しますか？" : "ミュートしますか？";
        String success = isMuting ? "ミュートを解除しました。" : "ミュートしました。";
        String fail = isMuting ? "ミュート解除に失敗しました..." : "ミュートに失敗しました...";

        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isMuting) {
                    TweetHubApp.getTwitter().destroyMute(user.getId());
                } else {
                    TweetHubApp.getTwitter().createMute(user.getId());
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
                ToastUtils.showShortToast(fail);
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
            }

            @Override
            public void onComplete() {
                // Success
                if (isMuting) {
                    user.setMuting(false);
                    TweetHubApp.getMyUser().getMuteIds().remove(user.getId());
                } else {
                    user.setMuting(true);
                    TweetHubApp.getMyUser().getMuteIds().add(user.getId());
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
        String confirm = isBlocking ? "ブロックを解除しますか？" : "ブロックしますか？";
        String success = isBlocking ? "ブロックを解除しました。" : "ブロックしました。";
        String fail = isBlocking ? "ブロック解除に失敗しました..." : "ブロックに失敗しました...";

        // Async
        Observable<Object> observable = Observable.create(e -> {
            try {
                if (isBlocking) {
                    TweetHubApp.getTwitter().destroyBlock(user.getId());
                } else {
                    TweetHubApp.getTwitter().createBlock(user.getId());
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
                ToastUtils.showShortToast(fail);
                ToastUtils.showShortToast(ExceptionUtils.getErrorMessage(t));
            }

            @Override
            public void onComplete() {
                // Success
                if (isBlocking) {
                    user.setBlocking(false);
                    TweetHubApp.getMyUser().getBlockIds().remove(user.getId());
                } else {
                    user.setBlocking(true);
                    user.setFriend(false);
                    TweetHubApp.getMyUser().getBlockIds().add(user.getId());
                    TweetHubApp.getMyUser().getFriendIds().remove(user.getId());
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
