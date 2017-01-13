package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.MerchantCardsAdapter;
import com.zemult.merchant.fragment.MerchantCardsFragment;
import com.zemult.merchant.util.ImageManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by admin on 2016/8/1.
 */
//商家优惠券
public class TestCardsActivity extends FragmentActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;


    MerchantCardsAdapter merchantCardsAdapter;
    @Bind(R.id.button21)
    RadioButton button21;
    @Bind(R.id.button22)
    RadioButton button22;
    @Bind(R.id.segmented2)
    SegmentedGroup segmented2;
    @Bind(R.id.content)
    FrameLayout content;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private MerchantCardsFragment merchantCardsFragment;


    ImageManager imageManager;
    int merchantId = 0;
    public static final String INTENT_MERCHANTID = "merchantId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testcards);
        ButterKnife.bind(this);
        lhTvTitle.setText("优惠券");

        lhBtnRightiamge.setVisibility(View.VISIBLE);
        lhBtnRightiamge.setBackgroundResource(R.mipmap.lishi);

        merchantId = getIntent().getIntExtra(INTENT_MERCHANTID, -1);

        segmented2.setTintColor(getResources().getColor(R.color.bg_head));
        button21.setChecked(true);

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.content,MerchantCardsFragment.newInstance(merchantId,0));
        transaction.commit();



        segmented2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case R.id.button21:
                        fragmentManager = getSupportFragmentManager();
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.content,MerchantCardsFragment.newInstance(merchantId,0));
                        transaction.commit();
                        break;
                    case R.id.button22:
                        fragmentManager = getSupportFragmentManager();
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.content,MerchantCardsFragment.newInstance(merchantId,1));
                        transaction.commit();
                        break;
                }
            }
        });



//


    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_btn_rightiamge})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.lh_btn_rightiamge:

                Intent intent1 = new Intent(TestCardsActivity.this, CardsHistoryActivity.class);
                intent1.putExtra(CardsHistoryActivity.INTENT_MERCHANTID, merchantId);
                startActivity(intent1);


                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    /**
     * =================================================处理刷新请求===========================================================================
     */
//    @Subscribe(threadMode = ThreadMode.MainThread)
//    public void refreshEvent(String s) {
//        if (EnterCardsActivity.Call_Cards_REFRESH.equals(s))
//            getNetworkData(false);
//    }


}
