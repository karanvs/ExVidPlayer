package com.veer.multiselect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.veer.multiselect.Adapter.MyRVAdpater;
import com.veer.multiselect.Util.Constants;
import java.util.ArrayList;
import java.util.Arrays;

public class MultiSelectActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {
  //views
  private RecyclerView mRecyclerView;
  private ImageView ivBack;
  private TextView mTittle;
  private Button btnSelect;
  private RelativeLayout layout;
  //var
  public static int mLimit = Constants.DEFAULT_LIMIT;
  private ArrayList<String> paths = new ArrayList<>();
  public static int pathType = Constants.PATH_IMAGE;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_multi_select);
    getData();
    initViews();
    checkSelection(paths);
    returnPaths();
    ivBack.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        setFailedResult();
      }
    });
    if (isStoragePermissionGranted()) {
      initLoader();
    }
  }

  public boolean isStoragePermissionGranted() {
    if (Build.VERSION.SDK_INT >= 23) {
      if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
          == PackageManager.PERMISSION_GRANTED) {
        return true;
      } else {
        requestPermissions(new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }, 2909);
        return false;
      }
    } else { //permission is automatically granted on sdk<23 upon installation
      return true;
    }
  }

  private void setFailedResult() {
    setResult(0);
  }

  private void returnPaths() {
    btnSelect.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent();
        intent.putExtra(Constants.GET_PATHS, paths);
        setResult(RESULT_OK, intent);
        finish();
      }
    });
  }

  public void checkSelection(ArrayList<String> path) {
    this.paths = path;
    if (paths.size() > 0) {
      btnSelect.setVisibility(View.VISIBLE);
      mTittle.setText(paths.size() + " Selected");
    } else {
      mTittle.setText("Select");
      btnSelect.setVisibility(View.GONE);
    }
  }

  private void initLoader() {
    if (pathType == Constants.PATH_IMAGE) {
      getSupportLoaderManager().initLoader(Constants.LOADER_IMAGE, null, this);
    } else {
      getSupportLoaderManager().initLoader(Constants.LOADER_VIDEO, null, this);
    }
  }

  private void initViews() {
    mRecyclerView = (RecyclerView) findViewById(R.id.gridView);
    ivBack = (ImageView) findViewById(R.id.media_action_back);
    mTittle = (TextView) findViewById(R.id.media_action_text);
    btnSelect = (Button) findViewById(R.id.selectBtn);
    layout = (RelativeLayout) findViewById(R.id.mlayout_media_choose);
  }

  private void getData() {
    mLimit = getIntent().getIntExtra(Constants.LIMIT, Constants.DEFAULT_LIMIT);
    pathType = getIntent().getIntExtra(Constants.SELECT_TYPE, Constants.PATH_IMAGE);
  }

  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    if (pathType == Constants.PATH_VIDEO) {
      String[] projection = {
          MediaStore.Video.Media.DATA
      };
      CursorLoader cursorLoader =
          new CursorLoader(this, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null,
              null, null);

      return cursorLoader;
    } else {
      String[] projection = {
          MediaStore.Images.Media.DATA
      };
      CursorLoader cursorLoader =
          new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
              null, null);

      return cursorLoader;
    }
  }

  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    if (data != null) {
      StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
      mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
      mRecyclerView.setAdapter(new MyRVAdpater(MultiSelectActivity.this, data,getInitSelectionList(data)));
    }
  }

  private ArrayList<Integer> getInitSelectionList(Cursor data) {
    Integer[] integers = new Integer[data.getCount()];
    Arrays.fill(integers, View.GONE);
    return new ArrayList<>(Arrays.asList(integers));
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {

  }

  @Override public void onBackPressed() {
    setFailedResult();
    super.onBackPressed();
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case 2909: {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          initLoader();
        } else {
          //denied
          String message = "Permission needed to read the media content from device";
          Snackbar snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_SHORT)
              .setActionTextColor(getResources().getColor(R.color.white))
              .setAction("Provide Permission", new View.OnClickListener() {
                @Override public void onClick(View view) {
                  isStoragePermissionGranted();
                }
              });

          snackbar.show();
        }
        return;
      }
    }
  }
}
