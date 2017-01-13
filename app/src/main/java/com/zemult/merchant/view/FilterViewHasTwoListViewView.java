package com.zemult.merchant.view;

import android.animation.ObjectAnimator;
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

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.FilterLeftAdapter;
import com.zemult.merchant.adapter.slashfrgment.FilterRightAdapter;
import com.zemult.merchant.aip.slash.UserIndustryListRequest;
import com.zemult.merchant.aip.slash.UserIndustryMerchantListRequest;
import com.zemult.merchant.aip.slash.UserMerchantListRequest;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetrecruitroleList;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;
import zema.volley.network.VolleyUtil;

/**
 * 查看全部记录时 顶部的条件栏
 */
public class FilterViewHasTwoListViewView extends LinearLayout implements View.OnClickListener {


    @Bind(R.id.tv_left)
    TextView tvLeft;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.iv_arrow)
    ImageView ivArrow;
    @Bind(R.id.ll_head_layout)
    LinearLayout llHeadLayout;
    @Bind(R.id.view_divider)
    View viewDivider;
    @Bind(R.id.view_mask_bg)
    View viewMaskBg;
    @Bind(R.id.lv_left)
    ListView lvLeft;
    @Bind(R.id.lv_right)
    ListView lvRight;
    @Bind(R.id.ll_content_list_view)
    LinearLayout llContentListView;


    private Context mContext;
    private boolean isShowing = false;
    private int panelHeight;

    private FilterLeftAdapter leftAdapter;
    private FilterRightAdapter rightAdapter;

    private UserIndustryListRequest leftRequest; // 获取用户的经营角色列表
    private UserIndustryMerchantListRequest rightRequest; // 获取用户的单角色下的场景列表
    private UserMerchantListRequest userMerchantListRequest; // 获取用户的所有场景列表
    private List<M_Industry> leftList = new ArrayList<>();
    private M_Industry selectedLeft;

    private List<M_Merchant> rightList = new ArrayList<>();
    private List<M_Merchant> rightAllList = new ArrayList<>();
    private M_Merchant selectedRight;
    private int userId;

    // 用户存储单角色下的场景列表
    private List<Memory> memoryList = new ArrayList<>();


    public FilterViewHasTwoListViewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterViewHasTwoListViewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_filter_has_two_listview_layout, this);
        ButterKnife.bind(this, view);

        initData();
        initView();
        initListener();
    }

    private void initData() {
        M_Industry all_jiaose = new M_Industry();
        all_jiaose.industryId = -1;
        all_jiaose.name = "全部角色";
        leftList.add(all_jiaose);

        M_Merchant all_changjing = new M_Merchant();
        all_changjing.merchantId = -1;
        all_changjing.name = "全部场景";
        rightList.add(all_changjing);

        // 如果没有选中项，默认选中第一项 全部角色
        if (selectedLeft == null) {
            selectedLeft = leftList.get(0);
        }
        // 如果没有选中项，默认选中第一项 全部场景
        if (selectedRight == null) {
            selectedRight = rightList.get(0);
        }
    }

    private void initView() {
        viewMaskBg.setVisibility(GONE);
        llContentListView.setVisibility(GONE);

        // 左边列表视图
        leftAdapter = new FilterLeftAdapter(mContext, leftList);
        lvLeft.setAdapter(leftAdapter);
        leftAdapter.setSelectedEntity(selectedLeft);

        // 右边列表视图
        rightAdapter = new FilterRightAdapter(mContext, rightList);
        lvRight.setAdapter(rightAdapter);
        rightAdapter.setSelectedEntity(selectedRight);
    }

    private void initListener() {
        llHeadLayout.setOnClickListener(this);
        viewMaskBg.setOnClickListener(this);
        llContentListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        lvLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedLeft = leftAdapter.getItem(position);
                leftAdapter.setSelectedEntity(selectedLeft);
                tvLeft.setText(selectedLeft.name);

                rightAdapter.setData(new ArrayList<M_Merchant>());
                // 左边选择全部角色时，右边显示全部场景
                if (position == 0) {
                    if (rightAllList == null || rightAllList.isEmpty())
                        getRightAllList();
                    else
                        rightAdapter.setData(rightAllList);
                } else {
                    // 获取该角色下的场景数据
                    for(Memory memory : memoryList){
                        if(memory.industryId == selectedLeft.industryId)
                            rightAdapter.setData(memory.merchantList);
                    }
                    getRightList();
                }
            }
        });

        lvRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedRight = rightAdapter.getItem(position);
                rightAdapter.setSelectedEntity(selectedRight);
                hide();
                if (onItemCategoryClickListener != null) {
                    onItemCategoryClickListener.onItemCategoryClick(selectedLeft.industryId, selectedRight.merchantId);
                }
                tvRight.setText(selectedRight.name);
            }
        });
    }

    public void setFilterData(int userId){
        this.userId = userId;
        // 获取用户的经营角色列表
        getLeftList();
        // 获取用户的全部场景列表
        getRightAllList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_head_layout:
                if (isShowing) {
                    hide();
                    return;
                }
                if (onFilterClickListener != null) {
                    onFilterClickListener.onFilterClick();
                }
                break;
            case R.id.view_mask_bg:
                hide();
                break;
        }

    }

    // 复位筛选的显示状态
    public void resetFilterStatus() {
        ivArrow.setImageResource(R.mipmap.down_btn);
    }

    // 复位所有的状态
    public void resetAllStatus() {
        resetFilterStatus();
        hide();
    }

    // 显示筛选布局
    public void showFilterLayout() {
        resetFilterStatus();
        ivArrow.setImageResource(R.mipmap.up_btn);
        lvLeft.setVisibility(VISIBLE);
        lvRight.setVisibility(VISIBLE);

        if (isShowing) return;
        show();
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
                ObjectAnimator.ofFloat(llContentListView, "translationY", -panelHeight, 0).setDuration(200).start();
            }
        });
    }

    // 隐藏动画
    public void hide() {
        isShowing = false;
        resetFilterStatus();
        viewMaskBg.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(llContentListView, "translationY", 0, -panelHeight).setDuration(200).start();
    }

    // 是否显示
    public boolean isShowing() {
        return isShowing;
    }

    private OnFilterClickListener onFilterClickListener;

    public void setOnFilterClickListener(OnFilterClickListener onFilterClickListener) {
        this.onFilterClickListener = onFilterClickListener;
    }

    public interface OnFilterClickListener {
        void onFilterClick();
    }

    private OnItemCategoryClickListener onItemCategoryClickListener;

    public void setOnItemCategoryClickListener(OnItemCategoryClickListener onItemCategoryClickListener) {
        this.onItemCategoryClickListener = onItemCategoryClickListener;
    }

    public interface OnItemCategoryClickListener {
        void onItemCategoryClick(int industryId, int merchantId);
    }


    /**
     * 获取用户的经营角色列表
     */
    private void getLeftList() {
        if (leftRequest != null) {
            leftRequest.cancel();
        }
        UserIndustryListRequest.Input input = new UserIndustryListRequest.Input();
        input.userId = userId;
        input.page = 1;
        input.rows = 1000;
        input.convertJosn();
        leftRequest = new UserIndustryListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetrecruitroleList) response).status == 1) {
                    setLeftData(((APIM_MerchantGetrecruitroleList) response).industryList
                    );
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantGetrecruitroleList) response).info);
                }
            }
        });
        sendJsonRequest(leftRequest);
    }

    // 设置该用户的角色数据
    private void setLeftData(List<M_Industry> list) {
        if (list != null
                && !list.isEmpty()) {
            leftAdapter.setData(list);
        }
    }

    /**
     * 获取用户的单角色下的场景列表
     */
    private void getRightList() {
        if (rightRequest != null) {
            rightRequest.cancel();
        }
        UserIndustryMerchantListRequest.Input input = new UserIndustryMerchantListRequest.Input();
        input.userId = userId;
        input.industryId = selectedLeft.industryId;
        input.center = Constants.CENTER;
        input.page = 1;
        input.rows = 1000;
        input.convertJosn();
        rightRequest = new UserIndustryMerchantListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    setRightData(((APIM_MerchantList) response).merchantList);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
            }
        });
        sendJsonRequest(rightRequest);
    }

    // 设置场景列表
    private void setRightData(List<M_Merchant> list) {
        if (list != null
                && !list.isEmpty()) {
            rightAdapter.setData(list);

            // 存储单角色下的场景列表
            boolean save = true;
            for(Memory memory : memoryList){
                if(memory.industryId == selectedLeft.industryId)
                    save = false;
            }
            if(save = true){
                Memory one = new Memory();
                one.setIndustryId(selectedLeft.industryId);
                one.setMerchantList(list);
                memoryList.add(one);
            }
        }
    }

    /**
     * 获取用户的全部场景列表
     *
     */
    private void getRightAllList() {
        if (userMerchantListRequest != null) {
            userMerchantListRequest.cancel();
        }
        UserMerchantListRequest.Input input = new UserMerchantListRequest.Input();
        input.userId = userId;
        input.center = Constants.CENTER;
        input.page = 1;
        input.rows = 1000;
        input.convertJosn();
        userMerchantListRequest = new UserMerchantListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    rightAllList.clear();
                    rightAllList.addAll(((APIM_MerchantList) response).merchantList);
                    setRightData(((APIM_MerchantList) response).merchantList);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
            }
        });
        sendJsonRequest(userMerchantListRequest);
    }

    protected ArrayList<WeakReference<Request>> listJsonRequest;

    /**
     * 发送请求
     *
     * @param request
     */
    public void sendJsonRequest(Request request) {

        if (listJsonRequest == null) {
            listJsonRequest = new ArrayList<WeakReference<Request>>();
        }
        WeakReference<Request> ref = new WeakReference<Request>(request);
        listJsonRequest.add(ref);
        VolleyUtil.getRequestQueue().add(request);
    }

    // 用户存储单角色下的场景列表
    class Memory{
        private int industryId;
        private List<M_Merchant> merchantList;

        public int getIndustryId() {
            return industryId;
        }

        public void setIndustryId(int industryId) {
            this.industryId = industryId;
        }

        public List<M_Merchant> getMerchantList() {
            return merchantList;
        }

        public void setMerchantList(List<M_Merchant> merchantList) {
            this.merchantList = merchantList;
        }
    }

}
