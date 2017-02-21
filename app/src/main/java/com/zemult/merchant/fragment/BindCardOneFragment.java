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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.BindBankCardActivity;
import com.zemult.merchant.aip.common.CommonFindBankNameRequest;
import com.zemult.merchant.aip.common.UserLoginRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.model.apimodel.APIM_CommonAppVersion;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

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
    @Bind(R.id.checkbox)
    CheckBox checkbox;
    @Bind(R.id.tv_readandconsent)
    TextView tvReadandconsent;
    @Bind(R.id.tv_agreement)
    TextView tvAgreement;
    @Bind(R.id.bt_next)
    Button btNext;


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
        fragmentCallBack = (BindBankCardActivity)context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity().getApplicationContext();
        mActivity = getActivity();

        btNext.setEnabled(false);
        btNext.setBackgroundResource(R.drawable.next_bg_btn_select);
        etBankcard.addTextChangedListener(watcher);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 15) {
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
        if(!checkbox.isChecked()){
            Toast.makeText(mContext, "请先阅读并同意《用户协议》", Toast.LENGTH_SHORT).show();
            return;
        }


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
                if(((APIM_CommonAppVersion) response).status == 1){
                    Bundle bundle = new Bundle();
                    bundle.putInt(BindCardTwoFragment.CARD_TYPE, BindCardTwoFragment.JIE_JI_KA);
                    bundle.putString(BindCardTwoFragment.BANK_NAME, ((APIM_CommonAppVersion) response).bankName);
                    fragmentCallBack.showTwo(bundle);
                }else
                    ToastUtil.showMessage(((APIM_CommonAppVersion) response).info);

                dismissPd();
            }
        });
        sendJsonRequest(request);
    }
}
