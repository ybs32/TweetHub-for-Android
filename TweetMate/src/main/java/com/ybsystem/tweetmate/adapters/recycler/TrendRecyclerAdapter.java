package com.ybsystem.tweetmate.adapters.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.adapters.holder.FooterRow;
import com.ybsystem.tweetmate.adapters.holder.TrendRow;
import com.ybsystem.tweetmate.libs.eventbus.FooterEvent;

import org.greenrobot.eventbus.EventBus;

import twitter4j.Trend;

public class TrendRecyclerAdapter extends RecyclerAdapterBase<Trend> {

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
            View view = inflater.inflate(R.layout.recycler_item_trend, parent, false);
            return new TrendRow(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Check instance
        if (holder instanceof TrendRow) {
            TrendRow row = (TrendRow) holder;
            Trend trend = mObjList.get(position);
            this.renderTrend(row, trend);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position == getItemCount() - 1) {
            return -1;
        } else {
            return mObjList.get(position).getURL().hashCode();
        }
    }

    private void renderTrend(TrendRow trendRow, Trend trend) {
        // Set fields
        trendRow.setTrend(trend);
        trendRow.setTrendText();
        trendRow.setTrendClickListener();
    }

}
