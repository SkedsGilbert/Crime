package com.south42studios.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Jsin on 10/11/2015.
 */
public class PictureUtils {
    private static final String TAG = "com.south42studios";

    public static Bitmap getScaledBitmap(String path, Activity activity) throws IOException {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        Bitmap scaledBitmap = getScaledBitmap(path, size.x, size.y);

        ExifInterface exifInterface = new ExifInterface(path);
        String orientationString = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientationTag = orientationString != null ? Integer.parseInt(orientationString) :
                ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle;
        switch (orientationTag) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotationAngle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotationAngle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotationAngle = 270;
                break;
            default:
                rotationAngle = 0;

        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                scaledBitmap.getHeight(), matrix, true);
        return rotatedBitmap;

    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        //Reading the size of the image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Figure out scale down size
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth < srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }
}
