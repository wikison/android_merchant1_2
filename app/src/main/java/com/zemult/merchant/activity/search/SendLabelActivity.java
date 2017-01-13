package com.zemult.merchant.activity.search;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserLabelAdd_1_2Request;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class SendLabelActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_labeldestription)
    EditText etLabeldestription;
    @Bind(R.id.btn_send)
    Button btnSend;
    @Bind(R.id.tv_labelname)
    RadioButton tvLabelname;

    UserLabelAdd_1_2Request userLabelAdd_1_2Request;
    String labeldescroption,labelName,labelId;
    int friendId;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_send_label);
    }

    @Override
    public void init() {
        lhTvTitle.setText("贴标签");
        labelName= getIntent().getStringExtra("labelName");
        labelId=getIntent().getStringExtra("labelId");
        friendId=getIntent().getIntExtra("friendId",0);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(2, Color.RED); // 边框粗细及颜色
        drawable.setCornerRadii(new float[]{25,
                25, 25, 25, 25, 25, 25, 25});
        drawable.setColor(0x22FFFFFF);  // 边框内部颜色
        tvLabelname.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
        tvLabelname.setTextSize(12);
        tvLabelname.setPadding(12, 8, 12, 8);
        tvLabelname.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
        tvLabelname.setTextColor(Color.RED);
        tvLabelname.setText(labelName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //贴标签
    private void user_label_add_1_2() {
        if (userLabelAdd_1_2Request != null) {
            userLabelAdd_1_2Request.cancel();
        }

        UserLabelAdd_1_2Request.Input input = new UserLabelAdd_1_2Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.operateUserId= SlashHelper.userManager().getUserId();
        }
        input.tagId=Integer.parseInt(labelId);
        input.userId=friendId;
        input.note=labeldescroption;
        input.convertJosn();
        userLabelAdd_1_2Request = new UserLabelAdd_1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("发送成功");
                    Intent intent =new Intent(Constants.BROCAST_CLOSESendLabelActivity);
                    sendBroadcast(intent);
                    finish();
                } else {
                    ToastUtils.show(SendLabelActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userLabelAdd_1_2Request);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_send:
                labeldescroption=etLabeldestription.getText().toString();
                if(StringUtils.isEmpty(labeldescroption)){
                    ToastUtil.showMessage("请对他说点什么");
                }
                else{
                    user_label_add_1_2();
                }

                break;
        }
    }
}
