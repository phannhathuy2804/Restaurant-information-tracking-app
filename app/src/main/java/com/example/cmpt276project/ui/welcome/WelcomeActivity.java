/*
    ------------------------------------------------------------------------------------------------
    WelcomeActivity Class Implementation
    This class uses for showing the Welcome Screen
    Creator: Lam, Ching Hang
    Email: CHL50@sfu.ca
    Last Modified Date: 2020/07/28
    ------------------------------------------------------------------------------------------------
 */
// Package
package com.example.cmpt276project.ui.welcome;

// Import

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmpt276project.R;
import com.example.cmpt276project.model.DBAdapter;
import com.example.cmpt276project.model.DataManager;
import com.example.cmpt276project.model.GetData2;
import com.example.cmpt276project.model.Inspection;
import com.example.cmpt276project.model.ReadOnlineManager;
import com.example.cmpt276project.model.Restaurant;
import com.example.cmpt276project.model.RestaurantManager;
import com.example.cmpt276project.model.UserDataManager;
import com.example.cmpt276project.model.Violation;
import com.example.cmpt276project.ui.restaurantlist.RestaurantsListActivity;
import com.example.cmpt276project.ui.restaurantlist.SharedPreferencesHelper;

import java.io.IOException;
import java.util.List;

// WelcomeActivity Class
public class WelcomeActivity extends AppCompatActivity implements UpdateDialog.YesClickedListener, UpdateDialog.CancelClickedListener, NoUpdateDialog.YesClickedListener {

    private List<Restaurant> restaurantList;
    private RestaurantManager manager;

    private DataManager dataManager;
    private ReadOnlineManager readOnlineManager;
    private UserDataManager userDataManager;

    private LoadingDialog loadingDialog;
    private UpdateDialog updateDialog;
    private NoUpdateDialog noUpdateDialog;

    private boolean isUpdate = false;
    public static final String KEY_UPDATE = "is_update_key";

    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize the DataManager, ReadOnlineManager, and UserDataManager
        dataManager = DataManager.getInstance(getApplicationContext());
        readOnlineManager = ReadOnlineManager.getInstance();
        userDataManager = UserDataManager.getInstance(getApplicationContext());
        Inspection.setContext(getApplicationContext());
        Violation.setContext(getApplicationContext());

        // Loading the CSV files
        loadCSVThread.start();

