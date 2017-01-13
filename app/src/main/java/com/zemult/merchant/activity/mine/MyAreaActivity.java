package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.common.CommonGetallregionsRequest;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.model.M_City;
import com.zemult.merchant.model.apimodel.APIM_CommonGetallregions;
import com.zemult.merchant.util.SlashHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class MyAreaActivity extends MBaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.areano_tv)
    TextView areanoTv;
    @Bind(R.id.areaok_tv)
    TextView areaokTv;
    @Bind(R.id.warn_tv)
    TextView warnTv;
    @Bind(R.id.province_lv)
    ListView provinceLv;
    @Bind(R.id.ll_back)
    LinearLayout llback;

    CommonGetallregionsRequest commonGetallregionsRequest;
    CommonAdapter commonAdapter;
    private List<M_City> mDatas = new ArrayList<M_City>();
    String requesttype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_area);
        ButterKnife.bind(this);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("地区");
        requesttype=getIntent().getStringExtra("requesttype");
        common_getallregions();
        provinceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SlashHelper.setSettingString(SlashHelper.APP.Key.AREA,SlashHelper.gson.toJson(mDatas.get(position)));
                Intent intent =new Intent(MyAreaActivity.this,MyAreaChildActivity.class);
                intent.putExtra("requesttype",requesttype);
                startActivityForResult(intent,1);
            }
        });
    }

    //获取所有的省市区列表
    private void common_getallregions() {
        if (commonGetallregionsRequest != null) {
            commonGetallregionsRequest.cancel();
        }


        commonGetallregionsRequest = new CommonGetallregionsRequest(new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {

                if (((APIM_CommonGetallregions) response).status == 1) {
                    mDatas = ((APIM_CommonGetallregions) response).provinceList;
                } else {
                    ToastUtils.show(MyAreaActivity.this, ((APIM_CommonGetallregions) response).info);
                }

                provinceLv.setAdapter(commonAdapter = new CommonAdapter<M_City>(MyAreaActivity.this, R.layout.item_choose_city, mDatas) {
                    @Override
                    public void convert(CommonViewHolder holder, M_City city, final int position) {
                        holder.setText(R.id.tv_area, city.name);
                    }

                });

            }
        });

        sendJsonRequest(commonGetallregionsRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK){
              finish();
        }
    }

    @OnClick({R.id.lh_btn_back,R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;



        }

    }
}
