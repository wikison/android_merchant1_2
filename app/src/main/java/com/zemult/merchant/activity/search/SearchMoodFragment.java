package com.zemult.merchant.activity.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.MoodDetailActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.HomeMoodAdapter;
import com.zemult.merchant.aip.slash.ManagerNewsListSearchRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.model.apimodel.APIM_ManagerSearchnewsList;
import com.zemult.merchant.mvp.presenter.MoodDetailPresenter;
import com.zemult.merchant.mvp.view.IMoodDetailView;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 搜索vp的子fragment——心情小记
 *
 * @author djy
 * @time 2016/8/3 9:45
 */
public class SearchMoodFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener, IMoodDetailView {
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    public static final String TAG = SearchMoodFragment.class.getSimpleName();
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;

    private ManagerNewsListSearchRequest request;
    private int page = 1;

    private HomeMoodAdapter mAdapter; // 主页数据

    private Context mContext;
    private Activity mActivity;

    private int selectedNewsPos;
    private M_News selectedNews;
    private String key;

    private static final int REQ_MOOD_DETAIL = 0x110;
    private MoodDetailPresenter moodDetailPresenter;
    private boolean hasLoaded;

    @Override
    protected void lazyLoad() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_child, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        mContext = getActivity();
        mActivity = getActivity();
        moodDetailPresenter = new MoodDetailPresenter(listJsonRequest, this);
    }

    private void initView() {
        mAdapter = new HomeMoodAdapter(mContext, new ArrayList<M_News>());
        smoothListView.setAdapter(mAdapter);
    }
    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

        // 方案详情
        mAdapter.setOnPlanDetailClickListener(new HomeMoodAdapter.OnPlanDetailClickListener() {
            @Override
            public void onPlanDetailClick(int position) {
                selectedNewsPos = position;
                selectedNews = mAdapter.getItem(position);

                M_News news = mAdapter.getItem(position);
                Intent intent = new Intent(mContext, MoodDetailActivity.class);
                intent.putExtra(MoodDetailActivity.INTENT_NEWS, news);
                startActivityForResult(intent, REQ_MOOD_DETAIL);
            }
        });
        // 用户详情
        mAdapter.setOnUserDetailClickListener(new HomeMoodAdapter.OnUserDetailClickListener() {
            @Override
            public void onUserDetailClick(int position) {
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, mAdapter.getItem(position).userId);
                intent.putExtra(UserDetailActivity.USER_NAME, mAdapter.getItem(position).userName);
                intent.putExtra(UserDetailActivity.USER_HEAD, mAdapter.getItem(position).userHead);
                intent.putExtra(UserDetailActivity.USER_SEX, mAdapter.getItem(position).sex);
                startActivity(intent);
            }
        });
    }

    public void search(String key) {
        if (key.equals(this.key))
            return;

        showPd();
        this.key = key;
        manager_searchnewsList(false);
    }

    //搜索方案列表
    public void manager_searchnewsList(final boolean isLoadMore) {
        if (request != null) {
            request.cancel();
        }
        ManagerNewsListSearchRequest.Input input = new ManagerNewsListSearchRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.operateUserId = SlashHelper.userManager().getUserId();
        }
        input.name = key;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();
        request = new ManagerNewsListSearchRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_ManagerSearchnewsList) response).status == 1) {
                    fillAdapter(((APIM_ManagerSearchnewsList) response).newsList,
                            ((APIM_ManagerSearchnewsList) response).maxpage,
                            isLoadMore);

                } else {
                    ToastUtils.show(getActivity(), ((APIM_ManagerSearchnewsList) response).info);
                }
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
                dismissPd();
            }
        });
        sendJsonRequest(request);
    }

    // 填充数据  List<M_News>  List<M_News>
    private void fillAdapter(List<M_News> list, int maxpage, boolean isLoadMore) {
        if (mAdapter == null) {
            initView();
            initListener();
        }
        if (list == null || list.size() == 0) {
            smoothListView.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
        } else {
            smoothListView.setVisibility(View.VISIBLE);
            rlNoData.setVisibility(View.GONE);

            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setDataSearch(list, isLoadMore);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        manager_searchnewsList(false);
    }

    @Override
    public void onLoadMore() {
        manager_searchnewsList(true);
    }

    /**
     * =================================处理刷新请求========================================
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == REQ_MOOD_DETAIL) {
                moodDetailPresenter.getMoodDetail(selectedNews.newsId);
            }
        }
    }

    @Override
    public void showError(String error) {
        ToastUtils.show(mContext, error);
    }

    @Override
    public void setMoodDetailInfo(M_News moodDetailInfo) {
        mAdapter.refreshOneRecord(moodDetailInfo, selectedNewsPos);
    }
}
