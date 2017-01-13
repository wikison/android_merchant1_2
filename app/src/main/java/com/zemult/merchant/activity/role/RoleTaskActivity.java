package com.zemult.merchant.activity.role;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.fragment.UserTaskFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 角色任务
 *
 * @author djy
 * @time 2016/10/26 16:29
 */
public class RoleTaskActivity extends BaseActivity{

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
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int industryId;
    private UserTaskFragment taskFragment;
    public static final String INTENT_INDUSTRYID = "industryId";


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_role_task);
    }

    @Override
    public void init() {
        industryId = getIntent().getIntExtra(INTENT_INDUSTRYID, -1);
        lhTvTitle.setText("角色任务");

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        taskFragment = new UserTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_INDUSTRYID, industryId);
        taskFragment.setArguments(bundle);

        transaction.replace(R.id.content, taskFragment);
        transaction.commit();
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_right, R.id.ll_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_right:
            case R.id.ll_right:

                break;
        }
    }



}
