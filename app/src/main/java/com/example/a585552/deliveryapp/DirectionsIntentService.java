package com.example.a585552.deliveryapp;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Yevhenii on 17-Feb-17.
 */

public class DirectionsIntentService extends IntentService {

    ArrayList<String> destinationArray = new ArrayList<>();



    public DirectionsIntentService(){
        super("DirectionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String[] projection = {
                DeliveryDBContract.DeliveryItemEntry._ID,
                DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION };


        Cursor cursor = getContentResolver().query(DeliveryDBContract.DeliveryItemEntry.CONTENT_URI, projection,null,null,null);

        while (cursor.moveToNext()){
            int colindex = cursor.getColumnIndex(DeliveryDBContract.DeliveryItemEntry.COLUMN_DESTINATION);
            String result = cursor.getString(colindex);
            destinationArray.add(result);
            Log.v("DirService: ", "Dest Array" + result);
        }



        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String directionsJsonStr = null;


        try{
            final String FORECAST_BASE_URL =
                    "https://maps.googleapis.com/maps/api/directions/json?origin=Adelaide,SA&destination=Adelaide,SA&waypoints=optimize:true|Barossa+Valley,SA|Clare,SA|Connawarra,SA|McLaren+Vale,SA&key=AIzaSyAtUp6uonJ_3fHPXssi7VeNngCeVqqC0qo";


            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .build();

            URL url = new URL(builtUri.toString());


            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return ;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return ;
            }
            directionsJsonStr = buffer.toString();

            Log.v("DirService: ", "Forecast string: " + directionsJsonStr);
        }
        catch (IOException e){
            Log.e("DirService: ", "Error: ", e );
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("DirService", "Error closing stream", e);
                }
            }
        }

    }

    public static class DeliveryAlarmReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            Intent sendIntent = new Intent(context, DirectionsIntentService.class);
            context.startService(sendIntent);
        }
    }
}
