package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserRealnameInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

public class TrueNameResultActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.et_phone)
    TextView etPhone;
    @Bind(R.id.et_name)
    TextView etName;
    @Bind(R.id.et_idnumber)
    TextView etIdnumber;
    @Bind(R.id.bt_over)
    Button btOver;
    String username,userno,userphone;
    UserRealnameInfoRequest userRealnameInfoRequest;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_true_name_result);
    }

    @Override
    public void init() {
        lhTvTitle.setText("实名认证");
        user_realname_info();
    }


    private void user_realname_info() {

        try {
            if (userRealnameInfoRequest != null) {
                userRealnameInfoRequest.cancel();
            }
            final UserRealnameInfoRequest.Input input = new UserRealnameInfoRequest.Input();
            input.userId = SlashHelper.userManager().getUserId()+"";
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
                        etPhone.setText(AppUtils.getHiddenMobile(((CommonResult) response).mobileNum+""));
                        if(((CommonResult) response).IDNum.length()>9){
                            String idnum1=((CommonResult) response).IDNum.substring(0,4);
                            String idnum2=((CommonResult) response).IDNum.substring(((CommonResult) response).IDNum.length()-4,((CommonResult) response).IDNum.length());
                            etIdnumber.setText(idnum1+"******"+idnum2);
                        }
                        else{
                            etIdnumber.setText("******");
                        }
                        if((((CommonResult) response).realName).length()>1){
                            String name=((CommonResult) response).realName.substring(1,((CommonResult) response).realName.length());
                            etName.setText("*"+name);
                        }else{
                            etName.setText("******");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.bt_over})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
            case R.id.bt_over:
                finish();
                break;
        }
    }
}
