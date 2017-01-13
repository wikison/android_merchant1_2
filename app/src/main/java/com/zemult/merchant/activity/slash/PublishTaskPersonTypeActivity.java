package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Wikison on 2016/8/6.
 */
public class PublishTaskPersonTypeActivity extends BaseActivity {

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
    @Bind(R.id.rl_public)
    RelativeLayout rlPublic;
    @Bind(R.id.rl_friend)
    RelativeLayout rlFriend;

    Context mContext;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_publish_task_person_type);
    }

    @Override
    public void init() {
        iniData();
    }

    private void iniData() {
        mContext = this;
        lhTvTitle.setText("选择发布对象");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rl_public, R.id.rl_friend})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_public:
                intent = new Intent(Constants.BROCAST_PUBLISH_TASK_PERSON);
                intent.putExtra("person_type", 1);
                intent.putExtra("person_type_name", "公开");
                setResult(RESULT_OK, intent);
                sendBroadcast(intent);
                finish();
                break;
            case R.id.rl_friend:
                intent = new Intent(mContext, PublishTaskFriendActivity.class);
                intent.putExtra("requesttype", Constants.BROCAST_PUBLISH_TASK_PERSON);
                intent.putExtra("person_type", 0);
                intent.putExtra("person_type_name", "好友");
                startActivityForResult(intent, 1);
                break;
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
        }
    }

}
