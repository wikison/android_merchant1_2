package com.zemult.merchant.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.PayPasswordManagerActivity;
import com.zemult.merchant.aip.common.UserCheckpaypwdRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.common.CommonDialog;
import com.zemult.merchant.view.password.GridPasswordView;

import cn.trinea.android.common.util.DigestUtils;
import zema.volley.network.ResponseListener;
import zema.volley.network.VolleyUtil;

public class BalancePayAlertView {

	private BaseActivity activity = null;
	private AlertDialog alertDialog = null;
	private TextView lblAmount = null;
	private TextView lblTips = null;
	private TextView lblTips2 = null;
	private TextView lblClose = null;
	private GridPasswordView passwordView = null;
	private OnValidatePasswordListener validatePasswordListener = null;

	public BalancePayAlertView(BaseActivity activity) {
		this.activity = activity;
		init();
	}

	private void init() {

		alertDialog = new AlertDialog.Builder(activity).create();
		alertDialog.show();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.setCancelable(false);
		Window window = alertDialog.getWindow();

		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setContentView((View.inflate(activity,
				R.layout.dialog_balance_pay_alert_view, null)));

		lblClose = (TextView) window
				.findViewById(R.id.balancePayAlertView_lblClose);
		lblClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				alertDialog.dismiss();
			}
		});
		lblAmount = (TextView) window
				.findViewById(R.id.balancePayAlertView_lblAmount);
		lblTips = (TextView) window
				.findViewById(R.id.balancePayAlertView_lblTips);
		lblTips2 = (TextView) window
				.findViewById(R.id.balancePayAlertView_lblTips2);
		passwordView = (GridPasswordView) window
				.findViewById(R.id.balancePayAlertView_passwordView);
		passwordView
				.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
					public void onChanged(String psw) {
						// System.out.println("onChanged-psw:" + psw);
					}

					public void onMaxLength(String psw) {
						// System.out.println("onMaxLength-psw:" + psw);
						hideSoftInput();
						verifyPayPassword(psw);
					}
				});

		passwordView.requestFocus();
	}

	private void verifyPayPassword(final String pwd) {
		UserCheckpaypwdRequest userCheckpaypwdRequest;
		UserCheckpaypwdRequest.Input input = new UserCheckpaypwdRequest.Input();
		if (SlashHelper.userManager().getUserinfo() != null) {
			input.userId = SlashHelper.userManager().getUserId();
			input.password = DigestUtils.md5(pwd).toUpperCase();
			input.convertJosn();
		}

		userCheckpaypwdRequest = new UserCheckpaypwdRequest(input, new ResponseListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(getClass().getName(), error.getMessage());
				activity.dismissPd();
			}

			@Override
			public void onResponse(Object response) {
				if (((CommonResult) response).status == 1) {
					onValidateSuccessed(DigestUtils.md5(pwd).toUpperCase());
				} else {

					CommonDialog.showDialogListener(activity, null, "忘记密码", "确定", "安全密码错误,请重试", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intentpaypassword=new Intent(activity,PayPasswordManagerActivity.class);
							intentpaypassword.putExtra("gotofind",true);
							activity.startActivity(intentpaypassword);
							passwordView.clearPassword();
							CommonDialog.DismissProgressDialog();
						}
					}, new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							passwordView.clearPassword();
							CommonDialog.DismissProgressDialog();

						}
					});
				}
				activity.dismissPd();
			}
		});

		VolleyUtil.getRequestQueue().add(userCheckpaypwdRequest) ;


//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("userId", App.getInstance().getUser().getAccount());
//		params.put("payPassword", MD5Tools.MD5(pwd));
//		webServiceManager.invokeMethod("verifyPayPassword", params,
//				new TypeToken<RemoteResult>() {
//				}.getType(), new WebServiceCallback<RemoteResult>() {
//
//					public void startInvoke() {
//						activity.showPd();
//					}
//
//					public void onSuccess(RemoteResult result) {
//						if ("1".equals(result.getStatue())) {
//							onValidateSuccessed(MD5Tools.MD5(pwd));
//						} else {
//							ECAlertDialog alertDialog = ECAlertDialog.buildAlert(
//									activity, result.getMessage(), "重试",
//									"忘记密码",
//									new DialogInterface.OnClickListener() {
//										public void onClick(
//												DialogInterface dialogInterface,
//												int arg1) {
//											show();
//										}
//									}, new DialogInterface.OnClickListener() {
//										public void onClick(
//												DialogInterface dialogInterface,
//												int arg1) {
//											Intent intent = new Intent(
//													activity,
//													PasswordManageActivity.class);
//											activity.startActivity(intent);
//										}
//									});
//							alertDialog.show();
//							onValidateFailed();
//						}
//					}
//
//					public void onFailure(Exception e) {
//						ToastUtil.showMessage( e.getMessage());
//						onValidateFailed();
//					}
//
//					public void endInvoke() {
//						activity.dismissPd();
//					}
//
//				});
	}

	private void onValidateSuccessed(String pwd) {
		hideSoftInput();
		if (alertDialog != null && alertDialog.isShowing()) {
			passwordView.clearPassword();
			alertDialog.dismiss();
		}
		if (validatePasswordListener != null) {
			validatePasswordListener.onValidateSuccessed(pwd);
		}
	}

	private void onValidateFailed() {
		if (validatePasswordListener != null) {
			validatePasswordListener.onValidateFailed();
		}
		hideSoftInput();
		if (alertDialog != null && alertDialog.isShowing()) {
			passwordView.clearPassword();
			alertDialog.dismiss();
		}
	}

	private void hideSoftInput() {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(passwordView.inputView.getWindowToken(),
				InputMethodManager.RESULT_UNCHANGED_HIDDEN);
	}

	public void setAmount(String amount) {
		lblAmount.setText("¥:" + amount);
	}

	public void setTips(String tips) {
		lblTips.setVisibility(View.VISIBLE);
		lblTips.setText(tips);
	}
	public void setTips2(String tips2) {
		lblTips2.setVisibility(View.VISIBLE);
		lblTips2.setText(tips2);
	}



	public void setValidatePasswordListener(
			OnValidatePasswordListener validatePasswordListener) {
		this.validatePasswordListener = validatePasswordListener;
	}

	public interface OnValidatePasswordListener {
		void onValidateSuccessed(String pwd);

		void onValidateFailed();
	}
}
