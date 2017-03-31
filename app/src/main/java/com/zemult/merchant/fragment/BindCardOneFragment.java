package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.RegisterActivity;
import com.zemult.merchant.activity.mine.BindBankCardActivity;
import com.zemult.merchant.aip.common.CommonFindBankNameRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_CommonAppVersion;
import com.zemult.merchant.util.IntentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

/**
 * 绑定银行卡1 输入卡号
 */
public class BindCardOneFragment extends BaseFragment {

    @Bind(R.id.et_bankcard)
    EditText etBankcard;
    @Bind(R.id.cb_agree)
    CheckBox cbAgree;
    @Bind(R.id.tv_readandconsent)
    TextView tvReadandconsent;
    @Bind(R.id.tv_agreement)
    TextView tvAgreement;
    @Bind(R.id.bt_next)
    Button btNext;
    @Bind(R.id.tv_error)
    TextView tvError;


    private Context mContext;
    private Activity mActivity;

    BindCardFragmentCallBack fragmentCallBack = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.binding_bank_card1_input_cardnum, container, false);
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

        mContext = getActivity().getApplicationContext();
        mActivity = getActivity();

        btNext.setEnabled(false);
        btNext.setBackgroundResource(R.drawable.next_bg_btn_select);
        etBankcard.addTextChangedListener(watcher);
        cbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (etBankcard.getText().toString().length() > 15 && cbAgree.isChecked()) {
                    btNext.setEnabled(true);
                    btNext.setBackgroundResource(R.drawable.common_selector_btn);
                } else {
                    btNext.setEnabled(false);
                    btNext.setBackgroundResource(R.drawable.next_bg_btn_select);
                }
            }
        });
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 15 && cbAgree.isChecked()) {
                btNext.setEnabled(true);
                btNext.setBackgroundResource(R.drawable.common_selector_btn);
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
        if (!cbAgree.isChecked()) {
            Toast.makeText(mContext, "请先阅读并同意《用户协议》", Toast.LENGTH_SHORT).show();
            return;
        }
        common_findBankName();
    }

    @OnClick(R.id.tv_agreement)
    public void onAgreementClick() {
        IntentUtil.start_activity(mActivity, BaseWebViewActivity.class, new Pair<String, String>("titlename", getString(R.string.app_name) + "服务协议"), new Pair<String, String>("url", Constants.PROTOCOL_REGISTER));
    }

    private CommonFindBankNameRequest request;

    private void common_findBankName() {
        showUncanclePd();
        if (request != null) {
            request.cancel();
        }
        CommonFindBankNameRequest.Input input = new CommonFindBankNameRequest.Input();
        input.number = etBankcard.getText().toString();
        input.convertJosn();

        request = new CommonFindBankNameRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(final Object response) {
                if (((CommonResult) response).status == 1) {
                    if(((CommonResult) response).isFit == 1){
                        Bundle bundle = new Bundle();
                        bundle.putInt(BindCardTwoFragment.CARD_TYPE, BindCardTwoFragment.JIE_JI_KA);
                        bundle.putString(BindCardTwoFragment.BANK_NAME, ((CommonResult) response).bankName);
                        bundle.putString(BindCardTwoFragment.CARD_NUM, etBankcard.getText().toString());
                        fragmentCallBack.showTwo(bundle);
                    }else
                        tvError.setText("请绑定招商银行借记卡");
                } else
                    tvError.setText(((CommonResult) response).info);

                dismissPd();
            }
        });
        sendJsonRequest(request);
    }
}
