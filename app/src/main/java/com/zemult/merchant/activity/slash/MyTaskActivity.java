package com.zemult.merchant.activity.slash;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.MyTaskFragmentAdapter;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.UserFilterView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Wikison on 2016/7/28.
 */
public class MyTaskActivity extends BaseActivity {

    public int industryId = -1;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.tab)
    TabLayout tabs;
    @Bind(R.id.vp_my_task)
    ViewPager vpMyTask;
    @Bind(R.id.fv)
    UserFilterView fv;
    @Bind(R.id.ll_filter)
    LinearLayout llFilter;
    int userId = -1;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_task);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {
        userId = SlashHelper.userManager().getUserId();
        fv.setUserId(userId);
        lhTvTitle.setText(getResources().getString(R.string.title_my_task));

    }

    private void initView() {
        MyTaskFragmentAdapter adapter = new MyTaskFragmentAdapter(getSupportFragmentManager(), this);
        vpMyTask.setAdapter(adapter);
        vpMyTask.setOffscreenPageLimit(4);
        tabs.setupWithViewPager(vpMyTask);
    }

    private void initListener() {
        vpMyTask.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                vpMyTask.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        fv.setOnItemClickListener(new UserFilterView.OnItemClickListener() {
            @Override
            public void onItemClick(int id) {
                industryId = id;

                Intent intent = new Intent(Constants.BROCAST_FRESHTASKLIST);
                intent.putExtra("industry_id", industryId);
                sendBroadcast(intent);

            }
        });
    }

    @OnClick({R.id.ll_back, R.id.lh_btn_back, R.id.lh_btn_right, R.id.ll_filter})
    public void Onclick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                this.finish();
                break;
            case R.id.lh_btn_right:
                IntentUtil.start_activity(MyTaskActivity.this, MyPublishTaskActivity.class);
                break;
            case R.id.ll_filter:
                fv.show();
                break;
        }
    }

}
