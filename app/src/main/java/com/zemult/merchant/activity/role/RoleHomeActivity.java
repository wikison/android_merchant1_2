package com.zemult.merchant.activity.role;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.role.ExtraServerAdapter;
import com.zemult.merchant.aip.mine.UserIndustryDelRequest;
import com.zemult.merchant.aip.slash.ManagerGetmanagerinfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.bean.ExtraServerModel;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_ManagerGetmanagerinfo;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.DragGridView;
import com.zemult.merchant.view.common.MMAlert;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;

public class RoleHomeActivity extends BaseActivity {
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
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
    @Bind(R.id.head_iv)
    ImageView headIv;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.level_tv)
    TextView levelTv;
    @Bind(R.id.nextEXP_tv)
    TextView nextEXPTv;
    @Bind(R.id.tv_country_rank)
    TextView tvCountryRank;
    @Bind(R.id.hiddenview)
    TextView hiddenview;
    @Bind(R.id.grid_view)
    DragGridView gridView;

    public static final String INTENT_INDUSTRY_ID = "industryId";
    public static List<ExtraServerModel> dataSourceList = new ArrayList<ExtraServerModel>();

    public static final String INTENT_ROLE_ID = "roleId";
    public static final String INTENT_ROLE_NAME = "roleName";
    public static final String INTENT_ROLE_ICON = "roleIcon";
    public static final String Call_SLASH_REFRESH = "call_SlashMenuWindow_refresh";

    private int roleId;

    private String roleName;
    private String roleIcon;
    private Context mContext;
    private Activity mActivity;
    private int industryId;
    ExtraServerAdapter mAdapter;
    private ManagerGetmanagerinfoRequest getmanagerinfoRequest;
    private APIM_ManagerGetmanagerinfo info;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_role_home);
    }

    @Override
    public void init() {
        mContext = this;
        lhTvTitle.setText("角色管理");
        mContext = this;
        mActivity = this;
        roleId = getIntent().getIntExtra(INTENT_ROLE_ID, -1);
        roleName = getIntent().getStringExtra(INTENT_ROLE_NAME);
        roleIcon = getIntent().getStringExtra(INTENT_ROLE_ICON);
        llRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.gengduo_black_icon);
        industryId = getIntent().getIntExtra(INTENT_INDUSTRY_ID, -1);
        manager_getmanagerinfo();
        initGridData();
        mAdapter = new ExtraServerAdapter(this, dataSourceList);
        mAdapter.setIndustryId(industryId);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(mAdapter);
    }

    // 用户 单角色的详情
    private void manager_getmanagerinfo() {
        showPd();
        if (getmanagerinfoRequest != null) {
            getmanagerinfoRequest.cancel();
        }
        final ManagerGetmanagerinfoRequest.Input input = new ManagerGetmanagerinfoRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.industryId = industryId;

        input.convertJosn();
        getmanagerinfoRequest = new ManagerGetmanagerinfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_ManagerGetmanagerinfo) response).status == 1) {
                    info = (APIM_ManagerGetmanagerinfo) response;
                    if (info != null)
                        setData();
                } else {
                    ToastUtils.show(mContext, ((APIM_ManagerGetmanagerinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(getmanagerinfoRequest);
    }

    private void setData() {
        if (!TextUtils.isEmpty(info.icon))
            imageManager.loadCircleImage(info.icon, headIv);
        if (!TextUtils.isEmpty(info.name))
            nameTv.setText(info.name);

        levelTv.setText("LV." + info.level);
        nextEXPTv.setText("距离下一级LV." + (info.level + 1) + "还差" + info.nextEXP + "点经验值");
        tvCountryRank.setText("当前全国排名" + info.place + "名");

    }


    private void initGridData() {
        dataSourceList.clear();
        dataSourceList.add(new ExtraServerModel("探索任务", R.mipmap.role_renwu_icon, Color
                .rgb(59, 164, 224), 0xffffa726));
//        dataSourceList.add(new ExtraServerModel("优品甄选", R.mipmap.role_zhenxuan_icon,
//                Color.rgb(32, 45, 86), 0xff5acb44));
//            dataSourceList.add(new ExtraServerModel("圈子", R.mipmap.pic_1_33, Color
//                    .rgb(0, 169, 83)));
//        dataSourceList.add(new ExtraServerModel("聚会活动", R.mipmap.role_juhui_icon,
//                Color.rgb(212, 10, 90), 0xff4b9ff7));
//            dataSourceList.add(new ExtraServerModel("百宝箱", R.mipmap.pic_1_52, Color
//                    .rgb(254, 149, 0)));
//        if (industryId == 999) {
//            dataSourceList.add(new ExtraServerModel("消费报表", R.mipmap.role_xiaofei_icon, Color.rgb(
//                    212, 10, 90), 0xffeb406d));
//        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_right, R.id.ll_right})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_right:
            case R.id.ll_right:
                showPopupWindow(mContext, view);
                break;
        }
    }

    private void showPopupWindow(final Context context, View rightButton) {
        //设置contentView
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_conversationhead, null);
        final PopupWindow mPopWindow = new PopupWindow(contentView,
                DensityUtil.dip2px(context, 120), DensityUtil.dip2px(context, 36), true);
        mPopWindow.setContentView(contentView);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopWindow.setOutsideTouchable(true);

//        TextView tv1 = (TextView) contentView.findViewById(R.id.tv1);
        TextView tv2 = (TextView) contentView.findViewById(R.id.tv2);
//        ImageView iv1 = (ImageView) contentView.findViewById(R.id.iv1);
        ImageView iv2 = (ImageView) contentView.findViewById(R.id.iv2);
        LinearLayout l1 = (LinearLayout) contentView.findViewById(R.id.l1);
        l1.setVisibility(View.GONE);
        LinearLayout l2 = (LinearLayout) contentView.findViewById(R.id.l2);

//        tv1.setText("分享");
        tv2.setText("删除");
//        iv1.setImageResource(R.mipmap.fengxiang_white_icon);
        iv2.setImageResource(R.mipmap.jubao_white_icon);
//        l1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String URL_SHARE_APP = SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_APP, "http://www.54xiegang.com/csdown/index.html");
//                UMImage image = new UMImage(mContext, roleIcon);
//                new ShareAction(mActivity).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)//SHARE_MEDIA.SINA,SHARE_MEDIA.QZONE,
//                        .withText("我在斜杠平台扮演了" + roleName + "角色！快来参与吧！")
//                        .withMedia(image)
//                        .withTargetUrl(URL_SHARE_APP)
//                        .setCallback(umShareListener)
//                        .open();
//                mPopWindow.dismiss();
//            }
//        });
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MMAlert.showDelDialog(mContext, roleName, new MMAlert.DelCallback() {
                    @Override
                    public void onDel() {
                        user_industry_del();
                    }
                });
                mPopWindow.dismiss();
            }
        });
        //显示PopupWindow
        mPopWindow.showAsDropDown(rightButton, -155, -26);
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
                    EventBus.getDefault().post(Call_SLASH_REFRESH);
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
