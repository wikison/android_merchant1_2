package com.zemult.merchant.adapter.role;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.role.CoastBillActivity;
import com.zemult.merchant.activity.role.PartyHomeActivity;
import com.zemult.merchant.activity.role.RoleTaskActivity;
import com.zemult.merchant.app.base.TBaseAdapter;
import com.zemult.merchant.bean.ExtraServerModel;

import java.util.List;

import cn.trinea.android.common.util.StringUtils;


public class ExtraServerAdapter extends TBaseAdapter<ExtraServerModel>
        implements OnItemClickListener {


    private int industryId;
    public ExtraServerAdapter(Context mContext, List<ExtraServerModel> beans) {
        super(mContext, beans);
    }

    public void setIndustryId(int industryId){
        this.industryId = industryId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {

        ViewHolder viewHolder;
        ExtraServerModel bean = getItem(position);
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.item_rolehome, null);
            viewHolder = new ViewHolder();
            viewHolder.iconImageView = (ImageView) convertView
                    .findViewById(R.id.icon);
            viewHolder.nameTextView = (TextView) convertView
                    .findViewById(R.id.name);
            viewHolder.tipTextView = (TextView) convertView
                    .findViewById(R.id.tip);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (bean.getIcon() < 0) {
            viewHolder.iconImageView.setImageBitmap(null);
        } else {
            viewHolder.iconImageView.setImageResource(bean.getIcon());
        }

        if (!StringUtils.isEmpty(bean.getTip())) {
            viewHolder.tipTextView.setText(bean.getTip());
            viewHolder.tipTextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tipTextView.setVisibility(View.GONE);
        }
        viewHolder.nameTextView.setText(bean.getName());
        viewHolder.nameTextView.setTextColor(bean.getNameColor());
        return convertView;
    }

    class ViewHolder {
        private ImageView iconImageView;
        private TextView nameTextView;
        private TextView tipTextView;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position,
                            long arg3) {
        ExtraServerModel model = items.get(position);
        if (model.getIcon() < 0) {
            return;
        }
        if (model.getName().equals("消费报表")) {
            Intent intent = new Intent(mContext, CoastBillActivity.class);
            intent.putExtra("type", model.getName());
            mContext.startActivity(intent);
        }
        if (model.getName().equals("聚会活动")) {
            Intent intent = new Intent(mContext, PartyHomeActivity.class);
            mContext.startActivity(intent);
        }
        if (model.getName().equals("探索任务")) {
            Intent intent = new Intent(mContext, RoleTaskActivity.class);
            intent.putExtra(RoleTaskActivity.INTENT_INDUSTRYID, industryId);
            mContext.startActivity(intent);
        }


    }

}
