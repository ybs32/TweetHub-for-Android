package com.ybsystem.tweetmate.adapters.holder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.storages.PrefTheme;
import com.ybsystem.tweetmate.usecases.ClickUseCase;

import butterknife.BindView;
import butterknife.ButterKnife;
import twitter4j.Trend;

public class TrendRow extends RecyclerView.ViewHolder {
    // Trend
    private Trend mTrend;

    // Mandatory
    @BindView(R.id.text_trend) TextView mTrendText;
    @BindView(R.id.relative_click) RelativeLayout mClickContainer;

    public TrendRow(View itemView) {
        super(itemView);

        // ButterKnife
        ButterKnife.bind(this, itemView);

        // Apply app settings
        applyThemeSetting();
    }

    public void setTrend(Trend trend) {
        mTrend = trend;
    }

    public void setTrendText() {
        mTrendText.setText(mTrend.getName());
    }

    public void setTrendClickListener() {
        mClickContainer.setOnClickListener(
                view -> ClickUseCase.searchWord(mTrend.getName())
        );
    }

    private void applyThemeSetting() {
        // Check
        if (!PrefTheme.isCustomThemeEnabled()) {
            return;
        }
        // Set trend color
        mTrendText.setTextColor(PrefTheme.getLinkColor());
    }

}
