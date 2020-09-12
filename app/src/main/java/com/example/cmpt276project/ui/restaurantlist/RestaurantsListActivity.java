package com.example.cmpt276project.ui.restaurantlist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.cmpt276project.R;
import com.example.cmpt276project.model.DBAdapter;
import com.example.cmpt276project.model.DateAndTime;
import com.example.cmpt276project.model.RestaurantManager;
import com.example.cmpt276project.model.Restaurant;
import com.example.cmpt276project.model.RestaurantManager;
import com.example.cmpt276project.model.DBAdapter;
import com.example.cmpt276project.model.RestaurantManager;
import com.example.cmpt276project.ui.restaurant.RestaurantDetailsActivity;
import com.example.cmpt276project.ui.welcome.WelcomeActivity;

import org.w3c.dom.Text;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

/**
 *  RestaurantListActivity
 *  Display either RestaurantListFragment or RestaurantMapFragment based on the state of toggle button
 *  Default screen shows RestaurantMapFragment
 */

public class RestaurantsListActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantsListActivity";

    /**
     * KEY to get value in intent
     */
    public static final String KEY_RESTAURANT = "restaurant";
    public static final String KEY_STATE = "state";
    private static final String KEY_COLOR = "color";
    private static final String KEY_IS_FAV = "isFav";
    public static final String KEY_UPDATE = "is_update_key";


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1337;
    DBAdapter myDb;

    private SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(RestaurantsListActivity.this);

    private RestaurantManager manager = RestaurantManager.getInstance();

    /**
     * if savedIndex is -1, we show the current location of a user at the center of the map
     * Otherwise, show the restaurant exists at the index of savedIndex at the center of the map
     */
    private Restaurant prevVisitedRestaurant = null;

    /**
     * if Map is shown to users, isMapShown is true. Otherwise false
     * Default screen setting is showing map to a user
     */
    private boolean isMapShown = true;

    /**
     * search filter variables
     */
    private String query = "";
    private String queryColor = "All";
    private boolean queryIsFav = false;
    private int queryMin;
    private int queryMax;

    /**
     * List of restaurant satisfying all search filters
     */
    private List<Restaurant> filteredRestaurantList = new ArrayList<>();

    /**
     * UI components
     */
    private LinearLayout filterContainer;
    private Spinner colorInput;
    private Switch isFavInput;
    private Spinner minInput;
    private Spinner maxInput;
    private Button resetBtn;
    private SearchView searchView;

    private ArrayAdapter adapter;

    private boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);

        query = helper.getQuery();
        queryColor = helper.getQueryColor();

        if(TextUtils.equals(queryColor, "All") ||
            TextUtils.equals(queryColor, "Low") ||
            TextUtils.equals(queryColor, "Moderate") || TextUtils.equals(queryColor, "High")){
            // do nothing
        }
        else if(TextUtils.equals(queryColor, getResources().getStringArray(R.array.spinner_color)[0])){
            queryColor = "All";
        }
        else if(TextUtils.equals(queryColor, getResources().getStringArray(R.array.spinner_color)[1])){
            queryColor = "Low";
        }
        else if(TextUtils.equals(queryColor, getResources().getStringArray(R.array.spinner_color)[2])){
            queryColor = "Moderate";
        }
        else{
            queryColor = "High";
        }

        queryIsFav = helper.getFav();
        queryMin = helper.getQueryMin();
        queryMax = helper.getQueryMax();

        filterContainer = findViewById(R.id.filter_conatiner_parent);
        colorInput = findViewById(R.id.spinner_color);
        isFavInput = findViewById(R.id.switch_isFav);
        minInput = findViewById(R.id.min_range);
        maxInput = findViewById(R.id.max_range);
        resetBtn = findViewById(R.id.btn_reset);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.equals(query, "") || !TextUtils.equals(queryColor, "All") || queryIsFav || queryMin != 0 || queryMax != 100) {
                    searchView.clearFocus();
                    helper.saveFilters("", "All", false, 0, 100);
                    Intent intent = new Intent(RestaurantsListActivity.this, RestaurantsListActivity.class);
                    intent.putExtra(KEY_STATE, isMapShown);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.restaurant_list_toolbar);
        setSupportActionBar(toolbar);

        // this is just boilerplate code. can ignore ----
        String[] minRange = new String[102];
        String[] maxRange = new String[102];
        minRange[0] = getString(R.string.search_filter_min);
        maxRange[0] = getString(R.string.search_filter_max);
        for(int i = 0; i < 101; i++){
            minRange[i+1] = String.valueOf(i);
            maxRange[i+1] = String.valueOf(i);
        }

        ArrayAdapter<String> minAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, minRange);
        ArrayAdapter<String> maxAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, maxRange);
        minInput.setAdapter(minAdapter);
        maxInput.setAdapter(maxAdapter);
        // ignore till here

        // check whether values was given inside an intent from other Activity
        Intent intent = getIntent();

        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            Bundle bundle = intent.getBundleExtra(SearchManager.APP_DATA);
            if(bundle != null){
                isMapShown = bundle.getBoolean(KEY_STATE);
            }
        }
        else if(intent.getExtras() != null){
            Log.d(TAG, "search happened " + query);
            prevVisitedRestaurant = intent.getParcelableExtra(KEY_RESTAURANT);
            isMapShown = intent.getBooleanExtra(KEY_STATE, true);
            isUpdate = intent.getBooleanExtra(WelcomeActivity.KEY_UPDATE, false);
        }

        openDB();

        // filter restaurantList
        filterRestaurant();

        if(isMapShown)
            createRestaurantMapFragment();
        else
            createRestaurantListFragment();

        if (isUpdate) {
            createFavoriteDialog();
        }
    }

    private void openDB() {
        myDb = new DBAdapter(this);
        myDb.open();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myDb.close();
    }

    private void createFavoriteDialog() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(RestaurantsListActivity.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        RestaurantManager manager = RestaurantManager.getInstance();
        builderSingle.setTitle(R.string.search_filter_found_new_inspections);

        // Counter
        int count = 0;

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RestaurantsListActivity.this, android.R.layout.simple_list_item_1);
        Cursor cursor = myDb.getAllRows();
        if (cursor.moveToFirst()){
            do {
                String message = "";
                String restaurantName = cursor.getString(DBAdapter.COL_NAME);
                String trackingNumber = cursor.getString(DBAdapter.COL_RESID);
                int index = findTheIndexOfRestaurant(trackingNumber);

                if (index >= 0) {
                    Restaurant favRestaurant = manager.getRestaurant(index);

                    DateAndTime cursorDate = new DateAndTime(DateAndTime.DATE_FORMAT_CSV_INPUT, cursor.getString(DBAdapter.COL_MOSTRECENT));
                    DateAndTime newInspectionDate =  favRestaurant.getTheMostRecentInspection().getInspectionDate();

                    if (newInspectionDate.after(cursorDate)) {
                            String date = manager.getRestaurant(index).getTheMostRecentInspection().getInspectionDate().getDateString("yyyy/MM/dd");
                            String hazardLevel = manager.getRestaurant(index).getTheMostRecentInspection().getHazardLevel().getResourceString();
                            message = restaurantName + ", " + date + ", " + hazardLevel;


                            myDb.updateRow(favRestaurant.getTrackingNumber(),
                                    favRestaurant.getName(),
                                    favRestaurant.getTheMostRecentInspection().getInspectionDate().getDateString(DateAndTime.DATE_FORMAT_CSV_INPUT),
                                    favRestaurant.getHazardLevelColor());

                            arrayAdapter.add(message);
                            count++;
                            Log.e(TAG, "" + cursor.getString(DBAdapter.COL_MOSTRECENT));
                    }
                }
            } while (cursor.moveToNext());
        }

        builderSingle.setPositiveButton(R.string.search_filter_ok_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            if (count != 0) {
                builderSingle.show();
            }

    }

    private int findTheIndexOfRestaurant(String trackingName) {
        for (int i = 0; i < manager.getRestaurants().size(); i++) {
            if (trackingName.equals(manager.getRestaurant(i).getTrackingNumber())) {
                return i;
            }
        }
        return -1;
    }



    private void filterRestaurant(){

        Log.d(TAG, "filter " + query + " " + queryColor + " " + queryIsFav + " " + queryMin + " " + queryMax);
        List<Restaurant> restaurantList = new ArrayList<>();

        for(Restaurant restaurant : manager.getRestaurants()){
            restaurant.setFavorite(false);
        }

        Cursor cursor = myDb.getAllRows();
        if(queryIsFav) {

            if(cursor.moveToFirst()) {
                do {
                    String tracker = cursor.getString((DBAdapter.COL_RESID));
                    Restaurant favRestaurant = findCorrectRestaurant(tracker);
                    if(favRestaurant != null) restaurantList.add(favRestaurant);

                }while(cursor.moveToNext());
            }
            for(Restaurant restaurant : restaurantList){
                for(int i = 0; i < manager.getRestaurants().size(); i++){
                    if(TextUtils.equals(restaurant.getTrackingNumber(), manager.getRestaurants().get(i).getTrackingNumber())){
                        manager.getRestaurant(i).setFavorite(true);
                    }
                }
            }
            Log.d(TAG, "database size " + restaurantList.size());
        }
        else{

            if(cursor.moveToFirst()) {
                do {
                    String tracker = cursor.getString((DBAdapter.COL_RESID));
                    Restaurant favRestaurant = findCorrectRestaurant(tracker);
                    if(favRestaurant != null) restaurantList.add(favRestaurant);
                }while(cursor.moveToNext());
            }
            Log.d(TAG, "database size " + restaurantList.size());

            for(Restaurant restaurant : restaurantList){
                for(int i = 0; i < manager.getRestaurants().size(); i++){
                    if(TextUtils.equals(restaurant.getTrackingNumber(), manager.getRestaurants().get(i).getTrackingNumber())){
                        manager.getRestaurant(i).setFavorite(true);
                    }
                }
            }

            restaurantList.clear();
            restaurantList.addAll(manager.getRestaurants());
        }

        Log.d(TAG, "filtered size " + restaurantList.size());

        if( TextUtils.equals(query, "") && TextUtils.equals(queryColor, "All") && !queryIsFav){
            filteredRestaurantList.addAll(restaurantList);
            return;
        }

        for(Restaurant restaurant : restaurantList){

            // filter search word
            if( !TextUtils.equals(query, "") && !restaurant.getName().toLowerCase().contains(query)){
                continue;
            }
            // filter hazard level color
            if( restaurant.getInspection().size() == 0 || ( !TextUtils.equals(queryColor, "All")
                    && !TextUtils.equals(queryColor.toLowerCase(), restaurant.getTheMostRecentInspection().getHazardLevel().toString().toLowerCase()))){
                continue;
            }
            // filter critical count
            int count = restaurant.getCriticalCountWithinOneYear();
            Log.d(TAG, String.valueOf(count));
            if(count < queryMin || count > queryMax) {
                continue;
            }

            filteredRestaurantList.add(restaurant);
        }

    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restaurant_list, menu);

        MenuItem searchMenu = menu.findItem(R.id.menu_search);

        searchMenu.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d(TAG, "menu expanded");
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_from_top);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) { filterContainer.setVisibility(View.VISIBLE); }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                filterContainer.setAnimation(animation);
                animation.start();

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "menu collapsed");
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_to_up);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) { filterContainer.setVisibility(View.GONE);}

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                filterContainer.setAnimation(animation);
                animation.start();
                filterContainer.setVisibility(View.GONE);
                return true;
            }
        });

        searchView = (SearchView) searchMenu.getActionView();
        searchView.setSubmitButtonEnabled(true);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                String selectedItem = (String) adapter.getItem(position);
                Log.d(TAG, selectedItem + " selected at" + position);
                searchView.setQuery(selectedItem.toLowerCase(), true);
                return true;
            }
        });

        // test autocomplete
        SearchView.SearchAutoComplete autoComplete = searchView.findViewById(R.id.search_src_text);
        autoComplete.setHint(R.string.search_filter_restaurant_name);
        autoComplete.setHintTextColor(getResources().getColor(R.color.white_bg));
        if(query.length() > 0){
            autoComplete.setText(query);
        }
        autoComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        List<String> restaurantNames = new ArrayList<>();
        for(Restaurant res : RestaurantManager.getInstance().getRestaurants()){
            restaurantNames.add(res.getName());
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, restaurantNames);
        autoComplete.setAdapter(adapter);
        autoComplete.setThreshold(1);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit " + query);
                FrameLayout container = findViewById(R.id.restaurant_list_container);
                container.setVisibility(View.GONE);

                Bundle bundle = new Bundle();
                String color = colorInput.getSelectedItem().toString();
                boolean isFav = isFavInput.isChecked();
                String minStr = (String) minInput.getSelectedItem();
                String maxStr = (String) maxInput.getSelectedItem();
                Log.d(TAG, "minString " +  minStr + " maxStr " + maxStr);

                if(TextUtils.equals(minStr, getResources().getString(R.string.search_filter_min))) queryMin = 0;
                else queryMin = Integer.parseInt(minStr);

                if(TextUtils.equals(maxStr, getResources().getString(R.string.search_filter_max))) queryMax = 100;
                else queryMax = Integer.parseInt(maxStr);

                bundle.putBoolean(KEY_STATE, isMapShown);

                searchView.setAppSearchData(bundle);

                SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(RestaurantsListActivity.this);
                helper.saveFilters(query, color, isFav, queryMin, queryMax);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange " + newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_search:
                Log.d(TAG, "search menu clicked");
                break;

            // control which view is displayed to users
            case R.id.menu_switch:
                Log.d(TAG, "switch menu clicked");
                if(!isMapShown){
                    createRestaurantMapFragment();
                    item.setIcon(R.drawable.ic_baseline_format_list_bulleted_24);
                    isMapShown = true;
                    setTitle(R.string.restaurant_list_activity_title_map);
                }
                else{
                    createRestaurantListFragment();
                    item.setIcon(R.drawable.ic_baseline_map_24);
                    isMapShown = false;
                    setTitle(R.string.restaurant_list_activity_title_list);
                }
                break;
        }
        return true;
    }

    /**
     *  Create RestaurantListFragment and set it to FrameLayout
     *  not added to back stack
     */
    public void createRestaurantListFragment(){
        RestaurantListFragment listFragment = RestaurantListFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.restaurant_list_container, listFragment).commit();
    }

    /**
     *  Create RestaurantMapFragment and set it to FrameLayout
     *  not added to back stack
     */
    public void createRestaurantMapFragment(){
        RestaurantMapFragment mapFragment = RestaurantMapFragment.newInstance(prevVisitedRestaurant);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.restaurant_list_container, mapFragment).commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);                             ////code from https://stackoverflow.com/questions/2354336/how-to-exit-when-back-button-is-pressed
    }

    /**
     *  Called from a child fragment. Method to move to RestaurantDetailsFragment.
     */
    public void moveToRestaurantDetailActivity(int position){
        Intent intent = new Intent(RestaurantsListActivity.this, RestaurantDetailsActivity.class);
        // putExtra will come here
        intent.putExtra(RestaurantDetailsActivity.KEY_STATE, isMapShown);
        intent.putExtra(RestaurantDetailsActivity.KEY_RESTAURANT, filteredRestaurantList.get(position));
        Log.d(TAG, "parcelable " + filteredRestaurantList.get(position).toString());
        startActivity(intent);
    }

    public void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult called");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Granted");

                    finishAffinity();
                    Intent intent = new Intent(RestaurantsListActivity.this, RestaurantsListActivity.class);
                    startActivity(intent);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "onRequestPermissionsResult: Rejected");
                    Toast.makeText(getApplicationContext(), R.string.restaurant_list_activity_permission_denied, Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public List<Restaurant> getFilteredRestaurantList() {
        return filteredRestaurantList;
    }

    public Restaurant findCorrectRestaurant(String TrackingID) {
        for(int i=0; i<manager.getRestaurants().size(); i++) {
            if(TrackingID.equals(manager.getRestaurant(i).getTrackingNumber())) {
                return manager.getRestaurant(i);
            }
        }
        return null;
    }

    public void insertDatabase(String resId, String name, String recent, String hazardLevel) {
        myDb.insertRow(resId, name, recent, hazardLevel);
        Log.d(TAG, "=====Inserted");
    }

    public void deleteFavouriteRestaurant(String trackingNumber){
        myDb.deleteRow(trackingNumber);
        Log.d(TAG, "=====deleted");
    }
}