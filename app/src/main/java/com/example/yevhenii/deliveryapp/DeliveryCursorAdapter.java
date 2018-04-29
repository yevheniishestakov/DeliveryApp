package com.example.yevhenii.deliveryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by a585552 on 1/21/2017.
 */

public class DeliveryCursorAdapter extends CursorAdapter {


    public DeliveryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.destination);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(DeliveryDBContract.DeliveryItemEntry.COLUMN_NAME);
        int destinationLatColumnIndex = cursor.getColumnIndex(DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION_LAT);
        int destinationLonColumnIndex = cursor.getColumnIndex(DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION_LON);

        // Read the item attributes from the Cursor for the current pet
        String name = cursor.getString(nameColumnIndex);
        double destination_lat = cursor.getDouble(destinationLatColumnIndex);
        double destination_lon = cursor.getDouble(destinationLonColumnIndex);

        // Update the TextViews with the attributes for the current item
        nameTextView.setText(name);
        summaryTextView.setText(String.valueOf(destination_lat)+String.valueOf(destination_lon));
    }
}
