package com.shahidul.qr.barcode.scanner.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.shahidul.qr.barcode.scanner.Constant;
import com.shahidul.qr.barcode.scanner.R;
import com.shahidul.qr.barcode.scanner.exception.InputStreamNotBitmapException;
import com.shahidul.qr.barcode.scanner.fragment.CreateBarcodeFragment;
import com.shahidul.qr.barcode.scanner.fragment.HistoryFragment;
import com.shahidul.qr.barcode.scanner.fragment.MessageDialogFragment;
import com.shahidul.qr.barcode.scanner.fragment.ScannerFragmentList;
import com.shahidul.qr.barcode.scanner.fragment.UrlInputDialogFragment;
import com.shahidul.qr.barcode.scanner.util.BarcodeUtil;
import com.shahidul.qr.barcode.scanner.util.HistoryUtil;
import com.shahidul.qr.barcode.scanner.util.PreferenceUtil;
import com.shahidul.qr.barcode.scanner.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements ScannerFragmentList.OnScannerListFragmentInteractionListener,UrlInputDialogFragment.UrlInputListener {
    private static final int BARCODE_SCAN_REQUEST_CODE = 101;
    private static final int PICK_IMAGE_REQUEST_CODE = 102;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProgressDialog barcodeDownloadingProgressDialog;
    private int[] tabIcons = {
            R.drawable.camera,R.drawable.edit,R.drawable.history
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolBar();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showText(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case BARCODE_SCAN_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    Intent intent = new Intent(getApplicationContext(), BarcodeDetailsActivity.class);
                    intent.putExtras(data.getExtras());
                    startActivity(intent);
                    HistoryUtil.saveBarcodeDetails(data.getStringExtra(Constant.BARCODE_FORMAT),
                            data.getStringExtra(Constant.BARCODE_CONTENT),data.getLongExtra(Constant.TIME_STAMP, 0),data.getByteArrayExtra(Constant.RAW_IMAGE_DATA),getApplicationContext());
                }
                break;
            case PICK_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                       showBarcodeFromInputStream(inputStream);
                    } catch (FileNotFoundException e) {
                        Log.d(TAG,"FileNotFound",e);
                    }
                }
                break;
        }
    }

    public void showBarcodeFromInputStream(InputStream inputStream){
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                throw new InputStreamNotBitmapException("Input stream is not bitmap");
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] rawImageBytes = stream.toByteArray();

            Result barcodeDecodeResult = BarcodeUtil.decodeBarcodeFromFile(bitmap);
            Intent intent = new Intent(getApplicationContext(),BarcodeDetailsActivity.class);
            intent.putExtra(Constant.BARCODE_CONTENT, barcodeDecodeResult.getText());
            intent.putExtra(Constant.BARCODE_FORMAT,barcodeDecodeResult.getBarcodeFormat().toString());
            intent.putExtra(Constant.TIME_STAMP, barcodeDecodeResult.getTimestamp());

            intent.putExtra(Constant.RAW_IMAGE_DATA,rawImageBytes);
            startActivity(intent);
            HistoryUtil.saveBarcodeDetails(barcodeDecodeResult.getBarcodeFormat().toString(),barcodeDecodeResult.getText(),
                    barcodeDecodeResult.getTimestamp(),rawImageBytes,getApplicationContext());
            if (PreferenceUtil.isPlaySoundOn(getApplicationContext())){
                Util.playSoundTone(getApplicationContext());
            }
            if (PreferenceUtil.isVibrateOn(getApplicationContext())){
                Util.vibrateDevice(getApplicationContext());
            }
        } catch (InputStreamNotBitmapException e) {
            showText(getString(R.string.image_is_not_barcode));
        } catch (NotFoundException e) {
            Log.d(TAG, "File not found", e);
            showText(getString(R.string.barcode_not_found));
        }
    }
    private void setupTabIcons() {
        for (int i = 0; i < tabIcons.length; i++){
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
        /*TextView scanView = (TextView) LayoutInflater.from(this).inflate(R.layout.tab, null);
        scanView.setText(R.string.scan);
        scanView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.camera, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(scanView);

        TextView createView = (TextView) LayoutInflater.from(this).inflate(R.layout.tab, null);
        createView.setText(R.string.create);
        createView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.file, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(createView);

        TextView historyView = (TextView) LayoutInflater.from(this).inflate(R.layout.tab, null);
        historyView.setText(R.string.history);
        historyView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.history,0, 0, 0);
        tabLayout.getTabAt(2).setCustomView(historyView);*/
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(ScannerFragmentList.newInstance(1), getString(R.string.scan));
        adapter.addFrag(CreateBarcodeFragment.newInstance(), getString(R.string.create));
        adapter.addFrag(HistoryFragment.newInstance(), getString(R.string.history));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onListFragmentInteraction(String optionName, int optionIconResourceId, int position) {
        switch (position){
            case 0:
                startActivityForResult(new Intent(getApplicationContext(),ScannerActivity.class),BARCODE_SCAN_REQUEST_CODE);
                break;
            case 1:
                pickBarcodeImage();
                break;
            case 2:
                UrlInputDialogFragment urlInputDialogFragment = UrlInputDialogFragment.getInstance();
                urlInputDialogFragment.show(getSupportFragmentManager(),"url_input_dialog");
                break;
        }

    }

    private void pickBarcodeImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_barcode)), PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onInputUrl(String url) {
        String completeUrl = url;
        if (!url.startsWith("http") || !url.startsWith("ftp")){
            completeUrl = "http://" + url;
        }
        URL urlObject = null;
        try {
            urlObject = new URL(completeUrl);
        } catch (MalformedURLException e) {
            Log.d(TAG, "Invalid URL",e);
            MessageDialogFragment.newInstance("Invalid URL", url + " is not a valid url",null).show(getSupportFragmentManager(), "message_dialog");
        }
        if (urlObject != null){
            new BarcodeDownloaderTask().execute(urlObject);
        }
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            //return mFragmentList.get(position);
            Log.d(TAG,new Exception().getStackTrace()[0].getMethodName() + " " + position);
            switch (position){
                case 0:
                    return ScannerFragmentList.newInstance(1);
                case 1:
                    return CreateBarcodeFragment.newInstance();
                case 2:
                    return HistoryFragment.newInstance();
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private class BarcodeDownloaderTask extends AsyncTask<URL,Void,InputStream>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            barcodeDownloadingProgressDialog = ProgressDialog.show(MainActivity.this, "", "Loading...");
        }

        @Override
        protected InputStream doInBackground(URL... params) {
            URL url = params[0];
            URLConnection urlConnection = null;
            try {
                urlConnection = url.openConnection();
                return urlConnection.getInputStream();
            } catch (IOException e) {
                Log.d(TAG,"IoException",e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            barcodeDownloadingProgressDialog.dismiss();
            if (inputStream != null){
                showBarcodeFromInputStream(inputStream);
            }
        }
    }
}
