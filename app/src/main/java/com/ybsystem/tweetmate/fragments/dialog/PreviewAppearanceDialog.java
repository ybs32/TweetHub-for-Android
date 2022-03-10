package com.ybsystem.tweetmate.fragments.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.fragments.timeline.ProfileTimeline;
import com.ybsystem.tweetmate.models.entities.Column;

import static com.ybsystem.tweetmate.models.enums.ColumnType.USER_TWEET;

public class PreviewAppearanceDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        View view = inflater.inflate(R.layout.dialog_preview_appearance, null);

        // Declare
        long TwitterJpUserId = 7080152L;
        boolean isPrivate = false;

        // Create timeline
        Fragment fragment = new ProfileTimeline().newInstance(
                new Column(TwitterJpUserId, "", USER_TWEET, false),
                isPrivate
        );

        // Set timeline
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.frame_container, fragment);
        ft.commit();

        return view;
    }

}
