package com.zemult.merchant.activity.slash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.message.PayBusinessActivity;
import com.zemult.merchant.adapter.slash.MyPushTaskListAdapter;
import com.zemult.merchant.aip.slash.MerchantGetinfoRequest;
import com.zemult.merchant.aip.task.TaskIndustryListPushMerchant_1_3Request;
import com.zemult.merchant.app.MAppCompatActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryListNew;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.zemult.merchant.view.common.MMAlert;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class ShopDetailActivity extends MAppCompatActivity implements SmoothListView.ISmoothListViewListener{

    public static final String MERCHANT_ID = "merchantId";
    public static final String MERCHANT_TYPE = "merchantType";
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.roapl_btn)
    Button roaplBtn;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;

    ImageView shdetaiPicIv;
    TextView introTv;
    TextView busidetaiAddressTv;
    ImageView busidetaiCallIv;

    private int page = 1;
    String phoneNo;
    int merchantId;
    MerchantGetinfoRequest merchantGetinfoRequest;
    private int type;
    private M_Merchant mMerchant;
    TaskIndustryListPushMerchant_1_3Request taskIndustryListPushMerchant_1_3Request;
    MyPushTaskListAdapter myPushTaskListAdapter;
    List<M_Task> mTaskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        ButterKnife.bind(this);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("店家详情");

        merchantId=getIntent().getIntExtra(MERCHANT_ID,0);
        type = getIntent().getIntExtra(MERCHANT_TYPE, 0);

        merchant_getinfo();
        taskIndustryListPushMerchant_1_3Request(false);


        View lvhead = LayoutInflater.from(this).inflate(R.layout.head_shop_detail, null);
        shdetaiPicIv = (ImageView) lvhead.findViewById(R.id.shdetai_pic_iv);
        introTv = (TextView) lvhead.findViewById(R.id.intro_tv);
        busidetaiAddressTv = (TextView) lvhead.findViewById(R.id.busidetai_address_tv);
        busidetaiCallIv = (ImageView) lvhead.findViewById(R.id.busidetai_call_iv);
        introTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        busidetaiCallIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNo.indexOf(";")!=-1){
                    call();
                }
                else{
                    ToastUtil.showMessage("暂无电话");
                }

            }
        });
        smoothListView.addHeaderView(lvhead);
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        myPushTaskListAdapter = new MyPushTaskListAdapter(ShopDetailActivity.this, mTaskList, 0);
        smoothListView.setAdapter(myPushTaskListAdapter);
        initListener();

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back,  R.id.roapl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                finish();
                break;
            case R.id.roapl_btn:
                Intent intent =new Intent(ShopDetailActivity.this,PayBusinessActivity.class);
                intent.putExtra("merchantName",mMerchant.name);
                intent.putExtra("merchantPic",mMerchant.pic);
                intent.putExtra("merchantAddress",mMerchant.address);
                intent.putExtra("merchantId",merchantId);
                intent.putExtra("merchantTel",mMerchant.tel);
                startActivity(intent);
                break;
        }
    }


    private void merchant_getinfo() {
        showPd();
        if (merchantGetinfoRequest != null) {
            merchantGetinfoRequest.cancel();
        }

        MerchantGetinfoRequest.Input input = new MerchantGetinfoRequest.Input();
        input.merchantId = merchantId;

        input.convertJosn();
        merchantGetinfoRequest = new MerchantGetinfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetinfo) response).status == 1) {
                    mMerchant=((APIM_MerchantGetinfo) response).merchant;
                    phoneNo=((APIM_MerchantGetinfo) response).merchant.tel;
                    busidetaiAddressTv.setText(((APIM_MerchantGetinfo) response).merchant.address);
//                    if (TextUtils.isEmpty(((APIM_MerchantGetinfo) response).merchant.detail)) {
//
//                        introTv.setText("这个商户很懒，什么都没留下~");
//                    } else {
//
//                        introTv.setText("\u3000\u3000" + ((APIM_MerchantGetinfo) response).merchant.detail);
//                    }
                    introTv.setText(mMerchant.name);
                    if(TextUtils.isEmpty(((APIM_MerchantGetinfo) response).merchant.pic)){

                        int number = new Random().nextInt(10) + 1;
                        String address = "http://xiegang.oss-cn-shanghai.aliyuncs.com/merchant/covers/changjing"+number+".jpg";
                        imageManager.loadUrlImage(address, shdetaiPicIv);//图片加载
                        mMerchant.setPic(address);
                    }else{
                        imageManager.loadUrlImage(((APIM_MerchantGetinfo) response).merchant.pic, shdetaiPicIv);
                    }


                    mMerchant = ((APIM_MerchantGetinfo) response).merchant;
                } else {
                    ToastUtils.show(ShopDetailActivity.this, ((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchantGetinfoRequest);
    }


    //获取对应页面下的任务列表
    private void taskIndustryListPushMerchant_1_3Request(final boolean isLoadMore) {
        if (taskIndustryListPushMerchant_1_3Request != null) {
            taskIndustryListPushMerchant_1_3Request.cancel();
        }

        TaskIndustryListPushMerchant_1_3Request.Input input = new TaskIndustryListPushMerchant_1_3Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.operateUserId = SlashHelper.userManager().getUserId();
        }

        input.merchantId = merchantId;
        //任务状态(-1:全部,0:进行中,1:已结束)
            input.state = -1;

        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();

        taskIndustryListPushMerchant_1_3Request = new TaskIndustryListPushMerchant_1_3Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskIndustryListNew) response).status == 1) {
                    fillAdapter(((APIM_TaskIndustryListNew) response).taskList,
                            ((APIM_TaskIndustryListNew) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(ShopDetailActivity.this, ((APIM_TaskIndustryListNew) response).info);
                }
                dismissPd();
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }
        });
        sendJsonRequest(taskIndustryListPushMerchant_1_3Request);
    }

    private void initListener() {
        myPushTaskListAdapter.setOnTaskDetailClickListener(new MyPushTaskListAdapter.OnTaskDetailClickListener() {
            @Override
            public void onTaskDetailClick(int position) {
                M_Task task = myPushTaskListAdapter.getItem(position);
                if(task.type==3){
                    Intent intent = new Intent(ShopDetailActivity.this, SearchDetailActivity.class);
                    intent.putExtra(SearchDetailActivity.INTENT_TASK, task);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(ShopDetailActivity.this, MyPublishTaskDetailActivity.class);
                    intent.putExtra("my_publish_task", task);
                    startActivity(intent);
                }




            }
        });
    }


    // 填充数据
    private void fillAdapter(List<M_Task> list, int maxPage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            smoothListView.setLoadMoreEnable(false);
        } else {
            smoothListView.setLoadMoreEnable(page < maxPage);
            myPushTaskListAdapter.setData(list, isLoadMore);
        }
    }


    @Override
    public void onRefresh() {
        taskIndustryListPushMerchant_1_3Request(false);
    }

    @Override
    public void onLoadMore() {
        taskIndustryListPushMerchant_1_3Request(true);
    }


    /**
     * 拨打电话
     */
    private void call() {
        final String[] phoneNoArray=phoneNo.split(";");
        MMAlert.showAlert(this, null, phoneNoArray, null,
                new MMAlert.OnAlertSelectId() {

                    @Override
                    public void onClick(int whichButton) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + phoneNoArray[whichButton]);
                        intent.setData(data);
                        startActivity(intent);
                    }
                });
    }

}
