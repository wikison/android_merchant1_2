package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.BindBankCardActivity;
import com.zemult.merchant.app.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BindCardSuccessFragment extends BaseFragment {
    public static final String BANK_NAME = "cardName";
    public static final String CARD_NUM = "cardNum";
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_num)
    TextView tvNum;

    BindCardFragmentCallBack fragmentCallBack = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_success_bank, container, false);
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

        init();
    }


    private void init() {
        tvName.setText(getArguments().getString(BANK_NAME));
        tvNum.setText("**** **** **** " + getArguments().getString(CARD_NUM).substring(
                getArguments().getString(CARD_NUM).length() - 4,
                getArguments().getString(CARD_NUM).length()));
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
