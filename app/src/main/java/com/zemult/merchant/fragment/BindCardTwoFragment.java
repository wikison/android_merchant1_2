package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.BindBankCardActivity;
import com.zemult.merchant.aip.common.CommonCheckcodeRequest;
import com.zemult.merchant.aip.common.CommonGetCodeBankRequest;
import com.zemult.merchant.aip.mine.UserBandcardDoRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

/**
 * 绑定银行卡2 输入信息
 */
public class BindCardTwoFragment extends BaseFragment {
    private static final int WAIT = 0x001;
    public static final String CARD_TYPE = "cardType";
    public static final String BANK_NAME = "cardName";
    public static final String CARD_NUM = "cardNum";
    public static final int JIE_JI_KA = 1;
    public static final int XIN_YONG_KA = 2;

    @Bind(R.id.tv_bank_name)
    TextView tvBankName;
    @Bind(R.id.tv_bankcard_kind)
    TextView tvBankcardKind;
    @Bind(R.id.et_ownername)
    EditText etOwnername;
    @Bind(R.id.et_identification)
    EditText etIdentification;
    @Bind(R.id.et_safe_num)
    EditText etSafeNum;
    @Bind(R.id.ll_safe_num)
    LinearLayout llSafeNum;
    @Bind(R.id.et_use_time)
    EditText etUseTime;
    @Bind(R.id.ll_use_time)
    LinearLayout llUseTime;
    @Bind(R.id.ll_just_credit_card_has)
    LinearLayout llJustCreditCardHas;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.bt_next)
    Button btNext;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.tv_sendcode)
    TextView tvSendcode;
    @Bind(R.id.tv_error)
    TextView tvError;

    private Context mContext;
    private Activity mActivity;

    // 借记卡1 信用卡2
    private int cardType = -1;
    private Bundle bundle;
    BindCardFragmentCallBack fragmentCallBack = null;
    private boolean isWait = false;
    private Thread mThread = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.binding_bank_card2, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCallBack = (BindBankCardActivity) context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
    }

    private void initData() {
        mContext = getActivity().getApplicationContext();
        mActivity = getActivity();
        bundle = getArguments();
        cardType = bundle.getInt(CARD_TYPE);
    }

    private void initView() {
        tvBankName.setText(bundle.getString(BANK_NAME));
        if (cardType == JIE_JI_KA) {
            llJustCreditCardHas.setVisibility(View.GONE);
            tvBankcardKind.setText("借记卡");
        } else if (cardType == XIN_YONG_KA) {
            llJustCreditCardHas.setVisibility(View.VISIBLE);
            tvBankcardKind.setText("信用卡");
        }
        btNext.setEnabled(false);
        btNext.setBackgroundResource(R.drawable.next_bg_btn_select);
        etOwnername.addTextChangedListener(watcher);
        etIdentification.addTextChangedListener(watcher);
        etSafeNum.addTextChangedListener(watcher);
        etUseTime.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        etCode.addTextChangedListener(watcher);
        tvSendcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvSendcode.getPaint().setAntiAlias(true);//抗锯齿
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (cardType == JIE_JI_KA) {
                    if (etOwnername.getText().toString().length() > 0
                            && etIdentification.getText().toString().length() > 0
                            && etPhone.getText().toString().length() > 0
                            && etCode.getText().toString().length() > 0) {
                        btNext.setEnabled(true);
                        btNext.setBackgroundResource(R.drawable.common_selector_btn);
                    }
                } else if (cardType == XIN_YONG_KA) {
                    if (etOwnername.getText().length() > 0
                            && etIdentification.getText().toString().length() > 0
                            && etSafeNum.getText().toString().length() > 0
                            && etUseTime.getText().toString().length() > 0
                            && etPhone.getText().toString().length() > 0
                            && etCode.getText().toString().length() > 0) {
                        btNext.setEnabled(true);
                        btNext.setBackgroundResource(R.drawable.commit);
                    }
                }
            } else {
                btNext.setEnabled(false);
                btNext.setBackgroundResource(R.drawable.next_bg_btn_select);
            }

            etPhone.setTextColor(mContext.getResources().getColor(R.color.font_black_28));
            etIdentification.setTextColor(mContext.getResources().getColor(R.color.font_black_28));
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

    @OnClick(R.id.tv_sendcode)
    public void onBtnCodeClick() {
        if (TextUtils.isEmpty(etPhone.getText().toString())) {
            tvError.setText("请输入手机号码");
        } else {
            if (!StringMatchUtils.isMobileNO(etPhone.getText().toString())) {
                tvError.setText("请输入正确的手机号码");
                etPhone.setTextColor(mContext.getResources().getColor(R.color.bg_head_red));
                return;
            }
            getCode();
        }
    }

    @OnClick(R.id.bt_next)
    public void onClick() {
        if (!StringMatchUtils.isIdCard(etIdentification.getText().toString())) {
            tvError.setText("请输入正确的身份证号码");
            etIdentification.setTextColor(mContext.getResources().getColor(R.color.bg_head_red));
            return;
        }
        if (!StringMatchUtils.isMobileNO(etPhone.getText().toString())) {
            tvError.setText("请输入正确的手机号码");
            etPhone.setTextColor(mContext.getResources().getColor(R.color.bg_head_red));
            return;
        }
        checkCode();
    }

    private CommonGetCodeBankRequest request_common_getcode;
    private void getCode() {
        try {
            if (request_common_getcode != null) {
                request_common_getcode.cancel();
            }
            CommonGetCodeBankRequest.Input input = new CommonGetCodeBankRequest.Input();
            input.phone = etPhone.getText().toString();
            input.convertJosn();

            request_common_getcode = new CommonGetCodeBankRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        ToastUtil.showMessage("验证码已发送, 请查收!");
                        tvSendcode.setText("重新获取(" + 60 + "s)");
                        tvSendcode.setClickable(false);
                        tvSendcode.setTextColor(0xff828282);
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

    private CommonCheckcodeRequest request_common_checkcode;
    private void checkCode() {//发送验证码校验
        showUncanclePd();
        try {
            if (request_common_checkcode != null) {
                request_common_checkcode.cancel();
            }
            final CommonCheckcodeRequest.Input input = new CommonCheckcodeRequest.Input();
            input.phone = etPhone.getText().toString();
            input.code = etCode.getText().toString();
            input.convertJosn();

            request_common_checkcode = new CommonCheckcodeRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                    dismissPd();
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        user_bandcard_do();
                    } else {
                        tvError.setText(((CommonResult) response).info);
                        etCode.setTextColor(mContext.getResources().getColor(R.color.bg_head_red));
                    }

                    dismissPd();
                }
            });
            sendJsonRequest(request_common_checkcode);
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
                tvSendcode.setText("重新获取(" + i + "s)");
                if (i == 0) {
                    isWait = false;
                    tvSendcode.setText("重新获取");
                    tvSendcode.setClickable(true);
                    tvSendcode.setTextColor(0xffe6bb7c);
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


    private UserBandcardDoRequest bandcardDoRequest;
    private void user_bandcard_do() {
        showUncanclePd();
        try {
            if (bandcardDoRequest != null) {
                bandcardDoRequest.cancel();
            }
            UserBandcardDoRequest.Input input = new UserBandcardDoRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.name = etOwnername.getText().toString();
            input.bankNumber = bundle.getString(CARD_NUM);
            input.bankName = bundle.getString(BANK_NAME);
            input.idCard = etIdentification.getText().toString();
            input.phone = etPhone.getText().toString();
            input.code = etCode.getText().toString();
            input.convertJosn();

            bandcardDoRequest = new UserBandcardDoRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                    dismissPd();
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        ToastUtil.showMessage("绑定成功");
                        SlashHelper.userManager().getUserinfo().setIsConfirm(1);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(BindCardSuccessFragment.BANK_NAME, bundle.getString(BANK_NAME));
                        bundle1.putString(BindCardSuccessFragment.CARD_NUM, bundle.getString(CARD_NUM));
                        fragmentCallBack.showSuccess(bundle1);
                    }else{
                        tvError.setText(((CommonResult) response).info);
                        etIdentification.setTextColor(mContext.getResources().getColor(R.color.bg_head_red));
                        etOwnername.setTextColor(mContext.getResources().getColor(R.color.bg_head_red));
                        etPhone.setTextColor(mContext.getResources().getColor(R.color.bg_head_red));
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(bandcardDoRequest);
        } catch (Exception e) {
            Log.e("COMMON_GETCODE", e.toString());
        }
    }
}
