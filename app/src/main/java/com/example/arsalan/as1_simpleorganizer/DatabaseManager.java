package com.example.arsalan.as1_simpleorganizer;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseManager {
    public static final String DB_NAME = "friends";
    public static final String DB_TABLE = "friendsRecord";
    public static final int DB_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (firstName TEXT NOT NULL, lastName TEXT NOT NULL, gender TEXT, age TEXT, address TEXT, PRIMARY KEY(firstName, lastName));";
    private SQLHelper helper;
    private SQLiteDatabase db;
    private Context context;

    public DatabaseManager(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();
    }

    public DatabaseManager openReadable() throws android.database.SQLException {
        helper = new SQLHelper(context);
        db = helper.getReadableDatabase();
        return this;
    }

    public void close() {
        helper.close();
    }

    public boolean addRow(String f, String l, String g, String a, String d) {   /* Add a row to friends table */
        ContentValues newFriend = new ContentValues();
        newFriend.put("firstName", f);
        newFriend.put("lastName", l);
        newFriend.put("gender", g);
        newFriend.put("age", a);
        newFriend.put("address", d);
        try {
            db.insertOrThrow(DB_TABLE, null, newFriend);
        }
        catch (Exception e) {
            Log.e("Error inserting rows...", e.toString());
            e.printStackTrace();
            return false;
        }
        db.close();
        return true;
    }

    public void deleteRow(String fname, String lname){
        db.execSQL("DELETE FROM " + DB_TABLE + " WHERE firstName = '" + fname + "' AND lastName = '" + lname + "'");
        Log.e("Database Operations. ", "Record Deletion...");
    }

    public ArrayList<String> retrieveRows() {   /*Returns all friend records */
        String[] columns = new String[] {"firstName", "lastName", "gender", "age", "address"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
        ArrayList<String> tablerows = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            tablerows.add(cursor.getString(0) + " " + cursor.getString(1));
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return tablerows;
    }

    public ArrayList<String> retrieveARowInfo(String f, String l){ /* Get all info for a specific friend */
        String whereClause = "firstName = '" + f + "' AND lastName = '" + l + "'";
        String[] columns = new String[] {"firstName", "lastName", "gender", "age", "address"};
        Cursor cursor2 = db.query(DB_TABLE, columns, whereClause, null, null, null, null);
        ArrayList<String> rows;
        rows = new ArrayList<>();
        cursor2.moveToFirst();
        rows.add(cursor2.getString(0));
        rows.add(cursor2.getString(1));
        rows.add(cursor2.getString(2));
        rows.add(Integer.toString(cursor2.getInt(3)));
        rows.add(cursor2.getString(4));
        if (cursor2 != null && !cursor2.isClosed()) {
            cursor2.close();
        }
        return rows;
    }

    public boolean UpdateRow(String f, String l, String g, String a, String d){
        ContentValues updatedRec = new ContentValues();
        updatedRec.put("firstName", f);
        updatedRec.put("lastName", l);
        updatedRec.put("gender", g);
        updatedRec.put("age", a);
        updatedRec.put("address", d);
        String whereClause = "firstName = '" + f + "' AND lastName = '" + l + "'";
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
            Log.w("Friends table", "Upgrading database i.e. dropping table and re-creating them");
            db.execSQL("DROP TALBE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }
}

