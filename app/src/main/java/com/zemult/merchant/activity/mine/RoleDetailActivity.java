package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.PublishTaskActivity;
import com.zemult.merchant.aip.slash.ManagerGetmanagerinfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.fragment.UserPublishFragment;
import com.zemult.merchant.fragment.UserTaskFragment;
import com.zemult.merchant.model.apimodel.APIM_ManagerGetmanagerinfo;
import com.zemult.merchant.util.ColorUtil;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.MyStickyNavLayout;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 我的角色管理界面
 *
 * @author djy
 * @time 2016/8/9 16:54
 */
public class RoleDetailActivity extends BaseActivity implements MyStickyNavLayout.onStickStateChangeListener {

    @Bind(R.id.iv_head)

    ImageView ivHead;
    @Bind(R.id.tv_rank)
    TextView tvRank;
    @Bind(R.id.tv_level2)
    TextView tvLevel2;
    @Bind(R.id.rll_child)
    RoundLinearLayout rllChild;
    @Bind(R.id.rll)
    RoundLinearLayout rll;
    @Bind(R.id.tv_level3)
    TextView tvLevel3;
    @Bind(R.id.tv_experience)
    TextView tvExperience;
    @Bind(R.id.id_stickynavlayout_topview)
    LinearLayout idStickynavlayoutTopview;
    //    @Bind(R.id.tab)
//    TabLayout tabs;
    @Bind(R.id.id_stickynavlayout_indicator)
    LinearLayout idStickynavlayoutIndicator;
    @Bind(R.id.id_stickynavlayout_viewpager)
    ViewPager vp;
    @Bind(R.id.id_stick)
    MyStickyNavLayout stickyNavLayout;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.iv_more)
    ImageView ivMore;
    @Bind(R.id.rl_topbar)
    RelativeLayout rlTopbar;
    @Bind(R.id.floatActionButton)
    FloatingActionButton floatActionButton;

    private Context mContext;
    private int industryId;
    private UserTaskFragment taskFragment;
    private UserPublishFragment moodFragment;
    private ManagerGetmanagerinfoRequest getmanagerinfoRequest;

    public static final int REQ_ROLE_SET = 0x110;
    public static final int REQ_NEW_TASK = 0x120;
    public static final int REQ_NEW_MOOD = 0x130;
    public static final String INTENT_INDUSTRYID = "industryId";
    private APIM_ManagerGetmanagerinfo info;

    @Override
    public void setContentView() {
        setContentView(R.layout.layout_role_detail);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        manager_getmanagerinfo();
    }

    private void initData() {
        industryId = getIntent().getIntExtra(INTENT_INDUSTRYID, -1);
        mContext = this;
    }

    private void initView() {
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);
        //tabs.setupWithViewPager(vp);
        //vp.setOffscreenPageLimit(2);
    }

    private void initListener() {
        stickyNavLayout.setOnStickStateChangeListener(this);
    }

    // 用户 单角色的详情
    private void manager_getmanagerinfo() {
        showPd();
        if (getmanagerinfoRequest != null) {
            getmanagerinfoRequest.cancel();
        }
        final ManagerGetmanagerinfoRequest.Input input = new ManagerGetmanagerinfoRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.industryId = industryId;

        input.convertJosn();
        getmanagerinfoRequest = new ManagerGetmanagerinfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_ManagerGetmanagerinfo) response).status == 1) {
                    info = (APIM_ManagerGetmanagerinfo) response;
                    if (info != null)
                        setData();
                } else {
                    ToastUtils.show(mContext, ((APIM_ManagerGetmanagerinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(getmanagerinfoRequest);
    }

    private void setData() {
        if (!TextUtils.isEmpty(info.icon))
            imageManager.loadCircleImage(info.icon, ivHead);
        if (!TextUtils.isEmpty(info.name))
            tvName.setText(info.name);

        tvLevel2.setText("Lv." + info.level);
        tvLevel3.setText("Lv." + (info.level + 1));
        tvExperience.setText((info.nextEXP - info.experience) + "");
        tvRank.setText(info.place + "");

        if(info.nextEXP > 0){
            int allWidth = DensityUtil.dip2px(mContext, 120);
            float itemWidth = (float) allWidth / info.nextEXP;

            RoundLinearLayout.LayoutParams lp = (RoundLinearLayout.LayoutParams) rllChild.getLayoutParams();
            lp.width = (int) (itemWidth * info.experience);
            rllChild.setLayoutParams(lp);
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_more, R.id.floatActionButton})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_more:
                intent = new Intent(mContext, RoleSetActivity.class);
                intent.putExtra(RoleSetActivity.INTENT_ROLE_ID, industryId);
                intent.putExtra(RoleSetActivity.INTENT_ROLE_NAME, info.name);
                intent.putExtra(RoleSetActivity.INTENT_ROLE_ICON, info.icon);
                startActivityForResult(intent, REQ_ROLE_SET);
                break;
            case R.id.floatActionButton:

//                switch (vp.getCurrentItem()){
//                    case 0: // 任务
                        intent = new Intent(mContext, PublishTaskActivity.class);
                        startActivityForResult(intent, REQ_NEW_TASK);
                        break;
//                    case 1: // 心情
//                        intent = new Intent(mContext, RecordLifeActivity.class);
//                        intent.putExtra("industryId", industryId);
//                        intent.putExtra("industryName", tvName.getText().toString());
//                        startActivityForResult(intent, REQ_NEW_MOOD);
            //   break;
            //     }
            //        break;
        }
    }

    @Override
    public void isStick(boolean var1) {

    }

    @Override
    public void scrollPercent(float fraction) {
        rlTopbar.setBackgroundColor(ColorUtil.getNewColorByStartEndColor(mContext, fraction, R.color.transparent, R.color.bg_head));
    }

    class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        public final int COUNT = 1;
        private Fragment[] fms;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);

            Bundle bundle = new Bundle();
            bundle.putInt(INTENT_INDUSTRYID, industryId);

            taskFragment = new UserTaskFragment();
            //  moodFragment = new UserMoodFragment();
            taskFragment.setArguments(bundle);
            //  moodFragment.setArguments(bundle);

            fms = new Fragment[]{taskFragment};
        }

        @Override
        public Fragment getItem(int position) {
            return fms[position];
        }

        @Override
        public int getCount() {
            return COUNT;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQ_ROLE_SET:
                    onBackPressed();
                    break;
                case REQ_NEW_TASK:
                    manager_getmanagerinfo();
                    taskFragment.getNetworkData(false);
                    break;
                case REQ_NEW_MOOD:
                    manager_getmanagerinfo();
                    moodFragment.getNetworkData(false);
                    break;
            }
        }

    }
}
