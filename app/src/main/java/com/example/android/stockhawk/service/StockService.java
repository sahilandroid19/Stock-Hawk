package com.example.android.stockhawk.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.android.stockhawk.R;
import com.example.android.stockhawk.data.StockContract;
import com.example.android.stockhawk.extras.Constants;
import com.example.android.stockhawk.extras.HttpRequest;
import com.example.android.stockhawk.extras.Stock;
import com.example.android.stockhawk.extras.Utility;
import com.example.android.stockhawk.widget.StockWidgetProvider;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StockService extends IntentService {

    public StockService(){
      super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String json;
        ArrayList<String> symbols = intent.getStringArrayListExtra(Constants.STOCK_INTENT);
        for(int i=0;i<symbols.size();i++) {
            String symbolQuery = String.format(Constants.SYMBOL_QUERY, symbols.get(i));
            Uri builder = Utility.buildStockAPI(symbolQuery);
            try {
                json = HttpRequest.get(builder.toString()).body();
            } catch (HttpRequest.HttpRequestException e) {
                json = "";
                e.printStackTrace();
            }
            //Putting stock into database
            Stock stock = getJsonData(json);
            if (stock != null) {
                insertData(stock);
            }
        }
    }

    private Stock getJsonData(String json){
        Stock stock = new Stock();
        try {
            JSONObject object = new JSONObject(json);
            JSONObject query = object.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");
            JSONObject quote = results.getJSONObject("quote");
            if(!quote.isNull("Name")) {
                stock.setSymbol(quote.getString("symbol"));
                stock.setName(quote.getString("Name"));
                stock.setPrice(quote.getString("Ask"));
                stock.setPercent_change(quote.getString("ChangeinPercent"));
                stock.setChange(quote.getString("Change"));
                stock.setOpen(quote.getString("Open"));
                stock.setDays_high(quote.getString("DaysHigh"));
                stock.setDays_low(quote.getString("DaysLow"));
                stock.setVol(quote.getString("Volume"));
                stock.setMktCap(quote.getString("MarketCapitalization"));
                stock.setYear_high(quote.getString("YearHigh"));
                stock.setYear_low(quote.getString("YearLow"));
                stock.setPeRatio(quote.getString("PERatio"));
            }else {
                showToast(getString(R.string.noStock));
                return null;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return stock;
    }

    /*
    Inserts and updates stocks in database
     */
    private void insertData(Stock stock) {
        ContentValues values = new ContentValues();
        values.put(StockContract.StockTable.STOCK_SYMBOL, stock.getSymbol());
        values.put(StockContract.StockTable.STOCK_NAME, stock.getName());
        values.put(StockContract.StockTable.STOCK_PRICE, stock.getPrice());
        values.put(StockContract.StockTable.STOCK_PER_CHANGE, stock.getPercent_change());
        values.put(StockContract.StockTable.STOCK_CHANGE, stock.getChange());
        values.put(StockContract.StockTable.STOCK_OPEN, stock.getOpen());
        values.put(StockContract.StockTable.STOCK_D_HIGH, stock.getDays_high());
        values.put(StockContract.StockTable.STOCK_D_LOW, stock.getDays_low());
        values.put(StockContract.StockTable.STOCK_VOL, stock.getVol());
        values.put(StockContract.StockTable.STOCK_CAP, stock.getMktCap());
        values.put(StockContract.StockTable.STOCK_Y_HIGH, stock.getYear_high());
        values.put(StockContract.StockTable.STOCK_Y_lOW, stock.getYear_low());
        values.put(StockContract.StockTable.STOCK_PE, stock.getPeRatio());
        if (Utility.checkData(this, stock.getSymbol())) {
            getContentResolver().update(StockContract.StockTable.CONTENT_URI, values,
                    StockContract.StockTable.STOCK_SYMBOL + "=?", new String[]{stock.getSymbol()});
            //Event registered for ui updates
            EventBus.getDefault().post(new ServiceEvent());
        } else {
            getContentResolver().insert(StockContract.StockTable.CONTENT_URI, values);
        }
        //Notifying app widget data has changed
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(this, StockWidgetProvider.class));
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
    }

    private void showToast(final String message){
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(StockService.this.getApplicationContext(), message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
