package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.MerchantVoucherCheckRequest;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.common.CommonDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/8/1.
 */
//优惠券输码验证
public class EnterCardsActivity extends MBaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.shuru_ed)
    EditText shuruEd;
    @Bind(R.id.commit_btn)
    Button commitBtn;
    public static final String INTENT_MERCHANTID ="merchantId";
    MerchantVoucherCheckRequest merchantVoucherCheckRequest;
    String code;
    int merchantId=0;
    public static final String Call_Cards_REFRESH = "call_cards_refresh";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entercards);
        ButterKnife.bind(this);
        lhTvTitle.setText("优惠券");
        merchantId = getIntent().getIntExtra(INTENT_MERCHANTID, -1);
      //  shuruEd.setText("100000000008");
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.commit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.commit_btn:
                code=  shuruEd.getText().toString().trim();
                if(TextUtils.isEmpty(code)){
                    ToastUtil.showMessage("请输入验证码");
                    return;
                }
                merchantVoucherCheck();
                break;
        }
    }
 //   商家输码验单 (商家id+验证码)
    private void merchantVoucherCheck() {
        if (merchantVoucherCheckRequest != null) {
            merchantVoucherCheckRequest.cancel();
        }

        MerchantVoucherCheckRequest.Input input = new MerchantVoucherCheckRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.merchantId = merchantId;//	商家(场景)id
        input.code = code;//      验证码
        input.convertJosn();

        merchantVoucherCheckRequest = new MerchantVoucherCheckRequest(input, new ResponseListener() {



            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showMessage("网络错误~");
            }
            @Override
            public void onResponse(Object response) {
                Log.i("YYYYYCCCCCLLL",((CommonResult) response).status+"");
                if (((CommonResult) response).status == 1) {
                    String name = ((CommonResult) response).name;//商家名称
                    double money = ((CommonResult) response).money;//代金券金额
                    String code = ((CommonResult) response).code;//兑换码
                    Intent it = new Intent(EnterCardsActivity.this, TestResultActivity.class);
                    it.putExtra("name", name);
                    it.putExtra("money", money);
                    it.putExtra("code", code);
                    startActivity(it);

                }
                else if(((CommonResult) response).status == 0){
                    CommonDialog.showDialogListener(EnterCardsActivity.this, null, "取消", "继续", "兑换码有误,请重新输入!", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonDialog.DismissProgressDialog();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shuruEd.setText("");
                            CommonDialog.DismissProgressDialog();
                        }
                    });

                }
                else {
                    ToastUtils.show(EnterCardsActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(merchantVoucherCheckRequest);
    }


}
