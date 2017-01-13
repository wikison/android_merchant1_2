package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.aip.mine.UserAttractAddRequest;
import com.zemult.merchant.aip.mine.UserAttractDelRequest;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.util.SlashHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/7/23.
 */
public class RanklistAdapter extends BaseListAdapter<M_Fan> {

    private UserAttractAddRequest attractAddRequest; // 添加关注
    private UserAttractDelRequest attractDelRequest; // 取消关注
    private List<M_Fan> mDatas = new ArrayList<M_Fan>();

    public RanklistAdapter(Context context, List<M_Fan> list) {
        super(context, list);
    }

    public void setData(List<M_Fan> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Log.e("sunjian","aaa"+position);

         MviewHolder holder = null;
         if (convertView == null) {
             convertView = mInflater.inflate(R.layout.level_item, null, false);
             holder = new MviewHolder(convertView);
             convertView.setTag(holder);
         } else {
             holder = (MviewHolder) convertView.getTag();
         }

         mDatas = getData();
         //     final M_Fan user = getData().get(position);

         holder.rankTv.setText("" + (position + 1));

         holder.mynameTv.setText(mDatas.get(position).name);

         if (!TextUtils.isEmpty(mDatas.get(position).head)) {
             //加载带外边框的
             mImageManager.loadCircleHasBorderImage(mDatas.get(position).head, holder.myheadIv, mContext.getResources().getColor(R.color.gainsboro), 1);
         }
//跳转到用户详情
         holder.myheadIv.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(mContext, UserDetailActivity.class);
                 intent.putExtra(UserDetailActivity.USER_ID, mDatas.get(position).userId);
                 intent.putExtra(UserDetailActivity.USER_NAME, mDatas.get(position).name);
                 intent.putExtra(UserDetailActivity.USER_HEAD, mDatas.get(position).head);
                 mContext.startActivity(intent);
             }
         });


         if (position == 0) {
             holder.paizi_iv.setVisibility(View.VISIBLE);
             holder.paizi_iv.setBackgroundResource(R.mipmap.jing_icon);

         } else if (position == 1) {
             holder.paizi_iv.setVisibility(View.VISIBLE);
             holder.paizi_iv.setBackgroundResource(R.mipmap.ying_icon);

         } else if (position == 2) {
             holder.paizi_iv.setVisibility(View.VISIBLE);
             holder.paizi_iv.setBackgroundResource(R.mipmap.tong_icon);
         } else {
             holder.paizi_iv.setVisibility(View.GONE);
         }


         holder.othersTv.setText("等级" + mDatas.get(position).level + "     " + "经验值" + mDatas.get(position).experience);


         if (SlashHelper.userManager().getUserId() == mDatas.get(position).userId) {
             holder.tvState.setVisibility(View.GONE);

         } else {
             holder.tvState.setVisibility(View.VISIBLE);
         }


         if (mDatas.get(position).isFan == 0) {
             holder.tvState.setText("+关注");
             holder.tvState.setTextColor(0xFFFFA726);
             holder.tvState.setBackgroundResource(R.drawable.focus_btn_orange_ret);

         } else {
             holder.tvState.setText("已关注");
             holder.tvState.setTextColor(0xffcecdcd);
             holder.tvState.setBackgroundResource(R.drawable.focus_btn_grey_ret);
         }


         holder.tvState.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (mDatas.get(position).isFan == 0) {
                     //未关注的状态

                     Log.e("sunjian", "111" + position);
                     addFous(mDatas.get(position).userId, position);//添加关注网络操作


                 } else if (mDatas.get(position).isFan == 1) {         //已关注的状态

                     cancleFocus(mDatas.get(position).userId, position); //取消关注操作
                 }

             }
         });

         return convertView;
     }


    // 用户添加关注
    private void addFous(int userId, final int position) {

        if (attractAddRequest != null) {
            attractAddRequest.cancel();
        }
        UserAttractAddRequest.Input input = new UserAttractAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.attractId = userId; // 被关注的用户id

        input.convertJosn();
        attractAddRequest = new UserAttractAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {

                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(mContext, "添加成功");
                    Log.e("sunjian","222"+position);
                    mDatas.get(position).isFan =1;
               //     setData(mDatas,true);  //改变按钮样式
                    notifyDataSetChanged();

                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractAddRequest);
    }


    // 用户取消关注
    private void cancleFocus(int userId, final int position) {

        if (attractDelRequest != null) {
            attractDelRequest.cancel();
        }
        UserAttractDelRequest.Input input = new UserAttractDelRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.attractId = userId; // 被关注的用户id
        input.convertJosn();
        attractDelRequest = new UserAttractDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {

                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(mContext, "取消成功");
                    mDatas.get(position).isFan = 0;
                    //setData(mDatas,true); //改变按钮样式
                    notifyDataSetChanged();

                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(attractDelRequest);
    }


    static class MviewHolder {

        @Bind(R.id.rank_tv)
        TextView rankTv;
        @Bind(R.id.myhead_iv)
        ImageView myheadIv;
        @Bind(R.id.paizi_iv)
        ImageView paizi_iv;
        @Bind(R.id.myname_tv)
        TextView mynameTv;
        @Bind(R.id.others_tv)
        TextView othersTv;
        @Bind(R.id.tv_state)
        TextView tvState;


        MviewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
