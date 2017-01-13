package com.zemult.merchant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context context;
    protected List<T> mLists;
    protected LayoutInflater mInflater;
    int layoutId;


    /**
     * 通用的Adapter
     *
     * @param context  上下文
     * @param layoutId item  布局视图
     * @param mLists   数据集
     */
    public CommonAdapter(Context context, int layoutId, List<T> mLists) {
        this.context = context;
        this.mLists = mLists;
        this.layoutId = layoutId;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mLists == null ? 0 : mLists.size();
    }

    @Override
    public T getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.get(context, convertView, parent, layoutId);
        convert(holder, getItem(position), position);
        return holder.getmConvertView();
    }

    public void addItem(T item) {
        mLists.add(item);
    }

    public void NotifyDataSetChanged(List<T> mLists) {
        this.mLists = mLists;
        notifyDataSetChanged();
    }
    public abstract void convert(CommonViewHolder holder, T t, int position);

    public void setDataChanged(List<T> tList) {
        this.mLists = tList;
        this.notifyDataSetChanged();
    }
}
