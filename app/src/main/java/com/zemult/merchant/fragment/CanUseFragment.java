package com.zemult.merchant.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.ShopDetailActivity;
import com.zemult.merchant.adapter.minefragment.MyCardListAdapter;
import com.zemult.merchant.aip.mine.UserVoucherInfoRequest;
import com.zemult.merchant.aip.mine.UserVoucherListRequest;
import com.zemult.merchant.app.BaseFragment;
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
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/7/21.
 */
//个人代金券
public class CanUseFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.card_lv)
    SmoothListView cardlv;
    public ImageManager mImageManager;

    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    private Context mContext;
    private int state;   //状态
    private MyCardListAdapter myCardListAdapter;
    private UserVoucherListRequest userVoucherListRequest;//用户的代金券列表
    private UserVoucherInfoRequest userVoucherInfoRequest;//用户的代金券详情信息
    private int page = 1;
    private List<M_Voucher> datas = new ArrayList<M_Voucher>();

    private PopupWindow mPopWindow;
    Bitmap bitmap;


//    private boolean isPrepared;
//    protected WeakReference<View> mRootView;
//    private View view;


    public static CanUseFragment newInstance(int status) {
        Bundle args = new Bundle();
        args.putInt("status", status);
        CanUseFragment fragment = new CanUseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = getArguments().getInt("status");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.canuse_fragment, null);
        ButterKnife.bind(this, view);
        myCardListAdapter = new MyCardListAdapter(getActivity(), datas);
        cardlv.setAdapter(myCardListAdapter);
        cardlv.setRefreshEnable(true);
        cardlv.setLoadMoreEnable(false);
        cardlv.setSmoothListViewListener(this);
        showPd();
        getNetworkData(false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cardlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                int userVoucherId = ((M_Voucher) parent.getAdapter().getItem(position)).userVoucherId;
                getcardinformation(userVoucherId);

            }
        });

    }

    private void getNetworkData(boolean isLoadMore) {
        uservoucherlist(isLoadMore);

    }


    //用户的代金券列表
    private void uservoucherlist(final boolean isLoadMore) {
        if (userVoucherListRequest != null) {
            userVoucherListRequest.cancel();
        }
        UserVoucherListRequest.Input input = new UserVoucherListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }

        input.state = state;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        userVoucherListRequest = new UserVoucherListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                ToastUtils.show(getActivity(),"网络故障");
                cardlv.stopRefresh();
                cardlv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserVoucherList) response).status == 1) {

                    fillAdapter(((APIM_UserVoucherList) response).voucherList,
                            ((APIM_UserVoucherList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(getActivity(), ((APIM_UserVoucherList) response).info);
                }
                dismissPd();
                cardlv.stopRefresh();
                cardlv.stopLoadMore();


            }
        });
        sendJsonRequest(userVoucherListRequest);

    }

    //用户的代金券详情信息
    private void getcardinformation(int userVoucherId) {
        if (userVoucherInfoRequest != null) {
            userVoucherInfoRequest.cancel();
        }
        UserVoucherInfoRequest.Input input = new UserVoucherInfoRequest.Input();

        input.userVoucherId = userVoucherId;
        input.convertJosn();
        userVoucherInfoRequest = new UserVoucherInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserVoucherList) response).status == 1) {
                    int voucherId = (((APIM_UserVoucherList) response).voucherUserInfo).voucherId;
                    String name = (((APIM_UserVoucherList) response).voucherUserInfo).name;
                    String head = (((APIM_UserVoucherList) response).voucherUserInfo).head;
                    double money = (((APIM_UserVoucherList) response).voucherUserInfo).money;
                    double minMoney = (((APIM_UserVoucherList) response).voucherUserInfo).minMoney;
                    String endtime = (((APIM_UserVoucherList) response).voucherUserInfo).endtime;
                    int isUnion = (((APIM_UserVoucherList) response).voucherUserInfo).isUnion;
                    String note = (((APIM_UserVoucherList) response).voucherUserInfo).note;
                    int merchantId = (((APIM_UserVoucherList) response).voucherUserInfo).merchantId;
                    String number = (((APIM_UserVoucherList) response).voucherUserInfo).number;
                    showPopupWindow(head, name, money, minMoney, endtime, isUnion, note, merchantId, number);
                } else {
                    ToastUtils.show(getActivity(), ((APIM_UserVoucherList) response).info);
                }
            }
        });
        sendJsonRequest(userVoucherInfoRequest);

    }

    //弹出代金券信息
    private void showPopupWindow(String head, String name, double money, double minMoney, String endtime, int isUnion, String note, final int merchantId, String number) {

        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.card_info, null);
        //对话框

        final Dialog dialog = new AlertDialog.Builder(getActivity()).create();
        ImageView head_iv = (ImageView) contentView.findViewById(R.id.head_iv);
        mImageManager = new ImageManager(getActivity());
        mImageManager.loadCircleHasBorderImage(head, head_iv, getActivity().getResources().getColor(R.color.white), 2);

        TextView name_tv = (TextView) contentView.findViewById(R.id.name_tv);
        name_tv.setText(name);

        TextView money_tv = (TextView) contentView.findViewById(R.id.money_tv);
        money_tv.setText("￥  " + money);

        TextView minimoney_tv = (TextView) contentView.findViewById(R.id.minmoney_tv);


        minimoney_tv.setText("消费满" + minMoney + "使用");

        TextView endtime_tv = (TextView) contentView.findViewById(R.id.endtime_tv);
        endtime_tv.setText("有效期至" + endtime);

        TextView isunion_tv = (TextView) contentView.findViewById(R.id.isunion_tv);
        if (isUnion == 1) {
            isunion_tv.setText("");
        } else {
            isunion_tv.setText("不与其他优惠同时使用");
        }

        TextView note_tv = (TextView) contentView.findViewById(R.id.note_tv);
        note_tv.setText("备注:   " + note);
//跳到买单里面
        Button go_btn = (Button) contentView.findViewById(R.id.go_btn);
        go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShopDetailActivity.class);
                intent.putExtra(ShopDetailActivity.MERCHANT_ID, merchantId);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });




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


    /**
     * 设置数据
     */
    private void fillAdapter(final List<M_Voucher> list, int maxpage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            cardlv.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
        } else {
            cardlv.setVisibility(View.VISIBLE);
            rlNoData.setVisibility(View.GONE);

            cardlv.setLoadMoreEnable(page < maxpage);
            myCardListAdapter.setData(list, isLoadMore);
        }
//        if (list != null && !list.isEmpty()) {
//            cardlv.setLoadMoreEnable(page < maxpage);
//            myCardListAdapter.setData(list, isLoadMore);
//        }
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        getNetworkData(false);
    }

    @Override
    public void onLoadMore() {
        getNetworkData(true);
    }
}
