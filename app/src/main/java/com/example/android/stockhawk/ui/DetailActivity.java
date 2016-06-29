package com.example.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.android.stockhawk.R;
import com.example.android.stockhawk.data.StockContract;
import com.example.android.stockhawk.extras.Constants;
import com.example.android.stockhawk.service.ServiceEvent;
import com.example.android.stockhawk.extras.Utility;
import com.example.android.stockhawk.service.DetailService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private String symbol, spinnerValue;
    private static final int LOADER_ID=2;
    private LineChart lineChart;
    private ProgressBar progressBar;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        symbol = getIntent().getStringExtra(Constants.STOCK_INTENT);
        spinner = (Spinner) findViewById(R.id.spinner);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        assert progressBar != null;
        progressBar.setVisibility(View.VISIBLE);
        lineChart = (LineChart) findViewById(R.id.chart);
        if(Utility.isNetworkAvailable(this)) {
            initializeLineChart();
        }else {
            lineChart.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setSpinnerValues();
        EventBus.getDefault().register(this);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /*
    Sets various parameters for line chart
     */
    private void initializeLineChart(){
            lineChart.animateXY(2000, 2000);
            lineChart.getXAxis().setTextColor(Color.WHITE);
            lineChart.getAxisLeft().setTextColor(Color.RED);
            lineChart.getAxisRight().setTextColor(Color.RED);
            lineChart.setNoDataTextDescription(getString(R.string.noNetLineChart));
            lineChart.setVisibility(View.INVISIBLE);
            lineChart.setTouchEnabled(true);
            lineChart.setDrawBorders(true);
            lineChart.setPinchZoom(true);
            lineChart.setDescriptionColor(Color.WHITE);
            lineChart.setDescription(getString(R.string.lineChartDescription));
    }

    /*
    Sets values to spinner
     */
    private void setSpinnerValues(){
        final Context context = this;
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list,
                getResources().getStringArray(R.array.spinnerList));
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.INVISIBLE);
                spinnerValue = parent.getItemAtPosition(position).toString();
                Intent intent = new Intent(context, DetailService.class);
                intent.putExtra(Constants.STOCK_INTENT, symbol);
                intent.putExtra(Constants.SPINNER_INTENT, spinnerValue);
                startService(intent);
                lineChart.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                StockContract.StockTable.CONTENT_URI,
                null, StockContract.StockTable.STOCK_SYMBOL + "=?", new String[]{symbol}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Utility.initializeViews(this, cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /*
    Event method called after getting chart data
     */
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onReceive(ServiceEvent event) {
          ArrayList<String> values = event.values;
            ArrayList<Float> closeValues = new ArrayList<>();
            for (int i=0;i<values.size();i++){
                closeValues.add(Float.parseFloat(values.get(i)));
            }
            if(Utility.isNetworkAvailable(this))
            createEntries(closeValues, event.dates);
        }

    /*
    Method for creating chart labels and entries
     */
    private void createEntries(ArrayList<Float> closeValues, ArrayList<String> labels){
        assert lineChart != null;
        lineChart.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        //creating list entry
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i=0; i<closeValues.size(); i++) {
            entries.add(new Entry(closeValues.get(i), i));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, getString(R.string.lineChartYAxis));
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawValues(false);
        lineDataSet.setCircleRadius(0);
        lineDataSet.setFillColor(Color.DKGRAY);
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        LineData lineData = new LineData(Utility.getFormattedLabels(labels, ""), lineDataSet);
        if(lineChart!=null)
            lineChart.setData(lineData);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
