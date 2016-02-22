package com.shahidul.qr.barcode.scanner.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.shahidul.qr.barcode.scanner.exception.InputStreamNotBitmapException;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shahidul Islam
 * @since 1/28/2016.
 */
public class BarcodeUtil {
    public static final String SUPPORTS_ONLY_NUMBERIC = "supports only numeric content";
    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0xFF000000;
    private static final String TAG = BarcodeUtil.class.getSimpleName();
    public static Map<String,BarcodeFormat> barcodeFormatHashMap = null;
    static {
        barcodeFormatHashMap = new HashMap<>();
        barcodeFormatHashMap.put("Aztec",BarcodeFormat.AZTEC);
        barcodeFormatHashMap.put("CODABAR",BarcodeFormat.CODABAR);
        barcodeFormatHashMap.put("Code 39",BarcodeFormat.CODE_39);
        //barcodeFormatHashMap.put("Code 93",BarcodeFormat.CODE_93); Can't encode Zxing
        barcodeFormatHashMap.put("Code 128",BarcodeFormat.CODE_128);
        barcodeFormatHashMap.put("Data Matrix",BarcodeFormat.DATA_MATRIX);
        barcodeFormatHashMap.put("EAN-8",BarcodeFormat.EAN_8);
        //barcodeFormatHashMap.put("EAN-13",BarcodeFormat.EAN_13); content needs to be 13 digits long & pass checksum so implement later
        barcodeFormatHashMap.put("ITF",BarcodeFormat.ITF);
       // barcodeFormatHashMap.put("MaxiCode",BarcodeFormat.MAXICODE); Can't encode zxing
        barcodeFormatHashMap.put("PDF417",BarcodeFormat.PDF_417);
        barcodeFormatHashMap.put("QR Code",BarcodeFormat.QR_CODE);
        //barcodeFormatHashMap.put("RSS 14",BarcodeFormat.RSS_14); Can't encode zxing
        //barcodeFormatHashMap.put("RSS EXPANDED",BarcodeFormat.RSS_EXPANDED); Can't encode zxing
        barcodeFormatHashMap.put("UPC-A",BarcodeFormat.UPC_A);
        //barcodeFormatHashMap.put("UPC-E",BarcodeFormat.UPC_E); Can't encode zxing
        //barcodeFormatHashMap.put("UPC/EAN extension",BarcodeFormat.UPC_EAN_EXTENSION); Can't encode zxing
    }
    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int color, int imageWidth, int imageHeight) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, imageWidth, imageHeight, hints);
        } catch (IllegalArgumentException iae) {
            Log.d(TAG, "EncodeException", iae);
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? color : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        /*for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;*/
        return "UTF-8";
    }

    public static Result decodeBarcodeFromFile(Bitmap bitmap) throws InputStreamNotBitmapException, NotFoundException {
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();
        bitmap = null;
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader reader = new MultiFormatReader();
        return reader.decode(bBitmap);
    }

    public static String validateText(BarcodeFormat barcodeFormat, String content){
        switch (barcodeFormat){
            case AZTEC:
                return null;
            case CODABAR:
                if(TextUtils.isDigitsOnly(content)){
                    return null;
                }
                else {
                    return barcodeFormat + " " + SUPPORTS_ONLY_NUMBERIC;
                }
            case CODE_128:
                return null;
            case CODE_39:
                for (int i = 0; i < content.length();i++){
                    int index = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(content.charAt(i));
                    if (index < 0){
                        return "Content contains Illegal character";
                    }
                }
                return null;
            case EAN_8:
                if (content.length() != 8){
                    return "Content must be exactly 8 digits long";
                }
                if (!TextUtils.isDigitsOnly(content)){
                    return "Content only can contain digits";
                }
                return null;
            case ITF:
                if (content.length()%2 != 0){
                    return "Number of digits must be even";
                }
                if (!TextUtils.isDigitsOnly(content)){
                    return "Content only can contain digits";
                }
                return null;
            case UPC_A:
                if (content.length() != 11 || content.length()!= 12){
                    return "Content length must be exactly 11 or 12 digits long";
                }
                if (!TextUtils.isDigitsOnly(content)){
                    return "Content only can contain digits";
                }
                return null;
        }
        return null;
    }
    public static boolean isSupportOnlyNumeric(BarcodeFormat barcodeFormat){
        switch (barcodeFormat){
            case CODABAR:
            case EAN_8:
            case ITF:
            case UPC_A:
                return true;
            default:
                return false;
        }

    }
}
