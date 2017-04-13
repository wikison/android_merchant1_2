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
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.User2SaleUserFanListRequest;
import com.zemult.merchant.aip.mine.UserAttractAddRequest;
import com.zemult.merchant.aip.mine.UserAttractDelRequest;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.model.apimodel.APIM_UserFansList;
import com.zemult.merchant.util.ImageManager;
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
//1026客户管理
public class MyFansActivity extends MBaseActivity implements SmoothListView.ISmoothListViewListener {
    public ImageManager imageManager;
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
        lhTvTitle.setText("SCRM客户管理");
        fansLv.setRefreshEnable(true);
        fansLv.setLoadMoreEnable(false);
        fansLv.setSmoothListViewListener(this);
        fansLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        showPd();
        user2_saleUser_fansList();
        searchView.setTvCancelVisible(View.GONE);
        searchView.setBgColor(getResources().getColor(R.color.divider_c1));
        searchView.setSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearch(String text) {
                name = text;
                onRefresh();
            }

            @Override
            public void onClear() {
                name = "";
                onRefresh();
            }
        });
    }

    private User2SaleUserFanListRequest userFansListRequest;

    //服务管家的SCRM列表         对该服务管家 关注过的/成功预约/完成支付/赞赏过的
    private void user2_saleUser_fansList() {
        if (userFansListRequest != null) {
            userFansListRequest.cancel();
        }
        User2SaleUserFanListRequest.Input input = new User2SaleUserFanListRequest.Input();
        input.saleUserId = userId;
        input.name = name;
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();
        userFansListRequest = new User2SaleUserFanListRequest(input, new ResponseListener() {
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
                        mDatas = ((APIM_UserFansList) response).userList;
                        if (mDatas == null || mDatas.isEmpty()) {
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
                                        holder.setText(R.id.tv_follow_name, mfollow.name);
                                        holder.setViewGone(R.id.iv_sex);
                                        holder.setViewGone(R.id.iv_status);

                                        holder.setOnclickListener(R.id.iv_follow_head, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent IMkitintent = LoginSampleHelper.getInstance().getIMKit().getChattingActivityIntent(mDatas.get(position).userId + "", Urls.APP_KEY);
                                                Bundle bundle=new Bundle();
                                                bundle.putInt("serviceId", mDatas.get(position).userId);
                                                IMkitintent.putExtras(bundle);
                                                startActivity(IMkitintent);
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

    @OnClick({R.id.ll_back, R.id.lh_btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onRefresh() {
        user2_saleUser_fansList();
    }

    @Override
    public void onLoadMore() {
        user2_saleUser_fansList();
    }
}

