package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.BindBankCardActivity;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.util.StringMatchUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 绑定银行卡3 输入验证码
 */
public class BindCardThreeFragment extends BaseFragment {

    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.view_resend_divider)
    View viewResendDivider;
    @Bind(R.id.tv_sendcode)
    TextView tvSendcode;
    @Bind(R.id.bt_over)
    Button btOver;
    private Context mContext;
    private Activity mActivity;
    private static final int WAIT = 0x001;
    private boolean isWait = false;
    private Thread mThread = null;

    BindCardFragmentCallBack fragmentCallBack = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.binding_bank_card3_yanzhengma, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCallBack = (BindBankCardActivity)context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity().getApplicationContext();
        mActivity = getActivity();
        btOver.setEnabled(false);
        btOver.setBackgroundResource(R.drawable.next_bg_btn_select);
        etPhone.addTextChangedListener(watcher);
        etCode.addTextChangedListener(watcher);

    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if(etCode.getText().toString().length() > 0
                        && etPhone.getText().toString().length() > 0){
                    btOver.setEnabled(true);
                    btOver.setBackgroundResource(R.drawable.commit);
                }

            } else {
                btOver.setEnabled(false);
                btOver.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        isWait = false;
    }

    @OnClick({R.id.tv_sendcode, R.id.bt_over})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sendcode:
                sendCode();
                break;
            case R.id.bt_over:
                if (!StringMatchUtils.isMobileNO(etPhone.getText().toString())) {
                    etPhone.setError("请输入正确的手机号");
                    return;
                }
                fragmentCallBack.showSuccess();
                break;
        }
    }

    // 发送验证码
    private void sendCode() {
        if (TextUtils.isEmpty(etPhone.getText().toString())) {
            etPhone.setError("请输入手机号");
        } else {
            if (!StringMatchUtils.isMobileNO(etPhone.getText().toString())) {
                etPhone.setError("请输入正确的手机号");
                return;
            }
            tvSendcode.setText("重新发送(" + 60 +"s)");
            tvSendcode.setClickable(false);
            tvSendcode.setTextColor(0xff828282);
            tvSendcode.setBackgroundColor(0xffffffff);
            viewResendDivider.setVisibility(View.VISIBLE);
            waitForClick();
        }

    }

    // 倒计时60s
    private void waitForClick() {
        isWait = true;
        final Handler handler = new Handler(){
            int i=60;
            public void handleMessage(android.os.Message msg) {
                i--;
                tvSendcode.setText("重新发送(" + i +"s)");
                if (i==0) {
                    isWait = false;
                    tvSendcode.setText("发送验证码");
                    tvSendcode.setClickable(true);
                    tvSendcode.setTextColor(0xffffffff);
                    tvSendcode.setBackgroundColor(0xffffa726);
                    viewResendDivider.setVisibility(View.GONE);
                    i=60;
                }
            };
        };

        mThread = new Thread(){
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
}
