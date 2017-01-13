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
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.taobao.av.util.StringUtil;
import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.view.swipelistview.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends ArrayAdapter<M_Friend> implements
		SectionIndexer {

	List<M_Friend> friendlist;
	Context mcontext;
	ImageManager imageManager;
	DelFriendClickListener onItemAddClick;
	private LayoutInflater layoutInflater;
	private SparseIntArray positionOfSection;
	private SparseIntArray sectionOfPosition;

	public FriendAdapter(Context context, int resource, List<M_Friend> objects) {
		super(context, resource, objects);
		layoutInflater = LayoutInflater.from(context);
		friendlist=objects;
		mcontext=context;
		imageManager =	new ImageManager(mcontext);
	}

	public void removeOne(M_Friend friend) {
		friendlist.remove(friend);
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {

			View view = layoutInflater.inflate(R.layout.item_friend, null);
			View menuView =layoutInflater.inflate(
					R.layout.item_swipe_operation, null);
			menuView.findViewById(R.id.share_btn).setVisibility(View.GONE);
			menuView.findViewById(R.id.remove_btn).setVisibility(View.GONE);
			convertView = new SwipeItemLayout(view, menuView, null, null);
			holder = new Holder();
		// 头像
		holder.avatar = (ImageView) convertView.findViewById(R.id.iv_friend_head);
		holder.headline = (ImageView) convertView.findViewById(R.id.headline);
		holder.nameTextview = (TextView) convertView.findViewById(R.id.tv_my_name);
		holder.tv_friend_work = (TextView) convertView.findViewById(R.id.tv_friend_work);
		holder.tv_friend_roles = (TextView) convertView.findViewById(R.id.tv_friend_roles);
		holder.iv_friend_sex = (ImageView) convertView.findViewById(R.id.iv_friend_sex);
		holder.remove_btn = (TextView) convertView.findViewById(R.id.remove_btn);
		holder.tvHeader = (TextView) convertView.findViewById(R.id.header);
		holder.ll_friend_work = (LinearLayout) convertView.findViewById(R.id.ll_friend_work);
		holder.tv_friend_workcount = (TextView) convertView.findViewById(R.id.tv_friend_workcount);
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
			holder.remove_btn.setLayoutParams(lp);
		} else {
			LinearLayout.LayoutParams  lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
			lp.topMargin=0;
			holder.remove_btn.setLayoutParams(lp);
			holder.tvHeader.setVisibility(View.GONE);
		}

//		if(friend.getSex()==0){//性别(0男,1女)
////			holder.iv_friend_sex.setBackgroundResource(R.mipmap.man_icon);
//			holder.nameTextview.setTextColor(Color.parseColor("#00BFFF"));
//		}
//		else{
////			holder.iv_friend_sex.setBackgroundResource(R.mipmap.woman);
//			holder.nameTextview.setTextColor(Color.parseColor("#e40381"));
//		}



		imageManager.loadCircleImage(friend.getHead(),
				holder.avatar);
		holder.nameTextview.setText(nickName);
		holder.remove_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemAddClick.onItemClick(friend);
			}
		});
//		holder.tv_friend_work.setText(friend.getCompany()+" "+friend.getPosition());
        String industryName="";
		for (M_UserRole userRole : friend.userIndustryList) {
			if (!TextUtils.isEmpty(userRole.industryName)
					&& !TextUtils.isEmpty(userRole.industryLevel))
				industryName += "Lv" + userRole.industryLevel + " " + userRole.industryName + "/";
		}
		if (!StringUtil.isEmpty(industryName)&&(industryName.lastIndexOf("/") == industryName.length() - 1))
			industryName = industryName.substring(0, industryName.length() - 1);
		if(friend.userIndustryNum==0){
			holder.ll_friend_work.setVisibility(View.GONE);
		}
		else{
			holder.ll_friend_work.setVisibility(View.VISIBLE);
			if(industryName.length()>18){
				industryName = industryName.substring(0, 18);
			}
				holder.tv_friend_work.setText(industryName);
			holder.tv_friend_workcount.setText("..."+friend.userIndustryNum + "重角色");
		}



		if(StringUtil.isEmpty(friend.userLevel+"")){
			holder.tv_friend_roles.setVisibility(View.GONE);
		}
		else {
			holder.tv_friend_roles.setVisibility(View.VISIBLE);
			holder.tv_friend_roles.setText(friend.userLevel + "");
		}
		return convertView;
	}

	public void setDelFriendClickListener(DelFriendClickListener onItemAddClick) {
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

	public interface DelFriendClickListener {
		public void onItemClick(M_Friend friend); //传递boolean类型数据给activity
	}

	private class Holder {
		ImageView avatar;
		TextView nameTextview;
		TextView tv_friend_work;
		TextView tv_friend_roles;
		ImageView iv_friend_sex;
		TextView remove_btn;
		TextView tvHeader;
		TextView tv_friend_workcount;
		LinearLayout ll_friend_work;
		ImageView headline;

	}

}
