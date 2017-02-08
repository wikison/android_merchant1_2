package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.MerchantDetailActivity;
import com.zemult.merchant.adapter.slash.MySaleMerchantAdapter;
import com.zemult.merchant.aip.mine.UserSaleMerchantDelRequest;
import com.zemult.merchant.aip.mine.UserSaleMerchantListRequest;
import com.zemult.merchant.aip.mine.UserSaleMerchantList_1_2Request;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.ShareText;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SharePopwindow;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;

/**
 * 我加入的商户
 *
 * @author djy
 * @time 2016/8/4 16:07
 */
public class SaleManageActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
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
    Button lhBtnRightIamge;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;

    private Context mContext;
    private Activity mActivity;

    private UserSaleMerchantListRequest userSaleMerchantListRequest;
    private UserSaleMerchantList_1_2Request userSaleMerchantList_1_2Request;
    private UserSaleMerchantDelRequest userSaleMerchantDelRequest;
    private List<M_Merchant> merchantList = new ArrayList<M_Merchant>();
    //private SaleMerchantAdapter saleMerchantAdapter;
    private MySaleMerchantAdapter mySaleMerchantAdapter;
    int page = 1;
    private SharePopwindow sharePopWindow;
    private M_Merchant merchantItem;
    public static int RECX = 111;
    public static String REFLASH = "REFLASH";

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_sale_manage);
    }

    @Override
    public void init() {
        mContext = this;
        mActivity = this;

        initView();
        initListener();

        getUserMerchantList();

    }

    private void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("我加入的商户");
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        //saleMerchantAdapter = new SaleMerchantAdapter(this, merchantList);
        mySaleMerchantAdapter = new MySaleMerchantAdapter(this, merchantList);

        smoothListView.setAdapter(mySaleMerchantAdapter);
    }

    private void initListener() {
        mySaleMerchantAdapter.setOnItemMerchantClickListener(new MySaleMerchantAdapter.ItemMerchantClickListener() {
            @Override
            public void onItemClick(M_Merchant merchant) {
                Intent intent = new Intent(mActivity, MerchantDetailActivity.class);
                intent.putExtra(MerchantDetailActivity.MERCHANT_ID, merchant.merchantId);
                startActivity(intent);
            }
        });

        mySaleMerchantAdapter.setOnItemServiceClickListener(new MySaleMerchantAdapter.ItemServiceClickListener() {
            @Override
            public void onItemClick(M_Merchant merchant) {
                Intent intent = new Intent(mActivity, TabManageActivity.class);
                intent.putExtra(TabManageActivity.TAG, merchant.merchantId);
                intent.putExtra(TabManageActivity.NAME, merchant.name);
                intent.putExtra(TabManageActivity.TAGS, merchant.tags);
                intent.putExtra(TabManageActivity.COMEFROM, 2);
                startActivityForResult(intent, RECX);
            }
        });

        mySaleMerchantAdapter.setOnItemQRClickListener(new MySaleMerchantAdapter.ItemQRClickListener() {
            @Override
            public void onItemClick(M_Merchant merchant) {
                Intent intent = new Intent(mActivity, MyQrActivity.class);
                intent.putExtra("from", "SaleManage");
                intent.putExtra("merchantId", merchant.merchantId);
                intent.putExtra("merchantName", merchant.name);
                intent.putExtra("merchantHead", merchant.head);
                intent.putExtra("merchantAddress", merchant.address);
                intent.putExtra("userSaleId", SlashHelper.userManager().getUserId());
                startActivity(intent);
            }
        });

        mySaleMerchantAdapter.setOnItemShareClickListener(new MySaleMerchantAdapter.ItemShareClickListener() {
            @Override
            public void onItemClick(final M_Merchant merchant) {
                merchantItem = merchant;
                if (sharePopWindow.isShowing())
                    sharePopWindow.dismiss();
                else
                    sharePopWindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0);

            }
        });

        mySaleMerchantAdapter.setOnItemDeleteClickListener(new MySaleMerchantAdapter.ItemDeleteClickListener() {
            @Override
            public void onItemClick(final M_Merchant merchant) {
                CommonDialog.showDialogListener(mContext, null, "否", "是", "是否删除商家", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                        userSaleMerchantDel(merchant);
                    }
                });

            }
        });

        sharePopWindow = new SharePopwindow(mContext, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage;
                if (null != merchantItem.head && merchantItem.head.indexOf("xiegang.oss") != -1) {
                    shareImage = new UMImage(mContext, merchantItem.head);
                } else {
                    shareImage = new UMImage(mContext, R.mipmap.icon_launcher);
                }
                switch (position) {
                    case SharePopwindow.SINA:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.SINA)
                                .setCallback(umShareListener)
                                .withText(merchantItem.name)
                                .withTargetUrl(SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_COMMISSION, Urls.URL + "wappay/wappay_index.do?merchantId=" + merchantItem.merchantId + "&saleUserId=" + SlashHelper.userManager().getUserId()))
                                .withMedia(shareImage)
                                .withTitle("分享买单")
                                .share();
                        break;

                    case SharePopwindow.WECHAT:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText(merchantItem.name)
                                .withTargetUrl(SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_COMMISSION, Urls.URL + "wappay/wappay_index.do?merchantId=" + merchantItem.merchantId + "&saleUserId=" + SlashHelper.userManager().getUserId()))
                                .withMedia(shareImage)
                                .withTitle("分享买单")
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText(merchantItem.name)
                                .withTargetUrl(SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_COMMISSION, Urls.URL + "wappay/wappay_index.do?merchantId=" + merchantItem.merchantId + "&saleUserId=" + SlashHelper.userManager().getUserId()))
                                .withMedia(shareImage)
                                .withTitle("分享买单")
                                .share();
                        break;

                    case SharePopwindow.QQ:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(umShareListener)
                                .withText(merchantItem.name)
                                .withTargetUrl(SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_COMMISSION, Urls.URL + "wappay/wappay_index.do?merchantId=" + merchantItem.merchantId + "&saleUserId=" + SlashHelper.userManager().getUserId()))
                                .withMedia(shareImage)
                                .withTitle("分享买单")
                                .share();
                        break;
                }
            }
        });
    }


    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
            sharePopWindow.dismiss();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 分享失败啦", Toast.LENGTH_SHORT).show();
            sharePopWindow.dismiss();

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            sharePopWindow.dismiss();

        }
    };

    // 获取挂靠的商家列表
    private void getUserMerchantList() {
        showPd();
        if (userSaleMerchantList_1_2Request != null) {
            userSaleMerchantList_1_2Request.cancel();
        }

        UserSaleMerchantList_1_2Request.Input input = new UserSaleMerchantList_1_2Request.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.center = (String) SPUtils.get(mContext, Constants.SP_CENTER, Constants.CENTER);
        input.convertJosn();

        userSaleMerchantList_1_2Request = new UserSaleMerchantList_1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_MerchantList) response).status == 1) {
                    fillAdapter(((APIM_MerchantList) response).industryList);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
            }
        });
        sendJsonRequest(userSaleMerchantList_1_2Request);
    }


    /**
     * 获取挂靠的商家列表
     */
    private void getUserMerchantList(final boolean isLoadMore) {
        if (userSaleMerchantListRequest != null) {
            userSaleMerchantListRequest.cancel();
        }
        UserSaleMerchantListRequest.Input input = new UserSaleMerchantListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();

        userSaleMerchantListRequest = new UserSaleMerchantListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    fillAdapter(((APIM_MerchantList) response).merchantList);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }


                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();

            }
        });
        sendJsonRequest(userSaleMerchantListRequest);
    }


    /**
     * 设置数据
     */
    private void fillAdapter(final List<M_Merchant> list) {

        if (list == null || list.size() == 0) {
            rlNoData.setVisibility(View.VISIBLE);
            smoothListView.setLoadMoreEnable(false);
        } else {
            rlNoData.setVisibility(View.GONE);
            smoothListView.setLoadMoreEnable(false);
            mySaleMerchantAdapter.setData(list);
        }
    }


    public void userSaleMerchantDel(final M_Merchant merchant) {
        showPd();
        if (userSaleMerchantDelRequest != null) {
            userSaleMerchantDelRequest.cancel();
        }
        UserSaleMerchantDelRequest.Input input = new UserSaleMerchantDelRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.merchantId = merchant.merchantId;

        input.convertJosn();
        userSaleMerchantDelRequest = new UserSaleMerchantDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    EventBus.getDefault().post(REFLASH);
                    onRefresh();
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userSaleMerchantDelRequest);
    }

    @Override
    public void onRefresh() {
        getUserMerchantList();
    }

    @Override
    public void onLoadMore() {
        getUserMerchantList();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECX && resultCode == RESULT_OK)
            onRefresh();

    }

}
