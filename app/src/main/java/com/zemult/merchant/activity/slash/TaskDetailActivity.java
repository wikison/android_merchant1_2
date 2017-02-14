package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.zemult.merchant.adapter.slashfrgment.HomeTaskAdapter;
import com.zemult.merchant.adapter.slashfrgment.PlanCommentAdapter;
import com.zemult.merchant.aip.slash.TaskIndustrySendBonuseRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.fragment.HomeFragment;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Comment;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.mvp.presenter.CommentPresenter;
import com.zemult.merchant.mvp.presenter.CompleteTaskPresenter;
import com.zemult.merchant.mvp.presenter.TaskDetailPresenter;
import com.zemult.merchant.mvp.presenter.TaskOrMoodOperatePresenter;
import com.zemult.merchant.mvp.view.ICommentView;
import com.zemult.merchant.mvp.view.ICompleteTaskView;
import com.zemult.merchant.mvp.view.ITaskDetailView;
import com.zemult.merchant.mvp.view.ITaskOrMoodOperateView;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.EmojiParser;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.ShareText;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.UserManager;
import com.zemult.merchant.util.sound.HttpOperateUtil;
import com.zemult.merchant.view.BounceScrollView;
import com.zemult.merchant.view.FixedListView;
import com.zemult.merchant.view.HeaderTaskDetailView;
import com.zemult.merchant.view.SharePopwindow;
import com.zemult.merchant.view.common.CommonDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 任务的详情页
 *
 * @author djy
 * @time 2016/7/26 15:29
 */
public class TaskDetailActivity extends BaseActivity implements ITaskDetailView, ITaskOrMoodOperateView, ICommentView, ICompleteTaskView {

    public static final String INTENT_TASKINDUSTRYRECORDID = "taskIndustryRecordId";
    public static final String INTENT_TAB_POS = "tabPos";
    public static final String INTENT_TASK = "task";
    private static final int LOGIN_REQ = 0x110;
    private static final int ALL_COMMENT_REQ = 0x120;
    public static final int REQ_COMPLETE_DETAIL = 0x130;
    private static final String TAG = TaskDetailActivity.class.getSimpleName();

    ImageView voiceImageBtn;
    AnimationDrawable voiceAnimation;
    TaskIndustrySendBonuseRequest taskIndustrySendBonuseRequest;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    headerView.setProgress(100);
                    stopPlay();
                    break;
                case 2:
                    voiceImageBtn.setImageResource(R.drawable.voice_from_yellow_icon);
                    voiceAnimation = (AnimationDrawable) voiceImageBtn.getDrawable();
                    voiceAnimation.start();
                    break;

