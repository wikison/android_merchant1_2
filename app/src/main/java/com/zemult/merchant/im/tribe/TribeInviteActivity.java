package com.zemult.merchant.im.tribe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.friend.FriendAdapter;
import com.zemult.merchant.aip.friend.UserFriendListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class TribeInviteActivity extends BaseActivity {

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
    @Bind(R.id.friends_lv)
    SmoothListView friendsLv;


    ArrayList<M_Friend> friendList = new ArrayList<M_Friend>();
    FriendAdapter adapter;
    UserFriendListRequest userFriendListRequest;
    int page=1;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_tribe_invite);
    }

    @Override
    public void init() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    private void user_friendList( ) {
        if (userFriendListRequest != null) {
            userFriendListRequest.cancel();
        }
        UserFriendListRequest.Input input = new UserFriendListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.name = "";//	名称(用户名/所在公司/职位/角色名)
        input.page = page;
        input.rows =1000;

        input.convertJosn();
        userFriendListRequest = new UserFriendListRequest(input,new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserFriendList) response).status == 1) {
                    friendList.clear();
                    friendList=(ArrayList<M_Friend>)(((APIM_UserFriendList) response).friendList).clone();



                } else {
                    ToastUtils.show(TribeInviteActivity.this, ((APIM_UserFriendList) response).info);
                }
            }
        });
        sendJsonRequest(userFriendListRequest);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_btn_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.lh_btn_right:
                break;
        }
    }
}
