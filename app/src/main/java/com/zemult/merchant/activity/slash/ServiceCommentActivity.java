package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.hedgehog.ratingbar.RatingBar;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.slash.CommentsListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Comment;
import com.zemult.merchant.model.apimodel.APIM_ManagerNewsCommentList;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class ServiceCommentActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.comment_lv)
    SmoothListView commentLv;
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    CommentsListRequest commentsListRequest;
    List<M_Comment> mDatas = new ArrayList<M_Comment>();
    CommonAdapter commonAdapter;
    private int page = 1;
    int saleUserId;
    int merchantId;
    public static final String INTENT_SALEUSERID = "saleUserId";
    public static final String INTENT_MERCHANTID = "merchantId";

    private Context mContext;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_service_comment);
    }

    @Override
    public void init() {
        lhTvTitle.setText("评价");
        mContext = this;
        saleUserId = getIntent().getIntExtra(INTENT_SALEUSERID, 0);
        merchantId = getIntent().getIntExtra(INTENT_MERCHANTID, 0);
        commentLv.setRefreshEnable(true);
        commentLv.setLoadMoreEnable(false);
        commentLv.setSmoothListViewListener(this);
        commentLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if (saleUserId > 0) {
            commentsList();
        }
    }

    private void commentsList() {
        showPd();
        if (commentsListRequest != null) {
            commentsListRequest.cancel();
        }
        CommentsListRequest.Input input = new CommentsListRequest.Input();
        input.saleUserId = saleUserId;
        input.merchantId = merchantId;
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的页数
        input.convertJosn();
        commentsListRequest = new CommentsListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                commentLv.stopRefresh();
                commentLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_ManagerNewsCommentList) response).status == 1) {
                    if (page == 1) {
                        mDatas = ((APIM_ManagerNewsCommentList) response).commentList;
                        if (mDatas == null || mDatas.size() == 0) {
                            commentLv.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            commentLv.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);
                            if (mDatas != null && !mDatas.isEmpty()) {
                                commentLv.setAdapter(commonAdapter = new CommonAdapter<M_Comment>(ServiceCommentActivity.this, R.layout.item_service_comment, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, final M_Comment mComment, final int position) {
                                        if (!TextUtils.isEmpty(mComment.head)) {
                                            holder.setCircleImage(R.id.head_iv, mComment.head);
                                            holder.setOnclickListener(R.id.head_iv, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    List<String> list = new ArrayList<String>();
                                                    list.add(mComment.head);
                                                    AppUtils.toImageDetial(ServiceCommentActivity.this, 0, list, null, false, false, true, 0, 0);

                                                }
                                            });
                                        }
                                        holder.setText(R.id.name_tv, mComment.name);

                                        if (mComment.type == 0) {
                                            holder.setText(R.id.type_tv, "服务评价");
                                        } else {
                                            holder.setText(R.id.type_tv, "消费评价");
                                        }
                                        RatingBar ratingbar = holder.getView(R.id.ratingbar);
                                        ratingbar.setStar(mComment.comment);
                                        if (!TextUtils.isEmpty(mComment.createTime)) {
                                            holder.setText(R.id.time_tv, mComment.createTime.substring(0, 10));
                                        }
                                        holder.setText(R.id.note_tv, mComment.note);
                                    }

                                });
                            }

                        }
                    } else {
                        mDatas.addAll(((APIM_ManagerNewsCommentList) response).commentList);
                        commonAdapter.notifyDataSetChanged();
                    }

                    if (((APIM_ManagerNewsCommentList) response).maxpage <= page) {
                        commentLv.setLoadMoreEnable(false);
                    } else {
                        commentLv.setLoadMoreEnable(true);
                        page++;
                    }
                } else {
                    ToastUtils.show(ServiceCommentActivity.this, ((APIM_ManagerNewsCommentList) response).info);
                }
                commentLv.stopRefresh();
                commentLv.stopLoadMore();


            }
        });
        sendJsonRequest(commentsListRequest);


    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back})
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
        page = 1;
        commentsList();
    }

    @Override
    public void onLoadMore() {
        commentsList();

    }
}
