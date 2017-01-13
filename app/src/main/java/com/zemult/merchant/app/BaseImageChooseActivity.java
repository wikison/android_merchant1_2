package com.zemult.merchant.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.widget.GridView;

import com.android.volley.Request;
import com.zemult.merchant.R;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.imagepicker.Bimp;
import com.zemult.merchant.util.imagepicker.ChoosePicRec;
import com.zemult.merchant.util.imagepicker.EditImageReciver;
import com.zemult.merchant.util.imagepicker.ImageAlbumActivity;
import com.zemult.merchant.util.imagepicker.ImagePickerAdapter;
import com.zemult.merchant.util.imagepicker.SelectPictureActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zema.volley.network.VolleyUtil;

public abstract class BaseImageChooseActivity extends MBaseActivity implements OnClickListener, ImagePickerAdapter.ChooseImageAdapterCallBack {
    private GridView gvImagePicker;
    protected ImagePickerAdapter imagePickerAdapter;
    protected EditImageReciver editImageReciver;
    protected ArrayList<WeakReference<Request>> listJsonRequest;
    private InternalReceiver internalReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeInit();
        initView();
        initImagePicker();
        initData();
    }

    protected abstract void beforeInit();

    protected abstract void initView();

    protected abstract void initData();

    private void initImagePicker() {
        editImageReciver = new EditImageReciver(editImageHandler);
        gvImagePicker = (GridView) appMainView.findViewById(R.id.gvImgs);
        imagePickerAdapter = new ImagePickerAdapter(this, Constants.DEFAULT_IMAGE_MAX_SIZE);
        gvImagePicker.setAdapter(imagePickerAdapter);
        imagePickerAdapter.setChooseImageAdapterCallBack(this);
    }

    protected String tackPhotoName = "";
    protected List<String> photos = new ArrayList<String>();

    protected Handler chooseImgHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case Constants.CHOOSE_PHOTOS:
                        // 将相册选取图片的结果添加到当前照片列表
                        photos.addAll((List<String>) msg.obj);
                        imagePickerAdapter.setDataChanged(photos);
                        // 取消广播监听
                        BaseImageChooseActivity.this.unregisterReceiver(choosePicRec);
                        break;
                    case Constants.PHOTO_COMPASS_SUCCESS:
                        // 拍照并 压缩照片成功
                        try {
                            String path = "file://" + msg.obj.toString();
                            photos.add(path);
                            imagePickerAdapter.setDataChanged(photos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case Constants.CHOOSE_SINGLE_PHOTO_SUCCESS:
                        // 选择单张图片
                        try {
                            String path = "file://" + msg.obj.toString();
                            photos.add(path);
                            imagePickerAdapter.setDataChanged(photos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    protected Handler editImageHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case Constants.EDIT_PHOTOS:
                        // 将相册选取图片的结果添加到当前照片列表
                        photos.clear();
                        photos.addAll((List<String>) msg.obj);
                        imagePickerAdapter.setDataChanged(photos);
                        // 取消广播监听
                        BaseImageChooseActivity.this.unregisterReceiver(editImageReciver);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    protected ChoosePicRec choosePicRec = new ChoosePicRec(chooseImgHandler);

    @Override
    public void takePic() {
        tackPhotoName = AppUtils.getStringToday() + ".jpg";
        AppUtils.tackPic(this, tackPhotoName, Constants.TACKPHOTO);
    }
    
    @Override
    public void chooseAndTakePic() {
        Intent intent =new Intent(this,SelectPictureActivity.class);
        intent.putExtra("intent_max_num", Constants.DEFAULT_IMAGE_MAX_SIZE - imagePickerAdapter.getPathSize());
        startActivityForResult(intent, Constants.IMAGEDITAL);
    }

    @Override
    public void choosePic() {
        // AppUtils.chooseSinglePhoto(PublishNoticeThreeActivity.this,
        // Constants.CHOOSE_SINGLE_PHOTO);
        chooseImgs(this, Constants.DEFAULT_IMAGE_MAX_SIZE - imagePickerAdapter.getPathSize(), choosePicRec);
    }

    /**
     * 从相册选择多张图片,注意: 传入的activity需要解绑ChoosePicRec
     *
     * @param activity
     * @param maxNum   最大可挑选的数量
     */
    public void chooseImgs(Activity activity, int maxNum, ChoosePicRec choosePicRec) {
        try {
            Bimp.clear();
            IntentFilter intentFilter = new IntentFilter("CHOOSEIMG");
            Intent intent = new Intent(activity, ImageAlbumActivity.class);
            activity.registerReceiver(choosePicRec, intentFilter);
            intent.putExtra("maxSize", maxNum);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // 拍照
            if (requestCode == Constants.TACKPHOTO) {
                AppUtils.tackPickResult(tackPhotoName, chooseImgHandler);
            }
            // 选择单张相册图片
            else if (requestCode == Constants.CHOOSE_SINGLE_PHOTO) {
                chooseImgs(this, 1, choosePicRec);
            } else if (requestCode == Constants.IMAGEDITAL && resultCode == RESULT_OK) {
                // 图片详情返回
                photos.addAll((List<String>) data.getSerializableExtra("paths"));
                imagePickerAdapter.setDataChanged(photos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toImageDetial(int position, List<String> photos) {
        AppUtils.toImageDetial(this, position, photos, editImageReciver);
    }

    @Override
    protected void onDestroy() {
        if (listJsonRequest != null) { //遍历取消所有请求
            for (WeakReference<Request> ref : listJsonRequest) {
                Request req = ref.get();
                if (req != null) {
                    req.cancel();
                }
            }
        }
        try {
            if (internalReceiver != null) {
                unregisterReceiver(internalReceiver);
            }
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    /**
     * 发送请求
     *
     * @param request
     */
    public void sendJsonRequest(Request request) {

        if (listJsonRequest == null) {
            listJsonRequest = new ArrayList<WeakReference<Request>>();
        }
        WeakReference<Request> ref = new WeakReference<Request>(request);
        listJsonRequest.add(ref);
        VolleyUtil.getRequestQueue().add(request) ;
    }

    protected final void registerReceiver(String[] actionArray) {
        if (actionArray == null) {
            return;
        }
        IntentFilter intentfilter = new IntentFilter();
        for (String action : actionArray) {
            intentfilter.addAction(action);
        }
        if (internalReceiver == null) {
            internalReceiver = new InternalReceiver();
        }
        registerReceiver(internalReceiver, intentfilter);
    }


    // Internal calss.
    private class InternalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            handleReceiver(context, intent);
        }
    }

    /**
     * 如果子界面需要拦截处理注册的广播 需要实现该方法
     *
     * @param context
     * @param intent
     */
    protected void handleReceiver(Context context, Intent intent) {
        // 广播处理
        if (intent == null) {
            return;
        }
    }
}
