/*
    This is InspectionDetailsActivity class implementation.
    Last Modified Date: 2020/07/18

    This class is an Activity which display the details of the inspection.
    This class includes a RecycleView which requires to use InspectionAdapter Class.

    This class uses 9 images from the online
    Citation:
    Image: led_green, Website: https://www.iconexperience.com/v_collection/icons/?icon=led_green
    Image: led_yellow, Website: https://www.iconexperience.com/v_collection/icons/?icon=led_yellow
    Image: led_red, Website: https://www.iconexperience.com/v_collection/icons/?icon=led_red
    Image: hazard_level, Website: https://en.wikipedia.org/wiki/Hazard_symbol#/media/File:DIN_4844-2_Warnung_vor_einer_Gefahrenstelle_D-W000.svg
    Image: food_violation, Website: https://www.pngegg.com/en/png-zenqs
    Image: utensil_violation, Website: https://www.pngegg.com/en/png-zdoua
    Image: equipment_violation, Website: https://www.pngegg.com/en/png-zlpju
    Image: pest_violation, Website: https://www.pngegg.com/en/png-znbhf/download
    Image: employee_violation, Website: https://www.pngegg.com/en/png-dvzjj/download

    // This function only uses for testing
    testing_initialization();
 */

// Package
package com.example.cmpt276project.ui.inspection;

// Import
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpt276project.R;
import com.example.cmpt276project.model.DateAndTime;
import com.example.cmpt276project.model.Inspection;
import com.example.cmpt276project.model.RestaurantManager;
import com.example.cmpt276project.ui.restaurant.RestaurantDetailsActivity;

// InspectionDetailsActivity Class
public class InspectionDetailsActivity extends AppCompatActivity {

    private static final String TAG = InspectionDetailsActivity.class.getSimpleName();
    public static final String KEY_INSPECTION = "key_parcelable";

    private RestaurantManager manager = RestaurantManager.getInstance();
    private Inspection inspection;
    private int restaurant_index = 0;
    private int inspection_index = 0;
    private boolean listScreenState;

    private boolean[] allCritical;
    boolean[][] allNatures;
    String[] allShortDescriptions;
    String[] allLongDescriptions;
    int images[] = {R.drawable.equipment_violation, R.drawable.utensil_violation,
            R.drawable.food_violation, R.drawable.pest_violation, R.drawable.employee_violation,
            R.drawable.hazard_level};
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);

        // This function only uses for testing
        //testing_initialization();
        Intent intent = getIntent();
        restaurant_index = intent.getIntExtra("RESTAURANT_INDEX", 0);
        inspection_index = intent.getIntExtra("INSPECTION_INDEX", 0);
        listScreenState = intent.getBooleanExtra("LISTSTATE", true);
        inspection = intent.getParcelableExtra(KEY_INSPECTION);
