package com.ybsystem.tweethub.adapters.recycler;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class RecyclerAdapterBase<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final int TYPE_FOOTER = 0;
    protected static final int TYPE_ITEM = 1;

    protected View mFooterView;
    protected ArrayList<T> mObjList;

    public RecyclerAdapterBase() {
        this.mObjList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return getObjCount() + 1;
    }

    public int getObjCount() {
        return mObjList.size();
    }

    public T getObj(int position) {
        return mObjList.get(position);
    }

    public T getLastObj() {
        return mObjList.get(getObjCount() - 1);
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void add(T item) {
        mObjList.add(item);
    }

    public void insert(int position, T item) {
        mObjList.add(position, item);
    }

    public void clear() {
        mObjList.clear();
        notifyDataSetChanged();
    }

    public void remove(T item) {
        int position = mObjList.indexOf(item);
        if (position != -1) {
            mObjList.remove(position);
        }
    }

    public boolean isEmpty() {
        return getObjCount() == 0;
    }

}
