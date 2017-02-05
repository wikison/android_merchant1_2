package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.android.volley.VolleyError;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ReportActivity;
import com.zemult.merchant.adapter.slash.TaMerchantAdapter;
import com.zemult.merchant.adapter.slashfrgment.SendRewardAdapter;
import com.zemult.merchant.aip.mine.UserAttractAddRequest;
import com.zemult.merchant.aip.mine.UserAttractDelRequest;
import com.zemult.merchant.aip.slash.MerchantOtherMerchantListRequest;
import com.zemult.merchant.aip.slash.UserInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.CreateBespeakActivity;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.FixedGridView;
import com.zemult.merchant.view.FixedListView;
import com.zemult.merchant.view.common.MMAlert;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
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
    public static final String USER_NAME = "userName"; // 用户名
    public static final String USER_HEAD = "userHead"; // 用户头像
    public static final String USER_SEX = "userSex"; // 用户性别

    private static final int REQ_ALBUM = 0x110;
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
    @Bind(R.id.btn_contact)
    RoundTextView btnContact;
    @Bind(R.id.ll_bottom)
    RelativeLayout llBottom;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.btn_focus)
    RoundTextView btnFocus;
    @Bind(R.id.tv_buy_num)
    TextView tvBuyNum;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.ll_phone)
    LinearLayout llPhone;
    @Bind(R.id.gv_pic)
    FixedGridView gvPic;
    @Bind(R.id.ll_photo)
    LinearLayout llPhoto;
    @Bind(R.id.flv_merchant)
    FixedListView flvMerchant;
    @Bind(R.id.btn_gift)
    RoundTextView btnGift;
    @Bind(R.id.btn_buy)
    RoundTextView btnBuy;
    @Bind(R.id.btn_service)
    RoundTextView btnService;
    @Bind(R.id.tv_level)
    TextView tvLevel;
    @Bind(R.id.num_tv)
    TextView numTv;


    private Context mContext;
    private Activity mActivity;
    private int userId;// 用户id(要查看的用户)
    private UserInfoRequest userInfoRequest; // 查看用户(其它人)详情
    private MerchantOtherMerchantListRequest merchantOtherMerchantListRequest; // 挂靠的商家
    private UserAttractAddRequest attractAddRequest; // 添加关注
    private UserAttractDelRequest attractDelRequest; // 取消关注
    private M_Userinfo userInfo;
    private String userName, userHead;
    private M_Merchant merchant;
    TaMerchantAdapter taMerchantAdapter;
    List<M_Merchant> listMerchant = new ArrayList<M_Merchant>();

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_user_detail);
    }

    private YWIMKit getIMkit() {
        YWIMKit imkit = LoginSampleHelper.getInstance().getIMKit();
        return imkit;
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
        userName = getIntent().getStringExtra(USER_NAME);
        userHead = getIntent().getStringExtra(USER_HEAD);

        mContext = this;
        mActivity = this;
    }

    private void initView() {
        lhTvTitle.setText("个人详情");

        // 用户头像
        if (!TextUtils.isEmpty(userHead)) {
            imageManager.loadCircleImage(userHead, ivHead);
        }
        // 用户名
        if (!TextUtils.isEmpty(userName))
            tvName.setText(userName);

        if (userId == SlashHelper.userManager().getUserId()) {
            btnFocus.setVisibility(View.GONE);
            llBottom.setVisibility(View.GONE);
        } else {
            llRight.setVisibility(View.VISIBLE);
            ivRight.setImageResource(R.mipmap.jubao_white_icon);
        }


        btnBuy.setWidth(DensityUtil.getWindowWidth(this) / 2 - DensityUtil.dip2px(this, 86));
        btnGift.setWidth(DensityUtil.getWindowWidth(this) / 2 - DensityUtil.dip2px(this, 86));
        taMerchantAdapter = new TaMerchantAdapter(mContext, listMerchant);
        flvMerchant.setAdapter(taMerchantAdapter);

    }

    private void initListener() {
        taMerchantAdapter.setOnAllClickListener(new TaMerchantAdapter.OnAllClickListener() {
            @Override
            public void onAllClick(int position) {
                IntentUtil.intStart_activity(mActivity,
                        MerchantDetailActivity.class, new Pair<String, Integer>(MerchantDetailActivity.MERCHANT_ID, taMerchantAdapter.getItem(position).merchantId));
            }
        });

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
     * 设置用户信息
     *
     * @param userInfo
     */
    private void setUserInfo(M_Userinfo userInfo) {
        Drawable drawable;
        // 头像
        if (!TextUtils.isEmpty(userInfo.getHead())) {
            imageManager.loadCircleImage(userInfo.getHead(), ivHead, "@120w_120h");
        }
        // 用户名
        if (!TextUtils.isEmpty(userInfo.getName()))
            tvName.setText(userInfo.getName());
        // 电话
        tvPhone.setText(userInfo.getPhoneNum());
        if (!TextUtils.isEmpty(userInfo.getPhoneNum())) {
            if (userInfo.getIsOpen() == 1) {
                llPhone.setVisibility(View.VISIBLE);
            } else
                llPhone.setVisibility(View.GONE);
        }

        tvLevel.setText(userInfo.getExperienceText());
        if (userInfo.getExperienceImg() > 0) {
            drawable = getResources().getDrawable(userInfo.getExperienceImg());
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvLevel.setCompoundDrawables(drawable, null, null, null);
        }

        //是否有挂靠商家
        if (userInfo.saleUserNum == 0) {
            btnService.setVisibility(View.INVISIBLE);
            btnBuy.setVisibility(View.INVISIBLE);
            btnGift.setVisibility(View.INVISIBLE);
            numTv.setText("");
        } else {
            btnService.setVisibility(View.VISIBLE);
            btnBuy.setVisibility(View.VISIBLE);
            btnGift.setVisibility(View.VISIBLE);
            numTv.setText("共在"+userInfo.saleUserNum+"家商户提供服务");
        }




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


        // 买单数
        //tvBuyNum.setText(userInfo.saleNum + "人找TA买单");
        // 相册图片(多个用","分隔，最多显示3个)
//        if (!TextUtils.isEmpty(userInfo.pics)) {
//            PhotoFix3Adapter adapter = new PhotoFix3Adapter(mContext, userInfo.pics);
//            adapter.setWidth(DensityUtil.dip2px(mContext, 50));
//            adapter.setRule("@50p");
//            gvPic.setAdapter(adapter);
//        }
        // 是否已经关注(0:未关注1:已关注)
        if (userInfo.getIsFan() == 0) {
            btnFocus.setText(R.string.add_focus_yogouser);
        } else {
            btnFocus.setText(R.string.has_focus);
        }

        this.userInfo = userInfo;
        this.userInfo.setUserId(userId);
        this.userInfo.setUserName(userName);
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
    private void fillAdapter(List<M_Merchant> list, boolean isLoadMore) {
        if (list == null || list.size() == 0) {

        }
        taMerchantAdapter.setData(list, isLoadMore);
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
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractDelRequest);
    }

    private void doReport() {
        if (noLogin(mContext))
            return;
        IntentUtil.intStart_activity(mActivity,
                ReportActivity.class,
                new Pair<String, Integer>(ReportActivity.INTENT_INFO_ID, userId),
                new Pair<String, Integer>(ReportActivity.INTENT_INFO_TYPE, 2));

    }

    @OnClick({R.id.lh_btn_back, R.id.btn_buy, R.id.btn_service, R.id.ll_back, R.id.iv_right, R.id.ll_right, R.id.tv_phone, R.id.ll_photo, R.id.btn_contact, R.id.btn_focus, R.id.btn_gift})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_right:
            case R.id.ll_right:
                doReport();
                break;
            case R.id.ll_photo:
                intent = new Intent(mContext, TAMerchantListActivity.class);
                intent.putExtra(USER_ID, userId);
                startActivity(intent);
                break;
            case R.id.tv_phone:
                call();
                break;
            case R.id.btn_buy:
                if (noLogin(mContext))
                    return;
                intent = new Intent(mContext, ChoosePayMerchantActivity.class);
                intent.putExtra(USER_ID, userId);
                startActivity(intent);
                break;
            case R.id.btn_contact:
                if (noLogin(mContext))
                    return;
                Intent IMkitintent = getIMkit().getChattingActivityIntent(userId + "", LoginSampleHelper.APP_KEY);
                startActivity(IMkitintent);
                break;
            case R.id.btn_focus:
                if (noLogin(mContext))
                    return;
                focus_operate();
                break;
            case R.id.btn_service:
                if (noLogin(mContext))
                    return;

                if (merchant != null) {
                    Intent merchantintent = new Intent(mContext, CreateBespeakActivity.class);
                    merchantintent.putExtra("serviceId", userId);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("m_merchant", merchant);
                    merchantintent.putExtras(mBundle);
                    startActivity(merchantintent);
                } else {
                    intent = new Intent(mContext, ChooseReservationMerchantActivity.class);
                    intent.putExtra("actionFrom", "UserDetailActivity");// 管家id
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }


                break;
            case R.id.btn_gift:
                if (noLogin(mContext))
                    return;
//                intent = new Intent(mContext, SendPresentActivity.class);
                intent = new Intent(mContext, SendRewardActivity.class);
                intent.putExtra(USER_ID, userId);
                intent.putExtra(USER_NAME, userName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
