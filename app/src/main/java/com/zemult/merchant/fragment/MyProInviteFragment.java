package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/5/11.
 */

public class MyProInviteFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {

    protected WeakReference<View> mRootView;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    private Context mContext;
    private Activity mActivity;
    int pagePosition = 0;
    int page = 1;
    private View view;
    private boolean hasLoaded;


    @Override
    protected void lazyLoad() {
        if (!hasLoaded || !isVisible) {
            return;
        }

//        if (mDatas.size() < 1) {
//            showPd();
//            //获取列表数据
//            userReservationList();
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get page index
        pagePosition = getArguments().getInt("page_position");
        registerReceiver(new String[]{Constants.BROCAST_REFRESH_MYPROINVITE});
    }

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_REFRESH_MYPROINVITE.equals(intent.getAction())) {
            onRefresh();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //保存当前界面，防止重新创建
        if (mRootView == null || mRootView.get() == null) {
            view = inflater.inflate(R.layout.fragment_my_pro_invite, container, false);
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

        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        smoothListView.setHeaderDividersEnabled(true);

        ButterKnife.bind(this, mRootView.get());
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
        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                reservationId = mDatas.get(position - 1).reservationId;
//                Intent intent = new Intent(mActivity, ServiceTicketDetailActivity.class);
//                intent.putExtra(ServiceTicketDetailActivity.INTENT_RESERVATIONID, reservationId + "");
//                startActivity(intent);
            }
        });

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
}
