package com.shahidul.qr.barcode.scanner.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.shahidul.qr.barcode.scanner.Constant;
import com.shahidul.qr.barcode.scanner.R;

import com.shahidul.qr.barcode.scanner.db.HistoryDatabaseHelper;
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
    private ActionMode mActionMode;

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
        mContent = intent.getStringExtra(Constant.BARCODE_CONTENT);
        mRawBytes = intent.getByteArrayExtra(Constant.RAW_IMAGE_DATA);
        mBarcodeTextView.setText(mContent);
        showBarcode(mContent, mFormat, BarcodeUtil.BLACK);

        mBarcodeTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mActionMode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = startActionMode(mActionModeCallback);
                return true;
            }
        });
    }
    void showBarcode(String content, String format, int color){
        BarcodeFormat barcodeFormat = BarcodeFormat.valueOf(format);
        try {
            mBarcodeBitMap = BarcodeUtil.encodeAsBitmap(content,barcodeFormat,color, Constant.BARCODE_WIDTH,Constant.BARCODE_HEIGHT);
        } catch (WriterException e) {
            Log.d(TAG, "BarcodeEncodingException", e);
        }
        if (mBarcodeBitMap != null){
            mBarcodeImageView.setImageBitmap(mBarcodeBitMap);
        }
        else {
            mChangeColorView.setEnabled(false);
            if (mRawBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(mRawBytes, 0, mRawBytes.length);
                mBarcodeImageView.setImageBitmap(bitmap);
            }
        }
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
                        shareIntent.putExtra(Intent.EXTRA_TEXT,mContent);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        startActivity(Intent.createChooser(shareIntent, "Choose an app"));

                    }
                }
                break;
            default:
                break;
        }
    }
    ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.barcode_content_context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.copy:
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("barcode_content", mContent);
                    clipboard.setPrimaryClip(clip);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
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
