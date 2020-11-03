package com.ybsystem.tweethub.fragments.preference;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.fragments.dialog.ConfirmDialog;
import com.ybsystem.tweethub.storages.PrefWallpaper;
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
        mAddWallpaper.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // Check permission
                if (!StorageUtils.isPermitted(getActivity())) {
                    StorageUtils.requestPermission(getActivity());
                    return false;
                }
                intentToGallery();
                return true;
            }
        });

        mDeleteWallpaper = findPreference(KEY_DELETE_WALLPAPER);
        mDeleteWallpaper.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // Check wallpaper
                if (PrefWallpaper.getWallpaperPath().equals("未設定")) {
                    ToastUtils.showShortToast("壁紙は設定されていません。");
                    return false;
                }
                showConfirmDialog();
                return true;
            }
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
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                0);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        getActivity().startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void showConfirmDialog() {
        // Create dialog
        ConfirmDialog confirmDialog = new ConfirmDialog().newInstance("壁紙を解除しますか？");
        confirmDialog.setOnPositiveClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().setResult(REBOOT_PREPARATION);
                PrefWallpaper.saveWallpaperPath("未設定");
                mAddWallpaper.setSummary(PrefWallpaper.getWallpaperPath());
                ToastUtils.showShortToast("壁紙を解除しました。");
            }
        });
        // Show dialog
        if (getFragmentManager().findFragmentByTag("ConfirmDialog") == null) {
            confirmDialog.show(getFragmentManager(), "ConfirmDialog");
        }
    }

}