package com.zemult.merchant.activity.slash;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.SearRoleActivity;
import com.zemult.merchant.activity.role.RoleClassifyFragment;
import com.zemult.merchant.adapter.multipleroles.DiscoverRecommendFragmentAdapter;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.fragment.GuessYouLikeFragment;
import com.zemult.merchant.fragment.RecommendRoleFragment;
import com.zemult.merchant.util.IntentUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2016/9/1.
 */
//探索推荐
public class DiscoverRecommendActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tablayout)
    TabLayout tablayout;
    @Bind(R.id.mViewPager)
    ViewPager mViewPager;
    @Bind(R.id.rolsear_et_search)
    TextView rolsearEtSearch;
    private DiscoverRecommendFragmentAdapter discoverRecommendFragmentAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private RecommendRoleFragment recommendRoleFragment;   //(猜你喜欢)
    private GuessYouLikeFragment guessYouLikeFragment;  //平台推荐
    private RoleClassifyFragment roleClassifyFragment;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_discoverrecommend);
    }

    @Override
    public void init() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("斜杠角色");


        recommendRoleFragment = new RecommendRoleFragment();
        guessYouLikeFragment = new GuessYouLikeFragment();
        roleClassifyFragment = new RoleClassifyFragment();

        mFragments.add(guessYouLikeFragment);
        mFragments.add(recommendRoleFragment);
        mFragments.add(roleClassifyFragment);

        discoverRecommendFragmentAdapter = new DiscoverRecommendFragmentAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(discoverRecommendFragmentAdapter);
        mViewPager.setOffscreenPageLimit(3);
        tablayout.setupWithViewPager(mViewPager);

    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rolsear_et_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:


                onBackPressed();
                break;

            case R.id.rolsear_et_search:
                IntentUtil.start_activity(DiscoverRecommendActivity.this, SearRoleActivity.class);
                break;
        }
    }


}
