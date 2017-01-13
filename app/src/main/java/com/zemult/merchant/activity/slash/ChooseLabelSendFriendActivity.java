package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.search.SendLabelActivity;
import com.zemult.merchant.adapter.friend.FriendAdapter;
import com.zemult.merchant.aip.friend.UserFriendListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.HanziToPinyin;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.common.Sidebar;
import com.zemult.merchant.view.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2016/8/6.
 */
public class ChooseLabelSendFriendActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lv_friends)
    SwipeListView lvFriends;
    @Bind(R.id.sidebar)
    Sidebar sidebar;
    @Bind(R.id.floating_header)
    TextView floatingHeader;
    @Bind(R.id.btn_ok)
    Button btnOk;

    Context mContext;

    ArrayList<M_Friend> friendList = new ArrayList<M_Friend>();
    ArrayList<M_Friend> filterContacts = new ArrayList<M_Friend>();
    int page = 1;
    String name = "";
    UserFriendListRequest userFriendListRequest;
    FriendAdapter adapter;
    boolean isAll = true;// 是否全选
    String labelId;
    String labelName;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_publish_friend);
    }

    @Override
    public void init() {
        registerReceiver(new String[]{Constants.BROCAST_CLOSESendLabelActivity});
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
        labelName = getIntent().getStringExtra("labelName");
        labelId = getIntent().getStringExtra("labelId");
    }

    private void initView() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("选择好友");
        tvRight.setVisibility(View.GONE);
        tvRight.setText("全选");
        btnOk.setVisibility(View.GONE);
        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(null!=adapter.getItem(position).userIndustryList&&adapter.getItem(position).userIndustryList.size()>0){
                    int friendId = adapter.getItem(position).getFriendId();
                    Intent intent =new Intent(ChooseLabelSendFriendActivity.this,SendLabelActivity.class);
                    intent.putExtra("labelName",labelName);
                    intent.putExtra("labelId",labelId);
                    intent.putExtra("friendId",friendId);
                    startActivity(intent);
                }
                else{
                    ToastUtil.showMessage("该用户还没有角色，不能给他贴标签");
                }

            }
        });

        user_friendList();
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_CLOSESendLabelActivity.equals(intent.getAction())) {
           finish();
        }
    }

    private void initListener() {
        sidebar.setListView(lvFriends);
        lvFriends.setFooterDividersEnabled(false);
    }


    //搜索好友列表(条件:名称--用户名/所在公司/职位/角色名)
    private void user_friendList() {
        if (userFriendListRequest != null) {
            userFriendListRequest.cancel();
        }
        UserFriendListRequest.Input input = new UserFriendListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.name = name;//	名称(用户名/所在公司/职位/角色名)
        input.page = page;
        input.rows = 1000;

        input.convertJosn();
        userFriendListRequest = new UserFriendListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserFriendList) response).status == 1) {
                    friendList.clear();
                    filterContacts.clear();
                    friendList = (ArrayList<M_Friend>) (((APIM_UserFriendList) response).friendList).clone();
                    filterContacts.addAll((ArrayList<M_Friend>) (((APIM_UserFriendList) response).friendList).clone());
                    getContactList();
                    Collections.sort(filterContacts, new Comparator<M_Friend>() {
                        @Override
                        public int compare(M_Friend lhs, M_Friend rhs) {
                            return lhs.getHeader().compareTo(rhs.getHeader());
                        }
                    });

                    if (adapter == null) {
                        adapter = new FriendAdapter(mContext,
                                R.layout.item_friend, filterContacts);
                        lvFriends.setAdapter(adapter);
                    } else {
                        adapter.setData(filterContacts);
                    }
                } else {
                    ToastUtils.show(mContext, ((APIM_UserFriendList) response).info);
                }
            }
        });
        sendJsonRequest(userFriendListRequest);
    }

    private void getContactList() {
        for (int i = 0; i < friendList.size(); i++) {
            M_Friend f = friendList.get(i);
            setPinYinAndHeader(f);
        }
    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param friend
     */
    protected void setPinYinAndHeader(M_Friend friend) {
        String nickName2 = friend.getName();

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

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;

            case R.id.tv_right:
                isAll = !isAll;
                if (isAll) {
                    tvRight.setText("全不选");
                    btnSelectAll();
                } else {
                    tvRight.setText("全选");
                    btnSelectNo();
                }
                break;
        }
    }


    public void btnSelectAll() {
        for (int i = 0; i < filterContacts.size(); i++) {
            filterContacts.get(i).setIsselected(true);
        }
        adapter.notifyDataSetChanged();

    }

    public void btnSelectNo() {
        for (int i = 0; i < filterContacts.size(); i++) {
            filterContacts.get(i).setIsselected(false);
        }
        adapter.notifyDataSetChanged();
    }

    private String getSelectIds() {
        String result = "";
        for (int i = 0; i < filterContacts.size(); i++) {
            if (filterContacts.get(i).isselected())
                result = (result.equals("") ? result + filterContacts.get(i).getFriendId() : result + "," + filterContacts.get(i).getFriendId());
        }
        return result;
    }

}
