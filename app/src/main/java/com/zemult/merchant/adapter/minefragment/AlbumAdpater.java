package com.zemult.merchant.adapter.minefragment;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Pic;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.PermissionTest;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.common.MMAlert;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的相册 商户相册
 *
 * @author djy
 * @time 2016/11/24 16:11
 */
public class AlbumAdpater extends BaseListAdapter<M_Pic> {
    private boolean showCheckbox;
    private List<String> pics;
    private List<Integer> picIds;
    private boolean unshowAdd;

    public AlbumAdpater(Context context, List<M_Pic> list) {
        super(context, list, (Activity) context);
    }

    public void unshowAdd(boolean unshowAdd){
        this.unshowAdd = unshowAdd;
    }

    public void setData(List<M_Pic> list){
        clearAll();
        addALL(list);
        for (int i = 1; i < getCount(); i++){
            getItem(i).setSelected(false);
        }
        notifyDataSetChanged();
    }

    public void clearCheck(){
        for (int i = 1; i < getCount(); i++){
            getItem(i).setSelected(false);
        }
        notifyDataSetChanged();
    }

    public void showCheckbox(boolean showCheckbox) {
        this.showCheckbox = showCheckbox;
        notifyDataSetChanged();
    }

    public void delone(int pos) {
        getData().remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // 正常数据
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pic, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        final M_Pic entity = getItem(position);
        if(position == 0 && !unshowAdd)
            mImageManager.loadResImage(R.mipmap.xiangji_icon, holder.ivPic);
        else if(!TextUtils.isEmpty(entity.picPath))
            mImageManager.loadUrlImage(entity.picPath, holder.ivPic);

        if (showCheckbox && position != 0){
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(getItem(position).isSelected());
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    getItem(position).setSelected(isChecked);
                }
            });
        }else
            holder.checkbox.setVisibility(View.GONE);

        holder.ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断点击的是不是添加照片按钮
                if (position == 0 && !unshowAdd) {
                    // 添加照片
                    addPhoto();
                } else {
                    // 不是添加照片.跳转到照片详情
                    pics = new ArrayList<>();
                    picIds = new ArrayList<>();

                    if(unshowAdd){
                        for (int i = 0; i < getData().size(); i++){
                            pics.add(getItem(i).picPath);
                            picIds.add(getItem(i).picId);
                        }
                        if(choosePicCallBack != null)
                            choosePicCallBack.picDetail(position, pics, picIds);
                    }else {
                        for (int i = 1; i < getData().size(); i++){
                            pics.add(getItem(i).picPath);
                            picIds.add(getItem(i).picId);
                        }
                        if(choosePicCallBack != null)
                            choosePicCallBack.picDetail(position - 1, pics, picIds);
                    }
                }
            }
        });

        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.iv_pic)
        ImageView ivPic;
        @Bind(R.id.checkbox)
        CheckBox checkbox;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private ChoosePicCallBack choosePicCallBack;
    public interface ChoosePicCallBack {
        /**
         * 拍照
         */
        public void takePic();

        /**
         * 选择照片
         */
        public void choosePic();

        /**
         * 看照片详情
         */
        public void picDetail(int pos, List<String> pics, List<Integer> picIds);
    }
    public void setChoosePicCallBack(ChoosePicCallBack choosePicCallBack) {
        this.choosePicCallBack = choosePicCallBack;
    }

    /**
     * 添加照片
     */
    public void addPhoto() {
        MMAlert.addPhotoDialog(mContext, new MMAlert.AddPhotoCallback() {
            @Override
            public void onTakePic() {
                PermissionTest pt = new PermissionTest();
                if (pt.isCameraPermission()) {
                    // 拍照
                    if (choosePicCallBack != null)
                        choosePicCallBack.takePic();
                } else {
                    ToastUtil.showMessage("需要允许使用相机权限");
                }
            }
            @Override
            public void onChoosePoc() {
                // 从相册选择
                if (choosePicCallBack != null)
                    choosePicCallBack.choosePic();
            }
        });
    }
}
