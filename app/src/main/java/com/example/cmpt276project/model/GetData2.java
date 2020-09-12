/*
    This is the GetData class implementation.
    Creator: Lam, Ching Hang
    Email: CHL50@sfu.ca
    Last Modified Date: 2020/07/19

    This class requires to input two csv files.
    1. inspectionreports.csv
    2. restaurants.csv
 */
// Package
package com.example.cmpt276project.model;

// Import
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// GetData Class
public class GetData2 {

    InputStream restaurant;
    InputStream inspection;

    private static final String TAG = "GetData2";

    private List<Restaurant> restaurants;
    private List<Inspection> inspections;

    //------------------------------------------------------------------------------------------------------------------
    public class ItemsOrder {

        private int[] positionArray;
        private int count;

        public final String[] INSPECTION_KEYWORDS = {"TrackingNumber", "InspectionDate", "InspType", "NumCritical",
                "NumNonCritical","HazardRating","ViolLump"};
        public final String[] RESTAURANT_KEYWORDS = {"TRACKINGNUMBER", "NAME", "PHYSICALADDRESS", "PHYSICALCITY",
                "FACTYPE","LATITUDE","LONGITUDE"};
        public final int NUM_OF_INSPECTION_ITEMS = INSPECTION_KEYWORDS.length;
        public final int NUM_OF_RESTAURANT_ITEMS = RESTAURANT_KEYWORDS.length;

        //--------------------------------------------------------------------------------------------------------------
        // Constructors
        public ItemsOrder() {
            positionArray = null;
            count = 0;
        }

        //--------------------------------------------------------------------------------------------------------------
        // Returns the token positions related to INSPECTION_KEYWORDS
        // isInspection: true for find Inspection Items
        // isInspection: false for find Restaurant Items
        public int[] findItems(String row, boolean isInspection) {
            // Separate a String
            String[] items = row.replaceAll("\"", "").split(",");
            int size = items.length;

            // Inspection
            if(isInspection) {
                // # of items should be same as # of inspection items
                if (size != NUM_OF_INSPECTION_ITEMS) {
                    return null;
                }
            }
            // Restaurant
            else {
                // # of items should be same as # of restaurant items
                if (size != NUM_OF_RESTAURANT_ITEMS) {
                    return null;
                }
            }

            // Initialize the positionArray and count
            positionArray = new int[size];
            Arrays.fill(positionArray, -1);
            count = 0;

            // Loop for finding the Keywords
            // Inspection
            if (isInspection) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        // Match to the Keyword
                        if (items[i].toLowerCase().contains(INSPECTION_KEYWORDS[j].toLowerCase())) {
                            positionArray[j] = i;
                            count++;
                        }
                    }
                }
            }
            // Restaurant
            else {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        // Match to the Keyword
                        if (items[i].toLowerCase().contains(RESTAURANT_KEYWORDS[j].toLowerCase())) {
                            positionArray[j] = i;
                            count++;
                        }
                    }
                }
            }

            // count should be same as # of items
            if (count != size) {
                return null;
            }

            return positionArray;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public GetData2(InputStream restaurant, InputStream inspection) {
        this.restaurant = restaurant;
        this.inspection = inspection;

        restaurants = new ArrayList<>();
        inspections = new ArrayList<>();

        try {
            readInspectionData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            readRestaurantData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        Collections.sort(inspections, new Comparator<Inspection>() {
            @Override
            public int compare(Inspection o1, Inspection o2) {
                if (o1.getInspectionDate().after(o2.getInspectionDate())) {
                    return -1;
                }
                else if (o2.getInspectionDate().after(o1.getInspectionDate())) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------
    public void readInspectionData() throws IOException {

        // Read the File
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inspection, Charset.forName("UTF-8")));

        // Read the First Line
        String firstLine = bufferedReader.readLine();
        ItemsOrder itemsOrder = new ItemsOrder();
        int[] positionArray = itemsOrder.findItems(firstLine, true);

        // Error
        if (positionArray == null) {
            Log.e(TAG, "Cannot not load the Restaurant CSV File");
            return;
        }

        // Read from the Second Line
        String line = "";
        while((line = bufferedReader.readLine()) != null) {

            // Found incorrect line
            if (line.contains(",,,,,,"))
                return;

            // Separate the line
            String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            // Call the Inspection Constructor
            Inspection inspection = new Inspection(tokens[positionArray[0]].replaceAll("\"", ""),
                    DateAndTime.DATE_FORMAT_CSV_INPUT,
                    tokens[positionArray[1]].replaceAll("\"", ""),
                    tokens[positionArray[2]].replaceAll("\"", ""),
                    tokens[positionArray[3]].replaceAll("\"", ""),
                    tokens[positionArray[4]].replaceAll("\"", ""),
                    tokens[positionArray[5]].replaceAll("\"", ""),
                    tokens[positionArray[6]].replaceAll("\"", "").replace('�', '?'));

            // Add the Inspection into the ArrayList
            inspections.add(inspection);
        }
        bufferedReader.close();
    }

    //------------------------------------------------------------------------------------------------------------------
    public void readRestaurantData() throws IOException {

        // Read the File
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(restaurant, Charset.forName("UTF-8")));

        // Read the First Line
        String firstLine = bufferedReader.readLine();
        ItemsOrder itemsOrder = new ItemsOrder();
        int[] positionArray = itemsOrder.findItems(firstLine, false);

        // Error
        if (positionArray == null) {
            Log.e(TAG, "Cannot not load the Inspection CSV File");
            return;
        }

        // Read from the Second Line
        String line = "";
        while((line = bufferedReader.readLine()) != null) {

            // Found incorrect line
            if (line.contains(",,,,,,"))
                return;

            // Separate the line
            String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            // Input Restaurant Details
            Restaurant restaurant = new Restaurant();
            restaurant.setTrackingNumber(tokens[positionArray[0]].replaceAll("\"", ""));
            restaurant.setName(tokens[positionArray[1]].replaceAll("\"", ""));
            restaurant.setAddress(tokens[positionArray[2]].replaceAll("\"", ""));
            restaurant.setLatitude(Double.parseDouble(tokens[positionArray[5]].replaceAll("\"", "")));
            restaurant.setLongitude(Double.parseDouble(tokens[positionArray[6]].replaceAll("\"", "")));
            ArrayList<Inspection> inspections = new ArrayList<>();
            restaurant.setInspection(inspections);

            // Add the Restaurant into the ArrayList
            restaurants.add(restaurant);
        }
        bufferedReader.close();
    }

    //------------------------------------------------------------------------------------------------------------------
    // Getters
    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public List<Inspection> getInspections() {
        return inspections;
    }

    //------------------------------------------------------------------------------------------------------------------
    public void inputAllInspections() {
        for (int i = 0; i < inspections.size(); i++) {
            for (int j = 0; j < restaurants.size(); j++) {
                if (inspections.get(i).getTrackingNumber().equals(restaurants.get(j).getTrackingNumber())) {
                    restaurants.get(j).getInspection().add(inspections.get(i));
                    break;
                }
            }
        }
    }

    public void countCritical() {
        for (Restaurant restaurant: restaurants) {
            restaurant.countCriticalWithinOneYear();
        }
    }
}