package com.example.cmpt276project.ui.restaurantlist;

import androidx.annotation.Nullable;

import com.example.cmpt276project.model.Inspection;
import com.example.cmpt276project.model.RestaurantManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterItems implements ClusterItem {

    private RestaurantManager manager = RestaurantManager.getInstance();
    private LatLng mPosition;
    private String mTitle;
    private Inspection.HazardLevel mHazardLevel;
    private String mSnippet;
    private int index;


    public ClusterItems(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public ClusterItems(double lat, double lng, String title, Inspection.HazardLevel HazardLevel, String snippet, int index) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mHazardLevel = HazardLevel;
        mSnippet = snippet;
        this.index = index;
    }

    public Inspection.HazardLevel getmHazardLevel() {
        return mHazardLevel;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public int getIndex() {
        return index;
    }
}