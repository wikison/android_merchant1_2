package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slash.TaMerchantChooseAdapter;
import com.zemult.merchant.aip.slash.MerchantOtherMerchantListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.BounceScrollView;
import com.zemult.merchant.view.FixedListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2017/1/20.
 */

public class ChoosePayMerchantActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.flv_can)
    FixedListView flvCan;
    @Bind(R.id.flv_cannot)
    FixedListView flvCannot;
    @Bind(R.id.bsv_container)
    BounceScrollView bsvContainer;

    private List<M_Merchant> merchantCanList = new ArrayList<M_Merchant>();
    private List<M_Merchant> merchantCannotList = new ArrayList<M_Merchant>();

    private MerchantOtherMerchantListRequest merchantOtherMerchantListRequest; // 挂靠的商家
    TaMerchantChooseAdapter adapterCan, adapterCannot;
    private Context mContext;
    private Activity mActivity;
    private int userId;// 用户id(要查看的用户)

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_choose_pay_merchant);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        getOtherMerchantList();
    }

    private void initListener() {

    }

    private void initView() {
        lhTvTitle.setText("选择买单商户");
    }

    private void initData() {
        userId = getIntent().getIntExtra(UserDetailActivity.USER_ID, -1);
        mContext = this;
        mActivity = this;

        adapterCan = new TaMerchantChooseAdapter(mContext, merchantCanList);
        adapterCannot = new TaMerchantChooseAdapter(mContext, merchantCannotList);

        flvCan.setAdapter(adapterCan);
        flvCannot.setAdapter(adapterCannot);
    }

    /**
     * 查看TA挂靠的商家
     */
    private void getOtherMerchantList() {
        if (merchantOtherMerchantListRequest != null) {
            merchantOtherMerchantListRequest.cancel();
        }
        MerchantOtherMerchantListRequest.Input input = new MerchantOtherMerchantListRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.center = (String) SPUtils.get(mContext, Constants.SP_CENTER, "119.971736,31.829737");
        input.userId = userId;
        input.page = 1;
        input.rows = Constants.ROWS;
        input.convertJosn();
        merchantOtherMerchantListRequest = new MerchantOtherMerchantListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    initList(((APIM_MerchantList) response).merchantList);

                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchantOtherMerchantListRequest);
    }

    private void initList(List<M_Merchant> list) {
        if (list == null || list.size() == 0)
            return;

        for (M_Merchant m : list) {
            if (m.reviewstatus == 2) {
                merchantCanList.add(m);
            } else {
                merchantCannotList.add(m);
            }
        }

        fillAdapter(false);

    }

    // 填充数据
    private void fillAdapter(boolean isLoadMore) {
        adapterCan.setData(merchantCanList, isLoadMore);
        adapterCannot.setData(merchantCannotList, isLoadMore);

        bsvContainer.post(new Runnable() {
            @Override
            public void run() {

                bsvContainer.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

}
