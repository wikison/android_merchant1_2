package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.LoginActivity;
import com.zemult.merchant.activity.ReportActivity;
import com.zemult.merchant.activity.slash.dotask.NewDoTaskPicActivity;
import com.zemult.merchant.activity.slash.dotask.NewDoTaskVoteActivity;
import com.zemult.merchant.adapter.slashfrgment.HomeTaskAdapter;
import com.zemult.merchant.aip.task.TaskCommissionShareFriend_1_3Request;
import com.zemult.merchant.aip.task.TaskIndustryCommissionComplete_1_3Request;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.mvp.presenter.CompleteTaskPresenter;
import com.zemult.merchant.mvp.presenter.SearchDetailOperatePresenter;
import com.zemult.merchant.mvp.presenter.TaskDetailPresenter;
import com.zemult.merchant.mvp.view.ICompleteTaskView;
import com.zemult.merchant.mvp.view.ISearchDetailOperateView;
import com.zemult.merchant.mvp.view.ITaskDetailView;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.ShareText;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.FixedListView;
import com.zemult.merchant.view.SharePopwindow;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 探索详情
 *
 * @author djy
 * @time 2016/9/7 10:02
 */
public class SearchDetailActivity extends BaseActivity implements ICompleteTaskView, ITaskDetailView, ISearchDetailOperateView {

