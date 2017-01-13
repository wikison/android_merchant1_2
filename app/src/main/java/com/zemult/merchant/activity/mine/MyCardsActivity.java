package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.fragment.CanUseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;

public class MyCardsActivity extends FragmentActivity {

    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.button21)
    RadioButton button21;
    @Bind(R.id.button22)
    RadioButton button22;
    @Bind(R.id.segmented2)

    SegmentedGroup segmented2;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private CanUseFragment canUseFragment;
 //   private CannotUseFragment cannotUseFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cards);
        ButterKnife.bind(this);

        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("我的券包");
        segmented2.setTintColor(getResources().getColor(R.color.bg_head));
        button21.setChecked(true);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.content,CanUseFragment.newInstance(0));
        transaction.commit();



        segmented2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case R.id.button21:
                        fragmentManager = getSupportFragmentManager();
                        transaction = fragmentManager.beginTransaction();
                       // canUseFragment = new CanUseFragment();
                        transaction.replace(R.id.content,CanUseFragment.newInstance(0));
                        transaction.commit();
                        break;
                    case R.id.button22:
                        fragmentManager = getSupportFragmentManager();
                        transaction = fragmentManager.beginTransaction();
                   //     cannotUseFragment = new CannotUseFragment();
                        transaction.replace(R.id.content,CanUseFragment.newInstance(1));
                        transaction.commit();
                        break;
                }
            }
        });




    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }


}

