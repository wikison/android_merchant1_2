package com.zemult.merchant.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonCheckcodeRequest;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.aip.common.UserIsRegisterRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;


/**
 * Created by Wikison on 2016/2/25.
 */
public class RegisterActivity extends BaseActivity {
    private static final int WAIT = 0x001;
    private static String LOG_TAG = "RegisterActivity";
    String strPhone, strCode, strRequestCode;
    Request request_common_getcode, request_common_checkcode, request_user_is_register;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.tv_sendcode)
    TextView tvSendcode;
    @Bind(R.id.btn_next)
    Button btnNext;
    @Bind(R.id.tv_not_now)
    TextView tvNotNow;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.cb_agree)
    CheckBox cbAgree;
    @Bind(R.id.tv_protocol)
    TextView tvProtocol;
    private boolean isWait = false;
    private Thread mThread = null;
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (etCode.getText().toString().length() > 0
                        && etPhone.getText().toString().length() > 0) {
                    btnNext.setEnabled(true);
                    btnNext.setBackgroundResource(R.drawable.common_selector_btn);
                }

            } else {
                btnNext.setEnabled(false);
                btnNext.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_register);
    }

    @Override
    public void init() {
        initViews();
    }

    public void initViews() {

        tvNotNow.setMovementMethod(LinkMovementMethod.getInstance());
        tvNotNow.setText(getClickableSpanString(getResources().getString(R.string.txt_register_notnow)));
        lhTvTitle.setText(getResources().getString(R.string.btn_register));
        cbAgree.setChecked(true);

        btnNext.setEnabled(false);
        btnNext.setBackgroundResource(R.drawable.next_bg_btn_select);
        etPhone.addTextChangedListener(watcher);
        etCode.addTextChangedListener(watcher);
        tvSendcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
        tvSendcode.getPaint().setAntiAlias(true);//抗锯齿
    }

    //设置超链接文字
    public SpannableString getClickableSpanString(String spanStr) {
        SpannableString spannableString = new SpannableString(spanStr);
        //设置文字的单击事件
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_not_now:
                        finish();
                        break;
                }

            }
        }, 0, spanStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @OnClick(R.id.tv_sendcode)
    public void onBtnCodeClick() {
        strPhone = etPhone.getText().toString();
        if (StringUtils.isBlank(strPhone)) {
            etPhone.setError("请输入手机号码");
        } else {
            if (!StringMatchUtils.isMobileNO(etPhone.getText().toString())) {
                etPhone.setError("请输入正确的手机号码");
                return;
            }

            isRegister();
        }
    }

    @OnClick(R.id.btn_next)
    public void onBtnRegisterClick() {
        strPhone = etPhone.getText().toString();
        strCode = etCode.getText().toString();

        if (cbAgree.isChecked()){
            //验证码校验
            checkCode();
        }else {
            ToastUtil.showMessage("请勾选同意本平台协议");
        }

    }

    private void isRegister() {
        try {
            if (request_user_is_register != null) {
                request_user_is_register.cancel();
            }
            UserIsRegisterRequest.Input input = new UserIsRegisterRequest.Input();
            input.phone = strPhone;
            input.convertJosn();

            request_user_is_register = new UserIsRegisterRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        getCode();
                    } else {
                        ToastUtil.showMessage("手机号码已注册");
                    }
                }
            });
            sendJsonRequest(request_user_is_register);
        } catch (Exception e) {
            Log.e("USER_IS_REGISTER", e.toString());
        }

    }


    private void getCode() {
        try {
            if (request_common_getcode != null) {
                request_common_getcode.cancel();
            }
            CommonGetCodeRequest.Input input = new CommonGetCodeRequest.Input();
            input.phone = strPhone;
            input.convertJosn();

            request_common_getcode = new CommonGetCodeRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        ToastUtil.showMessage("验证码已发送, 请查收!");
                        tvSendcode.setText("重新获取(" + 60 + "s)");
                        tvSendcode.setClickable(false);
                        tvSendcode.setTextColor(0xff828282);
                        waitForClick();
                    }else
                        ToastUtil.showMessage(((CommonResult) response).info);
                }
            });
            sendJsonRequest(request_common_getcode);
        } catch (Exception e) {
            Log.e("COMMON_GETCODE", e.toString());
        }

    }

    private void checkCode() {//发送验证码校验
        try {
            if (request_common_checkcode != null) {
                request_common_checkcode.cancel();
            }
            final CommonCheckcodeRequest.Input input = new CommonCheckcodeRequest.Input();
            input.phone = strPhone;
            input.code = strCode;
            input.convertJosn();

            request_common_checkcode = new CommonCheckcodeRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        Intent intent = new Intent(RegisterActivity.this, PasswordActivity.class);
                        intent.putExtra("RegisterPhone", strPhone);
                        startActivityForResult(intent, 0x110);
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);

                    }
                }
            });
            sendJsonRequest(request_common_checkcode);
        } catch (Exception e) {
            Log.e("COMMON_CHECKCODE", e.toString());
        }

    }

    // 倒计时60s
    private void waitForClick() {
        isWait = true;
        final Handler handler = new Handler() {
            int i = 60;

            public void handleMessage(Message msg) {
                i--;
                tvSendcode.setText("重新获取(" + i + "s)");
                if (i == 0) {
                    isWait = false;
                    tvSendcode.setText("重新获取");
                    tvSendcode.setClickable(true);
                    tvSendcode.setTextColor(0xffe6bb7c);
                    i = 60;
                }
            }
        };

        mThread = new Thread() {
            @Override
            public void run() {
                while (isWait) {
                    try {
                        handler.sendEmptyMessage(WAIT);
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isWait = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0x110)
                finish();
        }
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_protocol})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_protocol:
                IntentUtil.start_activity(RegisterActivity.this, BaseWebViewActivity.class, new Pair<String, String>("titlename", getString(R.string.app_name) + "服务协议"), new Pair<String, String>("url", Constants.PROTOCOL_REGISTER));
                break;
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }
}

