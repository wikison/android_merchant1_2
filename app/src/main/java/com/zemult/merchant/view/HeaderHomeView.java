package com.zemult.merchant.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.FindOneRecommandActivity;
import com.zemult.merchant.activity.slash.AllChangjingActivity;
import com.zemult.merchant.activity.slash.SelfUserDetailActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.AllIndustryAdapter;
import com.zemult.merchant.im.CreateBespeakNewActivity;
import com.zemult.merchant.model.M_Ad;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.SlashHelper;

import java.io.Serializable;
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
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.rl_fast_order)
    RoundLinearLayout rlFastOrder;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.tv_service)
    TextView tvService;
    @Bind(R.id.tv_num)
    TextView tvNum;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.rl_me)
    RelativeLayout rlMe;
    @Bind(R.id.ll_me)
    LinearLayout llMe;
    @Bind(R.id.rv)
    RecyclerView rv;
    private Intent it;


    private HeaderAdViewView headerAdViewView; // 广告视图
    private AllIndustryAdapter mAdapter;
    //    private ArrayList<View> pageViews;
//    private List<HomeIndustryAdapter> industryAdapters;
    private LinearLayoutManager linearLayoutManager;

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

    public void setMyInfo(final M_Merchant entity) {
        if (entity == null) {
            llMe.setVisibility(View.GONE);
            return;
        }

        llMe.setVisibility(View.VISIBLE);
        // 服务管家的用户昵称
        if (!TextUtils.isEmpty(entity.saleUserName))
            tvUserName.setText("我是服务管家");
//            tvUserName.setText(entity.saleUserName);
        // 服务管家的用户头像
        mImageManager.loadCircleHead(entity.saleUserHead, ivHead);

        tvService.setText(entity.getExperienceText() + "管家");
        tvNum.setText(" · "+entity.saleUserFanNum + "关注");
        tvMoney.setText(" · "+SlashHelper.userManager().getUserinfo().money + "元收益");
        rlMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                it = new Intent(mContext, SelfUserDetailActivity.class);
                it.putExtra(UserDetailActivity.USER_ID, SlashHelper.userManager().getUserId());
                mContext.startActivity(it);
            }
        });
        if (entity.isHaveInfo == 0) {
            rlFastOrder.setVisibility(View.GONE);
        } else {
            rlFastOrder.setVisibility(View.VISIBLE);
            rlFastOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cbintent = new Intent(mContext, CreateBespeakNewActivity.class);
                    cbintent.putExtra("reviewstatus", entity.reviewstatus + "");
                    cbintent.putExtra("merchantName", entity.name);
                    cbintent.putExtra("merchantId", entity.merchantId + "");
                    mContext.startActivity(cbintent);
                }
            });
        }
    }

    public void setVpIndustrys(final List<M_Industry> industryList) {
        final M_Industry industry = new M_Industry();
        industry.name = "人气推荐";
        industry.id = -1;

        industryList.add(0, industry);
        //设置布局管理器
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(linearLayoutManager);
        //设置适配器
        mAdapter = new AllIndustryAdapter(mContext, industryList);
        rv.setAdapter(mAdapter);

        mAdapter.setSelectedId(industryList.get(0).id);
        mAdapter.setOnItemClickLitener(new AllIndustryAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(int industryId) {
                if (onIndustryListener != null)
                    onIndustryListener.onIndustryClick(industryId);
            }
        });

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = linearLayoutManager.findFirstVisibleItemPosition();
                    View current = linearLayoutManager.findViewByPosition(position);

                    if (onIndustryListener != null)
                        onIndustryListener.onIndustryMove(position, current.getLeft());

                }
            }
        });

        llMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AllChangjingActivity.class);
                intent.putExtra("industryList", (Serializable) industryList);
                mContext.startActivity(intent);
            }
        });
    }


    public void setSelectedId(int id) {
        mAdapter.setSelectedId(id);
    }

    public void onMove(int pos, int offsetLeft) {
        linearLayoutManager.scrollToPositionWithOffset(pos, offsetLeft);
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
                it = new Intent(mContext, FindOneRecommandActivity.class);
                mContext.startActivity(it);

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
    public interface OnIndustryListener {
        void onIndustryClick(int industryId);

        void onIndustryMove(int pos, int offsetLeft);
    }

    private OnIndustryListener onIndustryListener;

    public void setOnIndustryClickListener(OnIndustryListener onIndustryListener) {
        this.onIndustryListener = onIndustryListener;
    }

    public boolean showMe() {
        return rlMe.getVisibility() == View.VISIBLE;
    }

}