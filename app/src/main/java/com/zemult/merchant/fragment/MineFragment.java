package com.zemult.merchant.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.BusinessManActivity;
import com.zemult.merchant.activity.mine.IamYuekeActivity;
import com.zemult.merchant.activity.mine.MerchantEnter2Activity;
import com.zemult.merchant.activity.mine.MyAppointmentActivity;
import com.zemult.merchant.activity.mine.MyCardsActivity;
import com.zemult.merchant.activity.mine.MyOrderActivity;
import com.zemult.merchant.activity.mine.MySettingActivity;
import com.zemult.merchant.activity.mine.MyWalletActivity;
import com.zemult.merchant.activity.mine.MyinfoSetActivity;
import com.zemult.merchant.activity.mine.SafeSettingActivity;
import com.zemult.merchant.activity.mine.ServiceHistoryActivity;
import com.zemult.merchant.activity.search.LabelHomeActivity;
import com.zemult.merchant.aip.mine.UserEditStateRequest;
import com.zemult.merchant.aip.mine.UserInfoOwnerRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.UserManager;
import com.zemult.merchant.view.common.MMAlert;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/6/3.
 */
public class MineFragment extends BaseFragment {

    @Bind(R.id.mhead_iv)
    ImageView mheadIv;
    @Bind(R.id.myname_tv)
    TextView mynameTv;
    @Bind(R.id.mgrade_tv)
    TextView mgradeTv;
    @Bind(R.id.iv_set)
    ImageView ivSet;
    @Bind(R.id.mygo_layout)
    RelativeLayout mygoLayout;

    @Bind(R.id.iv_my_order_right)
    ImageView ivMyOrderRight;
    @Bind(R.id.rl_my_order)
    RelativeLayout rlMyOrder;

