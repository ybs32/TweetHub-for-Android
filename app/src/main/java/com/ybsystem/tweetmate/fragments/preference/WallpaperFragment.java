package com.ybsystem.tweetmate.fragments.preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceScreen;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.storages.PrefWallpaper;
import com.ybsystem.tweetmate.utils.DialogUtils;
import com.ybsystem.tweetmate.utils.StorageUtils;
import com.ybsystem.tweetmate.utils.ToastUtils;

import static com.ybsystem.tweetmate.activities.preference.SettingActivity.*;
import static com.ybsystem.tweetmate.activities.preference.WallpaperActivity.*;
import static com.ybsystem.tweetmate.resources.ResString.*;
import static com.ybsystem.tweetmate.storages.PrefWallpaper.*;

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
            if (PrefWallpaper.getWallpaperPath().equals(STR_NOT_SET)) {
                ToastUtils.showShortToast(STR_FAIL_NO_SET_WALLPAPER);
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
                STR_CONFIRM_DESTROY_WALLPAPER,
                (dialog, which) -> {
                    getActivity().setResult(REBOOT_PREPARATION);
                    PrefWallpaper.saveWallpaperPath(STR_NOT_SET);
                    mAddWallpaper.setSummary(PrefWallpaper.getWallpaperPath());
                    ToastUtils.showShortToast(STR_SUCCESS_DESTROY_WALLPAPER);
                }
        );
    }

}
