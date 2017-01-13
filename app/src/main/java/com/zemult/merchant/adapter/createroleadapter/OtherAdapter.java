package com.zemult.merchant.adapter.createroleadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.zemult.merchant.R;
import com.zemult.merchant.bean.IndusPreferItem;

import java.util.List;

public class OtherAdapter extends BaseAdapter {
    private Context context;
    public List<IndusPreferItem> channelList;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    /**
     * 要删除的position
     */
    public int remove_position = -1;

    public OtherAdapter(Context context, List<IndusPreferItem> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @Override
    public int getCount() {
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public IndusPreferItem getItem(int position) {
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.indusprefer_item, null);
            holder.item_text = (TextView) convertView.findViewById(R.id.text_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        IndusPreferItem channel = getItem(position);
        if (channel.getName().length() >= 5) {
            holder.item_text.setTextSize(10);
        } else {
            holder.item_text.setTextSize(14);
        }
        holder.item_text.setText(channel.getName());
        if (!isVisible && (position == -1 + channelList.size())) {
            holder.item_text.setText("");
        }
        if (remove_position == position) {
            holder.item_text.setText("");
        }
        return convertView;
    }

    /**
     * 获取频道列表
     */
    public List<IndusPreferItem> getChannnelLst() {
        return channelList;
    }

    /**
     * 添加频道列表
     */
    public void addItem(IndusPreferItem channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
        // notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public void remove() {
        channelList.remove(remove_position);
        remove_position = -1;
        notifyDataSetChanged();
    }

    /**
     * 设置频道列表
     */
    public void setListDate(List<IndusPreferItem> list) {
        channelList = list;
        notifyDataSetChanged();
    }

    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    class ViewHolder {

        public TextView item_text;
    }


}