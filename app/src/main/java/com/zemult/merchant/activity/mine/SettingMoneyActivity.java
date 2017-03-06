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
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by admin on 2017/1/24.
 */

public class SettingMoneyActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.niname_et)
    EditText ninameEt;
    @Bind(R.id.ok_btn)
    Button okBtn;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_settingmoney);
    }

    @Override
    public void init() {
        lhTvTitle.setText("设置金额");
        okBtn.setBackgroundResource(R.drawable.next_bg_btn_select);
        EditFilter.CashFilter(ninameEt, Constants.MAX_PAY);
        ninameEt.addTextChangedListener(watcher);

    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (ninameEt.getText().toString().length() > 0) {
                    ninameEt.setHint("");
                    okBtn.setEnabled(true);
                    if (getMoney() > 0) {
                        okBtn.setBackgroundResource(R.drawable.common_selector_btn);
                    }
                }
            } else {
                ninameEt.setHint("请填写收款金额");
                okBtn.setEnabled(false);
                okBtn.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private double getMoney() {
        double result = 0;
        if (!StringUtils.isBlank(ninameEt.getText().toString())) {
            result = Double.parseDouble(ninameEt.getText().toString());
        }
        return result;
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.ok_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ok_btn:
                if (TextUtils.isEmpty(ninameEt.getText().toString())) {
                    ToastUtil.showMessage("请输入收款金额");
                } else {
                    Intent intent = new Intent();
                    //把返回数据存入Intent
                    intent.putExtra("result", ninameEt.getText().toString());
                    //设置返回数据
                    SettingMoneyActivity.this.setResult(RESULT_OK, intent);
//                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    SettingMoneyActivity.this.finish();
                }


                break;
        }
    }
}
