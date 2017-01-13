package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.taobao.av.util.StringUtil;
import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;

//个人介绍
public class MyInfoActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_task_describe)
    EditText etTaskDescribe;
    @Bind(R.id.iv_voice)
    ImageView ivVoice;
    @Bind(R.id.tv_second)
    TextView tvLength;
    @Bind(R.id.rel_voice)
    RelativeLayout relVoice;
    @Bind(R.id.imageButtonDial)
    ImageButton imageButtonDial;
    @Bind(R.id.tv_leftsecond)
    TextView tvLeftsecond;
    @Bind(R.id.tv_right)
    TextView tvRight;

    @Bind(R.id.rll_task_describe)
    RoundRelativeLayout rllTaskDescribe;
    @Bind(R.id.rll_voice)
    RoundRelativeLayout rllVoice;
    @Bind(R.id.tv_start)
    TextView tvStart;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    String infoNot = "";

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_info);
    }

    @Override
    public void init() {
        lhTvTitle.setText("个性签名");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("完成");

        if (!StringUtil.isEmpty(SlashHelper.userManager().getUserinfo().getNote())) {
            etTaskDescribe.setText(SlashHelper.userManager().getUserinfo().getNote());
            infoNot = SlashHelper.userManager().getUserinfo().getNote();
        }
        EditFilter.WordFilter(etTaskDescribe, 50);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_right:
                infoNot = etTaskDescribe.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("note", infoNot);
                setResult(RESULT_OK, intent);
                ToastUtil.showMessage("设置成功");
                finish();

                break;

        }
    }


}
