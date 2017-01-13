package com.zemult.merchant.activity.message;

import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.AddFriendsActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.friend.InviteContactsAdapter;
import com.zemult.merchant.aip.friend.UserCheckBookListRequest;
import com.zemult.merchant.aip.friend.UserFriendDelRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ContactsDao;
import com.zemult.merchant.util.HanziToPinyin;
import com.zemult.merchant.util.HanziToPinyin.Token;
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
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 邀请好友，通讯录联系人
 */
public class InviteContactsActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_rightiamge)
    Button lh_btn_rightiamge;
    @Bind(R.id.lv_friends)
    SwipeListView lvFriends;
    @Bind(R.id.sidebar)
    Sidebar sidebar;
    @Bind(R.id.tv_peoplecount)
    TextView tv_peoplecount;


    ArrayList<M_Friend> friendList = new ArrayList<M_Friend>();
    ArrayList<M_Friend> filtercontacts= new ArrayList<M_Friend>();
    InviteContactsAdapter adapter;
    UserCheckBookListRequest userCheckBookListRequest;
    UserFriendDelRequest  userFriendDelRequest;
    int page=1;
    String name="";
    List<M_Friend> cList2=new ArrayList<M_Friend>();//本地联系人
    String phoneIds;
    @Override
    public void setContentView() {
     setContentView(R.layout.activity_invitecontacts);
    }

    @Override
    public void init() {
        initView();
        phoneIds= AppUtils.getPhoneNumbers(this);
        if(!StringUtils.isEmpty(phoneIds)){
            user_check_bookList();
        }

        ContactsDao util = new ContactsDao(this);
        List<M_Friend> listMembers = new ArrayList<M_Friend>();
        util.getAllContactToFriend(listMembers);
        cList2 = util.removeDuplicateDataToFriend(listMembers);
        for (M_Friend contactDataBean : cList2) {
            String selfphone = SlashHelper.userManager().getUserinfo().getPhoneNum();
            if (selfphone.equals(contactDataBean.getPhone()) == true) {
                cList2.remove(contactDataBean);
                break;
            }
        }

    }



    public void initView() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("邀请联系人");
//        lh_btn_rightiamge.setVisibility(View.VISIBLE);
//        lh_btn_rightiamge.setBackgroundResource(R.mipmap.add_btn);
//        lh_btn_rightiamge.setWidth(DensityUtil.dip2px(InviteContactsActivity.this, 50));
//        lh_btn_rightiamge.setHeight(DensityUtil.dip2px(InviteContactsActivity.this, 20));
        sidebar.setListView(lvFriends);
        lvFriends.setFooterDividersEnabled(false);

        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int friendId = adapter.getItem(position).getFriendId();
                IntentUtil.intStart_activity(InviteContactsActivity.this,
                        UserDetailActivity.class,new Pair<String, Integer>("userId", friendId));
            }

        });
    }


