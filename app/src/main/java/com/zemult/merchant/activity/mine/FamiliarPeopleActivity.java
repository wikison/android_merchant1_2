package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.AddFriendsActivity;
import com.zemult.merchant.activity.message.RecogizePeopleActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.TadeFansListRequest;
import com.zemult.merchant.aip.mine.UserAttractAddRequest;
import com.zemult.merchant.aip.mine.UserAttractDelRequest;
import com.zemult.merchant.aip.mine.UserFansListRequest;
import com.zemult.merchant.aip.mine.UserSysSaleUserListNumRequest;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.model.apimodel.APIM_UserFansList;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SearchView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by wikison on 2016/6/14.
 */
//可能熟悉的人
public class FamiliarPeopleActivity extends MBaseActivity implements SmoothListView.ISmoothListViewListener {
    public ImageManager imageManager;
    UserFansListRequest userFansListRequest;
    TadeFansListRequest tadeFansListRequest;
    CommonAdapter commonAdapter;

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_people_num)
    TextView tvPeopleNum;
    @Bind(R.id.concern_lv)
    SmoothListView fansLv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.rel_invitepeople)
    RelativeLayout relInvitepeople;
    UserSysSaleUserListNumRequest userSysSaleUserListNumRequest;


    private List<M_Fan> mDatas = new ArrayList<M_Fan>();
    private Context mContext;
    private int page = 1;
    private UserAttractAddRequest attractAddRequest; // 添加关注
    private UserAttractDelRequest attractDelRequest; // 取消关注

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        setContentView(R.layout.activity_familiar_people);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        mContext = this;
        imageManager = new ImageManager(this);
        lhTvTitle.setText("可能熟悉的人");

        fansLv.setRefreshEnable(true);
        fansLv.setLoadMoreEnable(false);
        fansLv.setSmoothListViewListener(this);
        user_sys_saleUserList_num();


    }


    //获取 用户的 可能熟悉的人(推荐服务管家)
    private void userFansLis() {
        if (userFansListRequest != null) {
            userFansListRequest.cancel();
        }
        UserFansListRequest.Input input = new UserFansListRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();
        userFansListRequest = new UserFansListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fansLv.setVisibility(View.GONE);
                rlNoData.setVisibility(View.VISIBLE);
                fansLv.stopRefresh();
                fansLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserFansList) response).status == 1) {
                    if (page == 1) {
                        mDatas = ((APIM_UserFansList) response).userList;
                        if (mDatas == null || mDatas.size() == 0) {
                            fansLv.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            fansLv.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);
                            if (mDatas != null && !mDatas.isEmpty()) {

                                fansLv.setAdapter(commonAdapter = new CommonAdapter<M_Fan>(FamiliarPeopleActivity.this, R.layout.item_familiar_people, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, M_Fan mfollow, final int position) {

                                        if (!TextUtils.isEmpty(mfollow.head)) {
                                            holder.setCircleImage(R.id.iv_follow_head, mfollow.head);
                                        }

                                        holder.setOnclickListener(R.id.rel_layout, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(mContext, UserDetailActivity.class);
                                                intent.putExtra(UserDetailActivity.USER_ID, mDatas.get(position).userId);
                                                intent.putExtra(UserDetailActivity.USER_NAME, mDatas.get(position).name);
                                                intent.putExtra(UserDetailActivity.USER_HEAD, mDatas.get(position).head);
                                                startActivity(intent);
                                            }
                                        });
                                        if(!TextUtils.isEmpty(mfollow.merchantName))
                                            holder.setText(R.id.tv_rname,"来自："+ mfollow.merchantName);

                                        holder.setText(R.id.tv_follow_name, mfollow.userName);
                                        holder.setOnclickListener(R.id.ll_state, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                    addFous(mDatas.get(position).userId, position);//添加关注网络操作
                                            }
                                        });

                                    }

                                });
                            }

                        }
                    } else {
                        mDatas.addAll(((APIM_UserFansList) response).fansList);
                        commonAdapter.notifyDataSetChanged();
                    }

                    if (((APIM_UserFansList) response).maxpage <= page) {
                        fansLv.setLoadMoreEnable(false);
                    } else {
                        fansLv.setLoadMoreEnable(true);
                        page++;

                        Log.i("sunjian", "" + page);
                    }

                } else {
                    ToastUtils.show(FamiliarPeopleActivity.this, ((APIM_UserFansList) response).info);
                }
                fansLv.stopRefresh();
                fansLv.stopLoadMore();

            }
        });

        sendJsonRequest(userFansListRequest);
    }



    //获取 用户的 可能熟悉的人(推荐服务管家)的数量
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
                    if(((CommonResult) response).num!=0){
                        tvPeopleNum.setText("您有"+((CommonResult) response).num+"人可能认识");
                    }
                    else{
                        tvPeopleNum.setText("");
                    }

                    userFansLis();
                }
            }
        });
        sendJsonRequest(userSysSaleUserListNumRequest);
    }

    // 用户添加关注
    private void addFous(int userId, final int position) {
        if (attractAddRequest != null) {
            attractAddRequest.cancel();
        }
        UserAttractAddRequest.Input input = new UserAttractAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.attractId = userId; // 被关注的用户id

        input.convertJosn();
        attractAddRequest = new UserAttractAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(mContext, "添加成功");
                    onRefresh();
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractAddRequest);
    }




    @OnClick({R.id.ll_back, R.id.lh_btn_back,R.id.rel_invitepeople})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.rel_invitepeople:
                startActivity(new Intent(mContext, RecogizePeopleActivity.class));
                break;
        }
    }

    @Override
    public void onRefresh() {
            page = 1;
        user_sys_saleUserList_num();
    }

    @Override
    public void onLoadMore() {
            userFansLis();
    }


}

