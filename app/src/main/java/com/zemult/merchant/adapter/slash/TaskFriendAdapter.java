/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
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
package com.zemult.merchant.adapter.slash;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.view.swipelistview.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.trinea.android.common.util.StringUtils;

public class TaskFriendAdapter extends ArrayAdapter<M_Friend> implements
        SectionIndexer {

    public Map<Integer, String> selected;
    List<M_Friend> friendlist;
    Context mcontext;
    ImageManager imageManager;
    private LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private List<M_Friend> selectedFriends;

    public TaskFriendAdapter(Context context, int resource, List<M_Friend> objects) {
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

            View view = layoutInflater.inflate(R.layout.item_task_friend, null);
            View menuView = layoutInflater.inflate(
                    R.layout.item_swipe_operation, null);
            menuView.findViewById(R.id.share_btn).setVisibility(View.GONE);
            menuView.findViewById(R.id.remove_btn).setVisibility(View.GONE);
            convertView = new SwipeItemLayout(view, menuView, null, null);
            holder = new Holder();
            holder.tvHeader = (TextView) convertView.findViewById(R.id.header);
            holder.headline = (ImageView) convertView.findViewById(R.id.headline);
            holder.avatar = (ImageView) convertView.findViewById(R.id.iv_friend_head);
            holder.name = (TextView) convertView.findViewById(R.id.tv_friend_name);
            holder.tv_friend_roles = (TextView) convertView.findViewById(R.id.tv_friend_roles);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_friend);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final M_Friend friend = friendlist.get(position);
        String nickName = friend.getName();
        String header = friend.getHeader();
        if (position == 0 || header != null
                && !header.equals(getItem(position - 1).getHeader())) {
            if ("".equals(header)) {
                holder.tvHeader.setVisibility(View.GONE);
                holder.headline.setVisibility(View.VISIBLE);
            } else {
                holder.headline.setVisibility(View.VISIBLE);
                holder.tvHeader.setVisibility(View.VISIBLE);
                holder.tvHeader.setText(header);
            }
        } else {

            holder.tvHeader.setVisibility(View.GONE);
        }

        //性别(0男,1女)
//        if (friend.getSex() == 0) {
//            holder.name.setTextColor(Color.parseColor("#00BFFF"));
//        } else {
//            holder.name.setTextColor(Color.parseColor("#e40381"));
//        }

        imageManager.loadCircleImage(friend.getHead(),
                holder.avatar);
        holder.name.setText(nickName);

        if (StringUtils.isBlank(friend.userLevel + "")) {
            holder.tv_friend_roles.setVisibility(View.GONE);
        } else {
            holder.tv_friend_roles.setVisibility(View.VISIBLE);
            holder.tv_friend_roles.setText(friend.userLevel + "");
        }

        holder.checkBox.setChecked(friend.isselected());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friend.isselected()) {
                    friend.setIsselected(false);
                } else {
                    friend.setIsselected(true);
                }
            }
        });
        return convertView;
    }

    @Override
    public M_Friend getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {

        return super.getCount();
    }

    public void setData(List<M_Friend> data) {
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
        list.add(getContext().getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {

            String letter = getItem(i).getHeader();
            System.err.println("FeiendAdapter getsection getHeader:" + letter
                    + " name:" + getItem(i).getName());
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

    private class Holder {
        TextView tvHeader;
        ImageView headline;
        ImageView avatar;
        TextView name;
        TextView tv_friend_roles;
        CheckBox checkBox;

    }

}
