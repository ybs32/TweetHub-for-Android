package com.ybsystem.tweethub.activities.preference;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.ActivityBase;
import com.ybsystem.tweethub.fragments.preference.SettingFragment;

public class SettingActivity extends ActivityBase {

    public static final int REBOOT_IMMEDIATE = 1;
    public static final int REBOOT_PREPARATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preference);
        setSettingFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case REBOOT_IMMEDIATE:
                setResult(REBOOT_IMMEDIATE);
                finish();
                break;
            case REBOOT_PREPARATION:
                setResult(REBOOT_PREPARATION);
                break;
            default:
                break;
        }
    }

    private void setSettingFragment() {
        SettingFragment fragment = new SettingFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_container, fragment);
        transaction.commit();
    }
}
