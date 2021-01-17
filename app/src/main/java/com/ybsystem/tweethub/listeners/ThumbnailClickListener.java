package com.ybsystem.tweethub.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.PhotoActivity;
import com.ybsystem.tweethub.activities.VideoActivity;
import com.ybsystem.tweethub.models.entities.twitter.TwitterMediaEntity;
import com.ybsystem.tweethub.storages.PrefSystem;

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
        String mediaType = mMediaEntities[0].getType();
        if (mediaType.equals("photo")) {
            // Photo
            ArrayList<String> imageURLs = new ArrayList<>();
            for (TwitterMediaEntity entity : mMediaEntities) {
                String url = PrefSystem.getMediaByQuality(entity.getMediaURL());
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
