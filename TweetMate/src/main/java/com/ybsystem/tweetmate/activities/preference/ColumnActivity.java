package com.ybsystem.tweetmate.activities.preference;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.activities.ActivityBase;
import com.ybsystem.tweetmate.fragments.preference.ColumnFragment;

public class ColumnActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preference);
        setColumnFragment();
    }

    private void setColumnFragment() {
        ColumnFragment fragment = new ColumnFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_container, fragment);
        transaction.commit();
    }

}
