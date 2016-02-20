package com.shahidul.qr.barcode.scanner.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Shahidul Islam
 * @since 2/21/2016.
 */
public class PreferenceUtil {
    public static boolean isPlaySoundOn(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("sound",true);
    }
    public static boolean isVibrateOn(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("vibrate",true);
    }
    public static boolean isStartInScanModeOn(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("start_in_scan_mode",false);
    }
    public static boolean isSaveOnHistoryOn(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("save_in_history",true);
    }
}
