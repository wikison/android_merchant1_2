package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;

/**
 * 首页vp的子fragment——心情小记
 *
 * @author djy
 * @time 2016/7/25 10:20
 */
public class HomeMoodFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener, IMoodDetailView{
    @Bind(R.id.id_stickynavlayout_innerscrollview)
    SmoothListView smoothListView;
    public static final String TAG = HomeMoodFragment.class.getSimpleName();
    private ManagerNewsListSearchRequest request;
    private List<M_News> travelingList = new ArrayList<>();// ListView数据
    private int page = 1;

    private HomeMoodAdapter mAdapter; // 主页数据

    private Context mContext;
    private Activity mActivity;

    private int selectedNewsPos;
    private M_News selectedNews;
    private int noDataViewHeight;
    private boolean hasLoaded;

    public static final String MOOD_CALL_HOMEFRAGMENT_TO_COMPLETE_REFRESH = "mood_call_HomeFragment_completeRefresh";
    private static final int REQ_MOOD_DETAIL = 0x110;
    private MoodDetailPresenter moodDetailPresenter;

    @Override
    protected void lazyLoad() {
        if(!hasLoaded){
            hasLoaded = true;
            if(mAdapter == null){
                initView();
                initListener();
            }
            //获取列表数据
            showPd();
            manager_searchnewsList(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_child, container, false);
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

        noDataViewHeight = DensityUtil.getWindowHeight(mActivity) - DensityUtil.dip2px(mContext, 44 + 40 + 48); // 标题栏高度 ＋ tab的高度 + 底部选项卡的高度
    }

    private void initView() {
        mAdapter = new HomeMoodAdapter(mContext, travelingList);
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(false);
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

    //搜索方案列表
    public void manager_searchnewsList(final boolean isLoadMore) {
        if (request != null) {
            request.cancel();
        }

        ManagerNewsListSearchRequest.Input input = new ManagerNewsListSearchRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.operateUserId = SlashHelper.userManager().getUserId();
        }
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();
        request = new ManagerNewsListSearchRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                EventBus.getDefault().post(MOOD_CALL_HOMEFRAGMENT_TO_COMPLETE_REFRESH);
                smoothListView.stopLoadMore();
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
                EventBus.getDefault().post(MOOD_CALL_HOMEFRAGMENT_TO_COMPLETE_REFRESH);
                smoothListView.stopLoadMore();
                dismissPd();
            }
        });
        sendJsonRequest(request);
    }

    // 填充数据  List<M_News>  List<M_News>
    private void fillAdapter(List<M_News> list, int maxpage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            smoothListView.setLoadMoreEnable(false);
            mAdapter.setData(ModelUtil.getNoDataEntity(noDataViewHeight), false);
        } else {
            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setData(list, isLoadMore);
            if(!isLoadMore)
                smoothListView.setSelection(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public boolean canPtr(){
        View view = smoothListView.getChildAt(smoothListView.getFirstVisiblePosition());
        if(view != null)
            return view.getTop() == 0;
        else
            return false;
    }

    @Override
    public void onRefresh() {
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
        if(resultCode == -1){
            if(requestCode == REQ_MOOD_DETAIL){
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
