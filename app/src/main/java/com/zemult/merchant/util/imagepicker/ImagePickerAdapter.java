package com.zemult.merchant.util.imagepicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.base.CommonBaseAdapter;
//import com.inroids.slash.app.common.ImageLoaderOptions;
import com.zemult.merchant.view.common.MMAlert;
//import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ImagePickerAdapter extends CommonBaseAdapter {
    public boolean addAble = true;

    public interface ChooseImageAdapterCallBack {
        /**
         * 拍照
         */
        public void takePic();

        /**
         * 选择照片
         */
        public void choosePic();
        
        public void chooseAndTakePic();

        /**
         * 跳转到照片详情
         *
         * @param position 当前被点击照片位置
         * @param photos   所有照片路径
         */
        public void toImageDetial(int position, List<String> photos);

    }

    /**
     * 餐饮上传图片时对取消按钮添加监听
     */
    public interface ChooseImageAdapterCancelCallBack{
        public void cancel();
    }

    private ChooseImageAdapterCancelCallBack chooseImageAdapterCancelCallBack;

    public void setChooseImageAdapterCancelCallBack(ChooseImageAdapterCancelCallBack chooseImageAdapterCancelCallBack) {
        this.chooseImageAdapterCancelCallBack = chooseImageAdapterCancelCallBack;
    }
    Context mcontext;
    private List<String> paths = new ArrayList<String>();
    private ChooseImageAdapterCallBack chooseImageAdapterCallBack;
    private int maxSize = -1;

    public ImagePickerAdapter(Context context, int maxSize) {
    	
        super(context);
        this.maxSize = maxSize;
        mcontext=context;

    }

    public ImagePickerAdapter(Context context, boolean addAble) {
        super(context);
        this.addAble = addAble;
//        options = ImageLoaderOptions.getDefaultInstance();
    }

    @Override
    public int getCount() {
        if (addAble) {
            if (paths.size() < maxSize) {
                return paths.size() + 1;
            } else {
                return maxSize;
            }
        } else {
            if (paths.size() < maxSize) {
                return paths.size();
            } else {
                return maxSize;
            }
        }
    }

    public int getPathSize() {
        if (paths == null || paths.isEmpty()) {
            return 0;
        } else {
            return paths.size();
        }
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    ViewHolder mViewHolder;

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        mViewHolder = new ViewHolder();
        final int position = arg0;
        if (arg1 == null) {
            arg1 = LayoutInflater.from(mContext).inflate(R.layout.itemview_images, null);
            mViewHolder.imageView = (ImageView) arg1.findViewById(R.id.ivSend);
            mViewHolder.imageButton = (ImageButton) arg1.findViewById(R.id.ibDelete);
            arg1.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) arg1.getTag();
        }
        if (addAble) {
            if (paths.size() < maxSize && arg0 == paths.size()) {
//                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.addphoto, mViewHolder.imageView);
//                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.common_ic1_public_gray, mViewHolder.imageView);
                imageManager.loadResImage(R.mipmap.dotask_tianjia,mViewHolder.imageView);
            } else {
//                ImageLoader.getInstance().displayImage("file://" +paths.get(arg0), mViewHolder.imageView, options);
                imageManager.loadLocalImage(paths.get(arg0), mViewHolder.imageView);
            }
        } else {
//            ImageLoader.getInstance().displayImage( paths.get(arg0), mViewHolder.imageView, options);
            imageManager.loadUrlImage(paths.get(arg0), mViewHolder.imageView);
        }
        mViewHolder.imageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                paths.remove(position);
                ImagePickerAdapter.this.notifyDataSetChanged();
            }
        });
        mViewHolder.imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (addAble) {
                    // 判断点击的是不是添加照片按钮
                    if (position != paths.size()) {
                        // 不是添加照片.跳转到照片详情
                        if (chooseImageAdapterCallBack != null) {
                            chooseImageAdapterCallBack.toImageDetial(position, paths);
                        }
                    } else {
                        // 添加照片
//                        addPhoto();
                    	if (chooseImageAdapterCallBack != null) {
                            chooseImageAdapterCallBack.chooseAndTakePic();
                        }
                    
                    }
                } else {
                    if (chooseImageAdapterCallBack != null) {
                        chooseImageAdapterCallBack.toImageDetial(position, paths);
                    }
                }
            }
        });

        return arg1;
    }

    class ViewHolder {
        ImageView imageView;
        ImageButton imageButton;
    }

    /**
     * 添加照片
     */
    public void addPhoto() {
        final String[] addPhoto = {"拍照", "从手机相册选择"};
        MMAlert.showAlert(mContext, null, addPhoto, null, new MMAlert.OnAlertSelectId() {

            @Override
            public void onClick(int whichButton) {
                switch (whichButton) {
                    case 0: {
                        // 拍照
                        if (chooseImageAdapterCallBack != null) {
                            chooseImageAdapterCallBack.takePic();
                        }
                        break;
                    }
                    case 1: {
                        // 从相册选择
                        if (chooseImageAdapterCallBack != null) {
                            chooseImageAdapterCallBack.choosePic();
                        }
                        break;
                    }
                    case 2: {
                        // 取消
                        if(chooseImageAdapterCancelCallBack!=null){
                            chooseImageAdapterCancelCallBack.cancel();
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        });
    }

    public boolean isaddAble() {
        return addAble;
    }

    public void setaddAble(boolean addAble) {
        this.addAble = addAble;
        this.notifyDataSetChanged();
    }

    public void setChooseImageAdapterCallBack(ChooseImageAdapterCallBack chooseImageAdapterCallBack) {
        this.chooseImageAdapterCallBack = chooseImageAdapterCallBack;
    }

    public void setDataChanged(List<String> paths) {
        this.paths = paths;
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.paths.clear();
        this.notifyDataSetChanged();
    }

}
