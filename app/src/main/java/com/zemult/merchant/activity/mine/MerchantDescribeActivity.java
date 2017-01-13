package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;

/**
 * 商家简介
 *
 * @author djy
 * @time 2016/8/4 16:07
 */
public class MerchantDescribeActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_describe)
    EditText etDescribe;
    @Bind(R.id.btn_commit)
    Button btnCommit;

    public static final String INTENT_DESCRIBE = "describe";
    private Context mContext;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_business_describe);
    }

    @Override
    public void init() {
        mContext = this;
        if(!TextUtils.isEmpty(getIntent().getStringExtra(INTENT_DESCRIBE)))
            etDescribe.setText(getIntent().getStringExtra(INTENT_DESCRIBE));
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_commit:
                if(TextUtils.isEmpty(etDescribe.getText().toString().trim())){
                    ToastUtils.show(mContext, "请输入商家简介");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(INTENT_DESCRIBE, etDescribe.getText().toString().trim());
                setResult(RESULT_OK, intent);
                onBackPressed();
                break;
        }
    }
}