    @Bind(R.id.tv_my_account)
    TextView tvMyAccount;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.rl_wallet)
    RelativeLayout rlWallet;
    @Bind(R.id.rl_record)
    RelativeLayout rlRecord;
    @Bind(R.id.mtag_layout)
    RelativeLayout mtagLayout;

    @Bind(R.id.rl_sale_manage)
    RelativeLayout rlSaleManage;
    @Bind(R.id.msafe_layout)
    RelativeLayout msafeLayout;
    @Bind(R.id.mshop_layout)
    RelativeLayout mshopLayout;


    UserInfoOwnerRequest userInfoOwnerRequest;

    protected ImageManager mImageManager;

    int isSetPaypwd, isConfirm;
    double mymoney;
    String myname, head;
    @Bind(R.id.rl_my_prorder)
    RelativeLayout rlMyProrder;
    @Bind(R.id.rl_my_gift)
    RelativeLayout rlMyGift;
    @Bind(R.id.level_iv)
    ImageView levelIv;
    @Bind(R.id.level_tv)
    TextView levelTv;
    double experience;
    @Bind(R.id.iv1)
    ImageView iv1;
    @Bind(R.id.state_iv)
    ImageView stateIv;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.state_rl)
    RelativeLayout stateRl;
    @Bind(R.id.incomeaccount)
    TextView incomeaccount;
    @Bind(R.id.servicerecord)
    TextView servicerecord;
    @Bind(R.id.applyfor_tv)
    TextView applyforTv;
    @Bind(R.id.fuwuguanjia_ll)
    LinearLayout fuwuguanjiaLl;

    private boolean hasStarted = false;
    int state;

    @Override
    public void onResume() {
        super.onResume();
        get_user_info_owner_request();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            get_user_info_owner_request();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageManager = new ImageManager(getActivity());
        registerReceiver(new String[]{Constants.BROCAST_UPDATEMYINFO});
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.rl_record, R.id.mtag_layout,
            R.id.rl_wallet, R.id.mygo_layout, R.id.rl_my_order, R.id.rl_sale_manage,
            R.id.mshop_layout, R.id.iv_set, R.id.msafe_layout,
            R.id.mhead_iv, R.id.rl_my_prorder, R.id.rl_my_gift, R.id.state_rl, R.id.incomeaccount, R.id.servicerecord})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            /*case R.id.concern_rl:
                intent = new Intent(getActivity(), MyFollowActivity.class);
                startActivity(intent);
                break;
            case R.id.fans_rl:
                intent = new Intent(getActivity(), FamiliarPeopleActivity.class);
                startActivity(intent);
                break;*/
            case R.id.mtag_layout:
                intent = new Intent(getActivity(), LabelHomeActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_my_order:
                intent = new Intent(getActivity(), MyOrderActivity.class);
                intent.putExtra("page_position", 0);
                startActivity(intent);
                break;
            //我是约客
            case R.id.rl_sale_manage:
                intent = new Intent(getActivity(), IamYuekeActivity.class);
                startActivity(intent);
                break;
            case R.id.mhead_iv:
//                Intent user_it = new Intent(getActivity(), UserDetailActivity.class);
//                user_it.putExtra(UserDetailActivity.USER_ID, SlashHelper.userManager().getUserId());
//                startActivity(user_it);
//                break;
            case R.id.mygo_layout:
                intent = new Intent(getActivity(), MyinfoSetActivity.class);
                startActivity(intent);
                break;
//            case R.id.mcollect_layout:
//                startActivity(new Intent(getActivity(), MyCollectionActivity.class));
//                break;
            case R.id.mshop_layout:
                //我是商家
                if(SlashHelper.userManager().getUserinfo().getManagerUserNum()==0){
                    startActivity(new Intent(getActivity(), MerchantEnter2Activity.class));
                }else{
                    startActivity(new Intent(getActivity(), BusinessManActivity.class));
                }

                break;
            case R.id.iv_set:
                startActivity(new Intent(getActivity(), MySettingActivity.class));
                break;
            case R.id.msafe_layout:
                startActivity(new Intent(getActivity(), SafeSettingActivity.class));
                break;
            case R.id.rl_record:
                //我的券包
//                Intent intent_record = new Intent(getActivity(), AllRecordActivity.class);
//                intent_record.putExtra("userId", SlashHelper.userManager().getUserId());
//                startActivity(intent_record);
                startActivity(new Intent(getActivity(), MyCardsActivity.class));
                break;
//            case R.id.chatdetail:
//                    Intent mnews = new Intent(getActivity(), DoTaskVoteActivity.class);//DoTaskVoiceActivity
//                    startActivity(mnews);
//                break;
            case R.id.rl_wallet:
                Intent intent_wallet = new Intent(getActivity(), MyWalletActivity.class);
                intent_wallet.putExtra("isSetPaypwd", isSetPaypwd);
                intent_wallet.putExtra("isConfirm", isConfirm);
                intent_wallet.putExtra("mymoney", mymoney);
                startActivity(intent_wallet);
                break;
            case R.id.rl_my_prorder:
                startActivity(new Intent(getActivity(), MyAppointmentActivity.class));
                //我的预约
                break;
            case R.id.rl_my_gift:
                //我的礼物箱
                break;
            case R.id.state_rl:
                MMAlert.showChooseStateDialog(getActivity(), new MMAlert.ChooseCallback() {
                    @Override
                    public void onfirstChoose() {
                        state = 0;
                        userEditState();
                    }

                    @Override
                    public void onsecondChoose() {
                        state = 1;
                        userEditState();
                    }

                    @Override
                    public void onthirdChoose() {
                        state = 2;
                        userEditState();
                    }
                });

                break;
            case R.id.incomeaccount:
                //收益账户
                startActivity(new Intent(getActivity(), MyWalletActivity.class));

                break;
            case R.id.servicerecord:
                //服务记录
                startActivity(new Intent(getActivity(), ServiceHistoryActivity.class));
                break;

        }
    }


    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_UPDATEMYINFO.equals(intent.getAction())) {
            get_user_info_owner_request();
        }
    }


    //获取用户自身的资料（包含关注数/粉丝数）
    private void get_user_info_owner_request() {
        if (userInfoOwnerRequest != null) {
            userInfoOwnerRequest.cancel();
        }
          showPd();
        UserInfoOwnerRequest.Input input = new UserInfoOwnerRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId() + "";
            input.convertJosn();
        }

        userInfoOwnerRequest = new UserInfoOwnerRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserLogin) response).status == 1) {

                    if (((APIM_UserLogin) response).userInfo != null) {
                        ((APIM_UserLogin) response).userInfo.setUserId(SlashHelper.userManager().getUserId());
                        ((APIM_UserLogin) response).userInfo.setPassword(SlashHelper.userManager().getUserinfo().getPassword());
                        UserManager.instance().saveUserinfo(((APIM_UserLogin) response).userInfo);
                        mynameTv.setText(((APIM_UserLogin) response).userInfo.getName());
                        mgradeTv.setText(((APIM_UserLogin) response).userInfo.getProvinceName() + "    " + ((APIM_UserLogin) response).userInfo.getCityName());//用户省份+城市
                        mImageManager.loadCircleHead(((APIM_UserLogin) response).userInfo.getHead(), mheadIv);

                        isConfirm = ((APIM_UserLogin) response).userInfo.isConfirm;
                        myname = ((APIM_UserLogin) response).userInfo.getName();
                        head = ((APIM_UserLogin) response).userInfo.getHead();
                        isSetPaypwd = ((APIM_UserLogin) response).userInfo.isSetPaypwd;
                        mymoney = ((APIM_UserLogin) response).userInfo.money;
                        tvMyAccount.setText(mymoney + "元");

                        levelTv.setText(SlashHelper.userManager().getUserinfo().getExperienceText() + "服务管家");
                        experience = SlashHelper.userManager().getUserinfo().getExperience();
                        if (experience < 100) {
                            levelIv.setBackgroundResource(R.mipmap.xinshou_iconsj);
                        } else if (experience >= 100 && experience < 10000) {
                            levelIv.setBackgroundResource(R.mipmap.tong_iconsj);
                        } else if (experience >= 10000 && experience < 100000) {
                            levelIv.setBackgroundResource(R.mipmap.yin_iconsj);
                        } else if (experience >= 100000 && experience < 1000000) {
                            levelIv.setBackgroundResource(R.mipmap.jin_iconsj);
                        } else {
                            levelIv.setBackgroundResource(R.mipmap.demon_iconsj);
                        }

                        if(((APIM_UserLogin) response).userInfo.getIsSaleUser()>0){
                            fuwuguanjiaLl.setVisibility(View.VISIBLE);
                        }else{
                            fuwuguanjiaLl.setVisibility(View.GONE);
                        }
                        if(((APIM_UserLogin) response).userInfo.getManagerUserNum()==0){

                            applyforTv.setText("申请商户入驻");
                            mshopLayout.setVisibility(View.GONE);
                        }else{
                            mshopLayout.setVisibility(View.VISIBLE);
                            applyforTv.setText("我是商户");
                        }

                        state = ((APIM_UserLogin) response).userInfo.state;
                        ondeal(state);
                        SlashHelper.setSettingString(((APIM_UserLogin) response).userInfo.getPhoneNum(), ((APIM_UserLogin) response).userInfo.getHead());
                    }
                } else {
                    ToastUtils.show(getActivity(), ((APIM_UserLogin) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userInfoOwnerRequest);
    }


    UserEditStateRequest userEditStateRequest;

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
                    ondeal(state);
                } else {
                    ToastUtils.show(getActivity(), ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userEditStateRequest);
    }

    //处理状态
    private void ondeal(int state) {
        if (state == 0) {
            stateIv.setBackgroundResource(R.mipmap.kongxian_icon);
            tvState.setText("空闲");
            tvState.setTextColor(getResources().getColor(R.color.font_idle));
        } else if (state == 1) {
            stateIv.setBackgroundResource(R.mipmap.xiuxi_icon);
            tvState.setText("休息");
            tvState.setTextColor(getResources().getColor(R.color.font_black_999));
        } else if (state == 2) {
            stateIv.setBackgroundResource(R.mipmap.manglu_icon);
            tvState.setText("忙碌");
            tvState.setTextColor(getResources().getColor(R.color.font_busy));
        }

    }


    @Override
    protected void lazyLoad() {

    }


}
