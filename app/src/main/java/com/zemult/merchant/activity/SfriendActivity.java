package com.zemult.merchant.activity;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.friend.FriendAdapter;
import com.zemult.merchant.aip.friend.UserFriendDelRequest;
import com.zemult.merchant.aip.friend.UserFriendListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.demo.TribeActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.HanziToPinyin;
import com.zemult.merchant.util.HanziToPinyin.Token;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.common.CommonDialog;
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
 * Created by admin on 2016/6/3.
 */
public class SfriendActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_rightiamge)
    Button lh_btn_rightiamge;
    @Bind(R.id.search_et_input)
    EditText searchEtInput;
    @Bind(R.id.lv_friends)
    SwipeListView lvFriends;
    @Bind(R.id.sidebar)
    Sidebar sidebar;

    ArrayList<M_Friend> friendList = new ArrayList<M_Friend>();
    ArrayList<M_Friend> filtercontacts= new ArrayList<M_Friend>();
    FriendAdapter adapter;
    UserFriendListRequest userFriendListRequest;
    UserFriendDelRequest  userFriendDelRequest;
    int page=1;
    String name="";
    /**
     * 监听搜索框
     */
//    private TextWatcher textWatcher = new TextWatcher() {
//
//        @Override
//        public void onTextChanged(CharSequence text, int arg1, int arg2,
//                                  int arg3) {
//            if (friendList.size() !=0) {
//                filter(text.toString().trim());
//                adapter.setData(filtercontacts);
//            }
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence arg0, int arg1,
//                                      int arg2, int arg3) {
//        }
//
//        @Override
//        public void afterTextChanged(Editable arg0) {
//
//        }
//    };


    View.OnKeyListener onKey = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // TODO Auto-generated method stub
            Log.v("OnKeyListener", event.toString());
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                Log.v("OnKeyListener", "KeyEvent.KEYCODE_ENTER");
                InputMethodManager imm = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm.isActive()) {

                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(),
                            0);
                    name = searchEtInput.getText().toString().trim();
                    filter(name);
                    adapter.setData(filtercontacts);
                }
                return true;
            }
            return false;
        }

    };

    @Override
    public void setContentView() {
     setContentView(R.layout.sfriendtribe_fragment);
    }

    @Override
    public void init() {
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        user_friendList();
    }

    public void initView() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("杠友");
        lh_btn_rightiamge.setVisibility(View.VISIBLE);
        lh_btn_rightiamge.setBackgroundResource(R.mipmap.sfriend_icon);
        lh_btn_rightiamge.setWidth(DensityUtil.dip2px(SfriendActivity.this, 35));
        lh_btn_rightiamge.setHeight(DensityUtil.dip2px(SfriendActivity.this, 35));
        View  lvhead=LayoutInflater.from(this).inflate( R.layout.sfriendhead, null);
        LinearLayout ll_new_friend=(LinearLayout)lvhead.findViewById(R.id.ll_new_friend);
        LinearLayout ll_tribe=(LinearLayout)lvhead.findViewById(R.id.ll_tribe);
        ll_new_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.start_activity(SfriendActivity.this, NewFriendActivity.class);
            }
        });

        ll_tribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.start_activity(SfriendActivity.this, TribeActivity.class);
            }
        });
        lhBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //输入监听
//        searchEtInput.addTextChangedListener(textWatcher);
        //键盘搜索按钮监听
        searchEtInput.setOnKeyListener(onKey);
        sidebar.setListView(lvFriends);
        lvFriends.setFooterDividersEnabled(false);
        lvFriends.addHeaderView(lvhead);

        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int friendId = adapter.getItem(position-1).getFriendId();
                IntentUtil.intStart_activity(SfriendActivity.this,
                        UserDetailActivity.class,new Pair<String, Integer>("userId", friendId));
            }

        });
    }

    private List<M_Friend> filter(String keywords) {
        filtercontacts.clear();
        if (StringUtils.isEmpty(keywords)){
            filtercontacts.addAll((ArrayList<M_Friend>)friendList.clone());
            return filtercontacts;
        }
        for (M_Friend contact : friendList) {
            if (contact.getName().toUpperCase()
                    .contains(keywords.toUpperCase())) {
                filtercontacts.add(contact);
            }
        }
        return filtercontacts;
    }

    //搜索好友列表(条件:名称--用户名/所在公司/职位/角色名)
    private void user_friendList( ) {
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

            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserFriendList) response).status == 1) {
                    friendList.clear();
                    filtercontacts.clear();
                    friendList=(ArrayList<M_Friend>)(((APIM_UserFriendList) response).friendList).clone();
                    filtercontacts.addAll((ArrayList<M_Friend>)(((APIM_UserFriendList) response).friendList).clone());
                    getContactList();
                    Collections.sort(filtercontacts, new Comparator<M_Friend>() {
                        @Override
                        public int compare(M_Friend lhs, M_Friend rhs) {
                            return lhs.getHeader().compareTo(rhs.getHeader());
                        }
                    });

                    if(adapter==null){
                        adapter = new FriendAdapter(SfriendActivity.this,
                                R.layout.item_friend, filtercontacts);
                        lvFriends.setAdapter(adapter);
                    }
                    else{
                        adapter.setData(filtercontacts);
                    }

                    adapter.setDelFriendClickListener(new FriendAdapter.DelFriendClickListener() {
                        @Override
                        public void onItemClick(final M_Friend fed) {
                            CommonDialog.showDialogListener(SfriendActivity.this, null,"否", "是", getResources().getString(R.string.del_friend_tip), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CommonDialog.DismissProgressDialog();

                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CommonDialog.DismissProgressDialog();
                                    user_friend_delete(fed);
                                }
                            });
                        }
                    });
                } else {
                    ToastUtils.show(SfriendActivity.this, ((APIM_UserFriendList) response).info);
                }
            }
        });
        sendJsonRequest(userFriendListRequest);
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
                    ToastUtils.show(SfriendActivity.this, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userFriendDelRequest);
    }


    @OnClick({ R.id.lh_btn_rightiamge})
    public void viewClick(View v) {
        switch (v.getId()) {
            case R.id.lh_btn_rightiamge:
                IntentUtil.start_activity(SfriendActivity.this, AddFriendsActivity.class);
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
