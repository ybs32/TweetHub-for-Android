package com.ybsystem.tweetmate.activities;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterMediaEntity;
import com.ybsystem.tweetmate.databases.PrefSystem;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class VideoActivity extends ActivityBase {

    // Video player
    private ExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private PlayerControlView mPlayerControlView;

    // Media
    private TwitterMediaEntity mMediaEntity;
    private MediaSource mMediaSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init
        mMediaEntity = (TwitterMediaEntity)
                getIntent().getSerializableExtra("MEDIA_ENTITY");

        // Create media
        Uri videoURI = Uri.parse(PrefSystem.getVideoByQuality(mMediaEntity));
        mMediaSource = new ProgressiveMediaSource
                .Factory(new DefaultHttpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(videoURI));

        // Set actionbar title
        switch (mMediaEntity.getType()) {
            case "video":
                getSupportActionBar().setTitle(STR_MOVIE);
                break;
            case "animated_gif":
                getSupportActionBar().setTitle(STR_GIF);
                break;
        }

        // Set view
        setContentView(R.layout.activity_video);
        setPlayerView();
        setPlayerListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
        }
    }

    private void setPlayerView() {
        // Create player
        mExoPlayer = new SimpleExoPlayer.Builder(this).build();
        mExoPlayer.setMediaSource(mMediaSource);
        mExoPlayer.prepare();

        // Set to playerView
        mPlayerView = findViewById(R.id.player_view);
        mPlayerView.setPlayer(mExoPlayer);

        // Set to controlView
        mPlayerControlView = findViewById(R.id.player_control_view);
        mPlayerControlView.setPlayer(mExoPlayer);

        // Start video and Hide control
        mExoPlayer.setPlayWhenReady(true);
        mPlayerControlView.hide();
    }

    private void setPlayerListener() {
        // When click video, toggle control visibility
        mPlayerView.getVideoSurfaceView().setOnClickListener(view -> {
            if (mPlayerControlView.isVisible()) {
                mPlayerControlView.hide();
            } else {
                mPlayerControlView.show();
            }
        });

        // When completed video
        mExoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch(playbackState) {
                    case ExoPlayer.STATE_ENDED:
                        // Seek to start
                        mExoPlayer.seekTo(0);
                        mExoPlayer.setPlayWhenReady(false);

                        // If GIF, then repeat
                        if (mMediaEntity.getType().equals("animated_gif")) {
                            mExoPlayer.setPlayWhenReady(true);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
