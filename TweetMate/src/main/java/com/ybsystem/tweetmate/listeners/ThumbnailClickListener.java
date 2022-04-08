package com.ybsystem.tweetmate.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.activities.PhotoActivity;
import com.ybsystem.tweetmate.activities.VideoActivity;
import com.ybsystem.tweetmate.databases.PrefSystem;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterStatus;

import java.util.ArrayList;

public class ThumbnailClickListener implements View.OnClickListener {

    private final int mPosition;
    private final TwitterStatus mStatus;

    public ThumbnailClickListener(int position, TwitterStatus status){
        this.mPosition = position;
        this.mStatus = status;
    }

    @Override
    public void onClick(View view){
        // Prepare
        Intent intent;
        Activity act = (Activity) view.getContext();

        // Check media type
        if (!mStatus.isVideo()) {
            // Photo
            ArrayList<String> imageURLs = new ArrayList<>();
            for (String imageUrl : mStatus.getImageUrls()) {
                String url = PrefSystem.getMediaByQuality(imageUrl, mStatus.isThirdMedia());
                imageURLs.add(url);
            }
            intent = new Intent(act, PhotoActivity.class);
            intent.putExtra("TYPE", "MEDIA");
            intent.putExtra("PAGER_POSITION", mPosition);
            intent.putExtra("IMAGE_URLS", imageURLs);
            intent.putExtra("THIRD_MEDIA", mStatus.isThirdMedia());
        } else {
            // Video
            intent = new Intent(act, VideoActivity.class);
            intent.putExtra("MEDIA_ENTITY", mStatus.getMediaEntities()[0]);
        }

        // Start activity
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.fade_in_from_bottom, R.anim.none);
    }

}
