package com.ybsystem.tweetmate.adapters.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.libs.eventbus.FooterEvent;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweetmate.adapters.holder.FooterRow;
import com.ybsystem.tweetmate.adapters.holder.TweetRow;
import com.ybsystem.tweetmate.databases.PrefAppearance;

import org.greenrobot.eventbus.EventBus;

public class TweetRecyclerAdapter extends RecyclerAdapterBase<TwitterStatus> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflater
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Check type
        if (viewType == TYPE_FOOTER) {
            mFooterView = inflater.inflate(R.layout.recycler_item_footer, parent, false);
            EventBus.getDefault().post(new FooterEvent());
            return new FooterRow(mFooterView);
        } else {
            View view = inflater.inflate(R.layout.recycler_item_tweet, parent, false);
            return new TweetRow(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Check instance
        if (holder instanceof TweetRow) {
            TweetRow row = (TweetRow) holder;
            TwitterStatus status = mObjList.get(position);
            this.renderTweet(row, status);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position == getItemCount() - 1) {
            return -1;
        } else {
            return mObjList.get(position).getId();
        }
    }

    private void renderTweet(TweetRow tweetRow, TwitterStatus status) {
        // Set fields
        tweetRow.initVisibilities();
        tweetRow.setStatus(status);
        tweetRow.setUserName();
        tweetRow.setScreenName();
        tweetRow.setUserIcon();
        tweetRow.setTweetText();
        tweetRow.setRelativeTime();
        tweetRow.setMarks();
        tweetRow.setQuoteTweet();
        tweetRow.setDetailTweet();
        tweetRow.setBackgroundColor();
        tweetRow.setTweetClickListener();

        // Check detail
        if (status.isDetail()) {
            tweetRow.setAbsoluteTime();
            tweetRow.setThumbnail();
            tweetRow.setVia();
            tweetRow.setRetweetedBy();
        } else {
            // Check appearance setting
             if (PrefAppearance.isShowAbsoluteTime()) {
                 tweetRow.setAbsoluteTime();
             }
            if (PrefAppearance.isShowThumbnail()) {
                tweetRow.setThumbnail();
            }
            if (PrefAppearance.isShowVia()) {
                tweetRow.setVia();
            }
            if (PrefAppearance.isShowRtFavCount()) {
                tweetRow.setRtFavCount();
            }
            if (PrefAppearance.isShowRetweetedBy()) {
                tweetRow.setRetweetedBy();
            }
        }
    }

}
