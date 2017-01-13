package com.zemult.merchant.adapter.slashfrgment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;


import com.android.volley.Request;
import com.zemult.merchant.util.ImageManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zema.volley.network.VolleyUtil;

public abstract class BaseListAdapter<E> extends BaseAdapter {
    protected ArrayList<WeakReference<Request>> listJsonRequest;
    private List<E> mList = new ArrayList<E>();
    protected Context mContext;
    protected Activity mActivity;
    protected LayoutInflater mInflater;
    protected ImageManager mImageManager;

    public BaseListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mImageManager = new ImageManager(context);
    }

    public BaseListAdapter(Context context, List<E> list) {
        this(context);
        mList = list;
        mInflater = LayoutInflater.from(context);
        mImageManager = new ImageManager(context);
    }

    public BaseListAdapter(Context context, List<E> list, Activity activity) {
        this(context);
        mActivity = activity;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mImageManager = new ImageManager(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public void clearAll() {
        mList.clear();
    }

    public List<E> getData() {
        return mList;
    }

    public void addALL(List<E> lists){
        if(lists==null||lists.size()==0){
            return ;
        }
        mList.addAll(lists);
    }
    public void add(E item){
        mList.add(item);
    }
    public void add(E item, int pos){
        mList.add(pos, item);
    }

    @Override
    public E getItem(int position) {
        return (E) mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeEntity(E e){
        mList.remove(e);
    }



    /**
     * 发送请求
     *
     * @param request
     */
    public void sendJsonRequest(Request request) {

        if (listJsonRequest == null) {
            listJsonRequest = new ArrayList<WeakReference<Request>>();
        }
        WeakReference<Request> ref = new WeakReference<Request>(request);
        listJsonRequest.add(ref);
        VolleyUtil.getRequestQueue().add(request) ;
    }

}
