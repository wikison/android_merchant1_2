package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.HeadManageActivity;
import com.zemult.merchant.activity.MainActivity;
import com.zemult.merchant.adapter.slashfrgment.PhotoFix3Adapter;
import com.zemult.merchant.aip.common.UserLogoutRequest;
import com.zemult.merchant.aip.mine.UserEditinfoRequest;
import com.zemult.merchant.app.AppApplication;
import com.zemult.merchant.app.MAppCompatActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.FixedGridView;
import com.zemult.merchant.view.common.CommonDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;

public class MyinfoSetActivity extends MAppCompatActivity {

    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
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
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.mis_headgo_layout)
    RelativeLayout misHeadgoLayout;
    @Bind(R.id.textView1)
    TextView textView1;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.mis_name_layout)
    RelativeLayout misNameLayout;
    @Bind(R.id.textView2)
    TextView textView2;
    @Bind(R.id.tv_sex)
    TextView tvSex;
    @Bind(R.id.mis_sex_layout)
    RelativeLayout misSexLayout;
    @Bind(R.id.textView6)
    TextView textView6;
    @Bind(R.id.mis_mqr_layout)
    RelativeLayout misMqrLayout;
    @Bind(R.id.textView66)
    TextView textView66;
    @Bind(R.id.tv_my_phone)
    TextView tvMyPhone;
    @Bind(R.id.rl_my_phone)
    RelativeLayout rlMyPhone;
    @Bind(R.id.sc_public)
    SwitchCompat scPublic;
    @Bind(R.id.textView3)
    TextView textView3;
    @Bind(R.id.tv_area)
    TextView tvArea;
    @Bind(R.id.mis_area_layout)
    RelativeLayout misAreaLayout;
    @Bind(R.id.gv_pic)
    FixedGridView gvPic;
    @Bind(R.id.ll_my_photo)
    LinearLayout llMyPhoto;
    @Bind(R.id.textView4)
    TextView textView4;
    @Bind(R.id.perintro_layout)
    RelativeLayout perintroLayout;
    @Bind(R.id.mis_bt)
    Button misBt;


    UserEditinfoRequest userEditinfoRequest;
    String nameString, headString, accountString, companyString, positionString, provinceString, cityString, picString;//areaString
    int sexInt, isOpenInt;
    String provinceName = "", cityName = "", areaName = "", note = "", audio = "", audioTime = "";
    private YWIMKit mIMKit;

    UserLogoutRequest userLogoutRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo_set);
        ButterKnife.bind(this);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("个人信息");
        initData();
        //注册修改头像广播
        registerReceiver(new String[]{Constants.BROCAST_EDITUSERINFO});

        mIMKit = LoginSampleHelper.getInstance().getIMKit();
        if (mIMKit == null) {
            return;
        }
    }

    private void initData() {
        tvMyPhone.setText(SlashHelper.userManager().getUserinfo().getPhoneNum());
        tvName.setText(SlashHelper.userManager().getUserinfo().getName());
        tvSex.setText(SlashHelper.userManager().getUserinfo().getSex() == 0 ? "男" : "女");
        if (SlashHelper.userManager().getUserinfo().getProvinceName().equals(SlashHelper.userManager().getUserinfo().getCityName())) {
            tvArea.setText(SlashHelper.userManager().getUserinfo().getProvinceName());
        } else {
            tvArea.setText(SlashHelper.userManager().getUserinfo().getProvinceName() + " " + SlashHelper.userManager().getUserinfo().getCityName());
        }

        imageManager.loadCircleHead(SlashHelper.userManager().getUserinfo().getHead(), ivHead);

        nameString = SlashHelper.userManager().getUserinfo().getName();
        headString = SlashHelper.userManager().getUserinfo().getHead();
        accountString = SlashHelper.userManager().getUserinfo().getAccount();
        companyString = SlashHelper.userManager().getUserinfo().getCompany();
        positionString = SlashHelper.userManager().getUserinfo().getPosition();
        provinceString = SlashHelper.userManager().getUserinfo().getProvince();
        cityString = SlashHelper.userManager().getUserinfo().getCity();
//        areaString = SlashHelper.userManager().getUserinfo().getArea();
        sexInt = SlashHelper.userManager().getUserinfo().getSex();
        isOpenInt = SlashHelper.userManager().getUserinfo().getIsOpen();
        if (isOpenInt == 0) {
            scPublic.setChecked(false);
        } else if (isOpenInt == 1) {
            scPublic.setChecked(true);
        }
        picString = SlashHelper.userManager().getUserinfo().pics;
        // 相册图片(多个用","分隔，最多显示3个)
        if (!TextUtils.isEmpty(picString)) {
            PhotoFix3Adapter adapter = new PhotoFix3Adapter(this, picString);
            adapter.setWidth(DensityUtil.dip2px(this, 70));
            adapter.setRule("@50p");
            gvPic.setAdapter(adapter);
        }
        provinceName = SlashHelper.userManager().getUserinfo().getProvinceName();
        cityName = SlashHelper.userManager().getUserinfo().getCityName();
        note = SlashHelper.userManager().getUserinfo().getNote();
    }

    @OnClick({R.id.ll_back, R.id.lh_btn_back, R.id.mis_headgo_layout, R.id.mis_name_layout, R.id.mis_sex_layout, R.id.perintro_layout, R.id.mis_area_layout, R.id.mis_mqr_layout, R.id.mis_bt, R.id.ll_my_photo})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.mis_headgo_layout:
                //头像
                startActivityForResult(new Intent(MyinfoSetActivity.this, HeadManageActivity.class), 110);
                break;
            case R.id.mis_name_layout:
                //名字
                intent = new Intent(MyinfoSetActivity.this, NicknameActivity.class);
                startActivityForResult(intent, Constants.REQUESTCODE_CHANGENICKNAME);
                break;
            case R.id.mis_sex_layout:
                //性别
                startActivityForResult(new Intent(MyinfoSetActivity.this, MySexActivity.class), Constants.REQUESTCODE_CHANGESEX);
                break;
            case R.id.perintro_layout:
                //个人介绍
                startActivityForResult(new Intent(MyinfoSetActivity.this, MyInfoActivity.class), Constants.REQUESTCODE_MYINFO);
                break;
            case R.id.mis_area_layout:
                //地区
                IntentUtil.start_activity(MyinfoSetActivity.this, MyAreaActivity.class, new Pair<String, String>("requesttype", Constants.BROCAST_EDITUSERINFO));
                break;
            case R.id.mis_mqr_layout:
                //我的二维码
                intent = new Intent(MyinfoSetActivity.this, MyQrActivity.class);
                intent.putExtra("from", "MyInfoSet");
                startActivity(intent);
                break;
            case R.id.mis_bt:

                CommonDialog.showDialogListener(MyinfoSetActivity.this, null, "否", "是", getResources().getString(R.string.login_out_tip), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                        //退出用户登录
                        logout();
                    }
                });
                break;

            case R.id.ll_my_photo:
                intent = new Intent(MyinfoSetActivity.this, AlbumActivity.class);
                intent.putExtra(AlbumActivity.INTENT_USERID, SlashHelper.userManager().getUserId());
                startActivity(intent);
                break;


        }
    }

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_EDITUSERINFO.equals(intent.getAction())) {
            provinceName = intent.getStringExtra("provinceName");
            cityName = intent.getStringExtra("cityName");
            provinceString = intent.getStringExtra("province");
            cityString = intent.getStringExtra("city");
            user_editinfo();
//            if(provinceName.equals(cityName)){
//                tvArea.setText(provinceName);
//            }
//            else{
//                tvArea.setText(provinceName+" "+cityName);
//            }
        }
    }

    //用户退出登录
    private void logout() {

        if (userLogoutRequest != null) {
            userLogoutRequest.cancel();
        }
        UserLogoutRequest.Input input = new UserLogoutRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.device_token = SlashHelper.deviceManager().getUmengDeviceToken();
        input.convertJosn();
        userLogoutRequest = new UserLogoutRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
//                    SlashHelper.userManager().getUserinfo();
                    SlashHelper.userManager().saveUserinfo(null);
                    ImLogout();
                    ToastUtils.show(MyinfoSetActivity.this, "退出成功");
                    EventBus.getDefault().post("exit");
                    finish();
                }
            }
        });
        sendJsonRequest(userLogoutRequest);
    }

    public void ImLogout() {
        // openIM SDK提供的登录服务
        IYWLoginService mLoginService = mIMKit.getLoginService();

        mLoginService.logout(new IWxCallback() {
            //此时logout已关闭所有基于IMBaseActivity的OpenIM相关Actiivity，s
            @Override
            public void onSuccess(Object... arg0) {
                YWLog.i("------IM_LOGOUT---------", "退出成功");
                String account = YWAPI.getCurrentUser();
                mIMKit = YWAPI.getIMKitInstance(account);
                LoginSampleHelper.getInstance().setIMKit(mIMKit);
            }

            @Override
            public void onProgress(int arg0) {

            }

            @Override
            public void onError(int arg0, String arg1) {
                Toast.makeText(AppApplication.getContext(), "退出失败,请重新登录", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //修改用户资料信息
    private void user_editinfo() {
        showPd();
        if (userEditinfoRequest != null) {
            userEditinfoRequest.cancel();
        }
        UserEditinfoRequest.Input input = new UserEditinfoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
            input.name = nameString;
            input.head = headString;
            input.sex = sexInt;
            input.isOpen = isOpenInt;
            input.convertJosn();
        }

        userEditinfoRequest = new UserEditinfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    SlashHelper.userManager().getUserinfo().setName(nameString);
                    SlashHelper.userManager().getUserinfo().setHead(headString);
                    SlashHelper.userManager().getUserinfo().setAccount(accountString);
                    SlashHelper.userManager().getUserinfo().setCompany(companyString);
                    SlashHelper.userManager().getUserinfo().setPosition(positionString);
                    SlashHelper.userManager().getUserinfo().setProvince(provinceString);
                    SlashHelper.userManager().getUserinfo().setCity(cityString);
//                    SlashHelper.userManager().getUserinfo().setArea(areaString);
                    SlashHelper.userManager().getUserinfo().setSex(sexInt);
                    SlashHelper.userManager().getUserinfo().setIsOpen(isOpenInt);

                    SlashHelper.userManager().getUserinfo().setCityName(cityName);
                    SlashHelper.userManager().getUserinfo().setProvinceName(provinceName);
                    SlashHelper.userManager().getUserinfo().setAreaName(areaName);

                    SlashHelper.userManager().getUserinfo().setAudio(audio);
                    SlashHelper.userManager().getUserinfo().setAudioTime(audioTime);
                    SlashHelper.userManager().getUserinfo().setNote(note);

                    SlashHelper.userManager().saveUserinfo(SlashHelper.userManager().getUserinfo());
                    initData();

                } else {
                    ToastUtils.show(MyinfoSetActivity.this, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userEditinfoRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUESTCODE_CHANGENICKNAME && resultCode == RESULT_OK) {
            nameString = SlashHelper.userManager().getUserinfo().getName();
            //修改用户资料信息
            tvName.setText(SlashHelper.userManager().getUserinfo().getName());
           user_editinfo();
        } else if (requestCode == Constants.REQUESTCODE_CHANGESEX && resultCode == RESULT_OK) {
            sexInt = data.getExtras().getInt("sex_result");
            user_editinfo();
        } else if (requestCode == Constants.REQUESTCODE_MYINFO && resultCode == RESULT_OK) {
            note = data.getExtras().getString("note");
            user_editinfo();
        } else if (requestCode == 110 && resultCode == RESULT_OK) {
            headString = SlashHelper.userManager().getUserinfo().getHead();
            imageManager.loadCircleHead(headString, ivHead);
        }
        Intent in = new Intent(Constants.BROCAST_LOGIN);
        sendBroadcast(in);

    }

    @Override
    public void onBackPressed() {
        int isPublic = scPublic.isChecked() ? 1 : 0;
        if (isPublic != isOpenInt) {
            isOpenInt = isPublic;
            SlashHelper.userManager().getUserinfo().setIsOpen(isOpenInt);
            user_editinfo();
        }
        super.onBackPressed();
    }


}
