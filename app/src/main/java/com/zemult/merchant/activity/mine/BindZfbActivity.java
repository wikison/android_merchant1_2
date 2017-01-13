package com.zemult.merchant.activity.mine;


import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.common.MMAlert;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 绑定支付宝
 * @author djy
 * @time 2016/12/5 14:41
 */
public class BindZfbActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_zfb)
    EditText etZfb;
    @Bind(R.id.btn_commit)
    Button btnCommit;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bind_zfb);
    }

    @Override
    public void init() {
        lhTvTitle.setText("绑定支付宝");
        if(!TextUtils.isEmpty(getIntent().getStringExtra("aliAccount")))
            etZfb.setText(getIntent().getStringExtra("aliAccount"));
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_commit:
                if(TextUtils.isEmpty(etZfb.getText().toString())){
                ToastUtil.showMessage("请输入支付宝账号");
                return;
                }
                MMAlert.showOneOperateDialog(this, "已成功绑定支付宝", "确定", new MMAlert.OneOperateCallback() {
                    @Override
                    public void onOneOperate() {
                        Intent intent = new Intent();
                        intent.putExtra("moneyType", 1);
                        intent.putExtra("aliAccount", etZfb.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

                break;
        }
    }
}
