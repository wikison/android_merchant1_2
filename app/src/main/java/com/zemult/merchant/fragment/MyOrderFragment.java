package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.RedRecordDetailActivity;
import com.zemult.merchant.activity.mine.GiftAboutDetailActivity;
import com.zemult.merchant.activity.mine.PayInfoActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.minefragment.UserPayAdapter;
import com.zemult.merchant.aip.mine.UserPayInfoRequest;
import com.zemult.merchant.aip.mine.UserPayListRequest;
import com.zemult.merchant.alipay.taskpay.ChoosePayTypeActivity;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.model.apimodel.APIM_UserBillList;
import com.zemult.merchant.util.IntentUtil;
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
 * Created by Wikison on 2016/12/29.
 */

public class MyOrderFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {
    protected WeakReference<View> mRootView;
    @Bind(R.id.sml_task)
    SmoothListView mSmoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout mLinearLayoutNoData;

    private Context mContext;
    private Activity mActivity;
    UserPayListRequest userPayListRequest;
    UserPayInfoRequest userPayInfoRequest;
    int pagePosition = 0;
    int page = 1;
    private View view;
    private boolean hasLoaded;
    private List<M_Bill> payList = new ArrayList<M_Bill>();
    private UserPayAdapter userPayAdapter;
    int selectPosition;
    int selectPayId;


    @Override
    protected void lazyLoad() {
        if (!hasLoaded || !isVisible) {
            return;
        }

        if (payList.size() < 1) {
            showPd();
            //获取列表数据
            getUserPayList(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get page index
        pagePosition = getArguments().getInt("page_position");

        registerReceiver(new String[]{Constants.BROCAST_REFRESH_ORDER});

    }

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_REFRESH_ORDER.equals(intent.getAction())) {
            getUserPayList(false);
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
        mSmoothListView.setHeaderDividersEnabled(true);

        return mRootView.get();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        mContext = getActivity();
        mActivity = getActivity();
    }

    private void initListener() {
        userPayAdapter.setOnItemRootClickListener(new UserPayAdapter.ItemRootClickListener() {
            @Override
            public void onItemClick(final M_Bill m_bill) {
                if (m_bill.type == 0) {
                    IntentUtil.intStart_activity(mActivity,
                            PayInfoActivity.class, new Pair<String, Integer>("userPayId", m_bill.userPayId));
                } else if (m_bill.type == 3) {
                    IntentUtil.intStart_activity(mActivity,
                            GiftAboutDetailActivity.class, new Pair<String, Integer>("userPayId", m_bill.userPayId));
                } else if (m_bill.type == 4) {
                    Intent it = new Intent(mContext, RedRecordDetailActivity.class);
                    it.putExtra(RedRecordDetailActivity.INTENT_FLAG,1);
                    it.putExtra(RedRecordDetailActivity.INTENT_INFO, m_bill);
                    it.putExtra("userPayId", m_bill.userPayId);
                    startActivity(it);
                }
            }
        });

        userPayAdapter.setOnItemSaleUserClickListener(new UserPayAdapter.ItemSaleUserClickListener() {
            @Override
            public void onItemClick(M_Bill m_bill) {
                if (m_bill.type == 0) {
                    IntentUtil.intStart_activity(mActivity, UserDetailActivity.class, new Pair<String, Integer>(UserDetailActivity.USER_ID, m_bill.saleUserId));
                } else if (m_bill.type == 3 || m_bill.type == 4) {
                    IntentUtil.intStart_activity(mActivity, UserDetailActivity.class, new Pair<String, Integer>(UserDetailActivity.USER_ID, m_bill.toUserId));
                }

            }
        });

        userPayAdapter.setOnItemToPayClickListener(new UserPayAdapter.ItemToPayClickListener() {
            @Override
            public void onItemClick(final M_Bill m_bill) {
                Intent intent = new Intent(mActivity, ChoosePayTypeActivity.class);
                intent.putExtra("consumeMoney", m_bill.allMoney);
                intent.putExtra("order_sn", m_bill.number);
                intent.putExtra("userPayId", m_bill.userPayId);
                intent.putExtra("merchantName", m_bill.merchantName);
                intent.putExtra("merchantHead", m_bill.merchantHead);
                startActivity(intent);
            }
        });

        mSmoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position - 1;
                M_Bill m_bill = userPayAdapter.getItem(position - 1);
                selectPayId = m_bill.userPayId;
                if (m_bill.type == 0) {
                    Intent intent = new Intent(mContext, PayInfoActivity.class);
                    intent.putExtra("userPayId", m_bill.userPayId);
                    startActivity(intent);
                } else if (m_bill.type == 3) {
                    Intent intent = new Intent(mContext, GiftAboutDetailActivity.class);
                    intent.putExtra("userPayId", m_bill.userPayId);
                    startActivity(intent);
                }else if (m_bill.type == 4) {
                    Intent it = new Intent(mContext, RedRecordDetailActivity.class);
                    it.putExtra(RedRecordDetailActivity.INTENT_INFO, m_bill);
                    it.putExtra(RedRecordDetailActivity.INTENT_FLAG,1);
                    it.putExtra("userPayId", m_bill.userPayId);
                    startActivity(it);
                }

            }
        });
    }

    //订单详情
    private void user_pay_info() {
        showPd();
        if (userPayInfoRequest != null) {
            userPayInfoRequest.cancel();
        }


        UserPayInfoRequest.Input input = new UserPayInfoRequest.Input();
        input.userPayId = selectPayId;

        input.convertJosn();
        userPayInfoRequest = new UserPayInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillInfo) response).status == 1) {
                    M_Bill m = ((APIM_UserBillInfo) response).userPayInfo;
                    userPayAdapter.refreshOneRecord(m, selectPosition);
                } else {
                    ToastUtils.show(mContext, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userPayInfoRequest);
    }

    /**
     * 获取我的订单列表
     */
    private void getUserPayList(final boolean isLoadMore) {
        if (userPayListRequest != null) {
            userPayListRequest.cancel();
        }
        UserPayListRequest.Input input = new UserPayListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.state = pagePosition - 1;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();

        userPayListRequest = new UserPayListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                mSmoothListView.stopRefresh();
                mSmoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillList) response).status == 1) {
                    if (userPayAdapter == null) {
                        userPayAdapter = new UserPayAdapter(mContext, payList, pagePosition);
                        mSmoothListView.setAdapter(userPayAdapter);
                        initListener();
                    }
                    fillAdapter(((APIM_UserBillList) response).userPayList,
                            ((APIM_UserBillList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_UserBillList) response).info);
                }

                dismissPd();
                mSmoothListView.stopRefresh();
                mSmoothListView.stopLoadMore();

            }
        });
        sendJsonRequest(userPayListRequest);
    }


    // 填充数据
    private void fillAdapter(List<M_Bill> list, int maxPage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            mLinearLayoutNoData.setVisibility(View.VISIBLE);
            mSmoothListView.setVisibility(View.GONE);
            mSmoothListView.setLoadMoreEnable(false);
        } else {
            mLinearLayoutNoData.setVisibility(View.GONE);
            mSmoothListView.setVisibility(View.VISIBLE);
            mSmoothListView.setLoadMoreEnable(page < maxPage);
            userPayAdapter.setData(list, isLoadMore);
        }
    }

    @Override
    public void onRefresh() {
        getUserPayList(false);
    }

    @Override
    public void onLoadMore() {
        getUserPayList(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
