package com.example.android.stockhawk.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.stockhawk.ItemAnimators.DividerItemDecoration;
import com.example.android.stockhawk.ItemAnimators.StockTouchHelper;
import com.example.android.stockhawk.R;
import com.example.android.stockhawk.data.StockContract;
import com.example.android.stockhawk.extras.Constants;
import com.example.android.stockhawk.extras.StockAdapter;
import com.example.android.stockhawk.extras.Utility;
import com.example.android.stockhawk.service.ServiceEvent;
import com.example.android.stockhawk.service.StockService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/*
Swipe recycler view to update data
 */
public class StockFragment extends Fragment implements StockDialogFragment.DialogInterface, LoaderManager.LoaderCallbacks<Cursor>, StockAdapter.ViewClick, SwipeRefreshLayout.OnRefreshListener {
    private onScroll mScrollListener;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private StockAdapter mRecyclerAdapter;
    private static final int LOADER_ID = 1;

    public StockFragment() {}

    /*
    Interface for recycler view scrolling
     */
    public interface onScroll{
        void onScrolled(int state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerAdapter = new StockAdapter(getActivity(),null, this);
        setRecyclerView();
        mScrollListener = (onScroll) getActivity();
        if(!Utility.isNetworkAvailable(getActivity())){
            createDialog();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initialiseLoader();
        //Registering event bus for events
        EventBus.getDefault().register(this);
    }

    private void createDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.outdated_title));
        builder.setMessage(getString(R.string.outdated));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
    Sets various implications related to recycler view
    */
    private void setRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(mRecyclerAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mScrollListener.onScrolled(newState);
            }
        });
        StockTouchHelper touchHelper = new StockTouchHelper(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
        touchHelper.setStockAdapter(mRecyclerAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /*
    Called when recycler view is clicked
     */
    @Override
    public void onViewClick(String symbol) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(Constants.STOCK_INTENT, symbol);
        startActivity(intent);
    }

    /*
    Called when activity is refreshed
     */
    @Override
    public void onRefresh() {
        if(Utility.isNetworkAvailable(getActivity())) {
            Cursor cursor = getActivity().getContentResolver().query(StockContract.StockTable.CONTENT_URI,
                    null,null,null,null);
            if(cursor!=null&&cursor.getCount()>0){
                updateData(cursor);
                cursor.close();
            }
        }else {
            Toast.makeText(getActivity(), getString(R.string.noNetwork), Toast.LENGTH_LONG).show();
            refreshLayout.setRefreshing(false);
        }
    }

    private void initialiseLoader(){
        //initialising loader
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public void onMenuButtonClicked(Boolean isChangeInPercent){
      mRecyclerAdapter.setChange(isChangeInPercent);
    }

    /*
    Called when dialog button clicked
     */
    @Override
    public void buttonClicked(String symbol) {
        ArrayList<String> symbols = new ArrayList<>();
        symbols.add(symbol);
        if(Utility.checkData(getActivity(), symbol)){
            Toast.makeText(getActivity(), getString(R.string.stockInList), Toast.LENGTH_LONG).show();
        }else {
            if(Utility.isNetworkAvailable(getActivity())) {
                Intent intent = new Intent(getActivity(), StockService.class);
                intent.putExtra(Constants.STOCK_INTENT, symbols);
                getActivity().startService(intent);
            }else {
                Toast.makeText(getActivity(), getString(R.string.noNetwork), Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                StockContract.StockTable.CONTENT_URI,
                null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecyclerAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerAdapter.swapCursor(null);
    }

    /*
    Syncs for new stock updates
     */
    private void updateData(Cursor cursor){
        ArrayList<String> symbols = new ArrayList<>();
        if(cursor!=null&&cursor.getCount()>0){
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                symbols.add(cursor.getString(1));
            }
        }
            Intent intent = new Intent(getActivity(), StockService.class);
            intent.putExtra(Constants.STOCK_INTENT, symbols);
            getActivity().startService(intent);
    }

    /*
    Event bus method
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ServiceEvent event) {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.clearOnScrollListeners();
    }
}
