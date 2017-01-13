package com.zemult.merchant.adapter.discoverfragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Ad;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/6/14.
 */
public class DiscoverAdapter extends BaseListAdapter<M_Ad> {


    public DiscoverAdapter(Context context, List<M_Ad> list) {
        super(context, list);
    }

    public void setData(List<M_Ad> list){
        clearAll();
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_discover, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        M_Ad ad=getItem(position);
        mImageManager.loadUrlImage(ad.img, holder.iv);


        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv)
        ImageView iv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
