package com.zemult.merchant.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.MerchantCardsAdapter;
import com.zemult.merchant.aip.mine.MerchantVoucherListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Voucher;
import com.zemult.merchant.model.apimodel.APIM_UserVoucherList;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/10/19.
 */

public class MerchantCardsFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {


    @Bind(R.id.card_lv)
    SmoothListView cardLv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    MerchantCardsAdapter merchantCardsAdapter;
    public ImageManager imageManager;
    private int type;   //状态
    private List<M_Voucher> datas = new ArrayList<M_Voucher>();
    MerchantVoucherListRequest merchantVoucherListRequest;
    private int page = 1;
    private int merchantId;

    public static MerchantCardsFragment newInstance(int merchantId,int type) {

        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("merchantId",merchantId);
        MerchantCardsFragment fragment = new MerchantCardsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        merchantId=getArguments().getInt("merchantId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.canuse_fragment, null);
        ButterKnife.bind(this, view);
        merchantCardsAdapter = new MerchantCardsAdapter(getActivity(), datas);
        cardLv.setAdapter(merchantCardsAdapter);
        cardLv.setRefreshEnable(true);
        cardLv.setLoadMoreEnable(false);
        cardLv.setSmoothListViewListener(this);
        showPd();
        getNetworkData(false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        cardLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                M_Voucher m_voucher = (M_Voucher) parent.getAdapter().getItem(position);

                showPopupWindow(m_voucher);
            }
        });

    }

        //弹出代金券信息
    private void showPopupWindow(M_Voucher m_voucher) {

        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.shangjiacard_info, null);
        //对话框
        final Dialog dialog = new AlertDialog.Builder(getActivity()).create();
        ImageView head_iv = (ImageView) contentView.findViewById(R.id.head_iv);
        imageManager = new ImageManager(getActivity());
        imageManager.loadCircleHasBorderImage(m_voucher.head, head_iv, getResources().getColor(R.color.white), 2);
        TextView name_tv = (TextView) contentView.findViewById(R.id.name_tv);
        name_tv.setText(m_voucher.name);

        TextView money_tv = (TextView) contentView.findViewById(R.id.money_tv);
        money_tv.setText("￥  " + m_voucher.money);

        TextView minimoney_tv = (TextView) contentView.findViewById(R.id.minmoney_tv);
        minimoney_tv.setText("消费满" + m_voucher.minMoney + "使用");

        TextView endtime_tv = (TextView) contentView.findViewById(R.id.endtime_tv);
        endtime_tv.setText("有效期至" + m_voucher.endtime);

        TextView isunion_tv = (TextView) contentView.findViewById(R.id.isunion_tv);
        if (m_voucher.isUnion == 1) {
            isunion_tv.setText("");
        } else {
            isunion_tv.setText("不与其他优惠同时使用");
        }

        TextView note_tv = (TextView) contentView.findViewById(R.id.note_tv);
        note_tv.setText("备注:   " + m_voucher.note);

        TextView num_tv = (TextView) contentView.findViewById(R.id.num_tv);
        num_tv.setText("共设置" + m_voucher.num + "张");

        TextView usenum_tv = (TextView) contentView.findViewById(R.id.usenum_tv);
        usenum_tv.setText("已使用" + m_voucher.useNum + "张");


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

    private void getNetworkData(boolean isLoadMore) {
        merchantVoucherList(isLoadMore);

    }

    //获取商户代金券列表
    private void merchantVoucherList(final boolean isLoadMore) {
        if (merchantVoucherListRequest != null) {
            merchantVoucherListRequest.cancel();
        }
        MerchantVoucherListRequest.Input input = new MerchantVoucherListRequest.Input();

        input.type = type;
        input.merchantId = merchantId;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        merchantVoucherListRequest = new MerchantVoucherListRequest(input, new ResponseListener() {
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
                    ToastUtils.show(getActivity(), ((APIM_UserVoucherList) response).info);
                }
                cardLv.stopRefresh();
                cardLv.stopLoadMore();
            }
        });
        sendJsonRequest(merchantVoucherListRequest);

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




    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onRefresh() {
        merchantVoucherList(false);

    }

    @Override
    public void onLoadMore() {
        merchantVoucherList(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
