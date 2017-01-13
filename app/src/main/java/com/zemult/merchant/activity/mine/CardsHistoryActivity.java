package com.zemult.merchant.activity.mine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.MerchantVoucherInfoRequest;
import com.zemult.merchant.aip.mine.MerchantVoucherListRecordRequest;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Voucher;
import com.zemult.merchant.model.apimodel.APIM_UserVoucherList;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/8/1.
 */
//卡券历史记录
public class CardsHistoryActivity extends MBaseActivity implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.history_lv)
    SmoothListView historyLv;
    public static final String INTENT_MERCHANTID = "merchantId";
    MerchantVoucherListRecordRequest merchantVoucherListRecordRequest;

    MerchantVoucherInfoRequest merchantVoucherInfoRequest;

    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    private int page = 1;
    int merchantId = 0;
    List<M_Voucher> datas = new ArrayList<M_Voucher>();
    CommonAdapter commonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardshistory);
        ButterKnife.bind(this);
        lhTvTitle.setText("优惠券使用记录");

        showPd();
        merchantId = getIntent().getIntExtra(INTENT_MERCHANTID, 0);
        historyLv.setRefreshEnable(true);
        historyLv.setLoadMoreEnable(false);
        historyLv.setSmoothListViewListener(this);
        merchantVoucherListRecord();

        historyLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int voucherId = datas.get(position - 1).voucherId;
                getcardinformation(voucherId);
            }
        });
    }

    //获取商家的代金券使用记录列表
    private void merchantVoucherListRecord() {
        if (merchantVoucherListRecordRequest != null) {
            merchantVoucherListRecordRequest.cancel();
        }


        MerchantVoucherListRecordRequest.Input input = new MerchantVoucherListRecordRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }

        input.merchantId = merchantId;
        input.page = page;
        input.rows = Constants.ROWS;
        input.convertJosn();
        merchantVoucherListRecordRequest = new MerchantVoucherListRecordRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                historyLv.stopRefresh();
                historyLv.stopLoadMore();
                ToastUtils.show(CardsHistoryActivity.this,"网络故障");
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserVoucherList) response).status == 1) {
                    dismissPd();
                    if (page == 1) {
                        datas = ((APIM_UserVoucherList) response).voucherUserList;
                        if (datas == null || datas.size() == 0) {
                            historyLv.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            historyLv.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);
                            historyLv.setAdapter(commonAdapter = new CommonAdapter<M_Voucher>(CardsHistoryActivity.this, R.layout.cardshistory_item, datas) {
                                @Override
                                public void convert(CommonViewHolder holder, M_Voucher mVoucher, int position) {

                                    if (!TextUtils.isEmpty(mVoucher.userHead)) {
                                        holder.setCircleImage(R.id.head_iv, mVoucher.userHead);
                                    }

                                    holder.setText(R.id.name_tv, mVoucher.userName);
                                    holder.setText(R.id.number_tv, "使用了" + mVoucher.number + "号优惠券");
                                    holder.setText(R.id.time_tv, mVoucher.useTime);
                                }
                            });
                        }
                    }else {
                        datas.addAll(((APIM_UserVoucherList) response).voucherUserList);
                        commonAdapter.notifyDataSetChanged();

                    }
                    if (((APIM_UserVoucherList) response).maxpage <= page) {
                        historyLv.setLoadMoreEnable(false);
                    } else {
                        historyLv.setLoadMoreEnable(true);
                        page++;
                    }

                } else {
                    ToastUtils.show(CardsHistoryActivity.this, ((APIM_UserVoucherList) response).info);
                }

                historyLv.stopRefresh();
                historyLv.stopLoadMore();

            }
        });

        sendJsonRequest(merchantVoucherListRecordRequest);
    }


    //代金券详情信息
    private void getcardinformation(int voucherId) {
        if (merchantVoucherInfoRequest != null) {
            merchantVoucherInfoRequest.cancel();
        }
        MerchantVoucherInfoRequest.Input input = new MerchantVoucherInfoRequest.Input();

        input.voucherId = voucherId;
        input.convertJosn();
        merchantVoucherInfoRequest = new MerchantVoucherInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
            @Override
            public void onResponse(Object response) {
                if (((APIM_UserVoucherList) response).status == 1) {
                    M_Voucher voucherInfo = ((APIM_UserVoucherList) response).voucherInfo;
                    showPopupWindow(voucherInfo);
                } else {
                    ToastUtils.show(CardsHistoryActivity.this, ((APIM_UserVoucherList) response).info);
                }
            }
        });
        sendJsonRequest(merchantVoucherInfoRequest);

    }


    //弹出代金券信息
    private void showPopupWindow(M_Voucher voucherInfo) {

        View contentView = LayoutInflater.from(this).inflate(R.layout.shangjiacard_info, null);
        //对话框
        final Dialog dialog = new AlertDialog.Builder(this).create();
        ImageView head_iv = (ImageView) contentView.findViewById(R.id.head_iv);
        imageManager = new ImageManager(this);
        imageManager.loadCircleHasBorderImage(voucherInfo.head, head_iv, getResources().getColor(R.color.white), 2);
        TextView name_tv = (TextView) contentView.findViewById(R.id.name_tv);
        name_tv.setText(voucherInfo.name);

        TextView money_tv = (TextView) contentView.findViewById(R.id.money_tv);
        money_tv.setText("￥  " + voucherInfo.money);

        TextView minimoney_tv = (TextView) contentView.findViewById(R.id.minmoney_tv);
        minimoney_tv.setText("消费满" + voucherInfo.minMoney + "使用");

        TextView endtime_tv = (TextView) contentView.findViewById(R.id.endtime_tv);
        endtime_tv.setText("有效期至" + voucherInfo.endtime);

        TextView isunion_tv = (TextView) contentView.findViewById(R.id.isunion_tv);
        if (voucherInfo.isUnion == 1) {
            isunion_tv.setText("");
        } else {
            isunion_tv.setText("不与其他优惠同时使用");
        }

        TextView note_tv = (TextView) contentView.findViewById(R.id.note_tv);
        note_tv.setText("备注:   " + voucherInfo.note);

        TextView num_tv = (TextView) contentView.findViewById(R.id.num_tv);
        num_tv.setText("共设置" + voucherInfo.num + "张");

        TextView usenum_tv = (TextView) contentView.findViewById(R.id.usenum_tv);
        usenum_tv.setText("已使用" + voucherInfo.useNum + "张");


        ImageView close = (ImageView) contentView.findViewById(R.id.close_iv);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
        dialog.getWindow().setContentView(contentView);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    @Override
    public void onRefresh() {
        page = 1;
        merchantVoucherListRecord();
    }

    @Override
    public void onLoadMore() {
        merchantVoucherListRecord();
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
}
