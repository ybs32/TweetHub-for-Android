package com.ybsystem.tweetmate.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.activities.PhotoActivity;
import com.ybsystem.tweetmate.activities.VideoActivity;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterMediaEntity;
import com.ybsystem.tweetmate.databases.PrefSystem;

import java.util.ArrayList;

public class ThumbnailClickListener implements View.OnClickListener {

    private final int mPosition;
    private final TwitterMediaEntity[] mMediaEntities;

    public ThumbnailClickListener(int position, TwitterMediaEntity[] mediaEntities){
        this.mPosition = position;
        this.mMediaEntities = mediaEntities;
    }

    @Override
    public void onClick(View view){
        // Prepare
        Intent intent;
        Activity act = (Activity) view.getContext();

        // Check media type
        String type = mMediaEntities[0].getType();
        if (type.equals("photo")) {
            // Photo
            ArrayList<String> imageURLs = new ArrayList<>();
            for (TwitterMediaEntity entity : mMediaEntities) {
                String url = PrefSystem.getMediaByQuality(entity.getMediaURLHttps());
                imageURLs.add(url);
            }
            intent = new Intent(act, PhotoActivity.class);
            intent.putExtra("TYPE", "MEDIA");
            intent.putExtra("PAGER_POSITION", mPosition);
            intent.putExtra("IMAGE_URLS", imageURLs);
        } else {
            // Video
            intent = new Intent(act, VideoActivity.class);
            intent.putExtra("MEDIA_ENTITY", mMediaEntities[0]);
        }

        // Start activity
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
    }

}