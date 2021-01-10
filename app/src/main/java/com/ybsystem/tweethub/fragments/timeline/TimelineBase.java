package com.ybsystem.tweethub.fragments.timeline;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.adapters.recycler.RecyclerAdapterBase;
import com.ybsystem.tweethub.libs.eventbus.FooterEvent;
import com.ybsystem.tweethub.libs.eventbus.StatusEvent;
import com.ybsystem.tweethub.libs.eventbus.UserEvent;
import com.ybsystem.tweethub.models.entities.twitter.TwitterStatus;
import com.ybsystem.tweethub.storages.PrefSystem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import twitter4j.Paging;

import static com.ybsystem.tweethub.adapters.recycler.RecyclerAdapterBase.*;

public abstract class TimelineBase extends Fragment {
    // RecyclerView
    protected RecyclerView mRecyclerView;
    protected RecyclerAdapterBase mRecyclerAdapter;

    // PullToRefresh
    protected PtrClassicFrameLayout mPtrFrame;

    // Footer
    protected View mFooterView;
    protected TextView mFooterText;
    protected ProgressBar mFooterProgress;

    // Flags
    protected boolean mScrollLoad;
    protected boolean mFooterClick;

    // Paging
    protected Paging mFirstPaging;
    protected Paging mNextPaging;

    protected abstract void loadTweet(boolean isPullLoad, boolean isClickLoad);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirstPaging = new Paging();
        mFirstPaging.setPage(1);
        mFirstPaging.setCount(PrefSystem.getBootLoadCount());

        mNextPaging = new Paging();
        mNextPaging.setCount(PrefSystem.getScrollLoadCount());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(FooterEvent event) {
        if (mRecyclerAdapter.getFooterView() != null) {
            setFooterView();
        }
    }

    @Subscribe
    public void onEvent(UserEvent event) {
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onEvent(StatusEvent event) {
        TwitterStatus status = event.getStatus();
        if (status != null) mRecyclerAdapter.remove(status);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected void setRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_FOOTER, 128);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_ITEM, 128);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // When scrolling to the end of timeline
                if (mScrollLoad
                        && !recyclerView.canScrollVertically(1)) {
                    // Load timeline
                    mScrollLoad = false;
                    loadTweet(false, false);
                }
            }
        });
    }

    protected void setRecyclerAdapter(RecyclerAdapterBase adapter) {
        mRecyclerAdapter = adapter;
        mRecyclerAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    protected void setUltraPullToRefresh(View view) {
        mPtrFrame = view.findViewById(R.id.ptr_classic_frame_layout);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPtrFrame.postDelayed(() ->
                        loadTweet(true, false), 0);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
    }

    protected void setFooterView() {
        mFooterView = mRecyclerAdapter.getFooterView();
        mFooterText = mFooterView.findViewById(R.id.text_footer);
        mFooterProgress = mFooterView.findViewById(R.id.progressbar_footer);

        mFooterView.setOnClickListener(v -> {
            if (mFooterClick) {
                // Change text
                mFooterText.setText("読み込み中...");
                mFooterProgress.setVisibility(View.VISIBLE);

                // Load timeline
                mFooterClick = false;
                loadTweet(false, true);
            }
        });
    }

}
