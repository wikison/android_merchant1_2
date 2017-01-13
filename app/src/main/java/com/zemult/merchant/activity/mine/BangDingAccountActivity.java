package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserBandcardInfoRequest;
import com.zemult.merchant.alipay.PayBangDingAccountActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.common.CommonDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class BangDingAccountActivity extends BaseActivity {

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
    @Bind(R.id.iv_left)
    ImageView ivLeft;
    @Bind(R.id.tv_myaccount)
    TextView tvMyaccount;
    @Bind(R.id.rel_zhifu)
    RelativeLayout relZhifu;
    UserBandcardInfoRequest userBandcardInfoRequest;
    int isBanded;
    @Bind(R.id.account_tv)
    TextView accountTv;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bang_ding_account);
    }

    @Override
    public void init() {
        lhTvTitle.setText("支付宝绑定");
    }

    @Override
    protected void onResume() {
        super.onResume();
        user_bandcard_info();
    }

    private void user_bandcard_info() {
        if (userBandcardInfoRequest != null) {
            userBandcardInfoRequest.cancel();
        }


        UserBandcardInfoRequest.Input input = new UserBandcardInfoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }

        input.convertJosn();
        userBandcardInfoRequest = new UserBandcardInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    isBanded = ((CommonResult) response).isBand;
                    if (isBanded == 0) {//是否已经绑定(0:否,1:是)
                        accountTv.setText("未绑定");
                    } else {
                        accountTv.setText(((CommonResult) response).alipayNumber);
                        Intent updateintent = new Intent(Constants.BROCAST_UPDATEMYINFO);
                        sendBroadcast(updateintent);
                    }

                } else {
                    ToastUtils.show(BangDingAccountActivity.this, ((CommonResult) response).info);
                }


            }
        });
        sendJsonRequest(userBandcardInfoRequest);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_tv_title, R.id.tv_myaccount, R.id.rel_zhifu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.lh_tv_title:
                break;
            case R.id.rel_zhifu:
            case R.id.tv_myaccount:
                if (isBanded == 0) {
                    CommonDialog.showDialogListener(BangDingAccountActivity.this,
                            null, "取消", "继续", "使用您要绑定的支付宝账号充值0.01元完成绑定，绑定仅用于提现，" +
                                    "账号一经绑定，不可更改", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CommonDialog.DismissProgressDialog();
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent_pay = new Intent(BangDingAccountActivity.this, PayBangDingAccountActivity.class);
                                    startActivity(intent_pay);
                                    CommonDialog.DismissProgressDialog();
                                }
                            });
                }
                break;
        }
    }


}
