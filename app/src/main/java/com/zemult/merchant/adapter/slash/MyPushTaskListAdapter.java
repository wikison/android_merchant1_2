package com.zemult.merchant.adapter.slash;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.DateTimeUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Wikison on 2016/7/29.
 */
public class MyPushTaskListAdapter extends BaseListAdapter<M_Task> {

    private int pagePosition = 0;

    private OnTaskDetailClickListener onTaskDetailClickListener;

    public MyPushTaskListAdapter(Context context, List<M_Task> mTaskList) {
        super(context, mTaskList);
    }

    public MyPushTaskListAdapter(Context context, List<M_Task> mTaskList, int pagePosition) {
        super(context, mTaskList);
        this.pagePosition = pagePosition;
    }

    public void setData(List<M_Task> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_my_publish_task, null, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_Task entity = getItem(position);

        // 设置数据
        initData(holder, entity);
        // 设置监听器
        initListener(holder, position);
        return convertView;
    }

    private void initData(ViewHolder holder, M_Task entity) {
        switch (entity.state) {
            case 0:
                holder.tvTaskState.setText("进行中");
                holder.tvTaskState.setTextColor(Convert.stringToColor("#FFA726"));
                holder.tvDeadline.setText(DateTimeUtil.deadlineInfo(entity.endtime));
                break;
            case 1:
                holder.tvTaskState.setText("已结束");
                holder.tvTaskState.setTextColor(Convert.stringToColor("#888888"));
                break;
        }

        if(entity.type==3){
            holder.rtvTrade.setVisibility(View.VISIBLE);
        }else {
            holder.rtvTrade.setVisibility(View.GONE);
        }

        holder.tvTitle.setText(entity.title);
        holder.tvRoleName.setText(String.format("%sde探索", entity.industryName));
        holder.tvJoinNumber.setText(String.format("%d人参与", entity.recordNum));
        holder.tvPublishTime.setText(DateTimeUtil.strPubDiffTime(entity.createtime) + "发布");
        holder.tvPublishTime.setText((entity.createtime == null ? "" : DateTimeUtil.strPubDiffTime(entity.createtime) + "发布"));

    }


    private void initListener(ViewHolder holder, int position) {
        holder.rlTaskMain.setOnClickListener(new MyClickListener(position));
    }

    public void setOnTaskDetailClickListener(OnTaskDetailClickListener onTaskDetailClickListener) {
        this.onTaskDetailClickListener = onTaskDetailClickListener;
    }

    public interface OnTaskDetailClickListener {
        void onTaskDetailClick(int position);
    }

    static class ViewHolder {
        @Bind(R.id.rtv_trade)
        TextView rtvTrade;
        @Bind(R.id.tv_task_state)
        TextView tvTaskState;
        @Bind(R.id.tv_join_number)
        TextView tvJoinNumber;
        @Bind(R.id.tv_publish_deadline)
        TextView tvDeadline;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_role_name)
        TextView tvRoleName;
        @Bind(R.id.tv_publish_time)
        TextView tvPublishTime;
        @Bind(R.id.rl_task_main)
        RelativeLayout rlTaskMain;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class MyClickListener implements View.OnClickListener {
        int position;

        public MyClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (onTaskDetailClickListener != null)
                onTaskDetailClickListener.onTaskDetailClick(position);
        }
    }
}

