package com.example.cmpt276project.ui.restaurantlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


//I believe sharedPreferencesHelper properly supports what is required
//to save and provide the saved filters. The current issue is with the use of
//the bundle, I'm insure on where and when to initialize it with the new saved filters.
public class SharedPreferencesHelper {

    private String Query;
    private String QueryColor;
    private Boolean IsFav;
    private Integer QueryMin;
    private Integer QueryMax;

    private static final String FILTER_PREFS = "SearchAndFilter";

    private SharedPreferences preferences;
    private static SharedPreferencesHelper sharedPreferencesHelper;

    private SharedPreferencesHelper(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferencesHelper getInstance(Context context) {
        if(sharedPreferencesHelper == null) {
            sharedPreferencesHelper = new SharedPreferencesHelper(context);
        }
        return sharedPreferencesHelper;
    }

    public void saveFilters(String query, String queryColor, Boolean isFav, int queryMin, int queryMax) {
        preferences.edit().putString("SEARCH_BAR", query)
        .putString("COLOR_SELECT", queryColor)
        .putBoolean("FAV_SELECT", isFav)
        .putInt("MIN_SELECT", queryMin)
        .putInt("MAX_SELECT", queryMax)
        .apply();

        Query = query;
        QueryColor = queryColor;
        IsFav = isFav;
        QueryMin = queryMin;
        QueryMax = queryMax;
    }

    public String getQuery() {
        Query = preferences.getString("SEARCH_BAR", "");
        return Query;
    }

    public String getQueryColor() {
        QueryColor = preferences.getString("COLOR_SELECT", "All");
        return QueryColor;
    }

    public Boolean getFav() {
        IsFav = preferences.getBoolean("FAV_SELECT", false);
        return IsFav;
    }

    public Integer getQueryMin() {
        QueryMin = preferences.getInt("MIN_SELECT", 0);
        return QueryMin;
    }

    public Integer getQueryMax() {
        QueryMax = preferences.getInt("MAX_SELECT", 100);
        return QueryMax;
    }
}
