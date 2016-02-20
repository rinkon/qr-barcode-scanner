package com.shahidul.qr.barcode.scanner.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import com.shahidul.qr.barcode.scanner.Constant;
import com.shahidul.qr.barcode.scanner.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Shahidul Islam
 * @since 1/28/2016.
 */
public class Util {
    private static final String TAG = Util.class.getSimpleName();

    public static String getBarcodeDetails(Intent intent, Context context){
        StringBuilder stringBuilder = new StringBuilder();
        String format = intent.getStringExtra(Constant.BARCODE_FORMAT);
        if (format != null){
            stringBuilder.append("<b>Format</b> " + format + "<br></br>");
        }
        long timeStamp = intent.getLongExtra(Constant.TIME_STAMP,0);
        if (timeStamp != 0){
            stringBuilder.append("<b>Date</b> " + DateFormat.getDateTimeInstance().format(new Date(timeStamp)));
        }
        return stringBuilder.toString();
    }
    public static File getSharedImageLocation(Context context){
        File imageCachePath = new File(context.getCacheDir(), "images");
        if (!imageCachePath.exists()){
            imageCachePath.mkdirs();
        }
        return new File(imageCachePath+"/barcode.png");
    }
    public static void saveBitmapForSharing(Bitmap bitmap, File imageLocation){
        try {
            FileOutputStream stream = new FileOutputStream(imageLocation);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            Log.d(TAG,"BitmapSaveException",e);
        }

    }
    public static void playSoundTone(Context context){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.sound);
        mediaPlayer.start();
        /*try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            Log.d(TAG, "Exception in playing ringtone", e);
        }*/
    }
    public static void vibrateDevice(Context context){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }
}
