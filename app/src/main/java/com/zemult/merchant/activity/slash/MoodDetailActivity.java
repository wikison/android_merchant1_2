package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.LoginNewActivity;
import com.zemult.merchant.activity.ReportActivity;
import com.zemult.merchant.adapter.slashfrgment.PlanCommentAdapter;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.fragment.HomeFragment;
import com.zemult.merchant.model.M_Comment;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.mvp.presenter.CommentPresenter;
import com.zemult.merchant.mvp.presenter.MoodDetailPresenter;
import com.zemult.merchant.mvp.presenter.TaskOrMoodOperatePresenter;
import com.zemult.merchant.mvp.view.ICommentView;
import com.zemult.merchant.mvp.view.IMoodDetailView;
import com.zemult.merchant.mvp.view.ITaskOrMoodOperateView;
import com.zemult.merchant.util.EmojiParser;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SelectFaceHelper;
import com.zemult.merchant.util.ShareText;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.UserManager;
import com.zemult.merchant.view.FixedListView;
import com.zemult.merchant.view.HeaderMoodDetailView;
import com.zemult.merchant.view.SharePopwindow;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;

/**
 * 心情的详情页
 *
 * @author djy
 * @time 2016/7/26 15:29
 */
public class MoodDetailActivity extends BaseActivity implements IMoodDetailView, ITaskOrMoodOperateView, ICommentView {


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
    @Bind(R.id.iv_commentexpression)
    ImageButton ivCommentexpression;
    @Bind(R.id.et_comment)
    EditText etComment;
    @Bind(R.id.rl_comment)
    RelativeLayout rlComment;
    @Bind(R.id.vp_emoji)
    ViewPager vpEmoji;
    @Bind(R.id.msg_face_index_view)
    LinearLayout msgFaceIndexView;
    @Bind(R.id.ll_emoji)
    LinearLayout llEmoji;
    @Bind(R.id.ll_share)
    LinearLayout llShare;
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
    @Bind(R.id.ll_right)
    LinearLayout llRight;

    private Context mContext;
    private Activity mActivity;

    private HeaderMoodDetailView headerView; // 头部
    private List<M_Comment> commentList = new ArrayList<>(); // ListView数据
    private PlanCommentAdapter commentAdapter; // 评论列表适配器

    private static final int LOGIN_REQ = 0x110;
    private static final int ALL_COMMENT_REQ = 0x120;
    private static final String TAG = MoodDetailActivity.class.getSimpleName();
    public static final String INTENT_NEWS = "news";
    private M_News intent_news;
    private int detailId;
    private int commentType;
    private int ruserId;
    private boolean hasStar;
    private boolean hasLike;

    private SharePopwindow popwindow;
    private boolean showEmojiTool;
    private SelectFaceHelper mFaceHelper;

    private MoodDetailPresenter detailPresenter;
    private TaskOrMoodOperatePresenter operatePresenter;
    private CommentPresenter commentPresenter;

    private HomeFragment.HomeEnum homeEnum;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == LOGIN_REQ)
