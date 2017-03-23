package com.zemult.merchant.activity.slash;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.aip.slash.UserAddSaleUserRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class ConnectLocalPhoneActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.tv_tip)
    TextView tvTip;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.btn_next)
    Button btnNext;

    private int merchantId, isOnBook;
    private String tags, bookPhones, name;
    private Context mContext;
    private Boolean refuse;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_connect_local_phone);
    }

    @Override
    public void init() {
        mContext = this;
        lhTvTitle.setText("成为服务管家");
        tvRight.setText("跳过");
        tvRight.setVisibility(View.VISIBLE);

        merchantId = getIntent().getIntExtra(TabManageActivity.TAG, -1);
        tags = getIntent().getStringExtra(TabManageActivity.TAGS);
        name = getIntent().getStringExtra(TabManageActivity.NAME);

        refuse = getIntent().getBooleanExtra("refuse", false);

        if(refuse){
            btnNext.setVisibility(View.GONE);
            tvTip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_right:
                isOnBook = 0;
                user_add_saleuser();
                break;
            case R.id.btn_next:
                SPUtils.put(mContext, "has_req_contacts", true);
                ActivityCompat.requestPermissions(ConnectLocalPhoneActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
                break;
        }
    }

    /**
     * 用户申请成为商家的营销经理
     */
    UserAddSaleUserRequest userAddSaleUserRequest;

    private void user_add_saleuser() {
        showPd();
        if (userAddSaleUserRequest != null) {
            userAddSaleUserRequest.cancel();
        }

        UserAddSaleUserRequest.Input input = new UserAddSaleUserRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.tags = tags;
        input.isOnBook = isOnBook;
        input.bookPhones = bookPhones;
        input.convertJosn();
        userAddSaleUserRequest = new UserAddSaleUserRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    SlashHelper.userManager().getUserinfo().setIsOnBook(1);
                    Intent it = new Intent(mContext, BeManagerSuccessActivity.class);
                    it.putExtra(TabManageActivity.NAME, name);
                    startActivity(it);

                    onBackPressed();  //关闭自己
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userAddSaleUserRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0) {
//           if(grantResults[0] == PackageManager.PERMISSION_GRANTED) //经测试，对于vivo和redmi永远返回0
//               getContactsYes();
//            else
//               getContactsNo();

            try {
                bookPhones = AppUtils.getPhoneNumbers(mContext);
                // 只能用这种折中的方法了
                if (StringUtils.isBlank(bookPhones)) {
                    isOnBook = 0;
                } else {
                    isOnBook = 1;
                }
            }catch (Exception e){
                isOnBook = 0;
            }
            user_add_saleuser();
        }
    }
}
