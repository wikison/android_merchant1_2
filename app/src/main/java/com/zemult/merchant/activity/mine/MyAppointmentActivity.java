package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ScanQrActivity;
import com.zemult.merchant.activity.slash.FindPayActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.UserReservationListRequest;
import com.zemult.merchant.aip.mine.UserSaleReservationList;
import com.zemult.merchant.alipay.taskpay.TaskPayResultActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.model.apimodel.APIM_UserReservationList;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/1/18.
 */

public class MyAppointmentActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.myappointment_lv)
    SmoothListView myappointmentLv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;

    public ImageManager imageManager;
    UserReservationListRequest userReservationListRequest;//消费者的预约列表
    private int page = 1;

    private Context mContext;
    CommonAdapter commonAdapter;
    private List<M_Reservation> mDatas = new ArrayList<M_Reservation>();
    public static String INTENT_TYPE = "type";
    UserSaleReservationList userSaleReservationList;//服务管家的预约列表
    int type;
    private boolean fromHome;

    //此处item使用item_myappoint
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_myappointment);
    }

    @Override
    public void init() {
        EventBus.getDefault().register(this);
        mContext = this;
        imageManager = new ImageManager(this);
        myappointmentLv.setRefreshEnable(true);
        myappointmentLv.setLoadMoreEnable(false);
        myappointmentLv.setSmoothListViewListener(this);
        myappointmentLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        showPd();

        fromHome = getIntent().getBooleanExtra("fromHome", false);
        type = getIntent().getIntExtra(INTENT_TYPE, 0);


        if (type == 0) {
            if (fromHome) {
                lhTvTitle.setText("待结账预约单");
            } else {
                lhTvTitle.setText("我的预约");
            }
            userReservationList();
        } else if (type == 1) {
            lhTvTitle.setText("预约记录");
            userSaleReservation();
        }

        myappointmentLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                M_Reservation mReservation = (M_Reservation) commonAdapter.getItem(position - 1);
                Intent intent = new Intent(mContext, AppointmentDetailActivity.class);
                intent.putExtra(AppointmentDetailActivity.INTENT_RESERVATIONID, "" + mReservation.reservationId);
                intent.putExtra(AppointmentDetailActivity.INTENT_TYPE, type);
                startActivity(intent);
            }
        });

    }


    private void userReservationList() {

        if (userReservationListRequest != null) {
            userReservationListRequest.cancel();
        }
        UserReservationListRequest.Input input = new UserReservationListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.state = fromHome ? 1 : -1;
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();
        userReservationListRequest = new UserReservationListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                myappointmentLv.stopRefresh();
                myappointmentLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserReservationList) response).status == 1) {
                    if (page == 1) {
                        mDatas = ((APIM_UserReservationList) response).reservationList;
                        if (mDatas == null || mDatas.size() == 0) {
                            myappointmentLv.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            myappointmentLv.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);
                            if (mDatas != null && !mDatas.isEmpty()) {
                                myappointmentLv.setAdapter(commonAdapter = new CommonAdapter<M_Reservation>(MyAppointmentActivity.this, R.layout.item_myappoint, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, final M_Reservation mReservation, final int position) {

                                        if (position == 0) {
                                            holder.setViewVisible(R.id.v1);
                                        } else if (position > 0) {
                                            holder.setViewGone(R.id.v1);
                                        }

                                        if (!TextUtils.isEmpty(mReservation.saleUserHead)) {
                                            holder.setCircleImage(R.id.head_iv, mReservation.saleUserHead);
                                        }
                                        holder.setText(R.id.servicer_tv, "服务管家:  " + mReservation.saleUserName);


                                        holder.setText(R.id.shop_tv, mReservation.merchantName);

                                        if (fromHome) {
                                            holder.setViewVisible(R.id.tv_buy);
                                            holder.setViewGone(R.id.tv_state);

                                            holder.setOnclickListener(R.id.tv_buy, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(mContext, FindPayActivity.class);
                                                    // 预约单id
                                                    intent.putExtra("reservationId", Integer.valueOf(mReservation.reservationId));
                                                    intent.putExtra("merchantId", Integer.valueOf(mReservation.merchantId));
                                                    intent.putExtra("userSaleId", Integer.valueOf(mReservation.saleUserId));
                                                    startActivity(intent);

                                                }
                                            });
                                        } else {
                                            holder.setViewVisible(R.id.tv_state);
                                            holder.setViewInvisible(R.id.tv_buy);
                                            //状态(1:预约成功,2:已支付,3:预约结束)
                                            if (mReservation.state == 1) {
                                                //tv_state
                                                holder.setText(R.id.tv_state, "预约成功");
                                                holder.setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.e6bb7c));
                                            } else if (mReservation.state == 2) {
                                                holder.setText(R.id.tv_state, "已支付");
                                                holder.setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.e6bb7c));
                                            } else if (mReservation.state == 3) {
                                                holder.setText(R.id.tv_state, "预约结束");
                                                holder.setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.font_black_999));
                                            }

                                        }
                                        long a = DateTimeUtil.getIntervalDays(DateTimeUtil.getCurrentDate(), mReservation.reservationTime.substring(0, 10));

                                        if (a < 1 && a >= 0) {
                                            holder.setText(R.id.day_tv, "今天");
                                            holder.setText(R.id.time_tv, mReservation.reservationTime.substring(11, 16));
                                        } else if (a >= 1 && a < 2) {
                                            holder.setText(R.id.day_tv, "昨天");
                                            holder.setText(R.id.time_tv, mReservation.reservationTime.substring(11, 16));
                                        } else {
                                            holder.setText(R.id.day_tv, DateTimeUtil.getChinaDayofWeek(mReservation.reservationTime.substring(0, 10)));
                                            holder.setText(R.id.time_tv, mReservation.reservationTime.substring(5, 10));
                                        }


                                    }

                                });
                            }

                        }
                    } else {
                        mDatas.addAll(((APIM_UserReservationList) response).reservationList);
                        commonAdapter.notifyDataSetChanged();
                    }

                    if (((APIM_UserReservationList) response).maxpage <= page) {
                        myappointmentLv.setLoadMoreEnable(false);
                    } else {
                        myappointmentLv.setLoadMoreEnable(true);
                        page++;
                        Log.i("sunjian", "" + page);
                    }
                } else {
                    ToastUtils.show(MyAppointmentActivity.this, ((APIM_UserReservationList) response).info);
                }
                myappointmentLv.stopRefresh();
                myappointmentLv.stopLoadMore();
            }
        });
        sendJsonRequest(userReservationListRequest);
    }


    private void userSaleReservation() {

        if (userSaleReservationList != null) {
            userSaleReservationList.cancel();
        }
        UserSaleReservationList.Input input = new UserSaleReservationList.Input();
        input.saleUserId = SlashHelper.userManager().getUserId();
        ;//约客的用户id
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();
        userSaleReservationList = new UserSaleReservationList(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                myappointmentLv.stopRefresh();
                myappointmentLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserReservationList) response).status == 1) {
                    if (page == 1) {
                        mDatas = ((APIM_UserReservationList) response).reservationList;
                        if (mDatas == null || mDatas.size() == 0) {
                            myappointmentLv.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            myappointmentLv.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);
                            if (mDatas != null && !mDatas.isEmpty()) {
                                myappointmentLv.setAdapter(commonAdapter = new CommonAdapter<M_Reservation>(MyAppointmentActivity.this, R.layout.item_myappoint, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, M_Reservation mReservation, final int position) {

                                        if (position == 0) {
                                            holder.setViewVisible(R.id.v1);
                                        } else if (position > 0) {
                                            holder.setViewGone(R.id.v1);
                                        }
                                        long diff = DateTimeUtil.getIntervalDays(DateTimeUtil.getCurrentDate(), mReservation.reservationTime.substring(0, 10));

                                        if (diff < 1 && diff >= 0) {
                                            holder.setText(R.id.day_tv, "今天");
                                            holder.setText(R.id.time_tv, mReservation.reservationTime.substring(11, 16));
                                        } else if (diff >= 1 && diff < 2) {
                                            holder.setText(R.id.day_tv, "昨天");
                                            holder.setText(R.id.time_tv, mReservation.reservationTime.substring(11, 16));
                                        } else {
                                            holder.setText(R.id.day_tv, DateTimeUtil.getChinaDayofWeek(mReservation.reservationTime.substring(0, 10)));
                                            holder.setText(R.id.time_tv, mReservation.reservationTime.substring(5, 10));
                                        }

//                                        holder.setText(R.id.day_tv, mReservation.reservationTime.substring(5, 10));


                                        if (!TextUtils.isEmpty(mReservation.saleUserHead)) {
                                            holder.setCircleImage(R.id.head_iv, mReservation.head);
                                        }
                                        holder.setText(R.id.servicer_tv, "客户:  " + mReservation.name);

                                        holder.setText(R.id.shop_tv, mReservation.merchantName);
                                        //状态(1:预约成功,2:已支付,3:预约结束)
                                        if (mReservation.state == 1) {
                                            //tv_state
                                            holder.setText(R.id.tv_state, "预约成功");
                                            holder.setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.e6bb7c));
                                        } else if (mReservation.state == 2) {
                                            holder.setText(R.id.tv_state, "已支付");
                                            holder.setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.e6bb7c));
                                        } else if (mReservation.state == 3) {
                                            holder.setText(R.id.tv_state, "预约结束");
                                            holder.setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.font_black_999));
                                        }
                                    }

                                });
                            }

                        }
                    } else {
                        mDatas.addAll(((APIM_UserReservationList) response).reservationList);
                        commonAdapter.notifyDataSetChanged();
                    }

                    if (((APIM_UserReservationList) response).maxpage <= page) {
                        myappointmentLv.setLoadMoreEnable(false);
                    } else {
                        myappointmentLv.setLoadMoreEnable(true);
                        page++;
                        Log.i("sunjian", "" + page);
                    }
                } else {
                    ToastUtils.show(MyAppointmentActivity.this, ((APIM_UserReservationList) response).info);
                }
                myappointmentLv.stopRefresh();
                myappointmentLv.stopLoadMore();
            }
        });
        sendJsonRequest(userSaleReservationList);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onRefresh() {
        page = 1;
        if (type == 0) {
            userReservationList();
        } else if (type == 1) {
            userSaleReservation();
        }

    }

    @Override
    public void onLoadMore() {
        if (type == 0) {
            userReservationList();
        } else if (type == 1) {
            userSaleReservation();
        }

    }

    /**
     * =================================================处理刷新请求===========================================================================
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void refreshEvent(String s) {
        if (AppointmentDetailActivity.REFLASH_MYAPPOINT.equals(s))
            if (type == 0) {
                userReservationList();
            } else if (type == 1) {
                userSaleReservation();
            }
    }


}
