package com.example.android.stockhawk.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class StockContract {
    //Database name
    public static final String DATABASE_NAME = "stock";
    //Database version
    public static final int DATABASE_VERSION = 1;
    //Authority of content provider
    public static final String AUTHORITY = "com.example.android.stockhawk.data.StockProvider";
    //Content uri for top level
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static abstract class StockTable implements BaseColumns{
        //Constraints for columns
        public static final String TABLE_NAME = "stocks";
        public static final String STOCK_SYMBOL = "stock_symbol";
        public static final String STOCK_NAME = "stock_name";
        public static final String STOCK_PRICE = "stock_price";
        public static final String STOCK_PER_CHANGE = "stock_per_change";
        public static final String STOCK_CHANGE = "stock_change";
        public static final String STOCK_OPEN = "stock_open";
        public static final String STOCK_D_HIGH = "stock_d_high";
        public static final String STOCK_D_LOW = "stock_d_low";
        public static final String STOCK_VOL = "stock_vol";
        public static final String STOCK_CAP = "stock_cap";
        public static final String STOCK_Y_HIGH = "stock_y_high";
        public static final String STOCK_Y_lOW = "stock_y_low";
        public static final String STOCK_PE = "stock_pe";
        //Content uri for this table
        public static final Uri CONTENT_URI = Uri.withAppendedPath(StockContract.CONTENT_URI, TABLE_NAME);
        //MIME type for directory of items
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI.toString();
        //MIME type for single item
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI.toString();


        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                STOCK_SYMBOL + " TEXT NOT NULL," +
                STOCK_NAME + " TEXT NOT NULL," +
                STOCK_PRICE + " TEXT NOT NULL," +
                STOCK_PER_CHANGE + " TEXT NOT NULL," +
                STOCK_CHANGE + " TEXT NOT NULL," +
                STOCK_OPEN + " TEXT NOT NULL," +
                STOCK_D_HIGH + " TEXT NOT NULL," +
                STOCK_D_LOW + " TEXT NOT NULL," +
                STOCK_VOL + " TEXT NOT NULL," +
                STOCK_CAP + " TEXT NOT NULL," +
                STOCK_Y_HIGH + " TEXT NOT NULL," +
                STOCK_Y_lOW + " TEXT NOT NULL," +
                STOCK_PE + " TEXT NOT NULL" + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
