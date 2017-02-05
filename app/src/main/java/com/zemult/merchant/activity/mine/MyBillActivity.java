package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.Gravity;
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
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.MerchantBillListRequest;
import com.zemult.merchant.aip.mine.UserBillListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.apimodel.APIM_UserBillList;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by wikison on 2016/6/14.
 */
public class MyBillActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {
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
    @Bind(R.id.rel_filter_all)
    RelativeLayout relFilterAll;
    @Bind(R.id.iv_filter_all)
    ImageView ivFilterAll;
    @Bind(R.id.rel_filter_in)
    RelativeLayout relFilterIn;
    @Bind(R.id.iv_filter_in)
    ImageView ivFilterIn;
    @Bind(R.id.rel_filter_out)
    RelativeLayout relFilterOut;
    @Bind(R.id.iv_filter_out)
    ImageView ivFilterOut;
    @Bind(R.id.filter)
    LinearLayout filter;
    UserBillListRequest userBillListRequest;
    int typefilter = -1;
    private int page = 1;
    private int merchantId;
    private MerchantBillListRequest merchantBillListRequest;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_mybill);
    }

    public void init() {
        lhTvTitle.setText("账户明细");
        Drawable drawable = getResources().getDrawable(R.mipmap.dow_btn);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        lhTvTitle.setCompoundDrawables(null, null, drawable, null);
        lhTvTitle.setGravity(Gravity.CENTER_VERTICAL);
        lv_my_bill.setRefreshEnable(true);
        lv_my_bill.setLoadMoreEnable(false);
        lv_my_bill.setSmoothListViewListener(this);
        lv_my_bill.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        merchantId = getIntent().getIntExtra(INTENT_MERCHANTID, -1);
        showPd();
        // 用户的账户明细列表
        if (merchantId == -1) {
            userBillListRequest(true);
            lv_my_bill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    IntentUtil.intStart_activity((Activity) MyBillActivity.this,
                            BillInfoActivity.class, new Pair<String, Integer>("type", mbillList.get(position - 1).type),
                            new Pair<String, Integer>("billId", mbillList.get(position - 1).billId));
                }
            });
        }

        // 商家报表列表
        else {
            merchantBillListRequest(true);


            lv_my_bill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int type = mbillList.get(position - 1).type;
                    int billId = mbillList.get(position - 1).billId;
                    Intent it = new Intent(MyBillActivity.this, MerchantBillInfoActivity.class);
                    it.putExtra(MerchantBillInfoActivity.INTENT_TYPE, type);
                    it.putExtra(MerchantBillInfoActivity.INTENT_BILLID, billId);
                    startActivity(it);
                }
            });


        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_tv_title, R.id.rel_filter_in, R.id.rel_filter_out, R.id.rel_filter_all})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.lh_tv_title:
                if (filter.getVisibility() == View.VISIBLE) {
                    filter.setVisibility(View.GONE);
                    Drawable drawable = getResources().getDrawable(R.mipmap.u_btn);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    lhTvTitle.setCompoundDrawables(null, null, drawable, null);
                } else {
                    filter.setVisibility(View.VISIBLE);
                    Drawable drawable = getResources().getDrawable(R.mipmap.dow_btn);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    lhTvTitle.setCompoundDrawables(null, null, drawable, null);
                }
                break;
            case R.id.rel_filter_in:
                typefilter = 0;
                page = 1;
                ivFilterAll.setVisibility(View.INVISIBLE);
                ivFilterIn.setVisibility(View.VISIBLE);
                ivFilterOut.setVisibility(View.INVISIBLE);
                filter.setVisibility(View.GONE);
                onRefresh();
                break;
            case R.id.rel_filter_out:
                typefilter = 1;
                page = 1;
                ivFilterAll.setVisibility(View.INVISIBLE);
                ivFilterIn.setVisibility(View.INVISIBLE);
                ivFilterOut.setVisibility(View.VISIBLE);
                filter.setVisibility(View.GONE);
                onRefresh();
                break;
            case R.id.rel_filter_all:
                typefilter = -1;
                page = 1;
                ivFilterAll.setVisibility(View.VISIBLE);
                ivFilterIn.setVisibility(View.INVISIBLE);
                ivFilterOut.setVisibility(View.INVISIBLE);
                filter.setVisibility(View.GONE);
                onRefresh();
                break;
        }
    }

    //获取用户的场景列表
    private void userBillListRequest(boolean isfirstLoad) {
        if (userBillListRequest != null) {
            userBillListRequest.cancel();
        }


        UserBillListRequest.Input input = new UserBillListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.type = typefilter;//类型(-1:全部,0:收入,1:支出)
        input.rows = Constants.ROWS;
        if (isfirstLoad) {
            input.page = 1;
        } else {
            input.page = page;
        }

        input.convertJosn();
        userBillListRequest = new UserBillListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                lv_my_bill.stopRefresh();
                lv_my_bill.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillList) response).status == 1) {
                    dismissPd();
                    if (page == 1) {
                        mbillList = ((APIM_UserBillList) response).billList;
                        lv_my_bill.setAdapter(commonAdapter = new CommonAdapter<M_Bill>(MyBillActivity.this, R.layout.item_mybill, mbillList) {
                            @Override
                            public void convert(CommonViewHolder holder, M_Bill mbill, int position) {
                                //类型(0:支付买单,2:支付绑定支付宝账户,3:提现,6:消费任务佣金,7:购买礼物,8:礼物兑换)
                                holder.setText(R.id.tv_mybill_note, mbill.note);
                                if (mbill.type == 0) {
                                    holder.setText(R.id.tv_mybill_name, "支付买单");
                                    holder.setViewInvisible(R.id.tv_state);
                                }
                                if (mbill.type == 2) {
                                    holder.setText(R.id.tv_mybill_name, "支付绑定支付宝账户");
                                    holder.setViewInvisible(R.id.tv_state);
                                }
                                if (mbill.type == 3) {
                                    holder.setText(R.id.tv_mybill_name, "提现");
                                    holder.setViewVisible(R.id.tv_state);

                                    if (mbill.withdrawState == 0) {
                                        holder.setText(R.id.tv_state, "进行中...");
                                    }
                                    if (mbill.withdrawState == 1) {
                                        holder.setText(R.id.tv_state, "成功");
                                    }
                                    if (mbill.withdrawState == 2) {
                                        holder.setText(R.id.tv_state, "失败");
                                    }

                                }
                                if (mbill.type == 6) {
                                    holder.setText(R.id.tv_mybill_name, "红包");
                                    holder.setViewInvisible(R.id.tv_state);
                                }
                                if (mbill.type == 7) {
                                    holder.setText(R.id.tv_mybill_name, "购买礼物");
                                    holder.setViewInvisible(R.id.tv_state);
                                }
                                if (mbill.type == 8) {
                                    holder.setText(R.id.tv_mybill_name, "礼物兑换");
                                    holder.setViewInvisible(R.id.tv_state);
                                }
                                if (mbill.type == 9 || mbill.type == 10) {
                                    holder.setText(R.id.tv_mybill_name, "赞赏红包");
                                    holder.setViewInvisible(R.id.tv_state);
                                }

                                holder.setText(R.id.tv_mybill_date, mbill.createtime);
                                if (mbill.inCome == 0) {  //收入
                                    holder.setText(R.id.tv_mybill_money, "+" + Convert.getMoneyString(mbill.money));
                                } else if (mbill.inCome == 1) {  //支出
                                    holder.setText(R.id.tv_mybill_money, "-" + Convert.getMoneyString(mbill.money));
                                }
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
                    ToastUtils.show(MyBillActivity.this, ((APIM_UserBillList) response).info);
                }
                lv_my_bill.stopRefresh();
                lv_my_bill.stopLoadMore();
                dismissPd();

            }
        });
        sendJsonRequest(userBillListRequest);
    }

    //商家报表列表
    private void merchantBillListRequest(boolean isfirstLoad) {
        if (merchantBillListRequest != null) {
            merchantBillListRequest.cancel();
        }
        MerchantBillListRequest.Input input = new MerchantBillListRequest.Input();
        input.merchantId = merchantId;
        input.type = typefilter;//类型(-1:全部,0:收入,1:支出)
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
                lv_my_bill.stopRefresh();
                lv_my_bill.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillList) response).status == 1) {
                    if (page == 1) {
                        mbillList = ((APIM_UserBillList) response).billList;
                        lv_my_bill.setAdapter(commonAdapter = new CommonAdapter<M_Bill>(MyBillActivity.this, R.layout.item_mybill, mbillList) {
                            @Override
                            public void convert(CommonViewHolder holder, M_Bill mbill, int position) {
                                //类型(0:交易,1:提现)
                                holder.setText(R.id.tv_mybill_note, mbill.note);
                                if (mbill.type == 0) {
                                    holder.setText(R.id.tv_mybill_name, "支付买单");
                                }
                                if (mbill.type == 2) {
                                    holder.setText(R.id.tv_mybill_name, "支付绑定支付宝账号");
                                }
                                if (mbill.type == 3) {
                                    holder.setText(R.id.tv_mybill_name, "提现");
                                }
                                if (mbill.type == 6) {
                                    holder.setText(R.id.tv_mybill_name, "红包");
                                }
                                holder.setText(R.id.tv_mybill_date, mbill.createtime);

                                if (mbill.inCome == 0) {  //收入
                                    holder.setText(R.id.tv_mybill_money, "+" + Convert.getMoneyString(mbill.money));
                                }

                                if (mbill.inCome == 1) {  //支出
                                    holder.setText(R.id.tv_mybill_money, "-" + Convert.getMoneyString(mbill.money));
                                }

                            }
                        });
                    } else {

                        mbillList.addAll(((APIM_UserBillList) response).billList);
                    }
                    if (((APIM_UserBillList) response).maxpage <= page) {
                        lv_my_bill.setLoadMoreEnable(false);
                    } else {
                        lv_my_bill.setLoadMoreEnable(true);
                        page++;
                    }

                } else {
                    ToastUtils.show(MyBillActivity.this, ((APIM_UserBillList) response).info);
                }
                lv_my_bill.stopRefresh();
                lv_my_bill.stopLoadMore();
                dismissPd();

            }
        });
        sendJsonRequest(merchantBillListRequest);
    }


    @Override
    public void onRefresh() {
        if (merchantId == -1)
            userBillListRequest(true);
        else
            merchantBillListRequest(true);
    }

    @Override
    public void onLoadMore() {
        if (merchantId == -1)
            userBillListRequest(false);
        else
            merchantBillListRequest(false);
    }


}