//    private List<M_Friend> filter(String keywords) {
//        filtercontacts.clear();
//        if (StringUtils.isEmpty(keywords)){
//            filtercontacts.addAll((ArrayList<M_Friend>)friendList.clone());
//            return filtercontacts;
//        }
//        for (M_Friend contact : friendList) {
//            if (contact.getName().toUpperCase()
//                    .contains(keywords.toUpperCase())) {
//                filtercontacts.add(contact);
//            }
//        }
//        return filtercontacts;
//    }

    //搜索好友列表(条件:名称--用户名/所在公司/职位/角色名)
    private void user_check_bookList( ) {
        showPd();
        if (userCheckBookListRequest != null) {
            userCheckBookListRequest.cancel();
        }
        UserCheckBookListRequest.Input input = new UserCheckBookListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.phones =phoneIds;

        input.convertJosn();
        userCheckBookListRequest = new UserCheckBookListRequest(input,new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                ToastUtils.show(InviteContactsActivity.this, error.getMessage());
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    friendList.clear();
                    filtercontacts.clear();
                    String[] phoneArry = new String[0];
                    if(((CommonResult) response).phones.indexOf(",")!=-1){
                        phoneArry=((CommonResult) response).phones.split(",");
                    }
                    for(int i=0;i<phoneArry.length;i++){
                        for(int k=0;k<cList2.size();k++){
                            if(phoneArry[i].equals(cList2.get(k).getPhone()+"")){
                                friendList.add(cList2.get(k));
                                filtercontacts.add(cList2.get(k));
                            }
                        }
                    }
                    tv_peoplecount.setText("您的手机联系人还有"+filtercontacts.size()+"人没有激活");
                    getContactList();
                    Collections.sort(filtercontacts, new Comparator<M_Friend>() {
                        @Override
                        public int compare(M_Friend lhs, M_Friend rhs) {
                            return lhs.getHeader().compareTo(rhs.getHeader());
                        }
                    });

                    if(adapter==null){
                        adapter = new InviteContactsAdapter(InviteContactsActivity.this,
                                R.layout.item_friend, filtercontacts);
                        lvFriends.setAdapter(adapter);
                    }
                    else{
                        adapter.setData(filtercontacts);
                    }

                    adapter.setInviteAgainClickListener(new InviteContactsAdapter.InviteAgainClickListener() {
                        @Override
                        public void onItemInviteAgainClick(M_Friend friend) {
                            friend.setState(1);
                            AppUtils.sendMsg(InviteContactsActivity.this, friend.getPhone(),
                                    "hi～下载"+getResources().getString(R.string.app_name)+"并填写我的邀请码" +
                                            "（#"+SlashHelper.userManager().getUserinfo().getPhoneNum()+"#）注册，跟我一起玩吧！下载地址：" +
                                            SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_APP, "")
                                    );
                        }
                    });

                    adapter.setInviteClickListener(new InviteContactsAdapter.InviteClickListener() {
                        @Override
                        public void onItemInviteClick(M_Friend friend) {
                            friend.setState(1);
                            AppUtils.sendMsg(InviteContactsActivity.this, friend.getPhone(),
                                    "hi～下载"+getResources().getString(R.string.app_name)+"并填写我的邀请码" +
                                            "（#"+SlashHelper.userManager().getUserinfo().getPhoneNum()+"#）注册，跟我一起玩吧！下载地址：" +
                                            SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_APP, "")
                            );
                        }
                    });
                    adapter.setNotifyOnChange(true);
                } else {
                    ToastUtils.show(InviteContactsActivity.this, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userCheckBookListRequest);
    }


    public  void user_friend_delete(final M_Friend mfriend ) {
        showPd();
        if (userFriendDelRequest != null) {
            userFriendDelRequest.cancel();
        }
        UserFriendDelRequest.Input input = new UserFriendDelRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.friendId = mfriend.getFriendId();

        input.convertJosn();
        userFriendDelRequest = new UserFriendDelRequest(input,new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    adapter.removeOne(mfriend);
                } else {
                    ToastUtils.show(InviteContactsActivity.this, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userFriendDelRequest);
    }


    @OnClick({ R.id.lh_btn_rightiamge, R.id.lh_btn_back, R.id.ll_back})
    public void viewClick(View v) {
        switch (v.getId()) {
            case R.id.lh_btn_rightiamge:
                IntentUtil.start_activity(InviteContactsActivity.this, AddFriendsActivity.class);
                break;
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
        }
    }



    private void getContactList() {
        for (int i = 0; i < friendList.size(); i++) {
            M_Friend f = friendList.get(i);
            setPinYinAndHearder(f);
        }
    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param friend
     */
    protected void setPinYinAndHearder(M_Friend friend) {
        String nickName2 = friend.getName();

        if (Character.isDigit(nickName2.charAt(0))) {
            friend.setHeader("#");
        } else {
            ArrayList<Token> tokens = HanziToPinyin.getInstance()
                    .get(nickName2);
            StringBuilder sb = new StringBuilder();
            if (tokens != null && tokens.size() > 0) {
                for (Token token : tokens) {
                    if (Token.PINYIN == token.type) {
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


}
