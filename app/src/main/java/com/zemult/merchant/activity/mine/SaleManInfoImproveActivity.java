package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.HeadManageActivity;
import com.zemult.merchant.aip.mine.UserEditinfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class SaleManInfoImproveActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.tongxunlu_tv)
    TextView tongxunluTv;
    @Bind(R.id.notice_tv)
    TextView noticeTv;
    @Bind(R.id.btn_save)
    Button btnSave;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    UserEditinfoRequest userEditinfoRequest;

    String  headString="";

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_sale_man_info_improve);
    }

    @Override
    public void init() {
        lhTvTitle.setText("完善资料");
        initData();
        registerReceiver(new String[]{Constants.BROCAST_EDITUSERINFO});
        EditFilter.WordFilter(etName, 6);
    }

    private void initData() {
        if (!TextUtils.isEmpty(SlashHelper.userManager().getUserinfo().getHead())) {
            imageManager.loadCircleHead(SlashHelper.userManager().getUserinfo().getHead(), ivHead);
        }

    }

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_EDITUSERINFO.equals(intent.getAction())) {
            initData();
        }
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_head,R.id.tongxunlu_tv, R.id.notice_tv, R.id.btn_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:

                onBackPressed();
                break;
            case R.id.iv_head:
                startActivityForResult(new Intent(this, HeadManageActivity.class), 110);
                break;
            case R.id.tongxunlu_tv:
                break;
            case R.id.notice_tv:
                break;
            case R.id.btn_save:
                if (StringUtils.isBlank(etName.getText().toString())) {
                    Toast.makeText(this, "请输入您的昵称", Toast.LENGTH_SHORT).show();
                } else {
                    user_editinfo();
                }
                break;
        }
    }

    //修改用户资料信息
    private void user_editinfo() {
        showPd();
        if (userEditinfoRequest != null) {
            userEditinfoRequest.cancel();
        }
        UserEditinfoRequest.Input input = new UserEditinfoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
            input.name = etName.getText().toString();
            input.convertJosn();
        }

        userEditinfoRequest = new UserEditinfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    SlashHelper.userManager().getUserinfo().setName(etName.getText().toString());
                    SlashHelper.userManager().saveUserinfo(SlashHelper.userManager().getUserinfo());
                    setResult(RESULT_OK);
                    SaleManInfoImproveActivity.this.finish();

                } else {
                    ToastUtils.show(SaleManInfoImproveActivity.this, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userEditinfoRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == 110 && resultCode == RESULT_OK) {
            headString = SlashHelper.userManager().getUserinfo().getHead();
            imageManager.loadCircleHead(headString, ivHead);
        }
    }

}
