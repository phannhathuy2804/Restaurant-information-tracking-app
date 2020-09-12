package com.example.cmpt276project.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
//Desc: Get the data from the input file to store in the list of restaurant
public class GetData {

    private static final String TAG = "GetData";

    InputStream restaurant;
    InputStream inspection;

    public GetData(InputStream restaurant, InputStream inspection){
        this.restaurant = restaurant;
        this.inspection = inspection;
    }

    //Desc: Read the information of the restaurants from the csv file
    public List<Restaurant> read(){
        List<Restaurant> restaurantList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(restaurant, Charset.forName("GBK")));
        String line = "";
        try {
            reader.readLine();
            while (((line = reader.readLine())!=null)) {

                String[] tokens = line.split(",");
                Log.d(TAG, Arrays.toString(tokens));
                Restaurant restaurant = new Restaurant();
                restaurant.setTrackingNumber(tokens[0]);
                restaurant.setName(tokens[1].substring(1, tokens[1].length()-1));
                restaurant.setAddress(tokens[2].substring(1, tokens[2].length()-1));
                restaurant.setLatitude(Double.parseDouble(tokens[5]));
                restaurant.setLongitude(Double.parseDouble(tokens[6]));
//                Log.d(TAG, "------- Tracking Number " + tokens[0]);
                List<Inspection> inspectionList = readInspection(tokens[0]);
                restaurant.setInspection(inspectionList);
//                Log.d(TAG, inspectionList.toString());

                restaurantList.add(restaurant);

                Log.d(TAG, restaurant.toString());
                Log.d(TAG, String.valueOf(restaurantList.size()));
            }
        } catch (IOException e) {
            Log.wtf(TAG, "Error reading restaurant data file on line"+ line, e);
            e.printStackTrace();
        }

        Collections.sort(restaurantList, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        return restaurantList;
    }

    //Desc: This function returns an array of inspections that is contained by the restaurant objects.
    private ArrayList<Inspection> readInspection(String trackingNumber) throws IOException {
        ArrayList<Inspection> inspections = new ArrayList<>();
        inspection.mark(0);
        BufferedReader reader = new BufferedReader((new InputStreamReader(inspection)));
        String line = "";
        try {
            line = reader.readLine();
            while ((line = reader.readLine()) != null){
                //The arguments in the below split function is used to prevent it splits the tokens that have the commas into the smaller tokens
                //Reference:https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (tokens[0].equals(trackingNumber)) {
                    Log.d(TAG, Arrays.toString(tokens));
                    Inspection inspection = new Inspection(tokens[0].substring(1, tokens[0].length()-1),
                            DateAndTime.DATE_FORMAT_CSV_INPUT,
                            tokens[1],
                            tokens[2].substring(1, tokens[2].length()-1),
                            tokens[3],
                            tokens[4],
                            tokens[5].substring(1, tokens[5].length()-1),
                            tokens[6]);
                    inspections.add(inspection);
                }
            }
        } catch (IOException e) {
            Log.wtf(TAG, "Error reading inspection data file on line"+ line, e);
        }
        inspection.reset();
        return inspections;
    }
}