/*
    ------------------------------------------------------------------------------------------------
    Violation Class Implementation
    This class uses for analyzing the violation
    Creator: Lam, Ching Hang
    Email: CHL50@sfu.ca
    Last Modified Date: 2020/07/30
    ------------------------------------------------------------------------------------------------
    This class requires a string of violation as parameter.
    1. String description
    ------------------------------------------------------------------------------------------------
    Remark:
    1. If the input description is empty, then the criticality is set to ERROR
    2. If the system cannot analyze the criticality, then the criticality is set to ERROR
    ------------------------------------------------------------------------------------------------
    nature is an boolean array which index representing the natures
    (true for exist, false for does not exist)
    - index 0 = equipment
    - index 1 = utensil
    - index 2 = food
    - index 3 = pest
    - index 4 = employee
    ------------------------------------------------------------------------------------------------
 */
// Package
package com.example.cmpt276project.model;

// Import
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.cmpt276project.R;

import java.util.Arrays;

// Violation Class
public class Violation implements Parcelable {

    private static Context context;

    // Violation Input
    private String description;

    // Violation Outputs
    private Criticality criticality;
    private boolean[] nature;
    private String shortDescription;
    private String longDescription;

    private static final String TAG = "Violation";
    private static final int NUM_OF_COMMAS = 3;

    //----------------------------------------------------------------------------------------------
    // Criticality Enum
    public enum Criticality {

        CRITICAL("critical", context.getString(R.string.violation_class_critical)),
        NON_CRITICAL("not critical", context.getString(R.string.violation_class_not_critical)),
        ERROR("error", context.getString(R.string.violation_class_error));

        //------------------------------------------------------------------------------------------

        private final String SEARCH_KEY;
        private final String RESOURCE_STRING;

        public static final int NUM_OF_ITEMS = Criticality.values().length;

        //------------------------------------------------------------------------------------------
        // Constructor
        Criticality(final String SEARCH_KEY, final String RESOURCE_STRING) {
            this.SEARCH_KEY = SEARCH_KEY;
            this.RESOURCE_STRING = RESOURCE_STRING;
        }

        //------------------------------------------------------------------------------------------
        // Getter
        // Returns a String of Search Key
        public String getSearchKey() {
            return this.SEARCH_KEY;
        }

