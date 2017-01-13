package com.zemult.merchant.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.friend.ContactsAdapter;
import com.zemult.merchant.aip.friend.UserFriendDelRequest;
import com.zemult.merchant.aip.friend.UserFriendListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.HanziToPinyin;
import com.zemult.merchant.util.HanziToPinyin.Token;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.common.Sidebar;
import com.zemult.merchant.view.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/6/3.
 */
public class SfriendFragment extends BaseFragment {
    @Bind(R.id.lv_friends)
    SwipeListView lvFriends;
    @Bind(R.id.sidebar)
    Sidebar sidebar;

    ArrayList<M_Friend> friendList = new ArrayList<M_Friend>();
    ArrayList<M_Friend> filtercontacts= new ArrayList<M_Friend>();
    ContactsAdapter adapter;
    UserFriendListRequest userFriendListRequest;
    UserFriendDelRequest  userFriendDelRequest;
    int page=1;
    String name="";
    Set<String> selectidset=new HashSet<>();
    ArrayList userIdlist=new ArrayList();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sfriend_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        userIdlist=getArguments().getStringArrayList("userIdlist");
        user_friendList();
    }


    public void initView() {
        sidebar.setListView(lvFriends);
        lvFriends.setFooterDividersEnabled(false);

        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int friendId = adapter.getItem(position).getFriendId();
                IntentUtil.intStart_activity(getActivity(),
                        UserDetailActivity.class,new Pair<String, Integer>("userId", friendId));
            }

        });
    }


    //搜索好友列表(条件:名称--用户名/所在公司/职位/角色名)
    private void user_friendList( ) {
        showPd();
        if (userFriendListRequest != null) {
            userFriendListRequest.cancel();
        }
        UserFriendListRequest.Input input = new UserFriendListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.name = name;//	名称(用户名/所在公司/职位/角色名)
        input.page = page;
        input.rows =1000;

        input.convertJosn();
        userFriendListRequest = new UserFriendListRequest(input,new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserFriendList) response).status == 1) {
                    friendList.clear();
                    filtercontacts.clear();
                    friendList=(ArrayList<M_Friend>)(((APIM_UserFriendList) response).friendList).clone();
                    filtercontacts.addAll((ArrayList<M_Friend>)(((APIM_UserFriendList) response).friendList).clone());
                    for(int i=0;i<userIdlist.size();i++){
                        for(int k=0;k<friendList.size();k++){
                            if(userIdlist.get(i).equals(friendList.get(k).getFriendId()+"")){
                                friendList.remove(k);
                            }
                        }
                        for(int p=0;p<filtercontacts.size();p++){
                            if(userIdlist.get(i).equals(filtercontacts.get(p).getFriendId()+"")){
                                filtercontacts.remove(p);
                            }
                        }
                    }
                    getContactList();
                    Collections.sort(filtercontacts, new Comparator<M_Friend>() {
                        @Override
                        public int compare(M_Friend lhs, M_Friend rhs) {
                            return lhs.getHeader().compareTo(rhs.getHeader());
                        }
                    });

                    if(adapter==null){
                        adapter = new ContactsAdapter(getActivity(),
                                R.layout.item_friend, filtercontacts);
                        lvFriends.setAdapter(adapter);
                    }
                    else{
                        adapter.setData(filtercontacts);
                    }

                    adapter.setAddFriendClickListener(new ContactsAdapter.AddFriendClickListener() {
                        @Override
                        public void onItemAddClick(M_Friend friend) {
                            selectidset.add(friend.getFriendId()+"");
                        }
                    });

                    adapter.setDelFriendClickListener(new ContactsAdapter.DelFriendClickListener() {
                        @Override
                        public void onItemDelClick(M_Friend friend) {
                            selectidset.remove(friend.getFriendId()+"");
                        }
                    });

                } else {
                    ToastUtils.show(getActivity(), ((APIM_UserFriendList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userFriendListRequest);
    }


    public  List<IYWContact>  getSelectIYWContact( ) {
        List<IYWContact> list = new ArrayList<IYWContact>();
        for (String strid : selectidset) {
            IYWContact contact = YWContactFactory.createAPPContact(strid, LoginSampleHelper.APP_KEY);
            list.add(contact);
        }
        return  list;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
