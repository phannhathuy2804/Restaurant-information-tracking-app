/*
    ------------------------------------------------------------------------------------------------
    Inspection Class Implementation
    This class uses for showing the inspection of related restaurant
    Creator: Lam, Ching Hang
    Email: CHL50@sfu.ca
    Last Modified Date: 2020/07/30
    ------------------------------------------------------------------------------------------------
    This class requires 8 inputs
    1. String trackingNumber,
    2. String dateFormat (stored in DateAndTime class)
    3. String inspectionDateString
    4. String inspectionTypeString (Routine, Follow-Up)
    5. String numOfCriticalString
    6. String numOfNonCriticalString
    7. String hazardLevelString (Low, Moderate, High)
    8. String violationsString
    ------------------------------------------------------------------------------------------------
    Remark:
    1. The parameter violationsString is a String that can contain more than one violation.
    2. violationsString requires violationsAnalysis method to separate.
    3. Two Critical Numbers from the input should be same as Two Critical Numbers after Analysis.
    4. If they are not match, this class will use the data based on Analysis.
    ------------------------------------------------------------------------------------------------
 */
// Package
package com.example.cmpt276project.model;

// Import
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.example.cmpt276project.R;
import java.util.ArrayList;

// Inspection Class
public class Inspection implements Parcelable {

    private static Context context;

    private String trackingNumber;
    private DateAndTime inspectionDate;
    private InspectionType inspectionType;
    private int numOfCritical;
    private int numOfNonCritical;
    private HazardLevel hazardLevel;
    private ArrayList<Violation> violations;

    private static final String TAG = "Inspection";

    //----------------------------------------------------------------------------------------------
    // InspectionType Enum
    public enum InspectionType {

        ROUTINE("routine", context.getString(R.string.inspection_class_routine)),
        FOLLOW_UP("follow-up", context.getString(R.string.inspection_class_follow_up));

        //------------------------------------------------------------------------------------------

        private final String SEARCH_KEY;
        private final String RESOURCE_STRING;

