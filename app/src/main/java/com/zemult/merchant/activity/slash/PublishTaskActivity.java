package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundRelativeLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.task.TaskIndustryPushMerchantRequest;
import com.zemult.merchant.aip.task.TaskIndustryPushRequest;
import com.zemult.merchant.alipay.taskpay.BonuseChoosePayTypeActivity;
import com.zemult.merchant.app.MAppCompatActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.PMNumView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2016/8/2.
 * 发布探索
 */
public class PublishTaskActivity extends MAppCompatActivity {


    int REQUESTCODE_PAY = 1000;

    TaskIndustryPushRequest taskIndustryPushRequest;
    TaskIndustryPushMerchantRequest taskIndustryPushMerchantRequest;
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
    @Bind(R.id.tv_title_select_role)
    TextView tvTitleSelectRole;
    @Bind(R.id.tv_select_role)
    TextView tvSelectRole;
    @Bind(R.id.rl_select_role)
    RelativeLayout rlSelectRole;
    @Bind(R.id.iv_right_role)
    ImageView ivRightRole;
    @Bind(R.id.tv_title_task_title)
    TextView tvTitleTaskTitle;
    @Bind(R.id.rl_task_title)
    RelativeLayout rlTaskTitle;
    @Bind(R.id.et_task_title)
    EditText etTaskTitle;
    @Bind(R.id.tv_title_num_limit)
    TextView tvTitleNumLimit;
    @Bind(R.id.tv_title_select_publish_type)
    TextView tvTitleSelectPublishType;
    @Bind(R.id.tv_select_publish_type)
    TextView tvSelectPublishType;
    @Bind(R.id.rl_select_publish_type)
    RelativeLayout rlSelectPublishType;
    @Bind(R.id.tv_title_task_describe)
    TextView tvTitleTaskDescribe;
    @Bind(R.id.rl_task_describe)
    RelativeLayout rlTaskDescribe;
    @Bind(R.id.et_task_describe)
    EditText etTaskDescribe;
    @Bind(R.id.tv_num_limit)
    TextView tvNumLimit;
    @Bind(R.id.rll_task_describe)
    RoundRelativeLayout rllTaskDescribe;
    @Bind(R.id.tv_title_select_deadline)
    TextView tvTitleSelectDeadline;
    @Bind(R.id.pmnv_select_deadline)
    PMNumView pmnvDeadline;
    @Bind(R.id.rl_select_deadline)
    RelativeLayout rlSelectDeadline;
    @Bind(R.id.ll_part2)
    LinearLayout llPart2;
    @Bind(R.id.iv_del_bonuse)
    ImageView ivDelBonuse;
    @Bind(R.id.tv_title_select_task_bonuses)
    TextView tvTitleSelectTaskBonuses;
    @Bind(R.id.tv_select_task_bonuses)
    TextView tvSelectTaskBonuses;
    @Bind(R.id.rl_select_task_bonuses)
    RelativeLayout rlSelectTaskBonuses;
    @Bind(R.id.ll_content_bonuse)
    LinearLayout llContentBonuse;
    @Bind(R.id.clear_bonuse)
    Button clearBonuse;
    @Bind(R.id.ll_action_bonuse)
    LinearLayout llActionBonuse;
    @Bind(R.id.hsv_bonuse)
    HorizontalScrollView hsvBonuse;
    @Bind(R.id.iv_del_coupon)
    ImageView ivDelCoupon;
    @Bind(R.id.tv_title_select_task_coupon)
    TextView tvTitleSelectTaskCoupon;
    @Bind(R.id.tv_select_task_coupon)
    TextView tvSelectTaskCoupon;
    @Bind(R.id.rl_select_task_coupon)
    RelativeLayout rlSelectTaskCoupon;
    @Bind(R.id.ll_content_coupon)
    LinearLayout llContentCoupon;
    @Bind(R.id.clear_coupon)
    Button clearCoupon;
    @Bind(R.id.ll_action_coupon)
    LinearLayout llActionCoupon;
    @Bind(R.id.hsv_coupon)
    HorizontalScrollView hsvCoupon;
    @Bind(R.id.iv_del_vote)
    ImageView ivDelVote;
    @Bind(R.id.tv_title_select_insert_vote)
    TextView tvTitleSelectInsertVote;
    @Bind(R.id.tv_select_insert_vote)
    TextView tvSelectInsertVote;
    @Bind(R.id.rl_insert_vote)
    RelativeLayout rlInsertVote;
    @Bind(R.id.ll_content_vote)
    LinearLayout llContentVote;
    @Bind(R.id.clear_vote)
    Button clearVote;
    @Bind(R.id.ll_action_vote)
    LinearLayout llActionVote;
    @Bind(R.id.hsv_vote)
    HorizontalScrollView hsvVote;
    @Bind(R.id.iv_del_person)
    ImageView ivDelPerson;
    @Bind(R.id.tv_title_select_task_person)
    TextView tvTitleSelectTaskPerson;
    @Bind(R.id.tv_select_task_person)
    TextView tvSelectTaskPerson;
    @Bind(R.id.rl_select_task_person)
    RelativeLayout rlSelectTaskPerson;
    @Bind(R.id.ll_content_person)
    LinearLayout llContentPerson;
    @Bind(R.id.clear_person)
    Button clearPerson;
    @Bind(R.id.ll_action_person)
    LinearLayout llActionPerson;
    @Bind(R.id.hsv_person)
    HorizontalScrollView hsvPerson;
    @Bind(R.id.ll_part1)
    LinearLayout llPart1;
    @Bind(R.id.tv_title_merchant_name)
    TextView tvTitleMerchantName;
    @Bind(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @Bind(R.id.rl_merchant_name)
    RelativeLayout rlMerchantName;
    @Bind(R.id.tv_title_commission)
    TextView tvTitleCommission;
    @Bind(R.id.et_commission)
    EditText etCommission;
    @Bind(R.id.rll_task_commission)
    RoundRelativeLayout rllTaskCommission;
    @Bind(R.id.tv_commission)
    TextView tvCommission;
    @Bind(R.id.rl_commission)
    RelativeLayout rlCommission;
    @Bind(R.id.tv_title_discount)
    TextView tvTitleDiscount;
    @Bind(R.id.et_discount)
    EditText etDiscount;
    @Bind(R.id.rll_task_discount)
    RoundRelativeLayout rllTaskDiscount;
    @Bind(R.id.tv_discount)
    TextView tvDiscount;
    @Bind(R.id.rl_discount)
    RelativeLayout rlDiscount;
    @Bind(R.id.ll_part3)
    LinearLayout llPart3;

    private Context mContext;
    private int merchantId;
    private int roleId = -1;
    private String roleName;
    private String title;
    private String endTime;
    private String description;
    private int taskType = -1;
    private int personType = 1; //任务对象(0:好友，1:公开)
    private String friendIds = "";//任务对象为好友
    private int cashType = 0;
    private int type = 0;
    private int voteType = 0;
    private String voteTitle = "";
    private String voteNote = "";// 以,隔开
    private double bonuseMoney;
    private int bonuseNum;
    private int bonuseType;
    private int isVote = 0;
    private int isHand;
    private double payMoney;
    private double voucherMoney;
    private int voucherNum;
    private double voucherMinMoney;
    private int isUnion;
    private String voucherNote = "";
    private String voucherEndTime = "";
    private boolean isMerchant = false;
    private String merchantName = "";
    private String strFormatPattern = "yyyy-MM-dd HH:mm:ss";

    int isVoucher = 0;
    double discount = 0, commissionDiscount = 0;

    private boolean isMerchantBuy = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_task);
        ButterKnife.bind(this);
        registerReceiver(new String[]{
                Constants.BROCAST_PUBLISH_TASK_ROLE, Constants.BROCAST_PUBLISH_TASK_TYPE, Constants.BROCAST_PUBLISH_TASK_VOTE,
                Constants.BROCAST_PUBLISH_TASK_PERSON, Constants.BROCAST_PUBLISH_TASK_BONUSES,
                Constants.BROCAST_PUBLISH_TASK_COUPON
        });
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
        lhTvTitle.setText(getResources().getString(R.string.title_publish_task));
        merchantId = getIntent().getIntExtra("merchant_id", -1);
        if (merchantId > 0) {
            isMerchant = true;
            merchantName = getIntent().getStringExtra("merchant_name");
        } else {
            isMerchant = false;
        }

