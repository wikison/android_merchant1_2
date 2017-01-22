package com.zemult.merchant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundRelativeLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.MyQrActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.UserAttractAddRequest;
import com.zemult.merchant.aip.mine.UserAttractDelRequest;
import com.zemult.merchant.aip.mine.UserSearchUserPhoneRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_SearchUsersList;
import com.zemult.merchant.model.apimodel.APIM_UserFansList;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;


/**
 * Created by Administrator on 2016/4/27.
 * 添加朋友页面
 */
public class AddFriendsActivity extends BaseActivity {


    UserSearchUserPhoneRequest userSearchUserRequest;
    String strMyPhone;
    List<M_Userinfo> mDatas = new ArrayList<M_Userinfo>();
    CommonAdapter commonAdapter;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_search)
    ImageView ivSearch;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.rl_search)
    RoundRelativeLayout rlSearch;
    @Bind(R.id.tv_my_number)
    TextView tvMyNumber;
    @Bind(R.id.iv_my_qr)
    ImageView ivMyQr;
    @Bind(R.id.ll_my_account)
    LinearLayout llMyAccount;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.tv_no_result)
    TextView tvNoResult;
    @Bind(R.id.iv_scan)
    ImageView ivScan;
    @Bind(R.id.tv_scan)
    TextView tvScan;
    @Bind(R.id.rel_scan_add)
    RelativeLayout relScanAdd;
    @Bind(R.id.iv_invitepeople)
    ImageView ivInvitepeople;
    @Bind(R.id.tv_invitepeople)
    TextView tvInvitepeople;
    @Bind(R.id.rel_invitepeople)
    RelativeLayout relInvitepeople;
    @Bind(R.id.iv_recognizepeople)
    ImageView ivRecognizepeople;
    @Bind(R.id.tv_recognizepeople)
    TextView tvRecognizepeople;
    @Bind(R.id.rel_recognizepeople)
    RelativeLayout relRecognizepeople;
    @Bind(R.id.main_search_friends_ll)
    LinearLayout mainSearchFriends_ll;
    @Bind(R.id.main_search_keywords)
    TextView mainSearchKeywords;

    private UserAttractAddRequest attractAddRequest; // 添加关注
    private UserAttractDelRequest attractDelRequest; // 取消关注

    int iTime = 2;
    private int page = 1;

    private Timer timer = null;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            iTime--;
            if (iTime <= 0) {
                timer.cancel();
                timer = null;
                closeSearchResult();
            } else {

            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_addfriends);
    }

    public void init() {
        initView();
        initData();
        initListener();
    }

    private void initView() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("查找服务管家");
        tvNoResult.setVisibility(View.INVISIBLE);
        strMyPhone = SlashHelper.userManager().getUserinfo().getPhoneNum();
        tvMyNumber.setText(strMyPhone);
        tvNoResult.bringToFront();

        smoothListView.setRefreshEnable(false);
        smoothListView.setLoadMoreEnable(false);
    }

    private void initData() {

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int arg1, int arg2,
                                      int arg3) {
                int isVisible = text.length() > 0 ? View.VISIBLE : View.GONE;
                mainSearchFriends_ll.setVisibility(isVisible);
                mainSearchKeywords.setText(text.toString().trim());
            }

            @Override
            public void beforeTextChanged(CharSequence text, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    private void closeSearchResult() {
        tvNoResult.setVisibility(View.GONE);
    }

    private void initListener() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (timer != null) {
                        timer.cancel();
                        iTime = 2;
                        closeSearchResult();
                    }
                    startSearch(etSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });

        ivMyQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.start_activity(AddFriendsActivity.this, MyQrActivity.class);
            }
        });

