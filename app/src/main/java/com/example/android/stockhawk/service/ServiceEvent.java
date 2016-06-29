package com.example.android.stockhawk.service;

import java.util.ArrayList;

public class ServiceEvent {
    public ArrayList<String> values = new ArrayList<>();
    public ArrayList<String> dates = new ArrayList<>();

    public ServiceEvent(){}

    public ServiceEvent(ArrayList<String> values, ArrayList<String> dates){
        this.values = values;
        this.dates = dates;
    }
}
