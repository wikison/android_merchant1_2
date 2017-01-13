package com.zemult.merchant.adapter.role;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.role.RoleHomeActivity;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.slash.ManagerAddmanagerRequest;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_IndustryClass;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.util.SlashHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;
import zema.volley.network.VolleyUtil;

/**
 * Created by admin on 2016/10/18.
 */

public class RoleClassifyAdapter extends BaseAdapter{
    private static final int TYPE_CATEGORY_ITEM = 0;
    private static final int TYPE_ITEM = 1;
    private List<M_IndustryClass> mListData;
    private List<M_UserRole> realData;
    private LayoutInflater mInflater;
    ManagerAddmanagerRequest managerAddmanagerRequest;
    private Context mcontext;
    protected ArrayList<WeakReference<Request>> listJsonRequest;

    public RoleClassifyAdapter(Context context, List<M_IndustryClass> datas) {
        mcontext =context;
        mListData = datas;
        mInflater = LayoutInflater.from(context);
        initData();
    }

    /**
     * 此方法选择性调用
     *
     * @param mListData
     */
    public void setDatas(List<M_IndustryClass> mListData) {
        this.mListData = mListData;
        // TODO: 2016/10/25 这个方法必须调用，应为数据需要重构
        initData();
        // // TODO: 2016/10/25 选择性添加，不想要就注释掉
        notifyDataSetChanged();
    }

    /**
     * 使用此方法重新构建新数据
     */
    private void initData() {
        realData = new ArrayList<M_UserRole>();
        for (int i = 0; i < mListData.size(); i++) {
            M_UserRole userRole = new M_UserRole();
            userRole.viewType = TYPE_CATEGORY_ITEM;// 大分类
            userRole.name = mListData.get(i).name;// 设置大分类名称
            realData.add(userRole);
            for (int j = 0; j < mListData.get(i).industryList.size(); j++) {
                M_UserRole bean = mListData.get(i).industryList.get(j);
                bean.viewType = TYPE_ITEM;// 内部分类
                realData.add(bean);
            }
        }
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return realData.get(position).viewType;
    }

    @Override
    public int getCount() {
        return realData == null ? 0 : realData.size();
    }

    @Override
    public Object getItem(int position) {
        return realData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        final M_UserRole userRole = (M_UserRole) getItem(position);
        CommonViewHolder holder = null;
        if (itemViewType == TYPE_CATEGORY_ITEM) {   //头部

            holder = CommonViewHolder.get(mcontext, convertView, parent, R.layout.layout_item_daytask_title);
            holder.setText(R.id.daytask_title, userRole.name);

        }
        else if(itemViewType ==TYPE_ITEM){
            holder = CommonViewHolder.get(mcontext, convertView, parent, R.layout.item_recommendrole);

            holder.setCircleImage(R.id.head_iv,userRole.icon);
            holder.setText(R.id.name_tv,userRole.name);
            holder.setText(R.id.note_tv,userRole.tag);
            holder.setText(R.id.others_tv,userRole.num+"人参与"+"     杠友"+userRole.friendNum+"人");
            holder.setroleState(R.id.go_btn, userRole.isManager);
            holder.setOnclickListener(R.id.go_btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (realData.get(position).isManager == 0) {       //未
                        manager_addmanager(realData.get(position).id, position); //申请

                    } else if (realData.get(position).isManager == 1) {         //已经申请的状态
                        Intent intent = new Intent(mcontext, RoleHomeActivity.class);
                        intent.putExtra(RoleHomeActivity.INTENT_INDUSTRY_ID, realData.get(position).id);
                        mcontext.startActivity(intent);
                    }
                }
            });
        }

        return holder.getmConvertView();
    }

    //用户 成为某经营人角色(参与角色)
    private void manager_addmanager(int industryId, final int position) {
        if (managerAddmanagerRequest != null) {
            managerAddmanagerRequest.cancel();
        }
        ManagerAddmanagerRequest.Input input = new ManagerAddmanagerRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.industryId = industryId;
        input.convertJosn();
        managerAddmanagerRequest = new ManagerAddmanagerRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    realData.get(position).isManager = 1;
                    notifyDataSetChanged();  //改变按钮样式
                    Intent intent = new Intent(mcontext, RoleHomeActivity.class);
                    intent.putExtra(RoleHomeActivity.INTENT_INDUSTRY_ID, realData.get(position).id);
                    mcontext.startActivity(intent);

                } else {
                    ToastUtils.show(mcontext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(managerAddmanagerRequest);
    }



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
        VolleyUtil.getRequestQueue().add(request) ;
    }


}