        //------------------------------------------------------------------------------------------
        // Getter
        // Returns a String of Resource String
        public String getResourceString() {
            return this.RESOURCE_STRING;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Nature Enum
    public enum Nature {

        EQUIPMENT("equipment", context.getString(R.string.violation_class_equipment)),
        UTENSIL("utensil", context.getString(R.string.violation_class_utensil)),
        FOOD("food", context.getString(R.string.violation_class_food)),
        PEST("pest", context.getString(R.string.violation_class_pest)),
        EMPLOYEE("employee", context.getString(R.string.violation_class_employee));

        //------------------------------------------------------------------------------------------

        private final String SEARCH_KEY;
        private final String RESOURCE_STRING;

        public static final int NUM_OF_ITEMS = Nature.values().length;
        public static final String[] SEARCH_KEY_ARRAY = {EQUIPMENT.SEARCH_KEY, UTENSIL.SEARCH_KEY, FOOD.SEARCH_KEY, PEST.SEARCH_KEY, EMPLOYEE.SEARCH_KEY};
        public static final String[] RESOURCE_STRING_ARRAY = {EQUIPMENT.RESOURCE_STRING, UTENSIL.RESOURCE_STRING, FOOD.RESOURCE_STRING, PEST.RESOURCE_STRING, EMPLOYEE.RESOURCE_STRING};

        //------------------------------------------------------------------------------------------
        // Constructor
        Nature(final String SEARCH_KEY, final String RESOURCE_STRING) {
            this.SEARCH_KEY = SEARCH_KEY;
            this.RESOURCE_STRING = RESOURCE_STRING;
        }

        //------------------------------------------------------------------------------------------
        // Getter
        // Returns a String of Search Key
        public String getSearchKey() {
            return this.SEARCH_KEY;
        }

        //------------------------------------------------------------------------------------------
        // Getter
        // Returns a String of Resource String
        public String getResourceString() {
            return this.RESOURCE_STRING;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Constructor
    public Violation(String description) {
        this.description = description;
        this.criticality = Criticality.ERROR; // Set the criticality as ERROR
        this.nature = new boolean[Nature.NUM_OF_ITEMS];
        Arrays.fill(this.nature, Boolean.FALSE);
        this.shortDescription = "";
        this.longDescription = "";

        violationAnalysis();
    }

    //----------------------------------------------------------------------------------------------
    // Setter
    // Requires to pass the context as parameter before using this class
    // The getString method requires context to assess the string resources
    public static void setContext(Context context){
        Violation.context = context;
    }

    //----------------------------------------------------------------------------------------------
    // Violation Analysis
    // Analyzes the positions of commas and separates to 4 substrings
    private void violationAnalysis() {
        int length = description.length();

        // Empty Description
        if (length == 0) {
            return;
        }

        // Find the index of comma
        int[] indexOfComma = new int[NUM_OF_COMMAS];
        int commaCount = 0;
        int lastCommaIndex = 0;
        for (int i = 0; i < length; i++) {
            if (description.charAt(i) == ',') {
                commaCount++;

                if (commaCount <= 2) {
                    indexOfComma[commaCount - 1] = i;
                }
                else {
                    lastCommaIndex = i;
                }
            }
        }
        indexOfComma[2] = lastCommaIndex;

        // Error Case: # of commas less than 3
        if (commaCount < NUM_OF_COMMAS) {
            return;
        }

        // Call the methods to analyze
        // Find the Keyword Critical or Not Critical
        if(criticalityAnalysis(indexOfComma[0] + 1, indexOfComma[1] - 1)) {
            natureAnalysis(indexOfComma[1] + 1, indexOfComma[2] - 1);
            longDescriptionAnalysis(indexOfComma[1] + 1, indexOfComma[2] - 1);
            shortDescriptionAnalysis();
        }
    }

    //----------------------------------------------------------------------------------------------
    // Violation Analysis
    // Determines the Criticality
    private boolean criticalityAnalysis(int startIndex, int endIndex) {
        String substring = (description.substring(startIndex, endIndex + 1)).toLowerCase();

        // NON_CRITICAL
        if (substring.equalsIgnoreCase(Criticality.NON_CRITICAL.SEARCH_KEY)) {
            criticality = Criticality.NON_CRITICAL;
        }
        // CRITICAL
        else if (substring.equalsIgnoreCase(Criticality.CRITICAL.SEARCH_KEY)) {
            criticality = Criticality.CRITICAL;
        }
        // ERROR
        else {
            return false;
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------
    // Violation Analysis
    // Copies the substring to longDescription
    private void longDescriptionAnalysis(int startIndex, int endIndex) {
        longDescription = description.substring(startIndex, endIndex + 1);
    }

    //----------------------------------------------------------------------------------------------
    // Violation Analysis
    // Determines the Nature
    private void natureAnalysis(int startIndex, int endIndex) {
        String substring = (description.substring(startIndex, endIndex + 1)).toLowerCase();

        for (int i = 0; i < Nature.NUM_OF_ITEMS; i++) {
            if (substring.contains(Nature.SEARCH_KEY_ARRAY[i])) {
                nature[i] = true;
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // Violation Analysis
    // Determines the shortDescription
    private void shortDescriptionAnalysis() {

        boolean allDoesNotExist = true;
        String shortDescription = "";

        for (int i = 0; i < Nature.NUM_OF_ITEMS; i++) {
            if (nature[i] == true) {
                if (!allDoesNotExist) {
                    shortDescription += "/";
                }

                shortDescription += Nature.RESOURCE_STRING_ARRAY[i];
                allDoesNotExist = false;
            }
        }

        if (allDoesNotExist)
            shortDescription = context.getString(R.string.violation_class_other);

        shortDescription += (" " + context.getString(R.string.violation_class_issue));
        this.shortDescription = shortDescription;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns a String of Description
    public String getDescription() {
        return description;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns a Criticality
    public Criticality getCriticality() {
        return criticality;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns an Int Array of Nature
    public boolean[] getNature() {
        return nature;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns a String Array of Short Description
    public String getShortDescription() {
        return shortDescription;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns a String Array of Long Description
    public String getLongDescription() {
        return longDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeInt(this.criticality == null ? -1 : this.criticality.ordinal());
        dest.writeBooleanArray(this.nature);
        dest.writeString(this.shortDescription);
        dest.writeString(this.longDescription);
    }

    protected Violation(Parcel in) {
        this.description = in.readString();
        int tmpCriticality = in.readInt();
        this.criticality = tmpCriticality == -1 ? null : Criticality.values()[tmpCriticality];
        this.nature = in.createBooleanArray();
        this.shortDescription = in.readString();
        this.longDescription = in.readString();
    }

    public static final Parcelable.Creator<Violation> CREATOR = new Parcelable.Creator<Violation>() {
        @Override
        public Violation createFromParcel(Parcel source) {
            return new Violation(source);
        }

        @Override
        public Violation[] newArray(int size) {
            return new Violation[size];
        }
    };
}