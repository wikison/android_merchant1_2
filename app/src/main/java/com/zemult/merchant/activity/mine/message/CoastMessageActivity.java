package com.zemult.merchant.activity.mine.message;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.UserMessageListConsumer_1_3Request;
import com.zemult.merchant.aip.task.TaskIndustryRecordInfoRequest;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Message;
import com.zemult.merchant.model.apimodel.APIM_CommonSysMessageList;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
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

public class CoastMessageActivity extends MBaseActivity implements SmoothListView.ISmoothListViewListener {
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
    UserMessageListConsumer_1_3Request userMessageListConsumer_1_3Request;
    TaskIndustryRecordInfoRequest taskIndustryRecordInfoRequest;
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
        lhTvTitle.setText("消费任务消息");
        concernLv.setRefreshEnable(true);
        concernLv.setLoadMoreEnable(false);
        concernLv.setSmoothListViewListener(this);
        userMessageListConsumer_1_3Request(true);
        concernLv.setAdapter( commonAdapter=new CommonAdapter<M_Message>(CoastMessageActivity.this, R.layout.item_coastmessage_result, mDatas) {
            @Override
            public void convert(CommonViewHolder holder, final M_Message message, final int position) {
                holder.setText(R.id.tv_title_message, message.title==null?"":message.title);
                holder.setText(R.id.tv_messagedate, message.createtime);
                    holder.setOnclickListener(R.id.ll_coastmessage, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IntentUtil.intStart_activity(CoastMessageActivity.this,PayBusinessActivity.class,new Pair<String, Integer>("taskIndustryRecordId",message.infoId));
                        }
                    });
            }
        });
    }

    public void taskIndustryRecordInfoRequest(final int taskIndustryRecordId) {
        showPd();
        if (taskIndustryRecordInfoRequest != null) {
            taskIndustryRecordInfoRequest.cancel();
        }
        TaskIndustryRecordInfoRequest.Input input = new TaskIndustryRecordInfoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.taskIndustryRecordId = taskIndustryRecordId;
        input.convertJosn();

        taskIndustryRecordInfoRequest = new TaskIndustryRecordInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_TaskIndustryInfo) response).status == 1) {
                    Intent intent = new Intent(CoastMessageActivity.this, TaskDetailActivity.class);
                    ((APIM_TaskIndustryInfo) response).taskIndustryRecordInfo.setTaskIndustryRecordId(taskIndustryRecordId);
                    intent.putExtra(TaskDetailActivity.INTENT_TASK, ((APIM_TaskIndustryInfo) response).taskIndustryRecordInfo);
                    startActivity(intent);
                } else {
                    ToastUtil.showMessage(((APIM_TaskIndustryInfo) response).info);
                }
            }
        });
        sendJsonRequest(taskIndustryRecordInfoRequest);
    }

    private void userMessageListConsumer_1_3Request(final boolean  isFresh) {
        if (userMessageListConsumer_1_3Request != null) {
            userMessageListConsumer_1_3Request.cancel();
        }
        UserMessageListConsumer_1_3Request.Input input = new UserMessageListConsumer_1_3Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        if(isFresh){
            input.page = 1;
        }
        else{
            input.page = page;
        }

        input.rows = Constants.ROWS;     //每页显示的页数
        input.convertJosn();

        userMessageListConsumer_1_3Request = new UserMessageListConsumer_1_3Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                concernLv.stopRefresh();
                concernLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {

                if (((APIM_CommonSysMessageList) response).status == 1) {
                    if(isFresh){
                        mDatas.clear();
                    }
                    mDatas.addAll (((APIM_CommonSysMessageList) response).messageList);
                    if(page==1&&mDatas.size()==0){
                        rlNoData.setVisibility(View.VISIBLE);
                    }
                    else {
                        rlNoData.setVisibility(View.GONE);
                    }
                    commonAdapter.notifyDataSetChanged();
                    if(((APIM_CommonSysMessageList) response).maxpage<=page){
                        concernLv.setLoadMoreEnable(false);
                    }
                    else{
                        concernLv.setLoadMoreEnable(true);
                        page++;
                    }

                    concernLv.stopRefresh();
                    concernLv.stopLoadMore();
                } else {
                    ToastUtils.show(CoastMessageActivity.this, ((APIM_CommonSysMessageList) response).info);
                }

            }
        });
        sendJsonRequest(userMessageListConsumer_1_3Request);
    }



    @OnClick({R.id.ll_back,R.id.lh_btn_back})
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
        userMessageListConsumer_1_3Request(true);
    }

    @Override
    public void onLoadMore() {
        userMessageListConsumer_1_3Request(false);
    }
}
