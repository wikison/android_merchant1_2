package com.zemult.merchant.activity.mine.message;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.BillInfoActivity;
import com.zemult.merchant.activity.mine.ServiceHistoryDetailActivity;
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.UserMessageListBill_1_2_2Request;
import com.zemult.merchant.aip.mine.UserMessageListSys_1_2Request;
import com.zemult.merchant.aip.task.TaskIndustryRecordInfoRequest;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Message;
import com.zemult.merchant.model.apimodel.APIM_CommonSysMessageList;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class OrderMessageActivity extends MBaseActivity implements SmoothListView.ISmoothListViewListener {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    @Bind(R.id.smoothListView)
    SmoothListView concernLv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;


    List<M_Message> mDatas = new ArrayList<M_Message>();
    CommonAdapter commonAdapter;
    private int page = 1;
    UserMessageListBill_1_2_2Request userMessageListBill_1_2_2Request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        setContentView(R.layout.activity_commonlist);
        ButterKnife.bind(this);
        init();
    }


    public void init() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("约服账单");
        concernLv.setRefreshEnable(true);
        concernLv.setLoadMoreEnable(false);
        concernLv.setSmoothListViewListener(this);
        user_messageList_bill_1_2_2(true);
        concernLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDatas.get(position).messageType == 0) {//消息类型(0:获得激励红包，5:支付买单凭证(作为服务管家),8:收到打赏)
                    Intent intent = new Intent(OrderMessageActivity.this, BillInfoActivity.class);
                    intent.putExtra("billId", mDatas.get(position).billId);
                    intent.putExtra("type", 6);
                    startActivity(intent);
                }
                if (mDatas.get(position).messageType == 5) {
                    Intent intent = new Intent(OrderMessageActivity.this, ServiceHistoryDetailActivity.class);
                    intent.putExtra("userPayId", mDatas.get(position).userPayId);
                    startActivity(intent);
                }
                if (mDatas.get(position).messageType == 8) {
                    Intent intent = new Intent(OrderMessageActivity.this, BillInfoActivity.class);
                    intent.putExtra("billId", mDatas.get(position).billId);
                    intent.putExtra("type", 10);
                    startActivity(intent);
                }

            }
        });

        concernLv.setAdapter(commonAdapter = new CommonAdapter<M_Message>(OrderMessageActivity.this, R.layout.item_ordermessage_result, mDatas) {
            @Override
            public void convert(CommonViewHolder holder, final M_Message message, final int position) {
                holder.setText(R.id.tv_messagedate, message.createtime);
                holder.setText(R.id.tv_ordernum, message.number);

                holder.setText(R.id.tv_orderprice, "￥" + (message.money == 0 ? "0.00" : Convert.getMoneyString(message.money)));

                if (message.messageType == 0) {//消息类型(0:获得激励红包，5:支付买单凭证(作为服务管家),8:收到打赏)
                    holder.setText(R.id.tv_title, "约服收入凭证");
                    holder.setText(R.id.tv_describe, "收入来源：");
                    holder.setText(R.id.tv_orderdescription, "收到金额");
//                    holder.setViewVisible(R.id.iv_headimage);
//                    holder.setCircleImage(R.id.iv_headimage,message.fromUserHead);
                    holder.setViewGone(R.id.iv_headimage);
                    holder.setText(R.id.tv_orderfrom, "约服平台的激励红包");
                } else if (message.messageType == 5) {//支付买单凭证(作为服务管家)
                    holder.setText(R.id.tv_title, "消费流水凭证");
                    holder.setText(R.id.tv_describe, "流水来源：");
                    holder.setText(R.id.tv_orderdescription, "消费流水");
                  //  holder.setViewGone(R.id.iv_headimage);
                    holder.setViewVisible(R.id.iv_headimage);
                    holder.setCircleImage(R.id.iv_headimage, message.fromUserHead);
                    holder.setText(R.id.tv_orderfrom, message.fromUserName + "完成消费买单");
                } else if (message.messageType == 8) {//收到打赏
                    holder.setText(R.id.tv_title, "约服收入凭证");
                    holder.setText(R.id.tv_describe, "收入来源：");
                    holder.setText(R.id.tv_orderdescription, "收到金额");
                    holder.setText(R.id.tv_orderprice, "￥" + (message.bounseMoney == 0 ? "0.00" : Convert.getMoneyString(message.bounseMoney)));
//                    holder.setViewGone(R.id.iv_headimage);
                    holder.setViewVisible(R.id.iv_headimage);
                    holder.setCircleImage(R.id.iv_headimage, message.fromUserHead);
                    holder.setText(R.id.tv_orderfrom, message.fromUserName + "的赞赏");
                }

            }
        });

    }


    private void user_messageList_bill_1_2_2(final boolean isFresh) {
        if (userMessageListBill_1_2_2Request != null) {
            userMessageListBill_1_2_2Request.cancel();
        }
        UserMessageListBill_1_2_2Request.Input input = new UserMessageListBill_1_2_2Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        if (isFresh) {
            input.page = 1;
        } else {
            input.page = page;
        }

        input.rows = Constants.ROWS;     //每页显示的页数
        input.convertJosn();

        userMessageListBill_1_2_2Request = new UserMessageListBill_1_2_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                concernLv.stopRefresh();
                concernLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {

                if (((APIM_CommonSysMessageList) response).status == 1) {
                    if (isFresh) {
                        mDatas.clear();
                    }
                    mDatas.addAll(((APIM_CommonSysMessageList) response).messageList);
                    if (page == 1 && mDatas.size() == 0) {
                        rlNoData.setVisibility(View.VISIBLE);
                    } else {
                        rlNoData.setVisibility(View.GONE);
                    }
                    commonAdapter.notifyDataSetChanged();
                    if (((APIM_CommonSysMessageList) response).maxpage <= page) {
                        concernLv.setLoadMoreEnable(false);
                    } else {
                        concernLv.setLoadMoreEnable(true);
                        page++;
                    }

                    concernLv.stopRefresh();
                    concernLv.stopLoadMore();
                } else {
                    ToastUtils.show(OrderMessageActivity.this, ((APIM_CommonSysMessageList) response).info);
                }

            }
        });
        sendJsonRequest(userMessageListBill_1_2_2Request);
    }


    @OnClick({R.id.ll_back, R.id.lh_btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        user_messageList_bill_1_2_2(true);
    }

    @Override
    public void onLoadMore() {
        user_messageList_bill_1_2_2(false);
    }
}
