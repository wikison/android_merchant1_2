package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.SharePreInviteActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.UserPreInvitationListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Invitation;
import com.zemult.merchant.model.apimodel.APIM_MyInvitationList;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class MyInviteActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.myinvite_lv)
    SmoothListView myinviteLv;
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    public ImageManager imageManager;
    private int page = 1;

    private Context mContext;
    CommonAdapter commonAdapter;
    UserPreInvitationListRequest userPreInvitationListRequest;
    private List<M_Invitation> mDatas = new ArrayList<M_Invitation>();
    int preId;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_invite);
    }

    @Override
    public void init() {
        iv.setVisibility(View.VISIBLE);
        llRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.jiahao);
        lhTvTitle.setText("我发起的预邀");
        mContext = this;
        imageManager = new ImageManager(this);
        myinviteLv.setRefreshEnable(true);
        myinviteLv.setLoadMoreEnable(false);
        myinviteLv.setSmoothListViewListener(this);
        myinviteLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        showPd();

        userPreInvitationList();


        myinviteLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                M_Invitation mReservation = (M_Invitation) commonAdapter.getItem(position - 1);
                preId=mReservation.preId;
                IntentUtil.start_activity(MyInviteActivity.this, BaseWebViewActivity.class, new Pair<String, String>("titlename", "预邀函详情"), new Pair<String, String>("url", Constants.PERINVITATIONFEEDBACKINFO+preId));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    private void userPreInvitationList() {

        if (userPreInvitationListRequest != null) {
            userPreInvitationListRequest.cancel();
        }
        UserPreInvitationListRequest.Input input = new UserPreInvitationListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();
        userPreInvitationListRequest = new UserPreInvitationListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                myinviteLv.stopRefresh();
                myinviteLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_MyInvitationList) response).status == 1) {
                    if (page == 1) {
                        mDatas = ((APIM_MyInvitationList) response).invitationList;
                        if (mDatas == null || mDatas.size() == 0) {
                            myinviteLv.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            myinviteLv.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);
                            if (mDatas != null && !mDatas.isEmpty()) {
                                myinviteLv.setAdapter(commonAdapter = new CommonAdapter<M_Invitation>(MyInviteActivity.this, R.layout.item_my_invite, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, final M_Invitation mInvitation, final int position) {

//                                        if (position == 0) {
//                                            holder.setViewVisible(R.id.v1);
//                                        } else if (position > 0) {
//                                            holder.setViewGone(R.id.v1);
//                                        }
                                        if (!TextUtils.isEmpty(mInvitation.titleIcon)) {
                                            holder.setImage2(R.id.head_iv, mInvitation.titleIcon);
                                        }

                                        holder.setText(R.id.theme_tv, "活动主题:  " + mInvitation.titleName);
                                        long a = DateTimeUtil.getIntervalDays(DateTimeUtil.getCurrentDate(), mInvitation.invitationTime.substring(0, 10));

                                        if (a < 1 && a >= 0) {
                                            holder.setText(R.id.day_tv, "今天");
                                            holder.setText(R.id.time_tv, mInvitation.invitationTime.substring(11, 16));
                                        } else if (a >= 1 && a < 2) {
                                            holder.setText(R.id.day_tv, "昨天");
                                            holder.setText(R.id.time_tv, mInvitation.invitationTime.substring(11, 16));
                                        } else {
                                            holder.setText(R.id.day_tv, DateTimeUtil.getChinaDayofWeek(mInvitation.invitationTime.substring(0, 10)));
                                            holder.setText(R.id.time_tv, mInvitation.invitationTime.substring(5, 10));
                                        }
                                    }

                                });
                            }

                        }
                    } else {
                        mDatas.addAll(((APIM_MyInvitationList) response).invitationList);
                        commonAdapter.notifyDataSetChanged();
                    }

                    if (((APIM_MyInvitationList) response).maxpage <= page) {
                        myinviteLv.setLoadMoreEnable(false);
                    } else {
                        myinviteLv.setLoadMoreEnable(true);
                        page++;
                        Log.i("sunjian", "" + page);
                    }
                } else {
                    ToastUtils.show(MyInviteActivity.this, ((APIM_MyInvitationList) response).info);
                }
                myinviteLv.stopRefresh();
                myinviteLv.stopLoadMore();
            }
        });
        sendJsonRequest(userPreInvitationListRequest);

    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_right, R.id.ll_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_right:

            case R.id.ll_right:
                Intent it = new Intent(this, SharePreInviteActivity.class);
                startActivity(it);
                break;
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        userPreInvitationList();

    }

    @Override
    public void onLoadMore() {
        userPreInvitationList();
    }
}
