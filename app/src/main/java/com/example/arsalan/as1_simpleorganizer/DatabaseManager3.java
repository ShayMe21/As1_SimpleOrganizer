package com.example.arsalan.as1_simpleorganizer;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class DatabaseManager3 {
    public static final String DB_TABLE = "EventsRecord";
    public static final String DB_NAME = "events";
    public static final int DB_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (eventName TEXT NOT NULL PRIMARY KEY, eventDate TEXT NOT NULL, eventTime TEXT, eventLocation TEXT);";
    private SQLHelper helper;
    private SQLiteDatabase db;
    private Context context;

    public DatabaseManager3(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();
    }

    public DatabaseManager3 openReadable() throws android.database.SQLException {
        helper = new SQLHelper(context);
        db = helper.getReadableDatabase();
        return this;
    }

    public void close() {
        helper.close();
    }


    public boolean addRowEvent(String name, String date, String time, String location){      /* Add a row to events table */
        ContentValues newEvent = new ContentValues();
        newEvent.put("eventName", name);
        newEvent.put("eventDate", date);
        newEvent.put("eventTime", time);
        newEvent.put("eventLocation", location);

        try {
            db.insertOrThrow(DB_TABLE, null, newEvent);
        }
        catch (Exception e) {
            Log.e("Error inserting rows...", e.toString());
            e.printStackTrace();    /*Writes a printable representation of this Throwable's stack trace to the System.err stream.*/
            return false;
        }
        db.close();
        return true;
    }

    public ArrayList<String> retrieveRowsEvents() {  /* Return all events records */
        String[] columns = new String[] {"eventName", "eventDate", "eventTime", "eventLocation"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
        ArrayList<String> tablerows = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            tablerows.add(cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3));
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return tablerows;
    }

    public ArrayList<String> retrieveARow(String name){ /* Get all info for a specific task */
        String whereClause = "eventName = '" + name + "'";
        String[] columns = new String[] {"eventName", "eventDate", "eventTime", "eventLocation"};
        Cursor cursor2 = db.query(DB_TABLE, columns, whereClause, null, null, null, null);
        ArrayList<String> rows;
        rows = new ArrayList<>();
        cursor2.moveToFirst();
        rows.add(cursor2.getString(0));
        rows.add(cursor2.getString(1));
        rows.add(cursor2.getString(2));
        rows.add(cursor2.getString(3));
        if (cursor2 != null && !cursor2.isClosed()) {
            cursor2.close();
        }
        return rows;
    }

    public boolean updateEvent(String name, String date, String time, String loc){
        ContentValues updatedRec = new ContentValues();
        updatedRec.put("eventName", name);
        updatedRec.put("eventDate", date);
        updatedRec.put("eventTime", time);
        updatedRec.put("eventLocation", loc);

        String whereClause = "eventName = '" + name + "'";
        try {
            db.update(DB_TABLE, updatedRec, whereClause, null);
        }
        catch (Exception e) {
            Log.e("Error updating rows...", e.toString());
            e.printStackTrace();
            return false;
        }
        db.close();
        return true;
    }

    public void deleteRow(String name){
        db.execSQL("DELETE FROM " + DB_TABLE + " WHERE eventName = '" + name + "'");
        Log.e("Database Operations. ", "Record Deletion...");
    }



    public class SQLHelper extends SQLiteOpenHelper {
        public SQLHelper (Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
            Log.e("Database Operations. ", "Table Created...");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Events table", "Upgrading database i.e. dropping table and re-creating them");
            db.execSQL("DROP TALBE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }
}



