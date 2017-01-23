package com.zemult.merchant.activity.slash;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Present;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Wikison on 2017/1/22.
 */

public class SendPresentSuccessActivity extends BaseActivity {


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
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_present)
    ImageView ivPresent;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.btn_confirm)
    Button btnConfirm;

    M_Present m_present;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_send_present_success);

    }

    @Override
    public void init() {
        lhTvTitle.setText("赞赏礼物");
        m_present = (M_Present) getIntent().getSerializableExtra(SendPresentActivity.PRESENT);

        // 礼物名称
        if (!TextUtils.isEmpty(m_present.name)) {
            // 礼物图片
            if (m_present.name.contains("兰博基尼")) {
                tvTitle.setText("您已成功赠送" + "一辆豪车!");
                ivPresent.setImageResource(R.mipmap.che_icon);

            } else if (m_present.name.contains("钻戒")) {
                tvTitle.setText("您已成功赠送" + "一枚钻戒!");
                ivPresent.setImageResource(R.mipmap.zuanjie_icon);

            } else if (m_present.name.contains("钱包")) {
                tvTitle.setText("您已成功赠送" + "一个钱包!");
                ivPresent.setImageResource(R.mipmap.qianbao_icon);

            } else if (m_present.name.contains("花")) {
                tvTitle.setText("您已成功赠送" + "一束鲜花!");
                ivPresent.setImageResource(R.mipmap.hua_icon);
            } else {
                tvTitle.setText("您已成功赠送" + "666!");
                ivPresent.setImageResource(R.mipmap.liu_cion);
            }
        }

        tvName.setText(m_present.name + " ￥" + m_present.price);

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_next:
                this.finish();
                break;


        }
    }

}