        if (!isMerchantBuy) {
            pmnvDeadline.setMinNum(1);
            pmnvDeadline.setMaxNum(30);
            pmnvDeadline.setDefaultNum(7);
            pmnvDeadline.setFilter();
        } else {
            pmnvDeadline.setMinNum(1);
            pmnvDeadline.setMaxNum(365);
            pmnvDeadline.setDefaultNum(30);
            pmnvDeadline.setFilter();
        }

        isMerchantBuy = getIntent().getBooleanExtra("merchant_task_type", false);
        type = isMerchantBuy ? 3 : 0;

        endTime = DateTimeUtil.getFormatCurrentAdd(7, strFormatPattern);

    }

    private void initView() {
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("发布");

        if (isMerchant) {
            if (isMerchantBuy) {
                llPart2.setVisibility(View.GONE);
                llPart3.setVisibility(View.VISIBLE);
                tvTitleSelectDeadline.setText("探索时限(1-365天)");
            } else {
                llPart2.setVisibility(View.VISIBLE);
                llPart3.setVisibility(View.GONE);
                hsvPerson.setVisibility(View.GONE);
                hsvBonuse.setVisibility(View.GONE);
                hsvCoupon.setVisibility(View.VISIBLE);
                tvTitleSelectDeadline.setText("探索时限(1-30天)");
            }
            tvMerchantName.setText(merchantName);
            tvTitleTaskDescribe.setText("描述");
        } else {
            llPart2.setVisibility(View.VISIBLE);
            llPart3.setVisibility(View.GONE);
            hsvCoupon.setVisibility(View.GONE);
            hsvPerson.setVisibility(View.VISIBLE);
            hsvBonuse.setVisibility(View.VISIBLE);

            tvTitleTaskDescribe.setText("描述(选填)");
            tvTitleSelectDeadline.setText("探索时限(1-30天)");
        }

        if (isMerchantBuy) {
            tvSelectRole.setText("营销经理");
            ivRightRole.setVisibility(View.INVISIBLE);
        } else {
            tvSelectRole.setText("点击选择");
            ivRightRole.setVisibility(View.VISIBLE);
        }

        EditFilter.WordFilter(etTaskTitle, 120, tvTitleNumLimit);
        EditFilter.WordFilter(etTaskDescribe, 1000, tvNumLimit);
        EditFilter.CashFilter(etDiscount, 10);
        EditFilter.CashFilter(etCommission, 100);

        ViewGroup.LayoutParams lp = llContentBonuse.getLayoutParams();
        lp.width = DensityUtil.getWindowWidth(this);
        lp = llContentVote.getLayoutParams();
        lp.width = DensityUtil.getWindowWidth(this);
        lp = llContentPerson.getLayoutParams();
        lp.width = DensityUtil.getWindowWidth(this);
        lp = llContentCoupon.getLayoutParams();
        lp.width = DensityUtil.getWindowWidth(this);
    }

    private void initListener() {
        hsvBonuse.setOnTouchListener(hsvOnTouchListener);
        hsvCoupon.setOnTouchListener(hsvOnTouchListener);
        hsvVote.setOnTouchListener(hsvOnTouchListener);
        hsvPerson.setOnTouchListener(hsvOnTouchListener);
        pmnvDeadline.setOnNumChangeListener(new PMNumView.NumChangeListener() {
            @Override
            public void onNumChanged(int num) {
                endTime = DateTimeUtil.getFormatCurrentAdd(num, strFormatPattern);
            }
        });

    }

    HorizontalScrollView.OnTouchListener hsvOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    switch (v.getId()) {
                        case R.id.hsv_bonuse:
                            if (llActionBonuse.getVisibility() == View.VISIBLE) {
                                hsvBonuse.smoothScrollTo(0, 0);
                            }
                            break;
                        case R.id.hsv_coupon:
                            if (llActionCoupon.getVisibility() == View.VISIBLE) {
                                hsvCoupon.smoothScrollTo(0, 0);
                            }
                            break;
                        case R.id.hsv_vote:
                            if (llActionVote.getVisibility() == View.VISIBLE) {
                                hsvVote.smoothScrollTo(0, 0);
                            }
                            break;
                        case R.id.hsv_person:
                            if (llActionPerson.getVisibility() == View.VISIBLE) {
                                hsvPerson.smoothScrollTo(0, 0);
                            }
                            break;
                    }

            }
            return true;
        }
    };

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        if (Constants.BROCAST_PUBLISH_TASK_ROLE.equals(intent.getAction())) {
            roleId = intent.getIntExtra("role_id", -1);
            roleName = intent.getStringExtra("role_name");
            if (roleId > 0 && !StringUtils.isBlank(roleName)) {
                tvSelectRole.setText(roleName);
            }

        }
        if (Constants.BROCAST_PUBLISH_TASK_VOTE.equals(intent.getAction())) {
            voteTitle = intent.getStringExtra("vote_title");
            voteNote = intent.getStringExtra("vote_note");
            if (!StringUtils.isBlank(voteNote)) {
                isVote = 1;
                tvSelectInsertVote.setText("已设置");
                ivDelVote.setVisibility(View.VISIBLE);
            } else {
                isVote = 0;
            }
        }

        if (Constants.BROCAST_PUBLISH_TASK_TYPE.equals(intent.getAction())) {
            taskType = intent.getIntExtra("publish_type", -1);
            if (taskType == 3) {
                payMoney = intent.getDoubleExtra("pay_money", 0);
            }

        }
        if (Constants.BROCAST_PUBLISH_TASK_PERSON.equals(intent.getAction())) {
            personType = intent.getIntExtra("person_type", -1);
            tvSelectTaskPerson.setText(intent.getStringExtra("person_type_name"));
            if (personType == 0) {
                friendIds = intent.getStringExtra("friend_ids");
                int friendNum = intent.getIntExtra("friend_num", 0);
                if (friendNum > 0) {
                    tvSelectTaskPerson.setText(friendNum + "人");
                    ivDelPerson.setVisibility(View.VISIBLE);
                }
                Log.i("friendIds:", friendIds);
            }
        }

        if (Constants.BROCAST_PUBLISH_TASK_BONUSES.equals(intent.getAction())) {
            bonuseMoney = intent.getDoubleExtra("bonuse_money", 0);
            bonuseNum = intent.getIntExtra("bonuse_num", 0);
            bonuseType = intent.getIntExtra("bonuse_type", -1);
            isHand = intent.getIntExtra("is_hand", -1);

            if (bonuseMoney > 0) {
                cashType = 1;
                ivDelBonuse.setVisibility(View.VISIBLE);
                tvSelectTaskBonuses.setText(bonuseNum + "个/" + bonuseMoney + "元");
            } else {
                cashType = 0;
                ivDelBonuse.setVisibility(View.GONE);
            }

        }
        if (Constants.BROCAST_PUBLISH_TASK_COUPON.equals(intent.getAction())) {
            voucherMoney = intent.getDoubleExtra("voucher_money", 0);
            voucherNum = intent.getIntExtra("voucher_num", 0);
            voucherMinMoney = intent.getDoubleExtra("voucher_min_money", 0);
            voucherNote = intent.getStringExtra("voucher_note");
            voucherEndTime = intent.getStringExtra("voucher_end_time");
            isUnion = intent.getIntExtra("is_union", -1);
            if (voucherNum > 0) {
                isVoucher = 1;
                tvSelectTaskCoupon.setText(voucherNum + "张");
                ivDelCoupon.setVisibility(View.VISIBLE);
            } else {
                isVoucher = 0;
                tvSelectTaskCoupon.setText("");
                ivDelCoupon.setVisibility(View.GONE);
            }

        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right,
            R.id.tv_select_role, R.id.tv_select_task_person, R.id.tv_select_task_bonuses, R.id.tv_select_task_coupon, R.id.tv_select_insert_vote,
            R.id.rl_select_role, R.id.rl_select_task_person, R.id.rl_select_task_bonuses, R.id.rl_select_task_coupon, R.id.rl_insert_vote,
            R.id.iv_del_bonuse, R.id.clear_bonuse, R.id.iv_del_coupon, R.id.clear_coupon, R.id.iv_del_vote, R.id.clear_vote, R.id.iv_del_person, R.id.clear_person})
    public void viewClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;

            case R.id.iv_del_bonuse:
                hsvBonuse.smoothScrollTo(llActionBonuse.getWidth(), 0);
                break;
            case R.id.iv_del_coupon:
                hsvCoupon.smoothScrollTo(llActionCoupon.getWidth(), 0);
                break;
            case R.id.iv_del_vote:
                hsvVote.smoothScrollTo(llActionVote.getWidth(), 0);
                break;
            case R.id.iv_del_person:
                hsvPerson.smoothScrollTo(llActionPerson.getWidth(), 0);
                break;
            case R.id.clear_bonuse:
                bonuseMoney = 0;
                bonuseNum = 0;
                bonuseType = 0;
                isHand = -1;
                tvSelectTaskBonuses.setText("");
                hsvBonuse.smoothScrollTo(0, 0);
                ivDelBonuse.setVisibility(View.GONE);
                break;
            case R.id.clear_coupon:
                isVoucher = 0;
                tvSelectTaskCoupon.setText("");
                hsvCoupon.smoothScrollTo(0, 0);
                ivDelCoupon.setVisibility(View.GONE);
                voucherMoney = 0;
                voucherNum = 0;
                voucherMinMoney = 0;
                voucherEndTime = "";
                voucherNote = "";
                break;

            case R.id.clear_vote:
                isVote = 0;
                voteTitle = "";
                voteNote = "";
                tvSelectInsertVote.setText("");
                hsvVote.smoothScrollTo(0, 0);
                ivDelVote.setVisibility(View.GONE);
                break;

            case R.id.clear_person:
                friendIds = "";
                tvSelectTaskPerson.setText("");
                hsvPerson.smoothScrollTo(0, 0);
                ivDelPerson.setVisibility(View.GONE);
                break;

            case R.id.tv_select_role:
            case R.id.rl_select_role:
                if (!isMerchantBuy) {
                    IntentUtil.start_activity(this, AllCategoryActivity.class,
                            new Pair<String, String>("requesttype", Constants.BROCAST_PUBLISH_TASK_ROLE));
                }

                break;

            case R.id.tv_select_task_person:
            case R.id.rl_select_task_person:
                IntentUtil.start_activity(this, PublishTaskFriendActivity.class,
                        new Pair<String, String>("requesttype", Constants.BROCAST_PUBLISH_TASK_PERSON));
                break;
