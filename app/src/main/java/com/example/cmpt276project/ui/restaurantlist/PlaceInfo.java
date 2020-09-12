package com.example.cmpt276project.ui.restaurantlist;

import com.example.cmpt276project.model.Inspection;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class PlaceInfo {
    private String name;
    private String address;
    private String trackingNumber;
    private String hazardLevelColor;
    private String lastInspectionDate;
    private LatLng latLng;
    private List<Inspection> inspection;

    public PlaceInfo(String name, String address, String trackingNumber, String hazardLevelColor, String lastInspectionDate, double lat, double lon, List<Inspection> inspection) {
        this.name = name;
        this.address = address;
        this.trackingNumber = trackingNumber;
        this.hazardLevelColor = hazardLevelColor;
        this.lastInspectionDate = lastInspectionDate;
        this.latLng = new LatLng(lat, lon);
        this.inspection = inspection;
    }

    public PlaceInfo() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getHazardLevelColor() {
        return hazardLevelColor;
    }

    public void setHazardLevelColor(String hazardLevelColor) {
        this.hazardLevelColor = hazardLevelColor;
    }

    public String getLastInspectionDate() {
        return lastInspectionDate;
    }

    public void setLastInspectionDate(String lastInspectionDate) {
        this.lastInspectionDate = lastInspectionDate;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(double lat, double lon) {
        this.latLng = new LatLng(lat, lon);
    }

    public List<Inspection> getInspection() {
        return inspection;
    }

    public void setInspection(List<Inspection> inspection) {
        this.inspection = inspection;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", hazardLevelColor='" + hazardLevelColor + '\'' +
                ", lastInspectionDate='" + lastInspectionDate + '\'' +
                ", latLng=" + latLng +
                ", inspection=" + inspection +
                '}';
    }
}
