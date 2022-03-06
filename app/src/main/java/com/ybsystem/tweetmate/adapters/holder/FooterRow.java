package com.ybsystem.tweetmate.adapters.holder;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweetmate.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FooterRow extends RecyclerView.ViewHolder {

    @BindView(R.id.progressbar_footer) ProgressBar mFooterProgress;
    @BindView(R.id.text_footer) TextView mFooterText;

    public FooterRow(View itemView) {
        super(itemView);

        // ButterKnife
        ButterKnife.bind(this, itemView);
    }

}
