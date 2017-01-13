package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Industry;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author djy
 * @time 2016/12/23 9:58
 */
public class HomeIndustryAdapter extends BaseListAdapter<M_Industry> {
    public HomeIndustryAdapter(Context context) {
        super(context);
    }

    public HomeIndustryAdapter(Context context, List<M_Industry> list) {
        super(context, list);
    }

    public void setData(List<M_Industry> list){
        clearAll();
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_industry, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

        M_Industry industry = getData().get(pos);
        // 场景头像 地址
        if (!TextUtils.isEmpty(industry.icon)) {
            mImageManager.loadUrlImage2(industry.icon, holder.iv);
        }
        // 场景名称
        if (!TextUtils.isEmpty(industry.name))
            holder.tv.setText(industry.name);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv)
        ImageView iv;
        @Bind(R.id.tv)
        TextView tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
