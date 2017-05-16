package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.YWCustomMessageBody;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.android.volley.VolleyError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ShareAppointmentActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.User2SaleUserFanListRequest;
import com.zemult.merchant.aip.mine.UserAttractAddRequest;
import com.zemult.merchant.aip.mine.UserAttractDelRequest;
import com.zemult.merchant.aip.reservation.UserReservationAddRequest;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.CreateBespeakNewActivity;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.model.apimodel.APIM_UserFansList;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.ShareText;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SearchView;
import com.zemult.merchant.view.SharePopwindow;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by wikison on 2016/6/14.
 */
//1026客户管理
public class MyFansActivity extends MBaseActivity implements SmoothListView.ISmoothListViewListener {
    public ImageManager imageManager;
    CommonAdapter commonAdapter;
    UserReservationAddRequest userReservationAddRequest;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.concern_lv)
    SmoothListView fansLv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.search_view)
    SearchView searchView;
    private List<M_Fan> mDatas = new ArrayList<M_Fan>();
    private Context mContext;
    private int page = 1;
    private UserAttractAddRequest attractAddRequest; // 添加关注
    private UserAttractDelRequest attractDelRequest; // 取消关注
    public static final String INTENT_USERID = "userid";
    private int userId,planId;
    private String name,fromAct,merchantId,ordertime,reservationMoney,shopname,note,orderpeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        setContentView(R.layout.activity_my_follow);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        mContext = this;
        imageManager = new ImageManager(this);
        fromAct=getIntent().getStringExtra("fromAct");

        merchantId=getIntent().getStringExtra("merchantId");
        ordertime=getIntent().getStringExtra("ordertime");
        reservationMoney=getIntent().getStringExtra("reservationMoney");
        shopname=getIntent().getStringExtra("shopname");
        note=getIntent().getStringExtra("note");
        orderpeople=getIntent().getStringExtra("orderpeople");
        planId=getIntent().getIntExtra("planId",0);

        userId = getIntent().getIntExtra(INTENT_USERID, -1);
        lhTvTitle.setText("SCRM客户管理");
        fansLv.setRefreshEnable(true);
        fansLv.setLoadMoreEnable(false);
        fansLv.setSmoothListViewListener(this);
        fansLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        showPd();
        user2_saleUser_fansList();
        searchView.setTvCancelVisible(View.GONE);
        searchView.setBgColor(getResources().getColor(R.color.divider_c1));
        searchView.setSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearch(String text) {
                name = text;
                onRefresh();
            }

            @Override
            public void onClear() {
                name = "";
                onRefresh();
            }
        });



    }

    private User2SaleUserFanListRequest userFansListRequest;

    //服务管家的SCRM列表         对该服务管家 关注过的/成功预约/完成支付/赞赏过的
    private void user2_saleUser_fansList() {
        if (userFansListRequest != null) {
            userFansListRequest.cancel();
        }
        User2SaleUserFanListRequest.Input input = new User2SaleUserFanListRequest.Input();
        input.saleUserId = userId;
        input.name = name;
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();
        userFansListRequest = new User2SaleUserFanListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                fansLv.stopRefresh();
                fansLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserFansList) response).status == 1) {
                    if (page == 1) {
                        mDatas = ((APIM_UserFansList) response).userList;
                        if (mDatas == null || mDatas.isEmpty()) {
                            fansLv.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            fansLv.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);
                            if (mDatas != null && !mDatas.isEmpty()) {
                                fansLv.setAdapter(commonAdapter = new CommonAdapter<M_Fan>(MyFansActivity.this, R.layout.item_my_follow, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, M_Fan mfollow, final int position) {

                                        if (!TextUtils.isEmpty(mfollow.head)) {
                                            holder.setCircleImage(R.id.iv_follow_head, mfollow.head);
                                        }
                                        holder.setText(R.id.tv_follow_name, mfollow.name);
                                        holder.setViewGone(R.id.iv_sex);
                                        holder.setViewGone(R.id.iv_status);

                                        holder.setOnclickListener(R.id.ll_root, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(null!=fromAct){
                                                    user_reservation_add(mDatas.get(position).userId);
                                                }
                                                else{
                                                    Intent IMkitintent = LoginSampleHelper.getInstance().getIMKit().getChattingActivityIntent(mDatas.get(position).userId + "", Urls.APP_KEY);
                                                    Bundle bundle=new Bundle();
                                                    bundle.putInt("serviceId", mDatas.get(position).userId);
                                                    IMkitintent.putExtras(bundle);
                                                    startActivity(IMkitintent);
                                                }

                                            }
                                        });
                                    }

                                });
                            }

                        }
                    } else {
                        mDatas.addAll(((APIM_UserFansList) response).userList);
                        commonAdapter.notifyDataSetChanged();
                    }

                    if (((APIM_UserFansList) response).maxpage <= page) {
                        fansLv.setLoadMoreEnable(false);
                    } else {
                        fansLv.setLoadMoreEnable(true);
                        page++;

                        Log.i("sunjian", "" + page);
                    }

                } else {
                    ToastUtils.show(MyFansActivity.this, ((APIM_UserFansList) response).info);
                }
                fansLv.stopRefresh();
                fansLv.stopLoadMore();

            }
        });

        sendJsonRequest(userFansListRequest);
    }


    private void user_reservation_add(final int customerId) {

        try {
            if (userReservationAddRequest != null) {
                userReservationAddRequest.cancel();
            }
            UserReservationAddRequest.Input input = new UserReservationAddRequest.Input();
            input.merchantId = merchantId;
            input.saleUserId = SlashHelper.userManager().getUserId();
            if(ordertime.length()<17){
                input.reservationTime= ordertime+ ":00";
            }
            else{
                input.reservationTime= ordertime;
            }
            input.num = orderpeople;
            input.note = note;
            input.userId = customerId;
            input.reservationMoney =reservationMoney ;
            input.planId=planId;
            input.convertJosn();

            userReservationAddRequest = new UserReservationAddRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    if (((CommonResult) response).status == 1) {
                        YWCustomMessageBody messageBody = new YWCustomMessageBody();
                        //定义自定义消息协议，用户可以根据自己的需求完整自定义消息协议，不一定要用JSON格式，这里纯粹是为了演示的需要
                        JSONObject object = new JSONObject();
                        try {
                            object.put("customizeMessageType", "Task");
                            object.put("tasktype", "ORDER");
                            object.put("taskTitle", "[服务订单] " + (ordertime.length()<17?ordertime
                                    :ordertime.substring(0,16) ) + "  " + shopname+"(商户)");
                            object.put("serviceId",  SlashHelper.userManager().getUserId());
                            object.put("reservationId", ((CommonResult) response).reservationId);
                        } catch (JSONException e) {

                        }
                        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
                        messageBody.setSummary("[服务订单]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
                        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
                        YWMessage message2 = YWMessageChannel.createTextMessage("您好，已经按照你的要求订好了，你看一下，没问题就确认一下，谢谢~");
                        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
                        IYWContact appContact = YWContactFactory.createAPPContact(customerId+ "", imKit.getIMCore().getAppKey());
                        imKit.getConversationService()
                                .forwardMsgToContact(appContact
                                        , message, forwardCallBack);

                        imKit.getConversationService()
                                .forwardMsgToContact(appContact
                                        , message2, forwardCallBack);
                        startActivity(imKit.getChattingActivityIntent(customerId + ""));
                        finish();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(userReservationAddRequest);
        } catch (Exception e) {
        }
    }

    @OnClick({R.id.ll_back, R.id.lh_btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }

    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(MyFansActivity.this, "forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(MyFansActivity.this, "forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };
    @Override
    public void onRefresh() {
        user2_saleUser_fansList();
    }

    @Override
    public void onLoadMore() {
        user2_saleUser_fansList();
    }
}

