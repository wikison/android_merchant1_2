package com.zemult.merchant.activity.slash.dotask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ReportActivity;
import com.zemult.merchant.aip.task.TaskIndustryInfoRequest;
import com.zemult.merchant.alipay.taskpay.TaskPayInfoActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2016/8/1.
 */
public class DoTaskPayActivity extends BaseActivity {


    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.iv_type_icon)
    ImageView ivTypeIcon;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_task_info)
    TextView tvTaskInfo;
    @Bind(R.id.divider1)
    View divider1;
    @Bind(R.id.tv_title_task_describe)
    TextView tvTitleTaskDescribe;
    @Bind(R.id.tv_note)
    TextView tvNote;
    @Bind(R.id.divider2)
    View divider2;
    @Bind(R.id.tv_title_task_reward)
    TextView tvTitleTaskReward;
    @Bind(R.id.tv_exp)
    TextView tvExp;
    @Bind(R.id.tv_bonuses)
    TextView tvBonuses;
    @Bind(R.id.tv_voucher)
    TextView tvVoucher;
    @Bind(R.id.divider3)
    View divider3;
    @Bind(R.id.iv_publish_icon)
    ImageView ivPublishIcon;
    @Bind(R.id.tv_publish_name)
    TextView tvPublishName;
    @Bind(R.id.iv_complaints)
    ImageView ivComplaints;
    @Bind(R.id.tv_businessname)
    TextView tvBusinessname;
    @Bind(R.id.tv_task_state)
    TextView tvTaskState;
    @Bind(R.id.pb_progressbar)
    ProgressBar pbProgressbar;
    @Bind(R.id.btn_task_pay)
    Button btnTaskPay;
    @Bind(R.id.tv_pay_percent)
    TextView tvPayPercent;

    String businessName, businessAddress, businessImage, businessTel;

    int taskIndustryRecordId, taskIndustryId, businessId;

    TaskIndustryInfoRequest taskIndustryInfoRequest;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_do_task_pay);
    }

    @Override
    public void init() {
        lhTvTitle.setText("任务");
        imageManager.loadResImage(R.mipmap.xiaofei_icon_big, ivTypeIcon);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        taskIndustryRecordId = ((M_Task) getIntent().getSerializableExtra("task")).taskIndustryRecordId;
        if (taskIndustryRecordId <= 0) {
            taskIndustryRecordId = getIntent().getIntExtra("task_industry_record_id", -1);
        }
        taskIndustryId = ((M_Task) getIntent().getSerializableExtra("task")).taskIndustryId;
        task_industry_info();
    }


    private void task_industry_info() {

        try {
            if (taskIndustryInfoRequest != null) {
                taskIndustryInfoRequest.cancel();
            }
            TaskIndustryInfoRequest.Input input = new TaskIndustryInfoRequest.Input();
            input.taskIndustryId = taskIndustryId;
            input.userId = SlashHelper.userManager().getUserId();
            input.convertJosn();

            taskIndustryInfoRequest = new TaskIndustryInfoRequest(input, new ResponseListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }


                @Override
                public void onResponse(Object response) {
                    int status = ((APIM_TaskIndustryInfo) response).status;
                    if (status == 1) {
                        M_Task m_task = ((APIM_TaskIndustryInfo) response).taskIndustryInfo;
                        tvTitle.setText(m_task.title);
                        tvBusinessname.setText(m_task.merchantName);
                        tvTaskState.setText(m_task.state == 0 ? "进行中" : "已结束");
                        pbProgressbar.setMax((int) (m_task.payMoney * 100));
                        pbProgressbar.setProgress((int) (m_task.userPayMoney * 100));
                        tvPayPercent.setText(m_task.userPayMoney + "/" + m_task.payMoney);
                        businessTel = m_task.merchantTel;
                        businessId = m_task.merchantId;
                        businessName = m_task.merchantName;
                        businessAddress = m_task.merchantAddress;
                        businessImage = m_task.merchantHead;

                        tvExp.setText(String.format("经验X%s", String.valueOf(m_task.experience)));
                        String remainder = "";
                        switch (m_task.cashType) {
                            case 0:
                                tvBonuses.setVisibility(View.GONE);
                                tvVoucher.setVisibility(View.GONE);
                                break;
                            case 1:
                                tvBonuses.setVisibility(View.VISIBLE);
                                tvVoucher.setVisibility(View.GONE);
                                remainder = "红包余量" + (m_task.bonuseNum - m_task.outNum) + "/" + m_task.bonuseNum;
                                break;
                            case 2:
                                tvBonuses.setVisibility(View.GONE);
                                tvVoucher.setVisibility(View.VISIBLE);
                                tvVoucher.setText(String.format("代金券%s元", m_task.voucherMoney));
                                remainder = "代金券余量" + (m_task.voucherNum - m_task.outVoucherNum) + "/" + m_task.voucherNum;
                                break;
                        }

                        if (!StringUtils.isBlank(remainder)) {
                            remainder = "| " + remainder;
                        }
                        tvTaskInfo.setText(String.format("%sde任务 %s结束", m_task.industryName,
                                "| " + DateTimeUtil.strPubEndDiffTime(m_task.endtime)));
                        tvNote.setText(m_task.note);
                        imageManager.loadCircleHasBorderImage(m_task.userHead, ivPublishIcon, getResources().getColor(R.color.border_color), 1);
                        tvPublishName.setText(String.format("%s 发布", m_task.userName));


                    } else {
                        ToastUtil.showMessage(((APIM_TaskIndustryInfo) response).info);
                    }
                }
            });
            sendJsonRequest(taskIndustryInfoRequest);
        } catch (Exception e) {
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_task_pay, R.id.iv_complaints})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_task_pay:
                Intent intent = new Intent(DoTaskPayActivity.this, TaskPayInfoActivity.class);
                intent.putExtra("merchantName", businessName);
                intent.putExtra("merchantAddress", businessAddress);
                intent.putExtra("merchantId", businessId);
                intent.putExtra("taskIndustryRecordId", taskIndustryRecordId);
                intent.putExtra("merchantHead", businessImage);
                intent.putExtra("merchantTel", businessTel);
                startActivityForResult(intent,1);
                break;

            case R.id.iv_complaints:
                Intent intent2 = new Intent(DoTaskPayActivity.this, ReportActivity.class);
                intent2.putExtra("infoId", taskIndustryId);
                intent2.putExtra("infoType", 1);
                startActivity(intent2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode== RESULT_OK){
            finish();
        }
    }
}
