package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Area;
import com.zemult.merchant.model.M_Zone;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyAreaSubChildActivity extends MBaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    @Bind(R.id.ll_back)
    LinearLayout llback;
    @Bind(R.id.city_lv)
    ListView citylv;
    CommonAdapter  commonAdapter;
    M_Area marea;
    String requesttype,province,provinceName,city,cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_areachild);
        ButterKnife.bind(this);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("选择城市");
        requesttype=getIntent().getStringExtra("requesttype");
        cityName=getIntent().getStringExtra("cityName");
        city=getIntent().getStringExtra("city");
        provinceName=getIntent().getStringExtra("provinceName");
        province=getIntent().getStringExtra("province");



        citylv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent =new Intent(Constants.BROCAST_EDITMERCHANT);
                intent.putExtra("cityName",cityName);
                intent.putExtra("city",city);
                intent.putExtra("provinceName",provinceName);
                intent.putExtra("province",province);
                intent.putExtra("areaName",marea.areaList.get(position).name);
                intent.putExtra("area",marea.areaList.get(position).code);
                setResult(RESULT_OK,intent);
                sendBroadcast(intent);
                finish();
            }
        });
        try {

            String jsoncity=  SlashHelper.getSettingString(SlashHelper.APP.Key.COUNTRY,"");
            if(!"".equals(jsoncity)){
                marea = SlashHelper.gson.fromJson(jsoncity, M_Area.class);
            }
            citylv.setAdapter(commonAdapter = new CommonAdapter<M_Zone>(MyAreaSubChildActivity.this, R.layout.item_choose_city,marea.areaList) {
                @Override
                public void convert(CommonViewHolder holder, M_Zone zone, final int position) {
                    holder.setText(R.id.tv_area, zone.name);
                }

            });

        }catch (Exception e){

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
