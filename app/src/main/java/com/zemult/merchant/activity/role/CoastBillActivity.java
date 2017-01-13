package com.zemult.merchant.activity.role;

import android.app.Activity;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.BillInfoActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.ManagerCustomerBillList_1_3Request;
import com.zemult.merchant.aip.slash.ManagerCustomerBillInfo_1_3Request;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.apimodel.APIM_UserBillList;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;


public class CoastBillActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {
    public static final String INTENT_MERCHANTID = "merchantId";
    @Bind(R.id.lv_my_bill)
    SmoothListView lv_my_bill;
    @Bind(R.id.lh_btn_back)
    Button lh_btn_back;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    List<M_Bill> mbillList = new ArrayList<M_Bill>();
    CommonAdapter commonAdapter;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;


    ManagerCustomerBillList_1_3Request managerCustomerBillList_1_3Request;
    ManagerCustomerBillInfo_1_3Request managerCustomerBillInfo_1_3Request;
    int typefilter = -1;
    private int page = 1;
    private int merchantId;
    TextView tv_totalcount,tv_totalcoast,tv_totalpay;

//    CakeView mChart;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_coastbill);
    }

    public void init() {
        lhTvTitle.setText("消费报表");
//        Drawable drawable = getResources().getDrawable(R.mipmap.dow_btn);
//        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//        lhTvTitle.setCompoundDrawables(null, null, drawable, null);
//        lhTvTitle.setGravity(Gravity.CENTER_VERTICAL);
        lv_my_bill.setRefreshEnable(true);
        lv_my_bill.setLoadMoreEnable(false);
        lv_my_bill.setSmoothListViewListener(this);
        lv_my_bill.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        merchantId = getIntent().getIntExtra(INTENT_MERCHANTID, -1);
        // 用户的账户明细列表
            managerCustomerBillList_1_3Request(true);
            manager_customer_billInfo_1_3();
            lv_my_bill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    IntentUtil.intStart_activity((Activity) CoastBillActivity.this,
                            BillInfoActivity.class, new Pair<String, Integer>("type", mbillList.get(position - 1).type),
                            new Pair<String, Integer>("billId", mbillList.get(position - 1).billId));
                }
            });


        View lvhead = LayoutInflater.from(this).inflate(R.layout.coastbillhead, null,false);
//        mChart = (CakeView) lvhead.findViewById(R.id.cakeview);

        tv_totalcount = (TextView) lvhead.findViewById(R.id.tv_totalcount);
        tv_totalcoast = (TextView) lvhead.findViewById(R.id.tv_totalcoast);
        tv_totalpay = (TextView) lvhead.findViewById(R.id.tv_totalpay);
        // cv.setStartAngle(60);
        //cv.setSpacingLineColor(Color.parseColor("#000000"));
//        mChart.setTextColor(Color.parseColor("#000000"));
//        List mList = new ArrayList<>();
//        BaseMessage mes = new BaseMessage();
//        mes.percent = 50;
//        mes.content = "我的销量";
//        mes.color = Color.parseColor("#FFA726");
//        mList.add(mes);
//
//        BaseMessage message = new BaseMessage();
//        message.percent = 20;
//        message.content = "其他销量";
//        message.color = Color.parseColor("#fff000");
//        mList.add(message);
//
//        mChart.setCakeData(mList);
        lv_my_bill.addHeaderView(lvhead);
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

    //消费报表-营销经理角色
    private void managerCustomerBillList_1_3Request(boolean isfirstLoad) {
        if (managerCustomerBillList_1_3Request != null) {
            managerCustomerBillList_1_3Request.cancel();
        }


        ManagerCustomerBillList_1_3Request.Input input = new ManagerCustomerBillList_1_3Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.rows = Constants.ROWS;
        if (isfirstLoad) {
            input.page = 1;
        } else {
            input.page = page;
        }

        input.convertJosn();
        managerCustomerBillList_1_3Request = new ManagerCustomerBillList_1_3Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                lv_my_bill.stopRefresh();
                lv_my_bill.stopLoadMore();

            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillList) response).status == 1) {
                    dismissPd();
                    if (page == 1) {
                        mbillList = ((APIM_UserBillList) response).billList;
                        lv_my_bill.setAdapter(commonAdapter = new CommonAdapter<M_Bill>(CoastBillActivity.this, R.layout.item_coastbill, mbillList) {
                            @Override
                            public void convert(CommonViewHolder holder, M_Bill mbill, int position) {
                                String message= DateTimeUtil.strPubDiffTime(mbill.createtime)+mbill.userName+"用户通过你的分享链接在"+mbill.merchantName+
                                        "消费"+mbill.money+"元";
                                holder.setText(R.id.tv_mybill_name, message);
                                holder.setCircleImage(R.id.iv_userhead,mbill.userHead);
                            }
                        });
                    } else {
                        mbillList.addAll(((APIM_UserBillList) response).billList);
                        commonAdapter.notifyDataSetChanged();
                    }
                    if (((APIM_UserBillList) response).maxpage <= page) {
                        lv_my_bill.setLoadMoreEnable(false);
                    } else {
                        lv_my_bill.setLoadMoreEnable(true);
                        page++;
                    }

                } else {
                    ToastUtils.show(CoastBillActivity.this, ((APIM_UserBillList) response).info);
                }
                lv_my_bill.stopRefresh();
                lv_my_bill.stopLoadMore();


            }
        });
        sendJsonRequest(managerCustomerBillList_1_3Request);
    }



    private void manager_customer_billInfo_1_3() {
        if (managerCustomerBillInfo_1_3Request != null) {
            managerCustomerBillInfo_1_3Request.cancel();
        }


        ManagerCustomerBillInfo_1_3Request.Input input = new ManagerCustomerBillInfo_1_3Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }

        input.convertJosn();
        managerCustomerBillInfo_1_3Request = new ManagerCustomerBillInfo_1_3Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tv_totalcount.setText("");
                tv_totalcoast.setText("");
                tv_totalpay.setText("");
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    tv_totalcount.setText(((CommonResult) response).num+"笔");
                    tv_totalcoast.setText(((CommonResult) response).money+"元");
                    tv_totalpay.setText(((CommonResult) response).commissionMoney+"元");
                }
            }
        });
        sendJsonRequest(managerCustomerBillInfo_1_3Request);
    }

    @Override
    public void onRefresh() {
        showPd();
        managerCustomerBillList_1_3Request(true);
    }

    @Override
    public void onLoadMore() {
        managerCustomerBillList_1_3Request(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

}
