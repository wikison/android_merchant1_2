package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.UIUtil;
import com.zemult.merchant.view.DrawableCenterTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        initdata();

    }

    private void initdata() {
        experience= SlashHelper.userManager().getUserinfo().getExperience();

       if(experience<100){
           levelTv.setText("新手服务管家");
       }else if(experience>=100&&experience<100000){
           levelTv.setText("铜牌服务管家");
       }else if(experience>=100000&&experience<1000000){
           levelTv.setText("金牌服务管家");
       }



//        if(experience> 0){
//            int allWidth = DensityUtil.dip2px(this, UIUtil.getWindowsWidth(this)-32);
//            float itemWidth = (float) allWidth / info.nextEXP;
//
//            RoundLinearLayout.LayoutParams lp = (RoundLinearLayout.LayoutParams) rllChild.getLayoutParams();
//            lp.width = (int) (itemWidth * info.experience);
//            rllChild.setLayoutParams(lp);
//        }

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.read_tv, R.id.zhanghu_tv, R.id.merchantmanage_tv, R.id.consumermanage_tv, R.id.servicehis_tv, R.id.yuyuehis_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.read_tv:

                break;
            case R.id.zhanghu_tv:
                //商户管理
                startActivity(new Intent(IamYuekeActivity.this, MyWalletActivity.class));
                break;
            case R.id.merchantmanage_tv:
                //商户管理
                startActivity(new Intent(IamYuekeActivity.this, SaleManageActivity.class));
                break;
            case R.id.consumermanage_tv:
                //客户管理
                startActivity(new Intent(IamYuekeActivity.this, CustomManageActivity.class));
                break;
            case R.id.servicehis_tv:
                //服务记录
               startActivity(new Intent(IamYuekeActivity.this, ServiceHistoryActivity.class));
                break;
            case R.id.yuyuehis_tv:
                Intent it = new Intent(this,MyAppointmentActivity.class);
                it.putExtra(MyAppointmentActivity.INTENT_TYPE,1);
                startActivity(it);
                break;
        }
    }



}