//        Log.d(TAG, "parcelable " + inspection.toString());

        //Actionbar Button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Update all the information
        updateInformation();
        updateRecycleView();
    }

    // Actionbar back button
    public boolean onOptionsItemSelected(MenuItem item){
//        Intent myIntent = new Intent(getApplicationContext(), RestaurantDetailsActivity.class);
//        myIntent.putExtra("RESTAURANT_INDEX", restaurant_index);
//        myIntent.putExtra("ListView", listScreenState);
//        myIntent.putExtra(RestaurantDetailsActivity.KEY_STATE, listScreenState);
//        startActivityForResult(myIntent, 0);
//        return true;
        onBackPressed();
        return true;
    }

    // Back Button
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(this, RestaurantDetailsActivity.class);
//        intent.putExtra("RESTAURANT_INDEX", restaurant_index);
//        intent.putExtra("ListView", listScreenState);
//        startActivity(intent);
//    }

    // Set the RecyclerView
    private void updateRecycleView() {
        //Empty Inspection
        if (inspection == null)
            return;

        // Get all the data from the inspection
        allCritical = inspection.getAllCriticality();
        allNatures = inspection.getAllNatures();
        allShortDescriptions = inspection.getAllShortDescriptions();
        allLongDescriptions = inspection.getAllLongDescriptions();
        recyclerView = findViewById(R.id.inspection_RecycleView);

        // Set the adapter to the RecyclerView
        InspectionAdapter inspectionAdapter = new InspectionAdapter(this, allCritical, allNatures, allShortDescriptions, allLongDescriptions, images);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycleview_divier));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(inspectionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // This function only uses for testing
    private void testing_initialization() {
        String trackingNumber = "SWOD-AHZUMF";
        String inspectionDateString = "20180626";
        String inspectionTypeString = "Routine";
        String numOfCriticalString = "3";
        String numOfNonCriticalString = "4";
        String hazardLevelString = "High";
        String violationsString = "205,Critical,Cold potentially hazardous food stored/displayed above 4 Â°C. [s. 14(2)],Not Repeat|209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|301,Critical,Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17(1)],Not Repeat|304,Not Critical,Premises not free of pests [s. 26(a)],Not Repeat|305,Not Critical,Conditions observed that may allow entrance/harbouring/breeding of pests [s. 26(b)(c)],Not Repeat|306,Not Critical,Food premises not maintained in a sanitary condition [s. 17(1)],Not Repeat|401,Critical,Adequate handwashing stations not available for employees [s. 21(4)],Not Repeat";

        inspection = new Inspection(trackingNumber, DateAndTime.DATE_FORMAT_CSV_INPUT, inspectionDateString, inspectionTypeString, numOfCriticalString, numOfNonCriticalString, hazardLevelString, violationsString);
    }

    // Set All the TextView and ImageView in this Activity
    @SuppressLint("ResourceType")
    private void updateInformation() {

        // Link all the TextView and ImageView
        TextView textView_date = (TextView) findViewById(R.id.textView_date);
        TextView textView_type = (TextView) findViewById(R.id.textView_type);
        TextView textView_critical_issues = (TextView) findViewById(R.id.textView_critical_issues);
        TextView textView_non_critical_issues = (TextView) findViewById(R.id.textView_non_critical_issues);
        TextView textView_hazard_level = (TextView) findViewById(R.id.textView_hazard_level);
        ImageView imageView_hazard_level_icon = (ImageView) findViewById(R.id.imageView_hazard_level_icon);

        //Empty Inspection
        if (inspection == null) {
            imageView_hazard_level_icon.setImageResource(0);
            return;
        }

        // Set Text to all the TextView
        textView_date.setText(inspection.getInspectionDate().getDateString(DateAndTime.DATE_FORMAT_INSPECTION));
        textView_type.setText(inspection.getInspectionType().getResourceString());
        textView_critical_issues.setText("" + inspection.getNumOfCritical());
        textView_non_critical_issues.setText("" + inspection.getNumOfNonCritical());
        textView_hazard_level.setText(inspection.getHazardLevel().getResourceString());

        // Set the LED Image
        int id_led = R.drawable.led_green;
        String color = getString(R.color.inspection_activity_color_green);
        switch(inspection.getHazardLevel()) {
            case LOW:
                color = getString(R.color.inspection_activity_color_green);
                id_led = R.drawable.led_green;
                break;
            case MODERATE:
                color = getString(R.color.inspection_activity_color_yellow);
                id_led = R.drawable.led_yellow;
                break;
            case HIGH:
                color = getString(R.color.inspection_activity_color_red);
                id_led = R.drawable.led_red;
                break;
        }
        imageView_hazard_level_icon.setImageResource(id_led);
        imageView_hazard_level_icon.setMaxWidth(18);
        imageView_hazard_level_icon.setMaxHeight(21);

        // Set the Hazard Level Colour
        textView_hazard_level.setTextColor(Color.parseColor(color));
    }
}