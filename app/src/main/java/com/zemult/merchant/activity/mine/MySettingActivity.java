package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
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

import com.umeng.fb.FeedbackAgent;
import com.umeng.message.IUmengCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.common.UmengMessageDeviceConfig;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.AboutUsActivity;
import com.zemult.merchant.activity.IpSwitchActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.common.MMAlert;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

public class MySettingActivity extends BaseActivity {


    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.sc_liuliang)
    SwitchCompat scLiuliang;
    @Bind(R.id.ll_liuliang)
    RelativeLayout llLiuliang;
    @Bind(R.id.sc_xiaoxi_tixing)
    SwitchCompat scXiaoxiTixing;
    @Bind(R.id.ll_xiaoxi_tixing)
    RelativeLayout llXiaoxiTixing;
    @Bind(R.id.ll_invite_friend)
    RelativeLayout llInviteFriend;
    @Bind(R.id.iv_empty_cache_right)
    ImageView ivEmptyCacheRight;
    @Bind(R.id.ll_empty_cache)
    RelativeLayout llEmptyCache;
    @Bind(R.id.ll_fankui)
    RelativeLayout llFankui;
    @Bind(R.id.ll_help)
    RelativeLayout llHelp;
    @Bind(R.id.ll_about_us)
    RelativeLayout llAboutUs;
    @Bind(R.id.tv_cashsize)
    TextView tvcashsize;

    @Bind(R.id.ll_debug)
    RelativeLayout llDebug;
    private Context mContext;
    private PushAgent mPushAgent;
    int clickcount;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_setting);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

        getNetworkData();
    }

    private void initData() {
        mContext = this;

        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String downloadDirectoryPath = Environment.getExternalStorageDirectory().getPath() + "/" + Constants.ImageLoad_CACHE_DIR;
                return AppUtils.getFormatSize(AppUtils.getFolderSize(new File(downloadDirectoryPath)));
            }

            @Override
            protected void onPostExecute(String s) {
                tvcashsize.setText(s);

            }
        }.execute();


    }


    private void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("设置");
        if (SlashHelper.getSettingBoolean(SlashHelper.APP.Key.UMENMGPUSH, true)) {
            scXiaoxiTixing.setChecked(true);
        } else {
            scXiaoxiTixing.setChecked(false);
        }

        if (imageManager.getImageSize().equals("@50p")) {
            scLiuliang.setChecked(true);
        } else {
            scLiuliang.setChecked(false);
        }
    }

    private void initListener() {
    }

    /**
     * 访问网络接口
     */
    private void getNetworkData() {

    }

    public class ClearImageCashThead implements Runnable {
        public void run() {
            imageManager.clearDiskImageCash();
        }
    }

    //推送开关
    private void switchPush() {
        mPushAgent = PushAgent.getInstance(this);
        if (scXiaoxiTixing.isChecked()) {
            scXiaoxiTixing.setClickable(false);
            //开启推送并设置注销的回调处理
            mPushAgent.enable(new IUmengCallback() {
                @Override
                public void onSuccess() {
                    SlashHelper.setSettingBoolean(SlashHelper.APP.Key.UMENMGPUSH, true);
                    if (!TextUtils.isEmpty(mPushAgent.getRegistrationId())) {
                        SlashHelper.deviceManager().setUmengDeviceToken(mPushAgent.getRegistrationId());
                    }
                }

                @Override
                public void onFailure(String s, String s1) {
                    ToastUtil.showMessage("开启推送失败");
                    scXiaoxiTixing.setChecked(false);
                }
            });

        } else {
            //关闭推送并设置注册的回调处理
            mPushAgent.disable(new IUmengCallback() {
                @Override
                public void onSuccess() {
                    SlashHelper.setSettingBoolean(SlashHelper.APP.Key.UMENMGPUSH, false);
                }

                @Override
                public void onFailure(String s, String s1) {
                    ToastUtil.showMessage("关闭推送失败");
                    scXiaoxiTixing.setChecked(true);

                }
            });

        }
    }


    private void updateStatus() {
        String info = String.format("enabled:%s\nisRegistered:%s\nDeviceToken:%s\n" +
                        "SdkVersion:%s\nAppVersionCode:%s\nAppVersionName:%s",
                mPushAgent.getRegistrationId(), MsgConstant.SDK_VERSION,
                UmengMessageDeviceConfig.getAppVersionCode(this), UmengMessageDeviceConfig.getAppVersionName(this));
        Log.i(getClass().getName(), "switch Push:" + info);
        SlashHelper.setSettingBoolean(SlashHelper.APP.Key.UMENMGPUSH, mPushAgent.isPushCheck());
        scXiaoxiTixing.setClickable(true);
        if (!TextUtils.isEmpty(mPushAgent.getRegistrationId())) {
            SlashHelper.deviceManager().setUmengDeviceToken(mPushAgent.getRegistrationId());
        }
    }


    @OnClick({R.id.ll_back, R.id.sc_liuliang, R.id.ll_debug,
            R.id.sc_xiaoxi_tixing, R.id.ll_invite_friend, R.id.ll_empty_cache,
            R.id.ll_fankui, R.id.ll_help, R.id.ll_about_us, R.id.lh_btn_back, R.id.ll_black})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.sc_liuliang:
                if (scLiuliang.isChecked()) {
                    ToastUtil.showMessage("切换到小图模式");
                    imageManager.setImageSize("@50p");
                } else {
                    ToastUtil.showMessage("切换到大图模式");
                    imageManager.setImageSize("@100p");

                }
                break;
            case R.id.sc_xiaoxi_tixing:
                switchPush();
                break;
            case R.id.ll_invite_friend:
                startActivity(new Intent(this, InviteFriendActivity.class));

                break;
            case R.id.ll_empty_cache:
                MMAlert.deleteCashDialog(MySettingActivity.this, new MMAlert.DeleteCallback() {
                    @Override
                    public void OnDelete() {
                        ClearImageCashThead clearImageCashThead = new ClearImageCashThead();
                        Thread thread = new Thread(clearImageCashThead);
                        thread.start();
                        ToastUtil.showMessage("清除成功");
                        tvcashsize.setText("0M");
                    }
                });
                break;
            case R.id.ll_fankui:
                FeedbackAgent feedbackAgent = new FeedbackAgent(MySettingActivity.this);
                feedbackAgent.startFeedbackActivity();
                break;
            case R.id.ll_help:
                IntentUtil.start_activity(MySettingActivity.this, BaseWebViewActivity.class, new Pair<String, String>("titlename", getString(R.string.help)), new Pair<String, String>("url", Constants.URL_HELP));
                break;
            case R.id.ll_about_us:
                IntentUtil.start_activity(MySettingActivity.this, AboutUsActivity.class);
                break;
            case R.id.ll_debug:
                clickcount++;
                if (clickcount == 11) {
                    clickcount = 0;
                    IntentUtil.start_activity(MySettingActivity.this, IpSwitchActivity.class);
                }
                break;
            case R.id.ll_black:
                IntentUtil.start_activity(MySettingActivity.this, BlackListActivity.class);
                break;
        }
    }
}
