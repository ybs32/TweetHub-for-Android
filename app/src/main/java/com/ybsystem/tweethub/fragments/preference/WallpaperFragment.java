package com.ybsystem.tweethub.fragments.preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceScreen;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.storages.PrefWallpaper;
import com.ybsystem.tweethub.utils.DialogUtils;
import com.ybsystem.tweethub.utils.StorageUtils;
import com.ybsystem.tweethub.utils.ToastUtils;

import static com.ybsystem.tweethub.activities.preference.SettingActivity.*;
import static com.ybsystem.tweethub.activities.preference.WallpaperActivity.*;
import static com.ybsystem.tweethub.storages.PrefWallpaper.*;

public class WallpaperFragment extends PreferenceFragmentBase {

    // Preferences
    private PreferenceScreen mAddWallpaper, mDeleteWallpaper;
    private ListPreference mTransparency;

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {

        setPreferencesFromResource(R.xml.preference_wallpaper, rootKey);

        mAddWallpaper = findPreference(KEY_ADD_WALLPAPER);
        mAddWallpaper.setSummary(PrefWallpaper.getWallpaperPath());
        mAddWallpaper.setOnPreferenceClickListener(preference -> {
            // Check permission
            if (!StorageUtils.isPermitted(getActivity())) {
                StorageUtils.requestPermission(getActivity());
                return false;
            }
            intentToGallery();
            return true;
        });

        mDeleteWallpaper = findPreference(KEY_DELETE_WALLPAPER);
        mDeleteWallpaper.setOnPreferenceClickListener(preference -> {
            // Check wallpaper
            if (PrefWallpaper.getWallpaperPath().equals("未設定")) {
                ToastUtils.showShortToast("壁紙は設定されていません。");
                return false;
            }
            showConfirmDialog();
            return true;
        });

        mTransparency = findPreference(KEY_WALLPAPER_TRANSPARENCY);
        mTransparency.setSummary(mTransparency.getEntry());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Prepare reboot
        getActivity().setResult(REBOOT_PREPARATION);

        // When setting changed
        if (key.equals(KEY_WALLPAPER_TRANSPARENCY)) {
            mTransparency.setSummary(mTransparency.getEntry());
        }
    }

    private void intentToGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        getActivity().startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void showConfirmDialog() {
        // Show
        DialogUtils.showConfirm(
                "壁紙を解除しますか？",
                (dialog, which) -> {
                    getActivity().setResult(REBOOT_PREPARATION);
                    PrefWallpaper.saveWallpaperPath("未設定");
                    mAddWallpaper.setSummary(PrefWallpaper.getWallpaperPath());
                    ToastUtils.showShortToast("壁紙を解除しました。");
                }
        );
    }

}
