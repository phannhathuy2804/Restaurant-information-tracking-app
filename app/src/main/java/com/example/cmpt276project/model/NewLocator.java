package com.example.cmpt276project.model;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class NewLocator extends Fragment implements LocationListener {

    public NewLocator() {}

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
