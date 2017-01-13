package com.zemult.merchant.activity.mine.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.MerchantCardsAdapter;
import com.zemult.merchant.aip.mine.MerchantVoucherInfoRequest;
import com.zemult.merchant.aip.mine.MerchantVoucherListUserRequest;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Voucher;
import com.zemult.merchant.model.apimodel.APIM_UserVoucherList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
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
//商家优惠券
public class PayCouponListActivity extends MBaseActivity implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.history_lv)
    SmoothListView cardLv;
    public static final String INTENT_MERCHANTID = "merchantId";
    MerchantVoucherListUserRequest merchantVoucherListUserRequest;
    MerchantVoucherInfoRequest merchantVoucherInfoRequest;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @Bind(R.id.rtv_unuse)
    RoundTextView rtvUnuse;
    double paymoney;

    private int page = 1;
    int merchantId = 0;
    List<M_Voucher> datas = new ArrayList<M_Voucher>();
    MerchantCardsAdapter merchantCardsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardshistory);
        ButterKnife.bind(this);
        lhTvTitle.setText("优惠券列表");
        rtvUnuse.setVisibility(View.VISIBLE);
        showPd();
        merchantId = getIntent().getIntExtra(INTENT_MERCHANTID, 0);
        paymoney=getIntent().getDoubleExtra("paymoney",0);
        merchantCardsAdapter = new MerchantCardsAdapter(PayCouponListActivity.this, datas);
        cardLv.setAdapter(merchantCardsAdapter);
        cardLv.setRefreshEnable(true);
        cardLv.setLoadMoreEnable(false);
        cardLv.setSmoothListViewListener(this);
        merchantVoucherListUserRequest(false);

        cardLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int voucherId = datas.get(position - 1).voucherId;
                if(paymoney<datas.get(position - 1).minMoney){
                    ToastUtil.showMessage("实付金额需要"+datas.get(position - 1).minMoney+"元");
                }
                else {
                    Intent intent =new Intent();
                    intent.putExtra("isVoucher",1);//是否使用代金券(0:否,1:是)
                    intent.putExtra("voucherUserId",voucherId);
                    intent.putExtra("couponmoney",datas.get(position - 1).money);
                    setResult(RESULT_OK,intent);
                    finish();
                }

//                getcardinformation(voucherId);
            }
        });
    }

    //获取商户代金券列表
    private void merchantVoucherListUserRequest(final boolean isLoadMore) {
        if (merchantVoucherListUserRequest != null) {
            merchantVoucherListUserRequest.cancel();
        }

        MerchantVoucherListUserRequest.Input input = new MerchantVoucherListUserRequest.Input();
        if(SlashHelper.userManager().getUserinfo()!=null){
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.merchantId = merchantId;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        merchantVoucherListUserRequest = new MerchantVoucherListUserRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                cardLv.stopRefresh();
                cardLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserVoucherList) response).status == 1) {
                    fillAdapter(((APIM_UserVoucherList) response).voucherList,
                            ((APIM_UserVoucherList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(PayCouponListActivity.this, ((APIM_UserVoucherList) response).info);
                }
                cardLv.stopRefresh();
                cardLv.stopLoadMore();
            }
        });
        sendJsonRequest(merchantVoucherListUserRequest);

    }

    /**
     * 设置数据
     */
    private void fillAdapter(final List<M_Voucher> list, int maxpage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            cardLv.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
        } else {
            cardLv.setVisibility(View.VISIBLE);
            rlNoData.setVisibility(View.GONE);
            cardLv.setLoadMoreEnable(page < maxpage);
            merchantCardsAdapter.setData(list, isLoadMore);
        }
    }

    //代金券详情信息
//    private void getcardinformation(int voucherId) {
//        if (merchantVoucherInfoRequest != null) {
//            merchantVoucherInfoRequest.cancel();
//        }
//        MerchantVoucherInfoRequest.Input input = new MerchantVoucherInfoRequest.Input();
//
//        input.voucherId = voucherId;
//        input.convertJosn();
//        merchantVoucherInfoRequest = new MerchantVoucherInfoRequest(input, new ResponseListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                if (((APIM_UserVoucherList) response).status == 1) {
//                    M_Voucher voucherInfo = ((APIM_UserVoucherList) response).voucherInfo;
////                    showPopupWindow(voucherInfo);
//
//                } else {
//                    ToastUtils.show(PayCouponListActivity.this, ((APIM_UserVoucherList) response).info);
//                }
//            }
//        });
//        sendJsonRequest(merchantVoucherInfoRequest);
//
//    }


    //弹出代金券信息
//    private void showPopupWindow(M_Voucher m_voucher) {
//
//        View contentView = LayoutInflater.from(PayCouponListActivity.this).inflate(R.layout.shangjiacard_info, null);
//        //对话框
//        final Dialog dialog = new AlertDialog.Builder(PayCouponListActivity.this).create();
//        ImageView head_iv = (ImageView) contentView.findViewById(R.id.head_iv);
//        imageManager = new ImageManager(PayCouponListActivity.this);
//        imageManager.loadCircleHasBorderImage(m_voucher.head, head_iv, getResources().getColor(R.color.white), 2);
//        TextView name_tv = (TextView) contentView.findViewById(R.id.name_tv);
//        name_tv.setText(m_voucher.name);
//
//        TextView money_tv = (TextView) contentView.findViewById(R.id.money_tv);
//        money_tv.setText("￥  " + m_voucher.money);
//
//        TextView minimoney_tv = (TextView) contentView.findViewById(R.id.minmoney_tv);
//        minimoney_tv.setText("消费满" + m_voucher.minMoney + "使用");
//
//        TextView endtime_tv = (TextView) contentView.findViewById(R.id.endtime_tv);
//        endtime_tv.setText("有效期至" + m_voucher.endtime);
//
//        TextView isunion_tv = (TextView) contentView.findViewById(R.id.isunion_tv);
//        if (m_voucher.isUnion == 1) {
//            isunion_tv.setText("");
//        } else {
//            isunion_tv.setText("不与其他优惠同时使用");
//        }
//
//        TextView note_tv = (TextView) contentView.findViewById(R.id.note_tv);
//        note_tv.setText("备注:   " + m_voucher.note);
//
//        TextView num_tv = (TextView) contentView.findViewById(R.id.num_tv);
//        num_tv.setText("共设置" + m_voucher.num + "张");
//
//        TextView usenum_tv = (TextView) contentView.findViewById(R.id.usenum_tv);
//        usenum_tv.setText("已使用" + m_voucher.useNum + "张");
//
//
//        ImageView close = (ImageView) contentView.findViewById(R.id.close_iv);
//
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        dialog.show();
//        dialog.getWindow().setContentView(contentView);
//
//
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//    }

    @Override
    public void onRefresh() {
        page = 1;
        merchantVoucherListUserRequest(false);
    }

    @Override
    public void onLoadMore() {
        merchantVoucherListUserRequest(true);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back,R.id.rtv_unuse})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.rtv_unuse:
                Intent intent =new Intent();
                intent.putExtra("isVoucher",0);//是否使用代金券(0:否,1:是)
                intent.putExtra("voucherUserId",0);
                intent.putExtra("couponmoney",0);
                setResult(RESULT_OK,intent);
                finish();
                break;

        }
    }
}
