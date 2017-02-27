package com.veer.multiselect.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;
import com.veer.multiselect.MultiSelectActivity;
import java.lang.ref.WeakReference;

/**
 * Created by Brajendr on 1/5/2017.
 */

public class LoadBitmap {

  public static void loadBitmap(String resId, ImageView imageView) {
    if (cancelPotentialWork(resId, imageView)) {
      final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
      final AsyncDrawable asyncDrawable = new AsyncDrawable(task);
      imageView.setImageDrawable(asyncDrawable);
      task.execute(resId);
    }
  }

  public static boolean cancelPotentialWork(String path, ImageView imageView) {
    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

    if (bitmapWorkerTask != null) {
      final String bitmapData = bitmapWorkerTask.path;
      // If bitmapData is not yet set or it differs from the new data
      if (bitmapData.isEmpty() || bitmapData != path) {
        // Cancel previous task
        bitmapWorkerTask.cancel(true);
      } else {
        // The same work is already in progress
        return false;
      }
    }
    // No task associated with the ImageView, or an existing task was cancelled
    return true;
  }

  static class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

    public AsyncDrawable(BitmapWorkerTask bitmapWorkerTask) {
      bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
    }

    public BitmapWorkerTask getBitmapWorkerTask() {
      return bitmapWorkerTaskReference.get();
    }
  }

  static class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private String path = " ";

    public BitmapWorkerTask(ImageView imageView) {
      // Use a WeakReference to ensure the ImageView can be garbage collected
      imageViewReference = new WeakReference<ImageView>(imageView);
    }

    // Decode image in background.
    @Override protected Bitmap doInBackground(String... params) {
      path = params[0];
      Bitmap bitmap = null;
      if (MultiSelectActivity.pathType == Constants.PATH_IMAGE) {
        try {
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inSampleSize = 3;
          options.inJustDecodeBounds = false;
          options.inPreferredConfig = Bitmap.Config.RGB_565;
          bitmap = BitmapFactory.decodeFile(path, options);
        }
        catch (OutOfMemoryError e)
        {
          if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
            System.gc();
          }
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inSampleSize = 3;
          options.inJustDecodeBounds = false;
          options.inPreferredConfig = Bitmap.Config.RGB_565;
          bitmap = BitmapFactory.decodeFile(path, options);
        }
      } else {
        bitmap =
            ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
      }
      return bitmap;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override protected void onPostExecute(Bitmap bitmap) {
      if (isCancelled()) {
        bitmap = null;
      }

      if (imageViewReference != null && bitmap != null) {
        final ImageView imageView = imageViewReference.get();
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (this == bitmapWorkerTask && imageView != null) {
          imageView.setImageBitmap(bitmap);
        }
      }
    }
  }

  private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
    if (imageView != null) {
      final Drawable drawable = imageView.getDrawable();
      if (drawable instanceof AsyncDrawable) {
        final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
        return asyncDrawable.getBitmapWorkerTask();
      }
    }
    return null;
  }
}
