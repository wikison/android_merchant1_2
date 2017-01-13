package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zemult.merchant.R;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NicknameActivity extends MBaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.niname_et)
    EditText ninameEt;
    @Bind(R.id.ll_back)
    LinearLayout llBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);
        ButterKnife.bind(this);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("昵称");
        lhBtnRight.setVisibility(View.VISIBLE);

        lhBtnRight.setText("保存");
        lhBtnRight.setTextSize(16);
        lhBtnRight.setTextColor(getResources().getColor(R.color.font_black_999));
        lhBtnRight.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ninameEt.setText(SlashHelper.userManager().getUserinfo().getName());

        EditFilter.WordFilter(ninameEt, 11);

    }


    @OnClick({R.id.ll_back, R.id.lh_btn_right, R.id.lh_btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:  //返回按钮
            case R.id.lh_btn_back:
                NicknameActivity.this.finish();

                break;
            case R.id.lh_btn_right:   //点击保存按钮

                if (TextUtils.isEmpty(ninameEt.getText().toString())) {
                    Toast.makeText(NicknameActivity.this, "请输入您的昵称", Toast.LENGTH_SHORT).show();
                } else {

                    SlashHelper.userManager().getUserinfo().setName(ninameEt.getText().toString());

                    Intent intent = new Intent();
                    //把返回数据存入Intent
                    intent.putExtra("result", ninameEt.getText().toString());
                    //设置返回数据
                    NicknameActivity.this.setResult(RESULT_OK, intent);
//                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    NicknameActivity.this.finish();
                }
                break;
        }
    }

}
