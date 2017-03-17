package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.pickerview.TimePickerView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonGetAllTitleRequest;
import com.zemult.merchant.aip.slash.UserPreInvitationAddRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Title;
import com.zemult.merchant.model.apimodel.APIM_CommonGetAllTitleList;
import com.zemult.merchant.util.ShareText;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.common.MMAlert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2017/3/16.
 */

public class PreInviteActivity extends BaseActivity {
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
    @Bind(R.id.tv_topic)
    TextView tvTopic;
    @Bind(R.id.rl_topic)
    RelativeLayout rlTopic;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.rl_time)
    RelativeLayout rlTime;
    @Bind(R.id.et_organizer)
    EditText etOrganizer;
    @Bind(R.id.rl_organizer)
    RelativeLayout rlOrganizer;
    @Bind(R.id.btn_confirm)
    Button btnConfirm;

    CommonGetAllTitleRequest commonGetAllTitleRequest;
    UserPreInvitationAddRequest userPreInvitationAddRequest;
    private Context mContext;
    List<M_Title> titleList = new ArrayList<>();
    M_Title mTitle;
    String[] titles;
    TimePickerView pvTime;
    String selectTime = "";
    String selectTopic = "";
    int preId = -1;
    boolean isAdd = false;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_pre_invite);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;

        getAllTitleList();
    }

    private void getAllTitleList() {
        if (commonGetAllTitleRequest != null) {
            commonGetAllTitleRequest.cancel();
        }
        commonGetAllTitleRequest = new CommonGetAllTitleRequest(new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    titleList = ((APIM_CommonGetAllTitleList) response).titleList;
                    if (titleList != null && titleList.size() > 0) {
                        mTitle = getMTitle("商务宴请");
                        tvTopic.setText(mTitle == null ? "请选择活动主题" : mTitle.name);
                        selectTopic = (mTitle == null ? "" : mTitle.name);
                        titles = new String[titleList.size()];
                        for (int i = 0; i < titleList.size(); i++) {
                            titles[i] = titleList.get(i).name;
                        }
                    }
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });

        sendJsonRequest(commonGetAllTitleRequest);
    }

    //用户(经营人)发布方案
    private void user_pre_invitation() {
        if (userPreInvitationAddRequest != null) {
            userPreInvitationAddRequest.cancel();
        }

        UserPreInvitationAddRequest.Input input = new UserPreInvitationAddRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.titleId = mTitle.titleId;
        input.invitationTime = selectTime + ":00";
        input.convertJosn();
        userPreInvitationAddRequest = new UserPreInvitationAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    preId = ((CommonResult) response).preId;
                    tvTopic.setClickable(false);
                    tvTime.setClickable(false);
                    etOrganizer.setEnabled(false);
                    shareToWX();
                    isAdd = true;
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userPreInvitationAddRequest);
    }

    private void initView() {
        lhTvTitle.setText("发起预邀");
        etOrganizer.setText(SlashHelper.userManager().getUserinfo().name);
    }

    private void initListener() {

    }

    private M_Title getMTitle(String name) {
        M_Title result = null;
        for (M_Title t : titleList) {
            if (t.name.equals(name)) {
                result = t;
                break;

            }
        }
        return result;
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_topic, R.id.tv_time, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_topic:
                showTopics();
                break;
            case R.id.tv_time:
                showTimePicker();
                break;
            case R.id.btn_confirm:
                if (StringUtils.isBlank(selectTopic)) {
                    ToastUtil.showMessage("请选择活动主题");
                    return;
                }
                if (StringUtils.isBlank(selectTime)) {
                    ToastUtil.showMessage("请选择活动时间");
                    return;
                }
                if (StringUtils.isBlank(etOrganizer.getText().toString())) {
                    ToastUtil.showMessage("请填写发起人");
                    return;
                }

                if (isAdd) {
                    shareToWX();
                } else {
                    user_pre_invitation();
                }
                break;

        }
    }


    /**
     * 显示主题列表
     */
    private void showTopics() {
        MMAlert.showAlert(this, null, titles, null,
                new MMAlert.OnAlertSelectId() {
                    @Override
                    public void onClick(int whichButton) {
                        if (!(whichButton == titles.length)) {
                            mTitle = getMTitle(titles[whichButton]);
                            selectTopic = mTitle.name;
                            tvTopic.setText(mTitle.name);
                        }
                    }
                });
    }

    private void showTimePicker() {
        Date date = new Date();
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(new Date());
        Calendar endDate = Calendar.getInstance();
        endDate.set(2050, 12, 30);
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                Date now = new Date();
                if (date.getTime() - now.getTime() < 0) {
                    ToastUtil.showMessage("选择时间不能晚于当前时间");
                } else {
                    selectTime = getTime(date);
                    tvTime.setText(getTime(date));
                }

            }
        }).setType(TimePickerView.Type.YEAR_MONTH_DAY_HOUR_MIN)//默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText("选择时间")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.ls_blue))//确定按钮文字颜色
                .setCancelColor(Color.BLACK)//取消按钮文字颜色
                .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
    }

    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }


    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    private void shareToWX() {
        //分享到微信
        new ShareAction(PreInviteActivity.this)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(umShareListener)
                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】拟于" + selectTime.substring(0, 4) + "年" + selectTime.substring(5, 7) + "月" + selectTime.substring(8, 10) + "日" + selectTime.substring(11, 16) + "举行" + selectTopic + ", 请确认")
                .withTargetUrl(Constants.PRE_SHARE_INVITATION + preId)
                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】发起了一个" + selectTopic)
                .share();
    }
}
