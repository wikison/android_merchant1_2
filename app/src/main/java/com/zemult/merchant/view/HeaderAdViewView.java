package com.zemult.merchant.view;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.HeaderAdAdapter;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.model.M_Ad;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.IntentUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;

public class HeaderAdViewView extends HeaderViewInterface2<List<M_Ad>> {
    @Bind(R.id.vp_ad)
    ViewPager vpAd;
    @Bind(R.id.ll_index_container)
    LinearLayout llIndexContainer;
    @Bind(R.id.tv_pic_num)
    RoundTextView tvPicNum;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    private static final int TYPE_CHANGE_AD = 0;

    public static final int TYPE_INDICATOR_DOT = 110;
    public static final int TYPE_INDICATOR_NUM = 120;

    private int indicator_type = TYPE_INDICATOR_DOT;

    private Thread mThread;
    private List<ImageView> ivList;
    private List<String> ivurlList = new ArrayList<String>();
    ;
    private boolean isStopThread = false;
    private ImageManager mImageManager;
    private int height;
    private HeaderAdAdapter photoAdapter;

    private int showType = 1;  //0  无操作，1  网页  2   跳转到图片展示  3 自定义
    private boolean rotate = true;
    private boolean round,showTitle;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TYPE_CHANGE_AD) {
                vpAd.setCurrentItem(vpAd.getCurrentItem() + 1);
            }
        }
    };

    public HeaderAdViewView(Activity context) {
        super(context);
        ivList = new ArrayList<>();
        mImageManager = new ImageManager(context);
    }

    public HeaderAdViewView(Activity context, int height) {
        super(context);
        ivList = new ArrayList<>();
        mImageManager = new ImageManager(context);
        this.height = height;
    }

    public void showNum() {
        indicator_type = TYPE_INDICATOR_NUM;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }
    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
    }

    public void setRound(boolean round) {
        this.round = round;
    }

    @Override
    protected void getView(List<M_Ad> list, ViewGroup viewGroup) {
        View view = mInflate.inflate(R.layout.header_ad_layout, viewGroup, false);
        if (height > 0) {
            if (viewGroup instanceof ListView) {
                AbsListView.LayoutParams lp = (AbsListView.LayoutParams) view.getLayoutParams();
                lp.height = height;
                view.setLayoutParams(lp);
            } else {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                lp.height = height;
                view.setLayoutParams(lp);
            }
        }
        ButterKnife.bind(this, view);
        dealWithTheView(list);

        if (viewGroup instanceof ListView) {
            ListView listView = (ListView) viewGroup;
            listView.addHeaderView(view);
        } else {
            viewGroup.addView(view);
        }
    }

    private void dealWithTheView(List<M_Ad> list) {
        ivList.clear();
        ivurlList.clear();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ivList.add(createImageView(list.get(i), i));
            ivurlList.add(list.get(i).getImg());
        }
        photoAdapter = new HeaderAdAdapter(mContext, ivList);
        vpAd.setAdapter(photoAdapter);
        addIndicatorImageViews(size);
        setViewPagerChangeListener(size);
        if (rotate)
            startADRotate();

    }

    public void setData(List<M_Ad> list) {
        if (rotate)
            stopADRotate();

        dealWithTheView(list);
    }

    // 创建要显示的ImageView
    private ImageView createImageView(final M_Ad mAd, final int postion) {
        ImageView imageView = new ImageView(mContext);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        if (!round)
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        else
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        if (showType == 1) {//网页
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.start_activity(mContext, BaseWebViewActivity.class,
                            new Pair<String, String>("titlename", mAd.name), new Pair<String, String>("url", mAd.getUrl()));
//                    IntentUtil.start_activity(mContext,HomeAdActivity.class,
//                            new Pair<String, String>("titlename",mAd.name),new Pair<String, String>("url",mAd.getUrl()));
                }
            });
        } else if (showType == 2) {//打开图片展示

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.toImageDetial(mContext, postion, ivurlList, false);
                }
            });

        } else if (showType == 3) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageOnClick != null)
                        imageOnClick.imageOnclick(postion);
                }
            });
        }

        if (!round)
            mImageManager.loadUrlImage(mAd.getImg(), imageView, "@500h");
        else
            mImageManager.loadRoundImage2(mAd.getImg(), imageView, 24, "@450h");

        if(showTitle && !StringUtils.isBlank(mAd.getName())){
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(mAd.getName());
        }else
            tvTitle.setVisibility(View.GONE);

        return imageView;
    }

    // 添加指示图标
    private void addIndicatorImageViews(int size) {
        // 只有一张图片时不显示指示器
        if (size == 1) {
            llIndexContainer.setVisibility(View.GONE);
            tvPicNum.setVisibility(View.GONE);
            return;
        }
        if (indicator_type == TYPE_INDICATOR_DOT) {
            llIndexContainer.setVisibility(View.VISIBLE);
            tvPicNum.setVisibility(View.GONE);
            llIndexContainer.removeAllViews();
            for (int i = 0; i < size; i++) {
                ImageView iv = new ImageView(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DensityUtil.dip2px(mContext, 6), DensityUtil.dip2px(mContext, 6));
                if (i != 0) {
                    lp.leftMargin = DensityUtil.dip2px(mContext, 7);
                }
                iv.setLayoutParams(lp);
                iv.setBackgroundResource(R.drawable.xml_round_orange_grey_sel);
                iv.setEnabled(false);
                if (i == 0) {
                    iv.setEnabled(true);
                }
                llIndexContainer.addView(iv);
            }
        } else if (indicator_type == TYPE_INDICATOR_NUM) {
            llIndexContainer.setVisibility(View.GONE);
            tvPicNum.setVisibility(View.VISIBLE);
            setNum(1);
        }
    }

    public void setNum(int currentNum) {
        tvPicNum.setText(currentNum + "/" + ivList.size());
    }

    // 为ViewPager设置监听器
    private void setViewPagerChangeListener(final int size) {
        vpAd.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (ivList != null && ivList.size() > 0) {
                    int newPosition = position % size;

                    if (indicator_type == TYPE_INDICATOR_DOT) {
                        for (int i = 0; i < size; i++) {
                            llIndexContainer.getChildAt(i).setEnabled(false);
                            if (i == newPosition) {
                                llIndexContainer.getChildAt(i).setEnabled(true);
                            }
                        }
                    } else if (indicator_type == TYPE_INDICATOR_NUM) {
                        setNum(newPosition + 1);
                    }

                }
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    // 启动循环广告的线程
    private void startADRotate() {
        isStopThread = false;
        // 一个广告的时候不用转
        if (ivList == null || ivList.size() <= 1) {
            return;
        }
        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // 当没离开该页面时一直转
                    while (!isStopThread) {
                        // 每隔4秒转一次
                        SystemClock.sleep(4000);
                        // 在主线程更新界面
                        mHandler.sendEmptyMessage(TYPE_CHANGE_AD);
                    }
                }
            });
            mThread.start();
        }
    }

    // 停止循环广告的线程，清空消息队列
    public void stopADRotate() {
        isStopThread = true;
        if (mHandler != null && mHandler.hasMessages(TYPE_CHANGE_AD)) {
            mHandler.removeMessages(TYPE_CHANGE_AD);
        }
    }

    public interface ImageOnClick {
        void imageOnclick(int postion);
    }

    private ImageOnClick imageOnClick;

    public void setImageOnClick(ImageOnClick imageOnClick) {
        this.imageOnClick = imageOnClick;
    }
}

