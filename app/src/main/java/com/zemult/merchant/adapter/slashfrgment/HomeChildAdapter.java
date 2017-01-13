package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.util.DateTimeUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 热门，推荐，我的, 活动
 *
 * @author djy
 * @time 2016/8/31 16:36
 */
public class HomeChildAdapter extends BaseListAdapter<M_Task> {

    private boolean isNoData;
    private int mHeight, tabpos;

    public HomeChildAdapter(Context context, List<M_Task> list) {
        super(context, list);
    }

    public void setTabpos(int tabpos){
        this.tabpos = tabpos;
    }

    // 设置数据 任务
    public void setData(List<M_Task> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);

        isNoData = false;
        if (list.size() == 1 && list.get(0).isNoData()) {
            // 暂无数据布局
            isNoData = list.get(0).isNoData();
            mHeight = list.get(0).getHeight();
        }
        notifyDataSetChanged();
    }

    // 刷新单条记录
    public void refreshOneRecord(M_Task task, int pos) {
        task.taskIndustryRecordId = getData().get(pos).taskIndustryRecordId;
        getData().set(pos, task);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 暂无数据
        if (isNoData) {
            convertView = mInflater.inflate(R.layout.item_no_data_layout, null);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight);
            RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_no_data);
            rootView.setLayoutParams(params);
            return convertView;
        }

        M_Task entity = getItem(position);

        final ViewHolder holder;
        if (convertView != null && convertView instanceof LinearLayout) {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        } else {
            convertView = mInflater.inflate(R.layout.item_home_left_three, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        }

        if (position == 0)
            holder.llTop.setVisibility(View.GONE);
        else
            holder.llTop.setVisibility(View.VISIBLE);
        initData(holder, entity);
        initListner(holder, position);

        return convertView;
    }

    /**
     * 设置数据
     *
     * @param holder
     * @param entity
     */
    private void initData(ViewHolder holder, M_Task entity) {
        if(entity.pushType == 1 || tabpos == 3){
            // 用户头像
            if (!TextUtils.isEmpty(entity.merchantHead))
                mImageManager.loadCircleImage(entity.merchantHead, holder.ivHead);
            else
                holder.ivHead.setImageResource(R.mipmap.user_icon);
            // 用户名
            if (!TextUtils.isEmpty(entity.merchantName))
                holder.tvUserName.setText(entity.merchantName);
        }else {
            // 用户头像
            if (!TextUtils.isEmpty(entity.userHead))
                mImageManager.loadCircleImage(entity.userHead, holder.ivHead);
            else
                holder.ivHead.setImageResource(R.mipmap.user_icon);
            // 用户名
            if (!TextUtils.isEmpty(entity.userName))
                holder.tvUserName.setText(entity.userName);
        }
        // 角色
        if (!TextUtils.isEmpty(entity.industryName)){
            holder.tvRole.setText(entity.industryName);
            new MyAsyncTask(holder, entity.userName).execute();
        }

        // 左边标题
        if (!TextUtils.isEmpty(entity.endTime))
            holder.tvNum.setText(DateTimeUtil.strPubEndDiffTime(entity.endTime) + "结束   "+ entity.recordNum + "人探索");

        // 内容
        if (!TextUtils.isEmpty(entity.title)) {
            CharSequence text = "";
            if (entity.recordNum >= 50) {
                text = entity.title + "   ★";
                if (entity.cashType == 1)
                    text = text + " ★";
            } else if (entity.cashType == 1)
                text = entity.title + "   ★";
            else {
                holder.tvTitle.setText(entity.title);
                return;
            }
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            String rexgString = "★";
            Pattern pattern = Pattern.compile(rexgString);
            Matcher matcher = pattern.matcher(text);

            int num = 1;
            while (matcher.find()) {
                if (num == 2)
                    builder.setSpan(
                            new ImageSpan(mContext, R.mipmap.jiang_icon, ImageSpan.ALIGN_BASELINE), matcher.start(), matcher
                                    .end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                else if (entity.recordNum >= 50)
                    builder.setSpan(
                            new ImageSpan(mContext, R.mipmap.re_icon, ImageSpan.ALIGN_BASELINE), matcher.start(), matcher
                                    .end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                else
                    builder.setSpan(
                            new ImageSpan(mContext, R.mipmap.jiang_icon, ImageSpan.ALIGN_BASELINE), matcher.start(), matcher
                                    .end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                num++;
            }
            holder.tvTitle.setText(builder);
        }
    }

    /**
     * 设置监听器
     *
     * @param holder
     * @param position
     */
    private void initListner(ViewHolder holder, final int position) {
        holder.llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUserDetailClickListener != null)
                    onUserDetailClickListener.onUserDetailClick(position);
            }
        });
        holder.llRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlanDetailClickListener != null)
                    onPlanDetailClickListener.onPlanDetailClick(position);
            }
        });
        holder.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSearchClickListener != null)
                    onSearchClickListener.onSearchClick(position);
            }
        });
    }

    /**
     * 用户详情点击接口
     */
    private OnUserDetailClickListener onUserDetailClickListener;

    public void setOnUserDetailClickListener(OnUserDetailClickListener onUserDetailClickListener) {
        this.onUserDetailClickListener = onUserDetailClickListener;
    }

    public interface OnUserDetailClickListener {
        void onUserDetailClick(int position);
    }

    /**
     * 方案详情点击接口
     */
    private OnPlanDetailClickListener onPlanDetailClickListener;

    public void setOnPlanDetailClickListener(OnPlanDetailClickListener onPlanDetailClickListener) {
        this.onPlanDetailClickListener = onPlanDetailClickListener;
    }

    public interface OnPlanDetailClickListener {
        void onPlanDetailClick(int position);
    }

    /**
     * 去探索点击接口
     */
    private OnSearchClickListener onSearchClickListener;

    public void setOnSearchClickListener(OnSearchClickListener onSearchClickListener) {
        this.onSearchClickListener = onSearchClickListener;
    }

    public interface OnSearchClickListener {
        void onSearchClick(int position);
    }


    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        private ViewHolder holder;
        private String userName;
        public MyAsyncTask(ViewHolder holder, String userName){
            this.holder = holder;
            this.userName = userName;
        }
        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
            if (holder.tvRole.getLineCount() > 1) {
                if(userName!= null && userName.length() > 5)
                holder.tvUserName.setText(userName.substring(0, 6) + "...");
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }


    static class ViewHolder {
        @Bind(R.id.ll_top)
        LinearLayout llTop;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_user_name)
        TextView tvUserName;
        @Bind(R.id.tv_role)
        TextView tvRole;
        @Bind(R.id.ll_user)
        LinearLayout llUser;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_search)
        TextView tvSearch;
        @Bind(R.id.tv_num)
        TextView tvNum;
        @Bind(R.id.ll_root_view)
        LinearLayout llRootView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
