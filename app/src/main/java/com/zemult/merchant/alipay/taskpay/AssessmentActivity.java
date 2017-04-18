package com.zemult.merchant.alipay.taskpay;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.hedgehog.ratingbar.RatingBar;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.slash.UserMerchantPayCommont_1_1Request;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

public class AssessmentActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_userhead)
    ImageView ivUserhead;
    @Bind(R.id.tv_username)
    TextView tvUsername;
    @Bind(R.id.tv_shopname)
    TextView tvShopname;
    @Bind(R.id.ratingbar)
    RatingBar ratingbar;
    @Bind(R.id.btn_topinjia)
    Button btnTopinjia;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    int comment = 0;
    int userPayId;
    private String managerhead, managername, merchantName;

    UserMerchantPayCommont_1_1Request userMerchantPayCommont_1_1Request;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_assessment);
    }

    @Override
    public void init() {
        lhTvTitle.setText("评价");
        userPayId = getIntent().getIntExtra("userPayId", -1);
        managerhead = getIntent().getStringExtra("managerhead") == null ? "" : getIntent().getStringExtra("managerhead");
        managername = getIntent().getStringExtra("managername") == null ? "" : getIntent().getStringExtra("managername");
        merchantName = getIntent().getStringExtra("merchantName") == null ? "" : getIntent().getStringExtra("merchantName");
        tvUsername.setText(managername);
        tvShopname.setText(merchantName);
        if (!TextUtils.isEmpty(managerhead)) {
            imageManager.loadCircleImage(managerhead, ivUserhead);
        }

        ratingbar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float RatingCount) {
                comment = (int) RatingCount;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    // 用户添加关注
    private void userMerchantPayCommont_1_1Request() {
        showPd();
        if (userMerchantPayCommont_1_1Request != null) {
            userMerchantPayCommont_1_1Request.cancel();
        }
        UserMerchantPayCommont_1_1Request.Input input = new UserMerchantPayCommont_1_1Request.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.userPayId = userPayId;
        input.comment = comment;
        input.convertJosn();
        userMerchantPayCommont_1_1Request = new UserMerchantPayCommont_1_1Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("感谢您的评价");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userMerchantPayCommont_1_1Request);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_topinjia})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_topinjia:
                if (comment == 0) {
                    ToastUtil.showMessage("您还没有打分");
                    return;
                }
                userMerchantPayCommont_1_1Request();
                break;
        }
    }
}
