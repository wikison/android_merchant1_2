package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserIndustryDelRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.common.MMAlert;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;

/**
 * 角色设置
 *
 * @author djy
 * @time 2016/8/10 13:44
 */
public class RoleSetActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.rl_share)
    RelativeLayout rlShare;
    @Bind(R.id.tv_del)
    RoundTextView tvDel;

    public static final String INTENT_ROLE_ID = "roleId";
    public static final String INTENT_ROLE_NAME = "roleName";
    public static final String INTENT_ROLE_ICON = "roleIcon";
    public static final String Call_SLASHMENUWINDOW_REFRESH = "call_SlashMenuWindow_refresh";

    private Context mContext;
    private Activity mActivity;

    private int roleId;
    private String roleName;
    private String roleIcon;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_role_set);
    }

    @Override
    public void init() {
        mContext = this;
        mActivity = this;
        roleId = getIntent().getIntExtra(INTENT_ROLE_ID, -1);
        roleName = getIntent().getStringExtra(INTENT_ROLE_NAME);
        roleIcon = getIntent().getStringExtra(INTENT_ROLE_ICON);
        lhTvTitle.setText("角色设置");
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rl_share, R.id.tv_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.rl_share:
                String URL_SHARE_APP = SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_APP, "http://www.54xiegang.com/csdown/index.html");
                UMImage image = new UMImage(mContext, roleIcon);
                new ShareAction(mActivity).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)//SHARE_MEDIA.SINA,SHARE_MEDIA.QZONE,
                        .withText("我在斜杠平台扮演了" + roleName + "角色！快来参与吧！")
                        .withMedia(image)
                        .withTargetUrl(URL_SHARE_APP)
                        .setCallback(umShareListener)
                        .open();
                break;
            case R.id.tv_del:
                MMAlert.showDelDialog(mContext, roleName, new MMAlert.DelCallback() {
                    @Override
                    public void onDel() {
                        user_industry_del();
                    }
                });
                break;
        }
    }

    // 删除角色
    UserIndustryDelRequest userIndustryDelRequest;
    private void user_industry_del() {
        if (userIndustryDelRequest != null) {
            userIndustryDelRequest.cancel();
        }
        UserIndustryDelRequest.Input input = new UserIndustryDelRequest.Input();

        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.industryId = roleId;

        input.convertJosn();
        userIndustryDelRequest = new UserIndustryDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("删除成功");
                    EventBus.getDefault().post(Call_SLASHMENUWINDOW_REFRESH);
                    setResult(RESULT_OK);
                    onBackPressed();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });

        sendJsonRequest(userIndustryDelRequest);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
            } else {
                ToastUtil.showMessage("分享成功了");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtil.showMessage("分享失败了");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtil.showMessage("分享取消了");
        }
    };
}
