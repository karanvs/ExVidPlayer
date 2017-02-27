package com.veer.multiselect.Util;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;

public abstract class CursorRVAdpater<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private Cursor myCursor;
    private boolean isValidData;
    private int mYRowIdCol;
    private Context mContext;
    private DataSetObserver mDataSetObserver;

    public CursorRVAdpater(Context context, Cursor cursor) {
        mContext = context;
        myCursor = cursor;
        isValidData = cursor != null;
        mYRowIdCol = isValidData ? myCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (myCursor != null) {
            myCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    public Cursor getCursor() {
        return myCursor;
    }

    @Override
    public int getItemCount() {
        if (isValidData && myCursor != null) {
            return myCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (isValidData && myCursor != null && myCursor.moveToPosition(position)) {
            return myCursor.getLong(mYRowIdCol);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor,int position);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!isValidData) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!myCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(viewHolder, myCursor,position);
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == myCursor) {
            return null;
        }
        final Cursor oldCursor = myCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        myCursor = newCursor;
        if (myCursor != null) {
            if (mDataSetObserver != null) {
                myCursor.registerDataSetObserver(mDataSetObserver);
            }
            mYRowIdCol = newCursor.getColumnIndexOrThrow("_id");
            isValidData = true;
            notifyDataSetChanged();
        } else {
            mYRowIdCol = -1;
            isValidData = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            isValidData = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            isValidData = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}