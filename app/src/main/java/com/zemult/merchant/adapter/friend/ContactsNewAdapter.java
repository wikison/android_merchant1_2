/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zemult.merchant.adapter.friend;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.util.ImageManager;

import java.util.ArrayList;
import java.util.List;

public class ContactsNewAdapter extends ArrayAdapter<M_Fan> implements
        SectionIndexer {

    private LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    List<M_Fan> friendlist;
    Context mcontext;
    ImageManager imageManager;


    public ContactsNewAdapter(Context context, int resource, List<M_Fan> objects) {
        super(context, resource, objects);
        layoutInflater = LayoutInflater.from(context);
        friendlist = objects;
        mcontext = context;
        imageManager = new ImageManager(mcontext);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_familiar, null);
            holder = new Holder();
            // 头像
            holder.iv_follow_head = (ImageView) convertView.findViewById(R.id.iv_follow_head);
            holder.iv_sex = (ImageView) convertView.findViewById(R.id.iv_sex);
            holder.iv_status = (ImageView) convertView.findViewById(R.id.iv_status);
            holder.tv_describe = (TextView) convertView.findViewById(R.id.tv_describe);
            holder.tv_follow_name = (TextView) convertView.findViewById(R.id.tv_follow_name);
            holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            holder.tv_rname = (TextView) convertView.findViewById(R.id.tv_rname);
            holder.headline = (View) convertView.findViewById(R.id.headline);
            holder.tvHeader = (TextView) convertView.findViewById(R.id.header);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        final M_Fan friend = friendlist.get(position);
        String nickName = friend.getName();
        String header = friend.getHeader();

        if (position == 0 || header != null
                && !header.equals(getItem(position - 1).getHeader())) {

            if ("".equals(header)) {
                holder.tvHeader.setVisibility(View.GONE);
                holder.headline.setVisibility(View.VISIBLE);
            } else {
                holder.headline.setVisibility(View.GONE);
                holder.tvHeader.setVisibility(View.VISIBLE);
                holder.tvHeader.setText(header);
            }
        } else {
            holder.tvHeader.setVisibility(View.GONE);
        }

        imageManager.loadCircleHead(friend.head,
                holder.iv_follow_head);
        holder.tv_follow_name.setText(friend.name);
        holder.iv_sex.setImageResource(friend.getExperienceImg());
        holder.tv_describe.setText(friend.getExperienceText() + "管家");
        holder.iv_status.setImageResource(friend.getStatusImg(friend.state));
        holder.tv_status.setText(friend.getStatusText(friend.state));
        holder.tv_status.setTextColor(friend.getStatusTextColor(friend.state));
        holder.tv_status.setTextSize(12);
        if (!TextUtils.isEmpty(friend.note)) {
            holder.tv_rname.setVisibility(View.VISIBLE);
            holder.tv_rname.setText("备注名:" + friend.note);
        } else
            holder.tv_rname.setVisibility(View.GONE);
        return convertView;
    }


    private class Holder {
        ImageView iv_follow_head;
        ImageView iv_sex;
        ImageView iv_status;
        TextView tv_describe;
        TextView tv_follow_name;
        TextView tv_status;
        TextView tv_rname;
        TextView tvHeader;
        View headline;


    }


    @Override
    public M_Fan getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {

        return super.getCount();
    }

    public void setData(List<M_Fan> data, int count) {
        friendlist = data;
        notifyDataSetChanged();
    }

    @Override
    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        List<String> list = new ArrayList<String>();
//		list.add(getContext().getString(R.string.search_header));
//		positionOfSection.put(0, 0);
//		sectionOfPosition.put(0, 0);
        for (int i = 0; i < count; i++) {
            String letter = getItem(i).getHeader();
            if (list.isEmpty()) {
                list.add(letter);
                positionOfSection.put(0, i);
            } else {
                int section = list.size() - 1;
                if (list.get(section) != null && !list.get(section).equals(letter)) {
                    list.add(letter);
                    section++;
                    positionOfSection.put(section, i);
                }
                sectionOfPosition.put(i, section);
            }
        }
        list.add("#");
        positionOfSection.put(count, 0);
        sectionOfPosition.put(count, 0);
        return list.toArray(new String[list.size()]);
    }

}
