package com.example.android.stockhawk.extras;


public class Constants {
    //Intent constant
    public static final String STOCK_INTENT = "symbol";
    //Spinner intent constant
    public static final String SPINNER_INTENT = "spinner";
    //Symbol select query
    public static final String SYMBOL_QUERY = "select * from yahoo.finance.quotes where symbol = \"%s\"";
    //Chart select query
    public static final String CHART_QUERY = "select * from yahoo.finance.historicaldata where symbol = \"%s\" and " +
            "startDate = \"%s\" and endDate = \"%s\"";
}
