package com.ybsystem.tweethub.activities.preference;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.ActivityBase;
import com.ybsystem.tweethub.fragments.preference.BrowserAuthFragment;

public class BrowserAuthActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);
        setBrowserAuthFragment();
    }

    private void setBrowserAuthFragment() {
        BrowserAuthFragment fragment = new BrowserAuthFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_container, fragment);
        transaction.commit();
    }
}
