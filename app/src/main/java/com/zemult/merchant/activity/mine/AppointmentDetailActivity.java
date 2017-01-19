package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/1/19.
 */

public class AppointmentDetailActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.object_tv)
    TextView objectTv;
    @Bind(R.id.head_iv)
    ImageView headIv;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.shop_tv)
    TextView shopTv;
    @Bind(R.id.pernumber_tv)
    TextView pernumberTv;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_extra)
    TextView tvExtra;
    @Bind(R.id.tv_contacter)
    TextView tvContacter;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.appresult_tv)
    TextView appresultTv;
    @Bind(R.id.ordernum_tv)
    TextView ordernumTv;
    @Bind(R.id.havefinished_rl)
    RelativeLayout havefinishedRl;
    @Bind(R.id.mis_bt)
    Button misBt;
    @Bind(R.id.invite_btn)
    Button inviteBtn;
    @Bind(R.id.jiezhang_btn)
    Button jiezhangBtn;
    @Bind(R.id.ordersuccess_btn_rl)
    RelativeLayout ordersuccessBtnRl;
    @Bind(R.id.others_ll)
    LinearLayout othersLl;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_appointmentdetail);
    }

    @Override
    public void init() {
        lhTvTitle.setText("预约详情");
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.head_iv, R.id.mis_bt, R.id.invite_btn, R.id.jiezhang_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.head_iv:
                break;
            case R.id.mis_bt:
                break;
            case R.id.invite_btn:
                break;
            case R.id.jiezhang_btn:
                break;
        }
    }
}
