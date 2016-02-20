package com.shahidul.qr.barcode.scanner.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.shahidul.qr.barcode.scanner.R;

/**
 * @author Shahidul Islam
 * @since 1/30/2016.
 */
public class UrlInputDialogFragment extends DialogFragment {
    private static final String TAG = UrlInputDialogFragment.class.getSimpleName();
    private EditText urlInputView;
    private UrlInputListener listener;

    public static UrlInputDialogFragment getInstance(){
        return new UrlInputDialogFragment();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (UrlInputListener) activity;
        } catch (ClassCastException e) {
            Log.d(TAG,"ClassCastException",e);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_url_input,null);
        urlInputView = (EditText) view.findViewById(R.id.url_input);
        dialogBuilder.setView(view);
        dialogBuilder.setPositiveButton(R.string.decode, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null){
                    listener.onInputUrl(urlInputView.getText().toString().trim());
                }
            }
        });
        return dialogBuilder.create();
    }
    public interface UrlInputListener{
        void onInputUrl(String url);
    }
}
