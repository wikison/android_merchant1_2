package com.zemult.merchant.adapter.createroleadapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.role.RoleHomeActivity;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.aip.slash.ManagerAddmanagerRequest;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.util.SlashHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/4/15.
 */
//各个页面的fragment的适配器
public class RolelistAdapter extends BaseListAdapter<M_UserRole> {

    ManagerAddmanagerRequest managerAddmanagerRequest;
    private List<M_UserRole> mDatas = new ArrayList<M_UserRole>();
    public static final String Call_SLASHMENUWINDOW_REFRESH = "call_SlashMenuWindow_refresh";


    public RolelistAdapter(Context context, List<M_UserRole> list) {
        super(context, list);
    }


    public void setData(List<M_UserRole> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }






    @Override
    public View getView(final int position, View contenter, ViewGroup parent) {
        ViewHolder holder = null;
        if (contenter == null) {
            contenter = mInflater.inflate(R.layout.createrole_item, null, false);
            holder = new ViewHolder(contenter);

            contenter.setTag(holder);
        } else {
            holder = (ViewHolder) contenter.getTag();
        }
        mDatas = getData();

        if (!TextUtils.isEmpty(mDatas.get(position).icon)) {
            //加载带外边框的
            mImageManager.loadCircleHasBorderImage(mDatas.get(position).icon, holder.rolcreIvPic, mContext.getResources().getColor(R.color.gainsboro), 1);
        }

        holder.rolcreNameTv.setText(mDatas.get(position).name);
        holder.rolcreDescribeTv.setText(mDatas.get(position).tag);
        holder.rolcreNumberTv.setText(mDatas.get(position).num+"人参与    "+"杠友"+mDatas.get(position).friendNum+"人");

        if(mDatas.get(position).isManager==1){

            holder.btnGo.setBackgroundResource(R.drawable.rolesee_btn);
            holder.btnGo.setText("查看");
            holder.btnGo.setTextColor(mContext.getResources().getColor(R.color.seego_color));
            holder.btnGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, RoleHomeActivity.class);
                    intent.putExtra(RoleHomeActivity.INTENT_INDUSTRY_ID,mDatas.get(position).id);
                    mContext.startActivity(intent);
                }
            });


        }else{
            holder.btnGo.setBackgroundResource(R.drawable.roleapply_btn);
            holder.btnGo.setTextColor(mContext.getResources().getColor(R.color.bg_head));
            holder.btnGo.setText("申请");
            holder.btnGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager_addmanager(mDatas.get(position).id,position);
                }
            });

        }

        return contenter;
    }

    //用户 成为某经营人角色(参与角色)
    private void manager_addmanager(int industryId,final int position) {
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
                    ToastUtils.show(mContext,"参与成功");
                    mDatas.get(position).isManager = 1;
                    notifyDataSetChanged();  //改变按钮样式

                    Intent intent = new Intent(mContext, RoleHomeActivity.class);
                    intent.putExtra(RoleHomeActivity.INTENT_INDUSTRY_ID, mDatas.get(position).id);
                    mContext.startActivity(intent);

                    EventBus.getDefault().post(Call_SLASHMENUWINDOW_REFRESH);


                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(managerAddmanagerRequest);
    }






    static class ViewHolder {
        @Bind(R.id.rolcre_iv_pic)
        ImageView rolcreIvPic;
        @Bind(R.id.rolcre_name_tv)
        TextView rolcreNameTv;
        @Bind(R.id.rolcre_describe_tv)
        TextView rolcreDescribeTv;
        @Bind(R.id.rolcre_number_tv)
        TextView rolcreNumberTv;
        @Bind(R.id.btn_go)
        Button btnGo;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }


}