    public static final String INTENT_TASK = "task";
    public static final String INTENT_TASKINDUSTRYID = "taskIndustryId";
    private static final String TAG = SearchDetailActivity.class.getSimpleName();
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_sub_title)
    TextView tvSubTitle;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_note)
    TextView tvNote;
    @Bind(R.id.tv_exp)
    TextView tvExp;
    @Bind(R.id.tv_bonuses)
    TextView tvBonuses;
    @Bind(R.id.tv_voucher)
    TextView tvVoucher;
    @Bind(R.id.tv_people_num)
    TextView tvPeopleNum;
    @Bind(R.id.ll_more)
    LinearLayout llMore;
    @Bind(R.id.listView)
    FixedListView listView;
    @Bind(R.id.ll_user)
    LinearLayout llUser;
    @Bind(R.id.textView)
    TextView textView;
    @Bind(R.id.ll_star)
    LinearLayout llStar;
    @Bind(R.id.ll_search)
    LinearLayout llSearch;
    @Bind(R.id.iv_star)
    ImageView ivStar;
    @Bind(R.id.tv_discount)
    TextView tvDiscount;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.tv_quan)
    TextView tvQuan;

    private Context mContext;
    private Activity mActivity;
    private M_Task intent_task;
    private int taskIndustryId;
    private HomeTaskAdapter completeAdapter;
    private CompleteTaskPresenter completeListPresenter;
    private TaskDetailPresenter detailPresenter;
    private SearchDetailOperatePresenter operatePresenter;
    TaskCommissionShareFriend_1_3Request taskCommissionShareFriend_1_3Request;
    TaskIndustryCommissionComplete_1_3Request taskIndustryCommissionComplete_1_3Request;
    String friendIds;


    public static final int REQ_COMPLETE_DETAIL = 0x110;
    private int selectedTaskPos;
    private M_Task selectedTask;
    private boolean refresh, hasStar;
    private SharePopwindow popwindow;
    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(mContext, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_search_detail);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        showPd();
        detailPresenter.task_industry_info(taskIndustryId);
        completeListPresenter.task_industry_recordList_task_1_2(taskIndustryId, 1, 1, 3, false);
    }


    private void initData() {
        mContext = this;
        mActivity = this;
        completeListPresenter = new CompleteTaskPresenter(listJsonRequest, this);
        detailPresenter = new TaskDetailPresenter(listJsonRequest, this);
        operatePresenter = new SearchDetailOperatePresenter(listJsonRequest, this);
        taskIndustryId=getIntent().getIntExtra(INTENT_TASKINDUSTRYID,0);
        intent_task = (M_Task) getIntent().getSerializableExtra(INTENT_TASK);
        if (intent_task != null) {
            taskIndustryId = intent_task.taskIndustryId;
            setData(intent_task);
        }

        registerReceiver(new String[]{Constants.BROCAST_FRESHSLASH, Constants.BROCAST_PUBLISH_TASK_PERSON});
    }

    private void initView() {
        llRight.setVisibility(View.VISIBLE);
        lhTvTitle.setText("探索详情");
        ivRight.setImageResource(R.mipmap.jubao_icon);

        completeAdapter = new HomeTaskAdapter(mContext, new ArrayList<M_Task>());

        if (intent_task!=null&&intent_task.pushType== 1) {
            tvDiscount.setVisibility(View.VISIBLE);
            completeAdapter.showActivity(true);
        }

        listView.setAdapter(completeAdapter);
    }

    private void initListener() {
        // 方案详情
        completeAdapter.setOnPlanDetailClickListener(new HomeTaskAdapter.OnPlanDetailClickListener() {
            @Override
            public void onPlanDetailClick(int position) {
                selectedTaskPos = position;
                selectedTask = completeAdapter.getItem(position);

                M_Task task = completeAdapter.getItem(position);
                Intent intent = new Intent(mContext, TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.INTENT_TASK, task);
                startActivityForResult(intent, REQ_COMPLETE_DETAIL);
            }
        });
        // 用户详情
        completeAdapter.setOnUserDetailClickListener(new HomeTaskAdapter.OnUserDetailClickListener() {
            @Override
            public void onUserDetailClick(int position) {
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, completeAdapter.getItem(position).userId);
                intent.putExtra(UserDetailActivity.USER_NAME, completeAdapter.getItem(position).userName);
                intent.putExtra(UserDetailActivity.USER_HEAD, completeAdapter.getItem(position).userHead);
                intent.putExtra(UserDetailActivity.USER_SEX, completeAdapter.getItem(position).userSex);
                startActivity(intent);
            }
        });

        popwindow = new SharePopwindow(mContext, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage;
                if (null != intent_task.pic && intent_task.pic.indexOf(",") != -1) {
                    shareImage = new UMImage(mContext, intent_task.pic.split(",")[0]);
                } else {
                    shareImage = new UMImage(mContext, R.mipmap.icon_launcher);
                }
                switch (position) {
                    case SharePopwindow.WECHAT:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText(intent_task.note)
                                .withTargetUrl(SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_COMMISSION, "http://server.54xiegang.com/yongyou/wappay/wappay_index.do?taskIndustryRecordId=?id") + taskIndustryId)
                                .withMedia(shareImage).withTitle("消费任务")
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText(intent_task.note)
                                .withTargetUrl(SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_COMMISSION, "http://server.54xiegang.com/yongyou/wappay/wappay_index.do?taskIndustryRecordId=?id") + taskIndustryId)
                                .withMedia(shareImage).withTitle("消费任务")
                                .share();
                        break;
                    case SharePopwindow.RESEND:
                        IntentUtil.start_activity(SearchDetailActivity.this, PublishTaskFriendActivity.class,
                                new Pair<String, String>("requesttype", Constants.BROCAST_PUBLISH_TASK_PERSON));
                        break;
                }
            }
        });
        if (intent_task!=null&&intent_task.pushType == 1)
            popwindow.showResend();
    }

    private void setData(M_Task entity) {
        // 奖励栏 探索奖励方式(0:无,1:红包,2:代金券)
        switch (entity.cashType) {
            case 1:
                tvBonuses.setVisibility(View.VISIBLE);
                break;
            case 2:
                tvQuan.setVisibility(View.VISIBLE);
                break;
        }
        tvExp.setText(String.format("经验X%s", String.valueOf(entity.experience)));

        if (entity.pushType == 1) {
            // 用户头像
            if (!TextUtils.isEmpty(entity.merchantHead))
                imageManager.loadCircleImage(entity.merchantHead, ivHead);
            else
                ivHead.setImageResource(R.mipmap.user_icon);
            // 用户名
            if (!TextUtils.isEmpty(entity.merchantName))
                tvUserName.setText(entity.merchantName);
        } else {
            // 用户头像
            if (!TextUtils.isEmpty(entity.userHead))
                imageManager.loadCircleImage(entity.userHead, ivHead);
            else
                ivHead.setImageResource(R.mipmap.user_icon);
            // 用户名
            if (!TextUtils.isEmpty(entity.userName))
                tvUserName.setText(entity.userName);
        }

        // 任务标题
        if (!TextUtils.isEmpty(entity.title))
            tvTitle.setText(entity.title);
        // 角色名字
        if (!TextUtils.isEmpty(entity.industryName))
            tvSubTitle.setText(entity.industryName + "de探索");
        // 截止时间
        if (!TextUtils.isEmpty(entity.endtime))
            tvTime.setText(DateTimeUtil.strPubEndDiffTime(entity.endtime) + "结束");
        // 内容
        if (!TextUtils.isEmpty(entity.note))
            tvNote.setText(entity.note);

        tvPeopleNum.setText(entity.completeNum + "人参与");
        if (entity.completeNum == 0)
            llMore.setVisibility(View.GONE);
        else
            llMore.setVisibility(View.VISIBLE);

        // 操作用户是否收藏该方案(0:否1:是)---操作用户id无值时默认为0
        if (entity.isFavorite == 0) {
            ivStar.setImageResource(R.mipmap.star_btn);
            hasStar = false;
        } else {
            ivStar.setImageResource(R.mipmap.star_btn_sel);
            hasStar = true;
        }
        // 佣金百分比(FType=3时有值)(0-100)
        tvDiscount.setText("交易任务佣金" + entity.commissionDiscount + "%");
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_right, R.id.ll_right, R.id.ll_more, R.id.ll_user, R.id.ll_search, R.id.ll_star})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_right:
            case R.id.ll_right:
                operate(TaskDetailActivity.OperateType.REPORT);

                break;
            case R.id.ll_more:
                intent = new Intent(mContext, MoreDiscoverActivity.class);
                intent.putExtra(MoreDiscoverActivity.TASKID, taskIndustryId);
                intent.putExtra(MoreDiscoverActivity.TYPE, intent_task.pushType);
                startActivity(intent);
                break;
            case R.id.ll_user:
                intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, intent_task.userId);
                intent.putExtra(UserDetailActivity.USER_NAME, intent_task.userName);
                intent.putExtra(UserDetailActivity.USER_HEAD, intent_task.userHead);
                intent.putExtra(UserDetailActivity.USER_SEX, intent_task.userSex);
                startActivity(intent);
                break;
            case R.id.ll_search:
                if (intent_task.state == 1) {
                   ToastUtil.showMessage("该探索已结束！");
                    return;
                }
                if (intent_task.userId == SlashHelper.userManager().getUserId()) {
                    ToastUtil.showMessage("不能做自己发布的探索！");
                    return;
                }
                // 没有登录跳转到登录界面
                if (SlashHelper.userManager().getUserinfo() == null) {
                    CommonDialog.showDialogListener(mContext, null, "否", "是", getResources().getString(R.string.not_login_tip), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonDialog.DismissProgressDialog();

                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonDialog.DismissProgressDialog();
                            startActivity(new Intent(mContext, LoginActivity.class));
                        }
                    });
                    return;
                }
                if (intent_task.pushType == 1) {
                    operate(TaskDetailActivity.OperateType.SHARE);
                    return;
                }
                //用户是否完成该任务(0:否1:是)
                if (intent_task.isComplete == 0) {
                    if (null != intent_task.voteList && intent_task.voteList.size() > 0) {
                        Intent intent2 = new Intent(mContext, NewDoTaskVoteActivity.class);
                        intent2.putExtra(TaskDetailActivity.INTENT_TASK, intent_task);
                        startActivity(intent2);
                    } else {
                        Intent intent3 = new Intent(mContext, NewDoTaskPicActivity.class);
                        intent3.putExtra(TaskDetailActivity.INTENT_TASK, intent_task);
                        startActivity(intent3);
                    }
                } else {
                    ToastUtil.showMessage("您已经完成该探索");
                }

                break;
            case R.id.ll_star:
                operate(TaskDetailActivity.OperateType.STAR);
                break;
        }
    }

    @Override
    public void showError(String error) {
        ToastUtil.showMessage(error);
    }

    @Override
    public void setTaskDetailInfo(M_Task taskIndustryInfo) {
        if (taskIndustryInfo != null) {
            if (refresh) {
                refresh = false;
                completeAdapter.refreshOneRecord(taskIndustryInfo, selectedTaskPos);
            } else {
                intent_task = taskIndustryInfo;
                setData(taskIndustryInfo);
            }

        }
    }

    @Override
    public void showProgressDialog() {
        showPd();
    }

    @Override
    public void hideProgressDialog() {
        dismissPd();
    }


    @Override
    public void addStarSuccess() {
        // 显示收藏 实心的图标
        ivStar.setImageResource(R.mipmap.star_btn_sel);
        hasStar = true;
    }

    @Override
    public void cancleStarSuccess() {
        // 显示收藏 空心的图标
        ivStar.setImageResource(R.mipmap.star_btn);
        hasStar = false;
    }

    @Override
    public void setCompleteList(List<M_Task> list, boolean isLoadMore, int maxpage) {
        if (list != null) {
            completeAdapter.setData(list, false);
        }
    }

    @Override
    public void stopRefreshOrLoad() {
        dismissPd();
    }

    private void operate(TaskDetailActivity.OperateType type) {
        if (type == TaskDetailActivity.OperateType.SHARE) {
            if (popwindow.isShowing())
                popwindow.dismiss();
            else
                popwindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0); //设置layout在PopupWindow中显示的位置
            return;
        }
        // 没有登录跳转到登录界面
        if (SlashHelper.userManager().getUserinfo() == null) {
            CommonDialog.showDialogListener(mContext, null, "否", "是", getResources().getString(R.string.not_login_tip), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonDialog.DismissProgressDialog();

                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonDialog.DismissProgressDialog();
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
            });
            return;
        }
        // 判断类型
        switch (type) {
            case REPORT:
                IntentUtil.intStart_activity(mActivity,
                        ReportActivity.class,
                        new Pair<String, Integer>(ReportActivity.INTENT_INFO_ID, taskIndustryId),
                        new Pair<String, Integer>(ReportActivity.INTENT_INFO_TYPE, 1));
                break;
            case STAR:
                if (hasStar)
                    operatePresenter.cancleSearchDetailStar(taskIndustryId);
                else
                    operatePresenter.addSearchDetailStar(taskIndustryId);
                break;
        }
    }

    /**
     * 刷新
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_COMPLETE_DETAIL) {
                refresh = true;
                detailPresenter.getTaskDetail(selectedTask.taskIndustryRecordId);
            }
        }
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_FRESHSLASH.equals(intent.getAction())) {
            detailPresenter.task_industry_info(taskIndustryId);
            completeListPresenter.task_industry_recordList_task_1_2(taskIndustryId, 1, 1, 3, false);
        }
        if (Constants.BROCAST_PUBLISH_TASK_PERSON.equals(intent.getAction())) {
            friendIds = intent.getStringExtra("friend_ids");
            int friendNum = intent.getIntExtra("friend_num", 0);
            if (friendNum > 0) {
                taskIndustryCommissionComplete_1_3Request();
            }
        }
    }

    private void taskCommissionShareFriend_1_3Request(int taskIndustryRecordId) {
        if (taskCommissionShareFriend_1_3Request != null) {
            taskCommissionShareFriend_1_3Request.cancel();
        }
        TaskCommissionShareFriend_1_3Request.Input input = new TaskCommissionShareFriend_1_3Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.taskIndustryRecordId = taskIndustryRecordId;       //		是	角色id
        input.friends = friendIds;        //	    是	描述
        input.convertJosn();
        taskCommissionShareFriend_1_3Request = new TaskCommissionShareFriend_1_3Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(mContext, "分享成功");
                    popwindow.dismiss();
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(taskCommissionShareFriend_1_3Request);
    }

    private void taskIndustryCommissionComplete_1_3Request() {
        if (taskIndustryCommissionComplete_1_3Request != null) {
            taskIndustryCommissionComplete_1_3Request.cancel();
        }
        TaskIndustryCommissionComplete_1_3Request.Input input = new TaskIndustryCommissionComplete_1_3Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.taskIndustryId = taskIndustryId;       //		是	角色id
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.convertJosn();
        taskIndustryCommissionComplete_1_3Request = new TaskIndustryCommissionComplete_1_3Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    taskCommissionShareFriend_1_3Request(((CommonResult) response).taskIndustryRecordId);
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(taskIndustryCommissionComplete_1_3Request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
