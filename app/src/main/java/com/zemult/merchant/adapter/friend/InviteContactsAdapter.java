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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ImageManager;

import java.util.ArrayList;
import java.util.List;

public class InviteContactsAdapter extends ArrayAdapter<M_Friend> implements
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

	public InviteContactsAdapter(Context context, int resource, List<M_Friend> objects) {
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

			 convertView = layoutInflater.inflate(R.layout.item_invitecontacts, null);
			holder = new Holder();
		// 头像
		holder.headline = (ImageView) convertView.findViewById(R.id.headline);
		holder.nameTextview = (TextView) convertView.findViewById(R.id.tv_my_name);
		holder.tvHeader = (TextView) convertView.findViewById(R.id.header);
		holder.btn_invite = (Button) convertView.findViewById(R.id.btn_invite);
		holder.btn_againinvite = (Button) convertView.findViewById(R.id.btn_againinvite);
		holder.tv_my_phone = (TextView) convertView.findViewById(R.id.tv_my_phone);

		convertView.setTag(holder);
		}else {
			holder = (Holder) convertView.getTag();
		}
//		M_Friend friend = getItem(position);
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
			LinearLayout.LayoutParams  lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
			lp.topMargin= AppUtils.dip2px(mcontext,20);
		} else {
			LinearLayout.LayoutParams  lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
			lp.topMargin=0;
			holder.tvHeader.setVisibility(View.GONE);
		}

		holder.nameTextview.setText(nickName);
		holder.tv_my_phone.setText(friend.getPhone());

		if(friend.getState()==1){
			holder.btn_invite.setVisibility(View.GONE);
			holder.btn_againinvite.setVisibility(View.VISIBLE);
		}
		else{
			holder.btn_invite.setVisibility(View.VISIBLE);
			holder.btn_againinvite.setVisibility(View.GONE);
		}

		holder.btn_invite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemInviteClick.onItemInviteClick(friend);
			}
		});

		holder.btn_againinvite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemInviteAgainClick.onItemInviteAgainClick(friend);
			}
		});

		return convertView;
	}


	private class Holder {
		TextView nameTextview;
		TextView tvHeader;
		ImageView headline;
        Button btn_invite;
		Button btn_againinvite;
		TextView tv_my_phone;
	}

	InviteClickListener onItemInviteClick;

	public  interface InviteClickListener {
		public void onItemInviteClick(M_Friend friend); //传递boolean类型数据给activity
	}

	public void setInviteClickListener(InviteClickListener onItemInviteClick) {
		this.onItemInviteClick = onItemInviteClick;
	}


	InviteAgainClickListener onItemInviteAgainClick;

	public  interface InviteAgainClickListener {
		public void onItemInviteAgainClick(M_Friend friend); //传递boolean类型数据给activity
	}

	public void setInviteAgainClickListener(InviteAgainClickListener onItemInviteAgainClick) {
		this.onItemInviteAgainClick = onItemInviteAgainClick;
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
