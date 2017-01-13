package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zemult.merchant.R;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MySexActivity extends MBaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.male_iv)
    ImageView maleIv;
    @Bind(R.id.male_rl)
    RelativeLayout maleRl;
    @Bind(R.id.female_iv)
    ImageView femaleIv;
    @Bind(R.id.female_rl)
    RelativeLayout femaleRl;

    int sex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sex);
        ButterKnife.bind(this);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("性别");
        sex = SlashHelper.userManager().getUserinfo().getSex();
        if(sex==0){
            maleIv.setVisibility(View.VISIBLE);
            femaleIv.setVisibility(View.GONE);
        }else if(sex==1){
            femaleIv.setVisibility(View.VISIBLE);
            maleIv.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.ll_back,R.id.lh_btn_back, R.id.lh_btn_right, R.id.male_rl, R.id.female_rl})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.male_rl:
                maleIv.setVisibility(View.VISIBLE);
                femaleIv.setVisibility(View.GONE);
                sex = 0;
                go(sex);


                break;

            case R.id.female_rl:
                femaleIv.setVisibility(View.VISIBLE);
                maleIv.setVisibility(View.GONE);
                sex = 1;

                go(sex);

                break;
            case R.id.lh_btn_back:
            case R.id.ll_back:
                MySexActivity.this.finish();
                break;


        }
    }

    public void go(int sex) {

        SlashHelper.userManager().getUserinfo().setSex(sex);
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("sex_result", sex);
        //设置返回数据
        MySexActivity.this.setResult(RESULT_OK, intent);
        Toast.makeText(MySexActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
        MySexActivity.this.finish();


    }






}

