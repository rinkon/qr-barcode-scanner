package com.shahidul.qr.barcode.scanner.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.shahidul.qr.barcode.scanner.R;

/**
 * @author Shahidul Islam
 * @since 3/3/2016.
 */
public class AboutActivity extends BaseActivity {
    private TextView appVersionView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setUpToolBar();
        appVersionView = (TextView) findViewById(R.id.app_version);

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersionView.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
