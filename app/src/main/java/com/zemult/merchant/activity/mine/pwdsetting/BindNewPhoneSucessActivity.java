package com.zemult.merchant.activity.mine.pwdsetting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.city.utils.StringUtils;
import com.zemult.merchant.app.AppApplication;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class BindNewPhoneSucessActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.tv_phone)
    TextView tvPhone;

    private YWIMKit mIMKit;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bind_new_phone_sucess);
    }

    @Override
    public void init() {
        lhTvTitle.setText("更换绑定手机号码");
        mIMKit = LoginSampleHelper.getInstance().getIMKit();
        if (mIMKit == null) {
            return;
        }
        String phone = SlashHelper.userManager().getUserinfo().getPhoneNum();
        if(!TextUtils.isEmpty(phone.trim()))
            tvPhone.setText("手机号  " + phone.substring(0,3) + "*****" + phone.substring(phone.length()-3,phone.length()));
    }

    public void ImLogout() {
        // openIM SDK提供的登录服务
        IYWLoginService mLoginService = mIMKit.getLoginService();
        mLoginService.logout(new IWxCallback() {
            //此时logout已关闭所有基于IMBaseActivity的OpenIM相关Actiivity，s
            @Override
            public void onSuccess(Object... arg0) {
                YWLog.i("------IM_LOGOUT---------", "退出成功");
            }

            @Override
            public void onProgress(int arg0) {

            }

            @Override
            public void onError(int arg0, String arg1) {
                Toast.makeText(AppApplication.getContext(), "请重新登录", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
            case R.id.btn_ok:
                ImLogout();
                SlashHelper.userManager().getUserinfo();
                SlashHelper.userManager().saveUserinfo(null);
                EventBus.getDefault().post("exit");
                setResult(RESULT_OK);
                onBackPressed();
                break;
        }
    }
}
