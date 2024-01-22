package com.example.projetmeteo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dbMeteo";
    private static final String TABLE_NAME = "ville";
    private static final String PKEY = "pkey";
    private static final String COL1 = "col1";

    DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                PKEY + " INTEGER PRIMARY KEY," +
                COL1 + " TEXT);";
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) { // Upgrade pas très fin
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public void addNewVille(String nomVille){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL1, nomVille);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public ArrayList readVilleSaved(){
        String select = new String("SELECT * from " + TABLE_NAME);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(select, null);
        Log.i("XXXX", "Number of entries: " + cursor.getCount());
        ArrayList<String> ListVilleSaved = new ArrayList();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                ListVilleSaved.add(cursor.getString(cursor.getColumnIndex(COL1)));
            } while (cursor.moveToNext());
        }
        return ListVilleSaved;
    }
}