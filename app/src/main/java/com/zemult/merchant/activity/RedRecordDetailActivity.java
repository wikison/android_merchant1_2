package com.zemult.merchant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.alipay.taskpay.ChoosePayTypeActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.ImageManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/2/5.
 */
//红包记录
public class RedRecordDetailActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.iv_user_head)
    ImageView ivUserHead;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.tv_trade_number)
    TextView tvTradeNumber;
    @Bind(R.id.tv_pay_time)
    TextView tvPayTime;
    @Bind(R.id.ll_present)
    LinearLayout llPresent;
    public static String INTENT_INFO = "intent";
    public static String INTENT_FLAG = "flag";
    M_Bill m;
    int flag;
    protected ImageManager mImageManager;
    @Bind(R.id.from_tv)
    TextView fromTv;
    @Bind(R.id.rtv_to_pay)
    RoundTextView rtvToPay;
    int userPayId;

    @Override
    public void setContentView() {

        setContentView(R.layout.activity_redrecorddetail);
    }

    @Override
    public void init() {
        lhTvTitle.setText("消费单详情");
        mImageManager = new ImageManager(this);
        userPayId = getIntent().getIntExtra("userPayId", 0);
        m = (M_Bill) getIntent().getSerializableExtra(INTENT_INFO);
//        if (m.type==4||m.type==3) {
//            tvMoney.setText("-" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
//        } else {
//            tvMoney.setText("+" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
//        }
        flag = getIntent().getIntExtra(INTENT_FLAG, 0);

        if (flag == 1) {
            //来自消费单
            tvMoney.setText("-" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
            fromTv.setText("赠送对象");
            imageManager.loadCircleImage(m.toUserHead, ivUserHead);
            tvUserName.setText(m.toUserName);
            if(m.state==0){
                rtvToPay.setVisibility(View.VISIBLE);
                rtvToPay.setText("立即付款");
            }else{
                rtvToPay.setVisibility(View.GONE);
            }


        } else {
            tvMoney.setText("+" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
            fromTv.setText("来自");
            imageManager.loadCircleImage(m.userHead, ivUserHead);
            tvUserName.setText(m.userName);
        }
        tvTradeNumber.setText(m.number);
        tvPayTime.setText(m.createtime);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_user_head,R.id.rtv_to_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_user_head:
                Intent it = new Intent(this, UserDetailActivity.class);
                it.putExtra(UserDetailActivity.USER_ID, m.userId);
                it.putExtra(UserDetailActivity.USER_NAME, m.userName);
                it.putExtra(UserDetailActivity.USER_HEAD, m.userHead);
                startActivity(it);
                break;
            case R.id.rtv_to_pay:
                Intent intent = new Intent(this, ChoosePayTypeActivity.class);
                intent.putExtra("consumeMoney", m.payMoney);
                intent.putExtra("order_sn", m.number);
                intent.putExtra("userPayId", userPayId);
                intent.putExtra("merchantName","赞赏红包");
                intent.putExtra("merchantHead", m.merchantHead);
                startActivityForResult(intent, 1000);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                setResult(RESULT_OK);
                onBackPressed();
            }
        }
    }

}
