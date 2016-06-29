package com.example.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.stockhawk.R;
import com.example.android.stockhawk.data.StockContract;
import com.example.android.stockhawk.extras.Stock;

import java.util.ArrayList;

public class StockListAdapter implements RemoteViewsService.RemoteViewsFactory {
    private Context ctxt = null;
    private ArrayList<Stock> stocks = new ArrayList<>();

    public StockListAdapter(Context ctxt, ArrayList<Stock> stocks) {
        this.ctxt = ctxt;
        this.stocks = stocks;
    }

    @Override
    public void onCreate() {
        // no-op
    }

    @Override
    public void onDestroy() {
        // no-op
    }

    @Override
    public int getCount() {
        return (stocks.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(ctxt.getPackageName(),
                R.layout.stock_row);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("water", position);
        row.setOnClickFillInIntent(R.id.stock_row, fillInIntent);
        Double value = Double.parseDouble(stocks.get(position).getChange());
        if (value >= 0) {
            row.setInt(R.id.stock_change, "setBackgroundResource",
                    R.drawable.stock_change);
        } else {
            row.setInt(R.id.stock_change, "setBackgroundResource",
                    R.drawable.stock_change_negative);
        }
        row.setTextViewText(R.id.stock_symbol, stocks.get(position).getSymbol());
        row.setTextViewText(R.id.stock_detail, stocks.get(position).getName());
        row.setTextViewText(R.id.stock_value, stocks.get(position).getPrice());
        row.setTextViewText(R.id.stock_change, stocks.get(position).getPercent_change());
        return (row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return (null);
    }

    @Override
    public int getViewTypeCount() {
        return (1);
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    @Override
    public boolean hasStableIds() {
        return (true);
    }

    @Override
    public void onDataSetChanged() {
        Cursor cursor = ctxt.getContentResolver().query(StockContract.StockTable.CONTENT_URI,
                WidgetIntentService.columns, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            this.stocks = WidgetIntentService.getStockData(cursor);
            cursor.close();
        }
    }
}