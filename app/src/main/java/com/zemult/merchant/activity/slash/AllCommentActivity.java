package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.LoginActivity;
import com.zemult.merchant.activity.ReportActivity;
import com.zemult.merchant.adapter.slashfrgment.PlanCommentAdapter;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.fragment.HomeFragment;
import com.zemult.merchant.model.M_Comment;
import com.zemult.merchant.mvp.presenter.CommentPresenter;
import com.zemult.merchant.mvp.view.ICommentView;
import com.zemult.merchant.util.EmojiParser;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.UserManager;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.zemult.merchant.view.common.CommonDialog;
import com.zemult.merchant.view.common.MMAlert;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;


/**
 * 全部评论页
 *
 * @author djy
 * @time 2016/7/29 13:50
 */
public class AllCommentActivity extends BaseActivity implements ICommentView, SmoothListView.ISmoothListViewListener {


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
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.et_comment)
    EditText etComment;
    @Bind(R.id.rl_comment)
    RelativeLayout rlComment;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    private int page;
    private List<M_Comment> commentList = new ArrayList<>(); // ListView数据
    private PlanCommentAdapter commentAdapter; // 评论列表适配器
    private Context mContext;
    private Activity mActivity;

    private int newsId;
    private int taskIndustryRecordId;
    public static final String INTENT_NEWS_ID = "newsId";
    public static final String INTENT_TASK_INDUSTRY_RECORD_ID = "taskIndustryRecordId";
    public static final String INTENT_TAB_POS = "pos";
    private static final String TAG = AllCommentActivity.class.getSimpleName();

    private CommentPresenter commentPresenter;
    HomeFragment.HomeEnum homeEnum;
    private int detailId;
    private int ruserId;
    private int commentType;
    private int selectedPos, tabPos;
    private InputMethodManager inputManager;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_all_comment);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

        getNetworkData();
    }

    private void initData() {
        mContext = this;
        mActivity = this;
        newsId = getIntent().getIntExtra(INTENT_NEWS_ID, 0);
        taskIndustryRecordId = getIntent().getIntExtra(INTENT_TASK_INDUSTRY_RECORD_ID, 0);
        tabPos = getIntent().getIntExtra(INTENT_TAB_POS, 0);
        commentPresenter = new CommentPresenter(listJsonRequest, this);
        if (newsId != 0) {
            homeEnum = HomeFragment.HomeEnum.MOOD;
            detailId = newsId;
        } else if (taskIndustryRecordId != 0) {
            homeEnum = HomeFragment.HomeEnum.TASK;
            detailId = taskIndustryRecordId;
        }
        inputManager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
    }

    private void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setText("全部评论");
        // 设置ListView数据
        commentAdapter = new PlanCommentAdapter(mContext, commentList, tabPos == 3 ? true : false);
        smoothListView.setAdapter(commentAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
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
                                selectedPos = position;
                                // 用户删除自己的评论
                                commentPresenter.delComment(homeEnum, commentAdapter.getItem(position).commentId);
                            }
                        });
                    } else {
                        MMAlert.showCommentDialog(mContext, new MMAlert.CommentCallback() {
                            @Override
                            public void onReply() {
                                etComment.setHint("回复" + commentAdapter.getItem(position).userName + ":");
                            }

                            @Override
                            public void onReport() {
                                IntentUtil.intStart_activity(mActivity,
                                        ReportActivity.class,
                                        new Pair<String, Integer>(ReportActivity.INTENT_INFO_ID, detailId),
                                        new Pair<String, Integer>(ReportActivity.INTENT_INFO_TYPE, homeEnum == HomeFragment.HomeEnum.TASK ? 4 : 5));
                            }
                        });
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
                            startActivity(new Intent(mContext, LoginActivity.class));
                        }
                    });
                }
            }
        });
        commentAdapter.setOnZanClickListener(new PlanCommentAdapter.OnZanClickListener() {
            @Override
            public void onZanClick(int position, int isGood) {
                // 操作用户是否赞过该评论(0:否1:是)
                if(isGood == 0)
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
                        if(etComment.getHint().toString().contains("回复"))
                            commentType = 1;
                        else
                            commentType = 0;
                        commentPresenter.addComment(homeEnum, detailId, EmojiParser.getInstance(mContext).parseEmoji(etComment.getText().toString()),
                                commentType, ruserId);

                        etComment.setHint("写评论...");
                        hideSoftInputView();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void getNetworkData() {
        commentPresenter.getCommentList(homeEnum, detailId, page = 1, Constants.ROWS, false);
    }

    @Override
    public void onRefresh() {
        commentPresenter.getCommentList(homeEnum, detailId, page = 1, Constants.ROWS, false);
    }

    @Override
    public void onLoadMore() {
        commentPresenter.getCommentList(homeEnum, detailId, ++page, Constants.ROWS, true);
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
    public void setCommentList(List<M_Comment> commentList, boolean isLoadMore, int maxPage) {
        if (commentList != null && !commentList.isEmpty()) {
            smoothListView.setLoadMoreEnable(page < maxPage);
            commentAdapter.setData(commentList, isLoadMore);
            if (!isLoadMore)
                smoothListView.setSelection(0);
        }
    }

    @Override
    public void addCommentSuccess() {
        etComment.setText("");
        getNetworkData();
    }

    @Override
    public void delCommentSuccess() {
        commentAdapter.delComment(selectedPos);
    }

    @Override
    public void stopRefreshOrLoad() {
        smoothListView.stopLoadMore();
        smoothListView.stopRefresh();
    }

    @Override
    public void addCommentLikeSuccess(int posistion) {
        commentAdapter.commentLike(posistion, 1);
    }

    @Override
    public void cancleCommentLikeSuccess(int posistion) {
        commentAdapter.commentLike(posistion, 0);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
