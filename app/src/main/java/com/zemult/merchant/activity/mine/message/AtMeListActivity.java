package com.zemult.merchant.activity.mine.message;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.SearchDetailActivity;
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.UserMessageListAlert_1_2Request;
import com.zemult.merchant.aip.task.TaskIndustryRecordInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Message;
import com.zemult.merchant.model.apimodel.APIM_CommonSysMessageList;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class AtMeListActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener  {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.smoothListView)
    SmoothListView concernLv;
    UserMessageListAlert_1_2Request userMessageListAlert_1_2Request;
    TaskIndustryRecordInfoRequest taskIndustryRecordInfoRequest;
    List<M_Message> mDatas = new ArrayList<M_Message>();
    CommonAdapter commonAdapter;
    private int page = 1;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_commonlist);
    }

    @Override
    public void init() {
        lhTvTitle.setText("@我");

        concernLv.setRefreshEnable(true);
        concernLv.setLoadMoreEnable(false);
        concernLv.setSmoothListViewListener(AtMeListActivity.this);
        user_messageList_alert_1_2(true);
        concernLv.setAdapter( commonAdapter=new CommonAdapter<M_Message>(AtMeListActivity.this, R.layout.item_atme, mDatas) {
            @Override
            public void convert(CommonViewHolder holder, final M_Message message, final int position) {

                if (!TextUtils.isEmpty(message.userHead))
                    holder.setCircleImage( R.id.iv_head,message.userHead);
                else
                    holder.setImageResource( R.id.iv_head,R.mipmap.user_icon);
                // 用户名
                if (!TextUtils.isEmpty(message.userName))
                    holder.setText(R.id.tv_user_name,message.userName);
                    holder.setViewGone(R.id.ll_user_industry);
                holder.setText(R.id.tv_title,message.title);
                if(!StringUtils.isEmpty(message.createtime)){
                    holder.setText(R.id.tv_left,message.createtime);
                }
                else{
                    holder.setText(R.id.tv_left,"");
                }
                holder.setText(R.id.tv_atwho,"@"+SlashHelper.userManager().getUserinfo().getName());
                holder.setOnclickListener(R.id.ll_root_view, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//消息类型(5:@我的探索,6:@我的探索完成记录)
                        if(message.infoType==6){
                            taskIndustryRecordInfoRequest(message.infoId);
                        }
                        if(message.infoType==5){
                            Intent intent = new Intent(AtMeListActivity.this, SearchDetailActivity.class);
                            intent.putExtra(SearchDetailActivity.INTENT_TASKINDUSTRYID, message.infoId);
                            startActivity(intent);
                        }

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
                    Intent intent = new Intent(AtMeListActivity.this, TaskDetailActivity.class);
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

    private void user_messageList_alert_1_2(final boolean  isFresh) {
        if (userMessageListAlert_1_2Request != null) {
            userMessageListAlert_1_2Request.cancel();
        }
        UserMessageListAlert_1_2Request.Input input = new UserMessageListAlert_1_2Request.Input();
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

        userMessageListAlert_1_2Request = new UserMessageListAlert_1_2Request(input, new ResponseListener() {
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
                    mDatas.addAll(((APIM_CommonSysMessageList) response).messageList);
                    if ( page==1&&mDatas.size()==0){
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
                    ToastUtils.show(AtMeListActivity.this, ((APIM_CommonSysMessageList) response).info);
                }

            }
        });
        sendJsonRequest(userMessageListAlert_1_2Request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        user_messageList_alert_1_2(true);
    }

    @Override
    public void onLoadMore() {
        user_messageList_alert_1_2(false);
    }
}
