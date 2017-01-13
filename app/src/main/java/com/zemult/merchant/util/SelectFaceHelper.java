package com.zemult.merchant.util;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.EmojiAdapter;
import com.zemult.merchant.model.EmojiModle;

public class SelectFaceHelper implements OnItemClickListener {
    private static final String TAG = SelectFaceHelper.class.getSimpleName();
    private Context context;
    private View mFaceView;
    private ViewPager mViewPager;
    private LinearLayout mIndexContainer;
    private LayoutInflater mInflater;
    private int pageSize = 23;
    /**
     * 保存于内存中的表情集合
     */
    private List<EmojiModle> mMsgEmojiData = new ArrayList<EmojiModle>();
    /**
     * 表情分页的结果集合
     */
    public List<List<EmojiModle>> mPageEmojiDatas = new ArrayList<List<EmojiModle>>();

    /**
     * 表情页界面集合
     */
    private ArrayList<View> pageViews;

    /**
     * 表情数据填充器
     */
    private List<EmojiAdapter> faceAdapters;

    /**
     * 当前表情页
     */
    private int current = 0;
    /**
     * 游标点集合
     */
    private ArrayList<ImageView> pointViews;

    private OnEmojiOperateListener onEmojiOperateListener;

    public SelectFaceHelper(Context context, View toolView) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        mFaceView = toolView;
        mViewPager = (ViewPager) mFaceView.findViewById(R.id.vp_emoji);
        mIndexContainer = (LinearLayout) mFaceView.findViewById(R.id.msg_face_index_view);
        ParseData();
        initView();

    }

    private void initView() {
        Init_viewPager();
        Init_Point();
        Init_Data();
    }

    private void Init_viewPager() {
        pageViews = new ArrayList<View>();
        // 左侧添加空页
        View nullView1 = new View(context);
        // 设置透明背景
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView1);

        // 中间添加表情页
        faceAdapters = new ArrayList<EmojiAdapter>();
        for (int i = 0; i < mPageEmojiDatas.size(); i++) {
            GridView view = (GridView) mInflater.inflate(R.layout.msg_face_gridview, null);
            EmojiAdapter adapter = new EmojiAdapter(context, mPageEmojiDatas.get(i));
            view.setSelector(R.color.transparent);
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            view.setOnItemClickListener(this);
            pageViews.add(view);
        }

        // 右侧添加空页面
        View nullView2 = new View(context);
        // 设置透明背景
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView2);
    }

    /**
     * 初始化游标
     */
    private void Init_Point() {

        pointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.xml_round_orange_grey_sel);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            mIndexContainer.addView(imageView, layoutParams);
            if (i == 0 || i == pageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            } else {
                imageView.setVisibility(View.VISIBLE);
            }
            pointViews.add(imageView);

        }
    }

    /**
     * 填充数据
     */
    private void Init_Data() {
        mViewPager.setAdapter(new ViewPagerAdapter(pageViews));
        mViewPager.setCurrentItem(1);
        // 描绘分页点
        draw_Point(1);
        current = 0;
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                current = arg0 - 1;
                // 描绘分页点
                draw_Point(arg0);
                // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        mViewPager.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
                    } else {
                        mViewPager.setCurrentItem(arg0 - 1);// 倒数第二屏
//						pointViews.get(arg0 - 1).setBackgroundResource(R.drawable.icon_jw_face_index_prs);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    /**
     * 绘制游标背景
     */
    public void draw_Point(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setEnabled(true);
            } else {
                pointViews.get(i).setEnabled(false);
            }
        }
    }

    /**
     * 解析字符
     */
    private void ParseData() {
        EmojiModle emojEentry;
        try {
            int len = MsgFaceUtils.faceImgNames.length;
            for (int i = 0; i < len; i++) {
                emojEentry = new EmojiModle();
                emojEentry.setName(MsgFaceUtils.faceImgNames[i]);
                mMsgEmojiData.add(emojEentry);
            }
            int pageCount = (int) Math.ceil(mMsgEmojiData.size() / pageSize + 0.1);
            for (int i = 0; i < pageCount; i++) {
                mPageEmojiDatas.add(getData(i));
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    /**
     * 获取分页数据
     *
     * @param page
     * @return
     */
    private List<EmojiModle> getData(int page) {
        int startIndex = page * pageSize;
        int endIndex = startIndex + pageSize;
        if (endIndex > mMsgEmojiData.size()) {
            endIndex = mMsgEmojiData.size();
        }
        List<EmojiModle> list = new ArrayList<EmojiModle>();
        list.addAll(mMsgEmojiData.subList(startIndex, endIndex));
        if (list.size() < pageSize) {
            for (int i = list.size(); i < pageSize; i++) {
                EmojiModle object = new EmojiModle();
                list.add(object);
            }
        }
        if (list.size() == pageSize) {
            EmojiModle object = new EmojiModle();
            object.setName("delete");
            list.add(object);
        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EmojiModle emoji = (EmojiModle) faceAdapters.get(current).getItem(position);
        if ("delete".equals(emoji.getName())) {
            if (null != onEmojiOperateListener) {
                onEmojiOperateListener.onEmojiDeleted();
            }
        } else if (emoji.getName() != null) {
            SpannableString spannableString = EmojiParser.getInstance(context).getExpressionString(emoji.getName());
            Log.d(TAG, spannableString.toString());
            if (null != onEmojiOperateListener) {
                onEmojiOperateListener.onEmojiSelected(spannableString);
            }
        }
    }

    public void setOnEmojiOperateListener(OnEmojiOperateListener onEmojiOperateListener) {
        this.onEmojiOperateListener = onEmojiOperateListener;
    }

    public interface OnEmojiOperateListener {

        void onEmojiSelected(SpannableString spanEmojiStr);

        void onEmojiDeleted();
    }

    class ViewPagerAdapter extends PagerAdapter {

        private List<View> pageViews;

        public ViewPagerAdapter(List<View> pageViews) {
            super();
            this.pageViews = pageViews;
        }

        // 显示数目
        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(pageViews.get(arg1));
        }


        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }
    }
}
