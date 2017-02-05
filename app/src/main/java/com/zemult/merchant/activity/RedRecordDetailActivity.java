package com.zemult.merchant.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.ImageManager;

import butterknife.Bind;
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
    public static String INTENT_INFO="intent";
    M_Bill m;
    protected ImageManager mImageManager;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_redrecorddetail);
    }


    @Override
    public void init() {
        lhTvTitle.setText("记录详情");
        mImageManager = new ImageManager(this);
        m= (M_Bill) getIntent().getSerializableExtra(INTENT_INFO);
        tvMoney.setText("+" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
        imageManager.loadCircleImage(m.userHead,ivUserHead);
        tvUserName.setText(m.userName);
        tvTradeNumber.setText(m.number);
        tvPayTime.setText(m.createtime);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_user_head})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_user_head:
                Intent it = new Intent(this, UserDetailActivity.class);
                it.putExtra(UserDetailActivity.USER_ID,m.userId);
                it.putExtra(UserDetailActivity.USER_NAME,m.userName);
                it.putExtra(UserDetailActivity.USER_HEAD,m.userHead);
                startActivity(it);
                break;
        }
    }
}
