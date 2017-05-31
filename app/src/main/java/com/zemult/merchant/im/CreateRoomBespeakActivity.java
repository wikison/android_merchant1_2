package com.zemult.merchant.im;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bigkoo.pickerview.TimePickerView;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.ChooseReservationMerchantActivity;
import com.zemult.merchant.aip.reservation.UserReservationAddRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.DateTimePickDialogUtil;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.FNRadioGroup;
import com.zemult.merchant.view.PMNumView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class CreateRoomBespeakActivity extends BaseActivity {

    @Bind(R.id.pmnv_select_deadline)
    PMNumView pmnvSelectDeadline;

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.ll_bottom)
    RelativeLayout llBottom;
    @Bind(R.id.et_customername)
    EditText etCustomername;
    @Bind(R.id.et_customerphone)
    EditText etCustomerphone;

    @Bind(R.id.tv_out_bespek_time)
    TextView tvOutBespekTime;
    @Bind(R.id.tv_in_bespek_time)
    TextView tvInBespekTime;
    @Bind(R.id.btn_bespeak_commit)
    TextView btnBespeakCommit;
    Date selectinDate;




    String inordertime = "", outordertime = "", ordername = "", orderphone = "";
    int roomnum,roletype;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bespeak);
    }

    @Override
    public void init() {
        inordertime = getIntent().getStringExtra("inordertime");
        outordertime = getIntent().getStringExtra("outordertime");
        ordername = getIntent().getStringExtra("ordername");
        orderphone = getIntent().getStringExtra("orderphone");
        roomnum=getIntent().getIntExtra("roomnum",0);
        roletype=getIntent().getIntExtra("roletype",0);
        if(roletype==1){
            llBottom.setVisibility(View.VISIBLE);
        }
        else{
            pmnvSelectDeadline.setDisable(true);
            llBottom.setVisibility(View.GONE);
            etCustomerphone.setEnabled(false);
            etCustomername.setEnabled(false);
        }

        if(!StringUtils.isBlank(inordertime)){
            selectinDate=DateTimeUtil.getDate(inordertime, "yyyy-MM-dd");
            tvInBespekTime.setText(getTime(DateTimeUtil.getDate(inordertime, "yyyy-MM-dd")));
        }
        if(!StringUtils.isBlank(outordertime)){
            tvOutBespekTime.setText(getTime(DateTimeUtil.getDate(outordertime, "yyyy-MM-dd")));
        }
        if(!StringUtils.isBlank(ordername)){
            etCustomername.setText(ordername);
        }
        if(!StringUtils.isBlank(orderphone)){
            etCustomerphone.setText(orderphone);
        }
        if(roomnum!=0){
            pmnvSelectDeadline.setDefaultNum(roomnum);
            pmnvSelectDeadline.setText("" + pmnvSelectDeadline.getDefaultNum());
        }
        else{
            pmnvSelectDeadline.setDefaultNum(1);
            pmnvSelectDeadline.setText("" + pmnvSelectDeadline.getDefaultNum());
        }

        pmnvSelectDeadline.setMinNum(1);
        pmnvSelectDeadline.setMaxNum(99);

        roomnum =  pmnvSelectDeadline.getDefaultNum();
        pmnvSelectDeadline.setFilter();


        pmnvSelectDeadline.setOnNumChangeListener(new PMNumView.NumChangeListener() {
            @Override
            public void onNumChanged(int num) {
                roomnum = num ;
                pmnvSelectDeadline.setDefaultNum(num);
            }
        });
        lhTvTitle.setText("房间信息");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void showInTimePicker() {
        Date now = new Date();
        Calendar selectedDate = new GregorianCalendar();
        if (!StringUtils.isBlank(inordertime)) {
            selectedDate.setTime(DateTimeUtil.getDate(inordertime, "yyyy-MM-dd"));
        }
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(now);
        Calendar endDate = new GregorianCalendar();
        endDate.setTime(DateTimeUtil.getDateAdd(now, 15));
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                    selectinDate=date;
                    inordertime = DateTimeUtil.getFormatDate(date);
                    tvInBespekTime.setText(getTime(date));

            }
        }).setType(TimePickerView.Type.YEAR_MONTH_DAY)//默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText("选择时间")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.font_main))//确定按钮文字颜色
                .setCancelColor(Color.BLACK)//取消按钮文字颜色
                .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
    }



    private void showOutTimePicker() {
//        Calendar selectedDate = new GregorianCalendar();
//        if (!StringUtils.isBlank(outordertime)) {
//            selectedDate.setTime(DateTimeUtil.getDate(outordertime, "yyyy-MM-dd HH:mm:ss"));
//        }
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(DateTimeUtil.getDateAdd(selectinDate, 1));
        Calendar endDate = new GregorianCalendar();
        endDate.setTime(DateTimeUtil.getDateAdd(selectinDate, 7));
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                    outordertime = DateTimeUtil.getFormatDate(date);
                    tvOutBespekTime.setText(getTime(date));

            }
        }).setType(TimePickerView.Type.YEAR_MONTH_DAY)//默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText("选择时间")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.font_main))//确定按钮文字颜色
                .setCancelColor(Color.BLACK)//取消按钮文字颜色
                .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
//                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
    }

    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd (*)");
        return format.format(date).replace("*", DateTimeUtil.getWeekDayOfWeekisToday(date));
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bespeak_commit,R.id.rl_out_ordertime,R.id.rl_in_ordertime,R.id.btn_bespeak_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_bespeak_cancel:

                Intent intent2 =new Intent();
                intent2.putExtra("outordertime","");
                intent2.putExtra("inordertime","");
                intent2.putExtra("ordername","");
                intent2.putExtra("orderphone","");
                intent2.putExtra("roomnum",0);
                setResult(RESULT_OK,intent2);
                finish();

                break;
            case R.id.btn_bespeak_commit:
                if(roletype==0)
                    return;
                if (noLogin(CreateRoomBespeakActivity.this))
                    return;
                ordername = etCustomername.getText().toString();
                orderphone = etCustomerphone.getText().toString();
                if (roomnum==0) {
                    ToastUtil.showMessage("请填写房间数");
                    return;
                }

                if (StringUtils.isEmpty(inordertime) || "请选择入住时间".equals(inordertime)) {
                    ToastUtil.showMessage("请选择入住时间");
                    return;
                }
                if (StringUtils.isEmpty(outordertime) || "请选择离店时间".equals(outordertime)) {
                    ToastUtil.showMessage("请选择离店时间");
                    return;
                }
                if (StringUtils.isEmpty(ordername)) {
                    ToastUtil.showMessage("请填写联系人姓名");
                    return;
                }
                if (StringUtils.isEmpty(orderphone)) {
                    ToastUtil.showMessage("请填写联系人电话");
                    return;
                }
                if (!StringMatchUtils.isMobileNO(orderphone)) {
                    ToastUtil.showMessage("请输入正确的联系人电话");
                    return;
                }


                Intent intent =new Intent();
                intent.putExtra("outordertime",outordertime);
                intent.putExtra("inordertime",inordertime);
                intent.putExtra("ordername",ordername);
                intent.putExtra("orderphone",orderphone);
                intent.putExtra("roomnum",roomnum);
                setResult(RESULT_OK,intent);
                finish();

                break;

            case R.id.rl_out_ordertime:
                if(roletype==0)
                    return;
                if(null==selectinDate){
                    ToastUtil.showMessage("请选择入住时间");
                    return;
                }
                showOutTimePicker();
                break;
            case R.id.rl_in_ordertime:
                if(roletype==0)
                    return;
                showInTimePicker();
                break;

        }
    }

}
