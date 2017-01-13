package com.zemult.merchant.adapter.slash;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.SelectRoleActivity;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.IntentUtil;

import java.util.ArrayList;
import java.util.List;

public class UserMerchantAdapter extends BaseAdapter {

	Context mContext;
	private List<M_Merchant> merchantList = new ArrayList<M_Merchant>();
	ImageManager imageManager;
	public UserMerchantAdapter(Context context,List<M_Merchant> MmerchantList) {
		mContext=context;
		this.merchantList = MmerchantList;
		imageManager=new ImageManager(context);
	}

	@Override
	public int getCount() {
		return merchantList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return merchantList.get(arg0);
	}


	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		final ViewHolder viewHolder;
		if (arg1 == null) {
			viewHolder = new ViewHolder();
			arg1 = LayoutInflater.from(mContext).inflate(
					R.layout.item_my_merchant, null);
			viewHolder.iv_merchant_head = (ImageView) arg1
					.findViewById(R.id.iv_merchant_head);
			viewHolder.tv_merchant_name = (TextView) arg1
					.findViewById(R.id.tv_merchant_name);
			viewHolder.tv_merchant_distance = (TextView) arg1
					.findViewById(R.id.tv_merchant_distance);
			viewHolder.iv_select = (ImageView) arg1
					.findViewById(R.id.iv_select);
			viewHolder.rellayout = (RelativeLayout) arg1
					.findViewById(R.id.rellayout);

			arg1.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) arg1.getTag();
		}
		final M_Merchant merchant = (M_Merchant) getItem(arg0);
		try {
			viewHolder.tv_merchant_name.setText(merchant.name);
			viewHolder.tv_merchant_distance.setText(merchant.distance);
			imageManager.loadCircleImage(merchant.head, viewHolder.iv_merchant_head);
			viewHolder.rellayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					viewHolder.iv_select.setVisibility(View.VISIBLE);
					IntentUtil.intStart_activity((Activity) mContext,SelectRoleActivity.class,new Pair<String, Integer>("merchantId", merchant.merchantId));
					viewHolder.iv_select.setVisibility(View.INVISIBLE);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return arg1;
	}

	public void setDataChanged(List<M_Merchant> MmerchantList) {
		this.merchantList = MmerchantList;
		this.notifyDataSetChanged();
	}

	class ViewHolder {
		ImageView iv_merchant_head;
		TextView  tv_merchant_name, tv_merchant_distance;
		ImageView iv_select;
		RelativeLayout rellayout;
	}

}
