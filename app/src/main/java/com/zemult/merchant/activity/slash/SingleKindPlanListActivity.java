package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.HomeMoodAdapter;
import com.zemult.merchant.aip.slash.ManagerSearchnewsListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.FilterData;
import com.zemult.merchant.model.FilterEntity;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.model.apimodel.APIM_ManagerSearchnewsList;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.FilterView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 *
 */
public class SingleKindPlanListActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {
    public static final String INDUSTRY_ID = "industryId";
    public static final String INDUSTRY_NAME = "industryName";

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.fv_filter)
    FilterView fvFilter;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.ll_back)
    LinearLayout llBack;

    private Context mContext;
    private Activity mActivity;
    private int mScreenHeight; // 屏幕高度
    private int topbarHeight = 48; // topbar高度
    private int filterHright = 39; // filter高度

    private List<M_News> travelingList = new ArrayList<>(); // ListView数据

    private FilterData filterData; // 筛选数据
    private HomeMoodAdapter mAdapter; // 主页数据

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private int sort = 0; // 排序方式(0:发布时间,1:人气-点赞数,2:距离)
    private int sex = -1; // 性别 (0:男,1:女,-1:全部)
    private int distance = -1; // 距离(-1:不限,)单位:米,例：5公里以内--5000
    private int page = 1;
    private int industryId = -1; //行业id(为-1时 表示全行业)
    private String industryName;

    ManagerSearchnewsListRequest manager_searchnewsListrequest;
    public static final String TAG = "SingleKindPlanListActivity";
    private int selectedNewsPos;
    private M_News selectedNews;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_single_kind_plan_list);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

        showPd();
        getNetworkData(false);
    }

    private void initData() {
        industryId = getIntent().getIntExtra(INDUSTRY_ID, -1);
        industryName = getIntent().getStringExtra(INDUSTRY_NAME);
        mContext = this;
        mActivity = this;
        mScreenHeight = DensityUtil.getWindowHeight(mActivity);

        // 筛选数据
        filterData = new FilterData();
        filterData.setSorts(ModelUtil.getSortData());
    }

    private void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setText(industryName);

        // 设置筛选数据
        fvFilter.setFilterData(mActivity, filterData);

        mAdapter = new HomeMoodAdapter(mActivity, travelingList);
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        mAdapter.setOnPlanDetailClickListener(new HomeMoodAdapter.OnPlanDetailClickListener() {
            @Override
            public void onPlanDetailClick(int position) {
                M_News news = mAdapter.getItem(position);
                selectedNews = news;
                selectedNewsPos = position;
                Intent intent = new Intent(mContext, MoodDetailActivity.class);
                intent.putExtra(MoodDetailActivity.INTENT_NEWS, news);
                startActivity(intent);
            }
        });
        mAdapter.setOnUserDetailClickListener(new HomeMoodAdapter.OnUserDetailClickListener() {
            @Override
            public void onUserDetailClick(int position) {
                int userId = mAdapter.getItem(position).userId;
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, userId);
                startActivity(intent);
            }
        });
        // (真正的)筛选视图点击
        fvFilter.setOnFilterClickListener(new FilterView.OnFilterClickListener() {
            @Override
            public void onFilterClick(int position) {
                fvFilter.showFilterLayout(position);
            }
        });

        // 排序Item点击
        fvFilter.setOnItemSortClickListener(new FilterView.OnItemSortClickListener() {
            @Override
            public void onItemSortClick(FilterEntity entity) {
                sort = entity.getIntValue();
                getNetworkData(false);
            }
        });

        // 筛选确定按钮点击
        fvFilter.setOnFilterSureClickListener(new FilterView.OnFilterSureClickListener() {
            @Override
            public void onFilterSureClick(int sexFrom, int distanceFrom) {
                sex = sexFrom;
                distance = distanceFrom;
                getNetworkData(false);
            }
        });

        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
    }

    /**
     * 访问网络接口
     * @param isLoadMore true 加载更多时调用 false 初始化时以及下拉刷新
     */
    private void getNetworkData(boolean isLoadMore) {
        manager_searchnewsList(isLoadMore);
    }

    //搜索方案列表
    private void manager_searchnewsList(final boolean isLoadMore) {
        if (manager_searchnewsListrequest != null) {
            manager_searchnewsListrequest.cancel();
        }

        ManagerSearchnewsListRequest.Input input = new ManagerSearchnewsListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.operateUserId = SlashHelper.userManager().getUserId();
        }
        input.city = Constants.CITYID;
        input.center = Constants.CENTER;
        input.industryId = industryId;
        input.sex = sex;// (0:男,1:女,-1:全部)
        input.distance = distance;//(-1:不限,)单位:米,例：5公里以内--5000
        input.industryName = "";
        input.merchantName = "";
        input.orderType = sort;//(0:发布时间,1:人气-点赞数,2:距离)
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();
        manager_searchnewsListrequest = new ManagerSearchnewsListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_ManagerSearchnewsList) response).status == 1) {
                    fillAdapter(((APIM_ManagerSearchnewsList) response).newsList, ((APIM_ManagerSearchnewsList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_ManagerSearchnewsList) response).info);
                }
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
                dismissPd();
            }
        });
        sendJsonRequest(manager_searchnewsListrequest);
    }

    // 填充数据  List<M_News>  List<M_News>
    private void fillAdapter(final List<M_News> list, int maxpage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            int height = mScreenHeight - DensityUtil.dip2px(mContext, topbarHeight + filterHright); // 标题栏高度 ＋ FilterView的高度
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

    /**
     * =================================================处理刷新请求===========================================================================
     */

}
