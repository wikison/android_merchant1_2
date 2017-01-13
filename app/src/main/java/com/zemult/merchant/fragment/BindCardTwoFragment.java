package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.BindBankCardActivity;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.util.StringMatchUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 绑定银行卡2 输入信息
 */
public class BindCardTwoFragment extends BaseFragment {
    public static final String BUNDLE_CARD_INFO = "cardInfo";
    public static final String CARD_TYPE = "cardType";
    public static final String BANK_NAME = "cardName";
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
    private Context mContext;
    private Activity mActivity;

    // 借记卡1 信用卡2
    private int cardType = -1;
    private Bundle bundle;
    BindCardFragmentCallBack fragmentCallBack = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.binding_bank_card2, container, false);
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
        if (cardType == JIE_JI_KA){
            llJustCreditCardHas.setVisibility(View.GONE);
            tvBankcardKind.setText("借记卡");
        }
        else if(cardType == XIN_YONG_KA){
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
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if(cardType == JIE_JI_KA){
                    if(etOwnername.getText().toString().length() > 0
                            && etIdentification.getText().toString().length() > 0
                            && etPhone.getText().toString().length() > 0){
                        btNext.setEnabled(true);
                        btNext.setBackgroundResource(R.drawable.commit);
                    }
                }else if(cardType == XIN_YONG_KA){
                    if(etOwnername.getText().length() > 0
                            && etIdentification.getText().toString().length() > 0
                            && etSafeNum.getText().toString().length() > 0
                            && etUseTime.getText().toString().length() > 0
                            && etPhone.getText().toString().length() > 0){
                        btNext.setEnabled(true);
                        btNext.setBackgroundResource(R.drawable.commit);
                    }
                }
            } else {
                btNext.setEnabled(false);
                btNext.setBackgroundResource(R.drawable.next_bg_btn_select);
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
    }

    @OnClick(R.id.bt_next)
    public void onClick() {
        if(!StringMatchUtils.isMobileNO(etPhone.getText().toString())){
            etPhone.setError("请输入正确的手机号");
            return;
        }
        fragmentCallBack.showThree();
    }
}
