package com.zemult.merchant.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.FilterOneAdapter;
import com.zemult.merchant.model.FilterData;
import com.zemult.merchant.model.FilterEntity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/6/3.
 */
public class FilterView extends LinearLayout implements View.OnClickListener {

    @Bind(R.id.tv_sort)
    TextView tvSort;
    @Bind(R.id.iv_sort_arrow)
    ImageView ivSortArrow;
    @Bind(R.id.tv_filter)
    TextView tvFilter;
    @Bind(R.id.iv_filter_arrow)
    ImageView ivFilterArrow;
    @Bind(R.id.ll_sort)
    LinearLayout llSort;
    @Bind(R.id.ll_filter)
    LinearLayout llFilter;
    @Bind(R.id.ll_head_layout)
    LinearLayout llHeadLayout;
    @Bind(R.id.ll_content_list_view)
    LinearLayout llContentListView;
    @Bind(R.id.view_mask_bg)
    View viewMaskBg;
    @Bind(R.id.lv_sort)
    ListView lvSort;
    @Bind(R.id.filter_down_layout)
    LinearLayout filterDownLayout;
    @Bind(R.id.rg_sex)
    RadioGroup rgSex;
    @Bind(R.id.rb_sex_all)
    RadioButton rbSexAll;
    @Bind(R.id.rb_boy)
    RadioButton rbBoy;
    @Bind(R.id.rb_girl)
    RadioButton rbGirl;
    @Bind(R.id.rg_distance)
    RadioGroup rgDistance;
    @Bind(R.id.rb_distance_all)
    RadioButton rbDistanceAll;
    @Bind(R.id.rb_5)
    RadioButton rb5;
    @Bind(R.id.rb_10)
    RadioButton rb10;
    @Bind(R.id.btn_sure)
    Button btnSure;


    private Context mContext;
    private Activity mActivity;
    private boolean isStickyTop = false; // 是否吸附在顶部
    private boolean isShowing = false;
    private int filterPosition = -1;
    private int panelHeight;
    private FilterData filterData;

    private FilterEntity selectedSortEntity; // 被选择的排序项

