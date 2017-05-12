package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.User2ResOrderTmpSendRequest;
import com.zemult.merchant.aip.common.UserWxBandPhoneRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/1/24.
 */

public class SharePhoneNumActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.phoneNum_et)
    EditText phoneNumEt;
    @Bind(R.id.ok_btn)
    Button okBtn;

    int tmpid;
    String orderTime,shopName;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_settingsharephonenum);
    }

    @Override
    public void init() {
        lhTvTitle.setText("手机联系人");
        okBtn.setBackgroundResource(R.drawable.next_bg_btn_select);
        phoneNumEt.addTextChangedListener(watcher);
        tmpid=getIntent().getIntExtra("tmpid",0);
        orderTime=getIntent().getStringExtra("orderTime");
        shopName=getIntent().getStringExtra("shopName");

    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (phoneNumEt.getText().toString().length() == 11) {
                    okBtn.setEnabled(true);
                    okBtn.setBackgroundResource(R.drawable.common_selector_btn);
                }
                else {
                    okBtn.setEnabled(false);
                    okBtn.setBackgroundResource(R.drawable.next_bg_btn_select);
                }
            } else {
                phoneNumEt.setHint("请输入客户手机号码");
                okBtn.setEnabled(false);
                okBtn.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    //微信绑定手机号登陆(注册)
    private User2ResOrderTmpSendRequest user2ResOrderTmpSendRequest;
    private void user_wx_band_phone(){
        showUncanclePd();
        try {
            if (user2ResOrderTmpSendRequest != null) {
                user2ResOrderTmpSendRequest.cancel();
            }
            final User2ResOrderTmpSendRequest.Input input = new User2ResOrderTmpSendRequest.Input();
            input.resOrderId = tmpid;
            input.phoneNum = phoneNumEt.getText().toString();
            input.convertJosn();

            user2ResOrderTmpSendRequest = new User2ResOrderTmpSendRequest(input, new ResponseListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                    dismissPd();
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNumEt.getText().toString()));
                        intent.putExtra("sms_body", "【约服】您的好友"+ SlashHelper.userManager().getUserinfo().getName()
                                +"发来一个服务订单，"+orderTime+shopName+"，立即去查看并确认..."+  ((CommonResult) response).shorturl);
                        startActivity(intent);
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(user2ResOrderTmpSendRequest);
        } catch (Exception e) {
            Log.e("USER_REGISTER", e.toString());
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.ok_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ok_btn:
                if (TextUtils.isEmpty(phoneNumEt.getText().toString())) {
                    ToastUtil.showMessage("请输入客户手机号码");
                } else {
                    user_wx_band_phone();
                }
                break;
        }
    }
}
