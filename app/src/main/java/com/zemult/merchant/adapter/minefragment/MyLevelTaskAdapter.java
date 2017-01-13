package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.model.M_Task;

import java.util.List;

/**
 * Created by admin on 2016/8/11.
 */
public class MyLevelTaskAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<M_Task> alldatas;
    private List<M_Task> topdatas;
    final int VIEW_TYPE_NUM = 3;



    public MyLevelTaskAdapter(Context context, List<M_Task> topdatas, List<M_Task> alldatas) {
        this.alldatas = alldatas;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.topdatas = topdatas;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_NUM;
    }


    @Override
    public int getItemViewType(int position) {
        int viewType = -1;
//        if (position == 0)
//            viewType = 0;
//        else if (position > 0 && position <= topdatas.size())
//            viewType = 1;
//        else if (position == topdatas.size() + 1)
//            viewType = 2;
//        else if (position > topdatas.size())
//            viewType = 3;

        M_Task bean = (M_Task)getItem(position);
        if(bean.getViewType()==1){      //头部的日常任务
            viewType = 0;
        }
        else if(bean.getViewType()==2){   //头部的成长任务
            viewType =2;
        }

        else if(bean.getViewType()==0){    //item里面的内容
            viewType =1;
        }
        return viewType;
    }

    @Override
    public int getCount() {
        return alldatas.size();
    }

    @Override
    public Object getItem(int position) {
        return alldatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        CommonViewHolder holder = null;
        if (viewType == 0) {   //头部的日常任务
            holder = CommonViewHolder.get(context, convertView, parent, R.layout.layout_item_daytask_title);
            holder.setText(R.id.daytask_title, "日常任务");
        } else if (viewType == 1) {    //item里面的内容
            holder = CommonViewHolder.get(context, convertView, parent, R.layout.item_mytask);

            if (alldatas.get(position).isComplete == 1) {
                holder.setImageResource(R.id.iscomple_iv, R.mipmap.yes_btn);
            } else {
                holder.setImageResource(R.id.iscomple_iv, R.mipmap.no_icon);
            }
            if (alldatas.get(position).name.equals("连续登陆")) {
                holder.setText(R.id.tasknum_tv, "已连续登陆" + alldatas.get(position).completeNum + "天");
            } else {
                holder.setText(R.id.tasknum_tv, alldatas.get(position).completeNum + "/" + alldatas.get(position).taskNum);
            }

            holder.setText(R.id.taskname_tv, alldatas.get(position).name);
            holder.setText(R.id.addexp_tv, "+ " + alldatas.get(position).experience + "经验值");


        } else if (viewType == 2) {    //头部的成长任务
            holder = CommonViewHolder.get(context, convertView, parent, R.layout.layout_item_growtask_title);
            holder.setText(R.id.growtask_title, "成长任务");


        }

        return holder.getmConvertView();


    }
}