        // More than 20 hrs
        if (checkLastUpdate()) {
            needToCheckOnline();
        }
        // Within 20 hrs
        else {
            notNeedToCheckOnline();
        }


        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(WelcomeActivity.this);
        helper.saveFilters("", "All", false, 0, 100);
    }

    //----------------------------------------------------------------------------------------------
    // Loads the CSV files into the RestaurantManager
    private void retrieveData() throws IOException {
        GetData2 dataProcessing = new GetData2(dataManager.getRestaurantInputStream(), dataManager.getInspectionInputStream());
        restaurantList = dataProcessing.getRestaurants();
        dataProcessing.inputAllInspections();
        dataProcessing.countCritical();

        manager = RestaurantManager.getInstance();
        manager.setRestaurants(dataProcessing.getRestaurants());
    }

    //----------------------------------------------------------------------------------------------
    // Moves to the RestaurantList Activity
    private void moveToNextActivity() {
        Intent intent = new Intent(WelcomeActivity.this, RestaurantsListActivity.class);
        intent.putExtra(KEY_UPDATE, isUpdate);
        startActivity(intent);
        finish();
    }

    //----------------------------------------------------------------------------------------------
    // Thread
    // Loads the CSV files into the RestaurantManager
    Thread loadCSVThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                retrieveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    //----------------------------------------------------------------------------------------------
    // Shows the Loading Dialog
    private void showLoadingDialog() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.showLoadingDialog();
    }

    //----------------------------------------------------------------------------------------------
    // Shows the Update Dialog
    private void showUpdateDialog() {
        updateDialog = new UpdateDialog();
        updateDialog.show(getSupportFragmentManager(), "Update");
    }

    //----------------------------------------------------------------------------------------------
    // Shows the NoUpdate Dialog
    private void showNoUpdateDialog() {
        noUpdateDialog = new NoUpdateDialog();
        noUpdateDialog.show(getSupportFragmentManager(), "NoUpdate");
    }

    //----------------------------------------------------------------------------------------------
    // Implement from the Update Dialog after clicked the Yes button
    @Override
    public void updateDialogYesClicked() {
        // Shows the Loading Dialog
        showLoadingDialog();

        // Run a new Thread that download the Restaurant Report and Inspection Report
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Restaurant Report Needs Update
                if (checkRestaurantUpdate()) {
                    DataManager.downloadRestaurantThread.start();
                }

                // Inspection Report Needs Update
                if (checkInspectionUpdate()) {
                    DataManager.downloadInspectionThread.start();
                }

                // Wail until downloading finished
                try {
                    if (checkRestaurantUpdate()) {
                        DataManager.downloadRestaurantThread.join();
                        updateRestaurantReportDate();
                    }
                    if (checkInspectionUpdate()) {
                        DataManager.downloadInspectionThread.join();
                        updateInspectionReportDate();
                    }

                    // Stop showing the Loading Dialog
                    loadingDialog.dismissDialog();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Stop the loadingCSVThread if it is running
                loadCSVThread.interrupt();

                // Setup the InputStream
                DataManager.setInputStream();

                // Set the isUpdate
                isUpdate = true;

                // Run the loadCSVThread again
                loadCSVThread.run();
                try {
                    // Wait until the loadCSVThread finished
                    loadCSVThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Move to RestaurantList Activity
                moveToNextActivity();
            }
        }).start();
    }

    //----------------------------------------------------------------------------------------------
    // Implement from the Update Dialog after clicked the Cancel button
    @Override
    public void updateDialogCancelClicked() {
        // Run a new Thread that wait until finished loading CSV files
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Wait until the loadCSVThread finished
                    loadCSVThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Move to RestaurantList Activity
                moveToNextActivity();
            }
        }).start();
    }

    //----------------------------------------------------------------------------------------------
    // Implement from the NoUpdate Dialog after clicked the Yes button
    @Override
    public void noUpdateDialogYesClicked() {
        notNeedToCheckOnline();
    }

    //----------------------------------------------------------------------------------------------
    // Checking the Last Update
    // Returns the value of checkLastUpdate method in UserData Class
    // Returns true if there is more than 20 hours since last update
    // Otherwise returns false
    public boolean checkLastUpdate() {
        return UserDataManager.checkLastUpdate();
    }

    //----------------------------------------------------------------------------------------------
    // Action after checking 20 hrs
    // Not Need to Check Online
    private void notNeedToCheckOnline() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Wait until loading CSV files finished
                    loadCSVThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                moveToNextActivity();
            }
        }).start();
    }

    //----------------------------------------------------------------------------------------------
    // Action after checking 20 hrs
    // Need to Check Online
    // Returns true if needs to update
    // Returns false if does not need to update
    private void needToCheckOnline() {
        // Shows the Toast Message
        Toast.makeText(this, R.string.welcome_activity_toast_checking_update, Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Start Reading Data from Online
                ReadOnlineManager.readRestaurantThread.start();
                ReadOnlineManager.readInspectionThread.start();

                try {
                    // Wait until finished Reading from Online
                    ReadOnlineManager.readRestaurantThread.join();
                    ReadOnlineManager.readInspectionThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Need to Update
                if (checkUpdate()) {
                    showUpdateDialog();
                }
                // Not Need to Update
                else {
                   showNoUpdateDialog();
                }
            }
        }).start();
    }

    //----------------------------------------------------------------------------------------------
    // Action after finished Reading from Online
    // Checking Update
    // Returns true if needs to update
    // Returns false if does not need to update
    private boolean checkUpdate() {
        if (checkRestaurantUpdate()) {
            return true;
        }
        if (checkInspectionUpdate()) {
            return true;
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
    // Action after finished Reading from Online
    // Checking the Restaurant Report Update
    // Returns true if needs to update
    // Returns false if does not need to update
    private boolean checkRestaurantUpdate() {
        if (ReadOnlineManager.getDate(true).after(UserDataManager.getRestaurantReportDate())) {
            return true;
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
    // Action after finished Reading from Online
    // Checking the Inspection Report Update
    // Returns true if needs to update
    // Returns false if does not need to update
    private boolean checkInspectionUpdate() {
        if (ReadOnlineManager.getDate(false).after(UserDataManager.getInspectionReportDate())) {
            return true;
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
    // Action after finished Downloading Restaurant Report
    // Saving the Restaurant Report Date in the UserData
    private void updateRestaurantReportDate() {
        UserDataManager.saveRestaurantReportDate(ReadOnlineManager.getDate(true));
    }

    //----------------------------------------------------------------------------------------------
    // Action after finished Downloading Inspection Report
    // Saving the Inspection Report Date in the UserData
    private void updateInspectionReportDate() {
        UserDataManager.saveInspectionReportDate(ReadOnlineManager.getDate(false));
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


}