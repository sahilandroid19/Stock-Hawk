package com.example.android.stockhawk.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.stockhawk.R;

public class StockActivity extends AppCompatActivity implements StockFragment.onScroll {

    private FloatingActionButton fab;
    private Boolean isChangeInPercent = true;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockDialogFragment fragment = new StockDialogFragment();
                fragment.setTargetFragment(getSupportFragmentManager().findFragmentById(R.id.fragment), 0);
                fragment.setCancelable(true);
                fragment.show(getSupportFragmentManager(), "");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (prefs.getBoolean("firstrun", true)) {
            Toast.makeText(this, getString(R.string.firstTime), Toast.LENGTH_LONG).show();
            prefs.edit().putBoolean("firstrun", false).apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id==R.id.change_value){
            isChangeInPercent = !isChangeInPercent;
            StockFragment fragment = (StockFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            fragment.onMenuButtonClicked(isChangeInPercent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Called when recycler list is scrolled
     */
    @Override
    public void onScrolled(int state) {
        if(state== RecyclerView.SCROLL_STATE_DRAGGING){
            fab.hide();
        }else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab.show();
                }
            }, 400);
        }
    }
}
