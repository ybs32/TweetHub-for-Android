package com.ybsystem.tweetmate.listeners;

import android.view.View;

import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterHashtagEntity;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterURLEntity;
import com.ybsystem.tweetmate.models.enums.ClickAction;
import com.ybsystem.tweetmate.usecases.ClickUseCase;
import com.ybsystem.tweetmate.usecases.StatusUseCase;

public class TweetClickListener implements View.OnClickListener, View.OnLongClickListener {

    private final TwitterStatus mStatus;
    private final TwitterStatus mSource;
    private final ClickAction mClickAction;

    public TweetClickListener(TwitterStatus status, ClickAction action) {
        this.mStatus = status;
        this.mSource = mStatus.isRetweet() ? mStatus.getRtStatus() : mStatus;
        this.mClickAction = action;
    }

    @Override
    public void onClick(View view){
        this.click();
    }

    @Override
    public boolean onLongClick(View view) {
        this.click();
        return true;
    }

    private void click() {

        switch (mClickAction) {
            case MENU:
                ClickUseCase.showTweetMenu(mStatus);
                break;
            case REPLY:
                ClickUseCase.reply(mSource);
                break;
            case RETWEET:
                StatusUseCase.retweet(mStatus);
                break;
            case QUOTE:
                ClickUseCase.quote(mSource);
                break;
            case LIKE:
                StatusUseCase.favorite(mStatus);
                break;
            case TALK:
                if (mSource.isTalk()
                        && !TweetMateApp.getActivity().getSupportActionBar().getTitle().equals("会話")
                        && !TweetMateApp.getActivity().getSupportActionBar().getTitle().equals("詳細")) {
                    ClickUseCase.showTalk(mSource);
                }
                break;
            case DELETE:
                if (mSource.isMyTweet() && !mStatus.isRetweet()) {
                    StatusUseCase.delete(mStatus);
                }
                break;
            case URL:
                TwitterURLEntity[] urls = mSource.getUrlEntities();
                if (urls != null && urls.length != 0) {
                    ClickUseCase.openURL(urls[0].getUrl());
                }
                break;
            case HASH_SEARCH:
                TwitterHashtagEntity[] hashSearch = mSource.getHashtagEntities();
                if (hashSearch != null && hashSearch.length != 0) {
                    ClickUseCase.searchWord("#" + hashSearch[0].getText());
                }
                break;
            case HASH_TWEET:
                TwitterHashtagEntity[] hashTweet = mSource.getHashtagEntities();
                if (hashTweet != null && hashTweet.length != 0) {
                    ClickUseCase.tweetWithSuffix("#" + hashTweet[0].getText());
                }
                break;
            case USER:
                ClickUseCase.showUser(mSource.getUser().getId());
                break;
            case COPY:
                ClickUseCase.copyText(mSource);
                break;
            case DETAIL:
                ClickUseCase.showDetail(mSource);
                break;
            case SHARE:
                ClickUseCase.share(mStatus);
                break;
            case TWITTER:
                ClickUseCase.openStatus(mStatus);
                break;
            case NONE:
                break;
        }

    }

}
