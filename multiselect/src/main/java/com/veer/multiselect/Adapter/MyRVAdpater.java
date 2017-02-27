package com.veer.multiselect.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.veer.multiselect.MultiSelectActivity;
import com.veer.multiselect.R;
import com.veer.multiselect.Util.Constants;
import com.veer.multiselect.Util.CursorRVAdpater;
import com.veer.multiselect.Util.GetPaths;
import com.veer.multiselect.Util.LoadBitmap;
import java.util.ArrayList;

/**
 * Created by Brajendr on 1/5/2017.
 */

public class MyRVAdpater extends CursorRVAdpater<MyRVAdpater.ViewHolder> {
  private Context context;
  private ArrayList<String> paths;
  private ArrayList<Integer> visiblity;

  public MyRVAdpater(Context context, Cursor cursor,ArrayList<Integer> visiblity) {
    super(context, cursor);
    this.context = context;
    this.visiblity=visiblity;
    paths=new ArrayList<>();
  }

  @Override public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor, final int position) {
    final String path = GetPaths.getPathFromCursor(cursor, MultiSelectActivity.pathType);
    LoadBitmap.loadBitmap(path, viewHolder.ivThumb);
    if(visiblity.get(position)==View.GONE)
    {
      viewHolder.rlSelected.setVisibility(View.GONE);
    }
    else
    {
      viewHolder.rlSelected.setVisibility(View.VISIBLE);
    }
    viewHolder.ivThumb.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (visiblity.get(position) == View.GONE) {
          if (paths.size() < MultiSelectActivity.mLimit) {
            viewHolder.rlSelected.setVisibility(View.VISIBLE);
            paths.add(path);
            visiblity.add(position,View.VISIBLE);
            if (context instanceof MultiSelectActivity) {
              ((MultiSelectActivity) context).checkSelection(paths);
            }
          } else {
            Toast.makeText(context, Constants.LIMIT_MESSAGE + paths.size() + " items",
                Toast.LENGTH_SHORT).show();
          }
        } else {
          viewHolder.rlSelected.setVisibility(View.GONE);
          paths.remove(path);
          visiblity.add(position,View.GONE);
          if (context instanceof MultiSelectActivity) {
            ((MultiSelectActivity) context).checkSelection(paths);
          }
        }
      }
    });
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.galleryitem, parent, false);

    return new ViewHolder(itemView);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivThumb;
    RelativeLayout rlSelected;

    public ViewHolder(View itemView) {
      super(itemView);
      ivThumb = (ImageView) itemView.findViewById(R.id.thumbImage);
      rlSelected = (RelativeLayout) itemView.findViewById(R.id.layout_image_Sel);
    }
  }
}
