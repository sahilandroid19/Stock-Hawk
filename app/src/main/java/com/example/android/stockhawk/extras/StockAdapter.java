package com.example.android.stockhawk.extras;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.stockhawk.R;
import com.example.android.stockhawk.data.StockContract;
import com.example.android.stockhawk.widget.StockWidgetProvider;

public class StockAdapter extends CursorRecyclerViewAdapter<StockAdapter.ViewHolder> {
    private Context mContext;
    private ViewClick clickListener;
    private Boolean typeOfChange = true;

    /*
    Interface for recycler view item click
     */
    public interface ViewClick{
        void onViewClick(String symbol);
    }

    public StockAdapter(Context context, Cursor cursor, ViewClick viewClick){
        super(context, cursor);
        mContext = context;
        clickListener = viewClick;
    }

    public void setChange(Boolean change){
        typeOfChange = change;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if(getCursor()!=null)
        return getCursor().getCount();
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
                Typeface regular_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Regular.ttf");
                Typeface bold_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Bold.ttf");
                Double value = Double.parseDouble(cursor.getString(5));
                if (value >= 0) {
                    holder.stock_change.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.stock_change));
                } else {
                    holder.stock_change.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.stock_change_negative));
                }
                holder.stock_symbol.setTypeface(regular_font);
                holder.stock_detail.setTypeface(regular_font);
                holder.stock_price.setTypeface(regular_font);
                holder.stock_change.setTypeface(bold_font);
                holder.stock_symbol.setText(cursor.getString(1));
                holder.stock_detail.setText(cursor.getString(2));
                holder.stock_price.setText(cursor.getString(3));
                if(typeOfChange) {
                    holder.stock_change.setText(cursor.getString(4));
                }else {
                    holder.stock_change.setText(cursor.getString(5));
                }
    }

    /*
    Removes stock from database
     */
    public void removeStock(int position){
        //Deleting data
        getCursor().moveToPosition(position);
        Uri uri = Uri.withAppendedPath(StockContract.StockTable.CONTENT_URI, getCursor().getString(1));
        mContext.getContentResolver().delete(uri, null, null);
        notifyItemRemoved(position);
        //Notifying app widget data has changed
        AppWidgetManager manager = AppWidgetManager.getInstance(mContext);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(mContext, StockWidgetProvider.class));
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView stock_symbol, stock_detail, stock_price, stock_change;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            stock_symbol = (TextView) itemView.findViewById(R.id.stock_symbol);
            stock_detail = (TextView) itemView.findViewById(R.id.stock_detail);
            stock_price = (TextView) itemView.findViewById(R.id.stock_value);
            stock_change = (TextView) itemView.findViewById(R.id.stock_change);
        }

        @Override
        public void onClick(View v) {
            getCursor().moveToPosition(getAdapterPosition());
            clickListener.onViewClick(getCursor().getString(1));
        }
    }
}
