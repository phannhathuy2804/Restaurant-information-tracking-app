/*
    ------------------------------------------------------------------------------------------------
    DataManager Class Implementation
    This class uses for handling the CSV files
    Creator: Lam, Ching Hang
    Email: CHL50@sfu.ca
    Last Modified Date: 2020/07/28
    ------------------------------------------------------------------------------------------------
 */
// Package
package com.example.cmpt276project.model;

// Import
import android.content.Context;
import android.util.Log;
import com.example.cmpt276project.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

// DataManager Class
public class DataManager{

    private static DataManager instance;

    private static Context context;
    private static File INTERNAL_DIRECTORY;
    private static File RESTAURANT_REPORT_FILE;
    private static File INSPECTION_REPORT_FILE;
    private static File TEMP_RESTAURANT_REPORT_FILE;
    private static File TEMP_INSPECTION_REPORT_FILE;

    private static InputStream restaurantInputStream;
    private static InputStream inspectionInputStream;

    private static final String TAG = "DataManager";
    private static final String RESTAURANT_REPORT_FILE_NAME = "restaurant_report.csv";
    private static final String INSPECTION_REPORT_FILE_NAME = "inspection_report.csv";
    private static final String TEMP_RESTAURANT_REPORT_FILE_NAME = "temp_restaurant_report.csv";
    private static final String TEMP_INSPECTION_REPORT_FILE_NAME = "temp_inspection_report.csv";;

    public enum CSVFile{
        RESTAURANT_REPORT_CSV, INSPECTION_REPORT_CSV, TEMP_RESTAURANT_REPORT_CSV, TEMP_INSPECTION_REPORT_CSV;
    }

    public enum DownloadContext {
        RESTAURANT, INSPECTION;
    }

    //----------------------------------------------------------------------------------------------
    // Constructor
    private DataManager(Context context) {
        this.context = context;
        INTERNAL_DIRECTORY = context.getFilesDir();
        RESTAURANT_REPORT_FILE = new File(INTERNAL_DIRECTORY, RESTAURANT_REPORT_FILE_NAME);
        INSPECTION_REPORT_FILE = new File(INTERNAL_DIRECTORY, INSPECTION_REPORT_FILE_NAME);
        TEMP_RESTAURANT_REPORT_FILE = new File(INTERNAL_DIRECTORY, TEMP_RESTAURANT_REPORT_FILE_NAME);
        TEMP_INSPECTION_REPORT_FILE = new File(INTERNAL_DIRECTORY, TEMP_INSPECTION_REPORT_FILE_NAME);

        setInputStream();
    }

    //----------------------------------------------------------------------------------------------
    // Get Instance
    // Returns the instance of DataManager
    public static DataManager getInstance(Context context){
        if(instance == null){
            instance = new DataManager(context);
        }
        return instance;
    }

