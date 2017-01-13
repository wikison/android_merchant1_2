package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.AlbumActivity;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.adapter.slashfrgment.MerchantDetailAdpater;
import com.zemult.merchant.aip.slash.MerchantInfoRequest;
import com.zemult.merchant.aip.slash.MerchantSaleuserListFanRequest;
import com.zemult.merchant.aip.slash.MerchantSaleuserListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.model.apimodel.APIM_SearchUsersList;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ColorUtil;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.HeaderMerchantDetailView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class MerchantDetailActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {

    /**
     * 调用详情页面必传参数 MERCHANT_ID
     */
    public static final String MERCHANT_ID = "merchantId";
    private static final int REQ_APPLY = 0x110;

    @Bind(R.id.lv)
    SmoothListView lv;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_more)
    ImageView ivMore;
    @Bind(R.id.rl_top)
    RelativeLayout rlTop;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.rl_first)
    RelativeLayout rlFirst;

    private int merchantId;
    private Context mContext;
    private Activity mActivity;

    private MerchantInfoRequest request;
    private M_Merchant merchantInfo;
    private HeaderMerchantDetailView headerMerchantDetailView;
    private MerchantDetailAdpater mAdapter;
    private List<M_Userinfo> listFan;
    private int page = 1;
    private float mTopViewHeight, fraction, headerTopMargin;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_merchant_detail);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        getNetworkData();
    }

    private void initData() {
        merchantId = getIntent().getIntExtra(MERCHANT_ID, -1);
        mContext = this;
        mActivity = this;
    }

    private void initView() {
        if(SPUtils.contains(mContext, "merchant_first_run"))
            rlFirst.setVisibility(View.GONE);
        else {
            rlFirst.setVisibility(View.VISIBLE);
            SPUtils.put(mContext,"merchant_first_run",false);
        }
        // 设置其他头部
        headerMerchantDetailView = new HeaderMerchantDetailView(mActivity);
        headerMerchantDetailView.fillView(new M_Merchant(), lv);

        // 设置ListView数据
        mAdapter = new MerchantDetailAdpater(mContext, new ArrayList<M_Userinfo>());
        lv.setAdapter(mAdapter);
    }

    private void initListener() {
        lv.setRefreshEnable(true);
        lv.setLoadMoreEnable(false);
        lv.setSmoothListViewListener(this);
        lv.setOnScrollListener(new SmoothListView.OnSmoothScrollListener() {
            @Override
            public void onSmoothScrolling(View view) {

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (lv.getChildAt(1 - firstVisibleItem) != null) {
                    headerTopMargin = (float) lv.getChildAt(1 - firstVisibleItem).getTop();

                    if (mTopViewHeight == 0) {
                        mTopViewHeight = (float) rlTop.getLayoutParams().height;
                    }
                    if (headerTopMargin >= 0)
                        fraction = 0f;
                    else if (-headerTopMargin >= mTopViewHeight) {
                        fraction = 1f;
                    } else
                        fraction = 1 - (headerTopMargin + mTopViewHeight) / mTopViewHeight;

                    lhTvTitle.setTextColor(ColorUtil.getNewColorByStartEndColor(mContext, fraction, R.color.transparent, R.color.white));
                    rlTop.setBackgroundColor(ColorUtil.getNewColorByStartEndColor(mContext, fraction, R.color.transparent, R.color.font_black_28));
                }
            }
        });

        headerMerchantDetailView.setOnHeaderClickListener(new HeaderMerchantDetailView.OnHeaderClickListener() {
            @Override
            public void onCoverClick() {
                Intent intent = new Intent(mContext, AlbumActivity.class);
                intent.putExtra(AlbumActivity.INTENT_MERCHANTID, merchantId);
                intent.putExtra(AlbumActivity.INTENT_JUSTLOOK, 1);
                startActivity(intent);
            }
        });
        mAdapter.setOnMerchantDetailClick(new MerchantDetailAdpater.OnMerchantDetailClick() {
            @Override
            public void onUserClick(int position) {
//                if (mAdapter.getItem(position).getUserId() == SlashHelper.userManager().getUserId())
//                    return;
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, mAdapter.getItem(position).getUserId());
                intent.putExtra(UserDetailActivity.USER_NAME, mAdapter.getItem(position).getUserName());
                intent.putExtra(UserDetailActivity.USER_HEAD, mAdapter.getItem(position).getUserHead());
                intent.putExtra(UserDetailActivity.USER_SEX, mAdapter.getItem(position).getSex());
                startActivity(intent);
            }

            @Override
            public void onHeadClick(int position) {
                List<String> list = new ArrayList<String>();
                list.add(mAdapter.getItem(position).getUserHead());
                AppUtils.toImageDetial(mActivity, 0, list, false);
            }
        });
    }

    private void getNetworkData() {
        showPd();
        // 商家详情
        merchant_info();
        onRefresh();
    }

    /**
     * 商家详情
     */
    private void merchant_info() {
        if (request != null) {
            request.cancel();
        }
        MerchantInfoRequest.Input input = new MerchantInfoRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.convertJosn();
        request = new MerchantInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetinfo) response).status == 1) {
                    merchantInfo = ((APIM_MerchantGetinfo) response).merchant;
                    merchantInfo.merchantId = merchantId;
                    headerMerchantDetailView.dealWithTheView(merchantInfo);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(request);
    }

    @OnClick({R.id.iv_back, R.id.iv_more, R.id.rl_first})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_more:
                if (noLogin(mContext))
                    return;
                if (merchantInfo != null && merchantInfo.isCommission == 1) {
                    ToastUtil.showMessage("您已经申请过了");
                    return;
                }
                Intent it = new Intent(mActivity, TabManageActivity.class);
                it.putExtra(TabManageActivity.TAG, merchantId);
                startActivityForResult(it, REQ_APPLY);
                break;
            case R.id.rl_first:
                rlFirst.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onRefresh() {
        if (SlashHelper.userManager().getUserId() != 0)
            merchant_saleuserList_fan();
        else
            merchant_saleuserList_all(false);
    }

    @Override
    public void onLoadMore() {
        merchant_saleuserList_all(true);
    }

    private MerchantSaleuserListRequest allRequest;
    private MerchantSaleuserListFanRequest fanRequest;

    /**
     * 商家下的营销经理列表(我的关注-熟人)
     */
    private void merchant_saleuserList_fan() {
        if (fanRequest != null) {
            fanRequest.cancel();
        }
        MerchantSaleuserListFanRequest.Input input = new MerchantSaleuserListFanRequest.Input();

        input.operateUserId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.page = 1;
        input.rows = 5;
        input.convertJosn();
        fanRequest = new MerchantSaleuserListFanRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                merchant_saleuserList_all(false);
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_SearchUsersList) response).status == 1) {
                    listFan = ((APIM_SearchUsersList) response).userList;
                } else {
                    ToastUtils.show(mContext, ((APIM_SearchUsersList) response).info);
                }
                dismissPd();
                merchant_saleuserList_all(false);
            }
        });
        sendJsonRequest(fanRequest);
    }

    /**
     * 商家下的营销经理列表(全部)
     */
    private void merchant_saleuserList_all(final boolean isLoadMore) {
        if (allRequest != null) {
            allRequest.cancel();
        }
        MerchantSaleuserListRequest.Input input = new MerchantSaleuserListRequest.Input();

        input.operateUserId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        allRequest = new MerchantSaleuserListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lv.stopRefresh();
                lv.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_SearchUsersList) response).status == 1) {
                    setUserListAll(((APIM_SearchUsersList) response).userList,
                            ((APIM_SearchUsersList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_SearchUsersList) response).info);
                }
                lv.stopRefresh();
                lv.stopLoadMore();
                dismissPd();
            }
        });
        sendJsonRequest(allRequest);
    }

    /**
     * 全部
     */
    private void setUserListAll(List<M_Userinfo> listAll, int maxpage, boolean isLoadMore) {
        if ((listAll == null || listAll.isEmpty()) && (listFan == null || listFan.isEmpty())) {
            int height = DensityUtil.getWindowHeight(mActivity) - DensityUtil.dip2px(mContext, 48);
            mAdapter.setData(ModelUtil.getNoDataUserEntity(height), false);
            lv.setLoadMoreEnable(false);
        } else {
            lv.setLoadMoreEnable(page < maxpage);
            if (isLoadMore)
                mAdapter.setData(listAll, isLoadMore);
            else
                mAdapter.setData(listFan, listAll);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_APPLY)
            merchant_info();
        onRefresh();
    }
}
