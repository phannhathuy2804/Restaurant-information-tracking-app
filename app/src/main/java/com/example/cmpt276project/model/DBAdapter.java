package com.example.cmpt276project.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    private static final String TAG = "DBAdapter";
    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0;

    public static final String KEY_RESTAURANTID = "restaurantID";
    public static final String KEY_NAME = "name";
    public static final String KEY_MOSTRECENT = "mostRecentInspection";
    public static final String KEY_HAZARDLEVEL = "hazardLevel";

    public static final int COL_RESID = 1;
    public static final int COL_NAME = 2;
    public static final int COL_MOSTRECENT = 3;
    public static final int COL_HAZARDLEVEL = 4;

    public static final String[] ALL_KEYS = new String[]{KEY_ROWID, KEY_RESTAURANTID, KEY_NAME, KEY_MOSTRECENT, KEY_HAZARDLEVEL};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "FavoriteDb";
    public static final String DATABASE_TABLE = "mainTable";
    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_SQL =
            "CREATE TABLE " + DATABASE_TABLE + "("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_RESTAURANTID + " STRING,"
                    + KEY_NAME + " STRING,"
                    + KEY_MOSTRECENT + " DATE,"
                    + KEY_HAZARDLEVEL + " STRING "
                    + ");";

    private final Context context;

    //private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;
    private DatabaseHelper myDBHelper;


    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myDBHelper.close();
    }


    public long insertRow(String resId,String name, String mostRecent , String hazardLevel) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RESTAURANTID, resId);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_MOSTRECENT, mostRecent);
        initialValues.put(KEY_HAZARDLEVEL, hazardLevel);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteRow(String RestaurantID) {
        String where = KEY_RESTAURANTID + "=" +"\"" + RestaurantID + "\"";
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public boolean deleteRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public void deleteAll() {
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    public Cursor getAllRows() {
        String where = null;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getRow(String restaurantID) {
        String where = KEY_RESTAURANTID + "=" + "\"" + restaurantID + "\"";
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public boolean updateRow(String resID, String name, String mostRecent , String hazardLevel) {
        String where = KEY_RESTAURANTID + "=" + "\"" + resID + "\"";
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_RESTAURANTID, resID);
        newValues.put(KEY_NAME, name);
        newValues.put(KEY_MOSTRECENT, mostRecent);
        newValues.put(KEY_HAZARDLEVEL, hazardLevel);
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }

}
