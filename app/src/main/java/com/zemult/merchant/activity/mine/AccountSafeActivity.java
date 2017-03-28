package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.HeadManageActivity;
import com.zemult.merchant.activity.LoginActivity;
import com.zemult.merchant.activity.mine.pwdsetting.OldPhoneAuthActivity;
import com.zemult.merchant.aip.common.UserBandWxDelRequest;
import com.zemult.merchant.aip.common.UserBandWxInfoPhoneRequest;
import com.zemult.merchant.aip.common.UserBandWxInfoRequest;
import com.zemult.merchant.aip.common.UserBandWxRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/1/18.
 */

public class AccountSafeActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.rl_bangbankcard)
    RelativeLayout rlBangbankcard;
    @Bind(R.id.rl_changephone)
    RelativeLayout rlChangephone;
    @Bind(R.id.rl_authentication)
    RelativeLayout rlAuthentication;
    @Bind(R.id.rl_loginpassword)
    RelativeLayout rlLoginpassword;
    @Bind(R.id.rl_paypassword)
    RelativeLayout rlPaypassword;
    @Bind(R.id.tv_wx_tip)
    TextView tvWxTip;

    boolean isfirstload = true;
    int isSetPaypwd, isConfirm;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_accountsafe);
    }

    @Override
    public void init() {
        lhTvTitle.setText("账户与安全");
        user_band_wx_info();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == SlashHelper.userManager().getUserinfo()) {
            finish();
            return;
            //// TODO: 2017/2/10 去首页 ？？
        }
        isSetPaypwd = SlashHelper.userManager().getUserinfo().isSetPaypwd;
        isConfirm = SlashHelper.userManager().getUserinfo().isConfirm;//是否实名认证过(0:否1:是)
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rl_bangbankcard, R.id.rl_changephone, R.id.rl_authentication, R.id.rl_loginpassword, R.id.rl_paypassword, R.id.rl_wx})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                finish();
                break;
            case R.id.rl_bangbankcard:
                Intent intentcar = new Intent(AccountSafeActivity.this, BindBankCardActivity.class);
                startActivity(intentcar);
                break;
            case R.id.rl_changephone:
                Intent intentphone = new Intent(AccountSafeActivity.this, OldPhoneAuthActivity.class);
                startActivity(intentphone);
                break;
            case R.id.rl_authentication:
                if (isConfirm == 0) {
                    Intent intenttrue = new Intent(AccountSafeActivity.this, TrueNameActivity.class);
                    startActivity(intenttrue);
                } else {
                    Intent intenttrue = new Intent(AccountSafeActivity.this, TrueNameResultActivity.class);
                    startActivity(intenttrue);
                }
                break;
            case R.id.rl_loginpassword:
                Intent intentpwd = new Intent(AccountSafeActivity.this, ChangePasswordActivity.class);
                intentpwd.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentpwd);
                break;
            case R.id.rl_paypassword:
                //安全密码
                Intent intentpaypassword = new Intent(AccountSafeActivity.this, PayPasswordManagerActivity.class);
                startActivity(intentpaypassword);
                break;
            case R.id.rl_wx:
                if(StringUtils.isBlank(tvWxTip.getText().toString()))
                    return;
                if("已绑定".equals(tvWxTip.getText().toString())){
                    CommonDialog.showDialogListener(AccountSafeActivity.this, "解除绑定", "取消", "解除绑定", "确定要解除账户与微信的关联吗？", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonDialog.DismissProgressDialog();

                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonDialog.DismissProgressDialog();
                            user_band_wx_del();
                        }
                    });
                }else {
                    thirdLogin();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private UserBandWxInfoRequest userBandWxInfoRequest;
    private void user_band_wx_info() {
        showUncanclePd();
        try {
            if (userBandWxInfoRequest != null) {
                userBandWxInfoRequest.cancel();
            }
            UserBandWxInfoRequest.Input input = new UserBandWxInfoRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.convertJosn();

            userBandWxInfoRequest = new UserBandWxInfoRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                    dismissPd();
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        if(((CommonResult) response).isBand == 0)// 是否已经绑定了微信账号(0:否,1:是)
                            tvWxTip.setText("未绑定");
                        else
                            tvWxTip.setText("已绑定");
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(userBandWxInfoRequest);
        } catch (Exception e) {
            Log.e("USER_IS_REGISTER", e.toString());
        }
    }
    private UserBandWxRequest userBandWxRequest;
    private void user_band_wx(String openid,String nickname,String head) {
        showUncanclePd();
        try {
            if (userBandWxRequest != null) {
                userBandWxRequest.cancel();
            }
            UserBandWxRequest.Input input = new UserBandWxRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.openid = openid;
            input.name = nickname;
            input.pic = head;
            input.convertJosn();

            userBandWxRequest = new UserBandWxRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                    dismissPd();
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        tvWxTip.setText("已绑定");
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(userBandWxRequest);
        } catch (Exception e) {
            Log.e("USER_IS_REGISTER", e.toString());
        }
    }
    private UserBandWxDelRequest userBandWxDelRequest;
    private void user_band_wx_del() {
        showUncanclePd();
        try {
            if (userBandWxDelRequest != null) {
                userBandWxDelRequest.cancel();
            }
            UserBandWxDelRequest.Input input = new UserBandWxDelRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.convertJosn();

            userBandWxDelRequest = new UserBandWxDelRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                    dismissPd();
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        tvWxTip.setText("未绑定");
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(userBandWxDelRequest);
        } catch (Exception e) {
            Log.e("USER_IS_REGISTER", e.toString());
        }
    }


    private void thirdLogin() {
        UMShareAPI.get(AccountSafeActivity.this).doOauthVerify(AccountSafeActivity.this, SHARE_MEDIA.WEIXIN, doOauthVerifyListener);
    }

    private UMAuthListener doOauthVerifyListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            UMShareAPI.get(AccountSafeActivity.this).getPlatformInfo(AccountSafeActivity.this, SHARE_MEDIA.WEIXIN, getPlatformInfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "授权取消", Toast.LENGTH_SHORT).show();
        }
    };
    private UMAuthListener getPlatformInfoListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            user_band_wx( data.get("openid"), data.get("nickname"), data.get("headimgurl"));
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "获取信息失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "获取信息取消", Toast.LENGTH_SHORT).show();
        }
    };
}
