package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonFindBankNameRequest;
import com.zemult.merchant.aip.mine.UserBandcardInfo1_2_1Request;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.fragment.BindCardFragmentCallBack;
import com.zemult.merchant.fragment.BindCardOneFragment;
import com.zemult.merchant.fragment.BindCardSuccessFragment;
import com.zemult.merchant.fragment.BindCardTwoFragment;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_CommonAppVersion;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

/**
 * 绑定银行卡主页面
 */
public class BindBankCardActivity extends BaseActivity implements BindCardFragmentCallBack {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv1)
    ImageView iv1;
    @Bind(R.id.iv2)
    ImageView iv2;
    @Bind(R.id.iv3)
    ImageView iv3;
    @Bind(R.id.ll_head)
    LinearLayout llHead;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.content)
    FrameLayout content;
    @Bind(R.id.tv2)
    TextView tv2;
    @Bind(R.id.divider1)
    View divider1;
    @Bind(R.id.divider2)
    View divider2;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private BindCardOneFragment oneFragment;
    private BindCardTwoFragment twoFragment;
    //    private BindCardThreeFragment threeFragment;
    private BindCardSuccessFragment successFragment;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bind_bank_card);
    }

    @Override
    public void init() {
        llBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText(R.string.title_bind_card);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        oneFragment = new BindCardOneFragment();
        twoFragment = new BindCardTwoFragment();
//        threeFragment = new BindCardThreeFragment();
        successFragment = new BindCardSuccessFragment();

        user_bandcard_info_1_2_1();
    }


    @Override
    public void showTwo(Bundle bundle) {
        // 设置银行信息
        twoFragment.setArguments(bundle);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, twoFragment);
        transaction.commit();

        iv2.setImageResource(R.mipmap.two_icon_yes);
        tv2.setTextColor(getResources().getColor(R.color.bg_head));
        divider1.setBackgroundColor(getResources().getColor(R.color.bg_head));
        divider2.setBackgroundColor(getResources().getColor(R.color.bg_head));
    }

//    @Override
//    public void showThree() {
//        transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.content, threeFragment);
//        transaction.commit();
//    }

    @Override
    public void showSuccess(Bundle bundle) {
        successFragment.setArguments(bundle);
        llRoot.setVisibility(View.GONE);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, successFragment);
        transaction.commit();
    }

    @Override
    public void finishAll() {
        onBackPressed();
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private UserBandcardInfo1_2_1Request request;

    private void user_bandcard_info_1_2_1() {
        showUncanclePd();
        if (request != null) {
            request.cancel();
        }
        UserBandcardInfo1_2_1Request.Input input = new UserBandcardInfo1_2_1Request.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.convertJosn();

        request = new UserBandcardInfo1_2_1Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(final Object response) {
                if (((CommonResult) response).status == 1) {
                    if(((CommonResult) response).isBand == 1){
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(BindCardSuccessFragment.BANK_NAME, ((CommonResult) response).bankName);
                        bundle1.putString(BindCardSuccessFragment.CARD_NUM, ((CommonResult) response).bankNumber);
                        showSuccess(bundle1);
                    }else {
                        llRoot.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.content, oneFragment);
                        transaction.commit();
                    }
                } else
                    ToastUtil.showMessage(((CommonResult) response).info);

                dismissPd();
            }
        });
        sendJsonRequest(request);
    }
}
