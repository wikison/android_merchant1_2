package com.zemult.merchant.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.AddFriendsActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.TadeAttractListRequest;
import com.zemult.merchant.aip.mine.UserAttractAddRequest;
import com.zemult.merchant.aip.mine.UserAttractDelRequest;
import com.zemult.merchant.aip.mine.UserAttractListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.model.apimodel.APIM_UserFansList;
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
 * Created by wikison on 2016/6/14.    我的关注
 */
public class MyFollowFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {
    @Bind(R.id.lh_btn_back)

    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.concern_lv)
    SmoothListView concernLv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.search_view)
    SearchView searchView;

    List<M_Fan> mDatas = new ArrayList<M_Fan>();
    CommonAdapter commonAdapter;
    UserAttractListRequest userAttractListRequest;
    TadeAttractListRequest tadeAttractListRequest;


    private int page = 1;

    private Context mContext;

    private UserAttractAddRequest attractAddRequest; // 添加关注
    private UserAttractDelRequest attractDelRequest; // 取消关注
    public static final String INTENT_USERID = "userid";
    private int userId;
    private String name;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_my_follow, container, false);
        ButterKnife.bind(this, view);
        return view;

    }

    @Override
    protected void lazyLoad() {
    }



    @Override
    public void onResume() {
        super.onResume();
            userAttractLis();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }


    public void init() {
        mContext = getActivity();
//        userId = getIntent().getIntExtra(INTENT_USERID, -1);
        lhTvTitle.setVisibility(View.VISIBLE);

//        if (userId == -1) {
//            lhTvTitle.setText("我的关注");
//        } else {
            lhTvTitle.setText("熟人");
//        }

        llRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.add_btn);
        llBack.setVisibility(View.GONE);
        concernLv.setRefreshEnable(true);
        concernLv.setLoadMoreEnable(false);
        concernLv.setSmoothListViewListener(this);
        concernLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        concernLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, mDatas.get(position-1).userId);
                intent.putExtra(UserDetailActivity.USER_NAME, mDatas.get(position-1).name);
                intent.putExtra(UserDetailActivity.USER_HEAD, mDatas.get(position-1).head);
                startActivity(intent);
            }
        });

        showPd();


        searchView.setTvCancelVisible(View.GONE);
        searchView.setBgColor(getResources().getColor(R.color.divider_c1));
        searchView.setSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearch(String text) {
                name = text;
                onRefresh();
            }
        });
    }

    //获取我的关注数据
    private void userAttractLis() {
        if (userAttractListRequest != null) {
            userAttractListRequest.cancel();
        }
        UserAttractListRequest.Input input = new UserAttractListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.name = name;
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的页数

        input.convertJosn();

        userAttractListRequest = new UserAttractListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                concernLv.stopRefresh();
                concernLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserFansList) response).status == 1) {
                    if (page == 1) {
                        mDatas = ((APIM_UserFansList) response).userList;//这边是获取到的list

                        if (mDatas == null || mDatas.size() == 0) {
                            concernLv.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            concernLv.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);

                            if (mDatas != null && mDatas.size() > 0) {
                                for (M_Fan bean : mDatas) {
                                    bean.state = 0;
                                }
                            }
                            if (mDatas != null && !mDatas.isEmpty()) {
                                concernLv.setAdapter(commonAdapter = new CommonAdapter<M_Fan>(mContext, R.layout.item_my_follow, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, M_Fan mfollow, final int position) {
                                        if (!TextUtils.isEmpty(mfollow.head)) {
                                            holder.setCircleImage(R.id.iv_follow_head, mfollow.head);
                                        }
                                        // 性别
//                                        if (mfollow.sex == 0)
//                                            holder.setResImage(R.id.iv_sex, R.mipmap.man_icon);
//                                        else
//                                            holder.setResImage(R.id.iv_sex, R.mipmap.girl_icon);
//
//                                        holder.setOnclickListener(R.id.iv_follow_head, new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                Intent intent = new Intent(mContext, UserDetailActivity.class);
//                                                intent.putExtra(UserDetailActivity.USER_ID, mDatas.get(position).userId);
//                                                intent.putExtra(UserDetailActivity.USER_NAME, mDatas.get(position).name);
//                                                intent.putExtra(UserDetailActivity.USER_HEAD, mDatas.get(position).head);
//                                                startActivity(intent);
//                                            }
//                                        });

                                        if(mfollow.experience <= 0){
                                            holder.setImageResource(R.id.iv_sex, R.mipmap.xinshou_icon);
                                            holder.setText(R.id.tv_describe, "新手");
                                        }else if(mfollow.experience > 0 && mfollow.experience <= 9999){
                                            holder.setImageResource(R.id.iv_sex, R.mipmap.tong_icon2);
                                            holder.setText(R.id.tv_describe, "铜牌");
                                        }else if(mfollow.experience > 9999 && mfollow.experience <= 99999){
                                            holder.setImageResource(R.id.iv_sex, R.mipmap.yin_icon2);
                                            holder.setText(R.id.tv_describe, "银牌");
                                        }
                                        else if(mfollow.experience > 99999 && mfollow.experience <= 999999){
                                            holder.setImageResource(R.id.iv_sex, R.mipmap.jingpai_icon2);
                                            holder.setText(R.id.tv_describe, "金牌");
                                        }else {
                                            holder.setImageResource(R.id.iv_sex, R.mipmap.demon_icon2);
                                            holder.setText(R.id.tv_describe, "钻石");
                                        }
//                                        if (!TextUtils.isEmpty(mfollow.note))
//                                            holder.setText(R.id.tv_describe, mfollow.note);
                                        switch (mfollow.state){
                                            case 0:
                                                holder.setImageResource(R.id.iv_status, R.mipmap.kongxian_icon);
                                                holder.setText(R.id.tv_status, "空闲");
                                                holder.setTextColor(R.id.tv_status, 0xff5eb31b);
                                                break;
                                            case 1:
                                                holder.setImageResource(R.id.iv_status, R.mipmap.xiuxi_icon);
                                                holder.setText(R.id.tv_status, "休息");
                                                holder.setTextColor(R.id.tv_status, 0xff999999);
                                                break;
                                            case 2:
                                                holder.setImageResource(R.id.iv_status, R.mipmap.manglu_icon);
                                                holder.setText(R.id.tv_status, "忙碌");
                                                holder.setTextColor(R.id.tv_status, 0xffeb4f38);
                                                break;
                                        }

                                        holder.setText(R.id.tv_follow_name, mfollow.name);
                                        holder.setFocusState(R.id.tv_state, mfollow.state, R.id.iv_state);

                                        holder.setOnclickListener(R.id.ll_state, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (mDatas.get(position).state == 0) {       //已关注的状态
                                                    cancleFocus(mDatas.get(position).userId, position); //取消关注操作

                                                } else if (mDatas.get(position).state == 1) {         //未关注的状态
                                                    addFous(mDatas.get(position).userId, position);//添加关注网络操作

                                                }
                                            }
                                        });
                                    }
                                });

                            }
                        }
                    } else {
                        mDatas.addAll(((APIM_UserFansList) response).userList);
                        commonAdapter.notifyDataSetChanged();

                    }
                    if (((APIM_UserFansList) response).maxpage <= page) {
                        concernLv.setLoadMoreEnable(false);
                    } else {
                        concernLv.setLoadMoreEnable(true);
                        page++;
                    }

                } else {
                    ToastUtils.show(mContext, ((APIM_UserFansList) response).info);
                }
                concernLv.stopRefresh();
                concernLv.stopLoadMore();

            }
        });
        sendJsonRequest(userAttractListRequest);
    }




    // 用户添加关注
    private void addFous(int userId, final int position) {
        showPd();
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
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(mContext, "添加成功");

                    mDatas.get(position).state = 0;
                    commonAdapter.setDataChanged(mDatas);  //改变按钮样式

                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractAddRequest);
    }


    // 用户取消关注
    private void cancleFocus(int userId, final int position) {
        showPd();
        if (attractDelRequest != null) {
            attractDelRequest.cancel();
        }
        UserAttractDelRequest.Input input = new UserAttractDelRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.attractId = userId; // 被关注的用户id
        input.convertJosn();
        attractDelRequest = new UserAttractDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(mContext, "取消成功");
                    mDatas.get(position).state = 1;
                    commonAdapter.setDataChanged(mDatas);  //改变按钮样式

                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractDelRequest);
    }

    @OnClick({R.id.ll_back, R.id.lh_btn_back, R.id.iv_right, R.id.ll_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_right:
            case R.id.ll_right:
                startActivity(new Intent(mContext, AddFriendsActivity.class));
                break;
        }
    }

    @Override
    public void onRefresh() {
            page = 1;
            userAttractLis();
    }

    @Override
    public void onLoadMore() {
            userAttractLis();
    }
}
