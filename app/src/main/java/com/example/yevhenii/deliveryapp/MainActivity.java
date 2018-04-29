package com.example.yevhenii.deliveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, NoticeDialogListener{

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

            showAlertDialog();
        }

        if (id == R.id.action_show_map) {

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

                String[] destination_coordinates = item_destination.split(",");

                double destination_latitude = Double.valueOf(destination_coordinates[0]);
                double destination_longtitude = Double.valueOf(destination_coordinates[1]);


                ContentValues content_values = new ContentValues();
                content_values.put(DeliveryDBContract.DeliveryItemEntry.COLUMN_NAME, item_name);
                content_values.put(DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION_LAT,destination_latitude);
                content_values.put(DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION_LON,destination_longtitude);

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
                DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION_LAT,
                DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION_LON };

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

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        getContentResolver().delete(DeliveryDBContract.BASE_CONTENT_URI,null,null);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int title) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("title");

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mListener.onDialogPositiveClick(MyAlertDialogFragment.this);
                                }
                            }
                    )
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mListener.onDialogNegativeClick(MyAlertDialogFragment.this);
                                }
                            }
                    )
                    .create();
        }


        // Use this instance of the interface to deliver action events
        NoticeDialogListener mListener;

        // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            // Verify that the host activity implements the callback interface
            try {
                // Instantiate the NoticeDialogListener so we can send events to the host
                mListener = (NoticeDialogListener) activity;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString()
                        + " must implement NoticeDialogListener");
            }
        }


    }



    private void showAlertDialog() {
        FragmentManager fm = getSupportFragmentManager();
        MyAlertDialogFragment alertDialog = MyAlertDialogFragment.newInstance(R.string.dialog_title_delete_all);
        alertDialog.show(fm, "fragment_alert");
    }

}
