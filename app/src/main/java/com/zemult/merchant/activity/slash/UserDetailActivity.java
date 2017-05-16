package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.LinkagePager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.android.volley.VolleyError;
import com.flyco.roundview.RoundLinearLayout;
import com.flyco.roundview.RoundTextView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ReportActivity;
import com.zemult.merchant.activity.mine.RemarkNameActivity;
import com.zemult.merchant.adapter.slash.PagerUserMerchantAdapter;
import com.zemult.merchant.aip.mine.UserAttractAddRequest;
import com.zemult.merchant.aip.mine.UserAttractDelRequest;
import com.zemult.merchant.aip.slash.Merchant2SaleUserMerchantListRequest;
import com.zemult.merchant.aip.slash.UserInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.CustomerCreateBespeakActivity;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.FilterEntity;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SharePopwindow;
import com.zemult.merchant.view.VerticalScrollView;
import com.zemult.merchant.view.common.CommonDialog;
import com.zemult.merchant.view.common.MMAlert;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import me.crosswall.lib.coverflow.core.LinkageCoverTransformer;
import me.crosswall.lib.coverflow.core.LinkagePagerContainer;
import me.crosswall.lib.coverflow.core.PageItemClickListener;
import zema.volley.network.ResponseListener;

/**
 * 012111用户详情
 */
public class UserDetailActivity extends BaseActivity {
    /**
     * 调用用户详情页面必传参数
     */
    public static final String USER_ID = "userId";
    /**
     * 非必传
     */
    public static final String MERCHANT_INFO = "merchantInfo";
    public static final String MERCHANT_ID = "merchantId";
    public static final String USER_NAME = "userName"; // 用户名
    public static final String USER_HEAD = "userHead"; // 用户头像
    public static final String USER_SEX = "userSex"; // 用户性别

    private static final int REQ_ALBUM = 0x110;
    private static final int REQ_REMARK_NAME = 0x120;

