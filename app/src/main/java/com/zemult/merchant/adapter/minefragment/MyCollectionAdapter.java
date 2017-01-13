package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.util.DateTimeUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的收藏
 *
 * @author djy
 * @time 2016/8/9 9:12
 */
public class MyCollectionAdapter extends BaseListAdapter<M_News> {

    public MyCollectionAdapter(Context context, List<M_News> list) {
        super(context, list);
    }

    public void setData(List<M_News> list, boolean isLoadMore) {
        if (!isLoadMore)
            clearAll();

        addALL(list);
        notifyDataSetChanged();
    }


    // 删除单条记录
    public void delOneRecord(int pos){
        getData().remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_collection, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_News entity = getItem(position);

        // 发布用户名字
        if (!TextUtils.isEmpty(entity.userName))
            holder.tvName.setText(entity.userName);
        // 发布用户头像
        if (!TextUtils.isEmpty(entity.userHead))
            mImageManager.loadCircleImage(entity.userHead, holder.ivHead);
        else
            holder.ivHead.setImageResource(R.mipmap.user_icon);
        // 主题名(心情小记的内容/ 任务记录的标题)
        if (!TextUtils.isEmpty(entity.title))
            holder.tvTitle.setText(entity.title);
        // 收藏时间(格式为:yyyy-MM-dd HH:mm:ss)
        if (!TextUtils.isEmpty(entity.createtime))
            holder.tvTime.setText(DateTimeUtil.strPubDiffTime(entity.createtime));
        // 图标
        switch (entity.type){
            case 0:
                holder.ivTitle.setImageResource(R.mipmap.jilv_icon);
                break;
            case 1:
                holder.ivTitle.setImageResource(R.mipmap.renwu3_icon);
                break;
        }

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.iv_title)
        ImageView ivTitle;
        @Bind(R.id.tv_title)
        TextView tvTitle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
