package com.zemult.merchant.view.common;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.model.FilterEntity;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ToastUtil;

import java.util.List;

import cn.trinea.android.common.util.ToastUtils;


public class CommonDialog {

	public static Dialog dialog;

	static LayoutInflater inflater;


	public static Dialog showDialog(final Context context, String leftmsg,
			String rightmsg, String message ) {
		if (inflater == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		View v = inflater.inflate(R.layout.dialog_common_view, null);
		Button leftbtn = (Button) v.findViewById(R.id.daymode);
		Button rightbtn = (Button) v.findViewById(R.id.nightmode);
		TextView msg = (TextView) v.findViewById(R.id.laytext);
		msg.setText(message);
		leftbtn.setText(leftmsg);
		rightbtn.setText(rightmsg);
		leftbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent myIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(myIntent);
				dialog.dismiss();
			}
		});
		rightbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {



				dialog.dismiss();
			}
		});

		dialog = new Dialog(context, R.style.translucent_notitle);
		dialog.setContentView(v);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		return dialog;
	}



	public static Dialog showDialogListener(final Context context,String  title, String leftmsg,
									String rightmsg, String message, OnClickListener leftListener,OnClickListener rightListener ) {
		if (inflater == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		View v = inflater.inflate(R.layout.dialog_common_view, null);
		Button leftbtn = (Button) v.findViewById(R.id.daymode);
		Button rightbtn = (Button) v.findViewById(R.id.nightmode);
		TextView msg = (TextView) v.findViewById(R.id.laytext);
		TextView tvtitle = (TextView) v.findViewById(R.id.tv_title);
		ImageView ivtitle = (ImageView) v.findViewById(R.id.iv_title);

		if(!TextUtils.isEmpty(title)){
			tvtitle.setText(title);
			tvtitle.setVisibility(View.VISIBLE);
		}
		msg.setText(message);
		leftbtn.setText(leftmsg);
		rightbtn.setText(rightmsg);
		leftbtn.setOnClickListener(leftListener);
		rightbtn.setOnClickListener(rightListener);

		dialog = new Dialog(context, R.style.translucent_notitle);
		dialog.setContentView(v);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		return dialog;
	}
	/**
	 * dialog dismiss
	 */
	public static void DismissProgressDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public static Dialog showInputDialogListener(final Context context,String  title, String leftmsg,
											String rightmsg, String message, OnClickListener leftListener, final CommitClickListener commitClickListener) {
		if (inflater == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		View v = inflater.inflate(R.layout.dialog_common_view, null);
		Button leftbtn = (Button) v.findViewById(R.id.daymode);
		Button rightbtn = (Button) v.findViewById(R.id.nightmode);
		final EditText etName = (EditText) v.findViewById(R.id.et_name);

		if(message == null){
			etName.setVisibility(View.VISIBLE);
		}
		leftbtn.setText(leftmsg);
		rightbtn.setText(rightmsg);
		leftbtn.setTextColor(0xff007aff);
		rightbtn.setTextColor(0xff007aff);
		leftbtn.setOnClickListener(leftListener);
		rightbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(etName.getText().toString()))
					ToastUtils.show(context, "请输入商家全称");
				else{
					if(commitClickListener != null)
						commitClickListener.onCommit(etName.getText().toString());
				}
			}
		});

		dialog = new Dialog(context, R.style.translucent_notitle);
		dialog.setContentView(v);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		return dialog;
	}

	public interface CommitClickListener{
		void onCommit(String content);
	}

	public static void showPopupWindow(final Context context, View rightButton, final List<FilterEntity> list, final PopClickListener popClickListener) {
		//设置contentView
		View contentView = LayoutInflater.from(context).inflate(R.layout.pop_layout, null);
		final PopupWindow mPopWindow = new PopupWindow(contentView,
				list.get(0).getKey().length()>5 ? DensityUtil.dip2px(context, 172):DensityUtil.dip2px(context, 132), ViewGroup.LayoutParams.WRAP_CONTENT, true);
		mPopWindow.setContentView(contentView);
		mPopWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		mPopWindow.setOutsideTouchable(true);

		ListView lv = (ListView) contentView.findViewById(R.id.lv);


		lv.setAdapter(new CommonAdapter<FilterEntity>(context, R.layout.item_pop, list) {
			@Override
			public void convert(CommonViewHolder holder, FilterEntity entity, final int position) {
				holder.setImageResource(R.id.iv, entity.getMipmap());
				holder.setText(R.id.tv, entity.getKey());

				if(entity.isSelected())
					holder.setViewVisible(R.id.ivDot);
				else
					holder.setViewGone(R.id.ivDot);

				if (position==list.size()-1)
					holder.setViewGone(R.id.divider);
				else
					holder.setViewVisible(R.id.divider);
			}
		});

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mPopWindow.dismiss();
				if(popClickListener!= null)
					popClickListener.onClick(position);
			}
		});
		//显示PopupWindow
		if(list.get(0).getKey().length()>5)
			mPopWindow.showAsDropDown(rightButton, -250, -10);
		else
			mPopWindow.showAsDropDown(rightButton, -180, -20);

	}

	public interface PopClickListener{
		void onClick(int pos);
	}
}
