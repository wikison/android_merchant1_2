package com.zemult.merchant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.NewFriendActivity;
import com.zemult.merchant.activity.SfriendActivity;
import com.zemult.merchant.activity.mine.FamiliarPeopleActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.friend.ContactsNewAdapter;
import com.zemult.merchant.aip.friend.UserFriendDelRequest;
import com.zemult.merchant.aip.mine.UserAttractListRequest;
import com.zemult.merchant.aip.mine.UserMessageBillNumUnread_1_2_2Request;
import com.zemult.merchant.aip.mine.UserSysSaleUserListNumRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.model.apimodel.APIM_UserFansList;
import com.zemult.merchant.util.HanziToPinyin;
import com.zemult.merchant.util.HanziToPinyin.Token;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SearchView;
import com.zemult.merchant.view.common.Sidebar;
import com.zemult.merchant.view.swipelistview.SwipeListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/6/3.
 */
public class FamiliarFragment extends BaseFragment {
    @Bind(R.id.lv_friends)
    SwipeListView lvFriends;
    @Bind(R.id.sidebar)
    Sidebar sidebar;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.search_view)
    SearchView searchView;
    ArrayList<M_Fan> filtercontacts= new ArrayList<M_Fan>();
    ContactsNewAdapter adapter;
    UserAttractListRequest userAttractListRequest;
    UserSysSaleUserListNumRequest userSysSaleUserListNumRequest;
    int page=1,num;
    String name="";

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
        user_sys_saleUserList_num();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }


    public void initView() {
        llBack.setVisibility(View.GONE);
        lhTvTitle.setText("熟人");
        searchView.setMaxWordNum(11);
        searchView.setFilter();

        sidebar.setListView(lvFriends);
        lvFriends.setFooterDividersEnabled(false);
        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int friendId = adapter.getItem(position).getUserId();
                IntentUtil.intStart_activity(getActivity(),
                        UserDetailActivity.class,new Pair<String, Integer>("userId", friendId));
            }

        });

        searchView.setTvCancelVisible(View.GONE);
        searchView.setBgColor(getResources().getColor(R.color.divider_c1));
        searchView.setSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearch(String text) {
                name = text;
                user_friendList();
            }

            @Override
            public void onClear() {
                name = "";
                user_friendList();
            }
        });

    }


    //约服账单 消息未读的数量
    private void user_sys_saleUserList_num() {
        if (userSysSaleUserListNumRequest != null) {
            userSysSaleUserListNumRequest.cancel();
        }

        UserSysSaleUserListNumRequest.Input input = new UserSysSaleUserListNumRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.operateUserId = SlashHelper.userManager().getUserId();
            input.convertJosn();
        }

        userSysSaleUserListNumRequest = new UserSysSaleUserListNumRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    num=((CommonResult) response).num;
                    user_friendList();
                }
            }
        });
        sendJsonRequest(userSysSaleUserListNumRequest);
    }



    //熟人
    private void user_friendList( ) {
        showPd();
        if (userAttractListRequest != null) {
            userAttractListRequest.cancel();
        }
        UserAttractListRequest.Input input = new UserAttractListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.name = name;//	名称(用户名/所在公司/职位/角色名)
        input.page = page;
        input.rows =1000;

        input.convertJosn();
        userAttractListRequest = new UserAttractListRequest(input,new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserFansList) response).status == 1) {
                    filtercontacts.clear();
                    filtercontacts.addAll((ArrayList<M_Fan>)(((APIM_UserFansList) response).userList).clone());
                    getContactList();
                    Collections.sort(filtercontacts, new Comparator<M_Fan>() {
                        @Override
                        public int compare(M_Fan lhs, M_Fan rhs) {
                            return lhs.getHeader().compareTo(rhs.getHeader());
                        }
                    });

                    if(adapter==null){
                        adapter = new ContactsNewAdapter(getActivity(),
                                R.layout.item_friend, filtercontacts,num);
                        lvFriends.setAdapter(adapter);
                    }
                    else{
                        adapter.setData(filtercontacts,num);
                    }

                } else {
                    ToastUtils.show(getActivity(), ((APIM_UserFansList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userAttractListRequest);
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
        for (int i = 0; i < filtercontacts.size(); i++) {
            M_Fan f = filtercontacts.get(i);
            setPinYinAndHearder(f);
        }
    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param friend
     */
    protected void setPinYinAndHearder(M_Fan friend) {
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
