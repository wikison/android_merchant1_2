package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zemult.merchant.R;
import com.zemult.merchant.util.ImageManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 最多显示三张图片
 * @author djy
 * @time 2016/7/26 10:27
 */
public class PhotoFix3Adapter extends BaseAdapter {
    private List<String> photos;
    private Context mContext;
    private ImageManager imageManager;
    private int width;
    private boolean round;
    private String rule;
    private int MAX_NUM = 3;

    public void setWidth(int width){
        this.width = width;
    }
    public void isRound(boolean round){
        this.round = round;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public PhotoFix3Adapter(Context context, String pic) {
        mContext = context;
        imageManager = new ImageManager(context);
        initPhotos(pic);
    }

    public PhotoFix3Adapter(Context context, String pic, int maxNum) {
        mContext = context;
        imageManager = new ImageManager(context);
        this.MAX_NUM = maxNum;
        initPhotos(pic);
    }

    private void initPhotos(String pic){
        photos = new ArrayList<>();

        if (pic.contains(",")) {
            String[] photosarray = pic.split(",");

            if (photosarray.length > MAX_NUM) {
                for (int i = 0; i < MAX_NUM; i++) {
                    photos.add(photosarray[i]);
                }
            } else {
                for (int i = 0; i < photosarray.length; i++) {
                    photos.add(photosarray[i]);
                }
            }
        } else {
            photos.add(pic);
        }
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_photo_fix3, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder );
        } else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

        if(width != 0){
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.ivPhoto.getLayoutParams();
            lp.width = width;
            lp.height = lp.width;
        }
        String url = photos.get(position);
        if(round)
            imageManager.loadCircleImage(url, holder.ivPhoto, rule);
        else
            imageManager.loadUrlImage(url, holder.ivPhoto);

        if(onImageClickListener != null)
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImageClickListener.onImageClick(position, photos);
                }
            });

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_photo)
        ImageView ivPhoto;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnImageClickListener{
        void onImageClick(int pos, List<String> photos);
    }
    private OnImageClickListener onImageClickListener;

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }
}