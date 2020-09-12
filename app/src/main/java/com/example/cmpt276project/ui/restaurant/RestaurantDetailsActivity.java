package com.example.cmpt276project.ui.restaurant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpt276project.R;
import com.example.cmpt276project.model.DBAdapter;
import com.example.cmpt276project.model.DateAndTime;
import com.example.cmpt276project.model.Inspection;
import com.example.cmpt276project.model.Restaurant;
import com.example.cmpt276project.model.RestaurantManager;
import com.example.cmpt276project.ui.inspection.InspectionDetailsActivity;
import com.example.cmpt276project.ui.restaurantlist.RestaurantsListActivity;

import java.util.ArrayList;
import java.util.List;

//The Activity for the second screen, handles setting up the screen and recyclerView, passes list index position to third activity
public class RestaurantDetailsActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantDetails";

    public static final String KEY_STATE = "key_state";
    public static final String KEY_RESTAURANT = "restaurant_parcelable";

    private RecyclerView recyclerView;

    private RestaurantDetailsAdapter adapter;
    private List<Inspection> inspectionList = new ArrayList<>();
    private int index;
    private boolean listScreenState;

    private Restaurant selectedRestaurant;

    DBAdapter myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        listScreenState = intent.getBooleanExtra(KEY_STATE, true);
        selectedRestaurant = intent.getParcelableExtra(KEY_RESTAURANT);

        Log.d(TAG, "parcelable " + selectedRestaurant.toString());

        inspectionList = selectedRestaurant.getInspection();

        Log.d(TAG, "listScreenState == " + listScreenState);

        initializeScreen();
        initializeRecyclerView();
        initializeFavorites();

        onGPSPressed();

    }

    private void initializeFavorites() {
        myDb = new DBAdapter(this);
        myDb.open();
        final ImageButton fav = (ImageButton) findViewById(R.id.favorite);
        final Restaurant restaurant = selectedRestaurant;
        Cursor inDB = myDb.getRow(restaurant.getTrackingNumber());
        if (inDB.getCount()>0){
            Log.w(TAG,"ALREADY IN THE DB");
            restaurant.setFavorite(true);
            fav.setImageResource(R.drawable.ic_love);
        }
        inDB.close();
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurant.isFavorite()==false) {

                    if(restaurant.getInspection().size() == 0){
                        myDb.insertRow(restaurant.getTrackingNumber(),restaurant.getName(),
                                "0",
                                "");
                    }
                    else {
                        myDb.insertRow(restaurant.getTrackingNumber(), restaurant.getName(),
                                restaurant.getTheMostRecentInspection().getInspectionDate().getDateString(DateAndTime.DATE_FORMAT_CSV_INPUT),
                                restaurant.getTheMostRecentInspection().getHazardLevel().name());
                    }
                    restaurant.setFavorite(true);
                    fav.setImageResource(R.drawable.ic_love);
                    myDb.getAllRows();
                    //Testing
                    Cursor cursor = myDb.getAllRows();
                    String message = "add";
                    // populate the message from the cursor

                    // Reset cursor to start, checking to see if there's data:
                    if (cursor.moveToFirst()) {
                        do {
                            // Process the data:
                            String id = cursor.getString((DBAdapter.COL_RESID));
                            String name = cursor.getString(DBAdapter.COL_NAME);

                            String date = cursor.getString(DBAdapter.COL_MOSTRECENT);

                            String hazard = cursor.getString(DBAdapter.COL_HAZARDLEVEL);

                            // Append data to the message:
                            message += id + "+ resID\n"+ name + " + name \n"+ date + " + date \n" +hazard + "hazard \n";
                        } while(cursor.moveToNext());
                    }

                    // Close the cursor to avoid a resource leak.
                    cursor.close();

                    Log.e(TAG, message);
                    //Endtesting


                }
                else {
                    restaurant.setFavorite(false);
                    myDb.deleteRow(restaurant.getTrackingNumber());
                    fav.setImageResource((R.drawable.ic_action_name));
                    myDb.getAllRows();
                    //Testing
                    Cursor cursor = myDb.getAllRows();
                    String message = "remove";
                    // populate the message from the cursor

                    // Reset cursor to start, checking to see if there's data:
                    if (cursor.moveToFirst()) {
                        do {
                            // Process the data:
                            String id = cursor.getString((DBAdapter.COL_RESID));
                            String name = cursor.getString(DBAdapter.COL_NAME);

                            String date = cursor.getString(DBAdapter.COL_MOSTRECENT);

                            String hazard = cursor.getString(DBAdapter.COL_HAZARDLEVEL);

                            // Append data to the message:
                            message += id + "+ resID\n"+ name + " + name \n"+ date + " + date \n" +hazard + "hazard \n";
                        } while(cursor.moveToNext());
                    }

                    // Close the cursor to avoid a resource leak.
                    cursor.close();

                    Log.e(TAG, message);
                    //Endtesting
                }
            }

        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        myDb.close();
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, RestaurantsListActivity.class);
        intent.putExtra(RestaurantsListActivity.KEY_RESTAURANT, selectedRestaurant);
        intent.putExtra(RestaurantsListActivity.KEY_STATE, listScreenState);
        intent.putExtra("ListView", listScreenState);
        startActivity(intent);
    }

    //the reaction for pressing the action bar back button
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(getApplicationContext(), RestaurantsListActivity.class);
        intent.putExtra(RestaurantsListActivity.KEY_RESTAURANT, (Restaurant) null);
        intent.putExtra(RestaurantsListActivity.KEY_STATE, listScreenState);
        startActivity(intent);
        return true;
    }

    public void onGPSPressed() {
        TextView GPSText = (TextView) findViewById(R.id.GPSText);
        GPSText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantDetailsActivity.this, RestaurantsListActivity.class);
                intent.putExtra(RestaurantsListActivity.KEY_RESTAURANT, selectedRestaurant);
                startActivity(intent);

            }
        });
    }

    private void initializeRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.InspectionView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantDetailsAdapter(this, inspectionList, new InspectionAdapterItemClickListener() {
            @Override
            public void onItemClicked(int pos) {
                Intent intent = new Intent(RestaurantDetailsActivity.this, InspectionDetailsActivity.class);
                intent.putExtra("RESTAURANT_INDEX", index);
                intent.putExtra("INSPECTION_INDEX", pos);
                intent.putExtra("LISTSTATE", listScreenState);
                intent.putExtra(InspectionDetailsActivity.KEY_INSPECTION, inspectionList.get(pos));
                Log.d(TAG, "parcelable " + inspectionList.get(pos));
                startActivity(intent);
            }
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycleview_divier));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("LongLogTag")
    private void initializeScreen() {
        Log.d(TAG, "initialized main screen");
        TextView restaurantName = findViewById(R.id.nameText);
        TextView restaurantAddress = findViewById(R.id.addressText);
        TextView restaurantGPS = findViewById(R.id.GPSText);

        restaurantName.setText(selectedRestaurant.getName());
        restaurantAddress.setText(selectedRestaurant.getAddress());
        String GPSMsg = selectedRestaurant.getLatitude() + "   " + selectedRestaurant.getLongitude();
        restaurantGPS.setText(GPSMsg);
    }
}