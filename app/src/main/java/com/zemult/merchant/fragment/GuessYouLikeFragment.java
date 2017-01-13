package com.zemult.merchant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.adapter.multipleroles.GuessYouLikeAdapter;
import com.zemult.merchant.aip.slash.TaskIndustryRecordUserdoRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskSearchIndustryRecordList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/9/1.
 */
//探索推荐--猜你喜欢
public class GuessYouLikeFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {


    @Bind(R.id.roles_iv)
    SmoothListView rolesIv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    private int page = 1;
    private List<M_Task> datas = new ArrayList<M_Task>();
    TaskIndustryRecordUserdoRequest taskIndustryRecordUserdoRequest;
    GuessYouLikeAdapter guessYouLikeAdapter;
    private boolean hasLoaded ;

    @Override
    protected void lazyLoad() {
        if (!hasLoaded) {
            hasLoaded = true;
            showPd();
            getNetworkData(false);
        }

    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        // TODO Auto-generated method stub
//        if (isVisibleToUser) {
//            //fragment可见时加载数据
//
//            getNetworkData(false);
//        } else {
//            //不可见时不执行操作
//        }
//        super.setUserVisibleHint(isVisibleToUser);
//    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPd();

    }

    private void initView() {
        guessYouLikeAdapter = new GuessYouLikeAdapter(getActivity(), datas);
        rolesIv.setAdapter(guessYouLikeAdapter);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guessyoulike_fragment, null, false);
        ButterKnife.bind(this, view);
        rolesIv.setRefreshEnable(true);
        rolesIv.setLoadMoreEnable(false);
        rolesIv.setSmoothListViewListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
      //  getNetworkData(false);
    }

    private void initListener() {
        guessYouLikeAdapter.setOnPlanDetailClickListener(new GuessYouLikeAdapter.OnPlanDetailClickListener() {
            @Override
            public void onPlanDetailClick(int position) {
                M_Task task = guessYouLikeAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.INTENT_TASK, task);
                startActivity(intent);
            }
        });
    }
    private void getNetworkData(boolean isLoadMore) {
        taskIndustryRecordUserdo(isLoadMore);
    }

    //猜你喜欢
    private void taskIndustryRecordUserdo(final boolean isLoadMore) {
        if (taskIndustryRecordUserdoRequest != null) {
            taskIndustryRecordUserdoRequest.cancel();
        }
        TaskIndustryRecordUserdoRequest.Input input = new TaskIndustryRecordUserdoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        taskIndustryRecordUserdoRequest = new TaskIndustryRecordUserdoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                ToastUtils.show(getActivity(), "网络故障");
                rolesIv.stopRefresh();
                rolesIv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskSearchIndustryRecordList) response).status == 1) {

                    fillAdapter(((APIM_TaskSearchIndustryRecordList) response).recordList, ((APIM_TaskSearchIndustryRecordList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(getActivity(), ((APIM_TaskSearchIndustryRecordList) response).info);
                }
                dismissPd();
                rolesIv.stopRefresh();
                rolesIv.stopLoadMore();


            }
        });
        sendJsonRequest(taskIndustryRecordUserdoRequest);

    }


    /**
     * 设置数据
     */
    private void fillAdapter(final List<M_Task> list, int maxpage, boolean isLoadMore) {
        if (guessYouLikeAdapter == null) {
            initView();
            initListener();
        }
        if (list == null || list.size() == 0) {
            rolesIv.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
        } else {

            if (list != null && list.size() > 0) {
                for (M_Task bean : list) {
                    bean.state = 0;
                }
            }
            rolesIv.setVisibility(View.VISIBLE);
            rlNoData.setVisibility(View.GONE);
            rolesIv.setLoadMoreEnable(page < maxpage);
            guessYouLikeAdapter.setData(list, isLoadMore);
        }


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        getNetworkData(false);


    }

    @Override
    public void onLoadMore() {
        getNetworkData(true);

    }
}
