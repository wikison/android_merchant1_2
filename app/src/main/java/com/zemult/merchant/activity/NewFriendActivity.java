package com.zemult.merchant.activity;

import android.content.Context;
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
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.aip.friend.UserFriendAcceptRequest;
import com.zemult.merchant.aip.friend.UserRequestFriendListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by wikison on 2016/6/15.
 */
public class NewFriendActivity extends BaseActivity implements  SmoothListView.ISmoothListViewListener {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.sml_new_friend)
    SmoothListView lv_newfriend;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    UserFriendAcceptRequest  userFriendAcceptRequest;
    List<M_Friend> friendList =new ArrayList<M_Friend>();
    int page=1;
    NewFriendListAdapter adapter;
    UserRequestFriendListRequest userRequestFriendListRequest;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_new_friend);
    }

    @Override
    public void init() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("新的好友");
        lv_newfriend.setRefreshEnable(true);
        lv_newfriend.setLoadMoreEnable(false);
        lv_newfriend.setSmoothListViewListener(this);


        lv_newfriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int friendId = ((M_Friend)adapter.getItem(position-1)).getFriendId();
                IntentUtil.intStart_activity(NewFriendActivity.this,
                        UserDetailActivity.class,new Pair<String, Integer>("userId", friendId));
            }
        });
        user_request_friendList( true);
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
        private void user_request_friendList(final boolean  isFresh ) {
            if (userRequestFriendListRequest != null) {
                userRequestFriendListRequest.cancel();
            }
            UserRequestFriendListRequest.Input input = new UserRequestFriendListRequest.Input();
            if (SlashHelper.userManager().getUserinfo() != null) {
                input.userId = SlashHelper.userManager().getUserId();
            }
            input.lasttime = "2015-10-10 10:10:10";//	前端最新一条记录的时间(格式为:yyyy-MM-dd HH:mm:ss)
            if(isFresh){
                input.page = 1;
            }
            else{
                input.page = page;
            }

            input.rows = Constants.ROWS;
            input.convertJosn();
            userRequestFriendListRequest = new UserRequestFriendListRequest(input,new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    lv_newfriend.stopRefresh();
                    lv_newfriend.stopLoadMore();
                }

                @Override
                public void onResponse(Object response) {
                    if (((APIM_UserFriendList) response).status == 1) {
                        if(isFresh){
                            friendList.clear();
                            friendList=((APIM_UserFriendList) response).friendList;
                            adapter = new NewFriendListAdapter(NewFriendActivity.this,friendList);
                            lv_newfriend.setAdapter(adapter);
                        }
                        else{
                            friendList.addAll(((APIM_UserFriendList) response).friendList);
                            adapter.notifyDataSetChanged();
                        }

                        if(((APIM_UserFriendList) response).maxpage<=page){
                            lv_newfriend.setLoadMoreEnable(false);
                        }
                        else{
                            ++page;
                            lv_newfriend.setLoadMoreEnable(true);
                        }

                    } else {
                        ToastUtils.show(NewFriendActivity.this, ((APIM_UserFriendList) response).info);
                    }
                    lv_newfriend.stopRefresh();
                    lv_newfriend.stopLoadMore();
                }
            });
            sendJsonRequest(userRequestFriendListRequest);
        }


//接受好友请求
    private void user_friend_accept(final M_Friend mfried) {
        if (userFriendAcceptRequest != null) {
            userFriendAcceptRequest.cancel();
        }

        UserFriendAcceptRequest.Input input = new UserFriendAcceptRequest.Input();

        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.friendId = mfried.getFriendId();
        input.convertJosn();
        userFriendAcceptRequest = new UserFriendAcceptRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("已添加");
                    friendList.remove(mfried);
                    adapter.notifyDataSetChanged();

                } else {
                    ToastUtils.show(NewFriendActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userFriendAcceptRequest);
    }


    @Override
    public void onRefresh() {
        user_request_friendList(true);
    }

    @Override
    public void onLoadMore() {
        user_request_friendList(false);
    }


    public class NewFriendListAdapter extends BaseAdapter {
        private Context context;
        private List<M_Friend> list;

        public NewFriendListAdapter(Context context, List<M_Friend> list) {
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
                        R.layout.item_new_friend,// 文件名
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
                holder.iv_friend_sex = (ImageView) convertView
                        .findViewById(R.id.iv_friend_sex);
                holder.btn_add_state = (Button) convertView
                        .findViewById(R.id.btn_add_state);
                holder.tv_remove_btn = (TextView) convertView
                        .findViewById(R.id.remove_btn);
                holder.tv_add_state = (TextView) convertView
                        .findViewById(R.id.tv_add_state);
                holder.tv_my_remark = (TextView) convertView
                        .findViewById(R.id.tv_my_remark);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            M_Friend role = list.get(position);
            holder.tv_my_name.setText(role.getName());
            holder.tv_my_remark.setText(role.getNote());

            if(role.getSex()==0){
                holder.iv_friend_sex.setBackgroundResource(R.mipmap.man_icon);
            }
            else{
                holder.iv_friend_sex.setBackgroundResource(R.mipmap.woman);
            }
            // 状态(1:等待验证;2:被请求中--等待接收请求)
            if (role.getState() == 1) {
                holder.tv_add_state.setVisibility(View.VISIBLE);
                holder.btn_add_state.setVisibility(View.INVISIBLE);
            }else if(role.getState() == 2) {
                holder.tv_add_state.setVisibility(View.INVISIBLE);
                holder.btn_add_state.setVisibility(View.VISIBLE);
            }

            imageManager.loadCircleImage(role.getHead(), holder.iv_friend_head);
            holder.initView(role);
            return convertView;
        }


        private class Holder implements View.OnClickListener {
            TextView tv_my_name;
            ImageView iv_friend_head;
            ImageView iv_friend_sex;
            Button btn_add_state;
            TextView tv_remove_btn;
            TextView tv_add_state;
            TextView tv_my_remark;

            M_Friend model;

            public void initView(M_Friend model) {
                this.model = model;
                btn_add_state.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_add_state:
//                        ToastUtil.showMessage(model.getName());
                        user_friend_accept(model);
                        break;
                    default:
                        break;
                }

            }
        }

    }
}
