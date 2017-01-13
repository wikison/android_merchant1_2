package com.zemult.merchant.activity;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.multipleroles.ReportAdapter;
import com.zemult.merchant.aip.common.CommonReportAddRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 举报
 *
 * @author djy
 * @time 2016/8/2 14:48
 */
public class ReportActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll_commit)
    LinearLayout llCommit;
    @Bind(R.id.ll_complete)
    LinearLayout llComplete;
    @Bind(R.id.lv)
    ListView lv;

    private Context mContext;
    private ReportAdapter mAdapter;
    public static final String INTENT_INFO_ID = "infoId";
    public static final String INTENT_INFO_TYPE = "infoType"; // 举报信息类型(1:角色任务,2:任务完成记录,3:心情小记,4:任务完成记录评论,5:心情小记评论)
    private int infoId;
    private int infoType;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_report);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {
        infoId = getIntent().getIntExtra(INTENT_INFO_ID, -1);
        infoType = getIntent().getIntExtra(INTENT_INFO_TYPE, -1);
        mContext = this;
        mAdapter = new ReportAdapter(mContext, ModelUtil.getReportData());
    }

    private void initView() {
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("提交");
        lhTvTitle.setText("举报");
        lv.setAdapter(mAdapter);
    }

    private void initListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelectedEntity(mAdapter.getItem(position));
            }
        });
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_right:
                if ("提交".equals(tvRight.getText().toString())) {
                    if(mAdapter.getSelectedEntity() == null)
                        ToastUtils.show(mContext, "请选择举报内容");
                    else
                        common_report_add();
                } else
                    onBackPressed();

                break;
        }
    }

    private CommonReportAddRequest reportAddRequest;
    private void common_report_add(){
        showPd();
        if (reportAddRequest != null) {
            reportAddRequest.cancel();
        }
        CommonReportAddRequest.Input input = new CommonReportAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.type = mAdapter.getSelectedEntity().getIntValue();
        input.infoType = infoType;
        input.infoId = infoId;
        input.convertJosn();

        reportAddRequest = new CommonReportAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    llCommit.setVisibility(View.GONE);
                    tvRight.setText("完成");
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(reportAddRequest);

    }
}
