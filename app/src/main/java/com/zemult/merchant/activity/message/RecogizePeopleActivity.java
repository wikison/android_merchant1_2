package com.zemult.merchant.activity.message;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.AddFriendNoteActivity;
import com.zemult.merchant.activity.mine.FamiliarPeopleActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.aip.friend.UserCheckBookListFriendRequest;
import com.zemult.merchant.aip.friend.UserCheckBookListRequest;
import com.zemult.merchant.aip.friend.UserFriendAcceptRequest;
import com.zemult.merchant.aip.mine.UserAttractAddRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.bean.ContactDataBean;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ContactsDao;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.lang.reflect.Method;
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
public class RecogizePeopleActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.sml_new_friend)
    SmoothListView lv_newfriend;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_people_num)
    TextView tvPeopleNum;

    @Bind(R.id.ll_unconncet)
    LinearLayout llUnconncet;

    public static final int OP_READ_CONTACTS = 4;
    UserFriendAcceptRequest userFriendAcceptRequest;
    List<M_Userinfo> friendList = new ArrayList<M_Userinfo>();
    int page = 1;
    NewFriendListAdapter adapter;
    UserCheckBookListRequest userCheckBookListRequest;
    List<ContactDataBean> listMembers = new ArrayList<ContactDataBean>();
    private UserAttractAddRequest attractAddRequest; // 添加关注

    String phoneIds;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_new_friend);
    }

    @Override
    public void init() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("通讯录服务管家");
        lv_newfriend.setRefreshEnable(true);
        lv_newfriend.setLoadMoreEnable(false);
        lv_newfriend.setSmoothListViewListener(this);


        lv_newfriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int friendId = ((M_Userinfo) adapter.getItem(position - 1)).getUserId();
                IntentUtil.intStart_activity(RecogizePeopleActivity.this,
                        UserDetailActivity.class, new Pair<String, Integer>("userId", friendId));
            }
        });


        ActivityCompat.requestPermissions(RecogizePeopleActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0) {
//           if(grantResults[0] == PackageManager.PERMISSION_GRANTED) //经测试，对于vivo和redmi永远返回0
//               getContactsYes();
//            else
//               getContactsNo();

            try {
                phoneIds = AppUtils.getPhoneNumbersWithName(RecogizePeopleActivity.this);
                // 只能用这种折中的方法了
                if (StringUtils.isBlank(phoneIds)) {
                    llUnconncet.setVisibility(View.VISIBLE);
                } else {
                    llUnconncet.setVisibility(View.GONE);
                    user_check_bookList_friend();
                }
            }catch (Exception e){
                llUnconncet.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        phoneIds = AppUtils.getPhoneNumbersWithName(RecogizePeopleActivity.this);
//        if (!StringUtils.isEmpty(phoneIds)) {
//            user_check_bookList_friend();
//        }
//
//
//        if (checkOp(OP_READ_CONTACTS) == 0) {
//            llUnconncet.setVisibility(View.GONE);
//        } else {
//            llUnconncet.setVisibility(View.VISIBLE);
//        }
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
    private void user_check_bookList_friend() {
        if (userCheckBookListRequest != null) {
            userCheckBookListRequest.cancel();
        }
        UserCheckBookListRequest.Input input = new UserCheckBookListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.operateUserId = SlashHelper.userManager().getUserId();
        }
        input.phones = phoneIds;
        input.convertJosn();
        userCheckBookListRequest = new UserCheckBookListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lv_newfriend.stopRefresh();
                lv_newfriend.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserFriendList) response).status == 1) {
                    friendList.clear();
                    if (((APIM_UserFriendList) response).size != 0) {
                        tvPeopleNum.setText("您有" + ((APIM_UserFriendList) response).size + "人可能认识");
                        tvPeopleNum.setVisibility(View.VISIBLE);
                    }
                    friendList = ((APIM_UserFriendList) response).userList;
                    adapter = new NewFriendListAdapter(RecogizePeopleActivity.this, friendList);
                    lv_newfriend.setAdapter(adapter);
                    lv_newfriend.setLoadMoreEnable(false);
                } else {
                    ToastUtils.show(RecogizePeopleActivity.this, ((APIM_UserFriendList) response).info);
                }
                lv_newfriend.stopRefresh();
                lv_newfriend.stopLoadMore();
            }
        });
        sendJsonRequest(userCheckBookListRequest);
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
                convertView = LayoutInflater.from(context).inflate(
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
                holder.btn_add_state = (RoundTextView) convertView
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
            if (!StringUtils.isBlank(role.merchantName)) {
                holder.tv_my_remark.setText("来自：" + role.merchantName);
            } else {
                holder.tv_my_remark.setVisibility(View.GONE);
            }
            if (!StringUtils.isEmpty(role.getPhoneNum())) {
                holder.tv_friend_level.setText("手机联系人：" + AppUtils.getConactName(listMembers, role.getPhoneNum()));
            } else {
                holder.tv_friend_level.setVisibility(View.GONE);
            }


            imageManager.loadCircleImage(StringUtils.isEmpty(role.getUserHead()) ? "" : role.getUserHead(), holder.iv_friend_head);
            holder.initView(role);
            return convertView;
        }


        private class Holder implements View.OnClickListener {
            TextView tv_my_name;
            ImageView iv_friend_head;
            RoundTextView btn_add_state;
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
                        addFocus(model.userId);
                        break;
                    default:
                        break;
                }

            }
        }
    }

    private void addFocus(int attractId) {
        showPd();
        if (attractAddRequest != null) {
            attractAddRequest.cancel();
        }
        UserAttractAddRequest.Input input = new UserAttractAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.attractId = attractId; // 被关注的用户id

        input.convertJosn();
        attractAddRequest = new UserAttractAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    user_check_bookList_friend();
                } else {
                    ToastUtils.show(RecogizePeopleActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractAddRequest);
    }


    /**
     * @param op
     * @return
     */
    @TargetApi(19)
    private int checkOp(int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            try {
                AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                Method dispatchMethod = AppOpsManager.class.getMethod(
                        "checkOp", new Class[]{int.class, int.class,
                                String.class});
                int mode = (Integer) dispatchMethod.invoke(
                        appOpsManager,
                        new Object[]{op, Binder.getCallingUid(),
                                getPackageName()});
                return mode;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
