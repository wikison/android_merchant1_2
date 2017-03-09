package com.zemult.merchant.activity.mine.pwdsetting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.LoginActivity;
import com.zemult.merchant.activity.mine.BindBankCardActivity;
import com.zemult.merchant.activity.mine.TrueNameActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.common.CommonDialog;

import butterknife.Bind;
import butterknife.OnClick;

public class GotoTurenameActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.btn_gotoauth)
    Button btnGotoauth;

    private Context context;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_goto_turename);
    }

    @Override
    public void init() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lhTvTitle.setText("更换绑定手机号码");
        context = this;
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_gotoauth})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_gotoauth:

                // 没有登录跳转到登录界面
                CommonDialog.showDialogListener(context, null, "取消", "去绑定", "请先绑定银行卡进行实名认证", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                        startActivity(new Intent(context, LoginActivity.class));
                    }
                });

                Intent intent =new Intent(GotoTurenameActivity.this,BindBankCardActivity.class);
                startActivityForResult(intent, 0x110);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0x110 && SlashHelper.userManager().getUserinfo().getIsConfirm() == 1){
            Intent intent = new Intent(GotoTurenameActivity.this, IdnoAuthActivity.class);
            startActivity(intent);
        }
    }
}
