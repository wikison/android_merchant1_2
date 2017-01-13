package com.zemult.merchant.activity.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.MoodDetailActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.search.SearchNewsAdapter;
import com.zemult.merchant.aip.slash.ManagerSearchnewsListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.FilterData;
import com.zemult.merchant.model.FilterEntity;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.model.apimodel.APIM_ManagerSearchnewsList;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.FilterView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by wikison on 2016/6/15.
 */
public class SearchRoleFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {
    public static final String TAG = "SearchRoleFragment";
    private static SearchRoleFragment instance;

    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.fv_top_filter)
    FilterView fvFilter;
    @Bind(R.id.ll_no_data)
    LinearLayout mLinearLayoutNoData;

    ManagerSearchnewsListRequest manager_searchnewsListrequest;
    private Context mContext;
    private Activity mActivity;
    private int sort = 0; // 排序方式(0:发布时间,1:人气-点赞数,2:距离)
    private int sex = -1; // 性别 (0:男,1:女,-1:全部)
    private int distance = -1; // 距离(-1:不限,)单位:米,例：5公里以内--5000
    private int page = 1;
    private int industry_id = -1; //行业id(为-1时 表示全行业)
    private List<M_News> listNews = new ArrayList<>(); // ListView数据
    private FilterData filterData; // 筛选数据
    private SearchNewsAdapter mAdapter; // 主页数据
    private String strOld="";
    boolean isNewSearch = false, isFilter = false;
    private  String strSearch="";
    private boolean isF=false, isLoadMore=false;

    public static SearchRoleFragment getInstance() {
        if (instance == null) {
            instance = new SearchRoleFragment();
        }
        return instance;
    }

    public static void init() {
        instance = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
        initListener();
    }

    private void initView() {
        // 设置筛选数据
        fvFilter.setFilterData(mActivity, filterData);

        mAdapter = new SearchNewsAdapter(mActivity, listNews);
        smoothListView.setAdapter(mAdapter);
    }

    public void initData() {
        mContext = getActivity();
        mActivity = getActivity();

        filterData = new FilterData();
        filterData.setSorts(ModelUtil.getSortData());
    }

    private void initListener() {
        mAdapter.setOnPlanDetailClickListener(new SearchNewsAdapter.OnPlanDetailClickListener() {
            @Override
            public void onPlanDetailClick(int position) {
                M_News news = mAdapter.getItem(position);
                Intent intent = new Intent(mContext, MoodDetailActivity.class);
                intent.putExtra(MoodDetailActivity.INTENT_NEWS, news);
                startActivity(intent);
            }
        });
        mAdapter.setOnUserDetailClickListener(new SearchNewsAdapter.OnUserDetailClickListener() {
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
                getSearchRoleList(strOld, false, true);

            }
        });

        // 筛选确定按钮点击
        fvFilter.setOnFilterSureClickListener(new FilterView.OnFilterSureClickListener() {
            @Override
            public void onFilterSureClick(int sexFrom, int distanceFrom) {
                sex = sexFrom;
                distance = distanceFrom;
                getSearchRoleList(strOld, false, true);

            }
        });

        smoothListView.setRefreshEnable(false);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
    }

    @Override
    protected void lazyLoad() {
        isFilter = isF;
        if (isF) {
            isNewSearch = true;
            strOld = strSearch;
            page = 1;
            doRequest();
        } else {
            if (strSearch.equals(strOld)) {
                isNewSearch = false;
                if (isLoadMore) {
                    doRequest();
                }
            } else {
                isNewSearch = true;
                strOld = strSearch;
                page = 1;
                doRequest();
            }
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //获取用户列表
    public void getSearchRoleList(String strSearch, boolean isLoadMore, boolean isF) {
        this.strSearch=strSearch;
        this.isLoadMore=isLoadMore;
        this.isF=isF;

        onVisible();

/*        isFilter = isF;
        if (isF) {
            isNewSearch = true;
            strOld = strSearch;
            page = 1;
            doRequest();
        } else {
            if (strSearch.equals(strOld)) {
                isNewSearch = false;
                if (isLoadMore) {
                    doRequest();
                }
            } else {
                isNewSearch = true;
                strOld = strSearch;
                page = 1;
                doRequest();
            }
        }*/
    }

    private void doRequest() {
        if (manager_searchnewsListrequest != null) {
            manager_searchnewsListrequest.cancel();
        }

        showPd();

        ManagerSearchnewsListRequest.Input input = new ManagerSearchnewsListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.operateUserId = SlashHelper.userManager().getUserId();
        }
        input.city = Constants.CITYID;
        input.center = Constants.CENTER;
        input.industryId = industry_id;
        input.sex = sex;// (0:男,1:女,-1:全部)
        input.distance = distance;//(-1:不限,)单位:米,例：5公里以内--5000
        input.industryName = strOld;
        input.merchantName = "";
        input.orderType = sort;//(0:发布时间,1:人气-点赞数,2:距离)
        input.page = page;
        input.rows = Constants.ROWS;

        input.convertJosn();
        manager_searchnewsListrequest = new ManagerSearchnewsListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
                dismissPd();
                if (((APIM_ManagerSearchnewsList) response).status == 1) {
                    fillAdapter(((APIM_ManagerSearchnewsList) response).newsList, ((APIM_ManagerSearchnewsList) response).maxpage);
                } else {
                    ToastUtils.show(mContext, ((APIM_ManagerSearchnewsList) response).info);
                }
            }
        });
        sendJsonRequest(manager_searchnewsListrequest);
    }


    // 填充数据  List<M_News>  List<M_News>
    private void fillAdapter(final List<M_News> list, int maxpage) {
        if (list == null || list.size() == 0) {
            mLinearLayoutNoData.setVisibility(View.VISIBLE);
            mAdapter.setDataNoFill(list, false, isNewSearch);
            smoothListView.setLoadMoreEnable(false);
        } else {
            mLinearLayoutNoData.setVisibility(View.INVISIBLE);
            if (isNewSearch || isFilter) {
                smoothListView.setLoadMoreEnable(page < maxpage);
                mAdapter.setDataNoFill(list, true, true);
            } else {
                smoothListView.setLoadMoreEnable(page < maxpage);
                mAdapter.setDataNoFill(list, true, false);
            }

        }
    }

    @Override
    public void onRefresh() {
//        page = 1;
//        getSearchRoleList(true);
    }

    @Override
    public void onLoadMore() {
        ++page;
        getSearchRoleList(strOld, true, false);

    }
}
