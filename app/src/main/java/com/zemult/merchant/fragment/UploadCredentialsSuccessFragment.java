package com.zemult.merchant.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseFragment;

import butterknife.ButterKnife;

/**
 * 绑定银行卡3 成功
 */
public class UploadCredentialsSuccessFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_credentials_3_success, container, false);
        ButterKnife.bind(this, view);
        return view;
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
