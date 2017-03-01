package com.zemult.merchant.view.common;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;

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
}
