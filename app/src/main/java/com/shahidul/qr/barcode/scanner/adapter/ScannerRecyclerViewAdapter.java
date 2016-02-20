package com.shahidul.qr.barcode.scanner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shahidul.qr.barcode.scanner.R;
import com.shahidul.qr.barcode.scanner.fragment.ScannerFragmentList.OnScannerListFragmentInteractionListener;

public class ScannerRecyclerViewAdapter extends RecyclerView.Adapter<ScannerRecyclerViewAdapter.ViewHolder> {

    private String[] mOptionNames;
    private int[] mOptionIcons;
    private final OnScannerListFragmentInteractionListener mListener;

    public ScannerRecyclerViewAdapter(String[] options,int[] optionIcons, OnScannerListFragmentInteractionListener listener) {
        mOptionNames = options;
        mOptionIcons = optionIcons;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_scanner_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mOptionIcon.setImageResource(mOptionIcons[position]);
        holder.mOptionName.setText(mOptionNames[position]);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(mOptionNames[position], mOptionIcons[position], position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOptionNames.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mOptionIcon;
        public final TextView mOptionName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mOptionIcon = (ImageView) view.findViewById(R.id.optionIcon);
            mOptionName = (TextView) view.findViewById(R.id.optionName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mOptionName.getText() + "'";
        }
    }
}
