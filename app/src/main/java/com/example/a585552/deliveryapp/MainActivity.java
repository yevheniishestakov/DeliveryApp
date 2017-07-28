package com.example.a585552.deliveryapp;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private String [] scan_results;
    private String item_name, item_destination;
    private static final int PET_LOADER = 0;

    /** Adapter for the ListView */
    DeliveryCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQR(new View(getApplicationContext()));
            }
        });

        ListView petListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new DeliveryCursorAdapter(this, null);
        /*  . */
        petListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(PET_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_delete_all) {
            return true;
        }

        if (id == R.id.map_menu_item) {

            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }


    public void scanQR(View v) {

        try {

            //start the scanning activity from the com.google.zxing.client.android.SCAN intent

            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);

        } catch (ActivityNotFoundException anfe) {

            //on catch, show the download dialog
            Toast.makeText(getApplicationContext(), "Activity not found", Toast.LENGTH_SHORT);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {

                String contents = intent.getStringExtra("SCAN_RESULT");
                scan_results = contents.split(";");

                item_name = scan_results[0];
                item_destination = scan_results[1];
                Toast toast = Toast.makeText(this, "Content:" + contents , Toast.LENGTH_LONG);
                toast.show();

                ContentValues content_values = new ContentValues();
                content_values.put(DeliveryDBContract.DeliveryItemEntry.COLUMN_NAME, item_name);
                content_values.put(DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION,item_destination);


                Uri newUri = getContentResolver().insert(DeliveryDBContract.DeliveryItemEntry.CONTENT_URI, content_values);
                Log.v("Adding to DB (URI):" , newUri.toString());
                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, "Save failed",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, "Save successful",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                DeliveryDBContract.DeliveryItemEntry._ID,
                DeliveryDBContract.DeliveryItemEntry.COLUMN_NAME,
                DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                DeliveryDBContract.DeliveryItemEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
