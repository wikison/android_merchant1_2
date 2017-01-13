package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.model.M_UserRole;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * CommonFriendAdapter
 *
 * @author djy
 * @time 2016/8/2 13:05
 */
public class CommonFriendAdapter extends BaseListAdapter<M_Friend> implements SectionIndexer {

    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;

    public CommonFriendAdapter(Context context, List<M_Friend> list) {
        super(context, list);
    }

    public void setData(List<M_Friend> list) {
        clearAll();
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_common_friend, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

        M_Friend friend = getData().get(position);
        String header = friend.getHeader();
        if (position == 0 || header != null
                && !header.equals(getItem(position - 1).getHeader())) {
            if ("".equals(header)) {
                holder.tvHeader.setVisibility(View.GONE);
                holder.headline.setVisibility(View.VISIBLE);
            } else {
                holder.headline.setVisibility(View.VISIBLE);
                holder.tvHeader.setVisibility(View.VISIBLE);
                holder.tvHeader.setText(header);
            }
        } else {
            holder.tvHeader.setVisibility(View.GONE);
        }

        // 用户头像
        if (!TextUtils.isEmpty(friend.getUserHead()))
            mImageManager.loadCircleImage(friend.getUserHead(), holder.ivHead);
        else
            holder.ivHead.setImageResource(R.mipmap.user_icon);
        // 用户名
        if (!TextUtils.isEmpty(friend.getUserName()))
            holder.tvUserName.setText(friend.getUserName());
        // 用户的等级
        holder.tvUserLevel.setText(friend.userLevel + "");
        // 是否显示多重角色
        if (friend.userIndustryList == null
                || friend.userIndustryList.isEmpty()
                || friend.userIndustryNum == 0)
            holder.llUserIndustry.setVisibility(View.GONE);
        else {
            holder.llUserIndustry.setVisibility(View.VISIBLE);
            String industryName = "";

            for (M_UserRole userRole : friend.userIndustryList) {
                if (!TextUtils.isEmpty(userRole.industryName)
                        && !TextUtils.isEmpty(userRole.industryLevel))
                    industryName += "Lv" + userRole.industryLevel + " " + userRole.industryName + "/";
            }
            if (industryName.lastIndexOf("/") == industryName.length() - 1)
                industryName = industryName.substring(0, industryName.length() - 1);

            holder.tvUserIndustry.setText(industryName);
            holder.tvUserIndustryNum.setText(friend.userIndustryNum + "重角色");
        }

        holder.llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onUserDetailClickListener != null)
                    onUserDetailClickListener.onUserDetailClick(position);
            }
        });

        return convertView;
    }


    @Override
    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        List<String> list = new ArrayList<String>();
        list.add(mContext.getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {

            String letter = getItem(i).getHeader();
            System.err.println("FeiendAdapter getsection getHeader:" + letter
                    + " name:" + getItem(i).getUserName());
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

    static class ViewHolder {
        @Bind(R.id.tv_header)
        TextView tvHeader;
        @Bind(R.id.headline)
        View headline;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_user_name)
        TextView tvUserName;
        @Bind(R.id.tv_user_level)
        TextView tvUserLevel;
        @Bind(R.id.tv_user_industry)
        TextView tvUserIndustry;
        @Bind(R.id.tv_user_industry_num)
        TextView tvUserIndustryNum;
        @Bind(R.id.ll_user_industry)
        LinearLayout llUserIndustry;
        @Bind(R.id.ll_user)
        LinearLayout llUser;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
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
}
