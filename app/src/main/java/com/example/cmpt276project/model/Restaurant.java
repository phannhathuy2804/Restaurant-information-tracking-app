package com.example.cmpt276project.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;
import java.util.List;

//Desc: This class is used to store the information of every single restaurant including the trackingNumber, name, address, etc. and a list of Inspection which are inspected from that restaurant
public class Restaurant implements ClusterItem, Parcelable {

    private String trackingNumber;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int iconRestaurant;
    private int totalNumIssues;
    private String hazardLevelColor;
    private String lastInspectionDate;
    private List<Inspection> inspection;
    private int criticalCountWithinOneYear;


    boolean isFavorite = false;

    //Setters and getters
    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIconRestaurant() {
        return iconRestaurant;
    }

    public void setIconRestaurant(int iconRestaurant) {
        this.iconRestaurant = iconRestaurant;
    }

    public int getTotalNumIssues() {
        if(totalNumIssues == 0){
            setTotalNumIssues();
        }
        return totalNumIssues;
    }

    public void setTotalNumIssues() {
        for (int i=0; i<inspection.size();i++){
            totalNumIssues+=inspection.get(i).getNumOfCritical()
                    + inspection.get(i).getNumOfNonCritical();
        }
    }


    public String getHazardLevelColor() {
        return hazardLevelColor;
    }

    public void setHazardLevelColor(String hazardLevelColor) {
        this.hazardLevelColor = hazardLevelColor;
    }

    public void setLastInspectionDate() {
        int int_lastInspectionDate = 0;
        for (int i=0; i<inspection.size();i++) {
            int temp_inspect_date = Integer.parseInt(inspection.get(i).getInspectionDate().getDateString(DateAndTime.DATE_FORMAT_CSV_INPUT));
            if (temp_inspect_date>=int_lastInspectionDate){
                int_lastInspectionDate = temp_inspect_date;
            }
        }
        lastInspectionDate = Integer.toString(int_lastInspectionDate);
    }

    public String getLastInspectionDate() {
        if(lastInspectionDate == null){
            setLastInspectionDate();
        }
        return lastInspectionDate;
    }

    public List<Inspection> getInspection() {
        return inspection;
    }

    public void setInspection(List<Inspection> inspection) {
        for(int n = inspection.size()-1; n>=0; n--)
            for(int i=0; i<n; i++)
                if(Integer.parseInt(inspection.get(i).getInspectionDate().getDateString(DateAndTime.DATE_FORMAT_CSV_INPUT)) < Integer.parseInt(inspection.get(i + 1).getInspectionDate().getDateString(DateAndTime.DATE_FORMAT_CSV_INPUT))) {
                    Inspection temp = inspection.get(i);
                    inspection.set(i, inspection.get(i+1));
                    inspection.set(i+1, temp);
                }
        this.inspection = inspection;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // Get the latest inspection{
    public Inspection getTheMostRecentInspection(){
        if(inspection.size() > 0){
            return inspection.get(0);
        }
        return null;
    }

    public void countCriticalWithinOneYear() {
        // Initialize
        criticalCountWithinOneYear = 0;
        int size = inspection.size();

        // Loop for finding the Inspection Within One Year
        for (Inspection singleInspection: inspection) {
            if (singleInspection.getInspectionDate().timeDiffInDay() <= 365) {
                criticalCountWithinOneYear += singleInspection.getNumOfCritical();
            }
            else {
                break;
            }
        }
    }

    public int getCriticalCountWithinOneYear() {
        return criticalCountWithinOneYear;
    }


    @Override
    public String toString() {
        return "Restaurant{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", iconRestaurant=" + iconRestaurant +
                ", totalNumIssues=" + totalNumIssues +
                ", hazardLevelColor='" + hazardLevelColor + '\'' +
                ", lastInspectionDate='" + lastInspectionDate + '\'' +
                ", inspection=" + inspection +
                '}';
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return null;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lastInspectionDate);
        dest.writeList(this.inspection);
        dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
        dest.writeString(this.trackingNumber);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.iconRestaurant);
        dest.writeInt(this.totalNumIssues);
        dest.writeString(this.hazardLevelColor);
    }

    public Restaurant() {
    }

    protected Restaurant(Parcel in) {
        this.lastInspectionDate = in.readString();
        this.inspection = new ArrayList<Inspection>();
        in.readList(this.inspection, Inspection.class.getClassLoader());
        this.isFavorite = in.readByte() != 0;
        this.trackingNumber = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.iconRestaurant = in.readInt();
        this.totalNumIssues = in.readInt();
        this.hazardLevelColor = in.readString();
    }

    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel source) {
            return new Restaurant(source);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
