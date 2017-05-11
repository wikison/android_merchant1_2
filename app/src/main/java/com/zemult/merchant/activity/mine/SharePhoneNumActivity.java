package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by admin on 2017/1/24.
 */

public class SharePhoneNumActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.phoneNum_et)
    EditText phoneNumEt;
    @Bind(R.id.ok_btn)
    Button okBtn;

    int tmpid;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_settingsharephonenum);
    }

    @Override
    public void init() {
        lhTvTitle.setText("手机联系人");
        okBtn.setBackgroundResource(R.drawable.next_bg_btn_select);
        phoneNumEt.addTextChangedListener(watcher);
        tmpid=getIntent().getIntExtra("tmpid",0);

    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (phoneNumEt.getText().toString().length() == 11) {
                    okBtn.setEnabled(true);
                    okBtn.setBackgroundResource(R.drawable.common_selector_btn);
                }
                else {
                    okBtn.setEnabled(false);
                    okBtn.setBackgroundResource(R.drawable.next_bg_btn_select);
                }
            } else {
                phoneNumEt.setHint("请输入客户手机号码");
                okBtn.setEnabled(false);
                okBtn.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.ok_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ok_btn:
                if (TextUtils.isEmpty(phoneNumEt.getText().toString())) {
                    ToastUtil.showMessage("请输入客户手机号码");
                } else {


                }


                break;
        }
    }
}
