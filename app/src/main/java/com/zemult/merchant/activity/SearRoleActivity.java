package com.zemult.merchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.NewroleActivity;
import com.zemult.merchant.adapter.createroleadapter.RolelistAdapter;
import com.zemult.merchant.aip.common.CommonGetindustrychildsRequest;
import com.zemult.merchant.app.MAppCompatActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.model.apimodel.APIM_CommonGetindustrychilds;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class SearRoleActivity extends MAppCompatActivity implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.scensear_et_search)
    EditText scensearEtSearch;
    @Bind(R.id.scencsea_lv_content)
    SmoothListView scencseaLvContent;

    @Bind(R.id.tucao_ll)
    LinearLayout tucaoLl;
    @Bind(R.id.tucao_tv)
    TextView tucaoTv;

    private RolelistAdapter rolelistAdapter;
//    MerchantSearchmerchantListRequest merchantSearchmerchantListRequest;

    CommonGetindustrychildsRequest commonGetindustrychildsRequest;


    int page = 1;
    String searchKey = "", requesttype;
    private List<M_UserRole> roledatas = new ArrayList<M_UserRole>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sear_scene);
        ButterKnife.bind(this);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("斜杠角色");
       tucaoTv.setText(Html.fromHtml("<u>" + "没搜到   吐个槽" + "</u>"));
        rolelistAdapter = new RolelistAdapter(this, roledatas);
        scencseaLvContent.setAdapter(rolelistAdapter);
        scensearEtSearch.setOnKeyListener(onKey);
        scencseaLvContent.setRefreshEnable(true);
        scencseaLvContent.setLoadMoreEnable(false);
        scencseaLvContent.setSmoothListViewListener(this);
      //  requesttype = getIntent().getStringExtra("requesttype");

        getNetworkData(false);
    }


    private void getNetworkData(boolean isLoadMore) {
        commonGetindustrychilds(isLoadMore);

    }


    View.OnKeyListener onKey = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // TODO Auto-generated method stub
            Log.v("OnKeyListener", event.toString());
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                Log.v("OnKeyListener", "KeyEvent.KEYCODE_ENTER");
                InputMethodManager imm = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(),
                            0);
                    searchKey = scensearEtSearch.getText().toString().trim();
                    getNetworkData(false);
                }
                return true;
            }
            return false;

        }

    };
//    //获取用户的单角色下的场景列表
//    private void merchant_searchmerchantList(final boolean isFresh ) {
//        if (merchantSearchmerchantListRequest != null) {
//            merchantSearchmerchantListRequest.cancel();
//        }
//        MerchantSearchmerchantListRequest.Input input = new MerchantSearchmerchantListRequest.Input();
//        if (SlashHelper.userManager().getUserinfo() != null) {
//            input.operateUserId = SlashHelper.userManager().getUserId();
//        }
//        input.name = searchKey;//场景名称(模糊)
//        input.industryId = -1;
// //       input.state =rolestate ;   //	场景状态(-1:全部， 1:待上线 2:已上线)
//        input.city = Constants.CITYID;
//        input.center = Constants.CENTER;
//        if(isFresh){
//            input.page = 1;
//        }
//        else{
//            input.page = page;
//        }
//
//        input.rows =Constants.ROWS;
//        input.convertJosn();
//        merchantSearchmerchantListRequest = new MerchantSearchmerchantListRequest(input,new ResponseListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                scencseaLvContent.stopRefresh();
//                scencseaLvContent.stopLoadMore();
//            }
////            @Override
//            public void onResponse(Object response) {
//                if (((APIM_MerchantList) response).status == 1) {
//                    if(isFresh){
//                        roledatas.clear();
//                        roledatas.addAll(((APIM_MerchantList) response).merchantList);
//                        scencseaLvContent.setVisibility(View.VISIBLE);
//                        if (null == rolelistAdapter) {
//                            rolelistAdapter = new RolelistAdapter(SearRoleActivity.this, roledatas);
//                            scencseaLvContent.setAdapter(rolelistAdapter);
//                        } else {
//                            rolelistAdapter.notifyDataSetChanged();
//                        }
//                    }
//                    else{
//                        roledatas.addAll(((APIM_MerchantList) response).merchantList);
//                        rolelistAdapter.setRolesEntities(roledatas);
//                        rolelistAdapter.notifyDataSetChanged();
//                    }
//
//                    if(((APIM_MerchantList) response).maxpage==page){
//                        scencseaLvContent.setLoadMoreEnable(false);
//                    }
//                    else{
//                        scencseaLvContent.setLoadMoreEnable(true);
//                        ++page;
//                    }
//
//                } else {
//                    ToastUtils.show(SearRoleActivity.this, ((APIM_MerchantList) response).info);
//                }
//                scencseaLvContent.stopRefresh();
//                scencseaLvContent.stopLoadMore();
//            }
//        });
//        sendJsonRequest(merchantSearchmerchantListRequest);
//    }


    //获取单个行业下的角色列表
    private void commonGetindustrychilds(final boolean isLoadMore) {
        if (commonGetindustrychildsRequest != null) {
            commonGetindustrychildsRequest.cancel();
        }
        CommonGetindustrychildsRequest.Input input = new CommonGetindustrychildsRequest.Input();

        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.name = searchKey;//名称(模糊)
        input.industryId = -1;
        input.page = page;
        input.rows = Constants.ROWS;
        input.convertJosn();
        commonGetindustrychildsRequest = new CommonGetindustrychildsRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                scencseaLvContent.stopRefresh();
                scencseaLvContent.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {

                if (((APIM_CommonGetindustrychilds) response).status == 1) {

                    fillAdapter(((APIM_CommonGetindustrychilds) response).industryList,
                            ((APIM_CommonGetindustrychilds) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(SearRoleActivity.this, ((APIM_CommonGetindustrychilds) response).info);
                }
                scencseaLvContent.stopRefresh();
                scencseaLvContent.stopLoadMore();


            }
        });
        sendJsonRequest(commonGetindustrychildsRequest);

    }


    /**
     * 设置数据
     */
    private void fillAdapter(final List<M_UserRole> list, int maxpage, boolean isLoadMore) {
        if (list != null && !list.isEmpty()) {
            scencseaLvContent.setLoadMoreEnable(page < maxpage);
            rolelistAdapter.setData(list, isLoadMore);
        }
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tucao_ll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;

            case R.id.tucao_ll:
                Intent it = new Intent(SearRoleActivity.this, NewroleActivity.class);
                startActivity(it);
                break;
        }
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
