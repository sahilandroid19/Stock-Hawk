package com.example.android.stockhawk.extras;

public class Stock {
    private String symbol;
    private String name;
    private String price;
    private String percent_change;
    private String change;
    private String open;
    private String days_high;
    private String days_low;
    private String vol;
    private String mktCap;
    private String year_high;
    private String year_low;
    private String peRatio;

    public Stock(){}

    public void setPercent_change(String percent_change) {
        this.percent_change = percent_change;
    }

    public void setPrice(String price) {

        this.price = price;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setSymbol(String symbol) {

        this.symbol = symbol;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getPercent_change() {

        return percent_change;
    }

    public String getPrice() {

        return price;
    }

    public String getName() {

        return name;
    }

    public String getSymbol() {

        return symbol;
    }

    public String getChange() {
        return change;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getDays_high() {
        return days_high;
    }

    public void setDays_high(String days_high) {
        this.days_high = days_high;
    }

    public String getDays_low() {
        return days_low;
    }

    public void setDays_low(String days_low) {
        this.days_low = days_low;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    public String getMktCap() {
        return mktCap;
    }

    public void setMktCap(String mktCap) {
        this.mktCap = mktCap;
    }

    public String getYear_high() {
        return year_high;
    }

    public void setYear_high(String year_high) {
        this.year_high = year_high;
    }

    public String getYear_low() {
        return year_low;
    }

    public void setYear_low(String year_low) {
        this.year_low = year_low;
    }

    public String getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(String peRatio) {
        this.peRatio = peRatio;
    }
}
