package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.LinkagePager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.zemult.merchant.activity.mine.MyFansActivity;
import com.zemult.merchant.activity.mine.MyWalletActivity;
import com.zemult.merchant.activity.mine.SaleManInfoImproveActivity;
import com.zemult.merchant.activity.mine.ServiceHistoryActivity;
import com.zemult.merchant.activity.search.SearchActivity;
import com.zemult.merchant.adapter.slash.PagerUserMerchantAdapter;
import com.zemult.merchant.aip.mine.User2SaleUserFanListRequest;
import com.zemult.merchant.aip.mine.UserEditStateRequest;
import com.zemult.merchant.aip.slash.Merchant2SaleUserMerchantListRequest;
import com.zemult.merchant.aip.slash.User2SaleMerchantEditRequest;
import com.zemult.merchant.aip.slash.UserInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.CreateBespeakNewActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.model.apimodel.APIM_UserFansList;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
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
public class SelfUserDetailActivity extends BaseActivity {
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

    public static final String TAG = SelfUserDetailActivity.class.getSimpleName();
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
    @Bind(R.id.tv_focus)
    TextView tvFocus;
    @Bind(R.id.ll_my_info)
    LinearLayout llMyInfo;
    @Bind(R.id.tv_my_info)
    TextView tvMyInfo;
    @Bind(R.id.tv_text_level)
    TextView tvTextLevel;
    @Bind(R.id.tv_add_level)
    TextView tvAddLevel;
    @Bind(R.id.tv_level)
    TextView tvLevel;
    @Bind(R.id.iv_level)
    ImageView ivLevel;
    @Bind(R.id.tv_text_account)
    TextView tvTextAccount;
    @Bind(R.id.tv_add_money)
    TextView tvAddMoney;
    @Bind(R.id.tv_account)
    TextView tvAccount;
    @Bind(R.id.tv_text_scrm)
    TextView tvTextScrm;
    @Bind(R.id.ll_scrm_head)
    LinearLayout llScrmHead;
    @Bind(R.id.tv_scrm)
    TextView tvScrm;
    @Bind(R.id.iv_add_merchant)
    ImageView ivAddMerchant;
    @Bind(R.id.ll_add_merchant)
    LinearLayout llAddMerchant;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.pager_container)
    LinkagePagerContainer pagerContainer;
    @Bind(R.id.iv_arrow)
    ImageView ivArrow;
    @Bind(R.id.rl_container)
    RelativeLayout rlContainer;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.ll_add)
    LinearLayout llAdd;
    @Bind(R.id.fl_add_merchant)
    FrameLayout flAddMerchant;
    @Bind(R.id.bind_pager)
    LinkagePager bindPager;
    @Bind(R.id.ll_main)
    LinearLayout llMain;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.tv_unsure_num)
    TextView tvUnsureNum;
    @Bind(R.id.yuandianj)
    ImageView yuandianj;

    private Context mContext;
    private Activity mActivity;
    public static int EXIT_MERCHANT = 222;
    //    public static int ADD_MERCHANT = 333;
    public static int EDIT_USER_INFO = 444;
    private int userId;// 用户id(要查看的用户)
    private boolean isSelf = true; //用户是否是自己
    private UserInfoRequest userInfoRequest; // 查看用户(其它人)详情
    UserEditStateRequest userEditStateRequest;
    User2SaleMerchantEditRequest user2SaleMerchantEditRequest;
    User2SaleUserFanListRequest user2SaleUserFanListRequest;
    Merchant2SaleUserMerchantListRequest merchant2SaleUserMerchantListRequest; // 挂靠的商家
    private M_Userinfo userInfo;
    private String userName, userHead;
    private M_Merchant merchant, merchantAdd, selectMerchant;
    private int merchantId;
    PagerUserMerchantAdapter pagerUserMerchantHeadAdapter;
    PagerUserMerchantAdapter pagerUserMerchantDetailAdapter;

    List<M_Merchant> listMerchant = new ArrayList<M_Merchant>();
    List<M_Merchant> listMerchantTemp = new ArrayList<M_Merchant>();
    List<M_Fan> listFan = new ArrayList<M_Fan>();
    int merchantNum = 0;
    LinkagePager pager;
    int state = 0;
    int fromSaleLogin = 0;
    int selectPosition = 0;

    String strPosition = "";

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_self_user_detail);
    }


    @Override
    public void init() {
        initData();
        getNetworkData();
        initView();
        initListener();
    }

    private void initData() {
        merchantAdd = new M_Merchant();
        userId = getIntent().getIntExtra(USER_ID, -1);
        merchant = (M_Merchant) getIntent().getSerializableExtra(MERCHANT_INFO);
        merchantId = getIntent().getIntExtra(MERCHANT_ID, -1);
        userName = getIntent().getStringExtra(USER_NAME);
        userHead = getIntent().getStringExtra(USER_HEAD);
        state = SlashHelper.userManager().getUserinfo().state;
        fromSaleLogin = getIntent().getIntExtra("user_sale_login", 0);
        mContext = this;
        mActivity = this;
        registerReceiver(new String[]{Constants.BROCAST_BE_SERVER_MANAGER_SUCCESS});
    }

    private void initView() {
        lhTvTitle.setText("我的管家详情");
        llRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.fenxiang_icon);
        imageManager.loadCircleHead(userHead, ivHead, "@120w_120h");
        // 用户名
        if (!TextUtils.isEmpty(userName)) {
            tvName.setText(userName);
        }
        tvAccount.setText("￥" + Convert.getMoneyString(SlashHelper.userManager().getUserinfo().money) + "元");
        tvFocus.setText(SlashHelper.userManager().getUserinfo().getFansNum() + "客户关注");

        if (fromSaleLogin == 1) {
            MMAlert.showConfirmDialog(mContext, "服务管家注册成功", getResources().getString(R.string.one_key_service_success), "我知道了", new MMAlert.OneOperateCallback() {
                @Override
                public void onOneOperate() {

                }
            });
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

    }

    private void shareToWX() {
        if (!AppUtils.isWeixinAvailable(this)) {
            ToastUtil.showMessage("你还没有安装微信");
            return;
        }
        UMImage shareImage;
        shareImage = new UMImage(mContext, R.mipmap.icon_share);

        //分享到微信
        new ShareAction(mActivity)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(umShareListener)
                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】正在约服平台上做服务管家, 帮忙关注一下...")
                .withTargetUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx22ea2af5e7d47cb1&redirect_uri=http://www.yovoll.com/dzyx/app/weixinpress_bindphone.do?userId=" + SlashHelper.userManager().getUserId() + "&TargetPage=1&response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect")
                .withMedia(shareImage)
                .withTitle("约服-找个喜欢的人来服务")
                .share();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            ToastUtil.showMessage("分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtil.showMessage("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtil.showMessage("分享取消");
        }
    };

    private void getNetworkData() {
        showPd();
        getUserInfo();
        getFanList();
        getOtherMerchantList();
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
     * 查看TA挂靠的商家
     */
    private void getFanList() {
        if (user2SaleUserFanListRequest != null) {
            user2SaleUserFanListRequest.cancel();
        }
        User2SaleUserFanListRequest.Input input = new User2SaleUserFanListRequest.Input();
        input.saleUserId = SlashHelper.userManager().getUserId();
        input.name = "";
        input.page = 1;
        input.rows = 4;
        input.convertJosn();
        user2SaleUserFanListRequest = new User2SaleUserFanListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserFansList) response).status == 1) {
                    listFan = ((APIM_UserFansList) response).userList;
                    setSCRMHeads(listFan);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(user2SaleUserFanListRequest);
    }

    private void setSCRMHeads(List<M_Fan> listFans) {
        if (listFans != null && listFans.size() > 0) {
            llScrmHead.removeAllViews();
            for (int i = 0; i < listFans.size(); i++) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(DensityUtil.dip2px(this, 40), DensityUtil.dip2px(this, 40)));
                imageView.setPadding(0, 0, DensityUtil.dip2px(this, 12), 0);
                imageManager.loadCircleHead(listFans.get(i).head, imageView, "@120w_120h");
                llScrmHead.addView(imageView);
            }
        }


    }

    /**
     * 设置用户信息
     *
     * @param userInfo
     */
    private void setUserInfo(M_Userinfo userInfo) {
        Drawable drawable;
        // 头像
        if (!TextUtils.isEmpty(userInfo.getHead()) && !TextUtils.isEmpty(userInfo.getName())) {
            tvUnsureNum.setVisibility(View.INVISIBLE);
            yuandianj.setVisibility(View.INVISIBLE);
        } else {
            yuandianj.setVisibility(View.VISIBLE);
            tvUnsureNum.setVisibility(View.VISIBLE);
        }

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

        merchantNum = userInfo.saleUserNum;

        this.userInfo = userInfo;
        this.userInfo.setUserId(userId);
        this.userInfo.setUserName(userName);
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
            flAddMerchant.setVisibility(View.GONE);
            bindPager.setVisibility(View.GONE);
            llAddMerchant.setVisibility(View.VISIBLE);
        } else {
            flAddMerchant.setVisibility(View.VISIBLE);
            bindPager.setVisibility(View.VISIBLE);
            llAddMerchant.setVisibility(View.GONE);

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
            selectMerchant = listMerchant.get(selectPosition);
            changeItem(selectPosition);
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
                    Log.i("Scroll", "i=" + i + ", v=" + v + ", i1=" + i1);
                    if (i1 > 100)
                        ivAdd.setVisibility(View.GONE);
                    if (i == 0 && i1 < 50) {
                        ivAdd.setVisibility(View.VISIBLE);
                    }
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

            pagerUserMerchantDetailAdapter.setOnViewMerchantClickListener(new PagerUserMerchantAdapter.ViewMerchantClickListener() {
                @Override
                public void onMerchantManage(M_Merchant entity) {
                    Intent intent = new Intent(SelfUserDetailActivity.this, MerchantAdminActivity.class);
                    intent.putExtra(MerchantDetailActivity.MERCHANT_ID, entity.merchantId);
                    intent.putExtra(UserDetailActivity.MERCHANT_INFO, entity);
                    startActivityForResult(intent, EXIT_MERCHANT);
                }
            });

//            pagerUserMerchantDetailAdapter.setOnViewTagClickListener(new PagerUserMerchantAdapter.ViewTagClickListener() {
//                @Override
//                public void onTagManage(M_Merchant merchant) {
//                    Intent intent = new Intent(mActivity, TabManageActivity.class);
//                    intent.putExtra(TabManageActivity.TAG, merchant.merchantId);
//                    intent.putExtra(TabManageActivity.NAME, merchant.merchantName);
//                    intent.putExtra(TabManageActivity.TAGS, merchant.tags);
//                    intent.putExtra(TabManageActivity.COMEFROM, 2);
//                    startActivityForResult(intent, MODIFY_TAG);
//                }
//            });

            pagerUserMerchantDetailAdapter.setOnViewStateClickListener(new PagerUserMerchantAdapter.ViewStateClickListener() {
                @Override
                public void onStateManage(final M_Merchant entity) {
                    MMAlert.showChooseStateDialog(mContext, new MMAlert.ChooseCallback() {
                        @Override
                        public void onfirstChoose() {
                            state = 0;
                            userEditInfo();
                        }

                        @Override
                        public void onthirdChoose() {
                            state = 2;
                            userEditInfo();
                        }
                    });
                }
            });

//            pagerUserMerchantDetailAdapter.setOnViewPositionClickListener(new PagerUserMerchantAdapter.ViewPositionClickListener() {
//                @Override
//                public void onPositionManage(M_Merchant entity) {
//                    Intent intent = new Intent(SelfUserDetailActivity.this, PositionSetActivity.class);
//                    intent.putExtra("position_name", strPosition);
//                    startActivityForResult(intent, MODIFY_POSITION);
//                }
//            });

            pagerUserMerchantDetailAdapter.setOnViewClickListener(new PagerUserMerchantAdapter.ViewClickListener() {
                @Override
                public void onDetail(M_Merchant entity) {

                }

                @Override
                public void onServiceList(M_Merchant entity) {
                    Intent intent = new Intent(SelfUserDetailActivity.this, ServiceTicketListActivity.class);
                    intent.putExtra("saleUserId", userId);
                    intent.putExtra("merchantId", entity.merchantId);
                    // 有待确认的服务单跳转到待确认列表
                    if (entity.unSureOrderNum > 0) {
                        intent.putExtra("page_position", 1);
                    }
                    startActivity(intent);
                }

                @Override
                public void onServiceHistoryList(M_Merchant entity) {
                    Intent intent = new Intent(SelfUserDetailActivity.this, ServiceHistoryActivity.class);
                    intent.putExtra("saleUserId", userId);
                    intent.putExtra("merchantId", entity.merchantId);
                    startActivity(intent);
                }

                @Override
                public void onCommentList(M_Merchant entity) {
                    Intent intent = new Intent(SelfUserDetailActivity.this, ServiceCommentActivity.class);
                    intent.putExtra(ServiceCommentActivity.INTENT_SALEUSERID, userId);
                    intent.putExtra(ServiceCommentActivity.INTENT_MERCHANTID, entity.merchantId);
                    startActivity(intent);
                }

                @Override
                public void onServicePlanList(M_Merchant entity) {
                    Intent intent = new Intent(SelfUserDetailActivity.this, ServicePlanActivity.class);
                    intent.putExtra(ServicePlanActivity.INTENT_SALEUSER_ID, userId);
                    intent.putExtra(ServicePlanActivity.INTENT_MERCHANT_ID, entity.merchantId);
                    startActivity(intent);
                }
            });
        }
    }

    private void changeItem(int position) {
        selectPosition = position;
        pager.setCurrentItem(selectPosition);
        bindPager.setCurrentItem(selectPosition);
        selectMerchant = listMerchant.get(selectPosition);
        state = selectMerchant.state;
        strPosition = (selectMerchant.position == null ? "无" : selectMerchant.position);
        imageManager.loadBlurImage(selectMerchant.merchantPic, ivCover, 60);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_contact,R.id.infoimprove_rl, R.id.ll_my_info, R.id.ll_add, R.id.iv_add, R.id.tv_add_level, R.id.tv_level, R.id.iv_level, R.id.tv_account, R.id.ll_scrm_head, R.id.tv_scrm, R.id.iv_add_merchant, R.id.ll_right, R.id.iv_right})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                Intent in = new Intent(Constants.BROCAST_LOGIN);
                sendBroadcast(in);
                onBackPressed();
                break;
            case R.id.btn_contact:
                Intent cbintent =new Intent(SelfUserDetailActivity.this,CreateBespeakNewActivity.class);
                cbintent.putExtra("reviewstatus",selectMerchant.reviewstatus);
                cbintent.putExtra("merchantName",selectMerchant.merchantName);
                cbintent.putExtra("merchantId",selectMerchant.merchantId+"");
                startActivity(cbintent);

                break;
            case R.id.ll_my_info:
            case R.id.infoimprove_rl:
                intent = new Intent(mActivity, SaleManInfoImproveActivity.class);
                startActivityForResult(intent, EDIT_USER_INFO);
                break;
            case R.id.ll_add:
            case R.id.iv_add:
            case R.id.iv_add_merchant:
                if (userInfo.getMaxMerchantNum() == listMerchant.size()) {
                    ToastUtil.showMessage(String.format("您当前服务等级只能申请%d家商户, 当前已经申请了%d家", userInfo.getMaxMerchantNum(), listMerchant.size()));
                    return;
                }
                intent = new Intent(mActivity, SearchActivity.class);
                intent.putExtra("be_service_manager", 1);
                startActivity(intent);

                break;
            case R.id.tv_add_level:
                break;
            case R.id.tv_level:
            case R.id.iv_level:
                IntentUtil.start_activity(this, BaseWebViewActivity.class,
                        new Pair<String, String>("titlename", "服务等级"), new Pair<String, String>("url", Constants.SERVICELEVEL));
                break;
            case R.id.tv_account:
                startActivity(new Intent(SelfUserDetailActivity.this, MyWalletActivity.class));
                break;
            case R.id.ll_scrm_head:
            case R.id.tv_scrm:
                intent = new Intent(mActivity, MyFansActivity.class);
                intent.putExtra(MyFansActivity.INTENT_USERID, userId);
                startActivity(intent);
                break;
            case R.id.ll_right:
            case R.id.iv_right:
                shareToWX();
                break;
        }
    }


    private void userEditInfo() {
        if (user2SaleMerchantEditRequest != null) {
            user2SaleMerchantEditRequest.cancel();
        }
        showPd();
        User2SaleMerchantEditRequest.Input input = new User2SaleMerchantEditRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.merchantId = selectMerchant.merchantId;
        input.tags = selectMerchant.tags;
        input.state = state;
        input.position = strPosition.equals("") ? "无" : strPosition;
        input.convertJosn();
        user2SaleMerchantEditRequest = new User2SaleMerchantEditRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    SlashHelper.userManager().getUserinfo().setState(state);
                    getOtherMerchantList();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(user2SaleMerchantEditRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == EXIT_MERCHANT) {
                selectPosition = 0;
                getOtherMerchantList();

            } else if (requestCode == EDIT_USER_INFO) {
                getUserInfo();

            }
        }
    }

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_BE_SERVER_MANAGER_SUCCESS.equals(intent.getAction())) {
            selectPosition = 0;
            getOtherMerchantList();
        }
    }

}
