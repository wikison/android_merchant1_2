package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
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
import com.zemult.merchant.activity.mine.AlbumActivity;
import com.zemult.merchant.adapter.slash.TaMerchantAdapter;
import com.zemult.merchant.adapter.slashfrgment.PhotoFix3Adapter;
import com.zemult.merchant.aip.mine.UserAttractAddRequest;
import com.zemult.merchant.aip.mine.UserAttractDelRequest;
import com.zemult.merchant.aip.slash.MerchantOtherMerchantListRequest;
import com.zemult.merchant.aip.slash.UserInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SPUtils;
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
    @Bind(R.id.ll_phote)
    LinearLayout llPhote;
    @Bind(R.id.flv_merchant)
    FixedListView flvMerchant;
    @Bind(R.id.btn_gift)
    RoundTextView btnGift;
    @Bind(R.id.btn_buy)
    RoundTextView btnBuy;
    @Bind(R.id.btn_service)
    RoundTextView btnService;


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
        initListener();
        getNetworkData();
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
        if (merchant == null) {
            if (userId == SlashHelper.userManager().getUserId()) {
                btnFocus.setVisibility(View.GONE);
                llBottom.setVisibility(View.GONE);
            } else {
                llRight.setVisibility(View.VISIBLE);
                ivRight.setImageResource(R.mipmap.jubao_white_icon);
            }
        }

        btnContact.setWidth(DensityUtil.getWindowWidth(this) / 2 - DensityUtil.dip2px(this, 86));
        btnGift.setWidth(DensityUtil.getWindowWidth(this) / 2 - DensityUtil.dip2px(this, 86));
        taMerchantAdapter = new TaMerchantAdapter(mContext, listMerchant);
        flvMerchant.setAdapter(taMerchantAdapter);

    }

    private void initListener() {
        gvPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, AlbumActivity.class);
                intent.putExtra(AlbumActivity.INTENT_USERID, userId);
                startActivityForResult(intent, REQ_ALBUM);
            }
        });

        taMerchantAdapter.setOnBuyClickListener(new TaMerchantAdapter.OnBuyClickListener() {
            @Override
            public void onBuyClick(int position) {
                if (noLogin(mContext))
                    return;
                M_Merchant entity = listMerchant.get(position);
                Intent intent = new Intent(mContext, FindPayActivity.class);
                intent.putExtra(FindPayActivity.MERCHANT_INFO, entity);
                intent.putExtra(FindPayActivity.USER_INFO, userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
        taMerchantAdapter.setOnAllClickListener(new TaMerchantAdapter.OnAllClickListener() {
            @Override
            public void onAllClick(int position) {
                IntentUtil.intStart_activity(mActivity,
                        MerchantDetailActivity.class, new Pair<String, Integer>(MerchantDetailActivity.MERCHANT_ID, taMerchantAdapter.getItem(position).merchantId));
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
                    fillAdapter(listMerchant, false);
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
        // 头像
        if (!TextUtils.isEmpty(userInfo.getHead())) {
            imageManager.loadCircleImage(userInfo.getHead(), ivHead, "@120w_120h");
        }
        // 用户名
        if (!TextUtils.isEmpty(userInfo.getName()))
            tvName.setText(userInfo.getName());
        // 电话
/*        if (!TextUtils.isEmpty(userInfo.getPhoneNum())) {
            if (userInfo.getIsOpen() == 1)
                tvPhone.setText(userInfo.getPhoneNum());
            else
                tvPhone.setText(userInfo.getPhoneNum().substring(0, 3) + "****" + userInfo.getPhoneNum().substring(7, userInfo.getPhoneNum().length()));
        }*/
        // 电话
        tvPhone.setText(userInfo.getPhoneNum());
        if (!TextUtils.isEmpty(userInfo.getPhoneNum())) {
            if (userInfo.getIsOpen() == 1) {
                llPhone.setVisibility(View.VISIBLE);
            } else
                llPhone.setVisibility(View.GONE);
        }

        // 买单数
        tvBuyNum.setText(userInfo.saleNum + "人找TA买单");
        // 相册图片(多个用","分隔，最多显示3个)
        if (!TextUtils.isEmpty(userInfo.pics)) {
            PhotoFix3Adapter adapter = new PhotoFix3Adapter(mContext, userInfo.pics);
            adapter.setWidth(DensityUtil.dip2px(mContext, 50));
            adapter.setRule("@50p");
            gvPic.setAdapter(adapter);
        }
        // 是否已经关注(0:未关注1:已关注)
        if (userInfo.getIsFan() == 0) {
            btnFocus.setText(R.string.add_focus_yogouser);
        } else {
            btnFocus.setText(R.string.has_focus);
        }

        this.userInfo = userInfo;
        this.userInfo.setUserId(userId);
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

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_right, R.id.ll_right, R.id.tv_phone, R.id.ll_phote, R.id.btn_contact, R.id.btn_focus})
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
            case R.id.ll_phote:
                intent = new Intent(mContext, AlbumActivity.class);
                intent.putExtra(AlbumActivity.INTENT_USERID, userId);
                startActivityForResult(intent, REQ_ALBUM);
                break;
            case R.id.tv_phone:
                call();
                break;
            case R.id.btn_contact:
                if (noLogin(mContext))
                    return;
                Intent IMkitintent = getIMkit().getChattingActivityIntent(userId + "", LoginSampleHelper.APP_KEY);
                startActivity(IMkitintent);
                break;
            case R.id.btn_focus:
                focus_operate();
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
