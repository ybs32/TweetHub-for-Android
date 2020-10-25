package com.ybsystem.tweethub.libs.rfab;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.contentimpl.viewbase.RapidFloatingActionContentViewBase;
import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.utils.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

public class RapidFloatingActionListView extends RapidFloatingActionContentViewBase
        implements View.OnClickListener {

    // ##### Ingerface
    public interface OnRfaListViewListener {
        void onItemClick(int position);
    }

    // ##### Listener
    private OnRfaListViewListener onRfaViewListener;

    public void setOnRfaListViewListener(OnRfaListViewListener onRfaListViewListener) {
        this.onRfaViewListener = onRfaListViewListener;
    }

    // ##### Constructor
    public RapidFloatingActionListView(Context context) {
        super(context);
    }

    public RapidFloatingActionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RapidFloatingActionListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ##### MyFields
    private LinearLayout mContentView;

    private List<CardItem> mCardList = new ArrayList<>();

    public void setList(List<CardItem> list) {
        this.mCardList.clear();
        this.mCardList.addAll(list);
    }

    @Override
    public void onExpandAnimator(AnimatorSet animatorSet) {
        animatorSet.setDuration(200);
        super.onExpandAnimator(animatorSet);

        // Fadeout tweet fab
        Activity activity = (Activity) getContext();
        FloatingActionButton fab = activity.findViewById(R.id.fab);
        AnimationUtils.fadeOut(fab, 200);
    }

    @Override
    public void onCollapseAnimator(AnimatorSet animatorSet) {
        animatorSet.setDuration(200);
        super.onCollapseAnimator(animatorSet);

        // Fadein tweet fab
        Activity activity = (Activity) getContext();
        FloatingActionButton fab = activity.findViewById(R.id.fab);
        AnimationUtils.fadeIn(fab, 200);
    }

    @NonNull
    @Override
    protected View getContentView() {
        mContentView = new LinearLayout(getContext());
        mContentView.setOrientation(LinearLayout.VERTICAL);
        mContentView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        for (int i = 0, len = mCardList.size(); i < len; i++) {
            View item = LayoutInflater.from(getContext()).inflate(R.layout.list_item_common, (ViewGroup) null);
            View rootView = item.findViewById(R.id.linear_item);
            rootView.setTag(com.wangjie.rapidfloatingactionbutton.R.id.rfab__id_content_label_list_item_position, i);
            rootView.setOnClickListener(this);

            ImageView logoIv = rootView.findViewById(R.id.image_item);
            TextView titleTv = rootView.findViewById(R.id.text_item);
            CardItem cardItem = mCardList.get(i);
            logoIv.setImageResource(cardItem.getResId());
            titleTv.setText(cardItem.getName());

            // Change layout margin
            MarginLayoutParams logoIvMargin = (MarginLayoutParams) logoIv.getLayoutParams();
            MarginLayoutParams titleTvMargin = (MarginLayoutParams) titleTv.getLayoutParams();
            float scale = TweetHubApp.getInstance().getResources().getDisplayMetrics().density;
            logoIvMargin.setMargins((int) (20 * scale), 0, 0, 0);
            titleTvMargin.setMargins((int) (20 * scale), 0, (int) (30 * scale), 0);
            logoIv.setLayoutParams(logoIvMargin);
            titleTv.setLayoutParams(titleTvMargin);

            mContentView.addView(item);
        }

        return mContentView;
    }

    @Override
    public void onClick(View v) {
        Integer position = (Integer) v.getTag(
                com.wangjie.rapidfloatingactionbutton.R.id.rfab__id_content_label_list_item_position
        );
        if (onRfaViewListener == null || position == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.linear_item:
                onRfaViewListener.onItemClick(position);
                break;
        }
    }

}
