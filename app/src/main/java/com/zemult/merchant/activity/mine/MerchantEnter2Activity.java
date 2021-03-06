package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.SfriendActivity;
import com.zemult.merchant.aip.slash.MerchantAddentity1_1Request;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.common.CommonDialog;
import com.zemult.merchant.view.common.MMAlert;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class MerchantEnter2Activity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_address)
    EditText etAddress;
    @Bind(R.id.et_person_name)
    EditText etPersonName;
    @Bind(R.id.et_person_phone)
    EditText etPersonPhone;
    @Bind(R.id.btn_commit)
    Button btnCommit;
    @Bind(R.id.activity_merchant_enter2)
    LinearLayout activityMerchantEnter2;
    private Context mContext;

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (etName.getText().toString().length() > 0
                        && etAddress.getText().toString().length() > 0
                        && etPersonName.getText().toString().length() > 0
                        && etPersonPhone.getText().toString().length() > 0
                        ) {
                    btnCommit.setEnabled(true);
                    btnCommit.setBackgroundResource(R.drawable.common_selector_btn);
                }

            } else {
                btnCommit.setEnabled(false);
                btnCommit.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_merchant_enter2);
    }

    @Override
    public void init() {
        mContext = this;
        lhTvTitle.setText("商户入驻");
        EditFilter.WordFilter(etName, 20);
        EditFilter.WordFilter(etAddress, 50);
        EditFilter.WordFilter(etPersonName, 15);

        etName.addTextChangedListener(watcher);
        etAddress.addTextChangedListener(watcher);
        etPersonName.addTextChangedListener(watcher);
        etPersonPhone.addTextChangedListener(watcher);

        btnCommit.setEnabled(false);
        btnCommit.setBackgroundResource(R.drawable.next_bg_btn_select);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_commit:
                if(StringMatchUtils.isMobileNO(etPersonPhone.getText().toString())
                        || StringMatchUtils.isFixedPhone(etPersonPhone.getText().toString())){
                    merchant_addentity_new();
                }else
                    etPersonPhone.setError("联系电话格式不正确");

                break;
        }
    }

    private MerchantAddentity1_1Request merchantAddentityNewRequest;
    //实体商户入驻--添加全新的商户时
    private void merchant_addentity_new() {
        if (merchantAddentityNewRequest != null) {
            merchantAddentityNewRequest.cancel();
        }
        MerchantAddentity1_1Request.Input input = new MerchantAddentity1_1Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
            input.name = etName.getText().toString();
            input.address = etAddress.getText().toString();
            input.tel = etPersonPhone.getText().toString();
            input.userName = etPersonName.getText().toString();

            input.convertJosn();
        }

        merchantAddentityNewRequest = new MerchantAddentity1_1Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {

                    MMAlert.showConfirmDialog(mContext, "恭喜您提交成功", "将有平台专员在24小时与您联系\n请耐心等待", "确定", new MMAlert.OneOperateCallback() {
                        @Override
                        public void onOneOperate() {
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(merchantAddentityNewRequest);
    }

}
