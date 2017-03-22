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
import com.flyco.roundview.RoundTextView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ReportActivity;
import com.zemult.merchant.activity.mine.RemarkNameActivity;
import com.zemult.merchant.adapter.slash.PagerUserMerchantAdapter;
import com.zemult.merchant.adapter.slash.TaMerchantAdapter;
import com.zemult.merchant.aip.mine.UserAttractAddRequest;
import com.zemult.merchant.aip.mine.UserAttractDelRequest;
import com.zemult.merchant.aip.slash.MerchantOtherMerchantListRequest;
import com.zemult.merchant.aip.slash.UserInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.CreateBespeakActivity;
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
import com.zemult.merchant.view.common.CommonDialog;
import com.zemult.merchant.view.common.MMAlert;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
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

    public static final String TAG = UserDetailActivity.class.getSimpleName();
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
    @Bind(R.id.tv_buy_num)
    TextView tvBuyNum;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.pager_container)
    LinkagePagerContainer pagerContainer;
    @Bind(R.id.rl_container)
    RelativeLayout rlContainer;
    @Bind(R.id.bind_pager)
    LinkagePager bindPager;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.ll_phone)
    LinearLayout llPhone;
    @Bind(R.id.ll_contact)
    LinearLayout llContact;
    @Bind(R.id.ll_gift)
    LinearLayout llGift;
    @Bind(R.id.ll_bottom_menu)
    LinearLayout llBottomMenu;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.btn_buy)
    RoundTextView btnBuy;
    @Bind(R.id.btn_service)
    Button btnService;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;

    private Context mContext;
    private Activity mActivity;
    private int userId;// 用户id(要查看的用户)
    private boolean isSelf = false; //用户是否是自己
    private UserInfoRequest userInfoRequest; // 查看用户(其它人)详情
    private MerchantOtherMerchantListRequest merchantOtherMerchantListRequest; // 挂靠的商家
    private UserAttractAddRequest attractAddRequest; // 添加关注
    private UserAttractDelRequest attractDelRequest; // 取消关注
    private M_Userinfo userInfo;
    private String userName, userHead;
    private M_Merchant merchant, selectMerchant;
    private int merchantId;
    TaMerchantAdapter taMerchantAdapter;
    PagerUserMerchantAdapter pagerUserMerchantHeadAdapter;
    PagerUserMerchantAdapter pagerUserMerchantDetailAdapter;

    List<M_Merchant> listMerchant = new ArrayList<M_Merchant>();
    int merchantNum = 0;
    boolean isFromMerchant;
    LinkagePager pager;
    private SharePopwindow sharePopWindow;

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
        initView();
        getNetworkData();
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
            btnBuy.setVisibility(View.GONE);
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
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】给您推荐了一个服务管家【" + userName + "】快去看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】向您推荐服务管家【" + userName + "】")
                                .share();
                        break;

                    case SharePopwindow.WECHAT:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】给您推荐了一个服务管家【" + userName + "】快去看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】向您推荐服务管家【" + userName + "】")
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】给您推荐了一个服务管家【" + userName + "】快去看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】向您推荐服务管家【" + userName + "】")
                                .share();
                        break;

                    case SharePopwindow.QQ:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(umShareListener)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】给您推荐了一个服务管家【" + userName + "】快去看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】向您推荐服务管家【" + userName + "】")
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
                    listMerchant = ((APIM_MerchantList) response).merchantList;

                    fillAdapter(listMerchant);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchantOtherMerchantListRequest);
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
        tvLevel.setText(userInfo.getExperienceText());
        if (userInfo.getExperienceImg() > 0) {
            drawable = getResources().getDrawable(userInfo.getExperienceImg());
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvLevel.setCompoundDrawables(drawable, null, null, null);
        }

        // 电话
        if (!TextUtils.isEmpty(userInfo.getPhoneNum())) {
            if (userInfo.getIsOpen() == 1) {
                tvPhone.setText("打电话");
            } else {
                tvPhone.setText("未公开");
            }
        }

        merchantNum = userInfo.saleUserNum;

        switch (userInfo.getState()) {
            case 0:
                drawable = getResources().getDrawable(R.mipmap.kongxian_icon);
                // 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvBuyNum.setCompoundDrawables(drawable, null, null, null);
                tvBuyNum.setTextColor(getResources().getColor(R.color.font_idle));
                tvBuyNum.setText("空闲");
                break;
            case 1:
                drawable = getResources().getDrawable(R.mipmap.xiuxi_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvBuyNum.setCompoundDrawables(drawable, null, null, null);
                tvBuyNum.setTextColor(getResources().getColor(R.color.font_black_999));
                tvBuyNum.setText("休息");
                break;
            case 2:
                drawable = getResources().getDrawable(R.mipmap.manglu_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvBuyNum.setCompoundDrawables(drawable, null, null, null);
                tvBuyNum.setTextColor(getResources().getColor(R.color.font_busy));
                tvBuyNum.setText("忙碌");
                break;
        }

        // 是否已经关注(0:未关注1:已关注)
        tvRname.setVisibility(View.GONE);
        if (userInfo.getIsFan() == 0) {
            btnFocus.setText(R.string.add_focus_yogouser);
        } else {
            btnFocus.setText(R.string.has_focus);
            if (!TextUtils.isEmpty(userInfo.remarkName)) {
                tvRname.setVisibility(View.VISIBLE);
                tvRname.setText("备注名：" + userInfo.remarkName);
            }
        }

        this.userInfo = userInfo;
        this.userInfo.setUserId(userId);
        this.userInfo.setUserName(userName);
        if (userInfo.getIsSaleUser() > 0) {
            lhTvTitle.setText("管家详情");
        } else {
            lhTvTitle.setText("个人详情");
        }

    }

    /**
     * 拨打电话
     */
    private void call() {
        if (userInfo.getIsOpen() == 0)
            return;
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


    // 填充数据
    private void fillAdapter(List<M_Merchant> list) {
        if (list == null || list.size() == 0) {
            btnBuy.setVisibility(View.GONE);
            btnService.setVisibility(View.GONE);
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
            if (selectMerchant.reviewstatus != 2) {
                btnBuy.setVisibility(View.GONE);
            }
            imageManager.loadBlurImage(selectMerchant.pic, ivCover);
            pagerContainer.setPageItemClickListener(new PageItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position >= 0 && position < listMerchant.size()) {
                        bindPager.setCurrentItem(position);
                        imageManager.loadBlurImage(listMerchant.get(position).head, ivCover);
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
                        bindPager.setCurrentItem(position);
                        selectMerchant = listMerchant.get(position);
                        imageManager.loadBlurImage(listMerchant.get(position).pic, ivCover);
                        if (selectMerchant.reviewstatus != 2) {
                            btnBuy.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

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
            });
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
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractDelRequest);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_right, R.id.ll_right, R.id.btn_focus, R.id.ll_phone, R.id.tv_phone, R.id.ll_contact, R.id.ll_gift, R.id.btn_buy, R.id.btn_service})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_right:
            case R.id.ll_right:
                if (noLogin(mContext))
                    return;
                if (noHead(mContext))
                    return;

                List<FilterEntity> list = new ArrayList<>();
                list.add(new FilterEntity("设置备注名", 0, R.mipmap.bianji_icon));
                list.add(new FilterEntity("推荐给朋友", 1, R.mipmap.fenxiang_icon));
                list.add(new FilterEntity("投诉举报", 2, R.mipmap.jubao_icon));


                CommonDialog.showPopupWindow(mContext, view, list, new CommonDialog.PopClickListener() {
                    @Override
                    public void onClick(int pos) {
                        switch (pos) {
                            case 0:
                                doEdit();
                                break;
                            case 1:
                                if (sharePopWindow.isShowing())
                                    sharePopWindow.dismiss();
                                else
                                    sharePopWindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0);
                                break;
                            case 2:
                                doReport();
                                break;
                        }
                    }
                });

                break;
            case R.id.btn_buy:
                if (noLogin(mContext))
                    return;
                intent = new Intent(UserDetailActivity.this, FindPayActivity.class);
                intent.putExtra("userSaleId", userId);
                intent.putExtra("merchantId", selectMerchant.merchantId);
                startActivity(intent);
                break;
            case R.id.btn_service:
                if (noLogin(mContext))
                    return;
                intent = new Intent(mContext, CreateBespeakActivity.class);
                intent.putExtra("serviceId", userId);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("m_merchant", selectMerchant);
                intent.putExtras(mBundle);
                startActivity(intent);
                break;
            case R.id.ll_phone:
            case R.id.tv_phone:
                call();
                break;
            case R.id.ll_contact:
                if (noLogin(mContext))
                    return;
                Intent IMkitintent = getIMkit().getChattingActivityIntent(userId + "", Urls.APP_KEY);
                startActivity(IMkitintent);
                break;
            case R.id.btn_focus:
                if (noLogin(mContext))
                    return;
                focus_operate();
                break;
            case R.id.ll_gift:
                if (noLogin(mContext))
                    return;
                intent = new Intent(mContext, SendRewardActivity.class);
                intent.putExtra(USER_ID, userId);
                intent.putExtra(USER_NAME, userName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
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
        if (btnFocus.getText().toString().contains("＋")) {
            ToastUtil.showMessage("请先＋熟人");
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

}
