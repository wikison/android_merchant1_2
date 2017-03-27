package com.zemult.merchant.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.AllIndustryAdapter;
import com.zemult.merchant.model.M_Ad;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ToastUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 首页头部
 *
 * @author djy
 * @time 2016/12/27 9:39
 */
public class HeaderHomeView extends HeaderViewInterface<String> {


    @Bind(R.id.ll_ad_container)
    LinearLayout llAdContainer;
    @Bind(R.id.ll_merchant)
    LinearLayout llMerchant;
    @Bind(R.id.ll_people)
    LinearLayout llPeople;
    @Bind(R.id.rv)
    RecyclerView rv;


    private HeaderAdViewView headerAdViewView; // 广告视图
    private AllIndustryAdapter mAdapter;
//    private ArrayList<View> pageViews;
//    private List<HomeIndustryAdapter> industryAdapters;

    public HeaderHomeView(Activity context) {
        super(context);
    }

    @Override
    protected void getView(String s, ListView listView) {
        View view = mInflate.inflate(R.layout.home_header, listView, false);
        listView.addHeaderView(view);
        ButterKnife.bind(this, view);
    }

    public void setAd(List<M_Ad> advertList) {
        // 设置广告数据 加入到smoothListView的headerView
        headerAdViewView = new HeaderAdViewView(mContext, DensityUtil.dip2px(mContext, 194));
        headerAdViewView.fillView(advertList, llAdContainer);
    }

    public void setVpIndustrys(List<M_Industry> industryList) {
//        M_Industry industry = new M_Industry();
//        industry.name = "全部";
//        industry.id = -1;
//
//        industryList.add(0, industry);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(linearLayoutManager);
        //设置适配器
        mAdapter = new AllIndustryAdapter(mContext, industryList);
        rv.setAdapter(mAdapter);

        mAdapter.setSelectedId(industryList.get(0).id);
    }


    public void setSelectedId(int id) {
        mAdapter.setSelectedId(id);
    }

//    public void setVpIndustrys(List<M_Industry> industryList) {
//        List<M_Industry> industries1 = new ArrayList<>();
//        List<M_Industry> industries2 = new ArrayList<>();
//        for (int i = 0; i < industryList.size(); i++) {
//            if (i < 10)
//                industries1.add(industryList.get(i));
//            else
//                industries2.add(industryList.get(i));
//        }
//        List<List<M_Industry>> mIndustryiDatas = new ArrayList<List<M_Industry>>();
//        mIndustryiDatas.add(industries1);
//        mIndustryiDatas.add(industries2);
//
//        init_viewPager(mIndustryiDatas);
//    }
//
//
//    private void init_viewPager(List<List<M_Industry>> mIndustryDatas) {
//        pageViews = new ArrayList<View>();
//        industryAdapters = new ArrayList<HomeIndustryAdapter>();
////        for (int i = 0; i < mIndustryDatas.size(); i++) {
//        for (int i = 0; i < 1; i++) {
//            GridView view = (GridView) LayoutInflater.from(mContext).inflate(R.layout.home_industry_gridview, null);
//            final HomeIndustryAdapter adapter = new HomeIndustryAdapter(mContext, mIndustryDatas.get(i));
//            view.setSelector(R.color.transparent);
//            view.setAdapter(adapter);
//            industryAdapters.add(adapter);
//            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    if (headerClickListener != null)
//                        headerClickListener.onTabClick(adapter.getItem(position).id, adapter.getItem(position).name);
//                }
//            });
//            pageViews.add(view);
//        }
//        vpIndustry.setAdapter(new ViewPagerAdapter(pageViews));
//    }

    @OnClick({R.id.ll_merchant, R.id.ll_people})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_merchant:
                break;
            case R.id.ll_people:
                break;
        }
    }

//    class ViewPagerAdapter extends PagerAdapter {
//
//        private List<View> pageViews;
//
//        public ViewPagerAdapter(List<View> pageViews) {
//            super();
//            this.pageViews = pageViews;
//        }
//
//        // 显示数目
//        @Override
//        public int getCount() {
//            return pageViews.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View arg0, Object arg1) {
//            return arg0 == arg1;
//        }
//
//        @Override
//        public int getItemPosition(Object object) {
//            return super.getItemPosition(object);
//        }
//
//        @Override
//        public void destroyItem(View arg0, int arg1, Object arg2) {
//            ((ViewPager) arg0).removeView(pageViews.get(arg1));
//        }
//
//
//        @Override
//        public Object instantiateItem(View arg0, int arg1) {
//            ((ViewPager) arg0).addView(pageViews.get(arg1));
//            return pageViews.get(arg1);
//        }
//    }
//
//    public interface OnHeaderClickListener {
//        void onTabClick(int industryId, String industryName);
//    }
//
//    private OnHeaderClickListener headerClickListener;
//
//    public void setOnHeaderClickListener(OnHeaderClickListener headerClickListener) {
//        this.headerClickListener = headerClickListener;
//    }

}