package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.aip.common.UserCheckIDNumRequest;
import com.zemult.merchant.app.MAppCompatActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class RetrievePasswordActivity extends MAppCompatActivity implements
        View.OnClickListener {

    public static final int REQUEST_VALIDATE_SMS = 0x31;
    public static final int REQUEST_MODIFY_PASSWORD = 0x32;
    public static final int REQUEST_VALIDATE_ID = 0x33;
    Button btn_next,lh_btn_back;
    TextView lh_tv_title,tv_message,tv_sendcode;
    LinearLayout  ll_back;
    EditText et_code,et_idnumber,et_name;
    String strPhone;
    String strcode,stridnumber,strname;
    private boolean isWait = false;
    private Thread mThread = null;
    Request request_common_getcode;
    UserCheckIDNumRequest userCheckIDNumRequest;
    private static final int WAIT = 0x001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);
        initViews();
    }



    private void initViews() {
        btn_next = (Button) findViewById(R.id.btn_next);
        tv_sendcode = (TextView) findViewById(R.id.tv_sendcode);
        tv_message = (TextView) findViewById(R.id.tv_message);
        lh_tv_title = (TextView) findViewById(R.id.lh_tv_title);
        lh_btn_back = (Button) findViewById(R.id.lh_btn_back);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        et_code = (EditText) findViewById(R.id.et_code);
        et_idnumber = (EditText) findViewById(R.id.et_idnumber);
        et_name = (EditText) findViewById(R.id.et_name);
        tv_message.setText("正在为"+ AppUtils.getHiddenMobile(SlashHelper.userManager().getUserinfo().getPhoneNum())+"重置安全密码");
        tv_sendcode.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        lh_btn_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        lh_tv_title.setText("找回安全密码");
        strPhone=SlashHelper.userManager().getUserinfo().getPhoneNum();

        tv_sendcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
        tv_sendcode.getPaint().setAntiAlias(true);//抗锯齿
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sendcode:
                getCode();
                break;
            case R.id.ll_back:
            case R.id.lh_btn_back:
                finish();
                break;
            case R.id.btn_next:
                if (validate()) {
                    user_checkIDNum();
                }

                break;
            default:
                break;
        }
    }

    private boolean validate() {
         strcode = et_code.getText().toString();
         stridnumber = et_idnumber.getText().toString();
         strname = et_name.getText().toString();

      if(StringUtils.isEmpty(strcode)){
          et_code.setError("请输入验证码");
          return false;
      }
        if(StringUtils.isEmpty(stridnumber)){
            et_idnumber.setError("请输入身份证号码");
            return false;
        }
        if(StringUtils.isEmpty(strname)){
            et_name.setError("请输入验真实姓名");
            return false;
        }
        return true;
    }

    private void getCode() {
        try {
            if (request_common_getcode != null) {
                request_common_getcode.cancel();
            }
            CommonGetCodeRequest.Input input = new CommonGetCodeRequest.Input();
            input.phone = strPhone;
            input.convertJosn();

            request_common_getcode = new CommonGetCodeRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        ToastUtil.showMessage("验证码已发送, 请查收!");
                        tv_sendcode.setText("重新发送(" + 60 + "s)");
                        tv_sendcode.setClickable(false);
                        tv_sendcode.setTextColor(0xff828282);
                        waitForClick();
                    }else
                        ToastUtil.showMessage(((CommonResult) response).info);
                }
            });
            sendJsonRequest(request_common_getcode);
        } catch (Exception e) {
            Log.e("COMMON_GETCODE", e.toString());
        }

    }

    private void user_checkIDNum() {//修改安全密码-忘记密码(验证实名认证)
        try {
            if (userCheckIDNumRequest != null) {
                userCheckIDNumRequest.cancel();
            }
            final UserCheckIDNumRequest.Input input = new UserCheckIDNumRequest.Input();
            input.name = strname;
            input.idNum = stridnumber;
            input.code = strcode;
            input.userId = SlashHelper.userManager().getUserId();
            input.convertJosn();

            userCheckIDNumRequest = new UserCheckIDNumRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        Intent intent =new Intent();
                        intent.putExtra("smsCode",strcode);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(userCheckIDNumRequest);
        } catch (Exception e) {
            Log.e("COMMON_CHECKCODE", e.toString());
        }

    }

    // 倒计时60s
    private void waitForClick() {
        isWait = true;
        final Handler handler = new Handler() {
            int i = 60;

            public void handleMessage(Message msg) {
                i--;
                tv_sendcode.setText("重新发送(" + i + "s)");
                if (i == 0) {
                    isWait = false;
                    tv_sendcode.setText("重新发送");
                    tv_sendcode.setClickable(true);
                    tv_sendcode.setTextColor(0xffe6bb7c);
                    i = 60;
                }
            }
        };

        mThread = new Thread() {
            @Override
            public void run() {
                while (isWait) {
                    try {
                        handler.sendEmptyMessage(WAIT);
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isWait = false;
    }
}
