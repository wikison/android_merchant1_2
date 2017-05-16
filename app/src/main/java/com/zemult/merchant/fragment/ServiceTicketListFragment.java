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

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.UserSaleReservationList;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.AppointmentDetailNewActivity;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.model.apimodel.APIM_UserReservationList;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/4/13.
 */

public class ServiceTicketListFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {

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

    int selectPosition;
    int selectPayId;

    public ImageManager imageManager;
    CommonAdapter commonAdapter;
    private List<M_Reservation> mDatas = new ArrayList<M_Reservation>();
    public static String INTENT_TYPE = "type";
    UserSaleReservationList userSaleReservationList;//服务管家的预约列表
    int reservationId;
    int saleUserId, merchantId;


    @Override
    protected void lazyLoad() {
        if (!hasLoaded || !isVisible) {
            return;
        }

        if (mDatas.size() < 1) {
            showPd();
            //获取列表数据
            userSaleReservation();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get page index
        pagePosition = getArguments().getInt("page_position");
        saleUserId = getArguments().getInt("saleUserId");
        merchantId = getArguments().getInt("merchantId");
//        registerReceiver(new String[]{Constants.BROCAST_REFRESH_ORDER});
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //保存当前界面，防止重新创建
        if (mRootView == null || mRootView.get() == null) {
            view = inflater.inflate(R.layout.fragment_my_service_tickiet, container, false);
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
                reservationId = mDatas.get(position - 1).reservationId;
                Intent intent =new Intent(getActivity(),AppointmentDetailNewActivity.class);
                intent.putExtra(AppointmentDetailNewActivity.INTENT_RESERVATIONID,reservationId+"");
                getActivity().startActivity(intent);
            }
        });

    }


    private void userSaleReservation() {

        if (userSaleReservationList != null) {
            userSaleReservationList.cancel();
        }
        UserSaleReservationList.Input input = new UserSaleReservationList.Input();

        input.saleUserId = saleUserId;
        input.merchantId = merchantId;

        switch (pagePosition) {
            case 0:
                input.state = -1;
                break;
            case 1:
                input.state = 0;
                break;
            case 2:
                input.state = 1;
                break;
            case 3:
                input.state = 5;
                break;
        }


        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();
        userSaleReservationList = new UserSaleReservationList(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserReservationList) response).status == 1) {
                    if (page == 1) {
                        mDatas = ((APIM_UserReservationList) response).reservationList;
                        if (mDatas == null || mDatas.size() == 0) {
                            smoothListView.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            smoothListView.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);
                            if (mDatas != null && !mDatas.isEmpty()) {
                                smoothListView.setAdapter(commonAdapter = new CommonAdapter<M_Reservation>(mActivity, R.layout.item_myserviceticket_result, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, final M_Reservation mReservation, final int position) {
                                        try {
                                            holder.setText(R.id.tv_servicedate, DateTimeUtil.formatDate(mReservation.createTime));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        holder.setText(R.id.tv_describe, "客户:");

                                        if (!TextUtils.isEmpty(mReservation.head)) {
                                            holder.setCircleImage(R.id.iv_headimage, mReservation.head);
                                        }
                                        holder.setText(R.id.tv_saleuser, mReservation.name);

                                        holder.setText(R.id.tv_merchantName, mReservation.merchantName);
                                        holder.setText(R.id.tv_reservationTime, mReservation.reservationTime.substring(0,16));

                                        switch (mReservation.state) {
                                            //状态(状态(0:待确认,1:预约成功,2:已支付,3:预约失效(待确认超时)，4：预约未支付(超时)))
                                            case 0:
                                                holder.setViewVisible(R.id.money_ll);
                                                holder.setText(R.id.tv_state, "待确认");
                                                holder.setText(R.id.tv_orderdescription, "待支付订金");
                                                holder.setText(R.id.tv_price, "￥" + Convert.getMoneyString(mReservation.reservationMoney));


                                                break;
                                            case 1:
                                                if (mReservation.merchantReviewstatus == 2) {
                                                    holder.setViewVisible(R.id.money_ll);
                                                    holder.setText(R.id.tv_state, "待买单");
                                                    holder.setText(R.id.tv_orderdescription, "已付订金");
                                                    holder.setText(R.id.tv_price, "￥" + Convert.getMoneyString(mReservation.reservationMoney));

                                                } else {
                                                    holder.setViewGone(R.id.money_ll);
                                                    holder.setText(R.id.tv_state, "已确定");

                                                }
                                                break;

                                            case 2:
                                                holder.setViewVisible(R.id.money_ll);
                                                holder.setText(R.id.tv_state, "已支付");
                                                holder.setText(R.id.tv_orderdescription, "消费金额");
                                                holder.setText(R.id.tv_price, "￥" + Convert.getMoneyString(mReservation.userPayMoney));
                                                break;
                                            case 3:
                                            case 4:
                                                holder.setViewGone(R.id.money_ll);
                                                holder.setText(R.id.tv_state, "已结束");
                                                break;
                                        }


//                                        //状态(1:预约成功,2:已支付,3:预约结束)
//                                        if (mReservation.state == 1) {
//                                            holder.setText(R.id.tv_state, "已确认");
//                                            holder.setViewGone(R.id.money_ll);
//                                        } else if (mReservation.state == 2) {
//                                            holder.setText(R.id.tv_state, "已支付");
//                                            holder.setViewVisible(R.id.money_ll);
//                                            holder.setText(R.id.tv_price, "￥" + Convert.getMoneyString(mReservation.userPayMoney));
//                                        } else if (mReservation.state == 3) {
//                                            holder.setText(R.id.tv_state, "已结束");
//                                            holder.setViewGone(R.id.money_ll);
//                                        } else if (mReservation.state == 4) {
//                                            holder.setText(R.id.tv_state, "已结束");
//                                            holder.setViewGone(R.id.money_ll);
//                                        }
//
//
//                                        String time = mReservation.reservationTime;

//                                        holder.setText(R.id.tv_reservationTime, mReservation.reservationTime + DateTimeUtil.getWeekDayOfWeek(time) + "  " + time.substring(11, 16));

//
//
//                                       long a = DateTimeUtil.getIntervalDays(DateTimeUtil.getCurrentDate(), mReservation.reservationTime.substring(0, 10));
//getWeekDayOfWeek
//                                        if (a < 1 && a >= 0) {
//                                            holder.setText(R.id.day_tv, "今天");
//                                            holder.setText(R.id.time_tv, mReservation.reservationTime.substring(11, 16));
//                                        } else if (a >= 1 && a < 2) {
//                                            holder.setText(R.id.day_tv, "昨天");
//                                            holder.setText(R.id.time_tv, mReservation.reservationTime.substring(11, 16));
//                                        } else {
//                                            holder.setText(R.id.day_tv, DateTimeUtil.getChinaDayofWeek(mReservation.reservationTime.substring(0, 10)));
//                                            holder.setText(R.id.time_tv, mReservation.reservationTime.substring(5, 10));
//                                        }


                                    }

                                });
                            }

                        }
                    } else {
                        mDatas.addAll(((APIM_UserReservationList) response).reservationList);
                        commonAdapter.notifyDataSetChanged();
                    }

                    if (((APIM_UserReservationList) response).maxpage <= page) {
                        smoothListView.setLoadMoreEnable(false);
                    } else {
                        smoothListView.setLoadMoreEnable(true);
                        page++;
                        Log.i("sunjian", "" + page);
                    }
                    initListener();
                } else {
                    ToastUtils.show(mActivity, ((APIM_UserReservationList) response).info);
                }
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }
        });
        sendJsonRequest(userSaleReservationList);
    }


    @Override
    public void onRefresh() {
        page = 1;
        userSaleReservation();
    }

    @Override
    public void onLoadMore() {
        userSaleReservation();
    }
}
