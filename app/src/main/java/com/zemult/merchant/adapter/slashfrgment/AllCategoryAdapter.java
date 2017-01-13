package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.view.LineGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 全部分类页面的adapter
 */
public class AllCategoryAdapter extends BaseListAdapter<M_Industry> {

    public AllCategoryAdapter(Context context, List<M_Industry> list) {
        super(context, list);
    }
    public void setData(List<M_Industry> list){
        clearAll();
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_category, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(position + 1 == getCount()){
            holder.viewEmpty.setVisibility(View.VISIBLE);
        }else{
            holder.viewEmpty.setVisibility(View.GONE);
        }

        M_Industry type = getData().get(position);

        if(!TextUtils.isEmpty(type.name))
            holder.tvTitle.setText(type.name);
        if(!TextUtils.isEmpty(type.icon))
            mImageManager.loadCircleImage(type.icon, holder.iv);

        if (type.industryList != null
                && !type.industryList.isEmpty()) {
            holder.gv.setVisibility(View.VISIBLE);
            holder.gv.setAdapter(new GvAdapter(type, position));
        }else
            holder.gv.setVisibility(View.GONE);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv)
        ImageView iv;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.gv)
        LineGridView gv;
        @Bind(R.id.view_empty)
        View viewEmpty;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * gridView的adapter 最多显示三行
     */
    class GvAdapter extends BaseAdapter {
        private M_Industry type;
        private List<M_Industry> showData;
        private List<M_Industry> allData;
        private int parPosition;

        private GvAdapter(M_Industry type, int parPosition) {
            this.type = type;
            this.parPosition = parPosition;
            allData = type.industryList;
            showData = new ArrayList<>();

            // 每行4个，最多三行，超过12个取11个，第12个显示三角
            if (allData.size() > 12
                    && !type.isShowAll()) {
                for (int i = 0; i < 11; i++) {
                    showData.add(allData.get(i));
                }

                M_Industry arrow = new M_Industry();
                arrow.name = "showArrow";
                arrow.id = -1;
                showData.add(arrow);
            } else {
                showData.addAll(allData);
                // 补空数据
                addEmptyData();
            }
        }

        private void addEmptyData() {
            if (allData.size() % 4 != 0) {
                int num = (allData.size() / 4 + 1) * 4 - allData.size();
                M_Industry empty = new M_Industry();
                empty.name = "";
                empty.id = -1;
                switch (num) {
                    case 1:
                        showData.add(empty);
                        break;
                    case 2:
                        showData.add(empty);
                        showData.add(empty);
                        break;
                    case 3:
                        showData.add(empty);
                        showData.add(empty);
                        showData.add(empty);
                        break;
                }
            }
        }


        @Override
        public int getCount() {
            return showData.size();
        }

        @Override
        public Object getItem(int position) {
            return showData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_one_category, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(R.string.app_name, holder);
            } else {
                holder = (ViewHolder) convertView.getTag(R.string.app_name);
            }


            if ("showArrow".equals(showData.get(position).name)) {
                holder.tvName.setVisibility(View.GONE);
                holder.llMore.setVisibility(View.VISIBLE);
            } else {
                holder.tvName.setVisibility(View.VISIBLE);
                holder.llMore.setVisibility(View.GONE);

                if(!TextUtils.isEmpty(showData.get(position).name)){
                    holder.tvName.setText(showData.get(position).name);

                    if(showData.get(position).name.length() < 6){
                        holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    }else if(showData.get(position).name.length() == 6){
                        holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    }else{
                        holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                    }
                }

            }

            holder.llMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showData.clear();
                    showData.addAll(allData);
                    type.setShowAll(true);
                    // 补空数据
                    addEmptyData();
                    notifyDataSetChanged();
                    if (onArrowClickListner != null) {
                        onArrowClickListner.onArrowClick(parPosition);
                    }

                }
            });
            holder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListner != null){
                        if(showData.get(position).id > 0){
                            onItemClickListner.onItemClick(showData.get(position));
                        }
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tv_name)
            TextView tvName;
            @Bind(R.id.ll_more)
            LinearLayout llMore;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    private OnArrowClickListner onArrowClickListner;

    public void setOnArrowClickListner(OnArrowClickListner onArrowClickListner) {
        this.onArrowClickListner = onArrowClickListner;
    }

    public interface OnArrowClickListner {
        void onArrowClick(int parPosition);
    }

    private OnItemClickListner onItemClickListner;

    public void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }

    public interface OnItemClickListner {
        void onItemClick(M_Industry industry);
    }

}
