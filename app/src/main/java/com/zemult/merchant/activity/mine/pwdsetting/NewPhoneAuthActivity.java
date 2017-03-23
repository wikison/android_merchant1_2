package com.zemult.merchant.activity.mine.pwdsetting;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
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
import com.zemult.merchant.aip.common.CommonCheckcodeRequest;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.aip.mine.UserEditphoneBandRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class NewPhoneAuthActivity extends BaseActivity {
    private static final int REQ_SUCESS = 0x110;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_phone)
    EditText etphone;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.btn_bangding)
    Button btnBangding;
    @Bind(R.id.tv_sendcode)
    TextView tvSendcode;
    private static final int WAIT = 0x001;

    private boolean isWait = false;
    private Thread mThread = null;
    Request request_common_getcode, request_common_checkcode;
    String strPhone, strCode, strIdNo;
    UserEditphoneBandRequest userEditphoneBandRequest;


    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (etCode.getText().toString().length() > 0
                        && etphone.getText().toString().length() > 0) {
                    btnBangding.setEnabled(true);
                    btnBangding.setBackgroundResource(R.drawable.common_selector_btn);
                }
            } else {
                btnBangding.setEnabled(false);
                btnBangding.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_new_phone_auth);
    }

    @Override
    public void init() {
        strIdNo = getIntent().getStringExtra("strIdNo");
        tvSendcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvSendcode.getPaint().setAntiAlias(true);//抗锯齿
        lhTvTitle.setText("更换绑定手机号码");

        btnBangding.setEnabled(false);
        btnBangding.setBackgroundResource(R.drawable.next_bg_btn_select);
        etCode.addTextChangedListener(watcher);
        etphone.addTextChangedListener(watcher);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bangding, R.id.tv_sendcode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;

            case R.id.tv_sendcode:
                strPhone = etphone.getText().toString();
                if (StringUtils.isBlank(strPhone) || !StringMatchUtils.isMobileNO(strPhone)) {
                    ToastUtil.showMessage("请输入正确的手机号码");
                    return;
                }
                if (SlashHelper.userManager().getUserinfo().getPhoneNum().equals(strPhone)) {
                    ToastUtil.showMessage("该新手机号已注册，请输入新的手机号码");
                    return;
                }
                getCode();
                break;
            case R.id.btn_bangding:
                strPhone = etphone.getText().toString();
                strCode = etCode.getText().toString();
                if (SlashHelper.userManager().getUserinfo().getPhoneNum().equals(strPhone)) {
                    ToastUtil.showMessage("该新手机号已注册，请输入新的手机号码");
                    return;
                }
                checkCode();
                break;
        }
    }

    private void user_editphone_band() {
        loadingDialog.show();
        if (userEditphoneBandRequest != null) {
            userEditphoneBandRequest.cancel();
        }
        UserEditphoneBandRequest.Input input = new UserEditphoneBandRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();    //	用户id
        input.phone = strPhone;
        input.convertJosn();

        userEditphoneBandRequest = new UserEditphoneBandRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(Object response) {
                loadingDialog.dismiss();
                if (((CommonResult) response).status == 1) {

                    SlashHelper.userManager().getUserinfo().setPhoneNum(strPhone);
                    SlashHelper.setSettingString("last_login_phone", SlashHelper.userManager().getUserinfo().getPhoneNum());

                    Intent intent = new Intent(NewPhoneAuthActivity.this, BindNewPhoneSucessActivity.class);
                    startActivityForResult(intent, REQ_SUCESS);
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userEditphoneBandRequest);
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
                    } else
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
                        user_editphone_band();
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
        if (resultCode == RESULT_OK && requestCode == REQ_SUCESS) {
            setResult(RESULT_OK);
            onBackPressed();
        }
    }
}
