package com.shahidul.qr.barcode.scanner.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.shahidul.qr.barcode.scanner.Constant;
import com.shahidul.qr.barcode.scanner.R;

import com.shahidul.qr.barcode.scanner.util.BarcodeUtil;
import com.shahidul.qr.barcode.scanner.util.Util;

import java.io.File;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * @author Shahidul Islam
 * @since 1/28/2016.
 */
public class BarcodeDetailsActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = BarcodeDetailsActivity.class.getSimpleName();
    private TextView mBarcodeDetailsView;
    private TextView mBarcodeTextView;
    private ImageView mBarcodeImageView;
    private TextView mChangeColorView;
    private TextView mShareView;
    private Bitmap mBarcodeBitMap;
    private String mFormat;
    private String mContent;
    private byte[] mRawBytes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_details);
        setUpToolBar();
        mBarcodeDetailsView = (TextView) findViewById(R.id.barcode_details);
        mBarcodeTextView = (TextView) findViewById(R.id.barcode_content);
        mBarcodeImageView = (ImageView) findViewById(R.id.barcode_image);
        mChangeColorView = (TextView) findViewById(R.id.change_color);
        mShareView = (TextView) findViewById(R.id.share);
        mChangeColorView.setOnClickListener(this);
        mShareView.setOnClickListener(this);
        Intent intent = getIntent();
        mBarcodeDetailsView.setText(Html.fromHtml(Util.getBarcodeDetails(intent, getApplicationContext())));
        mFormat = intent.getStringExtra(Constant.BARCODE_FORMAT);
        mContent = intent.getStringExtra(Constant.TEXT);
        mRawBytes = intent.getByteArrayExtra(Constant.RAW_DATA);
        mBarcodeTextView.setText(mContent);
        showBarcode(mContent,mFormat, BarcodeUtil.BLACK);
    }
    void showBarcode(String content, String format, int color){
        BarcodeFormat barcodeFormat = BarcodeFormat.valueOf(format);
        try {
            mBarcodeBitMap = BarcodeUtil.encodeAsBitmap(content,barcodeFormat,color, Constant.BARCODE_WIDTH,Constant.BARCODE_HEIGHT);
            mBarcodeImageView.setImageBitmap(mBarcodeBitMap);
        } catch (WriterException e) {
            Log.d(TAG, "BarcodeEncodingException", e);
        }

       /* if (mRawBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mRawBytes, 0, mRawBytes.length);
            mBarcodeImageView.setImageBitmap(bitmap);
        }
*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_color:
                showColorPicker();
                break;
            case R.id.share:
                if (mBarcodeBitMap != null) {
                    File imageLocation = Util.getSharedImageLocation(this);
                    Util.saveBitmapForSharing(mBarcodeBitMap, imageLocation);
                    Uri contentUri = FileProvider.getUriForFile(this, "com.shahidul.qr.barcode.scanner.fileprovider", imageLocation);
                    if (contentUri != null) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        shareIntent.setType("image/png");
                        //shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                        //shareIntent.putExtra(Intent.EXTRA_TEXT,mContent);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        startActivity(Intent.createChooser(shareIntent, "Choose an app"));

                    }
                }
                break;
            default:
                break;
        }
    }

    private void showColorPicker(){
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, 0xFF000000, true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                showBarcode(mContent,mFormat,color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });
        dialog.show();
    }
}
