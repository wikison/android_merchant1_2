package com.zemult.merchant.activity.mine.pwdsetting;

import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonCheckcodeRequest;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.aip.mine.UserEditphoneBandRequest;
import com.zemult.merchant.app.AppApplication;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;

public class NewPhoneAuthActivity extends BaseActivity {

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

    private YWIMKit mIMKit;

    private boolean isWait = false;
    private Thread mThread = null;
    Request request_common_getcode, request_common_checkcode;
    String strPhone, strCode, strIdNo;
    UserEditphoneBandRequest userEditphoneBandRequest;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_new_phone_auth);
    }

    @Override
    public void init() {
        strIdNo = getIntent().getStringExtra("strIdNo");
        tvSendcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvSendcode.getPaint().setAntiAlias(true);//抗锯齿
        lhTvTitle.setText("绑定手机号码");
        mIMKit = LoginSampleHelper.getInstance().getIMKit();
        if (mIMKit == null) {
            return;
        }
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
                    etphone.setError("请输入正确的手机号码");
                    return;
                }
                if (SlashHelper.userManager().getUserinfo().getPhoneNum().equals(strPhone)) {
                    etphone.setError("请输入新的手机号码");
                    return;
                }
                getCode();
                break;
            case R.id.btn_bangding:
                strPhone = etphone.getText().toString();
                strCode = etCode.getText().toString();
                if (StringUtils.isEmpty(strPhone)) {
                    etphone.setError("请输入您的手机号码");
                    return;
                }
                if (SlashHelper.userManager().getUserinfo().getPhoneNum().equals(strPhone)) {
                    etphone.setError("请输入新的手机号码");
                    return;
                }
                if (StringUtils.isEmpty(strCode)) {
                    etCode.setError("请输入验证码");
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
        input.idCard = strIdNo;
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
                    ToastUtil.showMessage("绑定成功");
                    SlashHelper.userManager().getUserinfo().setPhoneNum(strPhone);
                    ImLogout();
                    SlashHelper.setSettingString("last_login_phone", SlashHelper.userManager().getUserinfo().getPhoneNum());
                    SlashHelper.userManager().getUserinfo();
                    SlashHelper.userManager().saveUserinfo(null);
                    EventBus.getDefault().post("exit");
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userEditphoneBandRequest);
    }

    public void ImLogout() {
        // openIM SDK提供的登录服务
        IYWLoginService mLoginService = mIMKit.getLoginService();
        mLoginService.logout(new IWxCallback() {
            //此时logout已关闭所有基于IMBaseActivity的OpenIM相关Actiivity，s
            @Override
            public void onSuccess(Object... arg0) {
                YWLog.i("------IM_LOGOUT---------", "退出成功");
            }

            @Override
            public void onProgress(int arg0) {

            }

            @Override
            public void onError(int arg0, String arg1) {
                Toast.makeText(AppApplication.getContext(), "请重新登录", Toast.LENGTH_SHORT).show();
            }
        });
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
}