        //------------------------------------------------------------------------------------------
        // Constructor
        InspectionType(final String SEARCH_KEY, final String RESOURCE_STRING) {
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
    // HazardLevel Enum
    public enum HazardLevel {

        LOW("low", context.getString(R.string.inspection_class_low)),
        MODERATE("moderate", context.getString(R.string.inspection_class_moderate)),
        HIGH("high", context.getString(R.string.inspection_class_high));

        //------------------------------------------------------------------------------------------

        private final String SEARCH_KEY;
        private final String RESOURCE_STRING;

        //------------------------------------------------------------------------------------------
        // Constructor
        HazardLevel(final String SEARCH_KEY, final String RESOURCE_STRING) {
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
    public Inspection(String trackingNumber, String dateFormat, String inspectionDateString, String inspectionTypeString,
                      String numOfCriticalString, String numOfNonCriticalString, String hazardLevelString,
                      String violationsString) {

        this.trackingNumber = trackingNumber;
        this.inspectionDate = new DateAndTime(dateFormat, inspectionDateString);

        // Initializes the Inspection Type, Critical Numbers, and Hazard Level
        initializeInspectionType(inspectionTypeString);
        initializeCritical(numOfCriticalString, numOfNonCriticalString);
        initializeHazardLevel(hazardLevelString);

        // Initializes the violations
        this.violations = new ArrayList<Violation>();
        violationsAnalysis(violationsString);
    }

    //----------------------------------------------------------------------------------------------
    // Setter
    // Requires to pass the context as parameter before using this class
    // The getString method requires context to assess the string resources
    public static void setContext(Context context){
        Inspection.context = context;
    }

    //----------------------------------------------------------------------------------------------
    // Initialization
    // Initializes the Inspection Type
    // If there is an error, then the inspection type is set to Routine
    private void initializeInspectionType(String inspectionTypeString) {
        // Routine Case
        if (inspectionTypeString.equalsIgnoreCase(InspectionType.ROUTINE.SEARCH_KEY)) {
            this.inspectionType = InspectionType.ROUTINE;
        }
        // Follow-Up Case
        else if (inspectionTypeString.equalsIgnoreCase(InspectionType.FOLLOW_UP.SEARCH_KEY)) {
            this.inspectionType = InspectionType.FOLLOW_UP;
        }
        // Error Case
        else {
            Log.e(TAG, "Invalid Input - Inspection Type");
            this.inspectionType = InspectionType.ROUTINE;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Initialization
    // Initializes the Critical Number and Non-Critical Number
    private void initializeCritical(String numOfCriticalString, String numOfNonCriticalString) {
        // Number of Critical Issues
        int numOfCritical = 0;
        try {
            numOfCritical = Integer.parseInt(numOfCriticalString);
        }
        catch (Exception e) {
            invalidInputCritical();
            return;
        }
        if (numOfCritical >= 0) {
            this.numOfCritical = numOfCritical;
        }
        else {
            invalidInputCritical();
            return;
        }

        // Number of Non-Critical Issues
        int numOfNonCritical = 0;
        try {
            numOfNonCritical = Integer.parseInt(numOfNonCriticalString);
        }
        catch (Exception e) {
            invalidInputCritical();
            return;
        }
        if (numOfNonCritical >= 0) {
            this.numOfNonCritical = numOfNonCritical;
        }
        else {
            invalidInputCritical();
            return;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Error Correction
    // If there is an error, then the Critical Numbers are set to 0
    private void invalidInputCritical() {
        Log.e(TAG, "Invalid Input - Number of Critical Issues or Non-Critical Issues");
        this.numOfCritical = 0;
        this.numOfNonCritical = 0;
    }

    //----------------------------------------------------------------------------------------------
    // Initialization
    // Initializes the Hazard Level
    // If there is an error, then the Hazard Level is set to Low
    private void initializeHazardLevel(String hazardLevelString) {
        // Set the Hazard Level
        // LOW
        if (hazardLevelString.equalsIgnoreCase(HazardLevel.LOW.SEARCH_KEY)) {
            this.hazardLevel = HazardLevel.LOW;
        }
        // MODERATE
        else if (hazardLevelString.equalsIgnoreCase(HazardLevel.MODERATE.SEARCH_KEY)) {
            this.hazardLevel = HazardLevel.MODERATE;
        }
        // HIGH
        else if (hazardLevelString.equalsIgnoreCase(HazardLevel.HIGH.SEARCH_KEY)) {
            this.hazardLevel = HazardLevel.HIGH;
        }
        // EMPTY
        else if (hazardLevelString.equals("")) {
            Log.e(TAG, "Invalid Input - Hazard Level is Empty");
            this.hazardLevel = HazardLevel.LOW;
        }
        // Invalid Input
        else {
            Log.e(TAG, "Invalid Input - Hazard Level");
            this.hazardLevel = HazardLevel.LOW;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Violation Analysis
    // Separates a String to more than one substring
    private void violationsAnalysis(String violationsString) {
        int length = violationsString.length();

        // Empty String
        if (length == 0) {
            // Check Number of Critical / Non-Critical
            checkNumOfCritical();
            return;
        }

        // Loop for separating violations from a string
        int startIndex = 0;
        for (int i = 0; i < length; i++) {
            if (violationsString.charAt(i) == '|') {
                String substring = violationsString.substring(startIndex, i);
                Violation violation = new Violation(substring);

                // If there is an error in this violation, then does not add to the ArrayList
                if (violation.getCriticality() != Violation.Criticality.ERROR) {
                    violations.add(violation);
                }
                startIndex = i + 1;
            }
        }

        // Only one violation or the Last one
        String substring = violationsString.substring(startIndex, length);
        Violation violation = new Violation(substring);

        // If there is an error in this violation, then does not add to the ArrayList
        if (violation.getCriticality() != Violation.Criticality.ERROR) {
            violations.add(violation);
        }

        // Check Number of Critical / Non-Critical
        checkNumOfCritical();
    }

    //----------------------------------------------------------------------------------------------
    // Error Checking
    // Checks the Critical Numbers after Analysis
    // If the values does not match, then set the Critical Numbers based on Analysis
    private void checkNumOfCritical() {

        int numOfIssues = violations.size();

        // Empty violation
        if (numOfIssues == 0) {
            if (numOfCritical != 0 || numOfNonCritical != 0) {
                Log.e(TAG, "The Number of Critical or Number of NonCritical is not equal to zero");
                this.numOfCritical = 0;
                this.numOfNonCritical = 0;
            }
            return;
        }

        int criticalCount = 0;
        int nonCriticalCount = 0;
        int errorCount = 0;

        for (int i = 0; i < numOfIssues; i++) {
            if (violations.get(i).getCriticality() == Violation.Criticality.CRITICAL) {
                criticalCount++;
            }
            else if (violations.get(i).getCriticality() == Violation.Criticality.NON_CRITICAL) {
                nonCriticalCount++;
            }
            else {
                errorCount++;
            }
        }

        // Error Checking
        if (errorCount != 0 || numOfCritical != criticalCount || numOfNonCritical != nonCriticalCount) {
            Log.e(TAG, "The Number of Critical Issues or Non-Critical Issues does not match to the violations");
            this.numOfCritical = criticalCount;
            this.numOfNonCritical = nonCriticalCount;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the Tracking Number
    public String getTrackingNumber() {
        return trackingNumber;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the Inspection Date
    public DateAndTime getInspectionDate() {
        return inspectionDate;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the Inspection Type
    public InspectionType getInspectionType() {
        return inspectionType;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the Critical Number
    public int getNumOfCritical() {
        return numOfCritical;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the Non-Critical Number
    public int getNumOfNonCritical() {
        return numOfNonCritical;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the Hazard Level
    public HazardLevel getHazardLevel() {
        return hazardLevel;
    }

    public void setHazardLevel(HazardLevel hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the ArrayList of Violations
    public ArrayList<Violation> getViolations() {
        return violations;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns an boolean array of Criticality
    // True for Critical, False for Non-Critical
    public boolean[] getAllCriticality() {
        int numOfIssues = violations.size();
        boolean[] allCriticality = new boolean[numOfIssues];
        for (int i = 0; i < numOfIssues; i++) {
            if (violations.get(i).getCriticality() == Violation.Criticality.CRITICAL)
                allCriticality[i] = true;
            else
                allCriticality[i] = false;
        }
        return allCriticality;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns an int array of Nature
    public boolean[][] getAllNatures() {
        int numOfIssues = violations.size();
        boolean[][] allNatures = new boolean[numOfIssues][Violation.Nature.NUM_OF_ITEMS];

        for (int i = 0; i < numOfIssues; i++) {
            for (int j = 0; j < Violation.Nature.NUM_OF_ITEMS; j++) {
                allNatures[i][j] = violations.get(i).getNature()[j];
            }
        }
        return allNatures;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns an String array of Short Descriptions
    public String[] getAllShortDescriptions() {
        int numOfIssues = violations.size();
        String[] allShortDescriptions = new String[numOfIssues];

        for (int i = 0; i < numOfIssues; i++) {
            allShortDescriptions[i] = violations.get(i).getShortDescription();
        }
        return allShortDescriptions;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns an String array of Long Descriptions
    public String[] getAllLongDescriptions() {
        int numOfIssues = violations.size();
        String[] allLongDescriptions = new String[numOfIssues];

        for (int i = 0; i < numOfIssues; i++) {
            allLongDescriptions[i] = violations.get(i).getLongDescription();
        }
        return allLongDescriptions;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns a String of entire Inspection
    @Override
    public String toString() {

        String[] stringArray = getAllLongDescriptions();
        String violationsString = "{";
        for (int i = 0; i < stringArray.length; i++) {
            violationsString = violationsString + (i+1) + "=[" + stringArray[i] + "]";
        }
        violationsString += "}";

        return "[TrackingNumber = " + trackingNumber + "]" +
                "[InspectionDate = " + inspectionDate.getDateString(DateAndTime.DATE_FORMAT_CSV_INPUT) + "]" +
                "[InspectionType = " + inspectionType.getResourceString() + "]" +
                "[NumOfCritical = " + numOfCritical + "]" +
                "[NumOfNonCritical = " + numOfNonCritical + "]" +
                "[HazardLevel = " + hazardLevel.getResourceString() + "]" +
                "[Violations = " + violationsString + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trackingNumber);
        dest.writeParcelable(this.inspectionDate, flags);
        dest.writeInt(this.inspectionType == null ? -1 : this.inspectionType.ordinal());
        dest.writeInt(this.numOfCritical);
        dest.writeInt(this.numOfNonCritical);
        dest.writeInt(this.hazardLevel == null ? -1 : this.hazardLevel.ordinal());
        dest.writeList(this.violations);
    }

    protected Inspection(Parcel in) {
        this.trackingNumber = in.readString();
        this.inspectionDate = in.readParcelable(DateAndTime.class.getClassLoader());
        int tmpInspectionType = in.readInt();
        this.inspectionType = tmpInspectionType == -1 ? null : InspectionType.values()[tmpInspectionType];
        this.numOfCritical = in.readInt();
        this.numOfNonCritical = in.readInt();
        int tmpHazardLevel = in.readInt();
        this.hazardLevel = tmpHazardLevel == -1 ? null : HazardLevel.values()[tmpHazardLevel];
        this.violations = new ArrayList<Violation>();
        in.readList(this.violations, Violation.class.getClassLoader());
    }

    public static final Parcelable.Creator<Inspection> CREATOR = new Parcelable.Creator<Inspection>() {
        @Override
        public Inspection createFromParcel(Parcel source) {
            return new Inspection(source);
        }

        @Override
        public Inspection[] newArray(int size) {
            return new Inspection[size];
        }
    };
}
