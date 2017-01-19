package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.view.DrawableCenterTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/12/22.
 */
//我是约客
public class IamYuekeActivity extends BaseActivity {

    double experience;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.level_tv)
    TextView levelTv;
    @Bind(R.id.state_sp)
    Spinner stateSp;
    @Bind(R.id.tv_exp)
    TextView tvExp;
    @Bind(R.id.dis_exp_tv)
    TextView disExpTv;
    @Bind(R.id.rll_child)
    RoundLinearLayout rllChild;
    @Bind(R.id.rll)
    RoundLinearLayout rll;
    @Bind(R.id.level_ll)
    LinearLayout levelLl;
    @Bind(R.id.left_level)
    TextView leftLevel;
    @Bind(R.id.right_level)
    TextView rightLevel;
    @Bind(R.id.read_tv)
    TextView readTv;
    @Bind(R.id.zhanghu_tv)
    DrawableCenterTextView zhanghuTv;
    @Bind(R.id.merchantmanage_tv)
    DrawableCenterTextView merchantmanageTv;
    @Bind(R.id.consumermanage_tv)
    DrawableCenterTextView consumermanageTv;
    @Bind(R.id.servicehis_tv)
    DrawableCenterTextView servicehisTv;
    @Bind(R.id.yuyuehis_tv)
    DrawableCenterTextView yuyuehisTv;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_iamhousekeeper);
    }


    @Override
    public void init() {
        lhTvTitle.setText("我是服务管家");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


//    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.service_rl, R.id.busmanage_rl, R.id.customanage_rl})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.lh_btn_back:
//            case R.id.ll_back:
//                onBackPressed();
//                break;
//            case R.id.service_rl:
//                //服务记录
//                startActivity(new Intent(IamYuekeActivity.this, ServiceHistoryActivity.class));
//                break;
//            case R.id.busmanage_rl:
//                //商户管理
//                startActivity(new Intent(IamYuekeActivity.this, SaleManageActivity.class));
//                break;
//            case R.id.customanage_rl:
//                //客户管理
//                startActivity(new Intent(IamYuekeActivity.this, CustomManageActivity.class));
//                break;
//        }
//    }


}
