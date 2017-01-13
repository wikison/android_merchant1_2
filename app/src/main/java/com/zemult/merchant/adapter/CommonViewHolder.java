package com.zemult.merchant.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.util.ImageManager;


/**
 * ViewHolder
 * Created by zsl on 15/5/19.
 */
public class CommonViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    private Context mContext;
    private ImageManager imageManager;

    /**
     * 初始化ViewHolder
     *
     * @param context  上下文
     * @param parent   父视图
     * @param layoutId Item layout
     */
    public CommonViewHolder(Context context, ViewGroup parent, int layoutId) {
        this.mContext = context;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        //设置Tag
        mConvertView.setTag(this);
        imageManager = new ImageManager(context);
    }

    /**
     * 获得到ViewHolder
     *
     * @param context     上下文
     * @param convertView convertView
     * @param parent      父视图
     * @param layoutId    Item layout
     * @return 返回ViewHolder对象
     */
    public static CommonViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new CommonViewHolder(context, parent, layoutId);
        } else {
            return (CommonViewHolder) convertView.getTag();
        }
    }


    /**
     * 初始化ViewHolder
     *
     * @param context  上下文
     */
    public CommonViewHolder(Context context, View convertView) {
        this.mContext = context;
        mConvertView =convertView;
        mConvertView.setTag(this);
    }

    public static CommonViewHolder get(Context context, View convertView) {
        if (convertView == null) {
            return new CommonViewHolder(context, convertView);
        } else {
            return (CommonViewHolder) convertView.getTag();
        }
    }

    /**
     * 获得到控件
     *
     * @param viewId item layout 中控件的id
     * @param <T>    范型
     * @return 范型View
     */

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 获得到convertView
     *
     * @return convertView
     */
    public View getmConvertView() {
        return mConvertView;
    }

    /**
     * 设置TextView的文本
     *
     * @param viewId item layout 中TextView的id
     * @param text   文本内容
     * @return ViewHolder
     */
    public CommonViewHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    /**
     * 设置TextView的文本和样式
     *
     * @param viewId item layout 中TextView的id
     * @param text   文本内容
     * @return ViewHolder
     */
    public CommonViewHolder setText(int viewId, String text,GradientDrawable drawable) {
        TextView textView = getView(viewId);
        textView.setText(text);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
        return this;
    }



    /**
     * 设置LinearLayout的背景颜色
     *
     * @param viewId item layout 中TextView的id
     * @return ViewHolder
     */
    public CommonViewHolder setTextBackGroundColor(int viewId, String colorValue) {
        LinearLayout linearLayout = getView(viewId);
        linearLayout.setBackgroundColor(Color.parseColor(colorValue));
        return this;
    }

    /**
     * 设置TextView的文本和样式
     *
     * @param viewId item layout 中TextView的id
     *  @param colorId   颜色
     * @return ViewHolder
     */
    public CommonViewHolder setTextColor(int viewId, int colorId) {
        TextView textView = getView(viewId);
        textView.setTextSize(12);
        textView.setTextColor(colorId);
        return this;
    }
    /**
     * 通过url设置ImageView 的图片
     * 这里可以修改为自己的图片加载库
     *
     * @param viewId item layout 中ImageView的id
     * @param url    图片的url
    * @return ViewHolder
    */
    public CommonViewHolder setImage(int viewId, String url) {
        ImageView imageView = getView(viewId);
        //这里可以修改为自己的图片加载库
        if(!TextUtils.isEmpty(url)){
            //    Glide.with(mContext).load(url).into(imageView);
            imageManager.loadUrlImage(url, imageView);

        }

        return this;
    }

    public CommonViewHolder setCircleImage(int viewId, String url) {
        ImageView imageView = getView(viewId);
        //这里可以修改为自己的图片加载库
        if(!TextUtils.isEmpty(url)){
            imageManager.loadCircleImage(url, imageView);
        }
        return this;
    }


    public CommonViewHolder addSpannableString(int viewId, SpannableStringBuilder ssb) {
        TextView textview = getView(viewId);
        textview.setText(ssb);
        return this;
    }

    public CommonViewHolder setCircleHasBorderImage(int viewId, String url, int color, int borderWidth) {
        ImageView imageView = getView(viewId);
        //这里可以修改为自己的图片加载库
        if(!TextUtils.isEmpty(url)){
            imageManager.loadCircleHasBorderImage(url, imageView,color, borderWidth );
        }
        return this;
    }

    //本地图片
    public CommonViewHolder setResImage(int viewId,int resouseid) {
        ImageView imageView = getView(viewId);
            imageManager.loadResImage(resouseid, imageView);
        return this;
    }


    /**
     * 通过ResourceId设置ImageView 的图片
     *
     * @param viewId     item layout 中ImageView的id
     * @param resourceId 图片资源文件的id
     * @return ViewHolder
     */
    public CommonViewHolder setImageResource(int viewId, int resourceId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resourceId);
        return this;
    }

    /**
     * 通过bitmap 设置ImageView 的图片
     *
     * @param viewId item layout 中ImageView的id
     * @param bitmap bitmap
     * @return ViewHolder
     */
    public CommonViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置View隐藏Gone
     *
     * @param viewId
     * @return
     */
    public CommonViewHolder setViewGone(int viewId) {
        getView(viewId).setVisibility(View.GONE);
        return this;
    }

    /**
     * 设置View隐藏Invisible
     *
     * @param viewId
     * @return
     */
    public CommonViewHolder setViewInvisible(int viewId) {
        getView(viewId).setVisibility(View.INVISIBLE);
        return this;
    }

    /**
     * 设置View显示Visible
     *
     * @param viewId
     * @return
     */
    public CommonViewHolder setViewVisible(int viewId) {
        getView(viewId).setVisibility(View.VISIBLE);
        return this;
    }


    /**
     * ==============下边可以写自己的控件的实现，参考上边的ImageView================
     */


    /**
     * 设置View显示点击
     *
     * @param viewId
     * @return
     */
    public CommonViewHolder setOnclickListener(int viewId,View.OnClickListener onclickListener) {
        getView(viewId).setOnClickListener(onclickListener);
        return this;
    }


    /**
     * 设置按钮不可点击
     *
     * @param viewId
     * @return
     */
    public void setButtonDisable(int viewId,int statues,int merchantstatus) {//statues 申请状态(0:未申请,1:已申请)  merchantstatus  场景状态(1:待上线 2:已上线)
        if(statues==1){
//            ((Button)getView(viewId)).setEnabled(false);
            ((Button)getView(viewId)).setText("已申请");
        }
        else{
            if(merchantstatus==1){
                ((Button)getView(viewId)).setText("预申请");
            }
            else{
                ((Button)getView(viewId)).setText("申请");
            }
//            ((Button)getView(viewId)).setEnabled(true);
        }
    }


    /**
     * 设置 是否关注状态
     *
     * @param viewId
     * @return
     */
    public void setFocusState(int viewId,int statues) {//statues 关注状态(0:已关注,1:未关注)
        if(statues==1){
            ((TextView)getView(viewId)).setText("添加关注");
            ((TextView) getView(viewId)).setTextColor(mContext.getResources().getColor(R.color.bg_head));
        }
        else {
            ((TextView)getView(viewId)).setText("已关注");
            ((TextView) getView(viewId)).setTextColor(mContext.getResources().getColor(R.color.font_black_46));
        }
    }
    /**
     * 设置 是否关注状态
     *
     * @param viewId
     * @return
     */
    public void setFocusState(int viewId,int statues, int ivId) {//statues 关注状态(0:已关注,1:未关注)
        if(statues==1){
            ((TextView)getView(viewId)).setText("添加关注");
            ((TextView) getView(viewId)).setTextColor(mContext.getResources().getColor(R.color.bg_head));
            ((ImageView) getView(ivId)).setImageResource(R.mipmap.tianjiaguanzhu_icon);
        }
        else {
            ((TextView)getView(viewId)).setText("已关注");
            ((TextView) getView(viewId)).setTextColor(mContext.getResources().getColor(R.color.font_black_46));
            ((ImageView) getView(ivId)).setImageResource(R.mipmap.yiguanhzu_icon);
        }
    }
    //设置申请按钮状态

    public void setroleState(int viewId, int isManager) {//isManager  (0:未申请,1:已经申请)
        if (isManager == 1) {
            ((Button) getView(viewId)).setText("查看");
            ((Button) getView(viewId)).setTextColor(mContext.getResources().getColor(R.color.seego_color));
            ((Button) getView(viewId)).setBackgroundResource(R.drawable.rolesee_btn);
        } else {
            ((Button) getView(viewId)).setText("申请");
            ((Button) getView(viewId)).setTextColor(mContext.getResources().getColor(R.color.bg_head));
            ((Button) getView(viewId)).setBackgroundResource(R.drawable.roleapply_btn);

        }
    }









}