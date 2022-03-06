package com.ybsystem.tweetmate.activities.preference;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweetmate.activities.ActivityBase;
import com.ybsystem.tweetmate.fragments.preference.BrowserAuthFragment;

public class BrowserAuthActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBrowserAuthFragment();
    }

    private void setBrowserAuthFragment() {
        BrowserAuthFragment fragment = new BrowserAuthFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, fragment);
        transaction.commit();
    }
}