//                getNetworkData();
            if (requestCode == ALL_COMMENT_REQ)
                commentPresenter.getCommentList(homeEnum, detailId, 1, 3, false);
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_mood_detail);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

        getNetworkData();
    }

    private void initData() {
        detailPresenter = new MoodDetailPresenter(listJsonRequest, this);
        operatePresenter = new TaskOrMoodOperatePresenter(listJsonRequest, this);
        commentPresenter = new CommentPresenter(listJsonRequest, this);
        intent_news = (M_News) getIntent().getSerializableExtra(INTENT_NEWS);

        mContext = this;
        mActivity = this;

        homeEnum = HomeFragment.HomeEnum.MOOD;
        detailId = intent_news.newsId;
        mFaceHelper = new SelectFaceHelper(mContext, llEmoji);
    }

    private void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhBtnBack.setVisibility(View.VISIBLE);
        llRight.setVisibility(View.VISIBLE);
        lhTvTitle.setText(getResources().getString(R.string.detail));
        // 设置其他头部
        headerView = new HeaderMoodDetailView(mActivity);
        headerView.fillView(intent_news, llTopContainer);
        headerView.dealWithTheView(intent_news);
        tvCommentNum.setText(intent_news.commentNum + "");
        // 设置ListView数据
        commentAdapter = new PlanCommentAdapter(mContext, commentList);
        lvComment.setAdapter(commentAdapter);
    }

    private void initListener() {
        headerView.setOnUserClickListener(new HeaderMoodDetailView.OnUserClickListener() {
            @Override
            public void onUserClick() {
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, intent_news.userId);
                intent.putExtra(UserDetailActivity.USER_NAME, intent_news.userName);
                intent.putExtra(UserDetailActivity.USER_HEAD, intent_news.userHead);
                intent.putExtra(UserDetailActivity.USER_SEX, intent_news.sex);
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
                            startActivityForResult(new Intent(mContext, LoginNewActivity.class), LOGIN_REQ);
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

        etComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (TextUtils.isEmpty(etComment.getText().toString().trim())) {
                        Toast.makeText(mContext, "您还没有评论~", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "发送~~~");
                        commentPresenter.addComment(homeEnum, detailId, EmojiParser.getInstance(mContext).parseEmoji(etComment.getText().toString()),
                                commentType, ruserId);

                        hideSoftInputView();
                    }
                    return true;
                }
                return false;
            }
        });

        etComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                llEmoji.setVisibility(View.GONE);
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
                    if (!showEmojiTool) {
                        rlComment.setVisibility(View.GONE);
                        llEmoji.setVisibility(View.GONE);
                        llBottom.setVisibility(View.VISIBLE);
                    } else {
                        llEmoji.setVisibility(View.VISIBLE);
                        showEmojiTool = false;
                    }

                }
            }
        });

        popwindow = new SharePopwindow(mContext, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage;
                if (null != intent_news.pic && intent_news.pic.indexOf(",") != -1) {
                    shareImage = new UMImage(mContext, intent_news.pic.split(",")[0]);
                } else {
                    shareImage = new UMImage(mContext, R.mipmap.icon_share);
                }
                switch (position) {
                    case SharePopwindow.WECHAT:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText(intent_news.note)
                                .withTargetUrl(SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_NEWS, "http://server.54xiegang.com/yongyou/app/share_newsInfo.do?id=") + detailId)
                                .withMedia(shareImage).withTitle("心情小计")
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText(intent_news.note)
                                .withTargetUrl(SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_NEWS, "http://server.54xiegang.com/yongyou/app/share_newsInfo.do?id=") + detailId)
                                .withMedia(shareImage).withTitle("心情小计")
                                .share();
                        break;
                }
            }
        });
        mFaceHelper.setOnEmojiOperateListener(new SelectFaceHelper.OnEmojiOperateListener() {
            @Override
            public void onEmojiSelected(SpannableString spanEmojiStr) {
                if (null != spanEmojiStr) {
                    etComment.append(spanEmojiStr);
                }
            }

            @Override
            public void onEmojiDeleted() {
                int selection = etComment.getSelectionStart();
                String text = etComment.getText().toString();
                if (selection > 0) {
                    if (text.contains("[/e]")
                            && text.lastIndexOf("[/e]") == (selection - 4)) {
                        int start = text.lastIndexOf("[e]");
                        int end = selection;
                        etComment.getText().delete(start, end);
                    } else {
                        etComment.getText().delete(selection - 1, selection);
                    }
                }
            }
        });
    }

    /**
     * 访问网络接口
     */
    private void getNetworkData() {
        detailPresenter.getMoodDetail(detailId);
        commentPresenter.getCommentList(homeEnum, detailId, 1, 3, false);
    }

    @Override
    protected void onDestroy() {
        hideSoftInputView();
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

    public void setMoodDetailInfo(M_News newsInfo) {
        // 其他
        headerView.dealWithTheView(newsInfo);
        tvCommentNum.setText(newsInfo.commentNum + "");

        // 操作用户是否收藏该方案(0:否1:是)---操作用户id无值时默认为0
        if (newsInfo.isFavorite == 0) {
            ivStar.setImageResource(R.mipmap.star_btn);
            hasStar = false;
        } else {
            ivStar.setImageResource(R.mipmap.star_btn_sel);
            hasStar = true;
        }

        // 操作用户是否赞过该方案(0:否1:是)---操作用户id无值时默认为0
        if (newsInfo.isGood == 0) {
            ivLike.setImageResource(R.mipmap.heart2_icon);
            hasLike = false;
        } else {
            ivLike.setImageResource(R.mipmap.heart_btn_sel);
            hasLike = true;
        }

        intent_news = newsInfo;
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
        ivLike.setImageResource(R.mipmap.heart_btn_sel);
        hasLike = true;

        ++intent_news.goodNum;
        headerView.setLikeNum(intent_news.goodNum);
    }

    @Override
    public void cancleLikeSuccess() {
        // 显示赞 空心的图标
        ivLike.setImageResource(R.mipmap.heart2_icon);
        hasLike = false;

        --intent_news.goodNum;
        headerView.setLikeNum(intent_news.goodNum);
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

    }

    @Override
    public void cancleCommentLikeSuccess(int posistion) {

    }

    @Override
    public void addCommentSuccess() {
        etComment.setText("");
        commentPresenter.getCommentList(homeEnum, detailId, 1, 3, false);

        ++intent_news.commentNum;
        tvCommentNum.setText(intent_news.commentNum + "");
        headerView.setCommentNum(intent_news.commentNum);
    }

    @Override
    public void delCommentSuccess() {
        commentPresenter.getCommentList(homeEnum, detailId, 1, 3, false);

        --intent_news.commentNum;
        tvCommentNum.setText(intent_news.commentNum + "");
        headerView.setCommentNum(intent_news.commentNum);
    }

    @Override
    public void stopRefreshOrLoad() {
        // do nothing
    }

    @OnClick({R.id.ll_back, R.id.ll_share, R.id.ll_comment, R.id.ll_star, R.id.ll_like, R.id.lh_btn_back, R.id.iv_commentexpression, R.id.rl_all_comment, R.id.ll_right})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.rl_all_comment:
                Intent intent = new Intent(mContext, AllCommentActivity.class);
                intent.putExtra(AllCommentActivity.INTENT_NEWS_ID, detailId);
                startActivityForResult(intent, ALL_COMMENT_REQ);
                break;
            case R.id.ll_share:
                operate(OperateType.SHARE);
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
            case R.id.iv_commentexpression:
                if (llEmoji.getVisibility() == View.GONE) {
                    showEmojiTool = true;
                    hideSoftInputView();
                    ivCommentexpression.setImageResource(R.mipmap.keyboard);
                } else {
                    llEmoji.setVisibility(View.GONE);
                    ivCommentexpression.setImageResource(R.mipmap.expression);
                    showSoftInputView(commentType);
                }
                break;
            case R.id.ll_right:
                operate(OperateType.REPORT);
                break;
        }
    }

    enum OperateType {
        SHARE, COMMENT, STAR, LIKE, REPORT
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
                    startActivityForResult(new Intent(mContext, LoginNewActivity.class), LOGIN_REQ);
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
                    etComment.setHint("评论:");
                    showSoftInputView(0);
                }
                break;

            case STAR:
                if (hasStar)
                    operatePresenter.cancleStar(homeEnum, detailId);
                else
                    operatePresenter.addStar(homeEnum, detailId);
                break;

            case LIKE:
                if (hasLike)
                    operatePresenter.cancleLike(homeEnum, detailId);
                else
                    operatePresenter.addLike(homeEnum, detailId);
                break;

            case REPORT:
                IntentUtil.intStart_activity(mActivity,
                        ReportActivity.class,
                        new Pair<String, Integer>(ReportActivity.INTENT_INFO_ID, detailId),
                        new Pair<String, Integer>(ReportActivity.INTENT_INFO_TYPE, 3));
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
        llEmoji.setVisibility(View.GONE);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (llEmoji.getVisibility() == View.VISIBLE) {
                rlComment.setVisibility(View.GONE);
                llEmoji.setVisibility(View.GONE);
                llBottom.setVisibility(View.VISIBLE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

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
            Toast.makeText(mContext, ShareText.shareMediaToCN(platform) + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

}