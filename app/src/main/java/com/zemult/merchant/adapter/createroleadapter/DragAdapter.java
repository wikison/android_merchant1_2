package com.zemult.merchant.adapter.createroleadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.zemult.merchant.R;
import com.zemult.merchant.bean.IndusPreferItem;

import java.util.List;

public class DragAdapter extends BaseAdapter {
    /**
     * TAG
     */
    private final static String TAG = "DragAdapter";
    /**
     * 是否显示底部的ITEM
     */
    private boolean isItemShow = false;
    private Context context;
    /**
     * 控制的postion
     */
    private int holdPosition;
    /**
     * 是否改变
     */
    private boolean isChanged = false;
    /**
     * 列表数据是否改变
     */
    private boolean isListChanged = false;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    /**
     * 可以拖动的列表（即用户选择的频道列表）
     */
    public List<IndusPreferItem> channelList;
    /**
     * TextView 频道内容
     */
    private TextView item_text;

    ImageView item_iv;
    /**
     * 要删除的position
     */
    public int remove_position = -1;


    Boolean b = false;

    public DragAdapter(Context context, List<IndusPreferItem> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    public void setB(Boolean b) {
        this.b = b;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public IndusPreferItem getItem(int position) {
        // TODO Auto-generated method stub
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View contenter, ViewGroup parent) {
//        ViewHolder holder = null;
//
//        if (contenter == null) {
//            holder = new ViewHolder();
//            contenter = LayoutInflater.from(context).inflate(R.layout.channel_item, null);
//            item_text = (TextView) contenter.findViewById(R.id.text_item);
//           // holder.item_text = (TextView) contenter.findViewById(R.id.text_item);
//            holder.item_iv = (ImageView) contenter.findViewById(R.id.delete_iv);
//            contenter.setTag(holder);
//        } else {
//            holder = (ViewHolder) contenter.getTag();
//        }
//
//        holder.item_iv.setVisibility(b ? View.VISIBLE : View.GONE);
//
//        ChannelItem channel = getItem(position);
//       item_text.setText(channel.getName());
//        if ((position == 0) || (position == 1)) {
////			item_text.setTextColor(context.getResources().getColor(R.color.black));
//            item_text.setEnabled(false);
//        }
//        if (isChanged && (position == holdPosition) && !isItemShow) {
//            item_text.setText("");
//            item_text.setSelected(true);
//            item_text.setEnabled(true);
//            isChanged = false;
//        }
//        if (!isVisible && (position == -1 + channelList.size())) {
//            item_text.setText("");
//            item_text.setSelected(true);
//            item_text.setEnabled(true);
//        }
//        if (remove_position == position) {
//            item_text.setText("");
//        }
//        return contenter;

        View view = LayoutInflater.from(context).inflate(R.layout.indusprefer_item, null);
        item_text = (TextView) view.findViewById(R.id.text_item);
        item_iv = (ImageView) view.findViewById(R.id.delete_iv);
        item_iv.setVisibility(b ? View.VISIBLE : View.GONE);
        IndusPreferItem channel = getItem(position);
        if (channel.getName().length() >= 5) {
            item_text.setTextSize(10);
        } else {
            item_text.setTextSize(14);
        }


        item_text.setText(channel.getName());
        if ((position == 0)) {// || (position == 1)
//			item_text.setTextColor(context.getResources().getColor(R.color.black));
            item_text.setEnabled(false);

        }
        if (isChanged && (position == holdPosition) && !isItemShow) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
            isChanged = false;
        }
        if (!isVisible && (position == -1 + channelList.size())) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
        }
        if (remove_position == position) {
            item_text.setText("");
        }
        return view;


    }

    /**
     * 添加频道列表
     */
    public void addItem(IndusPreferItem channel) {
        channelList.add(channel);
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 拖动变更频道排序
     */
    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        IndusPreferItem dragItem = getItem(dragPostion);
        Log.d(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
        if (dragPostion < dropPostion) {
            channelList.add(dropPostion + 1, dragItem);
            channelList.remove(dragPostion);
        } else {
            channelList.add(dropPostion, dragItem);
            channelList.remove(dragPostion + 1);
        }
        isChanged = true;
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 获取频道列表
     */
    public List<IndusPreferItem> getChannnelLst() {
        return channelList;
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public void remove() {
        try{
            channelList.remove(remove_position);
            remove_position = -1;
            isListChanged = true;
            notifyDataSetChanged();
        }catch (Exception e){

        }

    }

    /**
     * 设置频道列表
     */
    public void setListDate(List<IndusPreferItem> list) {
        channelList = list;
    }

    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 排序是否发生改变
     */
    public boolean isListChanged() {
        return isListChanged;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * 显示放下的ITEM
     */
    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }


//    class ViewHolder {
//
//        //public TextView item_text;
//        public ImageView item_iv;
//    }


}