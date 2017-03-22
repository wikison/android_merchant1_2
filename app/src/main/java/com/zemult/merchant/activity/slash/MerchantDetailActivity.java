package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.adapter.slashfrgment.MerchantDetailAdpater;
import com.zemult.merchant.aip.mine.MerchantPicListRequest;
import com.zemult.merchant.aip.mine.MerchantPicNoteListRequest;
import com.zemult.merchant.aip.mine.UserCheckSaleUser1_2_2Request;
import com.zemult.merchant.aip.slash.MerchantInfoRequest;
import com.zemult.merchant.aip.slash.MerchantSaleuserListFanRequest;
import com.zemult.merchant.aip.slash.MerchantSaleuserListRequest;
import com.zemult.merchant.aip.slash.UserFavoriteMerchantAddRequest;
import com.zemult.merchant.aip.slash.UserFavoriteMerchantDelRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.FilterEntity;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Pic;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.model.apimodel.APIM_PicList;
import com.zemult.merchant.model.apimodel.APIM_SearchUsersList;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ColorUtil;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.HeaderAdViewView;
import com.zemult.merchant.view.HeaderMerchantDetailView;
import com.zemult.merchant.view.SharePopwindow;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class MerchantDetailActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {

    /**
     * 调用详情页面必传参数 MERCHANT_ID
     */
    public static final String MERCHANT_ID = "merchantId";

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
    @Bind(R.id.ll_root)
    LinearLayout llRoot;

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
    private String name = "", tags = "";
    private SharePopwindow sharePopWindow;

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
        registerReceiver(new String[]{ Constants.BROCAST_BE_SERVER_MANAGER_SUCCESS});
    }

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if(Constants.BROCAST_BE_SERVER_MANAGER_SUCCESS.equals(intent.getAction())){
            onRefresh();
        }
    }

    private void initView() {
        if (SPUtils.contains(mContext, "merchant_first_run"))
            rlFirst.setVisibility(View.GONE);
        else {
            rlFirst.setVisibility(View.VISIBLE);
            SPUtils.put(mContext, "merchant_first_run", false);
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
                merchantInfo.setTags(mAdapter.getItem(position).getTags());
                intent.putExtra(UserDetailActivity.MERCHANT_ID, merchantId);
                intent.putExtra(UserDetailActivity.MERCHANT_INFO, mAdapter.getItem(position));
                intent.putExtra("merchantInfo", merchantInfo);
                startActivity(intent);
            }

            @Override
            public void onHeadClick(int position) {
                List<String> list = new ArrayList<String>();
                list.add(mAdapter.getItem(position).getUserHead());
                AppUtils.toImageDetial(mActivity, 0, list, null, false, false, true, 0, 0);
            }
        });

        headerMerchantDetailView.setImageOnClick(new HeaderMerchantDetailView.ImageOnClick() {
            @Override
            public void imageOnclick(int picId) {
                merchant_pic_noteList(picId);
            }
        });

        sharePopWindow = new SharePopwindow(mContext, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage = new UMImage(mContext, R.mipmap.icon_share);

                switch (position) {
                    case SharePopwindow.SINA:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.SINA)
                                .setCallback(umShareListener)
                                .withText("您的好友【"+ SlashHelper.userManager().getUserinfo().getName()+"】给您推荐了【"+ merchantInfo.name+"】的服务管家和服务,快去看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("您的好友【"+ SlashHelper.userManager().getUserinfo().getName()+"】向您推荐商家服务")
                                .share();
                        break;

                    case SharePopwindow.WECHAT:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText("您的好友【"+ SlashHelper.userManager().getUserinfo().getName()+"】给您推荐了【"+ merchantInfo.name+"】的服务管家和服务,快去看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("您的好友【"+ SlashHelper.userManager().getUserinfo().getName()+"】向您推荐商家服务")
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText("您的好友【"+ SlashHelper.userManager().getUserinfo().getName()+"】给您推荐了【"+ merchantInfo.name+"】的服务管家和服务,快去看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("您的好友【"+ SlashHelper.userManager().getUserinfo().getName()+"】向您推荐商家服务")
                                .share();
                        break;

                    case SharePopwindow.QQ:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(umShareListener)
                                .withText("您的好友【"+ SlashHelper.userManager().getUserinfo().getName()+"】给您推荐了【"+ merchantInfo.name+"】的服务管家和服务,快去看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("您的好友【"+ SlashHelper.userManager().getUserinfo().getName()+"】向您推荐商家服务")
                                .share();
                        break;
                }
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
                    name = merchantInfo.name;
                    tags = merchantInfo.tags;
                    headerMerchantDetailView.dealWithTheView(merchantInfo);
                    merchant_picList();
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(request);
    }
    /**
     * 获取商家详情的图片列表(非证件照)
     */
    private MerchantPicListRequest merchantPicListRequest;
    private void merchant_picList() {
        showPd();
        if (merchantPicListRequest != null) {
            merchantPicListRequest.cancel();
        }
        MerchantPicListRequest.Input input = new MerchantPicListRequest.Input();
        input.merchantId = merchantId;
        input.page = 1;
        input.rows = 5;
        input.convertJosn();

        merchantPicListRequest = new MerchantPicListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_PicList) response).status == 1) {
                    headerMerchantDetailView.dealWithTheView(merchantInfo, ((APIM_PicList) response).picList);
                } else {
                    ToastUtil.showMessage(((APIM_PicList) response).info);
                    headerMerchantDetailView.dealWithTheView(merchantInfo, null);
                }
               dismissPd();
            }
        });
        sendJsonRequest(merchantPicListRequest);
    }
    /**
     * 获取商家详情的图片对应的描述列表
     */
    private MerchantPicNoteListRequest picNoteListRequest;
    private void merchant_pic_noteList(int picId) {
        showPd();
        if (picNoteListRequest != null) {
            picNoteListRequest.cancel();
        }
        MerchantPicNoteListRequest.Input input = new MerchantPicNoteListRequest.Input();
        input.picId = picId;
        input.convertJosn();

        picNoteListRequest = new MerchantPicNoteListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_PicList) response).status == 1) {
                    List<M_Pic> noteList = ((APIM_PicList) response).noteList;
                    List<String> pics = new ArrayList<>();
                    List<String> notes = new ArrayList<>();
                    for(M_Pic pic : noteList){
                        pics.add(StringUtils.isBlank(pic.picPath)? "":pic.picPath);
                        notes.add(StringUtils.isBlank(pic.note)? "":pic.note);
                    }
                    AppUtils.toImageDetial(mActivity, 0, pics, notes);
                } else {
                    ToastUtil.showMessage(((APIM_PicList) response).info);
                }
               dismissPd();
            }
        });
        sendJsonRequest(picNoteListRequest);
    }

    /**
     * 用户添加收藏商家
     */
    private UserFavoriteMerchantAddRequest addRequest;
    private void user_favorite_merchant_add() {
        showPd();
        if (addRequest != null) {
            addRequest.cancel();
        }
        UserFavoriteMerchantAddRequest.Input input = new UserFavoriteMerchantAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.convertJosn();
        addRequest = new UserFavoriteMerchantAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(mContext, ("收藏成功"));
                    merchantInfo.isFavorite = 1;
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(addRequest);
    }
    /**
     * 用户的删除收藏商家
     */
    private UserFavoriteMerchantDelRequest delRequest;
    private void user_favorite_merchant_del() {
        showPd();
        if (delRequest != null) {
            delRequest.cancel();
        }
        UserFavoriteMerchantDelRequest.Input input = new UserFavoriteMerchantDelRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.convertJosn();
        delRequest = new UserFavoriteMerchantDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(mContext, ("取消收藏成功"));
                    merchantInfo.isFavorite = 0;
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(delRequest);
    }


    @Override
    public void onRefresh() {
        if (SlashHelper.userManager().getUserId() != 0)
            merchant_saleuserList_fan();
        else
            merchant_saleuserList_all(false);
        merchant_info();
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
        input.rows = 100;
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
            mAdapter.setHalfScreen(true);
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

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            sharePopWindow.dismiss();
            ToastUtil.showMessage("分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            sharePopWindow.dismiss();
            ToastUtil.showMessage("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            sharePopWindow.dismiss();
            ToastUtil.showMessage("分享取消");
        }
    };


    /**
     * 判断用户是否可以申请商家的服务管家
     */
    private UserCheckSaleUser1_2_2Request checkSaleUser1_2_2Request;
    private void user_check_saleuser_1_2_2() {
        showPd();
        if (checkSaleUser1_2_2Request != null) {
            checkSaleUser1_2_2Request.cancel();
        }
        UserCheckSaleUser1_2_2Request.Input input = new UserCheckSaleUser1_2_2Request.Input();

        input.userId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.convertJosn();
        checkSaleUser1_2_2Request = new UserCheckSaleUser1_2_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    if (noHead(mContext)) {
                        Intent it = new Intent(mActivity, BeManagerFirstActivity.class);
                        it.putExtra(TabManageActivity.TAG, merchantId);
                        it.putExtra(TabManageActivity.NAME, name);
                        startActivity(it);
                    } else {
                        Intent it = new Intent(mActivity, TabManageActivity.class);
                        it.putExtra(TabManageActivity.TAG, merchantId);
                        it.putExtra(TabManageActivity.NAME, name);
                        it.putExtra(TabManageActivity.COMEFROM, 3);
                        startActivity(it);
                    }
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(checkSaleUser1_2_2Request);
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

                List<FilterEntity> list = new ArrayList<>();
                list.add(new FilterEntity("推荐商户给好友", 0, R.mipmap.fenxiang_icon));

                // 收藏商户 0否1是
                if (merchantInfo != null && merchantInfo.isFavorite == 1)
                    list.add(new FilterEntity("取消收藏", 1, R.mipmap.shoucang_sel));
                else
                    list.add(new FilterEntity("收藏商户", 1, R.mipmap.shoucang_nor));

                if (merchantInfo != null && merchantInfo.isCommission == 1)
                    list.add(new FilterEntity("修改服务标签", 2, R.mipmap.xiugai_icon));
                else
                    list.add(new FilterEntity("成为服务管家", 2, R.mipmap.shenqing_icon));


                CommonDialog.showPopupWindow(mContext, view, list, new CommonDialog.PopClickListener() {
                    @Override
                    public void onClick(int pos) {
                        switch (pos){
                            case 0:
                                if (sharePopWindow.isShowing())
                                    sharePopWindow.dismiss();
                                else
                                    sharePopWindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0);
                                break;

                            case 1:
                                if (merchantInfo != null && merchantInfo.isFavorite == 1)
                                    user_favorite_merchant_del(); // 取消收藏
                                else
                                    user_favorite_merchant_add(); // 收藏

                                break;

                            case 2:
                                if (merchantInfo != null && merchantInfo.isCommission == 1) { // 是服务管家
                                    Intent intent = new Intent(mActivity, TabManageActivity.class);
                                    intent.putExtra(TabManageActivity.TAG, merchantId);
                                    intent.putExtra(TabManageActivity.NAME, name);
                                    intent.putExtra(TabManageActivity.TAGS, tags);
                                    intent.putExtra(TabManageActivity.COMEFROM, 2);
                                    startActivity(intent);
                                } else { // 不是服务管家
                                    user_check_saleuser_1_2_2();
                                }
                                break;
                        }
                    }
                });

                break;
            case R.id.rl_first:
                rlFirst.setVisibility(View.GONE);
                break;
        }
    }
}
