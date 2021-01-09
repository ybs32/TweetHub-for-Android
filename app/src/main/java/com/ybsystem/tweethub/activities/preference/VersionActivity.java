package com.ybsystem.tweethub.activities.preference;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.ActivityBase;
import com.ybsystem.tweethub.fragments.preference.VersionFragment;

public class VersionActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preference);
        setVersionFragment();
    }

    private void setVersionFragment() {
        VersionFragment fragment = new VersionFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_container, fragment);
        transaction.commit();
    }

}
