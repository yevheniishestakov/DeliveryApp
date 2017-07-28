package com.example.a585552.deliveryapp;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by a585552 on 1/19/2017.
 */

public class DeliveryDBContract {

    private DeliveryDBContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.a585552.deliveryapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DEL = "item";


    public static final class DeliveryItemEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DEL);

        public static final String TABLE_NAME = "item";

        public final static String _ID = BaseColumns._ID;

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_DESTINATION = "destination";
    }

}
