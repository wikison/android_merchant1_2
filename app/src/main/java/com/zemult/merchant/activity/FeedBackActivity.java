package com.zemult.merchant.activity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bugtags.library.Bugtags;
import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedBackActivity extends BaseActivity {


    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.sc_yjfk)
    SwitchCompat scYjfk;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_feed_back);
    }

    @Override
    public void init() {

        lhTvTitle.setText("意见反馈");
        if (SlashHelper.getSettingBoolean(SlashHelper.APP.Key.BUGTAGS, false)) {
            scYjfk.setChecked(true);
        } else {
            scYjfk.setChecked(false);
        }
        scYjfk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SlashHelper.setSettingBoolean(SlashHelper.APP.Key.BUGTAGS, true);
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventShake);
                } else {
                    SlashHelper.setSettingBoolean(SlashHelper.APP.Key.BUGTAGS, false);
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventNone);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
        }
    }
}
