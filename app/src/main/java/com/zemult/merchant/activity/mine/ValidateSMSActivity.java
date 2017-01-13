package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.MAppCompatActivity;
import com.zemult.merchant.util.AppUtils;


public class ValidateSMSActivity extends MAppCompatActivity {

	public static final String SMS_CODE = "sms_code";
	public static final String MOBILE = "phone";

	private String smsCode;
	private String phone;
	private TextView tipTextView;
	private TextView codeTextView;
	private boolean isFromBank;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validate_sms);
		smsCode = getIntent().getStringExtra(SMS_CODE);
		phone = getIntent().getStringExtra(MOBILE);
		isFromBank = getIntent().getBooleanExtra("isFromBank", false);

		initView();
	}

	private void initView() {
		tipTextView = (TextView) findViewById(R.id.tip);
		codeTextView = (TextView) findViewById(R.id.code);

		String text = String.format("验证码已发送至手机：%s,请按提示操作",
				AppUtils.getHiddenMobile(phone));
		text = isFromBank ? "绑定银行卡需要短信确认," + text : text;
		tipTextView.setText(text);

	}


	public void onConfirmClick(View v) {
		if (validate()) {
			Intent intent = new Intent();
			intent.putExtra("smsCode",smsCode);
			setResult(RESULT_OK,intent);
			this.finish();
		} else {
			setResult(RESULT_CANCELED);
		}
	}

	private boolean validate() {
//		String code = codeTextView.getText().toString();
//		String md5 = MD5Tools.MD5(code);
//		LogUtil.d(TAG, md5);
//		if (!md5.equals(smsCode)) {
//			ECAlertDialog alertDialog = ECAlertDialog.buildAlert(this,
//					"验证码错误,请核实后再试", "确定", null);
//			alertDialog.show();
//			return false;
//		}
		return true;
	}

	public void onResendClick(View v) {
//		showLoadingDialog();
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("tel", phone);
//		webServiceManager.invokeMethod("getShortMessageService", params,
//				new ResponseHandler());
	}

//	class ResponseHandler implements WebServiceInterface {
//
//		@Override
//		public void onSuccess(String jsonStr) {
//			SMSCode sms = new SMSCode(jsonStr);
//			closeLoadingDialog();
//			showToast("验证码已发送");
//			smsCode = sms.getCode();
//		}
//
//		@Override
//		public void onFailure() {
//		}
//
//	}
}
