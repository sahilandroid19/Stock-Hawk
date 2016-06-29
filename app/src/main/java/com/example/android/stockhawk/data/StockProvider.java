package com.example.android.stockhawk.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class StockProvider extends ContentProvider{
    //Constants for uri matcher
    private static final int STOCK_LIST = 1;
    private static final int STOCK_ID = 2;
    private static final UriMatcher URI_MATCHER;
    //Other variables
    private StockDatabase mHelper=null;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(StockContract.AUTHORITY, StockContract.StockTable.TABLE_NAME, STOCK_LIST);
        URI_MATCHER.addURI(StockContract.AUTHORITY, StockContract.StockTable.TABLE_NAME + "/*", STOCK_ID);
    }

    @Override
    public boolean onCreate() {
        mHelper = new StockDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor;
        switch (URI_MATCHER.match(uri)){
            case STOCK_LIST:
                cursor = db.query(StockContract.StockTable.TABLE_NAME, null, selection,
                        selectionArgs, sortOrder, null, null);
                break;
            case STOCK_ID:
                cursor = db.query(StockContract.StockTable.TABLE_NAME, null, StockContract.StockTable._ID + "=?",
                        new String[]{uri.getLastPathSegment()}, sortOrder, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri for query " + uri);
        }
        if(getContext()!=null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)){
            case STOCK_LIST:
                return StockContract.StockTable.CONTENT_TYPE;
            case STOCK_ID:
                return StockContract.StockTable.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if(URI_MATCHER.match(uri)==STOCK_ID){
            throw new IllegalArgumentException(
                    "Unsupported uri for insert"
            );
        }
        if(URI_MATCHER.match(uri)==STOCK_LIST){
            SQLiteDatabase db = mHelper.getWritableDatabase();
            long id = db.insert(StockContract.StockTable.TABLE_NAME, null, values);
            Uri itemUri = getUriForId(id,uri);
            if(getContext()!=null) {
                getContext().getContentResolver().notifyChange(itemUri, null);
            }
            return itemUri;
        }
        return null;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            return ContentUris.withAppendedId(uri, id);
        }
        throw new SQLException(
                "Problem while inserting into uri: " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int id;
        switch (URI_MATCHER.match(uri)){
            case STOCK_LIST:
                id = db.delete(StockContract.StockTable.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK_ID:
                id = db.delete(StockContract.StockTable.TABLE_NAME, StockContract.StockTable.STOCK_SYMBOL + " = ?",
                        new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
        if(getContext()!=null){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return id;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int id;
        switch (URI_MATCHER.match(uri)){
            case STOCK_LIST:
                id = db.update(StockContract.StockTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case STOCK_ID:
                id = db.update(StockContract.StockTable.TABLE_NAME, values, StockContract.StockTable._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
        if(getContext()!=null){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return id;
    }
}
