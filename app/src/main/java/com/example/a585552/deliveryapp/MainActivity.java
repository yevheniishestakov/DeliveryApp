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
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private String [] scan_results;
    private String item_name, item_destination;
    private static final int DELIVERY_ITEMS_LOADER = 0;

    /** Adapter for the ListView */
    DeliveryCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle("Screen Title");
        RecyclerView del_recycler_view = (RecyclerView)findViewById(R.id.recyclerview);
        del_recycler_view.setLayoutManager(new LinearLayoutManager(this));

        del_recycler_view.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(getLayoutInflater().inflate(R.layout.list_item, parent, false));
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {


            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQR(new View(getApplicationContext()));
            }
        });

        ListView deliveryListView = (ListView) findViewById(R.id.list);


        View emptyView = findViewById(R.id.empty_view);
        deliveryListView.setEmptyView(emptyView);


        mCursorAdapter = new DeliveryCursorAdapter(this, null);

        deliveryListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(DELIVERY_ITEMS_LOADER, null, this);

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

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;

        public ViewHolder(View itemView) {
            super(itemView);
            text1 = (TextView) itemView.findViewById(android.R.id.text1);
            text2 = (TextView) itemView.findViewById(android.R.id.text2);
        }
    }
}
