package com.ybsystem.tweethub.activities.preference;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.fragment.app.FragmentTransaction;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.activities.ActivityBase;
import com.ybsystem.tweethub.fragments.preference.WallpaperFragment;
import com.ybsystem.tweethub.storages.PrefWallpaper;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;

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
                ToastUtils.showShortToast("エラーが発生しました...");
                ToastUtils.showShortToast("画像の取得に失敗しました。");
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
        DialogUtils.showProgressDialog("設定を適用中...", this);
        new Handler().postDelayed(() -> {
            DialogUtils.dismissProgressDialog();
            setResult(REBOOT_IMMEDIATE);
            finish();
        }, 1500);
    }

}
