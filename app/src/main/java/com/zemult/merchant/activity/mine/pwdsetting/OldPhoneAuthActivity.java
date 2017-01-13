package com.zemult.merchant.activity.mine.pwdsetting;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.taobao.av.util.StringUtil;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonCheckcodeRequest;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

public class OldPhoneAuthActivity extends BaseActivity {

    private static final int WAIT = 0x001;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.tv_sendcode)
    TextView tvSendcode;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.btn_bangding)
    Button btnBangding;
    @Bind(R.id.tv_unusephone)
    TextView tvUnusephone;

    private boolean isWait = false;
    private Thread mThread = null;
    Request request_common_getcode, request_common_checkcode;
    String strPhone, strCode;
    int isConfirm;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_old_phone_auth);
    }

    @Override
    public void init() {
        lhTvTitle.setText("更换绑定");
        tvUnusephone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvUnusephone.getPaint().setAntiAlias(true);
        strPhone = SlashHelper.userManager().getUserinfo().getPhoneNum();
        isConfirm = SlashHelper.userManager().getUserinfo().isConfirm;
        tvPhone.setText(strPhone);
        tvSendcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvSendcode.getPaint().setAntiAlias(true);//抗锯齿
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                        tvSendcode.setText("重新发送(" + 60 + "s)");
                        tvSendcode.setClickable(false);
                        tvSendcode.setTextColor(0xff828282);
                        waitForClick();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
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
                        isWait = false;
                        tvSendcode.setText("重新发送");
                        tvSendcode.setClickable(true);
                        tvSendcode.setTextColor(0xffe6bb7c);
                        Intent intent = new Intent(OldPhoneAuthActivity.this, IdnoAuthActivity.class);
                        startActivity(intent);
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
                tvSendcode.setText("重新发送(" + i + "s)");
                if (i == 0) {
                    isWait = false;
                    tvSendcode.setText("重新发送");
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

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bangding, R.id.tv_unusephone, R.id.tv_sendcode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_sendcode:
                getCode();
                break;
            case R.id.btn_bangding:
                strCode = etCode.getText().toString();
                if (StringUtil.isEmpty(strCode)) {
                    etCode.setError("请输入验证码");
                    return;
                }
                checkCode();
                break;
            case R.id.tv_unusephone:
                isWait = false;
                tvSendcode.setText("重新发送");
                tvSendcode.setClickable(true);
                tvSendcode.setTextColor(0xffe6bb7c);
                if (isConfirm == 0) {
                    Intent intent = new Intent(OldPhoneAuthActivity.this, GotoTurenameActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(OldPhoneAuthActivity.this, IdnoAuthActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }
}
