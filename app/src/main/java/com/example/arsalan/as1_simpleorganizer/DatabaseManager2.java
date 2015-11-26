package com.example.arsalan.as1_simpleorganizer;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class DatabaseManager2 {
    public static final String DB_TABLE = "tasksRecord";
    public static final String DB_NAME = "tasks";
    public static final int DB_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (taskName TEXT NOT NULL PRIMARY KEY, taskLocation TEXT, taskStatus TEXT);";
    private SQLHelper helper;
    private SQLiteDatabase db;
    private Context context;

    public DatabaseManager2(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();
    }

    public DatabaseManager2 openReadable() throws android.database.SQLException {
        helper = new SQLHelper(context);
        db = helper.getReadableDatabase();
        return this;
    }

    public void close() {
        helper.close();
    }


    public boolean addRowTask(String name, String loc, String status){      /* Add a row to tasks table */
        ContentValues newTask = new ContentValues();
        newTask.put("taskName", name);
        newTask.put("taskLocation", loc);
        newTask.put("taskStatus", status);

        try {
            db.insertOrThrow(DB_TABLE, null, newTask);
        }
        catch (Exception e) {
            Log.e("Error inserting rows...", e.toString());
            e.printStackTrace();    /*Writes a printable representation of this Throwable's stack trace to the System.err stream.*/
            return false;
        }
        db.close();
        return true;
    }


    public ArrayList<String> retrieveRowsTasks() {  /* Return all tasks records */
        String[] columns = new String[] {"taskName", "taskLocation", "taskStatus"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, "taskStatus");  /*Order tasks by their status Completed/Uncompleted*/
        ArrayList<String> tablerows = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            tablerows.add(cursor.getString(0) + " " + cursor.getString(2));
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return tablerows;
    }

    public ArrayList<String> retrieveARow(String name){ /* Get all info for a specific task */
        String whereClause = "taskName = '" + name + "'";
        String[] columns = new String[] {"taskName", "taskLocation", "taskStatus"};
        Cursor cursor2 = db.query(DB_TABLE, columns, whereClause, null, null, null, null);
        ArrayList<String> rows;
        rows = new ArrayList<>();
        cursor2.moveToFirst();
        rows.add(cursor2.getString(0));
        rows.add(cursor2.getString(1));
        rows.add(cursor2.getString(2));
        if (cursor2 != null && !cursor2.isClosed()) {
            cursor2.close();
        }
        return rows;
    }

    public boolean updateTask(String name, String loc, String s){
        ContentValues updatedRec = new ContentValues();
        updatedRec.put("taskName", name);
        updatedRec.put("taskLocation", loc);
        updatedRec.put("taskStatus", s);

        String whereClause = "taskName = '" + name + "'";
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
            Log.w("Tasks table", "Upgrading database i.e. dropping table and re-creating them");
            db.execSQL("DROP TALBE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }
}