//        rel_scan_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PermissionTest pt = new PermissionTest();
//                if (pt.isCameraPermission()) {
//                    IntentUtil.start_activity(AddFriendsActivity.this, ScanQrActivity.class);
//                } else {
//                    ToastUtil.showMessage("需要允许使用摄像头权限");
//                }
//
//            }
//        });
//
//        rel_recognizepeople.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                IntentUtil.start_activity(AddFriendsActivity.this, RecogizePeopleActivity.class);
//            }
//        });
//
//        rel_invitepeople.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                IntentUtil.start_activity(AddFriendsActivity.this, InviteContactsActivity.class);
//
//            }
//        });
    }

    public void startSearch(String pNo) {
        if (userSearchUserRequest != null) {
            userSearchUserRequest.cancel();
        }
        UserSearchUserPhoneRequest.Input input = new UserSearchUserPhoneRequest.Input();
        input.name = pNo;
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.convertJosn();
        loadingDialog.show();
        userSearchUserRequest = new UserSearchUserPhoneRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(Object response) {
                loadingDialog.dismiss();
                int status = ((CommonResult) response).status;
                if (status == 1) {
                    if(((CommonResult) response).userId != 0 ){
                        Intent intent = new Intent(AddFriendsActivity.this, UserDetailActivity.class);
                        intent.putExtra(UserDetailActivity.USER_ID, ((CommonResult) response).userId);
                        startActivity(intent);
                    }
                    else{
                        iTime = 2;
                        tvNoResult.setVisibility(View.VISIBLE);
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            }
                        }, 0, 1000);
                    }
                    smoothListView.setLoadMoreEnable(false);
                } else {
                    iTime = 2;
                    tvNoResult.setVisibility(View.VISIBLE);
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }, 0, 1000);
                }
            }
        });
        sendJsonRequest(userSearchUserRequest);
    }

    private void initAdapter() {
        if (mDatas.isEmpty()) {
            smoothListView.setVisibility(View.GONE);
            iTime = 5;
            tvNoResult.setVisibility(View.VISIBLE);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }, 0, 1000);
        } else {
            smoothListView.setVisibility(View.VISIBLE);
            tvNoResult.setVisibility(View.GONE);

            smoothListView.setAdapter(commonAdapter = new CommonAdapter<M_Userinfo>(AddFriendsActivity.this, R.layout.item_my_follow, mDatas) {
                @Override
                public void convert(CommonViewHolder holder, M_Userinfo mfollow, final int position) {
                    if (!TextUtils.isEmpty(mfollow.getUserHead())) {
                        holder.setCircleImage(R.id.iv_follow_head, mfollow.getUserHead());
                    }
                    // 性别
                    if (mfollow.getUserSex() == 0)
                        holder.setResImage(R.id.iv_sex, R.mipmap.man_icon);
                    else
                        holder.setResImage(R.id.iv_sex, R.mipmap.girl_icon);

                    holder.setOnclickListener(R.id.iv_follow_head, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(AddFriendsActivity.this, UserDetailActivity.class);
                            intent.putExtra(UserDetailActivity.USER_ID, mDatas.get(position).getUserId());
                            intent.putExtra(UserDetailActivity.USER_NAME, mDatas.get(position).getUserName());
                            intent.putExtra(UserDetailActivity.USER_HEAD, mDatas.get(position).getUserHead());
                            startActivity(intent);
                        }
                    });
                    if (!TextUtils.isEmpty(mfollow.userNote))
                        holder.setText(R.id.tv_describe, mfollow.userNote);

                    holder.setText(R.id.tv_follow_name, mfollow.getUserName());
                    holder.setFocusState(R.id.tv_state, mfollow.getState(), R.id.iv_state);

                    if (mDatas.get(position).getState() == 2)
                        holder.setViewGone(R.id.ll_state);
                    else
                        holder.setViewVisible(R.id.ll_state);

                    holder.setOnclickListener(R.id.ll_state, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mDatas.get(position).getState() == 0) {       //已关注的状态
                                cancleFocus(mDatas.get(position).getUserId(), position); //取消关注操作

                            } else if (mDatas.get(position).getState() == 1) {         //未关注的状态
                                addFous(mDatas.get(position).getUserId(), position);//添加关注网络操作
                            }
                        }
                    });
                }
            });
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back,R.id.main_search_friends_ll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.main_search_friends_ll:
                if (timer != null) {
                    timer.cancel();
                    iTime = 5;
                    closeSearchResult();
                }
                startSearch(etSearch.getText().toString());
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    // 用户添加关注
    private void addFous(int userId, final int position) {
        showPd();
        if (attractAddRequest != null) {
            attractAddRequest.cancel();
        }
        UserAttractAddRequest.Input input = new UserAttractAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.attractId = userId; // 被关注的用户id

        input.convertJosn();
        attractAddRequest = new UserAttractAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("添加成功");

                    mDatas.get(position).setState(0);
                    commonAdapter.setDataChanged(mDatas);  //改变按钮样式

                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractAddRequest);
    }


    // 用户取消关注
    private void cancleFocus(int userId, final int position) {
        showPd();
        if (attractDelRequest != null) {
            attractDelRequest.cancel();
        }
        UserAttractDelRequest.Input input = new UserAttractDelRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.attractId = userId; // 被关注的用户id
        input.convertJosn();
        attractDelRequest = new UserAttractDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(AddFriendsActivity.this, "取消成功");
                    mDatas.get(position).setState(1);
                    commonAdapter.setDataChanged(mDatas);  //改变按钮样式

                } else {
                    ToastUtils.show(AddFriendsActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractDelRequest);
    }
}
