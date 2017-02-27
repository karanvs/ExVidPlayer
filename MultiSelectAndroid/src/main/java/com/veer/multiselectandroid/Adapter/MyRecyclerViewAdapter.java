package com.veer.multiselectandroid.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.veer.multiselectandroid.MultiSelectActivity;
import com.veer.multiselectandroid.R;
import com.veer.multiselectandroid.Util.Constants;
import com.veer.multiselectandroid.Util.CursorRecyclerViewAdapter;
import com.veer.multiselectandroid.Util.GetPaths;
import com.veer.multiselectandroid.Util.LoadBitmap;
import java.util.ArrayList;

/**
 * Created by Brajendr on 1/5/2017.
 */

public class MyRecyclerViewAdapter extends CursorRecyclerViewAdapter<MyRecyclerViewAdapter.ViewHolder> {
  private Context context;
  private ArrayList<String> paths;

  public MyRecyclerViewAdapter(Context context, Cursor cursor) {
    super(context, cursor);
    this.context = context;
    paths = new ArrayList<>();
  }

  @Override public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
    final String path = GetPaths.getPathFromCursor(cursor, MultiSelectActivity.pathType);
    LoadBitmap.loadBitmap(path, viewHolder.ivThumb);

    viewHolder.ivThumb.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (viewHolder.rlSelected.getVisibility() == View.GONE) {
          if (paths.size() < MultiSelectActivity.mLimit) {
            viewHolder.rlSelected.setVisibility(View.VISIBLE);
            paths.add(path);
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
