package com.zemult.merchant.util.imagepicker;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.zemult.merchant.R;
import com.zemult.merchant.util.ImageManager;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageBrowserAdapter extends PagerAdapter {

    private List<String> mPhotos = new ArrayList<String>();
    private Context context;
//    public DisplayImageOptions options;
    ImageManager imageManager;
    public interface OnItemClickCallback {
        public void onItemClick();
    }
    public interface OnItemLongClickCallback {
		public void onLongClick(View imageAware);
    }

    private OnItemClickCallback onItemClickCallback;
	private OnItemLongClickCallback onLongClick;

    public ImageBrowserAdapter(Context context, List<String> photos) {
        mPhotos = photos;
        this.context = context;
        imageManager=new ImageManager(context);
//        options = new DisplayImageOptions.Builder().showImageOnLoading(null).showImageForEmptyUri(R.mipmap.imagebrower1)
//                .showImageOnFail(R.mipmap.imagebrower1).cacheInMemory(true).cacheOnDisc(true).considerExifParams(true).build();
    }

    @Override
    public int getCount() {
        return mPhotos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View) object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_photoview, null);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.pv);

//        Matrix matrix = new Matrix();
//	    matrix.postRotate(180);
//	    photoView.setDisplayMatrix(matrix);
//	    photoView.setRotationTo(90);
//	    photoView.setRotationBy(90);Rotate
        if(mPhotos.get(position).indexOf("http://")==-1){
//        	 ImageLoader.getInstance().displayImage("file://"+mPhotos.get(position), photoView);
            imageManager.loadLocalImage(mPhotos.get(position), photoView);
        }
        else{
//        	 ImageLoader.getInstance().displayImage(mPhotos.get(position), photoView,options);
            imageManager.loadUrlImage(mPhotos.get(position), photoView);
        }
        container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                if (null != onItemClickCallback)
                    onItemClickCallback.onItemClick();
            }
        });
        photoView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
			 if (onLongClick != null) {
				onLongClick.onLongClick(arg0);
				return false;
			 }else{
				 return true;
			 }
			}
		});
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setDataChanged(List<String> photos) {
        this.mPhotos = photos;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }
    public void setOnItemLongClickCallback(OnItemLongClickCallback onLongClick) {
    	this.onLongClick = onLongClick;
    }
}
