package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.AllSlashAdapter;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.mvp.presenter.UserIndustryPresenter;
import com.zemult.merchant.mvp.view.IUserIndustryView;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;

/**
 * 0163用户斜杠列表
 */
public class AllSlashActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener, IUserIndustryView {

    public static final String INTENT_USERID = "userId";
    public static final String INTENT_SELECTED_INDUSTRYS = "industryList";

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.tv_right)
    TextView tvRight;
    private Context mContext;
    private AllSlashAdapter adapter;

    private int userId;
    private List<M_Industry> selectedIndustrys;
    private int page = 1;

    private UserIndustryPresenter presenter;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_all_slash);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

        getNetworkData(false);
    }

    private void initData() {
        mContext = this;
        presenter = new UserIndustryPresenter(listJsonRequest, this);
        userId = getIntent().getIntExtra(INTENT_USERID, 0);
        selectedIndustrys = (List<M_Industry>) getIntent().getSerializableExtra(INTENT_SELECTED_INDUSTRYS);
    }

    private void initView() {
        if (selectedIndustrys != null) {
            userId = SlashHelper.userManager().getUserId();
            lhTvTitle.setText("探索角色");
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setText("确定");
        } else{
            if(userId == SlashHelper.userManager().getUserId())
                lhTvTitle.setText("我的角色");
            else
                lhTvTitle.setText("TA的角色");
        }


        adapter = new AllSlashAdapter(this, new ArrayList<M_Industry>());
        smoothListView.setAdapter(adapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

        if (selectedIndustrys != null)
            smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(adapter.getItem(position -1).isSelected())
                        adapter.getItem(position -1).setSelected(false);
                    else
                        adapter.getItem(position -1).setSelected(true);

                    adapter.notifyDataSetChanged();
                }
            });
    }

    /**
     * @param isLoadMore true 加载更多时调用 false 初始化时以及下拉刷新
     */
    private void getNetworkData(boolean isLoadMore) {
        showPd();
        // 获取用户的经营角色列表
        presenter.getUserIndustryList(userId, page = 1, Constants.ROWS, false);
    }

    @Override
    public void onRefresh() {
        presenter.getUserIndustryList(userId, page = 1, Constants.ROWS, false);
    }

    @Override
    public void onLoadMore() {
        presenter.getUserIndustryList(userId, ++page, Constants.ROWS, true);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_right:
                selectedIndustrys.clear();
                for(M_Industry industry : adapter.getData()){
                    if(industry.isSelected())
                        selectedIndustrys.add(industry);
                }

                Intent intent = new Intent();
                intent.putExtra(INTENT_SELECTED_INDUSTRYS, (Serializable)selectedIndustrys);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void showError(String error) {
        ToastUtils.show(mContext, error);
    }

    @Override
    public void setUserIndustryList(List<M_Industry> list, boolean isLoadMore, int maxpage) {
        if (list != null && !list.isEmpty()) {
            smoothListView.setLoadMoreEnable(page < maxpage);

            if(selectedIndustrys != null && !selectedIndustrys.isEmpty())
                for(M_Industry industry : list){
                    for(M_Industry selectedIndustry : selectedIndustrys){
                        if(selectedIndustry.industryId == industry.industryId)
                            industry.setSelected(true);
                    }
                }
            adapter.setData(list, isLoadMore);
        }
    }

    @Override
    public void stopRefreshOrLoad() {
        dismissPd();
        smoothListView.stopRefresh();
        smoothListView.stopLoadMore();
    }
}