    //----------------------------------------------------------------------------------------------
    // Setter
    // Set the InputStream
    // If the CSV files are not in the hidden directory, then use the raw files
    public static void setInputStream() {
        // Restaurant
        if (isExist(CSVFile.RESTAURANT_REPORT_CSV)) {
            try {
                restaurantInputStream = new FileInputStream(RESTAURANT_REPORT_FILE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                restaurantInputStream = context.getResources().openRawResource(R.raw.restaurants);
            }
        } else {
            restaurantInputStream = context.getResources().openRawResource(R.raw.restaurants);
        }

        // Inspection
        if (isExist(CSVFile.INSPECTION_REPORT_CSV)) {
            try {
                inspectionInputStream = new FileInputStream(INSPECTION_REPORT_FILE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                inspectionInputStream = context.getResources().openRawResource(R.raw.inspectionreports);
            }
        } else {
            inspectionInputStream = context.getResources().openRawResource(R.raw.inspectionreports);
        }
    }

    //----------------------------------------------------------------------------------------------
    // Checking the existing of the file
    // Returns true if the file exists, otherwise returns false
    private static boolean isExist(CSVFile csvFile) {

        if (csvFile == CSVFile.RESTAURANT_REPORT_CSV) {
            return RESTAURANT_REPORT_FILE.exists();
        }
        else if (csvFile == CSVFile.INSPECTION_REPORT_CSV) {
            return INSPECTION_REPORT_FILE.exists();
        }
        else if (csvFile == CSVFile.TEMP_RESTAURANT_REPORT_CSV) {
            return TEMP_RESTAURANT_REPORT_FILE.exists();
        }
        else if (csvFile == CSVFile.TEMP_INSPECTION_REPORT_CSV) {
            return TEMP_INSPECTION_REPORT_FILE.exists();
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
    // Deleting the file
    // Returns true if the file deleted, otherwise returns false
    // Remark: This method automatically checks the existing of the file before deletion
    private static boolean delete(CSVFile csvFile) {

        if (csvFile == CSVFile.RESTAURANT_REPORT_CSV) {
            return RESTAURANT_REPORT_FILE.delete();
        }
        else if (csvFile == CSVFile.INSPECTION_REPORT_CSV) {
            return INSPECTION_REPORT_FILE.delete();
        }
        else if (csvFile == CSVFile.TEMP_RESTAURANT_REPORT_CSV) {
            return TEMP_RESTAURANT_REPORT_FILE.delete();
        }
        else if (csvFile == CSVFile.TEMP_INSPECTION_REPORT_CSV) {
            return TEMP_INSPECTION_REPORT_FILE.delete();
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
    // Renaming the file
    // Returns true if the file successfully rename, otherwise returns false
    private static boolean rename(DownloadContext downloadContext) {

        if (downloadContext == DownloadContext.RESTAURANT) {
            delete(CSVFile.RESTAURANT_REPORT_CSV);

            return TEMP_RESTAURANT_REPORT_FILE.renameTo(RESTAURANT_REPORT_FILE);
        }
        else if (downloadContext == DownloadContext.INSPECTION) {
            delete(CSVFile.INSPECTION_REPORT_CSV);

            return TEMP_INSPECTION_REPORT_FILE.renameTo(INSPECTION_REPORT_FILE);
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
    // Downloading the file
    // Returns true if the file successfully download, otherwise returns false
    private static boolean download(DownloadContext downloadContext) {
        String tempFileName = "";
        String tempPath = "";

        // Set the download file name and path
        // Restaurant Report
        if (downloadContext == DownloadContext.RESTAURANT) {
            delete(CSVFile.TEMP_RESTAURANT_REPORT_CSV);

            tempFileName = TEMP_RESTAURANT_REPORT_FILE_NAME;
            tempPath = "https://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv";
        }
        // Inspection Report
        else if (downloadContext == DownloadContext.INSPECTION) {
            delete(CSVFile.TEMP_INSPECTION_REPORT_CSV);

            tempFileName = TEMP_INSPECTION_REPORT_FILE_NAME;
            tempPath = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraserhealthrestaurantinspectionreports.csv";
        }

        final String FILE_NAME = tempFileName;
        final String PATH = tempPath;

        File file = context.getFileStreamPath(FILE_NAME);

        if (file.exists())
            return false;

        try {
            URL url = new URL(PATH);
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            Log.i(TAG, "Successfully Download");

            return true;
        } catch (final Exception e) {
            e.printStackTrace();;

            Log.i(TAG, "Download Failed");

            return false;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the Restaurant InputStream
    public static InputStream getRestaurantInputStream() {
        return restaurantInputStream;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the Inspection InputStream
    public static InputStream getInspectionInputStream() {
        return inspectionInputStream;
    }

    //----------------------------------------------------------------------------------------------
    // Thread
    // Downloads the Restaurant Report and renames it
    public static Thread downloadRestaurantThread = new Thread(new Runnable() {
        @Override
        public void run() {
            download(DataManager.DownloadContext.RESTAURANT);
            rename(DataManager.DownloadContext.RESTAURANT);
        }
    });

    //----------------------------------------------------------------------------------------------
    // Thread
    // Downloads the Inspection Report and renames it
    public static Thread downloadInspectionThread = new Thread(new Runnable() {
        @Override
        public void run() {
            download(DataManager.DownloadContext.INSPECTION);
            rename(DataManager.DownloadContext.INSPECTION);
        }
    });
}