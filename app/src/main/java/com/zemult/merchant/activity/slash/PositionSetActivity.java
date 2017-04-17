package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonPositionRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.model.apimodel.APIM_PresentList;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.BounceScrollView;
import com.zemult.merchant.view.PositionItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2017/4/17.
 */

public class PositionSetActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.iv_right2)
    ImageView ivRight2;
    @Bind(R.id.ll_right2)
    LinearLayout llRight2;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.ll_main)
    LinearLayout llMain;
    @Bind(R.id.scrollView)
    BounceScrollView scrollView;
    @Bind(R.id.btn_confirm)
    Button btnConfirm;

    private Context mContext;

    CommonPositionRequest commonPositionRequest;
    List<M_Reservation> positionList = new ArrayList<>();

    String positionName = "";

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_position_set);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

    }

    private void initData() {
        mContext = this;
        positionName = getIntent().getStringExtra("position_name");
        commonPosition();

    }

    private void initView() {
        lhTvTitle.setText("职位设置");

    }

    private void initListener() {

    }

    private void commonPosition() {
        if (commonPositionRequest != null) {
            commonPositionRequest.cancel();
        }
        commonPositionRequest = new CommonPositionRequest(new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {

                if (((APIM_PresentList) response).status == 1) {
                    if (((APIM_PresentList) response).positionList.size() > 0) {
                        positionList = ((APIM_PresentList) response).positionList;
                        initPositionItem(positionList);
                    }
                } else {
                    ToastUtils.show(mContext, ((APIM_PresentList) response).info);
                }
                dismissPd();
            }
        });

        sendJsonRequest(commonPositionRequest);
    }

    private void initPositionItem(List<M_Reservation> positionList) {
        for (int i = 0; i < positionList.size(); i++) {
            M_Reservation mReservation = positionList.get(i);
            PositionItemView p = new PositionItemView(this);
            p.setViewTag(i);
            p.setTextName(mReservation.name);
            p.setEditVisibility(View.GONE);
            p.setCb(mReservation.name.equals(positionName));
            llMain.addView(p);
        }

        PositionItemView p = new PositionItemView(this);
        p.setViewTag(positionList.size());
        p.setTextName("其他");
        p.setEditVisibility(View.VISIBLE);
        p.setCb(!noneSelect());
        if (p.getCb()) {
            p.setEditName(positionName);
        }
        llMain.addView(p);

        for (int i = 0; i < llMain.getChildCount(); i++) {
            final PositionItemView pIV = (PositionItemView) llMain.getChildAt(i);
            pIV.setOnViewPositionClickListener(new PositionItemView.ViewPositionClickListener() {
                @Override
                public void onPositionManage(int viewTag) {
                    for (int j = 0; j < llMain.getChildCount(); j++) {
                        PositionItemView p = (PositionItemView) llMain.getChildAt(j);
                        if (p.getViewTag() == viewTag) {
                            if (!p.getCb()) {
                                p.setCb(true);
                            }
                        } else {
                            p.setCb(false);
                        }
                    }
                }
            });

        }

    }

    //是否有选中的
    private boolean noneSelect() {
        boolean result = false;
        for (int i = 0; i < llMain.getChildCount(); i++) {
            final PositionItemView pIV = (PositionItemView) llMain.getChildAt(i);
            if (pIV.getCb()) {
                result = true;
                break;
            }
        }

        return result;
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_confirm:
                String positionName = selectPosition();
                if (positionName.equals("其他")) {
                    return;
                } else {
                    Intent data = new Intent();
                    data.putExtra("position_name", positionName);
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
        }
    }

    private String selectPosition() {
        String result = "";
        for (int i = 0; i < llMain.getChildCount(); i++) {
            final PositionItemView pIV = (PositionItemView) llMain.getChildAt(i);
            if (pIV.getCb()) {
                result = pIV.getTextName();
                if (pIV.getTextName().equals("其他")) {
                    if (pIV.isBlank()) {
                        ToastUtil.showMessage("请输入您的职位");
                    } else {
                        result = pIV.getEditName();
                    }
                }
                break;
            }
        }
        return result;
    }

}
