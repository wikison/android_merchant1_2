package com.zemult.merchant.activity.guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.SplashActivity;

import java.util.ArrayList;
import java.util.List;


public class GuideActivity extends FragmentActivity {

    private ViewPager viewPage;
    private  Fragment0 mFragment0;
    private Fragment1 mFragment1;
    private Fragment2 mFragment2;
    private Fragment3 mFragment3;
    private boolean misScrolled;

    private PagerAdapter mPgAdapter;
    private List<Fragment> mListFragment = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        SharedPreferences sharedPreferences=GuideActivity.this.getSharedPreferences("share",MODE_PRIVATE);
        boolean isFirstRun=sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(isFirstRun){       //第一次
            editor.putBoolean("isFirstRun", false);
            editor.commit();
        }else{
            startActivity(new Intent(GuideActivity.this,SplashActivity.class));
            GuideActivity.this.finish();
        }
        initView();
        viewPage.setOnPageChangeListener(new MyPagerChangeListener());

        viewPage.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedposition / 2 + 0.5f);
                page.setScaleY(normalizedposition / 2 + 0.5f);
            }
        });



    }

    private void initView() {
        viewPage = (ViewPager) findViewById(R.id.viewpager);
        mFragment0 = new Fragment0();
        mFragment1 = new Fragment1();
        mFragment2 = new Fragment2();
        mFragment3 = new Fragment3();

        mListFragment.add(mFragment0);
        mListFragment.add(mFragment1);
        mListFragment.add(mFragment2);
        mListFragment.add(mFragment3);

        mPgAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                mListFragment);
        viewPage.setAdapter(mPgAdapter);

    }

    public class MyPagerChangeListener implements OnPageChangeListener {

        public void onPageSelected(int position) {

        }

        public void onPageScrollStateChanged(int state) {

            //最后一页左滑进入
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    misScrolled = false;
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    misScrolled = true;
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    if (viewPage.getCurrentItem() == viewPage.getAdapter().getCount() - 1 && !misScrolled) {
                        startActivity(new Intent(GuideActivity.this, SplashActivity.class));
                        GuideActivity.this.finish();
                    }
                    misScrolled = true;
                    break;
            }

        }

        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 这个Activity中有Fragment，限的通知。这句话不能注释，否则Fragment将接收不到获取权
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}