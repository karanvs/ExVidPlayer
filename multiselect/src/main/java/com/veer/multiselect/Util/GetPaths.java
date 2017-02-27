package com.veer.multiselect.Util;

import android.database.Cursor;
import android.provider.MediaStore;

/**
 * Created by Brajendr on 1/5/2017.
 */

public class GetPaths {
  public static String getPathFromCursor(Cursor data,int path) {
    if(path==Constants.PATH_IMAGE)
    {
      int columnIndex = data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      return data.getString(columnIndex);
    }
    else
    {
      int columnIndex = data.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
      return data.getString(columnIndex);
    }
  }

}
