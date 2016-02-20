package com.shahidul.qr.barcode.scanner.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.shahidul.qr.barcode.scanner.Constant;
import com.shahidul.qr.barcode.scanner.R;
import com.shahidul.qr.barcode.scanner.activity.BarcodeDetailsActivity;
import com.shahidul.qr.barcode.scanner.util.BarcodeUtil;

import java.util.Arrays;
import java.util.Set;

/**
 * @author Shahidul Islam
 * @since 1/29/2016.
 */
public class CreateBarcodeFragment extends Fragment implements View.OnClickListener {

    private static final int CONTENT_LINE_NUMBERS = 8;
    private Spinner barcodeTypeSpinner;
    private EditText contentView;
    private TextView createView;
    public static CreateBarcodeFragment newInstance() {
        return new CreateBarcodeFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_barcode, container,false);
        barcodeTypeSpinner = (Spinner) view.findViewById(R.id.barcode_type);
        contentView = (EditText) view.findViewById(R.id.content);
        createView = (TextView) view.findViewById(R.id.create);
        createView.setOnClickListener(this);
        Set<String> barcodeFormatSet = BarcodeUtil.barcodeFormatHashMap.keySet();
        String[] barcodeFormats = new String[barcodeFormatSet.size()];
        barcodeFormatSet.toArray(barcodeFormats);
        Arrays.sort(barcodeFormats);
        ArrayAdapter<String> barcodeTypeAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,barcodeFormats);
        barcodeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barcodeTypeSpinner.setAdapter(barcodeTypeAdapter);
        barcodeTypeSpinner.setSelection(barcodeFormats.length-2);//Make QR Code Default
        barcodeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeInputTypeIfNeed();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }


    @Override
    public void onClick(View v) {
        String content = contentView.getText().toString().trim();
        BarcodeFormat barcodeFormat = getSelectedBarcodeFormat();
        String errorText = BarcodeUtil.validateText(barcodeFormat,content);
        if (errorText == null) {
            Intent intent = new Intent(getContext(), BarcodeDetailsActivity.class);
            intent.putExtra(Constant.TEXT, content);
            intent.putExtra(Constant.BARCODE_FORMAT, barcodeFormat.toString());
            intent.putExtra(Constant.TIME_STAMP, System.currentTimeMillis());
            startActivity(intent);
        }
        else {
            MessageDialogFragment.newInstance("Error",errorText,null).show(getFragmentManager(),"error_message");
        }
    }
    private BarcodeFormat getSelectedBarcodeFormat(){
        return BarcodeUtil.barcodeFormatHashMap.get(barcodeTypeSpinner.getSelectedItem().toString());
    }
    private void changeInputTypeIfNeed(){
        BarcodeFormat barcodeFormat = getSelectedBarcodeFormat();
        if (BarcodeUtil.isSupportOnlyNumeric(barcodeFormat)){
            contentView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            contentView.setLines(CONTENT_LINE_NUMBERS);
            contentView.setHint(getString(R.string.barcode_content_number_hint));
        }
        else {
            contentView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            contentView.setLines(CONTENT_LINE_NUMBERS);
            contentView.setHint(getString(R.string.barcode_content_text_hint));
        }
    }

}
