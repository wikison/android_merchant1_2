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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.MyFansActivity;
import com.zemult.merchant.activity.mine.MyWalletActivity;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.adapter.slash.PagerUserMerchantAdapter;
import com.zemult.merchant.aip.mine.UserEditStateRequest;
import com.zemult.merchant.aip.slash.MerchantOtherMerchantListRequest;
import com.zemult.merchant.aip.slash.UserInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.FixedGridView;
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
    @Bind(R.id.tv_add_fan)
    TextView tvAddFan;
    @Bind(R.id.tv_text_state)
    TextView tvTextState;
    @Bind(R.id.iv_state)
    ImageView ivState;
    @Bind(R.id.tv_state)
    TextView tvState;
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
    @Bind(R.id.fgv_scrm)
    FixedGridView fgvScrm;
    @Bind(R.id.tv_scrm)
    TextView tvScrm;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.pager_container)
    LinkagePagerContainer pagerContainer;
    @Bind(R.id.iv_arrow)
    ImageView ivArrow;
    @Bind(R.id.rl_container)
    RelativeLayout rlContainer;
    @Bind(R.id.ll_add)
    LinearLayout llAdd;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.bind_pager)
    LinkagePager bindPager;
    @Bind(R.id.ll_main)
    LinearLayout llMain;
    @Bind(R.id.btn_service)
    Button btnService;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;

    private Context mContext;
    private Activity mActivity;
    public static int MODIFY_TAG = 111;
    private int userId;// 用户id(要查看的用户)
    private boolean isSelf = true; //用户是否是自己
    private UserInfoRequest userInfoRequest; // 查看用户(其它人)详情
    UserEditStateRequest userEditStateRequest;
    private MerchantOtherMerchantListRequest merchantOtherMerchantListRequest; // 挂靠的商家
    private M_Userinfo userInfo;
    private String userName, userHead;
    private M_Merchant merchant, merchantAdd, selectMerchant;
    private int merchantId;
    PagerUserMerchantAdapter pagerUserMerchantHeadAdapter;
    PagerUserMerchantAdapter pagerUserMerchantDetailAdapter;

    List<M_Merchant> listMerchant = new ArrayList<M_Merchant>();
    int merchantNum = 0;
    LinkagePager pager;
    int state = 0;

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
        mContext = this;
        mActivity = this;

    }

    private void initView() {
        lhTvTitle.setText("管家详情");
        imageManager.loadCircleHead(userHead, ivHead, "@120w_120h");
        // 用户名
        if (!TextUtils.isEmpty(userName)) {
            tvName.setText(userName);
        }
        dealState(state);
        tvAccount.setText("￥" + Convert.getMoneyString(SlashHelper.userManager().getUserinfo().money) + "元");
        tvFocus.setText(SlashHelper.userManager().getUserinfo().getFansNum() + "客户关注");
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

    private void getNetworkData() {
        showPd();
        getUserInfo();
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

        merchantNum = userInfo.saleUserNum;

        this.userInfo = userInfo;
        this.userInfo.setUserId(userId);
        this.userInfo.setUserName(userName);
    }


    // 填充数据
    private void fillAdapter(List<M_Merchant> list) {
        if (list == null || list.size() == 0) {
            btnService.setVisibility(View.GONE);
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
                    Intent intent = new Intent(SelfUserDetailActivity.this, MerchantDetailActivity.class);
                    intent.putExtra("userSaleId", userId);
                    intent.putExtra(MerchantDetailActivity.MERCHANT_ID, entity.merchantId);
                    startActivity(intent);
                }
            });

            pagerUserMerchantDetailAdapter.setOnViewTagClickListener(new PagerUserMerchantAdapter.ViewTagClickListener() {
                @Override
                public void onTagManage(M_Merchant merchant) {
                    Intent intent = new Intent(mActivity, TabManageActivity.class);
                    intent.putExtra(TabManageActivity.TAG, merchant.merchantId);
                    intent.putExtra(TabManageActivity.NAME, merchant.name);
                    intent.putExtra(TabManageActivity.TAGS, merchant.tags);
                    intent.putExtra(TabManageActivity.COMEFROM, 2);
                    startActivityForResult(intent, MODIFY_TAG);
                }
            });
        }
    }

    private void changeItem(int position) {
        bindPager.setCurrentItem(position);
        selectMerchant = listMerchant.get(position);
        imageManager.loadBlurImage(selectMerchant.pic, ivCover, 60);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.ll_add, R.id.iv_add, R.id.tv_state, R.id.tv_add_level, R.id.tv_level, R.id.iv_level, R.id.tv_account, R.id.tv_scrm})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ll_add:
            case R.id.iv_add:
                ToastUtil.showMessage("add");
                break;
            case R.id.tv_state:
                MMAlert.showChooseStateDialog(this, new MMAlert.ChooseCallback() {
                    @Override
                    public void onfirstChoose() {
                        state = 0;
                        userEditState();
                    }

                    @Override
                    public void onthirdChoose() {
                        state = 2;
                        userEditState();
                    }
                });

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
            case R.id.tv_scrm:
                intent = new Intent(mActivity, MyFansActivity.class);
                intent.putExtra(MyFansActivity.INTENT_USERID, -1);
                startActivity(intent);
                break;
        }
    }

    private void userEditState() {
        if (userEditStateRequest != null) {
            userEditStateRequest.cancel();
        }
        showPd();
        UserEditStateRequest.Input input = new UserEditStateRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.state = state;
        input.convertJosn();
        userEditStateRequest = new UserEditStateRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    SlashHelper.userManager().getUserinfo().setState(state);
                    dealState(state);
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userEditStateRequest);
    }

    //处理状态
    private void dealState(int state) {
        if (state == 0) {
            ivState.setImageResource(R.mipmap.kongxian);
            tvState.setText("空闲");
            tvState.setTextColor(getResources().getColor(R.color.font_idle));
        } else if (state == 1) {
            ivState.setImageResource(R.mipmap.xiuxi_icon);
            tvState.setText("休息");
            tvState.setTextColor(getResources().getColor(R.color.font_black_999));
        } else if (state == 2) {
            ivState.setImageResource(R.mipmap.manglu);
            tvState.setText("忙碌");
            tvState.setTextColor(getResources().getColor(R.color.font_busy));
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MODIFY_TAG && resultCode == RESULT_OK) {
            getOtherMerchantList();
        }

    }
}
