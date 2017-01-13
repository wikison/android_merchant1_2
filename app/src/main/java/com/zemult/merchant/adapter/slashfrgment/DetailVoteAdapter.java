package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Vote;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 详情页里面投票适配器
 *
 * @author djy
 * @time 2016/7/28 17:29
 */
public class DetailVoteAdapter extends BaseListAdapter<M_Vote> {
    private float itemWidth;

    public DetailVoteAdapter(Context context, List<M_Vote> list, int allNum, int rootWidth) {
        super(context, list);
        if(allNum != 0){
            itemWidth = (float)rootWidth/allNum;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_detail_vote, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_Vote entity = getItem(position);

        // 选项内容
        if (!TextUtils.isEmpty(entity.voteNote)) {
            holder.tvContent.setText(entity.voteNote);
        }
        // 选项票数
        holder.tvNum.setText(entity.voteNum + "人");

        if (itemWidth != 0) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.viewFraction.getLayoutParams();
            lp.width = (int)(itemWidth * entity.voteNum);
        }
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.view_fraction)
        View viewFraction;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.tv_num)
        TextView tvNum;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
