package com.example.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;

import com.example.android.stockhawk.extras.Constants;
import com.example.android.stockhawk.extras.HttpRequest;
import com.example.android.stockhawk.extras.Utility;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class DetailService extends IntentService {

    public DetailService() {
        super("DetailService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String json;
        String symbol = intent.getStringExtra(Constants.STOCK_INTENT);
        String spinnerValue = intent.getStringExtra(Constants.SPINNER_INTENT);
        String currentDate = Utility.getCurrentDate();
        String query = String.format(Constants.CHART_QUERY, symbol, Utility.getPreviousDate(currentDate, spinnerValue), currentDate);
        Uri uri = Utility.buildStockAPI(query);
        try {
            json = HttpRequest.get(uri.toString()).body();
        } catch (HttpRequest.HttpRequestException e) {
            json = "";
            e.printStackTrace();
        }
        getJsonData(json);
    }

    private void getJsonData(String json){
        ArrayList<String> values = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            JSONObject query = object.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");
            JSONArray quote = results.getJSONArray("quote");
            for(int i=quote.length()-1; i>=0; i--){
                JSONObject object1 = quote.getJSONObject(i);
                String format = String.format(Locale.ENGLISH, "%.2f", object1.getDouble("Close"));
                values.add(format);
                dates.add(object1.getString("Date"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Registering event
        EventBus.getDefault().post(new ServiceEvent(values, dates));
    }

}
