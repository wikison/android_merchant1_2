package com.zemult.merchant.activity.message;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.AddFriendNoteActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.aip.friend.UserCheckBookListFriendRequest;
import com.zemult.merchant.aip.friend.UserFriendAcceptRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.bean.ContactDataBean;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ContactsDao;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.taobao.av.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 可能认识的人
 */
public class RecogizePeopleActivity extends BaseActivity implements  SmoothListView.ISmoothListViewListener {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.sml_new_friend)
    SmoothListView lv_newfriend;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    UserFriendAcceptRequest  userFriendAcceptRequest;
    List<M_Userinfo> friendList =new ArrayList<M_Userinfo>();
    int page=1;
    NewFriendListAdapter adapter;
    UserCheckBookListFriendRequest userCheckBookListFriendRequest;
    List<ContactDataBean> listMembers=new ArrayList<ContactDataBean>();
    String phoneIds;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_new_friend);
    }

    @Override
    public void init() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("可能认识的人");
        lv_newfriend.setRefreshEnable(true);
        lv_newfriend.setLoadMoreEnable(false);
        lv_newfriend.setSmoothListViewListener(this);


        lv_newfriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int friendId = ((M_Userinfo)adapter.getItem(position-1)).getUserId();
                IntentUtil.intStart_activity(RecogizePeopleActivity.this,
                        UserDetailActivity.class,new Pair<String, Integer>("userId", friendId));
            }
        });

        ContactsDao util = new ContactsDao(this);
        util.getAllContact(listMembers);

    }

    @Override
    protected void onResume() {
        super.onResume();
        phoneIds=AppUtils.getPhoneNumbers(RecogizePeopleActivity.this);
        if(!StringUtils.isEmpty(phoneIds)){
            user_check_bookList_friend();
        }

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
        }
    }

        //新的朋友(接受列表)
        private void user_check_bookList_friend( ) {
            showPd();
            if (userCheckBookListFriendRequest != null) {
                userCheckBookListFriendRequest.cancel();
            }
            UserCheckBookListFriendRequest.Input input = new UserCheckBookListFriendRequest.Input();
            if (SlashHelper.userManager().getUserinfo() != null) {
                input.operateUserId = SlashHelper.userManager().getUserId();
            }
            input.phones =phoneIds ;
            input.convertJosn();
            userCheckBookListFriendRequest = new UserCheckBookListFriendRequest(input,new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    lv_newfriend.stopRefresh();
                    lv_newfriend.stopLoadMore();
                }

                @Override
                public void onResponse(Object response) {
                    if (((APIM_UserFriendList) response).status == 1) {
                            friendList.clear();
                            friendList=((APIM_UserFriendList) response).userList;
                            adapter = new NewFriendListAdapter(RecogizePeopleActivity.this,friendList);
                            lv_newfriend.setAdapter(adapter);

                            lv_newfriend.setLoadMoreEnable(false);

                    } else {
                        ToastUtils.show(RecogizePeopleActivity.this, ((APIM_UserFriendList) response).info);
                    }
                    dismissPd();
                    lv_newfriend.stopRefresh();
                    lv_newfriend.stopLoadMore();
                }
            });
            sendJsonRequest(userCheckBookListFriendRequest);
        }

    @Override
    public void onRefresh() {
        user_check_bookList_friend();
    }

    @Override
    public void onLoadMore() {


    }


    public class NewFriendListAdapter extends BaseAdapter {
        private Context context;
        private List<M_Userinfo> list;

        public NewFriendListAdapter(Context context, List<M_Userinfo> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i("position -->>", String.valueOf(position));
            Holder holder;
            if (convertView == null) {
                convertView= LayoutInflater.from(context).inflate(
                        R.layout.item_recognizepeople,// 文件名
                        null);
//                View menuView = LayoutInflater.from(context).inflate(
//                        R.layout.item_swipe_operation, null);
//                menuView.findViewById(R.id.share_btn).setVisibility(View.GONE);
//                convertView = new SwipeItemLayout(view, menuView, null, null);
                holder = new Holder();
                holder.tv_my_name = (TextView) convertView
                        .findViewById(R.id.tv_my_name);
                holder.iv_friend_head = (ImageView) convertView
                        .findViewById(R.id.iv_friend_head);
                holder.btn_add_state = (Button) convertView
                        .findViewById(R.id.btn_add_state);
                holder.tv_remove_btn = (TextView) convertView
                        .findViewById(R.id.remove_btn);
                holder.tv_friend_level = (TextView) convertView
                        .findViewById(R.id.tv_friend_level);

                holder.tv_friend_ship = (TextView) convertView
                        .findViewById(R.id.tv_friend_ship);

                holder.tv_add_state = (TextView) convertView
                        .findViewById(R.id.tv_add_state);
                holder.tv_my_remark = (TextView) convertView
                        .findViewById(R.id.tv_my_remark);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            M_Userinfo role = list.get(position);
            holder.tv_my_name.setText(role.getUserName());
            if(!StringUtils.isEmpty(role.getPhoneNum())){
                holder.tv_my_remark.setText("手机联系人："+AppUtils.getConactName(listMembers,role.getPhoneNum()));
            }
            holder.tv_friend_level.setText(role.userLevel+"");

            // 状态(0:不是好友且无申请1:等待验证;2:被请求中--等待接收请求)
            if (role.getState() == 0) {
                holder.tv_add_state.setVisibility(View.INVISIBLE);
                holder.btn_add_state.setVisibility(View.VISIBLE);
            }else {
                holder.tv_add_state.setVisibility(View.VISIBLE);
                holder.btn_add_state.setVisibility(View.INVISIBLE);
            }
            imageManager.loadCircleImage(StringUtil.isEmpty(role.getUserHead())?"":role.getUserHead(), holder.iv_friend_head);
            holder.initView(role);
            return convertView;
        }


        private class Holder implements View.OnClickListener {
            TextView tv_my_name;
            ImageView iv_friend_head;
            Button btn_add_state;
            TextView tv_remove_btn;
            TextView tv_add_state;
            TextView tv_friend_ship;
            TextView tv_friend_level;
            TextView tv_my_remark;

            M_Userinfo model;

            public void initView(M_Userinfo model) {
                this.model = model;
                btn_add_state.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_add_state:
                        Intent i = new Intent(RecogizePeopleActivity.this, AddFriendNoteActivity.class);
                        i.putExtra("friendId", model.getUserId());
                        startActivity(i);
                        break;
                    default:
                        break;
                }

            }
        }

    }
}
