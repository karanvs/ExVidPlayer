package com.veer.multiselect.Util;

/**
 * Created by abhinav on 09/06/2016.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.view.Display;
import android.view.WindowManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BitmapHelper
{

    public static Bitmap decodeFile(Context context,File f)
    {
        Bitmap bitmap=null;
        Resources res = context.getResources();
        WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        @SuppressWarnings("deprecation")
        int width = display.getWidth();
        @SuppressWarnings("deprecation")
        int height = display.getHeight();
        try {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
                System.gc();
            }
            bitmap = Bitmap.createScaledBitmap(BitmapFactory
                            .decodeFile(f.getPath()),
                    width, height, true);
            bitmap=fixrotation(bitmap,f);
        } catch (OutOfMemoryError e) {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
                System.gc();
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Config.RGB_565;
            options.inSampleSize = 1;
            options.inPurgeable = true;
            bitmap.createScaledBitmap(BitmapFactory.decodeFile(f
                    .getPath().toString(), options), width, height,true);
            bitmap=fixrotation(bitmap,f);
        }
        return bitmap;
    }
    //decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File bitmapFile, int requiredWidth, int requiredHeight, boolean quickAndDirty)
    {
        try
        {
            //Decode image size
            BitmapFactory.Options bitmapSizeOptions = new BitmapFactory.Options();
            bitmapSizeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(bitmapFile), null, bitmapSizeOptions);

            // load image using inSampleSize adapted to required image size
            BitmapFactory.Options bitmapDecodeOptions = new BitmapFactory.Options();
            bitmapDecodeOptions.inTempStorage = new byte[16 * 1024];
            bitmapDecodeOptions.inSampleSize = computeInSampleSize(bitmapSizeOptions, requiredWidth, requiredHeight, false);
            bitmapDecodeOptions.inPurgeable = true;
            bitmapDecodeOptions.inDither = !quickAndDirty;
            bitmapDecodeOptions.inPreferredConfig = quickAndDirty ? Config.RGB_565 : Config.ARGB_8888;

            Bitmap decodedBitmap = BitmapFactory.decodeStream(new FileInputStream(bitmapFile), null, bitmapDecodeOptions);

            // scale bitmap to mathc required size (and keep aspect ratio)

            float srcWidth = (float) bitmapDecodeOptions.outWidth;
            float srcHeight = (float) bitmapDecodeOptions.outHeight;

            float dstWidth = (float) requiredWidth;
            float dstHeight = (float) requiredHeight;

            float srcAspectRatio = srcWidth / srcHeight;
            float dstAspectRatio = dstWidth / dstHeight;

            // recycleDecodedBitmap is used to know if we must recycle intermediary 'decodedBitmap'
            // (DO NOT recycle it right away: wait for end of bitmap manipulation process to avoid
            // java.lang.RuntimeException: Canvas: trying to use a recycled bitmap android.graphics.Bitmap@416ee7d8
            // I do not excatly understand why, but this way it's OK

            boolean recycleDecodedBitmap = false;

            Bitmap scaledBitmap = decodedBitmap;
            if (srcAspectRatio < dstAspectRatio)
            {
                scaledBitmap = getScaledBitmap(decodedBitmap, (int) dstWidth, (int) (srcHeight * (dstWidth / srcWidth)));
                // will recycle recycleDecodedBitmap
                recycleDecodedBitmap = true;
            }
            else if (srcAspectRatio > dstAspectRatio)
            {
                scaledBitmap = getScaledBitmap(decodedBitmap, (int) (srcWidth * (dstHeight / srcHeight)), (int) dstHeight);
                recycleDecodedBitmap = true;
            }

            // crop image to match required image size

            int scaledBitmapWidth = scaledBitmap.getWidth();
            int scaledBitmapHeight = scaledBitmap.getHeight();

            Bitmap croppedBitmap = scaledBitmap;

            if (scaledBitmapWidth > requiredWidth)
            {
                int xOffset = (scaledBitmapWidth - requiredWidth) / 2;
                croppedBitmap = Bitmap.createBitmap(scaledBitmap, xOffset, 0, requiredWidth, requiredHeight);
                scaledBitmap.recycle();
            }
            else if (scaledBitmapHeight > requiredHeight)
            {
                int yOffset = (scaledBitmapHeight - requiredHeight) / 2;
                croppedBitmap = Bitmap.createBitmap(scaledBitmap, 0, yOffset, requiredWidth, requiredHeight);
                scaledBitmap.recycle();
            }

            if (recycleDecodedBitmap)
            {
                decodedBitmap.recycle();
            }
            decodedBitmap = null;

            scaledBitmap = null;
            return croppedBitmap;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * compute powerOf2 or exact scale to be used as {@link BitmapFactory.Options#inSampleSize} value (for subSampling)
     *
     * @param //requiredWidth
     * @param //requiredHeight
     * @param powerOf2
     *            weither we want a power of 2 sclae or not
     * @return
     */
    public static int computeInSampleSize(BitmapFactory.Options options, int dstWidth, int dstHeight, boolean powerOf2)
    {
        int inSampleSize = 1;

        // Raw height and width of image
        final int srcHeight = options.outHeight;
        final int srcWidth = options.outWidth;

        if (powerOf2)
        {
            //Find the correct scale value. It should be the power of 2.

            int tmpWidth = srcWidth, tmpHeight = srcHeight;
            while (true)
            {
                if (tmpWidth / 2 < dstWidth || tmpHeight / 2 < dstHeight)
                    break;
                tmpWidth /= 2;
                tmpHeight /= 2;
                inSampleSize *= 2;
            }
        }
        else
        {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) srcHeight / (float) dstHeight);
            final int widthRatio = Math.round((float) srcWidth / (float) dstWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap drawableToBitmap(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int newWidth, int newHeight)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }


    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
      private static Bitmap fixrotation(Bitmap bitmap,File file)
      {
          Bitmap rotatedBitmap;

          try {
              ExifInterface exif = new ExifInterface(file.getPath());
              int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
              int rotationInDegrees = exifToDegrees(rotation);
              Matrix matrix = new Matrix();
              if (rotation != 0f) {

                  matrix.postRotate(rotationInDegrees);}
              rotatedBitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

              return rotatedBitmap;


          }catch(IOException ex){
              return bitmap;
          }
      }




}