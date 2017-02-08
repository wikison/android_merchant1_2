package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.MerchantBillListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.apimodel.APIM_UserBillList;
import com.zemult.merchant.view.RiseNumberTextView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/10/19.
 */
//商家账户
public class BusinessAccountActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_money)
    RiseNumberTextView tvMoney;
    @Bind(R.id.lv_business_bill)
    SmoothListView lvBusinessBill;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.btn_withdrawals)
    RoundTextView btnWithdrawals;

    private int merchantId;
    private double merchantMoney;
    public static final String INTENT_MERCHANTID = "merchantId";
    List<M_Bill> mbillList = new ArrayList<M_Bill>();
    private MerchantBillListRequest merchantBillListRequest;
    private int page = 1;
    CommonAdapter commonAdapter;

    @Override
    public void setContentView() {
        setContentView(R.layout.businessaccount_activity);

    }

    @Override
    public void init() {
        lhTvTitle.setText("商户账户");
        merchantId = getIntent().getIntExtra(INTENT_MERCHANTID, -1);
        lvBusinessBill.setRefreshEnable(true);
        lvBusinessBill.setLoadMoreEnable(false);
        lvBusinessBill.setSmoothListViewListener(this);
        showPd();
        merchantBillListRequest(true);

        lvBusinessBill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int type = mbillList.get(position - 1).type;
                int billId = mbillList.get(position - 1).billId;
                Intent it = new Intent(BusinessAccountActivity.this, BusinessAccountDetailActivity.class);
                it.putExtra(BusinessAccountDetailActivity.INTENT_TYPE, type);
                it.putExtra(BusinessAccountDetailActivity.INTENT_BILLID, billId);
                startActivity(it);
            }
        });


    }


    //商家报表列表
    private void merchantBillListRequest(boolean isfirstLoad) {
        if (merchantBillListRequest != null) {
            merchantBillListRequest.cancel();
        }
        MerchantBillListRequest.Input input = new MerchantBillListRequest.Input();
        input.merchantId = merchantId;
        input.type = -1;//类型(-1:全部,0:收入,1:支出)
        input.rows = Constants.ROWS;
        if (isfirstLoad) {
            input.page = 1;
        } else {
            input.page = page;
        }

        input.convertJosn();
        merchantBillListRequest = new MerchantBillListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                lvBusinessBill.stopRefresh();
                lvBusinessBill.stopLoadMore();

            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillList) response).status == 1) {
                    dismissPd();
                    merchantMoney = ((APIM_UserBillList) response).money;
                    tvMoney.setText("" + ((APIM_UserBillList) response).money);

                    if (page == 1) {
                        mbillList = ((APIM_UserBillList) response).billList;

                        if (mbillList == null || mbillList.size() == 0) {
                            lvBusinessBill.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            lvBusinessBill.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);
                        }


                        lvBusinessBill.setAdapter(commonAdapter = new CommonAdapter<M_Bill>(BusinessAccountActivity.this, R.layout.businessaccount_item, mbillList) {
                            @Override
                            public void convert(CommonViewHolder holder, M_Bill mbill, int position) {
                                //类型(0:交易,1:提现)
                                holder.setText(R.id.note_tv, mbill.note);
                                if (mbill.type == 0) {
                                    holder.setText(R.id.tv_type, "交易入账");
                                }
                                if (mbill.type == 1) {
                                    holder.setText(R.id.tv_type, "提现");
                                }
                                holder.setText(R.id.time_tv, mbill.createtime);

                                if (mbill.inCome == 0) {  //收入
                                    holder.setText(R.id.money_tv, "+" + mbill.money);
                                }

                                if (mbill.inCome == 1) {  //支出
                                    holder.setText(R.id.money_tv, "-" + mbill.money);
                                }

                            }
                        });
                    } else {

                        mbillList.addAll(((APIM_UserBillList) response).billList);
                    }
                    if (((APIM_UserBillList) response).maxpage <= page) {
                        lvBusinessBill.setLoadMoreEnable(false);
                    } else {
                        lvBusinessBill.setLoadMoreEnable(true);
                        page++;
                    }

                } else {
                    ToastUtils.show(BusinessAccountActivity.this, ((APIM_UserBillList) response).info);
                }
                lvBusinessBill.stopRefresh();
                lvBusinessBill.stopLoadMore();


            }
        });
        sendJsonRequest(merchantBillListRequest);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_withdrawals})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_withdrawals:
                intent = new Intent(BusinessAccountActivity.this, MerchantWithdrawalsActivity.class);
                intent.putExtra("merchantId", merchantId);
                intent.putExtra("merchantMoney", merchantMoney);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh() {
        showPd();
        merchantBillListRequest(true);
    }

    @Override
    public void onLoadMore() {
        merchantBillListRequest(false);
    }

}
