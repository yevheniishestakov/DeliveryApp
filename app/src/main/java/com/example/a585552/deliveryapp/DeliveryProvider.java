package com.example.a585552.deliveryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by a585552 on 1/19/2017.
 */

public class DeliveryProvider extends ContentProvider {

    private DeliveryDBHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new DeliveryDBHelper(getContext());
        return true;
    }



    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try{

             cursor =  db.query(DeliveryDBContract.DeliveryItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                    null, null, sortOrder);

            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        catch (IllegalArgumentException e){
            Log.v("Provider Unknown URI: ", uri.toString());
        }



        // Return the cursor
        //cursor.close();
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(DeliveryDBContract.DeliveryItemEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e("Provider: ", "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        rowsDeleted = database.delete(DeliveryDBContract.DeliveryItemEntry.TABLE_NAME, null,null);


        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
