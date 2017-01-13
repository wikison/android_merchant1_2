package com.zemult.merchant.fragment.role;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.MyPublishTaskDetailActivity;
import com.zemult.merchant.adapter.role.PartyHomeListAdapter;
import com.zemult.merchant.aip.task.TaskIndustryListPushRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryListNew;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2016/7/30.
 */
public class PartyHomeFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {
    protected WeakReference<View> mRootView;
    @Bind(R.id.sml_task)
    SmoothListView mSmoothListView;
    @Bind(R.id.ll_no_data)
    LinearLayout mLinearLayoutNoData;
    TaskIndustryListPushRequest taskIndustryListPushRequest;
    PartyHomeListAdapter partyHomeListAdapter;
    List<M_Task> mTaskList = new ArrayList<>();
    int pagePosition = 0;
    private View view;
    private Context mContext;
    private boolean hasLoaded;
    private int page = 1;

    @Override
    protected void lazyLoad() {
        if (!hasLoaded || !isVisible) {
            return;
        }

        if (mTaskList.size() < 1) {
            showPd();
            //获取列表数据
            taskIndustryListPush(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get page index
        pagePosition = getArguments().getInt("page_position");

        registerReceiver(new String[]{Constants.BROCAST_FRESHPARTYHOMELIST});

    }

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_FRESHPARTYHOMELIST.equals(intent.getAction())&&pagePosition==0) {
            taskIndustryListPush(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //保存当前界面，防止重新创建
        if (mRootView == null || mRootView.get() == null) {
            view = inflater.inflate(R.layout.fragment_my_task, container, false);
            mRootView = new WeakReference<View>(view);
            hasLoaded = true;
            ButterKnife.bind(this, view);
            lazyLoad();
        } else {
            ViewGroup parent = (ViewGroup) mRootView.get().getParent();
            if (parent != null) {
                parent.removeView(mRootView.get());
            }
        }

        mSmoothListView.setRefreshEnable(true);
        mSmoothListView.setLoadMoreEnable(false);
        mSmoothListView.setSmoothListViewListener(this);

        return mRootView.get();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        mContext = getActivity();
    }


    private void initListener() {
        partyHomeListAdapter.setOnTaskDetailClickListener(new PartyHomeListAdapter.OnTaskDetailClickListener() {
            @Override
            public void onTaskDetailClick(int position) {
                M_Task task = partyHomeListAdapter.getItem(position);
                Intent intent = new Intent(mContext, MyPublishTaskDetailActivity.class);
                intent.putExtra("my_publish_task", task);
                startActivity(intent);

            }
        });
    }

    //获取对应页面下的任务列表
    private void taskIndustryListPush(final boolean isLoadMore) {
        if (taskIndustryListPushRequest != null) {
            taskIndustryListPushRequest.cancel();
        }

        TaskIndustryListPushRequest.Input input = new TaskIndustryListPushRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }

        input.merchantId = -1;
        //任务状态(-1:全部,0:进行中,1:已结束)
        if (pagePosition == 0) {
            input.state = -1;
        } else if (pagePosition == 1) {
            input.state = 0;
        } else {
            input.state = 1;
        }

        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();

        taskIndustryListPushRequest = new TaskIndustryListPushRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                mSmoothListView.stopRefresh();
                mSmoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskIndustryListNew) response).status == 1) {
                    if (partyHomeListAdapter == null) {
                        partyHomeListAdapter = new PartyHomeListAdapter(mContext, mTaskList, pagePosition);
                        mSmoothListView.setAdapter(partyHomeListAdapter);
                        initListener();
                    }
                    fillAdapter(((APIM_TaskIndustryListNew) response).taskList,
                            ((APIM_TaskIndustryListNew) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(getActivity(), ((APIM_TaskIndustryListNew) response).info);
                }
                dismissPd();
                mSmoothListView.stopRefresh();
                mSmoothListView.stopLoadMore();
            }
        });
        sendJsonRequest(taskIndustryListPushRequest);
    }

    // 填充数据
    private void fillAdapter(List<M_Task> list, int maxPage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            mLinearLayoutNoData.setVisibility(View.VISIBLE);
            mSmoothListView.setVisibility(View.GONE);
            mSmoothListView.setLoadMoreEnable(false);
        } else {
            mLinearLayoutNoData.setVisibility(View.GONE);
            mSmoothListView.setVisibility(View.VISIBLE);
            mSmoothListView.setLoadMoreEnable(page < maxPage);
            partyHomeListAdapter.setData(list, isLoadMore);
        }
    }

    @Override
    public void onRefresh() {
        taskIndustryListPush(false);
    }

    @Override
    public void onLoadMore() {
        taskIndustryListPush(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
