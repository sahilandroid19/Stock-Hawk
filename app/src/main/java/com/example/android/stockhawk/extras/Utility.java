package com.example.android.stockhawk.extras;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.TextView;

import com.example.android.stockhawk.R;
import com.example.android.stockhawk.data.StockContract;
import com.example.android.stockhawk.ui.DetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utility {

    /*
    Used to update data from cursor
     */
    public static void initializeViews(Context context, Cursor cursor){
        DetailActivity activity = (DetailActivity) context;
        if(cursor!=null&&cursor.getCount()>0){
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                TextView detailSymbol = (TextView) activity.findViewById(R.id.detail_symbol);
                assert detailSymbol != null;
                detailSymbol.setText(cursor.getString(1));
                TextView detailName = (TextView) activity.findViewById(R.id.detail_name);
                assert detailName != null;
                detailName.setText(cursor.getString(2));
                TextView detailPrice = (TextView) activity.findViewById(R.id.detail_price);
                assert detailPrice != null;
                detailPrice.setText(cursor.getString(3));
                TextView detailChange = (TextView) activity.findViewById(R.id.detail_change);
                assert detailChange != null;
                detailChange.setText(cursor.getString(5));
                TextView detailPChange = (TextView) activity.findViewById(R.id.detail_p_change);
                assert detailPChange != null;
                detailPChange.setText(cursor.getString(4));
                if(Double.parseDouble(cursor.getString(5))>=0){
                    detailChange.setTextColor(context.getResources().getColor(R.color.changePositive));
                    detailPChange.setTextColor(context.getResources().getColor(R.color.changePositive));
                }else {
                    detailChange.setTextColor(context.getResources().getColor(R.color.changeNegative));
                    detailPChange.setTextColor(context.getResources().getColor(R.color.changeNegative));
                }
                TextView openView = (TextView) activity.findViewById(R.id.stock_open);
                assert openView != null;
                openView.setText(cursor.getString(6));
                TextView dhighView = (TextView) activity.findViewById(R.id.stock_d_high);
                assert dhighView != null;
                dhighView.setText(cursor.getString(7));
                TextView dlowView = (TextView) activity.findViewById(R.id.stock_d_low);
                assert dlowView != null;
                dlowView.setText(cursor.getString(8));
                TextView volView = (TextView) activity.findViewById(R.id.stock_vol);
                assert volView != null;
                volView.setText(cursor.getString(9));
                TextView mktcapView = (TextView) activity.findViewById(R.id.stock_mkt);
                assert mktcapView != null;
                mktcapView.setText(cursor.getString(10));
                TextView yhighView = (TextView) activity.findViewById(R.id.stock_y_high);
                assert yhighView != null;
                yhighView.setText(cursor.getString(11));
                TextView ylowView = (TextView) activity.findViewById(R.id.stock_y_low);
                assert ylowView != null;
                ylowView.setText(cursor.getString(12));
                TextView peView = (TextView) activity.findViewById(R.id.stock_pe);
                assert peView != null;
                peView.setText(cursor.getString(13));
            }
        }
    }

    /*
    Checks for internet network
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
    Checks if symbol is already in database
     */
    public static boolean checkData(Context context, String symbol){
        Cursor cursor = context.getContentResolver().query(StockContract.StockTable.CONTENT_URI,
                null, StockContract.StockTable.STOCK_SYMBOL + "=?",
                new String[]{symbol}, null);
        if(cursor!=null&&cursor.getCount()>0) {
            cursor.close();
            return true;
        }
        return false;
    }

    /*
    Builds api for stock using query
     */
    public static Uri buildStockAPI(String query){
        return Uri.parse("https://query.yahooapis.com/v1/public/yql?")
                .buildUpon()
                .appendQueryParameter("q", query)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("diagnostics", "true")
                .appendQueryParameter("env", "store://datatables.org/alltableswithkeys")
                .appendQueryParameter("callback", "")
                .build();
    }

    public static String getCurrentDate(){
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
    }

    /*
    Returns date based on spinner value
     */
    public static String getPreviousDate(String currentDate, String number){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            calendar.setTime(dateFormat.parse(currentDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        switch (number){
            case "1W":
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                break;
            case "1M":
                calendar.add(Calendar.MONTH, -1);
                break;
            case "3M":
                calendar.add(Calendar.MONTH, -3);
                break;
            case "6M":
                calendar.add(Calendar.MONTH, -6);
                break;
            case "1Y":
                calendar.add(Calendar.YEAR, -1);
                break;
            default:
                calendar.add(Calendar.DAY_OF_YEAR, -7);
        }
        Date newDate = calendar.getTime();
        return dateFormat.format(newDate);
    }

    /*
    Changes format of date according to spinner value
     */
    public static ArrayList<String> getFormattedLabels(ArrayList<String> dates, String format){
        ArrayList<String> labels = new ArrayList<>();
        String inputPattern = "yyyy-MM-dd";
        String outputPattern;
        switch (format){
            case "1W":
                outputPattern = "MM-dd";
                break;
            default:
                outputPattern = "MMM dd";
                break;
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
        Date date;
        for(int i=0; i<dates.size(); i++) {
            try {
                date = inputFormat.parse(dates.get(i));
                labels.add(outputFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return labels;
    }
}