//            case R.id.iv_hongbao:
//                bonuseMoney=0;
//                cashType = 0;
//                tvSelectTaskBonuses.setText("");
//                tvSelectTaskReward.setText("");
//                ivHongbao.setVisibility(View.GONE);
//                break;
//            case R.id.tv_select_deadline:
//            case R.id.rl_select_deadline:
//                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
//                        this, tvSelectDeadline.getText().toString(), "截止时间必须大于当前时间", 1);
//                dateTimePicKDialog.dateTimePicKDialog(tvSelectDeadline);
//                break;

            case R.id.tv_select_task_bonuses:
            case R.id.rl_select_task_bonuses:
                intent = new Intent(mContext, PublishTaskBonusesActivity.class);
                intent.putExtra("bonuse_money", bonuseMoney);
                intent.putExtra("bonuse_num", bonuseNum);
                intent.putExtra("bonuse_type", bonuseType);
                intent.putExtra("is_hand", isHand);
                intent.putExtra("requesttype", Constants.BROCAST_PUBLISH_TASK_BONUSES);
                startActivity(intent);

                break;

            case R.id.tv_select_insert_vote:
            case R.id.rl_insert_vote:
                title = etTaskTitle.getText().toString();
                if (StringUtils.isBlank(title)) {
                    ToastUtil.showMessage("插入投票需要先在标题中输入投票主题");
                } else {
                    intent = new Intent(mContext, PublishTaskVoteActivity.class);
                    intent.putExtra("title", title);
                    intent.putExtra("vote_note", voteNote);
                    intent.putExtra("merchant_id", merchantId);
                    intent.putExtra("merchant_name", merchantName);
                    intent.putExtra("pay_money", payMoney);
                    intent.putExtra("requesttype", Constants.BROCAST_PUBLISH_TASK_VOTE);
                    startActivity(intent);
                }
                break;

            case R.id.tv_select_task_coupon:
            case R.id.rl_select_task_coupon:
                intent = new Intent(mContext, PublishTaskRewardActivity.class);
                intent.putExtra("voucher_money", voucherMoney);
                intent.putExtra("voucher_num", voucherNum);
                intent.putExtra("voucher_min_money", voucherMinMoney);
                intent.putExtra("voucher_end_time", voucherEndTime);
                intent.putExtra("voucher_note", voucherNote);
                intent.putExtra("requesttype", Constants.BROCAST_PUBLISH_TASK_COUPON);
                startActivity(intent);
                break;

            case R.id.tv_right:

                title = etTaskTitle.getText().toString();
                if (StringUtils.isEmpty(title)) {
                    ToastUtil.showMessage("请填写任务标题");
                    return;
                }
                if (title.length() < 5) {
                    etTaskTitle.setError("任务标题不能少于5个字");
                    return;
                }
                description = etTaskDescribe.getText().toString();
                if(isMerchant){
                    if(StringUtils.isBlank(description)){
                        etTaskDescribe.setError("描述不能为空");
                        return;
                    }
                }

                if (!StringUtils.isBlank(etDiscount.getText().toString()))
                    discount = Double.valueOf(etDiscount.getText().toString());
                if (!StringUtils.isBlank(etCommission.getText().toString()))
                    commissionDiscount = Double.valueOf(etCommission.getText().toString());

                if (isMerchant) {
                    if (!isMerchantBuy) {
                        if (roleId == -1) {
                            ToastUtil.showMessage("请选择任务角色");
                            return;
                        }
                    } else {
                        roleId = 999;
                    }
                    publishMerchantTask();
                } else {
                    if (roleId == -1) {
                        ToastUtil.showMessage("请选择任务角色");
                        return;
                    }
                    publishMyTask();
                }

                break;
        }
    }

    private void publishMyTask() {
        if (taskIndustryPushRequest != null) {
            taskIndustryPushRequest.cancel();
        }
        TaskIndustryPushRequest.Input input = new TaskIndustryPushRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.industryId = roleId;       //		是	角色id
        input.title = title;             //		是	标题
        input.note = description;        //	    是	描述
        input.endTime = endTime;         //		是	截止时间(格式为:yyyy-MM-dd HH:mm:ss)
        input.friends = friendIds;       //		否	好友ids（对象为好友时）
        input.cashType = cashType;       //		是	金钱奖励方式(0:无,1:红包)
        input.isVote = isVote;           //     否  有投票插件(0:否,1:是)
        input.voteTitle = title;         //		否	投票主题(投票类必填)
        input.voteType = 0;              //		否	投票方式(投票类必填)(0:单选，1:多选)
        input.voteNotes = voteNote;      //		否	投票选项(投票类必填)(多个","分隔)
        input.bonuseMoney = bonuseMoney; //		否	红包金额(红包奖励必填)
        input.bonuseNum = bonuseNum;     //		否	红包个数(红包奖励必填)
        input.bonuseType = bonuseType;   //		否	红包方式(0:固定额度,1:随机)(红包奖励必填)
        input.isHand = isHand;           //		红包分配是否发布人手动分配(0:否,1:是)(红包奖励必填)默认填0
        input.convertJosn();
        taskIndustryPushRequest = new TaskIndustryPushRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    if (bonuseMoney > 0) {
                        Intent intent1 = new Intent(PublishTaskActivity.this, BonuseChoosePayTypeActivity.class);
                        intent1.putExtra("title", title);
                        intent1.putExtra("taskIndustryId", ((CommonResult) response).taskIndustryId);
                        intent1.putExtra("bonuseMoney", bonuseMoney);
                        startActivityForResult(intent1, REQUESTCODE_PAY);
                    } else {
                        ToastUtil.showMessage("任务发布成功");
                        sendBroadcast(new Intent(Constants.BROCAST_FRESHTASKLIST));
                        setResult(RESULT_OK);
                        PublishTaskActivity.this.finish();
                    }


                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(taskIndustryPushRequest);
    }

    //商家发布任务
    private void publishMerchantTask() {
        if (taskIndustryPushMerchantRequest != null) {
            taskIndustryPushMerchantRequest.cancel();
        }
        TaskIndustryPushMerchantRequest.Input input = new TaskIndustryPushMerchantRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.merchantId = merchantId;
        input.industryId = roleId;    //		是	营销经理角色id固定为999
        input.title = title;    //		是	标题
        input.note = description;         //	是	描述
        input.endTime = endTime;//		是	截止时间(格式为:yyyy-MM-dd HH:mm:ss)
        input.type = type;    //	是	探索方式(0:普通任务,3:买单/消费)
        input.isVote = isVote;        //		是	是否 有投票插件(0:否,1:是)
        input.voteTitle = title;    //			否	投票主题(投票类必填)
        input.voteType = 0;    //		否	投票方式(投票类必填)(0:单选，1:多选)
        input.voteNotes = voteNote;    //			否	投票选项(投票类必填)(多个","分隔)
        input.isVoucher = isVoucher;  // 是   是否有优惠券(0:否,1:是)
        input.voucherMoney = voucherMoney;
        input.voucherNum = voucherNum;
        input.voucherMinMoney = voucherMinMoney;
        input.voucherNote = voucherNote;
        input.voucherEndTime = voucherEndTime;
        input.discount = discount;  //  折扣(type=3时有值)(0-10)
        input.commissionDiscount = commissionDiscount;  //  佣金百分比(type=3时有值)(0-100)


        input.convertJosn();
        taskIndustryPushMerchantRequest = new TaskIndustryPushMerchantRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("任务发布成功");
                    sendBroadcast(new Intent(Constants.BROCAST_FRESHTASKLIST));
                    setResult(RESULT_OK);
                    PublishTaskActivity.this.finish();

                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(taskIndustryPushMerchantRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_PAY) {
            finish();
        }
    }
}
