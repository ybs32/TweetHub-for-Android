package com.ybsystem.tweetmate.storages;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.models.entities.AppData;

public class PrefEntity extends PrefBase {

    private static final String KEY_APP_DATA = RES.getString(R.string.pref_key_app_data);

    private PrefEntity() {
    }

    /**
     * Load app data from shared preferences
     * If no data has been stored, returns null
     *
     * @return App data model
     */
    public static AppData loadAppData() {
        Gson gson = new Gson();
        return gson.fromJson(
                getEntitySharedPreferences().getString(KEY_APP_DATA, null),
                new TypeToken<AppData>(){}.getType()
        );
    }

    /**
     * Save app data into shared preferences
     *
     * @param model App data model
     */
    public static void saveAppData(final AppData model) {
        if (model == null) {
            return;
        }
        Gson gson = new Gson();
        SharedPreferences.Editor editor = getEntitySharedPreferences().edit();
        editor.putString(KEY_APP_DATA, gson.toJson(model));
        editor.apply();
    }

}
