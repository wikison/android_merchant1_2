package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.ClassifyAdapter;
import com.zemult.merchant.aip.slash.CommonFirstpageIndustryListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.apimodel.APIM_CommonGetallindustry;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class ClassifyActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.smoothListView)
    ListView listView;
    @Bind(R.id.tv_right)
    TextView tvRight;

    private Context mContext;
    private ClassifyAdapter mAdapter;
    private CommonFirstpageIndustryListRequest industryListRequest; // 获取所有行业分类
    private String requesttype;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_classify);
    }

    @Override
    public void init() {
        requesttype = getIntent().getStringExtra("requesttype");
        lhTvTitle.setText("分类");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("提交");

        mContext = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelectedEntity(mAdapter.getItem(position));
            }
        });
        common_firstpage_industryList();
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_right:
                if(mAdapter.getSelectedEntity() == null){
                    ToastUtil.showMessage("请先选择分类");
                    return;
                }
                if (null != requesttype && requesttype.equals(Constants.BROCAST_EDITMERCHANT)) {
                    Intent intent = new Intent();
                    intent.putExtra("industryId", mAdapter.getSelectedEntity().id);
                    intent.putExtra("industryName", mAdapter.getSelectedEntity().name);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    public void common_firstpage_industryList() {
        if (industryListRequest != null) {
            industryListRequest.cancel();
        }

        industryListRequest = new CommonFirstpageIndustryListRequest(new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_CommonGetallindustry) response).status == 1) {
                    if(!((APIM_CommonGetallindustry) response).industryList.isEmpty()){
                        mAdapter = new ClassifyAdapter(mContext, ((APIM_CommonGetallindustry) response).industryList);
                        listView.setAdapter(mAdapter);
                    }
                } else {
                    ToastUtils.show(mContext, ((APIM_CommonGetallindustry) response).info);
                }
            }
        });
        sendJsonRequest(industryListRequest);
    }
}
