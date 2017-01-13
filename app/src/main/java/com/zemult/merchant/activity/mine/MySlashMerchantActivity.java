package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.RecordLifeActivity;
import com.zemult.merchant.activity.slash.MoodDetailActivity;
import com.zemult.merchant.activity.slash.ShopDetailActivity;
import com.zemult.merchant.adapter.slash.MyMerchantAdapter;
import com.zemult.merchant.adapter.slash.MyRecordAdapter;
import com.zemult.merchant.aip.mine.ManagerNewsDelRequest;
import com.zemult.merchant.aip.slash.ManagerNewsInfoRequest;
import com.zemult.merchant.aip.slash.UserIndustryMerchantListRequest;
import com.zemult.merchant.aip.slash.UserNewsListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.model.apimodel.APIM_ManagerNewsInfo;
import com.zemult.merchant.model.apimodel.APIM_ManagerSearchnewsList;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by wikison on 2016/6/17.
 */
public class MySlashMerchantActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {
    public static final String TAG = "MySlashMerchantActivity";
    public static final String INTENT_ROLE_ID = "roleId";
    public static final String INTENT_ROLE_NAME = "roleName";
    int page = 1;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.rv_merchant)
    RecyclerView rvMerchant;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.rl_merchant)
    RelativeLayout rlMerchant;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.floatActionButton)
    FloatingActionButton floatActionButton;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    int iPosition = -1;
    private Context mContext;
    private Activity mActivity;
    private int roleId;
    private int merchantId;
    private List<M_Merchant> merchantList = new ArrayList<>();
    private List<M_News> newsList = new ArrayList<>();
    private MyMerchantAdapter mMerchantAdapter;
    private MyRecordAdapter mRecordAdapter;
    private UserIndustryMerchantListRequest userIndustryMerchantListRequest;
    private UserNewsListRequest userNewsListRequest;
    MyMerchantAdapter.OnItemClickListener onItemClick = new MyMerchantAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, M_Merchant entity) {
            if (entity != null) {
                if (position != iPosition) {
                    iPosition = position;
                    tvName.setText(entity.name);
                    tvAddress.setText(entity.address);
                    merchantId = entity.merchantId;
                    //获取对应记录数据
                    getUserNewsList(merchantId, true, true);
                }
            }
        }
    };
    private int selectedNewsPos;
    private M_News selectedNews;
    private String roleName;
    private ManagerNewsDelRequest delRequest;
    private ManagerNewsInfoRequest newsInfoRequest; // 查看方案详情

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_slash_merchant);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        getMerchantData();
    }

    private void initData() {
        mContext = this;
        mActivity = this;
        roleId = getIntent().getIntExtra(INTENT_ROLE_ID, 0);
        roleName = getIntent().getStringExtra(INTENT_ROLE_NAME);
    }

    private void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(roleName))
            lhTvTitle.setText(roleName);

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvMerchant.setLayoutManager(linearLayoutManager);
        mMerchantAdapter = new MyMerchantAdapter(this, merchantList);
        rvMerchant.setAdapter(mMerchantAdapter);
        mRecordAdapter = new MyRecordAdapter(this, newsList);
        smoothListView.setAdapter(mRecordAdapter);

    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        mMerchantAdapter.setOnItemClickListener(onItemClick);

        // 方案详情
        mRecordAdapter.setOnPlanDetailClickListener(new MyRecordAdapter.OnPlanDetailClickListener() {
            @Override
            public void onPlanDetailClick(int position) {
                selectedNewsPos = position;
                selectedNews = mRecordAdapter.getItem(position);

                M_News news = mRecordAdapter.getItem(position);
                Intent intent = new Intent(mContext, MoodDetailActivity.class);
                intent.putExtra(MoodDetailActivity.INTENT_NEWS, news);
                startActivity(intent);
            }
        });
        // 方案删除
        mRecordAdapter.setOnDelClickListener(new MyRecordAdapter.OnDelClickListener() {
            @Override
            public void onDelClick(final int position) {
                CommonDialog.showDialogListener(mContext,null, "否", "是", "是否删除方案", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                        // 用户删除自己的方案
                        manager_news_del(position);
                    }
                });
            }
        });
    }

    private void manager_news_del(final int position) {
        if (delRequest != null) {
            delRequest.cancel();
        }
        ManagerNewsDelRequest.Input input = new ManagerNewsDelRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.newsId = mRecordAdapter.getItem(position).newsId;

        input.convertJosn();

        delRequest = new ManagerNewsDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    mRecordAdapter.delOneRecord(position);
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(delRequest);
    }

    @OnClick(R.id.floatActionButton)
    public void floatButtonClick() {
        IntentUtil.intStart_activity(this, RecordLifeActivity.class,
                new Pair<String, Integer>("merchantId", merchantId),
                new Pair<String, Integer>("industryId", roleId));
    }

    /**
     * 获取场景数据
     */
    private void getMerchantData() {
        getUserMerchantList();
    }

    /**
     * 获取我的场景列表
     */
    private void getUserMerchantList() {
        if (userIndustryMerchantListRequest != null) {
            userIndustryMerchantListRequest.cancel();
        }
        UserIndustryMerchantListRequest.Input input = new UserIndustryMerchantListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.industryId = roleId;
        input.center = Constants.CENTER;
        input.page = 1;
        input.rows = 1000;

        input.convertJosn();

        userIndustryMerchantListRequest = new UserIndustryMerchantListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    setMerchantAdapter(((APIM_MerchantList) response).merchantList);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
            }
        });
        sendJsonRequest(userIndustryMerchantListRequest);
    }

    private void getUserNewsList(int merchantId, final boolean isNewMerchant, final boolean isLoadMore) {
        if (userNewsListRequest != null) {
            userNewsListRequest.cancel();
        }
        UserNewsListRequest.Input input = new UserNewsListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.industryId = roleId; // 角色id(-1表示全部角色)
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        userNewsListRequest = new UserNewsListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_ManagerSearchnewsList) response).status == 1) {
                    setRecordAdapter(((APIM_ManagerSearchnewsList) response).newsList,
                            ((APIM_ManagerSearchnewsList) response).maxpage, isNewMerchant, isLoadMore
                    );
                } else {
                    ToastUtils.show(mContext, ((APIM_ManagerSearchnewsList) response).info);
                }
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }
        });
        sendJsonRequest(userNewsListRequest);
    }

    @OnClick(R.id.ll_back)
    public void onClick() {
        onBackPressed();
    }

    @OnClick({R.id.ll_back, R.id.rl_merchant, R.id.lh_btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.rl_merchant:
                Intent intent = new Intent(mContext, ShopDetailActivity.class);
                intent.putExtra("merchantId", merchantId);
                startActivity(intent);
                break;
        }
    }

    private void setMerchantAdapter(List<M_Merchant> list) {
        if (list != null && !list.isEmpty()) {
            llRoot.setVisibility(View.VISIBLE);
            mMerchantAdapter.setData(list);
            tvName.setText(list.get(0).name);
            tvAddress.setText(list.get(0).address);
            merchantId = list.get(0).merchantId;
            getUserNewsList(list.get(0).merchantId, true, true);
        }
    }

    private void setRecordAdapter(List<M_News> list, int maxpage, boolean isNewMerchant, boolean isLoadMore) {
        if (list == null || list.isEmpty()) {
            smoothListView.setLoadMoreEnable(false);
            mRecordAdapter.setNoData();
        } else if (list != null && !list.isEmpty()) {
            smoothListView.setLoadMoreEnable(page < maxpage);
            mRecordAdapter.setData(list, isNewMerchant, isLoadMore);
        }

    }

    @Override
    public void onRefresh() {
        getUserNewsList(merchantId, false, false);
    }

    @Override
    public void onLoadMore() {
        getUserNewsList(merchantId, false, true);
    }

    /**
     * 查看方案详情
     */
    private void getNewsInfo(int newsId) {
        if (newsInfoRequest != null) {
            newsInfoRequest.cancel();
        }
        ManagerNewsInfoRequest.Input input = new ManagerNewsInfoRequest.Input();
        input.newsId = newsId;
        input.convertJosn();

        newsInfoRequest = new ManagerNewsInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_ManagerNewsInfo) response).status == 1) {
                    mRecordAdapter.refreshOneRecord(((APIM_ManagerNewsInfo) response).newsInfo, selectedNewsPos);
                } else {
                    ToastUtils.show(mContext, ((APIM_ManagerNewsInfo) response).info);
                }
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }
        });
        sendJsonRequest(newsInfoRequest);
    }

}
