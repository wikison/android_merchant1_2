package com.zemult.merchant.app.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class TBaseAdapter<T> extends BaseAdapter {

	public Context mContext;
	public List<T> items;

	public TBaseAdapter(Context mContext, List<T> beans) {
		this.mContext = mContext;
		if (beans == null) {
			beans = new ArrayList<T>();
		}
		this.items = beans;
	}

	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public T getItem(int arg0) {
		return items.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View view, ViewGroup arg2);


}
