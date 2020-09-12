/*
    ------------------------------------------------------------------------------------------------
    ReadOnlineManager Class Implementation
    This class uses for reading the Data from online
    Creator: Lam, Ching Hang
    Email: CHL50@sfu.ca
    Last Modified Date: 2020/07/28
    ------------------------------------------------------------------------------------------------
 */
// Package
package com.example.cmpt276project.model;

// Import
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// ReadOnlineManager Class
public class ReadOnlineManager {

    private static ReadOnlineManager instance;

    private static DataPackage restaurantReportPackage;
    private static DataPackage inspectionReportPackage;

    private static final String TAG = "ReadOnlineManager";
    private static final String RESTAURANTS_JSON_PATH = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private static final String INSPECTIONS_JSON_PATH = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";

    private static final String JSON_GROUP1 = "result";
    private static final String JSON_GROUP2 = "resources";
    private static final String JSON_ITEM1 = "format";
    private static final String JSON_ITEM2 = "last_modified";
    private static final String JSON_ITEM3 = "url";

    public static final String INPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    //----------------------------------------------------------------------------------------------
    // DataPackage Class
    private static class DataPackage {
        private String format;
        private String lastModifiedString;
        private String path;
    }

    //----------------------------------------------------------------------------------------------
    // Constructor
    private ReadOnlineManager() {
        restaurantReportPackage = new DataPackage();
        inspectionReportPackage = new DataPackage();
    }

    //----------------------------------------------------------------------------------------------
    // Get Instance
    // Returns the instance of DataManager
    public static ReadOnlineManager getInstance(){
        if(instance == null){
            instance = new ReadOnlineManager();
        }
        return instance;
    }

    //----------------------------------------------------------------------------------------------
    // Read the data from Online
    // Parameter isRestaurant is true for reading the Restaurant Report Data
    // Parameter isRestaurant is false for reading the Inspection Report Data
    // Returns true if read the data successfully
    // Otherwise return false;
    private static boolean readData(boolean isRestaurant) {
        try {
            // Get the URL
            URL url = null;
            if (isRestaurant) {
                url = new URL(RESTAURANTS_JSON_PATH);
            } else {
                url = new URL(INSPECTIONS_JSON_PATH);
            }

            // Get the HttpURL Connection
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(2500);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            // Get the Buffered Reader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String context = bufferedReader.readLine();

            // Search in JSON Object
            JSONObject jsonObject = new JSONObject(context).getJSONObject(JSON_GROUP1).getJSONArray(JSON_GROUP2)
                        .getJSONObject(0);

            // Save the data
            if (isRestaurant) {
                restaurantReportPackage.format = jsonObject.getString(JSON_ITEM1);
                restaurantReportPackage.lastModifiedString = jsonObject.getString(JSON_ITEM2);
                restaurantReportPackage.path = jsonObject.getString(JSON_ITEM3);
            } else {
                inspectionReportPackage.format = jsonObject.getString(JSON_ITEM1);
                inspectionReportPackage.lastModifiedString = jsonObject.getString(JSON_ITEM2);
                inspectionReportPackage.path = jsonObject.getString(JSON_ITEM3);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "URL error : " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.e(TAG, "Download failed : " + e.getMessage());
            return false;
        } catch (JSONException e) {
            Log.e(TAG, "Catch failed : " + e.getMessage());
            return false;
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------
    // Thread
    // Reads the Restaurant Report Data
    public static Thread readRestaurantThread = new Thread(new Runnable() {
        @Override
        public void run() {
            readData(true);
        }
    });

    //----------------------------------------------------------------------------------------------
    // Thread
    // Reads the Inspection Report Data
    public static Thread readInspectionThread = new Thread(new Runnable() {
        @Override
        public void run() {
            readData(false);
        }
    });

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the format inside the Data Package
    // Parameter isRestaurant is true for getting the Restaurant Report Format
    // Parameter isRestaurant is false for getting the Inspection Report Format
    public String getFormat(boolean isRestaurant) {
        if (isRestaurant) {
            return restaurantReportPackage.format;
        } else {
            return inspectionReportPackage.format;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the Last Modified String inside the Data Package
    // Parameter isRestaurant is true for getting the Restaurant Report Last Modified String
    // Parameter isRestaurant is false for getting the Inspection Report Last Modified String
    public String getLastModifiedString(boolean isRestaurant) {
        if (isRestaurant) {
            return restaurantReportPackage.lastModifiedString;
        } else {
            return inspectionReportPackage.lastModifiedString;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the path inside the Data Package
    // Parameter isRestaurant is true for getting the Restaurant Report Path
    // Parameter isRestaurant is false for getting the Inspection Report Path
    public String getPath(boolean isRestaurant) {
        if (isRestaurant) {
            return restaurantReportPackage.path;
        } else {
            return inspectionReportPackage.path;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the date inside the Data Package
    // Parameter isRestaurant is true for getting the Restaurant Report Date
    // Parameter isRestaurant is false for getting the Inspection Report Date
    public static DateAndTime getDate(boolean isRestaurant) {
        if (isRestaurant) {
            return new DateAndTime(INPUT_DATE_FORMAT, restaurantReportPackage.lastModifiedString);
        } else {
            return new DateAndTime(INPUT_DATE_FORMAT, inspectionReportPackage.lastModifiedString);
        }
    }
}