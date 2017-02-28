package com.zemult.merchant.activity.mine;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserAttractEditRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class RemarkNameActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_clear)
    ImageView ivClear;
    @Bind(R.id.et_name)
    EditText etName;
    private String name;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_remark_name);
    }

    @Override
    public void init() {
        lhTvTitle.setText("设置备注名");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("保存");
        if (!TextUtils.isEmpty(getIntent().getStringExtra("name")))
            etName.setText(getIntent().getStringExtra("name"));

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length()>0)
                    ivClear.setVisibility(View.VISIBLE);
                else
                    ivClear.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right, R.id.iv_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_right:
                if(StringUtils.isBlank(etName.getText().toString())){
                    ToastUtil.showMessage("请输入备注名");
                    return;
                }
                user_attract_edit();
                break;
            case R.id.iv_clear:
                etName.setText("");
                break;
        }
    }

    private UserAttractEditRequest attractEditRequest;

    // 设置备注名
    private void user_attract_edit() {
        showPd();
        if (attractEditRequest != null) {
            attractEditRequest.cancel();
        }
        final UserAttractEditRequest.Input input = new UserAttractEditRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.attractId = getIntent().getIntExtra("attractId", 0); // 被关注的用户id
        input.note = etName.getText().toString();

        input.convertJosn();
        attractEditRequest = new UserAttractEditRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("设置成功");
                    Intent intent = new Intent();
                    intent.putExtra("name",  etName.getText().toString());
                    setResult(RESULT_OK, intent);

                    onBackPressed();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractEditRequest);
    }


}
