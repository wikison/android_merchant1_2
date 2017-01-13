package com.zemult.merchant.activity.mine.message;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserLabelAdd_1_2Request;
import com.zemult.merchant.aip.slash.ManagerNewsCommentAddRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class ReplyCommentActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_1)
    TextView tv1;

    @Bind(R.id.et_labeldestription)
    EditText etLabeldestription;

    UserLabelAdd_1_2Request userLabelAdd_1_2Request;
    String note,ruserName;
    int newsId,ruserId;
    ManagerNewsCommentAddRequest commentAddRequest;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_replycomment);
    }

    @Override
    public void init() {
        lhTvTitle.setText("评论回复");
        ruserName= getIntent().getStringExtra("ruserName");
        newsId=getIntent().getIntExtra("newsId",0);
        ruserId=getIntent().getIntExtra("ruserId",0);
        tv1.setText("回复"+ruserName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void addMoodComment() {
        if (commentAddRequest != null) {
            commentAddRequest.cancel();
        }
        ManagerNewsCommentAddRequest.Input input = new ManagerNewsCommentAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.newsId = newsId; // 信息id
        input.note = note;
        input.type = 1; // 类型(0:对信息评论;1:回复别人的评论 )
        input.ruserId = ruserId;
        input.convertJosn();
        commentAddRequest = new ManagerNewsCommentAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showPd();
            }

            @Override
            public void onResponse(Object response) {
             dismissPd();
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("回复成功");
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(commentAddRequest);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_send:
                note=etLabeldestription.getText().toString();
                if(StringUtils.isEmpty(note)){
                    ToastUtil.showMessage("请输入回复内容");
                }
                else{
                    addMoodComment();
                }

                break;
        }
    }
}
