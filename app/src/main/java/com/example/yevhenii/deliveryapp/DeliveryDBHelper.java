package com.example.yevhenii.deliveryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by a585552 on 1/19/2017.
 */

public class DeliveryDBHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "deldb.db";
    private static final int DATABASE_VERSION = 2;

    @Override
    public void onCreate(SQLiteDatabase db) {


        String SQL_CREATE_ITEMS_TABLE =  "CREATE TABLE " + DeliveryDBContract.DeliveryItemEntry.TABLE_NAME + " ("
                + DeliveryDBContract.DeliveryItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DeliveryDBContract.DeliveryItemEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION_LAT + " REAL NOT NULL, "
                + DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION_LON + " REAL NOT NULL );";


        db.execSQL(SQL_CREATE_ITEMS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DeliveryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
