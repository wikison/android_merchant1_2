package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.DateTimePickDialogUtil;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2016/8/9.
 */
public class PublishTaskRewardActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_voucher_money)
    EditText etVoucherMoney;
    @Bind(R.id.et_voucher_num)
    EditText etVoucherNum;
    @Bind(R.id.et_voucher_min_money)
    EditText etVoucherMinMoney;
    @Bind(R.id.tv_voucher_end_time)
    TextView tvVoucherEndTime;
    @Bind(R.id.cb_is_union)
    CheckBox cbIsUnion;
    @Bind(R.id.et_voucher_note)
    EditText etVoucherNote;
    @Bind(R.id.btn_commit)
    Button btnCommit;

    Context mContext;
    String requestType = "";
    private double voucherMoney;
    private int voucherNum;
    private double voucherMinMoney;
    private int isUnion;
    private String voucherNote = "";
    private String voucherEndTime = "";
    private int cashType;
    private String reWardName;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_publish_task_reward);
    }

    @Override
    public void init() {
        initData();
        initListener();
    }

    private void initData() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("优惠券设置");
        requestType = getIntent().getStringExtra("requesttype");

        voucherMoney = getIntent().getDoubleExtra("voucher_money", 0);
        voucherNum = getIntent().getIntExtra("voucher_num", 0);
        voucherMinMoney = getIntent().getDoubleExtra("voucher_min_money", -1);
        voucherEndTime = getIntent().getStringExtra("voucher_end_time");
        voucherNote = getIntent().getStringExtra("voucher_note");

        if (voucherMoney > 0) {
            etVoucherMoney.setText(voucherMoney + "");
        } else {
            etVoucherMoney.setText("");
        }
        if (voucherNum > 0) {
            etVoucherNum.setText(voucherNum + "");
        } else {
            etVoucherNum.setText("");
        }
        if (voucherMinMoney >= 0) {
            etVoucherMinMoney.setText(voucherMinMoney + "");
        } else {
            etVoucherMinMoney.setText("");
        }
        if (!StringUtils.isBlank(voucherEndTime)) {
            tvVoucherEndTime.setText(voucherEndTime);
        } else {
            tvVoucherEndTime.setText("设置优惠券有效期");
        }
        etVoucherNote.setText(voucherNote);

        EditFilter.CashFilter(etVoucherMoney, 200);
        EditFilter.IntegerFilter(etVoucherNum, 999);
        EditFilter.CashFilter(etVoucherMinMoney, 10000);
        EditFilter.WordFilter(etVoucherNote, 100);

    }

    private void initListener() {

    }

    @OnClick({R.id.lh_btn_back, R.id.tv_voucher_end_time, R.id.ll_back, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;

            case R.id.tv_voucher_end_time:
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        this, tvVoucherEndTime.getText().toString(), "截止时间必须大于当前时间", 2);
                dateTimePicKDialog.dateTimePicKDialog(tvVoucherEndTime);
                break;

            case R.id.btn_commit:
                if (isValid()) {
                    if (requestType.equals(Constants.BROCAST_PUBLISH_TASK_COUPON)) {
                        Intent intent = new Intent(Constants.BROCAST_PUBLISH_TASK_COUPON);
                        intent.putExtra("voucher_money", voucherMoney);
                        intent.putExtra("voucher_num", voucherNum);
                        intent.putExtra("voucher_min_money", voucherMinMoney);
                        intent.putExtra("voucher_end_time", voucherEndTime+":00");
                        intent.putExtra("voucher_note", voucherNote);
                        setResult(RESULT_OK, intent);
                        sendBroadcast(intent);
                        finish();
                    }
                }

                break;
        }
    }

    private boolean isValid() {
        try {
            voucherMoney = Double.valueOf(etVoucherMoney.getText().toString());
            voucherNum = Integer.valueOf(etVoucherNum.getText().toString());
            voucherMinMoney = Double.valueOf(etVoucherMinMoney.getText().toString());
            voucherEndTime = tvVoucherEndTime.getText().toString();
            isUnion = cbIsUnion.isChecked() ? 1 : 0;
            voucherNote = etVoucherNote.getText().toString();
            if (StringUtils.isBlank(voucherNote)) {
                etVoucherNote.setError("优惠券使用规则不能为空");
                return false;
            }

            if (voucherMinMoney != 0 && voucherMinMoney < voucherMoney) {
                etVoucherMinMoney.setError("最低消费额度不能小于优惠券抵扣金额");
                return false;
            }
            if ("设置优惠券有效期".equals(voucherEndTime)) {
                ToastUtil.showMessage("优惠券有效期不能为空");
                return false;
            }

        } catch (Exception e) {

        }
        return true;
    }

}