                case 3:
                    headerView.startPlay(Integer.valueOf(intent_task.audioTime) * 100);
                    progress();
                    break;
            }
            super.handleMessage(msg);
        }

    };
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
    @Bind(R.id.ll_top_container)
    LinearLayout llTopContainer;
    @Bind(R.id.tv_comment_num)
    TextView tvCommentNum;
    @Bind(R.id.rl_all_comment)
    RelativeLayout rlAllComment;
    @Bind(R.id.lv_comment)
    FixedListView lvComment;
    @Bind(R.id.ll_comment_container)
    LinearLayout llCommentContainer;
    @Bind(R.id.tv_people_num)
    TextView tvPeopleNum;
    @Bind(R.id.ll_more)
    LinearLayout llMore;
    @Bind(R.id.lv_task_complete)
    FixedListView lvTaskComplete;
    @Bind(R.id.ll_task_complete_container)
    LinearLayout llTaskCompleteContainer;
    @Bind(R.id.scrollview)
    BounceScrollView scrollview;
    @Bind(R.id.iv_send_bonuse)
    ImageView ivSendBonuse;
    @Bind(R.id.et_comment)
    EditText etComment;
    @Bind(R.id.rl_comment)
    RelativeLayout rlComment;
    @Bind(R.id.ll_comment)
    LinearLayout llComment;
    @Bind(R.id.iv_star)
    ImageView ivStar;
    @Bind(R.id.ll_star)
    LinearLayout llStar;
    @Bind(R.id.iv_like)
    ImageView ivLike;
    @Bind(R.id.ll_like)
    LinearLayout llLike;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    private Context mContext;
    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {

            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
            popwindow.dismiss();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 分享失败啦", Toast.LENGTH_SHORT).show();
            popwindow.dismiss();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 分享取消了", Toast.LENGTH_SHORT).show();
            popwindow.dismiss();
        }
    };

    private Activity mActivity;
    private HeaderTaskDetailView headerView; // 头部
    private PlanCommentAdapter commentAdapter; // 评论列表适配器
    private HomeTaskAdapter completeAdapter; // 完成列表适配器
    private M_Task intent_task;
    private int taskIndustryRecordId, taskIndustryId;
    private int commentType;
    private int ruserId;
    private boolean hasStar;
    private boolean hasLike;
    private boolean isPlayed;
    private MediaPlayer mMediaPlayer;
    private SharePopwindow popwindow;
    private TaskDetailPresenter detailPresenter;
    private TaskOrMoodOperatePresenter operatePresenter;
    private CommentPresenter commentPresenter;
    private CompleteTaskPresenter completeListPresenter;
    private HomeFragment.HomeEnum homeEnum;

    private int selectedTaskPos, selectedTabPos;
    private M_Task selectedTask;
    private boolean refresh;

    boolean isSendBonuseSuccess = false;
    private String intentFrom = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == LOGIN_REQ)
                getNetworkData();
            if (requestCode == ALL_COMMENT_REQ) {
                commentPresenter.getCommentList(homeEnum, taskIndustryRecordId, 1, 3, false);
                detailPresenter.getTaskDetail(taskIndustryRecordId);
            }
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
            getNetworkData();
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_task_detail);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

        getNetworkData();
    }

    private void initData() {
        intentFrom = getIntent().getStringExtra("intent_from");
        detailPresenter = new TaskDetailPresenter(listJsonRequest, this);
        operatePresenter = new TaskOrMoodOperatePresenter(listJsonRequest, this);
        commentPresenter = new CommentPresenter(listJsonRequest, this);
        completeListPresenter = new CompleteTaskPresenter(listJsonRequest, this);
        intent_task = (M_Task) getIntent().getSerializableExtra(INTENT_TASK);
        selectedTabPos = getIntent().getIntExtra(INTENT_TAB_POS, 0);
        if (intent_task != null) {
            taskIndustryRecordId = intent_task.taskIndustryRecordId;
            taskIndustryId = intent_task.taskIndustryId;
        } else {
            taskIndustryRecordId = getIntent().getIntExtra(INTENT_TASKINDUSTRYRECORDID, 0);
        }

        mContext = this;
        mActivity = this;
        homeEnum = HomeFragment.HomeEnum.TASK;

        registerReceiver(new String[]{Constants.BROCAST_FRESHSLASH});
    }

    private void initView() {
        llRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.gengduo_black_icon);
        lhTvTitle.setText("探索完成详情");
        // 设置其他头部
        headerView = new HeaderTaskDetailView(mActivity);
        headerView.fillView(intent_task, llTopContainer);
        if (intent_task != null) {
            headerView.dealWithTheView(intent_task, selectedTabPos);
            tvCommentNum.setText(intent_task.commentNum + "");
        }

        // 设置ListView数据
        commentAdapter = new PlanCommentAdapter(mContext, new ArrayList<M_Comment>(), selectedTabPos == 3 ? true : false);
        lvComment.setAdapter(commentAdapter);
        completeAdapter = new HomeTaskAdapter(mContext, new ArrayList<M_Task>());
        lvTaskComplete.setAdapter(completeAdapter);

        switch (homeEnum) {
            case TASK:
                llTaskCompleteContainer.setVisibility(View.VISIBLE);
                break;
            case MOOD:
                llTaskCompleteContainer.setVisibility(View.GONE);
                break;
        }
        voiceImageBtn = headerView.getVoiceBtn();

        scrollview.smoothScrollTo(0, 20);

        if (selectedTabPos == 3) {
            llBottom.setVisibility(View.GONE);
            rlComment.setVisibility(View.VISIBLE);
            llTaskCompleteContainer.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        headerView.setOnUserClickListener(new HeaderTaskDetailView.OnUserClickListener() {
            @Override
            public void onUserClick() {
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, intent_task.userId);
                intent.putExtra(UserDetailActivity.USER_NAME, intent_task.userName);
                intent.putExtra(UserDetailActivity.USER_HEAD, intent_task.userHead);
                intent.putExtra(UserDetailActivity.USER_SEX, intent_task.userSex);
                startActivity(intent);
            }
        });
        commentAdapter.setOnItemClickListener(new PlanCommentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                ruserId = commentAdapter.getItem(position).userId;

                if (UserManager.instance().getUserinfo() != null) {

                    // 判断是否是自己的评论
                    if (UserManager.instance().getUserinfo().getUserId() == ruserId) {
                        CommonDialog.showDialogListener(mContext, null, "否", "是", "是否删除评论", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommonDialog.DismissProgressDialog();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommonDialog.DismissProgressDialog();
                                // 用户删除自己的评论
                                commentPresenter.delComment(homeEnum, commentAdapter.getItem(position).commentId);
                            }
                        });
                    } else {
                        etComment.setHint("回复" + commentAdapter.getItem(position).userName + ":");
                        showSoftInputView(1);
                    }
                } else {
                    // 没有登录跳转到登录界面
                    CommonDialog.showDialogListener(mContext, null, "否", "是", getResources().getString(R.string.not_login_tip), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonDialog.DismissProgressDialog();

                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonDialog.DismissProgressDialog();
                            startActivityForResult(new Intent(mContext, LoginActivity.class), LOGIN_REQ);
                        }
                    });
                }
            }
        });

        commentAdapter.setOnHeadClickListener(new PlanCommentAdapter.OnHeadClickListener() {
            @Override
            public void onHeadClick(int position) {
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, commentAdapter.getItem(position).userId);
                intent.putExtra(UserDetailActivity.USER_NAME, commentAdapter.getItem(position).userName);
                intent.putExtra(UserDetailActivity.USER_HEAD, commentAdapter.getItem(position).userHead);
                startActivity(intent);
            }
        });
        commentAdapter.setOnZanClickListener(new PlanCommentAdapter.OnZanClickListener() {
            @Override
            public void onZanClick(int position, int isGood) {
                // 操作用户是否赞过该评论(0:否1:是)
                if (isGood == 0)
                    commentPresenter.addCommentLike(commentAdapter.getItem(position).commentId, position);
                else
                    commentPresenter.cancleCommentLike(commentAdapter.getItem(position).commentId, position);
            }
        });

        etComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (TextUtils.isEmpty(etComment.getText().toString().trim())) {
                        Toast.makeText(mContext, "您还没有评论~", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "发送~~~");
                        commentPresenter.addComment(homeEnum, taskIndustryRecordId, EmojiParser.getInstance(mContext).parseEmoji(etComment.getText().toString()),
                                commentType, ruserId);

                        hideSoftInputView();
                    }
                    return true;
                }
                return false;
            }
        });


        // 添加layout大小发生改变监听器
        llRoot.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // 只要控件将Activity向上推的高度超过了10px，就认为软键盘弹起
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > 10)) {
                    Log.i(TAG, "键盘弹出状态");
                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > 10)) {
                    Log.i(TAG, "键盘收起状态");
                    if (selectedTabPos != 3) {
                        rlComment.setVisibility(View.GONE);
                        llBottom.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        popwindow = new SharePopwindow(mContext, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage;
                if (null != intent_task.pic && intent_task.pic.indexOf(",") != -1) {
                    shareImage = new UMImage(mContext, intent_task.pic.split(",")[0]);
                } else {
                    shareImage = new UMImage(mContext, R.mipmap.icon_share);
                }
                switch (position) {
                    case SharePopwindow.WECHAT:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText(intent_task.note)
                                .withTargetUrl(SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_TASK, "http://server.54xiegang.com/yongyou/app/share_taskIndustryRecord.do?id=") + taskIndustryRecordId)
                                .withMedia(shareImage).withTitle("任务记录")
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText(intent_task.note)
                                .withTargetUrl(SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_TASK, "http://server.54xiegang.com/yongyou/app/share_taskIndustryRecord.do?id=") + taskIndustryRecordId)
                                .withMedia(shareImage).withTitle("任务记录")
                                .share();
                        break;
                }
            }
        });


        //语音播放
        headerView.setOnVioceClickListener(new HeaderTaskDetailView.OnVioceClickListener() {
            @Override
            public void onVioceClick() {
                if (isPlayed) {
                    stopPlay();
                } else {
                    startPlay();
                }
            }
        });

        ivSendBonuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDialog.showDialogListener(mContext, null, "取消", "确定", "手动给此用户分配红包", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendBonuse();
                        CommonDialog.DismissProgressDialog();

                    }
                });
            }
        });


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

    }


    private void sendBonuse() {

        if (taskIndustrySendBonuseRequest != null) {
            taskIndustrySendBonuseRequest.cancel();
        }

        final TaskIndustrySendBonuseRequest.Input input = new TaskIndustrySendBonuseRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.taskIndustryRecordId = taskIndustryRecordId;
        input.convertJosn();

        showPd();
        taskIndustrySendBonuseRequest = new TaskIndustrySendBonuseRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                int status = ((CommonResult) response).status;
                if (status == 1) {
                    dismissPd();
                    ivSendBonuse.setVisibility(View.GONE);
                    isSendBonuseSuccess = true;
                    ToastUtil.showMessage("成功手动分配红包");
                } else {
                    dismissPd();
                    isSendBonuseSuccess = false;
                    ivSendBonuse.setVisibility(View.VISIBLE);
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(taskIndustrySendBonuseRequest);
    }

    public void startPlay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                // TODO Auto-generated method stub
                String fileUrl = intent_task.audio;

                String fileName = HttpOperateUtil.downLoadFile(fileUrl,
                        fileUrl.substring(fileUrl.lastIndexOf("/") + 1));

                Log.i("audiotask", "fileName = " + fileName);
                File file = new File(fileName);

                if (!file.exists()) {
                    Toast.makeText(TaskDetailActivity.this, "没有语音文件！", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                isPlayed = true;
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
                mMediaPlayer = MediaPlayer.create(TaskDetailActivity.this,
                        Uri.parse(fileName));
                mMediaPlayer.setLooping(false);
                mMediaPlayer.start();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                });

                handler.sendEmptyMessage(3);

                Looper.loop();
            }
        }).start();
    }

    public void stopPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        if (voiceAnimation != null) {
            voiceImageBtn.setImageResource(R.mipmap.yuyan_icon);
            voiceAnimation.stop();
        }
        isPlayed = false;
        headerView.stopPlay();
    }

    // 进度条
    private void progress() {

        final Handler handler = new Handler() {
            int i = 0;

            public void handleMessage(Message msg) {
                i++;
                headerView.setProgress(i);
            }
        };

        Thread mThread = new Thread() {
            @Override
            public void run() {
                while (isPlayed) {
                    try {
                        handler.sendEmptyMessage(0);
                        sleep(9);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mThread.start();
    }

    /**
     * 访问网络接口
     */
    private void getNetworkData() {
        detailPresenter.getTaskDetail(taskIndustryRecordId);
        commentPresenter.getCommentList(homeEnum, taskIndustryRecordId, 1, 3, false);
        if (selectedTabPos != 3)
            completeListPresenter.task_industry_recordList_other_1_2(taskIndustryRecordId, 1, 1, 3, false);
    }

    @Override
    protected void onDestroy() {
        hideSoftInputView();
        stopPlay();
        isPlayed = false;
        super.onDestroy();
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
    public void showError(String error) {
        ToastUtils.show(mContext, error);
    }

    @Override
    public void setCompleteList(List<M_Task> list, boolean isLoadMore, int maxpage) {
        if (list != null) {
            completeAdapter.setData(list, false);
        }
    }

    @Override
    public void setTaskDetailInfo(M_Task taskDetailInfo) {
        if (refresh) {
            refresh = false;
            completeAdapter.refreshOneRecord(taskDetailInfo, selectedTaskPos);
        } else {
            // 其他
            headerView.dealWithTheView(taskDetailInfo, selectedTabPos);
            tvCommentNum.setText(taskDetailInfo.commentNum + "");

            // 操作用户是否收藏该方案(0:否1:是)---操作用户id无值时默认为0
            if (taskDetailInfo.isFavorite == 0) {
                ivStar.setImageResource(R.mipmap.star_btn);
                hasStar = false;
            } else {
                ivStar.setImageResource(R.mipmap.star_btn_sel);
                hasStar = true;
            }

            // 操作用户是否赞过该方案(0:否1:是)---操作用户id无值时默认为0
            if (taskDetailInfo.isGood == 0) {
                ivLike.setImageResource(R.mipmap.zan_icon_black);
                hasLike = false;
            } else {
                ivLike.setImageResource(R.mipmap.zan_icon_sel);
                hasLike = true;
            }

            if (taskDetailInfo.isSendBonuse == 0) {
                ivSendBonuse.setVisibility(View.GONE);
            } else if (taskDetailInfo.isSendBonuse == 1) {
                ivSendBonuse.setVisibility(View.VISIBLE);
            }
            tvPeopleNum.setText(taskDetailInfo.completeNum + "人参与");

            intent_task = taskDetailInfo;
        }
    }

    @Override
    public void setCommentList(List<M_Comment> list, boolean isLoadMore, int maxpage) {
        if (list != null) {
            commentAdapter.setData(list, false);
        }
    }

    @Override
    public void addLikeSuccess() {
        // 显示赞 实心的图标
        ivLike.setImageResource(R.mipmap.zan_icon_sel);
        hasLike = true;
        detailPresenter.getTaskDetail(taskIndustryRecordId);
    }

    @Override
    public void cancleLikeSuccess() {
        // 显示赞 空心的图标
        ivLike.setImageResource(R.mipmap.zan_icon_nor);
        hasLike = false;
        detailPresenter.getTaskDetail(taskIndustryRecordId);
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
    public void addCommentLikeSuccess(int posistion) {
        commentAdapter.commentLike(posistion, 1);
    }

    @Override
    public void cancleCommentLikeSuccess(int posistion) {
        commentAdapter.commentLike(posistion, 0);
    }

    @Override
    public void addCommentSuccess() {
        etComment.setText("");
        commentPresenter.getCommentList(homeEnum, taskIndustryRecordId, 1, 3, false);
        detailPresenter.getTaskDetail(taskIndustryRecordId);
    }

    @Override
    public void delCommentSuccess() {
        commentPresenter.getCommentList(homeEnum, taskIndustryRecordId, 1, 3, false);
        detailPresenter.getTaskDetail(taskIndustryRecordId);
    }

    @Override
    public void stopRefreshOrLoad() {
        // do nothing
    }

    @OnClick({R.id.ll_back, R.id.ll_comment, R.id.ll_star, R.id.ll_like, R.id.lh_btn_back, R.id.rl_all_comment, R.id.ll_more, R.id.ll_right,
            R.id.iv_right})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.rl_all_comment:
                Intent intent = new Intent(mContext, AllCommentActivity.class);
                intent.putExtra(AllCommentActivity.INTENT_TASK_INDUSTRY_RECORD_ID, taskIndustryRecordId);
                intent.putExtra(AllCommentActivity.INTENT_TAB_POS, selectedTabPos);
                startActivityForResult(intent, ALL_COMMENT_REQ);
                break;
            case R.id.ll_more:
                Intent intent2 = new Intent(mContext, MoreDiscoverActivity.class);
                intent2.putExtra(AllTaskCompleteActivity.INTENT_TASK_INDUSTRY_RECORD_ID, taskIndustryRecordId);
                startActivity(intent2);
                break;
            case R.id.ll_comment:
                operate(OperateType.COMMENT);
                break;
            case R.id.ll_star:
                operate(OperateType.STAR);
                break;
            case R.id.ll_like:
                operate(OperateType.LIKE);
                break;
            case R.id.ll_right:
            case R.id.iv_right:
                showPopupWindow(mContext, view);
                break;
        }
    }

    private void operate(OperateType type) {
        if (type == OperateType.SHARE) {
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
                    startActivityForResult(new Intent(mContext, LoginActivity.class), LOGIN_REQ);
                }
            });
            return;
        }
        // 判断类型
        switch (type) {
            case COMMENT:
                if (rlComment.getVisibility() == View.VISIBLE) {
                    rlComment.setVisibility(View.GONE);
                    llBottom.setVisibility(View.VISIBLE);
                } else {
                    etComment.setHint("写评论...");
                    showSoftInputView(0);
                }
                break;

            case STAR:
                if (hasStar)
                    operatePresenter.cancleStar(homeEnum, taskIndustryRecordId);
                else
                    operatePresenter.addStar(homeEnum, taskIndustryRecordId);
                break;

            case LIKE:
                if (hasLike)
                    operatePresenter.cancleLike(homeEnum, taskIndustryRecordId);
                else
                    operatePresenter.addLike(homeEnum, taskIndustryRecordId);
                break;

            case REPORT:
                IntentUtil.intStart_activity(mActivity,
                        ReportActivity.class,
                        new Pair<String, Integer>(ReportActivity.INTENT_INFO_ID, taskIndustryRecordId),
                        new Pair<String, Integer>(ReportActivity.INTENT_INFO_TYPE, 2));
                break;
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 弹出软键盘
     *
     * @param type 0:对信息评论;1:回复别人的评论
     */
    private void showSoftInputView(int type) {
        commentType = type;
        rlComment.setVisibility(View.VISIBLE);
        llBottom.setVisibility(View.GONE);
        // 弹出软键盘
        etComment.setFocusable(true);
        etComment.setFocusableInTouchMode(true);
        etComment.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) etComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(etComment, 0);
    }

    @Override
    public void onBackPressed() {
        if ("MyPublishTaskDetail".equals(intentFrom)) {
            if (isSendBonuseSuccess) {
                setResult(RESULT_OK);
            } else {
            }
        } else {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    enum OperateType {
        SHARE, COMMENT, STAR, LIKE, REPORT, BLACK
    }

    private void showPopupWindow(final Context context, View rightButton) {
        //设置contentView
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_conversationhead, null);
        final PopupWindow mPopWindow = new PopupWindow(contentView,
                DensityUtil.dip2px(context, 120), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopWindow.setOutsideTouchable(true);

        TextView tv1 = (TextView) contentView.findViewById(R.id.tv1);
        TextView tv2 = (TextView) contentView.findViewById(R.id.tv2);
        ImageView iv1 = (ImageView) contentView.findViewById(R.id.iv1);
        ImageView iv2 = (ImageView) contentView.findViewById(R.id.iv2);
        LinearLayout l1 = (LinearLayout) contentView.findViewById(R.id.l1);
        LinearLayout l2 = (LinearLayout) contentView.findViewById(R.id.l2);

        tv1.setText("分享");
        tv2.setText("举报");
        iv1.setImageResource(R.mipmap.fengxiang_white_icon);
        iv2.setImageResource(R.mipmap.jubao_white_icon);
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operate(OperateType.SHARE);
                mPopWindow.dismiss();
            }
        });
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operate(OperateType.REPORT);
                mPopWindow.dismiss();
            }
        });
        //显示PopupWindow
        mPopWindow.showAsDropDown(rightButton, -155, -26);
    }


}