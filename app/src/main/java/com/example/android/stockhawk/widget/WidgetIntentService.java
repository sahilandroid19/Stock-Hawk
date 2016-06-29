package com.example.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViewsService;

import com.example.android.stockhawk.data.StockContract;
import com.example.android.stockhawk.extras.Stock;

import java.util.ArrayList;

public class WidgetIntentService extends RemoteViewsService {
    public static  String[] columns = {StockContract.StockTable.STOCK_SYMBOL,
            StockContract.StockTable.STOCK_NAME,
            StockContract.StockTable.STOCK_PRICE,
            StockContract.StockTable.STOCK_CHANGE,
            StockContract.StockTable.STOCK_PER_CHANGE
    };

    public WidgetIntentService() {}

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockListAdapter(this.getApplicationContext(), getStockData(getCursor()));
    }

    public static ArrayList<Stock> getStockData(Cursor cursor){
        ArrayList<Stock> stocks= new ArrayList<>();
        if(cursor!=null&&cursor.getCount()>0){
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                Stock stock = new Stock();
                stock.setSymbol(cursor.getString(1));
                stock.setName(cursor.getString(2));
                stock.setPrice(cursor.getString(3));
                stock.setChange(cursor.getString(5));
                stock.setPercent_change(cursor.getString(4));
                stocks.add(stock);
            }
            cursor.close();
        }
        return stocks;
    }

    private Cursor getCursor(){
        return getContentResolver().query(StockContract.StockTable.CONTENT_URI,
                columns, null, null, null);
    }

}
