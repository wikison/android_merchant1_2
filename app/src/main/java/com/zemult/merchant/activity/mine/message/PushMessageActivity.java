package com.zemult.merchant.activity.mine.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserMessageList_1_2Request;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.apimodel.APIM_CommonSysMessageList;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

import static com.zemult.merchant.config.Constants.BROCAST_UPDATESYSMESSAGE;

public class PushMessageActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_comment_count)
    TextView tvCommentCount;
    @Bind(R.id.tv_zan_count)
    TextView tvZanCount;
    @Bind(R.id.tv_sysmessage_count)
    TextView tvSysmessageCount;
    @Bind(R.id.tv_atme_count)
    TextView tvAtmeCount;

    @Bind(R.id.tv_noticemessage_count)
    TextView tvNoticemessage_count;
    @Bind(R.id.tv_coastmessage_count)
    TextView tvCoastmessage_count;


    @Bind(R.id.rl_atme)
    RelativeLayout rlAtme;
    @Bind(R.id.rl_commentlist)
    RelativeLayout rlCommentlist;
    @Bind(R.id.rl_zanlsit)
    RelativeLayout rlZanlsit;
    @Bind(R.id.rl_sysmessage)
    RelativeLayout rlSysmessage;

    @Bind(R.id.rl_coastmessage)
    RelativeLayout rlCoastmessage;
    @Bind(R.id.rl_topicmessage)
    RelativeLayout rlTopicmessage;
    @Bind(R.id.rl_noticemessage)
    RelativeLayout rlNoticemessage;

    UserMessageList_1_2Request userMessageList_1_2Request;
    int zanNum,commentNum,atmeNum,sysNum,tzNum,xfNum;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_push_message);
    }

    @Override
    public void init() {
        lhTvTitle.setText("消息");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        sendBroadcast(new Intent(BROCAST_UPDATESYSMESSAGE));
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        user_messageList_1_2();
    }

    private void user_messageList_1_2() {
        if (userMessageList_1_2Request != null) {
            userMessageList_1_2Request.cancel();
        }
        UserMessageList_1_2Request.Input input = new UserMessageList_1_2Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.convertJosn();

        userMessageList_1_2Request = new UserMessageList_1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_CommonSysMessageList) response).status == 1) {
                    for(int i=0;i< ((APIM_CommonSysMessageList) response).messageList.size();i++){
                        if(((APIM_CommonSysMessageList) response).messageList.get(i).type==1){//消息类型(1:@我的,2:评论,3:赞,4:系统消息,5:通知消息,6:消费任务消息)
                            atmeNum =((APIM_CommonSysMessageList) response).messageList.get(i).num;
                            if(atmeNum!=0){
                                tvAtmeCount.setText(atmeNum+"");
                                tvAtmeCount.setVisibility(View.VISIBLE);
                            }
                            else{
                                tvAtmeCount.setVisibility(View.INVISIBLE);
                            }
                        }
                        if(((APIM_CommonSysMessageList) response).messageList.get(i).type==2){//消息类型(1:@我的,2:评论,3:赞,4:系统消息,5:通知消息,6:消费任务消息)
                            commentNum =((APIM_CommonSysMessageList) response).messageList.get(i).num;
                            if(commentNum!=0){
                                tvCommentCount.setText(commentNum+"");
                                tvCommentCount.setVisibility(View.VISIBLE);
                            }
                            else{
                                tvCommentCount.setVisibility(View.INVISIBLE);
                            }
                        }
                        if(((APIM_CommonSysMessageList) response).messageList.get(i).type==3){//消息类型(1:@我的,2:评论,3:赞,4:系统消息,5:通知消息,6:消费任务消息)
                            zanNum =((APIM_CommonSysMessageList) response).messageList.get(i).num;
                            if(zanNum!=0){
                                tvZanCount.setText(zanNum+"");
                                tvZanCount.setVisibility(View.VISIBLE);
                            }else{
                                tvZanCount.setVisibility(View.INVISIBLE);
                            }
                        }
                        if(((APIM_CommonSysMessageList) response).messageList.get(i).type==4){//消息类型(1:@我的,2:评论,3:赞,4:系统消息,5:通知消息,6:消费任务消息)
                            sysNum=((APIM_CommonSysMessageList) response).messageList.get(i).num;
                            if(sysNum!=0){
                                tvSysmessageCount.setText(sysNum+"");
                                tvSysmessageCount.setVisibility(View.VISIBLE);
                            }else{
                                tvSysmessageCount.setVisibility(View.INVISIBLE);
                            }
                        }
                        if(((APIM_CommonSysMessageList) response).messageList.get(i).type==5){//消息类型(1:@我的,2:评论,3:赞,4:系统消息,5:通知消息,6:消费任务消息)
                            tzNum=((APIM_CommonSysMessageList) response).messageList.get(i).num;
                            if(tzNum!=0){
                                tvNoticemessage_count.setText(tzNum+"");
                                tvNoticemessage_count.setVisibility(View.VISIBLE);
                            }else{
                                tvNoticemessage_count.setVisibility(View.INVISIBLE);
                            }
                        }

                        if(((APIM_CommonSysMessageList) response).messageList.get(i).type==6){//消息类型(1:@我的,2:评论,3:赞,4:系统消息,5:通知消息,6:消费任务消息)
                            xfNum=((APIM_CommonSysMessageList) response).messageList.get(i).num;
                            if(xfNum!=0){
                                tvCoastmessage_count.setText(xfNum+"");
                                tvCoastmessage_count.setVisibility(View.VISIBLE);
                            }else{
                                tvCoastmessage_count.setVisibility(View.INVISIBLE);
                            }
                        }

                    }
                    if((zanNum+commentNum+atmeNum+sysNum+tzNum+xfNum)!=0){
                        lhTvTitle.setText("消息("+(zanNum+commentNum+atmeNum+sysNum+tzNum+xfNum)+")");
                    }
                    else{
                        lhTvTitle.setText("消息");
                    }

                } else {
                    ToastUtils.show(PushMessageActivity.this, ((APIM_CommonSysMessageList) response).info);
                }
                SlashHelper.setSettingString(SlashHelper.User.Key.UNREADMESSAGE,"");
            }
        });
        sendJsonRequest(userMessageList_1_2Request);
    }

    @OnClick({R.id.lh_btn_back, R.id.rl_coastmessage , R.id.rl_noticemessage,  R.id.rl_topicmessage,R.id.ll_back, R.id.rl_atme, R.id.rl_commentlist, R.id.rl_zanlsit, R.id.rl_sysmessage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.rl_atme:
                Intent intent =new Intent(PushMessageActivity.this,AtMeListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_commentlist:
                Intent intent2 =new Intent(PushMessageActivity.this,CommentListActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_zanlsit:
                Intent intent3 =new Intent(PushMessageActivity.this,ZanListActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_sysmessage:
                Intent intent4 =new Intent(PushMessageActivity.this,SystemMessageActivity.class);
                startActivity(intent4);
                break;
            case R.id.rl_topicmessage:
                Intent intent5 =new Intent(PushMessageActivity.this,TopicMessageActivity.class);
                startActivity(intent5);
                break;
            case R.id.rl_coastmessage:
                Intent intent6 =new Intent(PushMessageActivity.this,CoastMessageActivity.class);
                startActivity(intent6);
                break;
            case R.id.rl_noticemessage:
                Intent intent7 =new Intent(PushMessageActivity.this,NoticeMessageActivity.class);
                startActivity(intent7);
                break;

        }
    }
}
