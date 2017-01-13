/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zemult.merchant.adapter.friend;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.util.ImageManager;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends ArrayAdapter<M_Friend> implements
		SectionIndexer {

	private LayoutInflater layoutInflater;
	private SparseIntArray positionOfSection;
	private SparseIntArray sectionOfPosition;
	List<M_Friend> friendlist;
	Context mcontext;
	ImageManager imageManager;

	public void removeOne(M_Friend friend){
		friendlist.remove(friend);
		notifyDataSetChanged();
	}

	public ContactsAdapter(Context context, int resource, List<M_Friend> objects) {
		super(context, resource, objects);
		layoutInflater = LayoutInflater.from(context);
		friendlist=objects;
		mcontext=context;
		imageManager =	new ImageManager(mcontext);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			convertView= layoutInflater.inflate(R.layout.item_contacts, null);
			holder = new Holder();
		// 头像
		holder.avatar = (ImageView) convertView.findViewById(R.id.iv_friend_head);
		holder.headline = (ImageView) convertView.findViewById(R.id.headline);
		holder.nameTextview = (TextView) convertView.findViewById(R.id.tv_my_name);
		holder.tvHeader = (TextView) convertView.findViewById(R.id.header);
		holder.cbselect = (CheckBox) convertView.findViewById(R.id.cb_select);
		convertView.setTag(holder);
		}else {
			holder = (Holder) convertView.getTag();
		}
		final M_Friend friend =friendlist.get(position);
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

		holder.cbselect.setChecked(friend.isselected());
		imageManager.loadCircleImage(friend.getHead(),
				holder.avatar);
		holder.nameTextview.setText(nickName);
		holder.cbselect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				friend.setIsselected(isChecked);
				if(isChecked){
					onItemAddClick.onItemAddClick(friend);
				}
				else {
					onItemDelClick.onItemDelClick(friend);
				}
			}
		});
		return convertView;
	}


	private class Holder {
		ImageView avatar;
		TextView nameTextview;
		TextView tvHeader;
		ImageView headline;
		CheckBox cbselect;

	}

	DelFriendClickListener onItemDelClick;
	AddFriendClickListener onItemAddClick;
	public  interface DelFriendClickListener {
		public void onItemDelClick(M_Friend friend);
	}
	public  interface AddFriendClickListener {
		public void onItemAddClick(M_Friend friend);
	}

	public void setDelFriendClickListener(DelFriendClickListener onItemDelClick) {
		this.onItemDelClick = onItemDelClick;
	}
	public void setAddFriendClickListener(AddFriendClickListener onItemAddClick) {
		this.onItemAddClick = onItemAddClick;
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
		friendlist=data;
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

}
