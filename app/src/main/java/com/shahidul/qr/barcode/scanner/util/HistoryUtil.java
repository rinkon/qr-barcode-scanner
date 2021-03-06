package com.shahidul.qr.barcode.scanner.util;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shahidul.qr.barcode.scanner.db.HistoryDatabaseHelper;

/**
 * @author Shahidul Islam
 * @since 1/28/2016.
 */
public class HistoryUtil {
    private static final String TAG = HistoryUtil.class.getSimpleName();
    public static final int DISPLAYABLE_CONTENT_LENGTH = 97;

    public static void saveBarcodeDetails(Intent data){

    }
    public static long saveBarcodeDetails(String format, String content, long timeStamp, byte[] rawImageBytes, Context context){
        if (!PreferenceUtil.isSaveOnHistoryOn(context)){
            return -1;
        }
        if (!PreferenceUtil.isDuplicateAllowedInHistory(context)){
            HistoryDatabaseHelper.getInstance(context).deleteByFormatAndContent(format,content);
        }
        return HistoryDatabaseHelper.getInstance(context).insertToHistory(format, content, timeStamp, Util.byteArrayToHexString(rawImageBytes));

    }
    public static String getDisplayableContent(String content){
        if (content.length() <= DISPLAYABLE_CONTENT_LENGTH){
            return content;
        }
        return content.substring(0,DISPLAYABLE_CONTENT_LENGTH-3) + "...";
    }
}
