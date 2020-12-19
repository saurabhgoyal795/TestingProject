package com.atlaaya.evdrecharge.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class Utility {

    private static final String TAG = Utility.class.getSimpleName();

    public static boolean isValidPhoneNumber(String country_code, String phone) {
        return phone.length() >= 9 && phone.length() <= 9 && Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        long number = rnd.nextLong();

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public static File saveBitMap(Context context, View drawView, String fileName) {

        String DIRECTORY_NAME = "EVD Highlight";

//        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                "VoucherReceipt");
//        File pictureFileDir = new File(context.getCacheDir().getPath());
        File pictureFileDir = new File(context.getExternalFilesDir(null), DIRECTORY_NAME);
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.i("TAG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator + fileName + ".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            System.gc();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
            System.gc();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(context, pictureFile.getAbsolutePath());
        return pictureFile;
    }

    //create bitmap from view and returns it
    private static Bitmap getBitmapFromView(View view) {

        System.gc();

        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap;
        if (view instanceof ScrollView) {
            returnedBitmap = Bitmap.createBitmap(((ViewGroup) view).getChildAt(0).getWidth(), ((ViewGroup) view).getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);
        } else if (view instanceof RecyclerView) {
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

//            returnedBitmap = Bitmap.createBitmap(400, view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        } else {
            returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//            view.setDrawingCacheEnabled(true);
//            returnedBitmap = Bitmap.createBitmap(view.getDrawingCache());
//            view.setDrawingCacheEnabled(false);
        }
        System.gc();
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);

        System.gc();
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        System.gc();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }

        System.gc();
        // draw the view on the canvas
        view.draw(canvas);
        System.gc();
        //return the bitmap
        return returnedBitmap;
    }

    // used for scanning gallery
    private static void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, (path1, uri) -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public static String getDeviceDensityString(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        Log.e("Utility","getDeviceDensityString density : "+density);
        Log.e("Utility","getDeviceDensityString densityDpi: "+densityDpi);

        switch (densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_TV:
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_260:
            case DisplayMetrics.DENSITY_280:
            case DisplayMetrics.DENSITY_300:
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_340:
            case DisplayMetrics.DENSITY_360:
            case DisplayMetrics.DENSITY_400:
            case DisplayMetrics.DENSITY_420:
            case DisplayMetrics.DENSITY_440:
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_560:
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";
            default:
                return null;
        }
    }

}
