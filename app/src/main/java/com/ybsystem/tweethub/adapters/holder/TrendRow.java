package com.ybsystem.tweethub.adapters.holder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.storages.PrefTheme;
import com.ybsystem.tweethub.usecases.ClickUseCase;
import com.ybsystem.tweethub.utils.ResourceUtils;

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

        // Apply color
        int color = PrefTheme.isCustomThemeEnabled()
                ? PrefTheme.getLinkColor()
                : ResourceUtils.getLinkColor();
        mTrendText.setTextColor(color);
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

}
