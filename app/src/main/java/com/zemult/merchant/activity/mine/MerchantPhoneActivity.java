package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.MerchantPhoneAdapter;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class MerchantPhoneActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.ll_add)
    LinearLayout llAdd;

    public static final String INTENT_PHONE = "phone";
    private Context mContext;
    private Activity mActivity;

    private String phone;
    private List<String> list = new LinkedList<>();
    private MerchantPhoneAdapter adapter;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_merchant_phone);
    }

    @Override
    public void init() {
        mContext = this;
        mActivity = this;
        tvRight.setVisibility(View.VISIBLE);
        lhTvTitle.setText("电话");
        tvRight.setText("编辑");

        phone = getIntent().getStringExtra(INTENT_PHONE);
        if(!TextUtils.isEmpty(phone)){
            String[] phones = phone.split(",");
            if(phones != null && phones.length > 0){
                for (String one : phones){
                    list.add(one);
                }
            }
        }
        adapter = new MerchantPhoneAdapter(mContext, list, mActivity);
        listview.setAdapter(adapter);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right, R.id.iv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_right:
                if("编辑".equals(tvRight.getText().toString())){
                    tvRight.setText("完成");
                    llAdd.setVisibility(View.VISIBLE);
                    adapter.refresh(true);
                }else
                    check();
                break;
            case R.id.iv_add:
                if(adapter.getData().size() < 2)
                    adapter.addOne();

                break;
        }
    }

    private void check() {
        boolean correct = true;

        if(adapter.getData().isEmpty()){
            ToastUtil.showMessage("请添加电话号码");
            correct = false;
        }else {
            for(String phone : adapter.getData()){
                if(TextUtils.isEmpty(phone.trim())){
                    ToastUtil.showMessage("请输入电话号码");
                    correct = false;
                    break;
                } else if(!StringMatchUtils.isMobileNO(phone)){
                    ToastUtil.showMessage("电话号码格式不正确");
                    correct = false;
                    break;
                }
            }
        }

        if(correct){
            hideSoftInputView();
            tvRight.setText("编辑");
            llAdd.setVisibility(View.GONE);
            adapter.refresh(false);

            phone = "";
            for(String one : adapter.getData()){
                this.phone += one + ",";
            }
            if (phone.lastIndexOf(",") == phone.length() - 1)
                phone = phone.substring(0, phone.length() - 1);

            Intent intent = new Intent();
            intent.putExtra(INTENT_PHONE, phone);
            setResult(RESULT_OK, intent);
            onBackPressed();
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
