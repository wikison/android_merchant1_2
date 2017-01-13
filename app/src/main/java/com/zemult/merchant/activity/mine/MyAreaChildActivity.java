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
import com.zemult.merchant.model.M_City;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyAreaChildActivity extends MBaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    @Bind(R.id.ll_back)
    LinearLayout llback;
    @Bind(R.id.city_lv)
    ListView citylv;
    CommonAdapter  commonAdapter;
    M_City mcity;
    String requesttype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_areachild);
        ButterKnife.bind(this);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("选择城市");
        requesttype=getIntent().getStringExtra("requesttype");

        citylv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(requesttype.equals(Constants.BROCAST_EDITUSERINFO))
                {
                Intent intent =new Intent(Constants.BROCAST_EDITUSERINFO);
                intent.putExtra("cityName",mcity.cityList.get(position).name);
                intent.putExtra("city",mcity.cityList.get(position).code);
                intent.putExtra("provinceName",mcity.name);
                intent.putExtra("province",mcity.code);
                setResult(RESULT_OK,intent);
                sendBroadcast(intent);
                finish();
            }else{
                    Intent intent =new Intent(MyAreaChildActivity.this,MyAreaSubChildActivity.class);
                    intent.putExtra("cityName",mcity.cityList.get(position).name);
                    intent.putExtra("city",mcity.cityList.get(position).code);
                    intent.putExtra("provinceName",mcity.name);
                    intent.putExtra("province",mcity.code);
                    SlashHelper.setSettingString(SlashHelper.APP.Key.COUNTRY,SlashHelper.gson.toJson(mcity.cityList.get(position)));
                    startActivityForResult(intent,1);

                }
            }
        });
        try {

            String jsoncity=  SlashHelper.getSettingString(SlashHelper.APP.Key.AREA,"");
            if(!"".equals(jsoncity)){
                mcity = SlashHelper.gson.fromJson(jsoncity, M_City.class);
            }
            citylv.setAdapter(commonAdapter = new CommonAdapter<M_Area>(MyAreaChildActivity.this, R.layout.item_choose_city,mcity.cityList) {
                @Override
                public void convert(CommonViewHolder holder, M_Area zone, final int position) {
                    holder.setText(R.id.tv_area, zone.name);
                }

            });

        }catch (Exception e){

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&RESULT_OK==resultCode){
            setResult(RESULT_OK);
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
