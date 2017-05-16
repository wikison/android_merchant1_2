package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
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
import com.zemult.merchant.aip.mine.User2OrderInvitationFeedListRequest;
import com.zemult.merchant.aip.mine.User2OrderInvitationListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.model.apimodel.APIM_UserReservationList;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

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
    CommonAdapter commonAdapter;
    private List<M_Reservation> mDatas = new ArrayList<M_Reservation>();

    int pagePosition = 0;
    int page = 1;
    private View view;
    private boolean hasLoaded;

    int reservationId;

    User2OrderInvitationListRequest user2OrderInvitationListRequest;
    User2OrderInvitationFeedListRequest user2OrderInvitationFeedListRequest;

    @Override
    protected void lazyLoad() {
        if (!hasLoaded || !isVisible) {
            return;
        }

        if (mDatas.size() < 1) {
            showPd();
            //获取列表数据
            getListData();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagePosition = getArguments().getInt("page_position");
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

    private void getListData() {
        switch (pagePosition) {
            case 0:
                user2OrderInvitationList();
                break;
            case 1:
                user2OrderInvitationFeedList();
                break;
        }
    }


    private void user2OrderInvitationList() {
        if (user2OrderInvitationListRequest != null) {
            user2OrderInvitationListRequest.cancel();
        }
        User2OrderInvitationListRequest.Input input = new User2OrderInvitationListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();
        user2OrderInvitationListRequest = new User2OrderInvitationListRequest(input, new ResponseListener() {
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
                                smoothListView.setAdapter(commonAdapter = new CommonAdapter<M_Reservation>(mActivity, R.layout.item_myproinvite_result, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, final M_Reservation mReservation, final int position) {
                                        holder.setCircleResImage(R.id.iv_headimage, getHeadId(mReservation.title));
                                        holder.setText(R.id.theme_tv, mReservation.title);
                                        Date dateInfo = DateTimeUtil.getDateFromString(mReservation.reservationTime, "yyyy-MM-dd HH:mm:ss");
                                        Calendar calendar = new GregorianCalendar();
                                        calendar.setTime(dateInfo);
                                        String reservationTimeInfo = calendar.get(Calendar.MONTH) + 1 + "月-" + calendar.get(Calendar.DAY_OF_MONTH) + "日 (" + DateTimeUtil.getChinaDayOfWeek(dateInfo) + ") " + mReservation.reservationTime.substring(11, 16);
                                        holder.setText(R.id.tv_time, reservationTimeInfo);
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
                    }
                    initListener();
                } else {
                    ToastUtils.show(mActivity, ((APIM_UserReservationList) response).info);
                }
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }
        });
        sendJsonRequest(user2OrderInvitationListRequest);
    }

    private void user2OrderInvitationFeedList() {
        if (user2OrderInvitationFeedListRequest != null) {
            user2OrderInvitationFeedListRequest.cancel();
        }
        User2OrderInvitationFeedListRequest.Input input = new User2OrderInvitationFeedListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();
        user2OrderInvitationFeedListRequest = new User2OrderInvitationFeedListRequest(input, new ResponseListener() {
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
                                smoothListView.setAdapter(commonAdapter = new CommonAdapter<M_Reservation>(mActivity, R.layout.item_myproinvite_result, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, final M_Reservation mReservation, final int position) {
                                        holder.setCircleResImage(R.id.iv_headimage, getHeadId(mReservation.title));
                                        holder.setText(R.id.theme_tv, mReservation.title);
                                        Date dateInfo = DateTimeUtil.getDateFromString(mReservation.reservationTime, "yyyy-MM-dd HH:mm:ss");
                                        Calendar calendar = new GregorianCalendar();
                                        calendar.setTime(dateInfo);
                                        String reservationTimeInfo = (calendar.get(Calendar.MONTH) + 1) + "月-" + calendar.get(Calendar.DAY_OF_MONTH) + "日 (" + DateTimeUtil.getChinaDayOfWeek(dateInfo) + ") " + mReservation.reservationTime.substring(11, 16);
                                        holder.setText(R.id.tv_time, reservationTimeInfo);
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
                    }
                    initListener();
                } else {
                    ToastUtils.show(mActivity, ((APIM_UserReservationList) response).info);
                }
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }
        });
        sendJsonRequest(user2OrderInvitationFeedListRequest);
    }

    private int getHeadId(String title) {
        int result = R.mipmap.tupiansilie_circle_icon;
        switch (title) {
            case "商务宴请":
                result = R.mipmap.shangwu;
                break;
            case "婚宴":
                result = R.mipmap.hunyan;
                break;
            case "同窗聚会":
                result = R.mipmap.tongchuang;
                break;
            case "满月宴":
                result = R.mipmap.manyue;
                break;
            case "谢师宴":
                result = R.mipmap.xieshi;
                break;
            case "寿宴":
                result = R.mipmap.shouyan;
                break;
        }
        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        page = 1;
        getListData();
    }

    @Override
    public void onLoadMore() {
        getListData();
    }


    private void initListener() {
        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reservationId = mDatas.get(position - 1).reservationId;
                IntentUtil.start_activity(getActivity(), BaseWebViewActivity.class,
                        new Pair<String, String>("titlename", "邀请函详情"), new Pair<String, String>("url", Constants.RESERVATIONFEEDBACKINFO + reservationId));
            }
        });

    }
}