    private FilterOneAdapter sortAdapter;
    private int sex = -1;
    private int distance = -1;
    private int oldSex = -1;
    private int oldDistance = -1;
    private boolean showSort;
    // 筛选视图点击
    private OnFilterClickListener onFilterClickListener;
    // 排序Item点击
    private OnItemSortClickListener onItemSortClickListener;
    // 筛选的确定按钮点击
    private OnFilterSureClickListener onFilterSureClickListener;

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_filter_layout, this);
        ButterKnife.bind(this, view);

        initData();
        initView();
        initListener();
    }

    private void initData() {

    }

    private void initView() {
        viewMaskBg.setVisibility(GONE);
        llContentListView.setVisibility(GONE);
    }

    private void initListener() {
        llSort.setOnClickListener(this);
        llFilter.setOnClickListener(this);
        viewMaskBg.setOnClickListener(this);
        llContentListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_sort:
                if(isShowing && showSort){
                    hide();
                    return;
                }
                filterPosition = 0;
                if (onFilterClickListener != null) {
                    onFilterClickListener.onFilterClick(filterPosition);
                }
                break;
            case R.id.ll_filter:

                if(isShowing && !showSort){
                    hide();
                    return;
                }
                filterPosition = 1;
                if (onFilterClickListener != null) {
                    onFilterClickListener.onFilterClick(filterPosition);
                }
                break;
            case R.id.view_mask_bg:
                hide();
                break;
        }

    }

    // 复位筛选的显示状态
    public void resetFilterStatus() {
        tvSort.setTextColor(mContext.getResources().getColor(R.color.font_black_888));
        ivSortArrow.setImageResource(R.mipmap.down_btn);

        tvFilter.setTextColor(mContext.getResources().getColor(R.color.font_black_888));
        ivFilterArrow.setImageResource(R.mipmap.down_btn);
    }

    // 复位所有的状态
    public void resetAllStatus() {
        resetFilterStatus();
        hide();
    }

    // 显示筛选布局
    public void showFilterLayout(int position) {
        resetFilterStatus();
        switch (position) {
            case 0:
                setSortAdapter();
                break;
            case 1:
                setFilterAdapter();
                break;
        }

//        if (isShowing) return;
        show();
    }

    // 设置排序数据
    private void setSortAdapter() {
        showSort = true;
        tvSort.setTextColor(mActivity.getResources().getColor(R.color.orange));
        ivSortArrow.setImageResource(R.mipmap.up_btn);
        filterDownLayout.setVisibility(GONE);
        lvSort.setVisibility(VISIBLE);
        sortAdapter = new FilterOneAdapter(mContext, filterData.getSorts());
        lvSort.setAdapter(sortAdapter);
        lvSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSortEntity = filterData.getSorts().get(position);
                sortAdapter.setSelectedEntity(selectedSortEntity);
                tvSort.setText(selectedSortEntity.getKey());
                hide();
                if (onItemSortClickListener != null)
                    onItemSortClickListener.onItemSortClick(selectedSortEntity);
            }
        });
    }

    // 设置筛选数据
    private void setFilterAdapter() {
        showSort = false;
        tvFilter.setTextColor(mActivity.getResources().getColor(R.color.orange));
        ivFilterArrow.setImageResource(R.mipmap.up_btn);
        lvSort.setVisibility(GONE);
        filterDownLayout.setVisibility(VISIBLE);
        switch (oldSex){
            case -1:
                rbSexAll.setChecked(true);
                break;
            case 0:
                rbBoy.setChecked(true);
                break;
            case 1:
                rbGirl.setChecked(true);
                break;
        }
        switch (oldDistance){
            case -1:
                rbDistanceAll.setChecked(true);
                break;
            case 5000:
                rb5.setChecked(true);
                break;
            case 10000:
                rb10.setChecked(true);
                break;
        }

        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sex_all:
                        sex = -1;
                        break;
                    case R.id.rb_boy:
                        sex = 0;
                        break;
                    case R.id.rb_girl:
                        sex = 1;
                        break;
                }

            }
        });
        rgDistance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_distance_all:
                        distance = -1;
                        break;
                    case R.id.rb_5:
                        distance = 5000;
                        break;
                    case R.id.rb_10:
                        distance = 10000;
                        break;
                }

            }
        });
        btnSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onFilterSureClickListener != null){
                    oldSex = sex;
                    oldDistance = distance;
                    hide();
                    onFilterSureClickListener.onFilterSureClick(sex, distance);
                }
            }
        });
    }

    // 动画显示
    private void show() {
        isShowing = true;
        viewMaskBg.setVisibility(VISIBLE);
        llContentListView.setVisibility(VISIBLE);
        llContentListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                llContentListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                panelHeight = llContentListView.getHeight();
                // 平移
                // llContentListView,动画操纵的View
                // 动画操纵的View的属性
                // float... values: A set of values that the animation will animate between over time
                ObjectAnimator.ofFloat(llContentListView, "translationY", -panelHeight, 0).setDuration(200).start();
            }
        });
    }

    // 隐藏动画
    public void hide() {
        isShowing = false;
        resetFilterStatus();
        viewMaskBg.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(llContentListView, "translationY", 0, -panelHeight).setDuration(100).start();

    }

    // 是否吸附在顶部
    public void setStickyTop(boolean stickyTop) {
        isStickyTop = stickyTop;
    }

    // 设置筛选数据
    public void setFilterData(Activity activity, FilterData filterData) {
        this.mActivity = activity;
        this.filterData = filterData;
    }

    // 是否显示
    public boolean isShowing() {
        return isShowing;
    }

    public void setOnFilterClickListener(OnFilterClickListener onFilterClickListener) {
        this.onFilterClickListener = onFilterClickListener;
    }

    public void setOnItemSortClickListener(OnItemSortClickListener onItemSortClickListener) {
        this.onItemSortClickListener = onItemSortClickListener;
    }

    public void setOnFilterSureClickListener(OnFilterSureClickListener onFilterSureClickListener) {
        this.onFilterSureClickListener = onFilterSureClickListener;
    }

    public interface OnFilterClickListener {
        void onFilterClick(int position);
    }

    public interface OnItemSortClickListener {
        void onItemSortClick(FilterEntity entity);
    }

    public interface OnFilterSureClickListener {
        void onFilterSureClick(int sex, int distance);
    }

}
