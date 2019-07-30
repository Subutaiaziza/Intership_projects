package com.example.runkeeper.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String TABLE_NAME = "myTable";
    private final static String COL1 = "ID";
    private final static String COL2 = "distance";
    private final static String COL3 = "time";
    private final static String COL4 = "date";

    public DatabaseHelper(Context context) { super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME
                +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL2 + " TEXT, "
                + COL3 + " TEXT,"
                + COL4 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE  IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public Cursor getData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+ TABLE_NAME, null);
        return  data;
    }
    public boolean addData(String distance, String time, String date)
    {
        SQLiteDatabase sd =getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL2, distance);
        cv.put(COL3, time);
        cv.put(COL4, date);

        Log.d("Database", "Adding to db");
        long result = sd.insert(TABLE_NAME, null, cv);

        if(result == 1)
        {
            return  true;
        }else
        {
            return  false;
        }
    }

    public boolean deleteRow(String text)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        long res = db.delete(TABLE_NAME, COL4 + " = " + "'"+ text +"'", null);

        if(res == 1)
        {
            return  true;
        }else
        {
            return  false;
        }
    }
}
