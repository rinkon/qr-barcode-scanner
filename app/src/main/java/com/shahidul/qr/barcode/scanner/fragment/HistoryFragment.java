package com.shahidul.qr.barcode.scanner.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.shahidul.qr.barcode.scanner.R;
import com.shahidul.qr.barcode.scanner.db.HistoryDatabaseHelper;
import com.shahidul.qr.barcode.scanner.db.SQLiteCursorLoader;
import com.shahidul.qr.barcode.scanner.util.HistoryUtil;

import java.text.DateFormat;
import java.util.Date;

/**
 * @author Shahidul Islam
 * @since 2/16/2016.
 */
public class HistoryFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int HISTORY_LOADER_ID = 100;
    private static final String TAG = HistoryFragment.class.getSimpleName();
    private HistoryListAdapter mAdapter;
    private SQLiteCursorLoader cursorLoader;

    public static HistoryFragment newInstance(){
        return new HistoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
        getLoaderManager().initLoader(HISTORY_LOADER_ID, null, this);
        mAdapter = new HistoryListAdapter(getContext(),null,true);
        setListAdapter(mAdapter);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG,new Exception().getStackTrace()[0].getMethodName());
        String sql = "SELECT * FROM " + HistoryDatabaseHelper.TABLE_NAME;
        cursorLoader = new SQLiteCursorLoader(getContext(),new HistoryDatabaseHelper(getContext()),sql,null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG,new Exception().getStackTrace()[0].getMethodName());
        mAdapter.changeCursor(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG,new Exception().getStackTrace()[0].getMethodName());
        mAdapter.changeCursor(null);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,new Exception().getStackTrace()[0].getMethodName());
        super.onDestroy();
    }

    private class HistoryListAdapter extends CursorAdapter {


        public HistoryListAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.history_list_item, null);
            HistoryViewHolder historyViewHolder = new HistoryViewHolder();
            historyViewHolder.mContentView = (TextView) view.findViewById(R.id.content);
            historyViewHolder.mDateView = (TextView) view.findViewById(R.id.date);
            historyViewHolder.mTimeView = (TextView) view.findViewById(R.id.time);
            view.setTag(historyViewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            HistoryViewHolder historyViewHolder = (HistoryViewHolder) view.getTag();
            String content = cursor.getString(cursor.getColumnIndex(HistoryDatabaseHelper.COLUMN_BARCODE_CONTENT));
            long dateTime = cursor.getLong(cursor.getColumnIndex(HistoryDatabaseHelper.COLUMN_DATE));
            historyViewHolder.mContentView.setText(HistoryUtil.getDisplayableContent(content));
            historyViewHolder.mDateView.setText(DateFormat.getDateInstance().format(new Date(dateTime)));
            historyViewHolder.mTimeView.setText(DateFormat.getTimeInstance().format(new Date(dateTime)));
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long id = cursor.getLong(cursor.getColumnIndex(HistoryDatabaseHelper.COLUMN_ID));
                    /*int affectedRows = new HistoryDatabaseHelper(getContext()).deleteHistoryById(id);
                    Log.d(TAG,affectedRows + " row deleted");*/
                    cursorLoader.delete(HistoryDatabaseHelper.TABLE_NAME,HistoryDatabaseHelper.COLUMN_ID + " = ?",new String[]{String.valueOf(id)});
                    //getLoaderManager().initLoader(HISTORY_LOADER_ID, null, HistoryFragment.this);
                    return true;
                }
            });
        }
    }


    private class HistoryViewHolder{
        TextView mContentView;
        TextView mDateView;
        TextView mTimeView;
    }
}
