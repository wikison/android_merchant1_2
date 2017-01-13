package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.aip.mine.UserRealnameAttestationRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;


/**
 * 实名认证
 */
public class TrueNameActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.tv_sendcode)
    TextView tvSendcode;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_idnumber)
    EditText etIdnumber;
    @Bind(R.id.bt_over)
    Button btOver;
    private boolean isWait = false;
    private Thread mThread = null;
    Request request_common_getcode, userRealnameAttestationRequest;
    private static final int WAIT = 0x001;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_truename);
    }

    @Override
    public void init() {
        lhTvTitle.setText("实名认证");
        btOver.setBackgroundResource(R.drawable.next_bg_btn_select);
        etPhone.addTextChangedListener(watcher);
        etCode.addTextChangedListener(watcher);
        btOver.setEnabled(false);
        tvSendcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvSendcode.getPaint().setAntiAlias(true);//抗锯齿
    }


    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (etCode.getText().toString().length() > 0
                        && etPhone.getText().toString().length() > 0) {
                    btOver.setEnabled(true);
                    btOver.setBackgroundResource(R.drawable.commit);
                }

            } else {
                btOver.setEnabled(false);
                btOver.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @OnClick({R.id.tv_sendcode, R.id.bt_over, R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_sendcode:
                sendCode();
                break;
            case R.id.bt_over:
                if (!StringMatchUtils.isMobileNO(etPhone.getText().toString())) {
                    etPhone.setError("请输入正确的手机号");
                    return;
                }
                if (StringUtils.isEmpty(etName.getText().toString())) {
                    etName.setError("请输入姓名");
                    return;
                }
                if (StringUtils.isEmpty(etIdnumber.getText().toString())) {
                    etIdnumber.setError("请输入身份证号");
                    return;
                }
                if (StringUtils.isEmpty(etCode.getText().toString())) {
                    etCode.setError("请输入验证码");
                    return;
                }
                user_realname_attestation();
                break;
        }
    }

    // 发送验证码
    private void sendCode() {
        if (TextUtils.isEmpty(etPhone.getText().toString())) {
            etPhone.setError("请输入手机号");
        } else {
            if (!StringMatchUtils.isMobileNO(etPhone.getText().toString())) {
                etPhone.setError("请输入正确的手机号");
                return;
            }
            getCode();
        }

    }

    // 倒计时60s
    private void waitForClick() {
        isWait = true;
        final Handler handler = new Handler() {
            int i = 60;

            public void handleMessage(Message msg) {
                i--;
                tvSendcode.setText("重新发送(" + i + "s)");
                if (i == 0) {
                    isWait = false;
                    tvSendcode.setText("重新发送");
                    tvSendcode.setClickable(true);
                    tvSendcode.setTextColor(0xffe6bb7c);
                    i = 60;
                }
            }

            ;
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

    private void getCode() {
        try {
            if (request_common_getcode != null) {
                request_common_getcode.cancel();
            }
            CommonGetCodeRequest.Input input = new CommonGetCodeRequest.Input();
            input.phone = etPhone.getText().toString();
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
                        tvSendcode.setText("重新发送(" + 60 + "s)");
                        tvSendcode.setClickable(false);
                        tvSendcode.setTextColor(0xff828282);
                        waitForClick();
                    }
                }
            });
            sendJsonRequest(request_common_getcode);
        } catch (Exception e) {
            Log.e("COMMON_GETCODE", e.toString());
        }

    }

    private void user_realname_attestation() {//发送验证码校验

        ;
        try {
            if (userRealnameAttestationRequest != null) {
                userRealnameAttestationRequest.cancel();
            }
            final UserRealnameAttestationRequest.Input input = new UserRealnameAttestationRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.phone = etPhone.getText().toString();
            input.code = etCode.getText().toString();
            input.realName = etName.getText().toString();
            input.idCard = etIdnumber.getText().toString();
            input.convertJosn();

            userRealnameAttestationRequest = new UserRealnameAttestationRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        Intent updateintent = new Intent(Constants.BROCAST_UPDATEMYINFO);
                        sendBroadcast(updateintent);
                        Intent intent = new Intent(TrueNameActivity.this, TrueNameResultActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(userRealnameAttestationRequest);
        } catch (Exception e) {
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

}
