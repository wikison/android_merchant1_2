package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2016/12/22.
 */
//我是约客
public class IamYuekeActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    @Bind(R.id.service_rl)
    RelativeLayout serviceRl;
    @Bind(R.id.busmanage_rl)
    RelativeLayout busmanageRl;
    @Bind(R.id.customanage_rl)
    RelativeLayout customanageRl;
    @Bind(R.id.level_iv)
    ImageView levelIv;
    @Bind(R.id.level_tv)
    TextView levelTv;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.money_tv)
    TextView moneyTv;

    @Bind(R.id.distance_tv)
    TextView distanceTv;
    double experience;
    @Bind(R.id.progress_bar1)
    ProgressBar progressBar1;
    @Bind(R.id.progress_bar2)
    ProgressBar progressBar2;
    @Bind(R.id.progress_bar3)
    ProgressBar progressBar3;
    @Bind(R.id.progress_bar4)
    ProgressBar progressBar4;


    @Override
    public void setContentView() {
        setContentView(R.layout.iamyueke_activity);
    }


    @Override
    public void init() {
        lhTvTitle.setText("我是约客");
        nameTv.setText(SlashHelper.userManager().getUserinfo().getName());
        experience = SlashHelper.userManager().getUserinfo().getExperience();
        moneyTv.setText("" + Convert.getMoneyString(experience));


        if (experience == 0) {
            levelTv.setText("新手约客");
            levelIv.setBackgroundResource(R.mipmap.xinshou_icon2);

            distanceTv.setText("还差1经验值升级为铜牌会员");
        } else if (experience >= 1 && experience <= 9999) {
            levelTv.setText("铜牌约客");
            progressBar1.setProgress((int) experience);
            levelIv.setBackgroundResource(R.mipmap.tongpai_icon);
            int dis = (int) (10000 - experience);

            distanceTv.setText("还差" + dis + "经验值升级为银牌会员");
        } else if (experience >= 10000 && experience <= 99999) {
            levelTv.setText("银牌约客");
            levelIv.setBackgroundResource(R.mipmap.yin_icon);
            int dis = (int) (100000 - experience);

            progressBar1.setProgress(9999);
            int e = (int) ((experience - 9999) / (99999 - 9999));
            progressBar2.setProgress(e);

            distanceTv.setText("还差" + dis + "经验值升级为金牌会员");
        } else if (experience >= 100000 && experience <= 999999) {
            levelTv.setText("金牌约客");

            progressBar1.setProgress(9999);
            progressBar2.setProgress(99999);
            int e = (int) ((experience - 99999) / (999999 -99999));
            progressBar2.setProgress(e);

            levelIv.setBackgroundResource(R.mipmap.jingpai_icon);
            int dis = (int) (1000000 - experience);
            distanceTv.setText("还差" + dis + "经验值升级为钻石会员");
        } else if (experience >= 1000000) {
            levelTv.setText("钻石约客");

            progressBar1.setProgress(9999);
            progressBar2.setProgress(99999);
            progressBar3.setProgress(999999);
            progressBar4.setProgress(1000000);

            levelIv.setBackgroundResource(R.mipmap.demon_icon);
            distanceTv.setText("恭喜成为钻石会员~");
        }

    }



    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.service_rl, R.id.busmanage_rl, R.id.customanage_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.service_rl:
                //服务记录
                startActivity(new Intent(IamYuekeActivity.this, ServiceHistoryActivity.class));
                break;
            case R.id.busmanage_rl:
                //商户管理
                startActivity(new Intent(IamYuekeActivity.this, SaleManageActivity.class));
                break;
            case R.id.customanage_rl:
                //客户管理
                startActivity(new Intent(IamYuekeActivity.this, CustomManageActivity.class));
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
