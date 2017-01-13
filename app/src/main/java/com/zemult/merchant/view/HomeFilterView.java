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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.FilterOneAdapter;
import com.zemult.merchant.model.FilterData;
import com.zemult.merchant.model.FilterEntity;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HomeFilterView extends LinearLayout implements View.OnClickListener {

    @Bind(R.id.tv_sex)
    TextView tvSex;
    @Bind(R.id.iv_sex_arrow)
    ImageView ivSexArrow;
    @Bind(R.id.rll_sex)
    RoundLinearLayout rllSex;
    @Bind(R.id.tv_type)
    TextView tvType;
    @Bind(R.id.iv_type_arrow)
    ImageView ivTypeArrow;
    @Bind(R.id.rll_type)
    RoundLinearLayout rllType;
    @Bind(R.id.tv_friend)
    TextView tvFriend;
    @Bind(R.id.iv_friend_arrow)
    ImageView ivFriendArrow;
    @Bind(R.id.rll_friend)
    RoundLinearLayout rllFriend;
    @Bind(R.id.view_mask_bg)
    View viewMaskBg;
    @Bind(R.id.ll_content_list_view)
    LinearLayout llContentListView;
    @Bind(R.id.ll_task_layout)
    LinearLayout llTaskLayout;
    @Bind(R.id.lv_sex)
    ListView lvSex;
    @Bind(R.id.lv_type)
    ListView lvType;
    @Bind(R.id.lv_friend)
    ListView lvFriend;
    private Context mContext;
    private Activity mActivity;
    private boolean isShowing = false;
    private TaskEnum taskEnum = TaskEnum.SEX;
    private int panelHeight;
    private FilterData filterData;

    private FilterEntity selectedSexEntity;
    private FilterEntity selectedTypeEntity;
    private FilterEntity selectedFriendEntity;

    private FilterOneAdapter sexAdapter;
    private FilterOneAdapter typeAdapter;
    private FilterOneAdapter friendAdapter;

    // 选择任务时的筛选
    public enum TaskEnum {
        SEX, TYPE, FRIEND
    }

    public HomeFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_home_filter_layout, this);
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
        rllSex.setOnClickListener(this);
        rllType.setOnClickListener(this);
        rllFriend.setOnClickListener(this);
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
            case R.id.rll_sex:
                if (isShowing && taskEnum == TaskEnum.SEX) {
                    hide();
                    return;
                }
                taskEnum = TaskEnum.SEX;
                if (onTaskFilterClickListener != null) {
                    onTaskFilterClickListener.onTaskFilterClickListener(taskEnum);
                }
                break;
            case R.id.rll_type:
                if (isShowing && taskEnum == TaskEnum.TYPE) {
                    hide();
                    return;
                }
                taskEnum = TaskEnum.TYPE;
                if (onTaskFilterClickListener != null) {
                    onTaskFilterClickListener.onTaskFilterClickListener(taskEnum);
                }
                break;
            case R.id.rll_friend:
                if (isShowing && taskEnum == TaskEnum.FRIEND) {
                    hide();
                    return;
                }
                taskEnum = TaskEnum.FRIEND;
                if (onTaskFilterClickListener != null) {
                    onTaskFilterClickListener.onTaskFilterClickListener(taskEnum);
                }
                break;
            case R.id.view_mask_bg:
                hide();
                break;
        }
    }

    // 复位筛选的显示状态
    public void resetFilterStatus() {
        rllSex.getDelegate().setBackgroundColor(mContext.getResources().getColor(R.color.bg_f2));
        tvSex.setTextColor(mContext.getResources().getColor(R.color.font_black_333));
        ivSexArrow.setImageResource(R.mipmap.down_btn);

        rllType.getDelegate().setBackgroundColor(mContext.getResources().getColor(R.color.bg_f2));
        tvType.setTextColor(mContext.getResources().getColor(R.color.font_black_333));
        ivTypeArrow.setImageResource(R.mipmap.down_btn);

        rllFriend.getDelegate().setBackgroundColor(mContext.getResources().getColor(R.color.bg_f2));
        tvFriend.setTextColor(mContext.getResources().getColor(R.color.font_black_333));
        ivFriendArrow.setImageResource(R.mipmap.down_btn);
    }

    // 复位所有的状态
    public void resetAllStatus() {
        resetFilterStatus();
        hide();
    }

    // 显示筛选布局
    public void showFilterLayout(TaskEnum taskEnum) {
        resetFilterStatus();
        this.taskEnum = taskEnum;
        setTaskLayout();
        show();
    }

    // 设置排序数据
    private void setTaskLayout() {
        switch (taskEnum) {
            case SEX:
                lvSex.setVisibility(VISIBLE);
                lvType.setVisibility(GONE);
                lvFriend.setVisibility(GONE);
                tvSex.setTextColor(mActivity.getResources().getColor(R.color.white));
                ivSexArrow.setImageResource(R.mipmap.up_btn_white);
                rllSex.getDelegate().setBackgroundColor(mContext.getResources().getColor(R.color.bg_head));

                break;
            case TYPE:
                lvSex.setVisibility(GONE);
                lvType.setVisibility(VISIBLE);
                lvFriend.setVisibility(GONE);
                tvType.setTextColor(mActivity.getResources().getColor(R.color.white));
                ivTypeArrow.setImageResource(R.mipmap.up_btn_white);
                rllType.getDelegate().setBackgroundColor(mContext.getResources().getColor(R.color.bg_head));

                break;
            case FRIEND:
                lvSex.setVisibility(GONE);
                lvType.setVisibility(GONE);
                lvFriend.setVisibility(VISIBLE);
                tvFriend.setTextColor(mActivity.getResources().getColor(R.color.white));
                ivFriendArrow.setImageResource(R.mipmap.up_btn_white);
                rllFriend.getDelegate().setBackgroundColor(mContext.getResources().getColor(R.color.bg_head));

                break;
        }
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
        if (isShowing()) {
            isShowing = false;
            resetFilterStatus();
            viewMaskBg.setVisibility(View.GONE);
            ObjectAnimator.ofFloat(llContentListView, "translationY", 0, -panelHeight).setDuration(100).start();
        }
    }

    // 设置筛选数据
    public void setFilterData(Activity activity, final FilterData filterData) {
        this.mActivity = activity;
        this.filterData = filterData;

        sexAdapter = new FilterOneAdapter(mContext, filterData.getSex());
        lvSex.setAdapter(sexAdapter);

        typeAdapter = new FilterOneAdapter(mContext, filterData.getType());
        lvType.setAdapter(typeAdapter);

        friendAdapter = new FilterOneAdapter(mContext, filterData.getFriend());
        lvFriend.setAdapter(friendAdapter);

        lvFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFriendEntity = filterData.getFriend().get(position);
                friendAdapter.setSelectedEntity(selectedFriendEntity);
                tvFriend.setText(selectedFriendEntity.getKey());
                hide();
                if (onItemFriendClickListener != null)
                    onItemFriendClickListener.onItemFriendClick(selectedFriendEntity);
            }
        });

        lvType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTypeEntity = filterData.getType().get(position);
                typeAdapter.setSelectedEntity(selectedTypeEntity);
                tvType.setText(selectedTypeEntity.getKey());
                hide();
                if (onItemTypeClickListener != null)
                    onItemTypeClickListener.onItemTypeClick(selectedTypeEntity);
            }
        });

        lvSex.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSexEntity = filterData.getSex().get(position);
                sexAdapter.setSelectedEntity(selectedSexEntity);
                tvSex.setText(selectedSexEntity.getKey());
                hide();
                if (onItemSexClickListener != null)
                    onItemSexClickListener.onItemSexClick(selectedSexEntity);
            }
        });
    }

    // 是否显示
    public boolean isShowing() {
        return isShowing;
    }

    /**
     * 任务筛选视图点击
     */
    public interface OnTaskFilterClickListener {
        void onTaskFilterClickListener(TaskEnum taskEnum);
    }

    private OnTaskFilterClickListener onTaskFilterClickListener;

    public void setOnTaskFilterClickListener(OnTaskFilterClickListener onTaskFilterClickListener) {
        this.onTaskFilterClickListener = onTaskFilterClickListener;
    }

    public interface OnItemSexClickListener {
        void onItemSexClick(FilterEntity entity);
    }

    private OnItemSexClickListener onItemSexClickListener;

    public void setOnItemSexClickListener(OnItemSexClickListener onItemSexClickListener) {
        this.onItemSexClickListener = onItemSexClickListener;
    }

    public interface OnItemTypeClickListener {
        void onItemTypeClick(FilterEntity entity);
    }

    private OnItemTypeClickListener onItemTypeClickListener;

    public void setOnItemTypeClickListener(OnItemTypeClickListener onItemTypeClickListener) {
        this.onItemTypeClickListener = onItemTypeClickListener;
    }

    public interface OnItemFriendClickListener {
        void onItemFriendClick(FilterEntity entity);
    }

    private OnItemFriendClickListener onItemFriendClickListener;

    public void setOnItemFriendClickListener(OnItemFriendClickListener onItemFriendClickListener) {
        this.onItemFriendClickListener = onItemFriendClickListener;
    }

}
