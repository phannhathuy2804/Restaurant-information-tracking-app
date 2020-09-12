/*
    ------------------------------------------------------------------------------------------------
    UserDataManager Class Implementation
    This class uses for saving the User Data
    Creator: Lam, Ching Hang
    Email: CHL50@sfu.ca
    Last Modified Date: 2020/07/28
    ------------------------------------------------------------------------------------------------
 */
// Package
package com.example.cmpt276project.model;

// Import
import android.content.Context;
import android.content.SharedPreferences;

// UserDataManager Class
public class UserDataManager {

    private static UserDataManager instance;

    private static Context context;
    private static SharedPreferences sharedPreferences;

    private static DateAndTime restaurantReportDate;
    private static DateAndTime inspectionReportDate;
    private static DateAndTime lastReportDate;

    private static final String TAG = "UserDataManager";
    private static final String USER_DATA = "user_data";
    private static final String RESTAURANT_REPORT_DATE_KEY = "restaurant_report_date_key";
    private static final String INSPECTION_REPORT_DATE_KEY = "inspection_report_date_key";
    private static final String LAST_REPORT_DATE_KEY = "last_report_date_key";
    private static final String REMIND_UPDATE_KEY = "remind_update_key";

    private static final String DEFAULT_DATE = "2020-07-01T00:00:00";
    private static final String INPUT_DATE_FORMAT = ReadOnlineManager.INPUT_DATE_FORMAT;

    //----------------------------------------------------------------------------------------------
    // Constructor
    private UserDataManager(Context context) {
        this.context = context;

        sharedPreferences = context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        loadData();
    }

    //----------------------------------------------------------------------------------------------
    // Get Instance
    // Returns the instance of DataManager
    public static UserDataManager getInstance(Context context){
        if(instance == null){
            instance = new UserDataManager(context);
        }
        return instance;
    }

    //----------------------------------------------------------------------------------------------
    // Load the Data from SharedPreferences
    private void loadData() {
        String restaurantReportDateString = sharedPreferences.getString(RESTAURANT_REPORT_DATE_KEY, DEFAULT_DATE);
        restaurantReportDate = new DateAndTime(INPUT_DATE_FORMAT, restaurantReportDateString);

        String inspectionReportDateString = sharedPreferences.getString(INSPECTION_REPORT_DATE_KEY, DEFAULT_DATE);
        inspectionReportDate = new DateAndTime(INPUT_DATE_FORMAT, inspectionReportDateString);

        String lastReportDateString = sharedPreferences.getString(LAST_REPORT_DATE_KEY, DEFAULT_DATE);
        lastReportDate = new DateAndTime(INPUT_DATE_FORMAT, lastReportDateString);

        boolean remindUpdate = sharedPreferences.getBoolean(REMIND_UPDATE_KEY, false);
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the Restaurant Report Date
    public static DateAndTime getRestaurantReportDate() {
        return restaurantReportDate;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the Inspection Report Date
    public static DateAndTime getInspectionReportDate() {
        return inspectionReportDate;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the Last Report Date
    public static DateAndTime getLastReportDate() {
        return lastReportDate;
    }

    //----------------------------------------------------------------------------------------------
    // Setter
    // Saves the Restaurant Report Date
    public static void saveRestaurantReportDate(DateAndTime restaurantReportDate) {
        UserDataManager.restaurantReportDate = restaurantReportDate;
        String restaurantReportDateString = restaurantReportDate.getDateString(INPUT_DATE_FORMAT);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RESTAURANT_REPORT_DATE_KEY, restaurantReportDateString);
        editor.commit();

        saveLastReportDate(restaurantReportDate);
    }

    //----------------------------------------------------------------------------------------------
    // Setter
    // Saves the Inspection Report Date
    public static void saveInspectionReportDate(DateAndTime inspectionReportDate) {
        UserDataManager.inspectionReportDate = inspectionReportDate;
        String inspectionReportDateString = inspectionReportDate.getDateString(INPUT_DATE_FORMAT);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(INSPECTION_REPORT_DATE_KEY, inspectionReportDateString);
        editor.commit();

        saveLastReportDate(inspectionReportDate);
    }

    //----------------------------------------------------------------------------------------------
    // Setter
    // Saves the Last Report Date
    private static void saveLastReportDate(DateAndTime dateAndTime) {
        if (dateAndTime.after(lastReportDate)) {
            lastReportDate = dateAndTime;
            String dataAndTimeString = dateAndTime.getDateString(INPUT_DATE_FORMAT);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LAST_REPORT_DATE_KEY, dataAndTimeString);
            editor.commit();
        }
    }

    //----------------------------------------------------------------------------------------------
    // Clear
    // Clears all the Data from SharedPreferences
    public static void clearAllData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    //----------------------------------------------------------------------------------------------
    // Checking Update
    // Returns true if there is more than 20 hours since last update
    // Otherwise returns false
    public static boolean checkLastUpdate() {
        if (lastReportDate.timeDiffInHour() > 20) {
            return true;
        }
        return false;
    }
}