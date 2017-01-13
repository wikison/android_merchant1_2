package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zemult.merchant.R;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SwitchView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkExperActivity extends MBaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.wx_comname_et)
    EditText wxComnameEt;             //公司名称
    @Bind(R.id.wx_posname_et)
    EditText wxPosnameEt;             //职位名称

    @Bind(R.id.ispublic_sv)             //是否公开
    SwitchView ispublicSv;

    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.ll_back)
    LinearLayout llBack;

    int isOpenInt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_exper);
        ButterKnife.bind(this);

        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("工作经历");

        lhBtnRight.setVisibility(View.VISIBLE);
        lhBtnRight.setText("保存");
        lhBtnRight.setTextSize(18);

        lhBtnRight.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        lhBtnRight.setTextColor(getResources().getColor(R.color.white));

        wxComnameEt.setText(SlashHelper.userManager().getUserinfo().getCompany());

        wxPosnameEt.setText(SlashHelper.userManager().getUserinfo().getPosition());

        isOpenInt = SlashHelper.userManager().getUserinfo().getIsOpen();
        ispublicSv.setOpened(isOpenInt == 1 ? true : false);

    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_btn_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:   //返回
                WorkExperActivity.this.finish();
                break;
            case R.id.lh_btn_right:    //保存

                SlashHelper.userManager().getUserinfo().setCompany(wxComnameEt.getText().toString());
                SlashHelper.userManager().getUserinfo().setPosition(wxPosnameEt.getText().toString());
                if ((ispublicSv.isOpened()) == true) {
                    SlashHelper.userManager().getUserinfo().setIsOpen(1);
                    isOpenInt = 1;
                } else {
                    SlashHelper.userManager().getUserinfo().setIsOpen(0);
                    isOpenInt = 0;
                }

                Intent intent = new Intent();
                intent.putExtra("company", wxComnameEt.getText().toString());

                intent.putExtra("position", wxPosnameEt.getText().toString());
                intent.putExtra("is", isOpenInt);

                WorkExperActivity.this.setResult(RESULT_OK, intent);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
