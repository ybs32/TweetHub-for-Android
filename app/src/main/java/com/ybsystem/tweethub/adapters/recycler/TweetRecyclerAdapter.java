package com.ybsystem.tweethub.adapters.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.libs.eventbus.FooterEvent;
import com.ybsystem.tweethub.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweethub.adapters.holder.FooterRow;
import com.ybsystem.tweethub.adapters.holder.TweetRow;
import com.ybsystem.tweethub.storages.PrefAppearance;
import com.ybsystem.tweethub.storages.PrefTheme;

import org.greenrobot.eventbus.EventBus;

public class TweetRecyclerAdapter extends RecyclerAdapterBase<TwitterStatus> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Check view type
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_FOOTER) {
            mFooterView = inflater.inflate(R.layout.recycler_item_footer, null);
            EventBus.getDefault().post(new FooterEvent());
            return new FooterRow(mFooterView);
        } else {
            View view = inflater.inflate(R.layout.recycler_item_tweet, null);
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
        // Hide optional fields
        tweetRow.hideOptionalFields();

        // Set fields
        tweetRow.setStatus(status);
        tweetRow.setUserName();
        tweetRow.setScreenName();
        tweetRow.setUserIcon();
        tweetRow.setTweetText();
        tweetRow.setRelativeTime();
        tweetRow.setQuoteTweet();
        tweetRow.setDetailTweet();
        tweetRow.setMarks();

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

        // Check theme setting
        if (PrefTheme.isCustomThemeEnabled()) {
            tweetRow.setCustomBackgroundColor();
        }
    }

}
