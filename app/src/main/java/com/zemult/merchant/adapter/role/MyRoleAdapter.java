package com.zemult.merchant.adapter.role;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.role.RoleHomeActivity;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_UserRole;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/4/15.
 */
//各个页面的fragment的适配器
public class MyRoleAdapter extends BaseListAdapter<M_UserRole> {

    private List<M_UserRole> mDatas = new ArrayList<M_UserRole>();


    public MyRoleAdapter(Context context, List<M_UserRole> list) {
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
            contenter = mInflater.inflate(R.layout.myrole_item, null, false);
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
        holder.rolcreNumberTv.setText(mDatas.get(position).num + "人参与    " + "杠友" + mDatas.get(position).friendNum + "人");
        holder.rolcreNumberTv.setVisibility(View.GONE);


        holder.llMyrole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RoleHomeActivity.class);
                intent.putExtra(RoleHomeActivity.INTENT_INDUSTRY_ID, mDatas.get(position).industryId);
                intent.putExtra(RoleHomeActivity.INTENT_ROLE_ID, mDatas.get(position).industryId);
                intent.putExtra(RoleHomeActivity.INTENT_ROLE_NAME, mDatas.get(position).name);
                intent.putExtra(RoleHomeActivity.INTENT_ROLE_ICON, mDatas.get(position).icon);
                mContext.startActivity(intent);
            }
        });


        return contenter;
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
        @Bind(R.id.ll_myrole)
        LinearLayout llMyrole;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }


}

