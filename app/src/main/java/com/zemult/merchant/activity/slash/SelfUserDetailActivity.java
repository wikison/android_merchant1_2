package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.LinkagePager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slash.PagerUserMerchantAdapter;
import com.zemult.merchant.aip.slash.MerchantOtherMerchantListRequest;
import com.zemult.merchant.aip.slash.UserInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.FixedGridView;

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
    @Bind(R.id.iv_level)
    ImageView ivLevel;
    @Bind(R.id.tv_level)
    TextView tvLevel;
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
    private int userId;// 用户id(要查看的用户)
    private boolean isSelf = false; //用户是否是自己
    private UserInfoRequest userInfoRequest; // 查看用户(其它人)详情
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

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_self_user_detail);
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
        merchantAdd= new M_Merchant();
        userId = getIntent().getIntExtra(USER_ID, -1);
        merchant = (M_Merchant) getIntent().getSerializableExtra(MERCHANT_INFO);
        merchantId = getIntent().getIntExtra(MERCHANT_ID, -1);
        userName = getIntent().getStringExtra(USER_NAME);
        userHead = getIntent().getStringExtra(USER_HEAD);

        mContext = this;
        mActivity = this;

    }

    private void initView() {
        lhTvTitle.setText("管家详情");
        imageManager.loadCircleHead(userHead, ivHead, "@120w_120h");
        // 用户名
        if (!TextUtils.isEmpty(userName))
            tvName.setText(userName);

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
            imageManager.loadBlurImage(selectMerchant.pic, ivCover, 60);
            pagerContainer.setPageItemClickListener(new PageItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position >= 0 && position < listMerchant.size()) {
                        bindPager.setCurrentItem(position);
                        imageManager.loadBlurImage(listMerchant.get(position).head, ivCover, 60);
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
                        imageManager.loadBlurImage(listMerchant.get(position).pic, ivCover, 60);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            pagerUserMerchantDetailAdapter.setOnViewClickListener(new PagerUserMerchantAdapter.ViewClickListener() {
                @Override
                public void onDetail(M_Merchant entity) {
                    Intent intent = new Intent(SelfUserDetailActivity.this, MerchantDetailActivity.class);
                    intent.putExtra("userSaleId", userId);
                    intent.putExtra(MerchantDetailActivity.MERCHANT_ID, entity.merchantId);
                    startActivity(intent);
                }
            });
        }
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        Intent intent;
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
        if (resultCode == RESULT_OK && requestCode == REQ_ALBUM
                && userId == SlashHelper.userManager().getUserId()) {
            getUserInfo();
        }

    }


}
