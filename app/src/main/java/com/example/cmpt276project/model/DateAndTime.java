/*
    --------------------------------------------------------------------------------------------------------------------
    DateAndTime Class Implementation
    This class uses for inputting or outputting the date and time in different timezones.
    Creator: Lam, Ching Hang
    Email: CHL50@sfu.ca
    Last Modified Date: 2020/07/27
    --------------------------------------------------------------------------------------------------------------------
    Formatting Keywords:
    Example: 1990/01/02, 23:58:59
    String format = "yyyy/MM/dd, HH:mm:ss";

    Year: yyyy (Output: 1990), yy (Output: 90)
    Month: MM (Output: 01), MMM (Output: Jan), MMMM (Output: January)
    Day: dd (Output: 02)
    Hour: HH (Output: 23)
    Minute: mm (Output: 58)
    Second: ss (Output: 59)
    --------------------------------------------------------------------------------------------------------------------
 */
// Package
package com.example.cmpt276project.model;

// Import
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import static java.lang.Math.abs;

// DateAndTime Class
public class DateAndTime implements Parcelable {

    private Date date;
    private static final String TAG = "DateAndTime";

    // DEFAULT_DATE = 1990/01/01
    public static final Date DEFAULT_DATE = new GregorianCalendar(1990, 0, 1).getTime();
    public static final String DATE_FORMAT_DEFAULT_OUTPUT = "yyyy/MM/dd, HH:mm:ss";
    public static final String DATE_FORMAT_CSV_INPUT = "yyyyMMdd";
    public static final String DATE_FORMAT_ONE_YEAR = "MMM dd";
    public static final String DATE_FORMAT_ENTIRE = "MMM yyyy";
    public static final String DATE_FORMAT_INSPECTION = "MMM dd, yyyy";

    // DEFAULT_TIMEZONE
    public static final TimeZone VANCOUVER_TIMEZONE = TimeZone.getTimeZone("GMT-7");

    //----------------------------------------------------------------------------------------------
    // Constructor
    // Sets to the current time and the default time zone
    public DateAndTime() {
        this.date = new Date();
    }

