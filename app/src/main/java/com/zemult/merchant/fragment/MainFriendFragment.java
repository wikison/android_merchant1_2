package com.zemult.merchant.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.IYWContactService;
import com.alibaba.mobileim.contact.YWContactManager;
import com.alibaba.mobileim.fundamental.widget.YWAlertDialog;
import com.alibaba.mobileim.utility.ToastHelper;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.AddFriendsActivity;
import com.zemult.merchant.activity.ConversationFragmentActivity;
import com.zemult.merchant.activity.NewFriendActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.friend.FriendAdapter;
import com.zemult.merchant.aip.friend.UserFriendDelRequest;
import com.zemult.merchant.aip.friend.UserFriendListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.demo.TribeActivity;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.DensityUtil;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/6/3.
 */
public class MainFriendFragment extends BaseFragment {
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


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mainfriend_fragment, container, false);
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
        user_friendList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }


    public void initView() {
        lhBtnBack.setVisibility(View.INVISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("杠友");
        lh_btn_rightiamge.setVisibility(View.VISIBLE);
        lh_btn_rightiamge.setBackgroundResource(R.mipmap.gengduo_black_icon);
        lh_btn_rightiamge.setWidth(DensityUtil.dip2px(getActivity(), 50));
        lh_btn_rightiamge.setHeight(DensityUtil.dip2px(getActivity(), 50));

        View  lvhead=LayoutInflater.from(getActivity()).inflate( R.layout.sfriendhead, null);
        LinearLayout ll_new_friend=(LinearLayout)lvhead.findViewById(R.id.ll_new_friend);
        LinearLayout ll_tribe=(LinearLayout)lvhead.findViewById(R.id.ll_tribe);
        ll_new_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.start_activity(getActivity(), NewFriendActivity.class);
            }
        });

        ll_tribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.start_activity(getActivity(), TribeActivity.class);
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
                IntentUtil.intStart_activity(getActivity(),
                        UserDetailActivity.class,new Pair<String, Integer>("userId", friendId));
            }

        });
        lvFriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String friendId = adapter.getItem(position-1).getFriendId()+"";
                final IYWContactService contactService = LoginSampleHelper.getInstance().getIMKit().getContactService();
                final String[] items = new String[1];
                boolean isBlocked = contactService.isBlackContact(friendId, Urls.APP_KEY);
                if (isBlocked) {
                    items[0] = "移除黑名单";
                } else {
                    items[0] = "加入黑名单";
                }
                if(!YWContactManager.isBlackListEnable()) {
                    YWContactManager.enableBlackList();
                }
                //此处为示例代码
                new YWAlertDialog.Builder(getActivity())
                        .setTitle(adapter.getItem(position-1).getName())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (items[which].equals("加入黑名单")) {
                                    contactService.addBlackContact(friendId, Urls.APP_KEY, new IWxCallback() {
                                        @Override
                                        public void onSuccess(Object... result) {
                                            IYWContact iywContact = (IYWContact) result[0];
                                            YWLog.i("MainFriendFragment", "加入黑名单成功, id = " + iywContact.getUserId() + ", appkey = " + iywContact.getAppKey());
                                            ToastHelper.showToastMsg(getActivity(), "加入黑名单成功");//, id = " + iywContact.getUserId() + ", appkey = " + iywContact.getAppKey()
                                        }

                                        @Override
                                        public void onError(int code, String info) {
                                            YWLog.i("MainFriendFragment", "加入黑名单失败，code = " + code + ", info = " + info);
                                            ToastHelper.showToastMsg(getActivity(), "加入黑名单失败");//，code = " + code + ", info = " + info
                                        }

                                        @Override
                                        public void onProgress(int progress) {

                                        }
                                    });
                                } else if (items[which].equals("移除黑名单")) {
                                    contactService.removeBlackContact(friendId, Urls.APP_KEY, new IWxCallback() {
                                        @Override
                                        public void onSuccess(Object... result) {
                                            IYWContact iywContact = (IYWContact) result[0];
                                            YWLog.i("MainFriendFragment", "移除黑名单成功,  id = " + iywContact.getUserId() + ", appkey = " + iywContact.getAppKey());
                                            ToastHelper.showToastMsg(getActivity(), "移除黑名单成功");//,  id = " + iywContact.getUserId() + ", appkey = " + iywContact.getAppKey()
                                        }

                                        @Override
                                        public void onError(int code, String info) {
                                            YWLog.i("MainFriendFragment", "移除黑名单失败，code = " + code + ", info = " + info);
                                            ToastHelper.showToastMsg(getActivity(), "移除黑名单失败");//，code = " + code + ", info = " + info
                                        }

                                        @Override
                                        public void onProgress(int progress) {

                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create().show();
                return true;
            }
        });
    }


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
                    filtercontacts=(ArrayList<M_Friend>)(((APIM_UserFriendList) response).friendList).clone();
                    getContactList();
                    Collections.sort(filtercontacts, new Comparator<M_Friend>() {
                        @Override
                        public int compare(M_Friend lhs, M_Friend rhs) {
                            return lhs.getHeader().compareTo(rhs.getHeader());
                        }
                    });

                    adapter = new FriendAdapter(getActivity(),
                            R.layout.item_friend, filtercontacts);
                    lvFriends.setAdapter(adapter);
                } else {
                    ToastUtils.show(getActivity(), ((APIM_UserFriendList) response).info);
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
                    ToastUtils.show(getActivity(), ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userFriendDelRequest);
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({ R.id.lh_btn_rightiamge})
    public void viewClick(View v) {
        switch (v.getId()) {
            case R.id.lh_btn_rightiamge:
                showPopupWindow(getActivity(),v);
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

    private void showPopupWindow(final Context context,View rightButton) {
        //设置contentView
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_conversationhead, null);
        final PopupWindow   mPopWindow = new PopupWindow(contentView,
                DensityUtil.dip2px(context, 120), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopWindow.setOutsideTouchable(true);

        //设置各个控件的点击响应
        LinearLayout l1 = (LinearLayout) contentView.findViewById(R.id.l1);
        LinearLayout l2 = (LinearLayout) contentView.findViewById(R.id.l2);
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context,AddFriendsActivity.class);
                context.startActivity(intent);
                mPopWindow.dismiss();
            }
        });
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context,ConversationFragmentActivity.class);
                context.startActivity(intent);
                mPopWindow.dismiss();
            }
        });
        //显示PopupWindow
        mPopWindow.showAsDropDown(rightButton,-160,-26);

    }
}
