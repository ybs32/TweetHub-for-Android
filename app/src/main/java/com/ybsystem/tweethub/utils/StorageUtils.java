package com.ybsystem.tweethub.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

/**
 * Utils class for accessing device storage
 */
public class StorageUtils {

    private StorageUtils() {
    }

    /**
     * Create media uri for Android 10 (API 29) or above version
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri createMediaUri(String rootDir, ContentResolver resolver) {
        String fileName = "TweetHub_" + System.currentTimeMillis() + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.RELATIVE_PATH, rootDir + "/TweetHub");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * Create media file for Android 9 (API 28) or below version
     */
    public static File createMediaFile(String rootDir) {
        String fileName = "TweetHub_" + System.currentTimeMillis() + ".jpg";
        File dir = new File(
                Environment.getExternalStoragePublicDirectory(rootDir), "TweetHub");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return new File(dir, fileName);
    }

    public static boolean isPermitted(Context context) {
        int read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return read == PackageManager.PERMISSION_GRANTED
                && write == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity) {
        ToastUtils.showShortToast("アクセス許可が必要です。");
        new Handler().postDelayed(() ->
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0), 1500);
    }

}
