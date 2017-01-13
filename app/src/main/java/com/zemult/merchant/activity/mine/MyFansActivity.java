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
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.TadeFansListRequest;
import com.zemult.merchant.aip.mine.UserAttractAddRequest;
import com.zemult.merchant.aip.mine.UserAttractDelRequest;
import com.zemult.merchant.aip.mine.UserFansListRequest;
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
//我的粉丝
public class MyFansActivity extends MBaseActivity implements SmoothListView.ISmoothListViewListener {
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
    @Bind(R.id.concern_lv)
    SmoothListView fansLv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.search_view)
    SearchView searchView;
    private List<M_Fan> mDatas = new ArrayList<M_Fan>();
    private Context mContext;
    private int page = 1;
    private UserAttractAddRequest attractAddRequest; // 添加关注
    private UserAttractDelRequest attractDelRequest; // 取消关注
    public static final String INTENT_USERID = "userid";
    private int userId;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        setContentView(R.layout.activity_my_follow);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        mContext = this;
        imageManager = new ImageManager(this);


        userId = getIntent().getIntExtra(INTENT_USERID, -1);
        lhTvTitle.setVisibility(View.VISIBLE);
        if (userId == -1) {
            lhTvTitle.setText("我的粉丝");
        } else {
            lhTvTitle.setText("TA的粉丝");
        }
        fansLv.setRefreshEnable(true);
        fansLv.setLoadMoreEnable(false);
        fansLv.setSmoothListViewListener(this);
        fansLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        showPd();
        if (userId == -1) {
            userFansLis();
        } else {
            tadeFansList();
        }

        llRight.setVisibility(View.INVISIBLE);
        ivRight.setImageResource(R.mipmap.tianjia2_icon);
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


    //获取粉丝数据
    private void userFansLis() {
        if (userFansListRequest != null) {
            userFansListRequest.cancel();
        }
        UserFansListRequest.Input input = new UserFansListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.name = name;
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();
        userFansListRequest = new UserFansListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                fansLv.stopRefresh();
                fansLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserFansList) response).status == 1) {
                    if (page == 1) {
                        mDatas = ((APIM_UserFansList) response).fansList;
                        if (mDatas == null || mDatas.size() == 0) {
                            fansLv.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            fansLv.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);
                            if (mDatas != null && !mDatas.isEmpty()) {
                                fansLv.setAdapter(commonAdapter = new CommonAdapter<M_Fan>(MyFansActivity.this, R.layout.item_my_follow, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, M_Fan mfollow, final int position) {

                                        if (!TextUtils.isEmpty(mfollow.head)) {
                                            holder.setCircleImage(R.id.iv_follow_head, mfollow.head);
                                        }
                                        // 性别
                                        if (mfollow.sex == 0)
                                            holder.setResImage(R.id.iv_sex, R.mipmap.man_icon);
                                        else
                                            holder.setResImage(R.id.iv_sex, R.mipmap.girl_icon);

                                        holder.setOnclickListener(R.id.iv_follow_head, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(mContext, UserDetailActivity.class);
                                                intent.putExtra(UserDetailActivity.USER_ID, mDatas.get(position).userId);
                                                intent.putExtra(UserDetailActivity.USER_NAME, mDatas.get(position).name);
                                                intent.putExtra(UserDetailActivity.USER_HEAD, mDatas.get(position).head);
                                                startActivity(intent);
                                            }
                                        });
                                        if(!TextUtils.isEmpty(mfollow.note))
                                            holder.setText(R.id.tv_describe, mfollow.note);

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
                    ToastUtils.show(MyFansActivity.this, ((APIM_UserFansList) response).info);
                }
                fansLv.stopRefresh();
                fansLv.stopLoadMore();

            }
        });

        sendJsonRequest(userFansListRequest);
    }

    //获取TA的粉丝数据
    private void tadeFansList() {

        if (tadeFansListRequest != null) {
            tadeFansListRequest.cancel();
        }
        TadeFansListRequest.Input input = new TadeFansListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.operateUserId = SlashHelper.userManager().getUserId();
        }
        input.userId = userId;
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();

        tadeFansListRequest = new TadeFansListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                fansLv.stopRefresh();
                fansLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserFansList) response).status == 1) {
                    if (page == 1) {
                        mDatas = ((APIM_UserFansList) response).fansList;
                        if (mDatas == null || mDatas.size() == 0) {
                            fansLv.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            fansLv.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);

                            if (mDatas != null && !mDatas.isEmpty()) {
                                fansLv.setAdapter(commonAdapter = new CommonAdapter<M_Fan>(MyFansActivity.this, R.layout.item_my_follow, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, M_Fan mfollow, final int position) {

//                                        if (!TextUtils.isEmpty(mfollow.head)) {
//                                            holder.setCircleImage(R.id.iv_fan_head, mfollow.head);
//                                        }
//
//                                        holder.setOnclickListener(R.id.iv_fan_head, new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                Intent intent = new Intent(mContext, UserDetailActivity.class);
//                                                intent.putExtra(UserDetailActivity.USER_ID, mDatas.get(position).userId);
//                                                intent.putExtra(UserDetailActivity.USER_NAME, mDatas.get(position).name);
//                                                intent.putExtra(UserDetailActivity.USER_HEAD, mDatas.get(position).head);
//                                                startActivity(intent);
//                                            }
//                                        });
//                                        holder.setText(R.id.sign_tv, mfollow.sign);
//                                        holder.setText(R.id.tv_fan_name, mfollow.name);
//                                        holder.setFocusState(R.id.tv_state, mfollow.state);
//                                        if (mDatas.get(position).userId == SlashHelper.userManager().getUserId()) {
//                                            holder.setViewGone(R.id.tv_state);
//
//                                        }

                                        holder.setOnclickListener(R.id.tv_state, new View.OnClickListener() {
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
                    ToastUtils.show(MyFansActivity.this, ((APIM_UserFansList) response).info);
                }
                fansLv.stopRefresh();
                fansLv.stopLoadMore();

            }
        });

        sendJsonRequest(tadeFansListRequest);
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
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_right:
            case R.id.ll_right:
                startActivity(new Intent(mContext, AddFriendsActivity.class));
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (userId == -1) {
            page = 1;
            userFansLis();
        } else {
            page = 1;
            tadeFansList();
        }
    }

    @Override
    public void onLoadMore() {
        if (userId == -1) {
            userFansLis();
        } else {
            tadeFansList();
        }
    }


}

