package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserLevelRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Level;
import com.zemult.merchant.model.apimodel.APIM_UserLevelList;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 用户详情--他的等级
 *
 * @author djy
 * @time 2016/8/2 10:46
 */
public class UserLevelActivity extends BaseActivity {

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
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.rl)
    RelativeLayout rl;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_level)
    TextView tvLevel;
    @Bind(R.id.tv_complete_num)
    TextView tvCompleteNum;
    @Bind(R.id.tv_complete_percent)
    TextView tvCompletePercent;
    @Bind(R.id.tv_highest_level)
    TextView tvHighestLevel;
    @Bind(R.id.tv_role_num)
    TextView tvRoleNum;
    @Bind(R.id.tv_like_num)
    TextView tvLikeNum;
    @Bind(R.id.tv1)
    TextView tv1;

    public static final String INTENT_USERID = "userId";
    private int userId;
    private UserLevelRequest userLevelRequest;
    private Context mContext;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_user_level);
    }

    @Override
    public void init() {
        userId = getIntent().getIntExtra(INTENT_USERID, -1);
        mContext = this;
        if(userId == SlashHelper.userManager().getUserId()){
            lhTvTitle.setText("我的等级");
            tv1.setText("我的数据");
        }else
            lhTvTitle.setText("Ta的等级");
        user_level();
    }


    // 查看用户的(其它人)等级
    private void user_level() {
        showPd();
        if (userLevelRequest != null) {
            userLevelRequest.cancel();
        }
        UserLevelRequest.Input input = new UserLevelRequest.Input();
        input.userId = userId; // 用户id

        input.convertJosn();
        userLevelRequest = new UserLevelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserLevelList) response).status == 1) {
                    setData(((APIM_UserLevelList) response).levelInfo);
                } else {
                    ToastUtils.show(mContext, ((APIM_UserLevelList) response).info);
                }
            }
        });
        sendJsonRequest(userLevelRequest);
    }

    private void setData(M_Level levelInfo) {
        if(levelInfo != null){

            // 用户头像
            if(!TextUtils.isEmpty(levelInfo.userHead))
                imageManager.loadCircleHasBorderImage(levelInfo.userHead, ivHead, 0xffdcdcdc, 1);
            // 用户名
            if(!TextUtils.isEmpty(levelInfo.userName))
                tvName.setText(levelInfo.userName);
            // 任务达成率(例:90%
            if(!TextUtils.isEmpty(levelInfo.taskDiscount))
                tvCompletePercent.setText(levelInfo.taskDiscount);
            // 账户等级
            tvLevel.setText(levelInfo.level + "");
            // 完成角色任务数
            tvCompleteNum.setText(levelInfo.taskNum + "次");
            // 最高的角色等级
            tvHighestLevel.setText("Lv." + levelInfo.industryLevel);
            // 角色总数
            tvRoleNum.setText(levelInfo.industryNum + "个");
            // 用户累计被赞的数目
            tvLikeNum.setText(levelInfo.goodNum + "");
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }
}
