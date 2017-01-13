package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.AllRecordAdapter;
import com.zemult.merchant.aip.mine.ManagerNewsDelRequest;
import com.zemult.merchant.aip.slash.UserNewsListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.model.apimodel.APIM_ManagerSearchnewsList;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.FilterViewHasTwoListViewView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 0165查看全部记录
 */
public class AllRecordActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
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
    @Bind(R.id.fv_filter)
    FilterViewHasTwoListViewView fvFilter;

    private Context mContext;
    private Activity mActivity;

    private List<M_News> travelingList = new ArrayList<>(); // ListView数据

    private AllRecordAdapter mAdapter; // 主页数据

    private int userId;
    private int page = 1;
    private int industryId = -1; // 角色id(-1表示全部角色)
    private UserNewsListRequest userNewsListRequest; // 获取用户的方案列表(角色/场景/时间倒排序)

    public static final String TAG = "AllRecordActivity";
    private int selectedNewsPos;
    private M_News selectedNews;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_all_record);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

        getNetworkData(false);
    }

    private void initData() {
        userId = getIntent().getIntExtra("userId", 0);
        mContext = this;
        mActivity = this;
    }

    private void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setText(getResources().getString(R.string.title_all_record));

        // 设置筛选数据
        fvFilter.setFilterData(userId);

        mAdapter = new AllRecordAdapter(mActivity, travelingList, userId);
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        // 筛选视图点击
        fvFilter.setOnFilterClickListener(new FilterViewHasTwoListViewView.OnFilterClickListener() {
            @Override
            public void onFilterClick() {
                fvFilter.showFilterLayout();
            }
        });

        fvFilter.setOnItemCategoryClickListener(new FilterViewHasTwoListViewView.OnItemCategoryClickListener() {
            @Override
            public void onItemCategoryClick(int industryid, int merchantid) {
                industryId = industryid;
                getNetworkData(false);
            }
        });

        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

        // 方案详情
        mAdapter.setOnPlanDetailClickListener(new AllRecordAdapter.OnPlanDetailClickListener() {
            @Override
            public void onPlanDetailClick(int position) {
                selectedNewsPos = position;
                selectedNews = mAdapter.getItem(position);

                M_News news = mAdapter.getItem(position);
                Intent intent = new Intent(mContext, MoodDetailActivity.class);
                intent.putExtra(MoodDetailActivity.INTENT_NEWS, news);
                startActivity(intent);
            }
        });
        // 方案删除
        mAdapter.setOnDelClickListener(new AllRecordAdapter.OnDelClickListener() {
            @Override
            public void onDelClick(final int position) {
                CommonDialog.showDialogListener(mContext, null,"否", "是", "是否删除方案", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                        // 用户删除自己的方案
                        manager_news_del(position);
                    }
                });
            }
        });
    }

    private ManagerNewsDelRequest delRequest;
    private void manager_news_del(final int position){
        if (delRequest != null) {
            delRequest.cancel();
        }
        ManagerNewsDelRequest.Input input = new ManagerNewsDelRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.newsId = mAdapter.getItem(position).newsId;

        input.convertJosn();

        delRequest = new ManagerNewsDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    mAdapter.delOneRecord(position);
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(delRequest);
    }


    /**
     * 访问网络接口
     *
     * @param isLoadMore true 加载更多时调用 false 初始化时以及下拉刷新
     */
    private void getNetworkData(final boolean isLoadMore) {
        getUserNewsList(isLoadMore);
    }

    /**
     * 获取用户的方案列表(角色/场景/时间倒排序)
     */
    private void getUserNewsList(final boolean isLoadMore) {
        if (userNewsListRequest != null) {
            userNewsListRequest.cancel();
        }
        UserNewsListRequest.Input input = new UserNewsListRequest.Input();
        input.userId = userId;
        input.industryId = industryId; // 角色id(-1表示全部角色)
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        userNewsListRequest = new UserNewsListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_ManagerSearchnewsList) response).status == 1) {
                    setUserNewsList(((APIM_ManagerSearchnewsList) response).newsList,
                            ((APIM_ManagerSearchnewsList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_ManagerSearchnewsList) response).info);
                }
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }
        });
        sendJsonRequest(userNewsListRequest);
    }

    /**
     * 设置TA的记录
     */
    private void setUserNewsList(List<M_News> list, int maxpage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            int height = DensityUtil.getWindowHeight(mActivity) - DensityUtil.dip2px(mContext, 48 + 39); // 标题栏高度 ＋ FilterView的高度
            mAdapter.setData(ModelUtil.getNoDataEntity(height), false);
            smoothListView.setLoadMoreEnable(false);
        } else {
            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setData(list, isLoadMore);
        }
    }

    @Override
    public void onRefresh() {
        getNetworkData(false);
    }

    @Override
    public void onLoadMore() {
        getNetworkData(true);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }
}
