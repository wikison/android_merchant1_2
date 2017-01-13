package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.CommonFriendAdapter;
import com.zemult.merchant.aip.friend.UserFriendListOtherRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.HanziToPinyin;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.common.Sidebar;
import com.zemult.merchant.view.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 用户详情--共同好友
 *
 * @author djy
 * @time 2016/8/2 10:46
 */
public class CommonFriendActivity extends BaseActivity {

    @Bind(R.id.lv_friends)
    SwipeListView lvFriends;
    @Bind(R.id.sidebar)
    Sidebar sidebar;
    @Bind(R.id.floating_header)
    TextView floatingHeader;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    public static final String INTENT_USERID = "userId";
    private CommonFriendAdapter adapter;
    private UserFriendListOtherRequest request; // 用户查看和他人的共同好友列表
    private int userId;
    private Context mContext;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_common_friend);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

        user_friendList_other();
    }

    private void initData() {
        mContext = this;
        userId = getIntent().getIntExtra(INTENT_USERID, -1);
        adapter = new CommonFriendAdapter(mContext, new ArrayList<M_Friend>());
    }
    private void initView() {
        lhTvTitle.setText("共同好友");
        lvFriends.setAdapter(adapter);
        sidebar.setListView(lvFriends);
        lvFriends.setFooterDividersEnabled(false);
    }
    private void initListener() {
        adapter.setOnUserDetailClickListener(new CommonFriendAdapter.OnUserDetailClickListener() {
            @Override
            public void onUserDetailClick(int position) {
                int userId = adapter.getItem(position).getUserId();
                IntentUtil.intStart_activity(CommonFriendActivity.this,
                        UserDetailActivity.class, new Pair<String, Integer>(UserDetailActivity.USER_ID, userId));
            }
        });
    }

    // 用户查看和他人的共同好友列表
    private void user_friendList_other( ) {
        if (request != null) {
            request.cancel();
        }
        UserFriendListOtherRequest.Input input = new UserFriendListOtherRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.userId = userId;
        input.page = 1;
        input.rows = 1000;

        input.convertJosn();
        request = new UserFriendListOtherRequest(input,new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserFriendList) response).status == 1) {
                    setData(((APIM_UserFriendList) response).friendList);
                } else {
                    ToastUtils.show(mContext, ((APIM_UserFriendList) response).info);
                }
            }
        });
        sendJsonRequest(request);
    }

    private void setData(List<M_Friend> list) {
        if(list != null && !list.isEmpty()){
            getContactList(list);
            Collections.sort(list, new Comparator<M_Friend>() {
                @Override
                public int compare(M_Friend lhs, M_Friend rhs) {
                    return lhs.getHeader().compareTo(rhs.getHeader());
                }
            });
            adapter.setData(list);
        }
    }

    private void getContactList(List<M_Friend> list) {
        for (int i = 0; i < list.size(); i++) {
            M_Friend f = list.get(i);
            setPinYinAndHearder(f);
        }
    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param friend
     */
    protected void setPinYinAndHearder(M_Friend friend) {
        String nickName2 = friend.getUserName();

        if (Character.isDigit(nickName2.charAt(0))) {
            friend.setHeader("#");
        } else {
            ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance()
                    .get(nickName2);
            StringBuilder sb = new StringBuilder();
            if (tokens != null && tokens.size() > 0) {
                for (HanziToPinyin.Token token : tokens) {
                    if (HanziToPinyin.Token.PINYIN == token.type) {
                        sb.append(token.target);
                    } else {
                        sb.append(token.source);
                    }
                }
            }
            String pingyin = sb.toString().toUpperCase();
            friend.setNicknamepinyin(pingyin);
            String header = pingyin.substring(0, 1);
            friend.setHeader(header);
            char headerChar = header.toLowerCase().charAt(0);
            if (headerChar < 'a' || headerChar > 'z') {
                friend.setHeader("#");
            }
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
