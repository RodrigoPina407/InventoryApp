package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Rodrigo on 23/07/2017.
 */

public final class InventoryContract {

    private InventoryContract() {
    }


    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    public static final String PATH_INVENTORY = "inventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class InventoryEntry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static String TABLE_NAME = "inventory";

        public final static String _ID = BaseColumns._ID;

        public static String COLUMN_ITEM_NAME = "item";

        public static String COLUMN_ITEM_PRICE = "price";

        public static String COLUMN_ITEM_QUANTITY = "quantity";

        public static String COLUMN_ITEM_SUPPLIER = "supplier";

        public static String COLUMN_ITEM_DESCRIPTION = "description";

        public static String COLUMN_ITEM_IMAGE = "image";


    }

}
