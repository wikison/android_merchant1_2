package com.zemult.merchant.view.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.FilterEntity;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ImageManager;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.StringUtils;


public final class MMAlert {
	public static final int TYPE_BUTTON = 0;
	public static final int TYPE_TITLE = 1;
	public static final int TYPE_EXIT = 2;
	public static final int TYPE_CANCEL = 3;



	public interface OnAlertSelectId {
		void onClick(int whichButton);
	}

	private MMAlert() {
	}


	public static Dialog showAlert(final Context context, final String title,
								   final String[] items, String exit, final OnAlertSelectId alertDo) {
		return showAlert(context, title, items, exit, alertDo, null);
	}

	/**
	 * @param context Context.
	 * @param title   The title of this AlertDialog can be null .
	 * @param items   button name list.
	 * @param alertDo methods call Id:Button + cancel_Button.
	 * @param exit    Name can be null.It will be Red Color
	 * @return A AlertDialog
	 */
	public static Dialog showAlert(Context context, final String title,
								   final String[] items, String exit, final OnAlertSelectId alertDo,
								   DialogInterface.OnCancelListener cancelListener) {
		String cancel = context.getString(R.string.btn_cancel);
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_dialog_menu_layout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		final ListView list = (ListView) layout.findViewById(R.id.content_list);
		AlertAdapter adapter = new AlertAdapter(context, title, items, exit,
				cancel);
		list.setAdapter(adapter);
		list.setDividerHeight(0);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				if (!(title == null || title.equals("")) && position - 1 >= 0) {
					alertDo.onClick(position - 1);
					dlg.dismiss();
					list.requestFocus();
				} else {
					alertDo.onClick(position);
					dlg.dismiss();
					list.requestFocus();
				}

			}
		});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null)
			dlg.setOnCancelListener(cancelListener);

		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	public interface DeleteCallback {
		void OnDelete();
	}

	public interface AddPhotoCallback {
		void onTakePic();
		void onChoosePoc();
	}
	/**
	 * 选择照片
	 */
	public static Dialog addPhotoDialog( Context context,
										   final AddPhotoCallback callback) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		dlg.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_dialog_addphoto_layout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		TextView tv1 = (TextView) layout.findViewById(R.id.tv1);
		TextView tv2 = (TextView) layout.findViewById(R.id.tv2);
		TextView tv3 = (TextView) layout.findViewById(R.id.tv3);
		tv1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				callback.onChoosePoc();
				dlg.dismiss();
			}
		});
		tv2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				callback.onTakePic();
				dlg.dismiss();
			}
		});
		tv3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}








	/**
	 * 清除缓存
	 */
	public static Dialog deleteCashDialog( Context context,
										  final DeleteCallback callback) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		dlg.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_dialog_deletecash_layout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		TextView tvOk = (TextView) layout.findViewById(R.id.tvOk);
		TextView tvCancel = (TextView) layout.findViewById(R.id.tvCancel);
		tvOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				callback.OnDelete();
				dlg.dismiss();
			}
		});
		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	/**
	 * 全部评论--回复、举报
	 */
	public interface CommentCallback {
		void onReply();
		void onReport();
	}
	public static Dialog showCommentDialog( Context context,
										   final CommentCallback callback) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		dlg.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_dialog_comment_layout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		TextView tvReply = (TextView) layout.findViewById(R.id.tv_reply);
		TextView tvReport = (TextView) layout.findViewById(R.id.tv_report);
		TextView tvCancel = (TextView) layout.findViewById(R.id.tv_cancle);
		tvReply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				callback.onReply();

			}
		});
		tvReport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				callback.onReport();
			}
		});
		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	/**
	 * 全部评论--回复、举报
	 */
	public interface ChooseCallback {
		void onfirstChoose();
		void onsecondChoose();
		void onthirdChoose();
	}
	public static Dialog showChooseStateDialog( Context context,
											final ChooseCallback callback) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		dlg.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_dialog_statechoose_layout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		TextView tvfirst = (TextView) layout.findViewById(R.id.tv_kongxian);
		TextView tvsecond = (TextView) layout.findViewById(R.id.tv_xiuxi);
		TextView tvthird = (TextView) layout.findViewById(R.id.tv_manglu);
		TextView tvCancel = (TextView) layout.findViewById(R.id.tv_cancle);
		tvfirst.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				callback.onfirstChoose();

			}
		});
		tvsecond.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				callback.onsecondChoose();
			}
		});
		tvthird.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				callback.onthirdChoose();
			}
		});

		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}






	/**
	 * 角色设置--删除角色
	 */
	public interface DelCallback {
		void onDel();
	}

	public static Dialog showDelDialog(Context context, String msg,
											final DelCallback callback) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		dlg.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_dialog_del_layout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		TextView tvContent = (TextView) layout.findViewById(R.id.tv_content);
		TextView tvDel = (TextView) layout.findViewById(R.id.tv_del);
		TextView tvCancel = (TextView) layout.findViewById(R.id.tv_cancle);

		tvContent.setText("将角色\"" + msg + "\"删除,同时删除与此角色的相关信息");
		tvDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				callback.onDel();

			}
		});
		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	/**
	 * 商家详情--申请营销经理
	 */
	public interface ApplyCallback {
		void onApply();
	}

	public static Dialog showApplyDialog(Context context, M_Merchant m_merchant,
									   final ApplyCallback callback) {
		ImageManager imageManager = new ImageManager(context);
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		dlg.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_dialog_apply_layout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		TextView tvName = (TextView) layout.findViewById(R.id.tv_name);
		TextView tvDiscount = (TextView) layout.findViewById(R.id.tv_discount);
		TextView tvApply = (TextView) layout.findViewById(R.id.tv_apply);
		ImageView ivClose = (ImageView) layout.findViewById(R.id.iv_close);
		ImageView ivHead = (ImageView) layout.findViewById(R.id.iv_head);

		if(!StringUtils.isBlank(m_merchant.name))
			tvName.setText(m_merchant.name);
		tvDiscount.setText("佣金" + m_merchant.commissionDiscount + "%") ;
		if(!StringUtils.isBlank(m_merchant.head))
			imageManager.loadCircleImage(m_merchant.head, ivHead);

		tvApply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				callback.onApply();

			}
		});
		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	public interface OneOperateCallback {
		void onOneOperate();
	}
	public static Dialog showOneOperateDialog(Context context, String msg, String operate, final OneOperateCallback callback) {
		ImageManager imageManager = new ImageManager(context);
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		dlg.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_dialog_apply_success_layout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		TextView tvMsg = (TextView) layout.findViewById(R.id.tv_msg);
		TextView tvOk = (TextView) layout.findViewById(R.id.tv_ok);

		if(!StringUtils.isBlank(msg))
			tvMsg.setText(msg);
		if(!StringUtils.isBlank(operate))
			tvOk.setText(operate);

		tvOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				callback.onOneOperate();
			}
		});
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}
	public static Dialog showConfirmDialog(Context context, String title, String msg, String operate, final OneOperateCallback callback) {
		ImageManager imageManager = new ImageManager(context);
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		dlg.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.alert_dialog_confirm_layout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		TextView tvTitle = (TextView) layout.findViewById(R.id.tv_title);
		TextView tvMsg = (TextView) layout.findViewById(R.id.tv_msg);
		TextView tvOk = (TextView) layout.findViewById(R.id.tv_ok);

		if(!StringUtils.isBlank(title))
			tvTitle.setText(title);
		if(!StringUtils.isBlank(msg))
			tvMsg.setText(msg);
		if(!StringUtils.isBlank(operate))
			tvOk.setText(operate);

		tvOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				callback.onOneOperate();
			}
		});
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}



	public interface ChoosefromcameraCallback {
		void cameraCallback();
	}

	public interface Choosefromalbum {
		void albumCallback();
	}

}

	class AlertAdapter extends BaseAdapter {
		public static final int TYPE_BUTTON = 0;
		public static final int TYPE_TITLE = 1;
		public static final int TYPE_EXIT = 2;
		public static final int TYPE_CANCEL = 3;
		private List<String> items;
		private int[] types;
		private boolean isTitle = false;
		private Context context;

		public AlertAdapter(Context context, String title, String[] items,
							String exit, String cancel) {
			if (items == null || items.length == 0) {
				this.items = new ArrayList<String>();
			} else {
				this.items = stringsToList(items);
			}
			this.types = new int[this.items.size() + 3];
			this.context = context;
			if (title != null && !title.equals("")) {
				types[0] = TYPE_TITLE;
				this.isTitle = true;
				this.items.add(0, title);
			}

			if (exit != null && !exit.equals("")) {
				// this.isExit = true;
				types[0] = TYPE_EXIT;
				this.items.add(0, exit);
			}

			if (cancel != null && !cancel.equals("")) {
				// this.isSpecial = true;
				types[this.items.size()] = TYPE_CANCEL;
				this.items.add(cancel);
			}
		}

		private List<String> stringsToList(String[] items2) {
			List<String> items = new ArrayList<String>();
			if (items2 == null || items2.length == 0) {
				return items;
			}
			for (String item : items2) {
				items.add(item);
			}
			return items;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean isEnabled(int position) {
			if (position == 0 && isTitle) {
				return false;
			} else {
				return super.isEnabled(position);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final String textString = (String) getItem(position);
			ViewHolder holder;
			int type = types[position];
			if (convertView == null
					|| ((ViewHolder) convertView.getTag()).type != type) {
				holder = new ViewHolder();
				if (type == TYPE_CANCEL) {
					convertView = View.inflate(context,
							R.layout.alert_dialog_menu_list_layout_cancel, null);
				} else if (type == TYPE_BUTTON) {
					convertView = View.inflate(context,
							R.layout.alert_dialog_menu_list_layout, null);
				} else if (type == TYPE_TITLE) {
					convertView = View.inflate(context,
							R.layout.alert_dialog_menu_list_layout_title, null);
				} else if (type == TYPE_EXIT) {
					convertView = View.inflate(context,
							R.layout.alert_dialog_menu_list_layout_special, null);
				}

				holder.text = (TextView) convertView.findViewById(R.id.popup_text);
				holder.type = type;

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.text.setText(textString);
			return convertView;
		}

		static class ViewHolder {
			TextView text;
			int type;
		}


	}