    private static final int STATE_NORMAL = 1;// 默认的状态
    private static final int STATE_RECORDING = 2;// 正在录音
    private static final int STATE_WANT_TO_CANCEL = 3;// 希望取消

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.iv_right2)
    ImageView ivRight2;
    @Bind(R.id.ll_right2)
    LinearLayout llRight2;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_level)
    TextView tvLevel;
    @Bind(R.id.tv_rname)
    TextView tvRname;
    @Bind(R.id.btn_focus)
    RoundTextView btnFocus;
    @Bind(R.id.tv_hint)
    TextView tvHint;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.pager_container)
    LinkagePagerContainer pagerContainer;
    @Bind(R.id.iv_arrow)
    ImageView ivArrow;
    @Bind(R.id.rl_container)
    RelativeLayout rlContainer;
    @Bind(R.id.bind_pager)
    LinkagePager bindPager;
    @Bind(R.id.ll_main)
    LinearLayout llMain;
    @Bind(R.id.vertical_scrollview)
    VerticalScrollView verticalScrollview;
    @Bind(R.id.iv_normal_head)
    ImageView ivNormalHead;
    @Bind(R.id.tv_normal_name)
    TextView tvNormalName;
    @Bind(R.id.btn_contact)
    Button btnContact;
    @Bind(R.id.ll_normal)
    LinearLayout llNormal;
    @Bind(R.id.rll_im)
    RoundLinearLayout rllIm;
    @Bind(R.id.rll_call)
    RoundLinearLayout rllCall;
    @Bind(R.id.btn_service)
    RoundLinearLayout btnService;
    @Bind(R.id.ll_bottom_menu)
    LinearLayout llBottomMenu;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.tv_call)
    TextView tvCall;
    @Bind(R.id.iv_call)
    ImageView ivCall;
    @Bind(R.id.tv_focus)
    TextView tvFocus;

    public static final String TAG = UserDetailActivity.class.getSimpleName();


    private Context mContext;
    private Activity mActivity;
    private int userId;// 用户id(要查看的用户)
    private boolean isSelf = false; //用户是否是自己
    UserInfoRequest userInfoRequest; // 查看用户(其它人)详情
    Merchant2SaleUserMerchantListRequest merchant2SaleUserMerchantListRequest; // 挂靠的商家
    //    User2RemindIMAddRequest user2RemindIMAddRequest;  //用户发送语音预约消息
    UserAttractAddRequest attractAddRequest; // 添加关注
    UserAttractDelRequest attractDelRequest; // 取消关注
    private M_Userinfo userInfo;
    private String userName, userHead;
    private M_Merchant merchant, selectMerchant;
    private int merchantId;
    PagerUserMerchantAdapter pagerUserMerchantHeadAdapter;
    PagerUserMerchantAdapter pagerUserMerchantDetailAdapter;

    List<M_Merchant> listMerchant = new ArrayList<M_Merchant>();
    List<M_Merchant> listMerchantTemp = new ArrayList<M_Merchant>();
    int merchantNum = 0;
    boolean isFromMerchant;
    LinkagePager pager;
    private SharePopwindow sharePopWindow;
    boolean isNormal = false;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_user_detail);
    }

    private YWIMKit getIMkit() {
        return LoginSampleHelper.getInstance().getIMKit();
    }

    @Override
    public void init() {
        initData();
        getNetworkData();
        initView();
        initListener();
    }

    private void initData() {
        userId = getIntent().getIntExtra(USER_ID, -1);
        merchant = (M_Merchant) getIntent().getSerializableExtra(MERCHANT_INFO);
        merchantId = getIntent().getIntExtra(MERCHANT_ID, -1);
        if (merchant != null) {
            isFromMerchant = true;
        } else {
            isFromMerchant = false;
        }
        userName = getIntent().getStringExtra(USER_NAME);
        userHead = getIntent().getStringExtra(USER_HEAD);

        mContext = this;
        mActivity = this;
    }

    private void initView() {
        imageManager.loadCircleHead(userHead, ivHead, "@120w_120h");
        // 用户名
        if (!TextUtils.isEmpty(userName))
            tvName.setText(userName);

        if (userId == SlashHelper.userManager().getUserId()) {
            btnFocus.setVisibility(View.GONE);
            isSelf = true;
            llBottomMenu.setVisibility(View.GONE);
            btnService.setVisibility(View.GONE);
        } else {
            llRight.setVisibility(View.VISIBLE);
            ivRight.setImageResource(R.mipmap.gengduo_icon);
            isSelf = false;
            llBottomMenu.setVisibility(View.VISIBLE);
        }

    }

    private void initListener() {
        ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = new ArrayList<String>();
                list.add(userInfo.getHead());
                AppUtils.toImageDetial(mActivity, 0, list, null, false, false, true, 0, 0);
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
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】给您推荐了一个服务管家【" + userInfo.name + "】快去看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】向您推荐服务管家【" + userInfo.name + "】")
                                .share();
                        break;

                    case SharePopwindow.WECHAT:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】给您推荐了一个服务管家【" + userInfo.name + "】快去看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】向您推荐服务管家【" + userInfo.name + "】")
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】给您推荐了一个服务管家【" + userInfo.name + "】快去看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】向您推荐服务管家【" + userInfo.name + "】")
                                .share();
                        break;

                    case SharePopwindow.QQ:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(umShareListener)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】给您推荐了一个服务管家【" + userInfo.name + "】快去看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】向您推荐服务管家【" + userInfo.name + "】")
                                .share();
                        break;
                }
            }
        });
    }

    private void getNetworkData() {
        showPd();
        getUserInfo();
        getOtherMerchantList();
    }


    // 关注方面的操作
    private void focus_operate() {
        if (noLogin(mContext)) return;

        if (getResources().getString(R.string.add_focus_yogouser).equals(btnFocus.getText().toString()))
            addFocus();
        else
            cancleFocus();
    }


    /**
     * 查看用户(其它人)详情
     */
    private void getUserInfo() {
        if (userInfoRequest != null) {
            userInfoRequest.cancel();
        }
        UserInfoRequest.Input input = new UserInfoRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.userId = userId;
        input.convertJosn();
        userInfoRequest = new UserInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserLogin) response).status == 1) {
                    setUserInfo(((APIM_UserLogin) response).UserInfo);
                } else {
                    ToastUtils.show(mContext, ((APIM_UserLogin) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userInfoRequest);
    }

    /**
     * 查看TA挂靠的商家
     */
    private void getOtherMerchantList() {
        if (merchant2SaleUserMerchantListRequest != null) {
            merchant2SaleUserMerchantListRequest.cancel();
        }
        Merchant2SaleUserMerchantListRequest.Input input = new Merchant2SaleUserMerchantListRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.center = (String) SPUtils.get(mContext, Constants.SP_CENTER, "119.971736,31.829737");
        input.saleUserId = userId;
        input.convertJosn();
        merchant2SaleUserMerchantListRequest = new Merchant2SaleUserMerchantListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    listMerchantTemp = ((APIM_MerchantList) response).merchantList;
                    listMerchant = sortList(listMerchantTemp);
                    fillAdapter(listMerchant);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchant2SaleUserMerchantListRequest);
    }

    /**
     * 设置用户信息
     *
     * @param userInfo
     */
    private void setUserInfo(M_Userinfo userInfo) {
        Drawable drawable;
        // 头像
        if (!TextUtils.isEmpty(userInfo.getHead())) {
            imageManager.loadCircleHead(userInfo.getHead(), ivHead, "@120w_120h");
        }
        // 用户名
        if (!TextUtils.isEmpty(userInfo.getName()))
            tvName.setText(userInfo.getName());
        tvLevel.setText(userInfo.getExperienceText() + "管家");
        if (userInfo.getExperienceImg() > 0) {
            drawable = getResources().getDrawable(userInfo.getExperienceImg());
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvLevel.setCompoundDrawables(drawable, null, null, null);
        }
        tvFocus.setText(userInfo.fansNum + "已关注");

        merchantNum = userInfo.saleUserNum;

        // 是否已经关注(0:未关注1:已关注)
        tvRname.setVisibility(View.GONE);
        if (userInfo.getIsFan() == 0) {
            btnFocus.setText(R.string.add_focus_yogouser);
            drawable = getResources().getDrawable(R.mipmap.weiguanzhu);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btnFocus.setCompoundDrawables(drawable, null, null, null);
        } else {
            btnFocus.setText(R.string.has_focus);
            drawable = getResources().getDrawable(R.mipmap.guanzhu);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btnFocus.setCompoundDrawables(drawable, null, null, null);

            if (!TextUtils.isEmpty(userInfo.remarkName)) {
                tvRname.setVisibility(View.VISIBLE);
                tvRname.setText("备注名：" + userInfo.remarkName);
            }
        }

        this.userInfo = userInfo;
        this.userInfo.setUserId(userId);
        this.userInfo.setUserName(userName);
        if (userInfo.getIsSaleUser() == 1) {
            lhTvTitle.setText("管家详情");
            isNormal = false;
        } else {
            lhTvTitle.setText("个人详情");
            isNormal = true;
            llNormal.setVisibility(View.VISIBLE);
            llBottom.setVisibility(View.GONE);
            verticalScrollview.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(userInfo.getHead())) {
                imageManager.loadCircleHead(userInfo.getHead(), ivNormalHead, "@120w_120h");
            }
            if (!TextUtils.isEmpty(userInfo.getName()))
                tvNormalName.setText(userInfo.getName());
            ivRight.setImageResource(R.mipmap.jubao_icon);
        }

        String[] phoneNoArray = userInfo.getPhoneNum().split(";");
        if (phoneNoArray[0] != null && phoneNoArray[0].length() == 11) {
            rllCall.setEnabled(true);

        } else {
            rllCall.setEnabled(false);
            rllCall.getDelegate().setStrokeColor(getResources().getColor(R.color.font_black_999));
            tvCall.setTextColor(getResources().getColor(R.color.font_black_999));
            ivCall.setImageResource(R.mipmap.dadianhua_hui);
        }


    }

    /**
     * 拨打电话
     */
    private void call() {
        final String[] phoneNoArray = userInfo.getPhoneNum().split(";");
        MMAlert.showAlert(this, null, phoneNoArray, null,
                new MMAlert.OnAlertSelectId() {

                    @Override
                    public void onClick(int whichButton) {
                        if (!(whichButton == phoneNoArray.length)) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            Uri data = Uri.parse("tel:" + phoneNoArray[whichButton]);
                            intent.setData(data);
                            startActivity(intent);
                        }
                    }
                });
    }


    private List<M_Merchant> sortList(List<M_Merchant> tList) {
        List<M_Merchant> result = new ArrayList<>();
        for (M_Merchant m : tList) {
            if (m.merchantId == merchantId) {
                result.add(m);
                break;
            }
        }
        for (M_Merchant m : tList) {
            if (m.merchantId != merchantId) {
                result.add(m);
            }
        }
        return result;
    }

    // 填充数据
    private void fillAdapter(List<M_Merchant> list) {

        if (list == null || list.size() == 0) {
            btnService.setVisibility(View.GONE);
            tvHint.setVisibility(View.VISIBLE);
            ivArrow.setVisibility(View.GONE);
        } else {
            pager = pagerContainer.getViewPager();
            pagerUserMerchantHeadAdapter = new PagerUserMerchantAdapter(mContext, listMerchant, 0, isSelf);
            pagerUserMerchantDetailAdapter = new PagerUserMerchantAdapter(mContext, listMerchant, 1, isSelf);
            pager.setAdapter(pagerUserMerchantHeadAdapter);

            pager.setOffscreenPageLimit(pagerUserMerchantHeadAdapter.getCount());
            bindPager.setAdapter(pagerUserMerchantDetailAdapter);
            bindPager.setOffscreenPageLimit(pagerUserMerchantDetailAdapter.getCount());
            bindPager.setLinkagePager(pager);
            pager.setLinkagePager(bindPager);

            pager.setClipChildren(true);
            pager.setPageTransformer(false, new LinkageCoverTransformer(0.3f, 0f, 0f, 0f));
            selectMerchant = listMerchant.get(0);
            changeItem(0);
            pagerContainer.setPageItemClickListener(new PageItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position >= 0 && position < listMerchant.size()) {
                        changeItem(position);
                    }

                }
            });
            pager.setOnPageChangeListener(new LinkagePager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position >= 0 && position < listMerchant.size()) {
                        changeItem(position);

                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            pagerUserMerchantHeadAdapter.setOnViewImageClickListener(new PagerUserMerchantAdapter.ViewImageClickListener() {
                @Override
                public void onMerchantDetail(M_Merchant entity) {
                    if (entity.merchantId == selectMerchant.merchantId) {
                        Intent intent = new Intent(UserDetailActivity.this, MerchantDetailActivity.class);
                        intent.putExtra("userSaleId", userId);
                        intent.putExtra(MerchantDetailActivity.MERCHANT_ID, entity.merchantId);
                        startActivity(intent);
                    }

                }
            });

            pagerUserMerchantDetailAdapter.setOnViewClickListener(new PagerUserMerchantAdapter.ViewClickListener() {
                @Override
                public void onDetail(M_Merchant entity) {
                    Intent intent = new Intent(UserDetailActivity.this, MerchantDetailActivity.class);
                    intent.putExtra("userSaleId", userId);
                    intent.putExtra(MerchantDetailActivity.MERCHANT_ID, entity.merchantId);
                    startActivity(intent);
                }

                @Override
                public void onServiceList(M_Merchant entity) {

                }

                @Override
                public void onServiceHistoryList(M_Merchant entity) {

                }

                @Override
                public void onCommentList(M_Merchant entity) {
                    Intent intent = new Intent(UserDetailActivity.this, ServiceCommentActivity.class);
                    intent.putExtra(ServiceCommentActivity.INTENT_SALEUSERID, userId);
                    intent.putExtra(ServiceCommentActivity.INTENT_MERCHANTID, entity.merchantId);
                    startActivity(intent);
                }

                @Override
                public void onServicePlanList(M_Merchant entity) {
                    Intent intent = new Intent(UserDetailActivity.this, ServicePlanActivity.class);
                    intent.putExtra(ServicePlanActivity.INTENT_SALEUSER_ID, userId);
                    intent.putExtra(ServicePlanActivity.INTENT_MERCHANT_ID, entity.merchantId);
                    startActivity(intent);
                }
            });
        }
    }

    private void changeItem(int position) {
        bindPager.setCurrentItem(position);
        selectMerchant = listMerchant.get(position);
        imageManager.loadBlurImage(selectMerchant.merchantPic, ivCover, 60);
        setBuyState(selectMerchant.reviewstatus == 2);
    }

    private void setBuyState(boolean isCan) {
        if (isCan) {
            llRight2.setVisibility(View.VISIBLE);
            ivRight2.setImageResource(R.mipmap.zhaotamaidan);
        } else {
            llRight2.setVisibility(View.GONE);
        }

    }

    // 用户添加关注
    private void addFocus() {
        showPd();
        if (attractAddRequest != null) {
            attractAddRequest.cancel();
        }
        UserAttractAddRequest.Input input = new UserAttractAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.attractId = userId; // 被关注的用户id

        input.convertJosn();
        attractAddRequest = new UserAttractAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    // 显示已关注
                    btnFocus.setText(R.string.has_focus);
                    Drawable drawable = getResources().getDrawable(R.mipmap.guanzhu);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    btnFocus.setCompoundDrawables(drawable, null, null, null);
                    getUserInfo();
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractAddRequest);
    }

    // 用户取消关注
    private void cancleFocus() {
        showPd();
        if (attractDelRequest != null) {
            attractDelRequest.cancel();
        }
        UserAttractDelRequest.Input input = new UserAttractDelRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.attractId = userId; // 被关注的用户id

        input.convertJosn();
        attractDelRequest = new UserAttractDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    // 显示关注约客
                    btnFocus.setText(R.string.add_focus_yogouser);
                    tvRname.setVisibility(View.GONE);
                    userInfo.remarkName = "";
                    Drawable drawable = getResources().getDrawable(R.mipmap.weiguanzhu);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    btnFocus.setCompoundDrawables(drawable, null, null, null);
                    getUserInfo();

                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractDelRequest);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_right, R.id.lh_tv_title, R.id.ll_right, R.id.btn_focus, R.id.ll_right2, R.id.iv_right2, R.id.rll_call, R.id.rll_im, R.id.btn_service})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_service:
                intent = new Intent(UserDetailActivity.this, CustomerCreateBespeakActivity.class);
                intent.putExtra("userSaleId", userId);
                intent.putExtra("m_merchant", selectMerchant);
                startActivity(intent);
                break;
            case R.id.iv_right:
            case R.id.ll_right:
                if (noLogin(mContext))
                    return;

                if (isNormal) {
                    doReport();
                } else {
                    List<FilterEntity> list = new ArrayList<>();
                    list.add(new FilterEntity("送赞赏", 0, R.mipmap.zanshang_white));
                    list.add(new FilterEntity("投诉举报", 1, R.mipmap.jubao_icon));
                    list.add(new FilterEntity("推荐给好友", 2, R.mipmap.fenxiang_icon));
                    list.add(new FilterEntity("设置备注名", 3, R.mipmap.bianji_icon));


                    CommonDialog.showPopupWindow(mContext, view, list, new CommonDialog.PopClickListener() {
                        @Override
                        public void onClick(int pos) {
                            switch (pos) {
                                case 0:
                                    if (noLogin(mContext))
                                        return;
                                    Intent intent = new Intent(mContext, SendRewardActivity.class);
                                    intent.putExtra(USER_ID, userId);
                                    intent.putExtra(USER_NAME, userName);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    doReport();
                                    break;
                                case 2:
                                    if (sharePopWindow.isShowing())
                                        sharePopWindow.dismiss();
                                    else
                                        sharePopWindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0);
                                    break;

                                case 3:
                                    doEdit();
                                    break;
                            }
                        }
                    });
                }

                break;
            case R.id.ll_right2:
            case R.id.iv_right2:
                if (noLogin(mContext))
                    return;
                intent = new Intent(UserDetailActivity.this, FindPayActivity.class);
                intent.putExtra("userSaleId", userId);
                intent.putExtra("merchantId", selectMerchant.merchantId);
                startActivity(intent);
                break;
            case R.id.rll_call:
                call();
                break;
            case R.id.btn_focus:
                if (noLogin(mContext))
                    return;
                focus_operate();
                break;
            case R.id.rll_im:
                if (noLogin(mContext))
                    return;
                Intent IMkitintent = getIMkit().getChattingActivityIntent(userId + "", Urls.APP_KEY);
                Bundle bundle = new Bundle();
                bundle.putInt("serviceId", userId);
                IMkitintent.putExtras(bundle);
                startActivity(IMkitintent);
                break;

        }
    }

    private void doReport() {
        if (noLogin(mContext))
            return;
        IntentUtil.intStart_activity(mActivity,
                ReportActivity.class,
                new Pair<String, Integer>(ReportActivity.INTENT_INFO_ID, userId),
                new Pair<String, Integer>(ReportActivity.INTENT_INFO_TYPE, 2));

    }

    private void doEdit() {
        if (noLogin(mContext))
            return;
        if ("关注管家".equals(btnFocus.getText().toString())) {
            ToastUtil.showMessage("请先关注管家");
            return;
        }
        Intent intent = new Intent(mContext, RemarkNameActivity.class);
        intent.putExtra("name", userInfo.remarkName);
        intent.putExtra("attractId", userId);
        startActivityForResult(intent, REQ_REMARK_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_ALBUM
                && userId == SlashHelper.userManager().getUserId()) {
            getUserInfo();
        } else if (resultCode == RESULT_OK && requestCode == REQ_REMARK_NAME) {
            tvRname.setVisibility(View.VISIBLE);
            tvRname.setText("备注名：" + data.getStringExtra("name"));
            userInfo.remarkName = data.getStringExtra("name");
        }

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
