package com.ybsystem.tweetmate.activities.preference;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.activities.ActivityBase;
import com.ybsystem.tweetmate.fragments.preference.WallpaperFragment;
import com.ybsystem.tweetmate.databases.PrefWallpaper;
import com.ybsystem.tweetmate.utils.DialogUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import static com.ybsystem.tweetmate.activities.preference.SettingActivity.*;
import static com.ybsystem.tweetmate.resources.ResString.*;

public class WallpaperActivity extends ActivityBase {

    public static final int REQUEST_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preference);
        setWallpaperFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Prepare reboot application
        setResult(REBOOT_PREPARATION);

        // Return from gallery
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            try {
                setWallpaper(data);
            } catch (Exception e) {
                // Failed...
                ToastUtils.showShortToast(STR_FAIL_LOAD);
            }
        }
    }

    private void setWallpaperFragment() {
        WallpaperFragment fragment = new WallpaperFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_container, fragment);
        transaction.commit();
    }

    private void setWallpaper(Intent data) {
        // Get image path
        Uri uri = data.getData();
        String[] columns = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uri, columns, null, null, null);
        c.moveToFirst();

        // Save image path
        PrefWallpaper.saveWallpaperPath(c.getString(0));
        c.close();

        // Finish activity
        DialogUtils.showProgress(STR_APPLYING, this);
        new Handler().postDelayed(() -> {
            DialogUtils.dismissProgress();
            setResult(REBOOT_IMMEDIATE);
            finish();
        }, 1500);
    }

}
