package com.zemult.merchant.activity.mine.pwdsetting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserRealnameInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class IdnoAuthActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_idno)
    EditText etIdno;
    @Bind(R.id.btn_bangding)
    Button btnBangding;
    String strIdNo;

    UserRealnameInfoRequest userRealnameInfoRequest;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_idno_auth);
    }

    @Override
    public void init() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lhTvTitle.setText("更换绑定");
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bangding})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_bangding:
                strIdNo = etIdno.getText().toString();
                if (!StringUtils.isEmpty(strIdNo)) {
                    user_realname_info();
                } else {
                    etIdno.setError("请输入身份证后8位");
                }
                break;
        }
    }

    private void user_realname_info() {

        try {
            if (userRealnameInfoRequest != null) {
                userRealnameInfoRequest.cancel();
            }
            final UserRealnameInfoRequest.Input input = new UserRealnameInfoRequest.Input();
            input.userId = SlashHelper.userManager().getUserId() + "";
            input.convertJosn();

            userRealnameInfoRequest = new UserRealnameInfoRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        String idNum = ((CommonResult) response).IDNum;
                        if (((CommonResult) response).IDNum.length() > 9) {
                            if (strIdNo.equalsIgnoreCase(idNum.substring(idNum.length() - 8, idNum.length()))) {
                                Intent intent = new Intent(IdnoAuthActivity.this, NewPhoneAuthActivity.class);
                                intent.putExtra("strIdNo", strIdNo);
                                startActivity(intent);
                            } else {
                                ToastUtil.showMessage("身份证号验证错误");
                            }
                        } else {
                            ToastUtil.showMessage("实名认证身份证号错误, 请联系管理员");
                        }


                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(userRealnameInfoRequest);
        } catch (Exception e) {
        }

    }


}
