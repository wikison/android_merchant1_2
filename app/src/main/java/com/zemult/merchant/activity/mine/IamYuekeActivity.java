package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserEditStateRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.UIUtil;
import com.zemult.merchant.view.DrawableCenterTextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

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


        experience = SlashHelper.userManager().getUserinfo().getExperience();
        tvExp.setText("经验值  " + experience + "exp");

        readTv.setText(Html.fromHtml("<u>查看详细服务等级规则</u>"));


//状态(0:空闲,1:休息,2:忙碌)
        if (SlashHelper.userManager().getUserinfo().getState() == 0) {
            stateSp.setSelection(0);

        } else if (SlashHelper.userManager().getUserinfo().getState() == 1) {
            stateSp.setSelection(1);
        } else if (SlashHelper.userManager().getUserinfo().getState() == 2) {
            stateSp.setSelection(2);
        }


        if (experience < 100) {
            double dis = 100 - experience;
            disExpTv.setText("距离铜牌服务管家还差" + dis + "exp");

            leftLevel.setText("新手");
            rightLevel.setText("铜牌");


            int allWidth = DensityUtil.dip2px(this, UIUtil.getWindowsWidth(this) - 32);
            float itemWidth = (float) allWidth / 100;
            RoundLinearLayout.LayoutParams lp = (RoundLinearLayout.LayoutParams) rllChild.getLayoutParams();
            lp.width = (int) (itemWidth * experience);
            rllChild.setLayoutParams(lp);


        } else if (experience >= 100 && experience < 10000) {
            double dis = 10000 - experience;
            disExpTv.setText("距离银牌服务管家还差" + dis + "exp");

            leftLevel.setText("铜牌");
            rightLevel.setText("银牌");


            int allWidth = DensityUtil.dip2px(this, UIUtil.getWindowsWidth(this) - 32);
            float itemWidth = (float) allWidth / 10000;
            RoundLinearLayout.LayoutParams lp = (RoundLinearLayout.LayoutParams) rllChild.getLayoutParams();
            lp.width = (int) (itemWidth * experience);
            rllChild.setLayoutParams(lp);

        } else if (experience >= 10000 && experience < 100000) {
            double dis = 100000 - experience;
            disExpTv.setText("距离金牌服务管家还差" + dis + "exp");

            leftLevel.setText("银牌");
            rightLevel.setText("金牌");

            int allWidth = DensityUtil.dip2px(this, UIUtil.getWindowsWidth(this) - 32);
            float itemWidth = (float) allWidth / 100000;
            RoundLinearLayout.LayoutParams lp = (RoundLinearLayout.LayoutParams) rllChild.getLayoutParams();
            lp.width = (int) (itemWidth * experience);
            rllChild.setLayoutParams(lp);


        } else if (experience >= 100000 && experience < 1000000) {
            double dis = 1000000 - experience;
            disExpTv.setText("距离钻石服务管家还差" + dis + "exp");

            leftLevel.setText("金牌");
            rightLevel.setText("钻石");

            int allWidth = DensityUtil.dip2px(this, UIUtil.getWindowsWidth(this) - 32);
            float itemWidth = (float) allWidth / 1000000;
            RoundLinearLayout.LayoutParams lp = (RoundLinearLayout.LayoutParams) rllChild.getLayoutParams();
            lp.width = (int) (itemWidth * experience);
            rllChild.setLayoutParams(lp);

        } else {
            disExpTv.setText("恭喜达到钻石会员~~~");

            RoundLinearLayout.LayoutParams lp = (RoundLinearLayout.LayoutParams) rllChild.getLayoutParams();
            lp.width = (int) (UIUtil.getWindowsWidth(this) - 32);
            rllChild.setLayoutParams(lp);
        }

        levelTv.setText(SlashHelper.userManager().getUserinfo().getExperienceText()+"服务管家");

    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.read_tv, R.id.zhanghu_tv, R.id.merchantmanage_tv, R.id.consumermanage_tv, R.id.servicehis_tv, R.id.yuyuehis_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.read_tv:
                IntentUtil.start_activity(this, BaseWebViewActivity.class,
                        new Pair<String, String>("titlename", "服务等级"), new Pair<String, String>("url", Constants.SERVICELEVEL));
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
                Intent it = new Intent(this, MyAppointmentActivity.class);
                it.putExtra(MyAppointmentActivity.INTENT_TYPE, 1);
                startActivity(it);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (SlashHelper.userManager().getUserinfo().getState() == stateSp.getSelectedItemPosition()) {
        } else {
            userEditState();
        }
    }

    UserEditStateRequest userEditStateRequest;

    private void userEditState() {
        if (userEditStateRequest != null) {
            userEditStateRequest.cancel();
        }
        UserEditStateRequest.Input input = new UserEditStateRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        if (stateSp.getSelectedItem().toString().equals("空闲")) {
            input.state = 0;
        } else if (stateSp.getSelectedItem().toString().equals("休息")) {
            input.state = 1;
        } else if (stateSp.getSelectedItem().toString().equals("忙碌")) {
            input.state = 2;
        }
        input.convertJosn();
        userEditStateRequest = new UserEditStateRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                } else {
                    ToastUtils.show(IamYuekeActivity.this, ((CommonResult) response).info);
                }

            }
        });
        sendJsonRequest(userEditStateRequest);
    }


}
