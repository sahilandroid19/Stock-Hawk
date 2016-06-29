package com.example.android.stockhawk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StockDatabase extends SQLiteOpenHelper {

    public StockDatabase(Context context) {
        super(context, StockContract.DATABASE_NAME, null, StockContract.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StockContract.StockTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(StockContract.StockTable.DELETE_TABLE);
    }
}