    //----------------------------------------------------------------------------------------------
    // Constructor
    // Sets to the specific time and the default time zone
    public DateAndTime(String format, String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        try {
            this.date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "Incorrect date format");
            this.date = DEFAULT_DATE;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Constructor
    // Sets to the specific time and the specific time zone
    public DateAndTime(String format, String dateString, TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(timeZone);

        try {
            this.date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "Incorrect date format");
            this.date = DEFAULT_DATE;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Constructor
    // Sets to the specific date and the default time zone
    public DateAndTime(Date date) {
        this.date = date;
    }

    //----------------------------------------------------------------------------------------------
    // Setter
    // Sets to the current time and the default time zone
    public void setDate() {
        this.date = new Date();
    }

    //----------------------------------------------------------------------------------------------
    // Setter
    // Sets to the specific time and the default time zone
    public void setDate(String format, String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        try {
            this.date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "Incorrect date format");
            this.date = DEFAULT_DATE;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Setter
    // Sets to the specific time and the specific time zone
    public void setDate(String format, String dateString, TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(timeZone);

        try {
            this.date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "Incorrect date format");
            this.date = DEFAULT_DATE;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Setter
    // Sets to the specific date and the default time zone
    public void setDate(Date date) {
        this.date = date;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns the date according to the default time zone
    public Date getDate() {
        return date;
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns a String of date according to the default time zone
    public String getDateString(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    //----------------------------------------------------------------------------------------------
    // Getter
    // Returns a String of date according to the specific time zone
    public String getDateString(String format, TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(date);
    }

    //----------------------------------------------------------------------------------------------
    // Calculate Time Difference
    // Returns the time different between this object and parameter in Milliseconds
    public long timeDiffInMillisecond(DateAndTime dateAndTime) {
        return abs(this.date.getTime() - dateAndTime.getDate().getTime());
    }

    //----------------------------------------------------------------------------------------------
    // Calculate Time Difference
    // Returns the time different between this object and parameter in Seconds
    public long timeDiffInSecond(DateAndTime dateAndTime) {
        return abs(this.date.getTime() - dateAndTime.getDate().getTime()) / (1000);
    }

    //----------------------------------------------------------------------------------------------
    // Calculate Time Difference
    // Returns the time different between this object and parameter in Minute
    public long timeDiffInMinute(DateAndTime dateAndTime) {
        return abs(this.date.getTime() - dateAndTime.getDate().getTime()) / (1000 * 60);
    }

    //----------------------------------------------------------------------------------------------
    // Calculate Time Difference
    // Returns the time different between this object and parameter in Hours
    public long timeDiffInHour(DateAndTime dateAndTime) {
        return abs(this.date.getTime() - dateAndTime.getDate().getTime()) / (1000 * 60 * 60);
    }

    //----------------------------------------------------------------------------------------------
    // Calculate Time Difference
    // Returns the time different between this object and parameter in Days
    public long timeDiffInDay(DateAndTime dateAndTime) {
        return abs(this.date.getTime() - dateAndTime.getDate().getTime()) / (1000 * 60 * 60 * 24);
    }

    //----------------------------------------------------------------------------------------------
    // Calculate Time Difference (Comparing to the Current Time)
    // Returns the time different between this object and the current time in Milliseconds
    public long timeDiffInMillisecond() {
        return abs(this.date.getTime() - new Date().getTime());
    }

    //----------------------------------------------------------------------------------------------
    // Calculate Time Difference (Comparing to the Current Time)
    // Returns the time different between this object and the current time in Seconds
    public long timeDiffInSecond() {
        return abs(this.date.getTime() - new Date().getTime()) / (1000);
    }

    //----------------------------------------------------------------------------------------------
    // Calculate Time Difference (Comparing to the Current Time)
    // Returns the time different between this object and the current time in Minutes
    public long timeDiffInMinute() {
        return abs(this.date.getTime() - new Date().getTime()) / (1000 * 60);
    }

    //----------------------------------------------------------------------------------------------
    // Calculate Time Difference (Comparing to the Current Time)
    // Returns the time different between this object and the current time in Hours
    public long timeDiffInHour() {
        return abs(this.date.getTime() - new Date().getTime()) / (1000 * 60 * 60);
    }

    //----------------------------------------------------------------------------------------------
    // Calculate Time Difference (Comparing to the Current Time)
    // Returns the time different between this object and the current time in Days
    public long timeDiffInDay() {
        return abs(this.date.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24);
    }

    //----------------------------------------------------------------------------------------------
    // Check before
    // Returns true if this Date of Object before the Parameter
    // Otherwise returns false
    public boolean before(DateAndTime dateAndTime) {
        return this.date.before(dateAndTime.getDate());
    }

    //----------------------------------------------------------------------------------------------
    // Check before
    // Returns true if this Date of Object before the Current Time
    // Otherwise returns false
    public boolean before() {
        return this.date.before(new Date());
    }

    //----------------------------------------------------------------------------------------------
    // Check after
    // Returns true if this Date of Object after the Parameter
    // Otherwise returns false
    public boolean after(DateAndTime dateAndTime) {
        return this.date.after(dateAndTime.getDate());
    }

    //----------------------------------------------------------------------------------------------
    // Check after
    // Returns true if this Date of Object after the Current Time
    // Otherwise returns false
    public boolean after() {
        return this.date.after(new Date());
    }

    //----------------------------------------------------------------------------------------------
    // Returns a Sting in the specific format mentioned in Iteration 1
    public String formatDate() {
        // Within 30 days
        if (timeDiffInDay() < 30) {
            return Long.toString(timeDiffInDay()) + " days";
        }
        // Within 365 days
        else if (timeDiffInDay() < 365) {
            return getDateString(DATE_FORMAT_ONE_YEAR);
        }
        // Entire
        else {
            return getDateString(DATE_FORMAT_ENTIRE);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
    }

    protected DateAndTime(Parcel in) {
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Parcelable.Creator<DateAndTime> CREATOR = new Parcelable.Creator<DateAndTime>() {
        @Override
        public DateAndTime createFromParcel(Parcel source) {
            return new DateAndTime(source);
        }

        @Override
        public DateAndTime[] newArray(int size) {
            return new DateAndTime[size];
        }
    };
}