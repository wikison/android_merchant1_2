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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.FilterLeftAdapter;
import com.zemult.merchant.aip.slash.UserIndustryListRequest;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetrecruitroleList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;
import zema.volley.network.VolleyUtil;

/**
 * 
 * @author djy
 * @time 2016/8/1 17:34
 */
public class UserFilterView extends LinearLayout{


    @Bind(R.id.view_mask_bg)
    View viewMaskBg;
    @Bind(R.id.lv_jiaose)
    MyListView lvJiaose;
    @Bind(R.id.ll_content_list_view)
    RelativeLayout llContentListView;


    private Context mContext;
    private Activity mActivity;
    private boolean isShowing = false;
    private int panelHeight;

    private FilterLeftAdapter mAdapter;
    private UserIndustryListRequest industryListRequest; // 获取用户的经营角色列表
    private List<M_Industry> leftList = new ArrayList<>();
    private M_Industry selectedLeft;
    private int userId;

    public UserFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UserFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.mActivity = (Activity) mContext;
        View view = LayoutInflater.from(context).inflate(R.layout.view_user_filter_layout, this);
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
        // 如果没有选中项，默认选中第一项 全部角色
        if (selectedLeft == null) {
            selectedLeft = leftList.get(0);
        }
    }

    private void initView() {
        viewMaskBg.setVisibility(GONE);
        llContentListView.setVisibility(GONE);

        mAdapter = new FilterLeftAdapter(mContext, leftList);
        lvJiaose.setAdapter(mAdapter);
        mAdapter.setSelectedEntity(selectedLeft);
    }

    private void initListener() {
        viewMaskBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        llContentListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        lvJiaose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mAdapter.getItem(position).industryId != 0){
                    selectedLeft = mAdapter.getItem(position);
                    mAdapter.setSelectedEntity(selectedLeft);
                    hide();
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(selectedLeft.industryId);
                    }
                }
            }
        });
    }

    public void setUserId(int userId){
        this.userId = userId;
        // 获取用户的经营角色列表
        getIndustryList();
    }

    /**
     * 获取用户的经营角色列表
     */
    private void getIndustryList() {
        if (industryListRequest != null) {
            industryListRequest.cancel();
        }
        UserIndustryListRequest.Input input = new UserIndustryListRequest.Input();
        input.userId = userId;
        input.page = 1;
        input.rows = 1000;
        input.convertJosn();
        industryListRequest = new UserIndustryListRequest(input, new ResponseListener() {
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
        sendJsonRequest(industryListRequest);
    }

    // 设置该用户的角色数据
    private void setLeftData(List<M_Industry> list) {
        if (list != null
                && !list.isEmpty()) {
            mAdapter.setData(list);
        }
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


    // 动画显示
    public void show() {
        if(!isShowing()){
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
        }else
            hide();
    }
    // 隐藏动画
    public void hide() {
        if (isShowing()) {
            isShowing = false;
            viewMaskBg.setVisibility(View.GONE);
            ObjectAnimator.ofFloat(llContentListView, "translationY", 0, -panelHeight).setDuration(100).start();
        }
    }

    // 是否显示
    public boolean isShowing() {
        return isShowing;
    }

    /**
     * 角色筛选视图点击
     */
    public interface OnItemClickListener {
        void onItemClick(int industryId);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
