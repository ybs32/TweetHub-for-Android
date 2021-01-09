package com.ybsystem.tweethub.activities.preference;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.ActivityBase;
import com.ybsystem.tweethub.fragments.preference.AccountFragment;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;

public class AccountActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preference);
        setAccountFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == REBOOT_IMMEDIATE) {
            setResult(REBOOT_IMMEDIATE);
            finish();
        }
    }

    private void setAccountFragment() {
        AccountFragment fragment = new AccountFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_container, fragment);
        transaction.commit();
    }

}
