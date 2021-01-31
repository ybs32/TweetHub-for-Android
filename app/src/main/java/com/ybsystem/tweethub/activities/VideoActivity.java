package com.ybsystem.tweethub.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.models.entities.twitter.TwitterMediaEntity;
import com.ybsystem.tweethub.storages.PrefSystem;

public class VideoActivity extends ActivityBase {

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init
        TwitterMediaEntity media = (TwitterMediaEntity)
                getIntent().getSerializableExtra("MEDIA_ENTITY");

        // Set actionbar title
        switch (media.getType()) {
            case "video":
                getSupportActionBar().setTitle("ムービー");
                break;
            case "animated_gif":
                getSupportActionBar().setTitle("GIFアニメ");
                break;
        }

        // Set view
        setContentView(R.layout.activity_video);
        setVideoView(media);
    }

    private void setVideoView(TwitterMediaEntity media) {
        // Get video URL by setting
        String videoURL = PrefSystem.getVideoByQuality(media);

        // Start video
        mVideoView = findViewById(R.id.video_view);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setVideoURI(Uri.parse(videoURL));
        mVideoView.start();

        // When completed video
        mVideoView.setOnCompletionListener(mp -> {
            // Seek start position
            mVideoView.seekTo(0);

            // When GIF, then repeat
            if (media.getType().equals("animated_gif")) {
                mVideoView.start();
            }
        });
    }

}
