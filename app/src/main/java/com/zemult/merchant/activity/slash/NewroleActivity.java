package com.zemult.merchant.activity.slash;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.slash.UserFeedindustryRequest;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class NewroleActivity extends MBaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.newrole_name_et)
    EditText newroleNameEt;
    @Bind(R.id.newrole_industry_et)
    EditText newroleIndustryEt;

    @Bind(R.id.newrole_intro_et)
    EditText newroleIntroEt;

    UserFeedindustryRequest userFeedindustryRequest;
    String industryName, roleName, merchantName, note;
    @Bind(R.id.tv_right)
    TextView tvRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newrole);
        ButterKnife.bind(this);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("建议角色");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("提交");
    }

    //提交新角色
    private void user_feedindustry() {
        if (userFeedindustryRequest != null) {
            userFeedindustryRequest.cancel();
        }

        UserFeedindustryRequest.Input input = new UserFeedindustryRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.industryName = industryName;//	角色行业
        input.roleName = roleName;//	角色名称
        // input.merchantName = "";//	角色场景(商户)
        input.note = note;//	角色职能描述
        input.convertJosn();
        userFeedindustryRequest = new UserFeedindustryRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(NewroleActivity.this, "提交成功");
                    finish();
                } else {
                    ToastUtils.show(NewroleActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userFeedindustryRequest);
    }

    @OnClick({R.id.lh_btn_back, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
                finish();
                break;
            case R.id.tv_right:
                roleName = newroleNameEt.getText().toString().trim();
                industryName = newroleIndustryEt.getText().toString().trim();
                note = newroleIntroEt.getText().toString().trim();

                if (TextUtils.isEmpty(roleName)) {
                    ToastUtil.showMessage("请输入角色名称");
                    return;
                }
                if (TextUtils.isEmpty(industryName)) {
                    ToastUtil.showMessage("请输入角色所属的行业");
                    return;
                }
//                if (TextUtils.isEmpty(merchantName)) {
//                    ToastUtil.showMessage("请输入角色所属的场景");
//                    return;
//                }
                if (TextUtils.isEmpty(note)) {
                    ToastUtil.showMessage("请描述角色的定义");
                    return;
                }
                user_feedindustry();
                break;
        }
    }


}
