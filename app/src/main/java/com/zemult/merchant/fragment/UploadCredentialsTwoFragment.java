package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.UploadCredentialsActivity;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ImageHelper;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.oss.OssHelper;
import com.zemult.merchant.util.oss.OssService;
import com.zemult.merchant.view.common.MMAlert;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 上传证件2 上传
 */
public class UploadCredentialsTwoFragment extends BaseFragment {


    @Bind(R.id.tv)
    TextView tv;
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.bt_next)
    Button btNext;
    @Bind(R.id.tv_again_takephoto)
    TextView tvAgainTakephoto;
    private Context mContext;
    private Activity mActivity;
    private Bundle bundle;
    private String filePath;
    String path = "";
    boolean isFromCamera = false;// 区分拍照旋转
    int degree = 0;
    private Bitmap bitmap;

    UploadCredentialsFragmentCallBack fragmentCallBack = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_credentials_2_upload, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCallBack = (UploadCredentialsActivity) context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        mActivity = getActivity();
        // 从bundle里获得照片
        bundle = getArguments();
        path=bundle.getString("imageurl");
        iv.setImageBitmap(BitmapFactory.decodeFile(path));

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.bt_next, R.id.tv_again_takephoto})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_next:
        OssService ossService = OssHelper.initOSS(getActivity());
            if (SlashHelper.userManager().getUserinfo() != null) {
             String   ossImgname = "app/android_" + SlashHelper.userManager().getUserId() + System.currentTimeMillis() + ".jpg";
                ossService.asyncPutImage(ossImgname, path);
                Log.d(getClass().getName(), ossImgname);
            }
                fragmentCallBack.showSuccess();
                break;
            case R.id.tv_again_takephoto:
                addPhoto();
                break;
        }
    }

    /**
     * 添加照片
     */
    private void addPhoto() {
        final String[] addPhoto = {"拍照", "从手机相册选择"};
        MMAlert.showAlert(mContext, null, addPhoto, null,
                new MMAlert.OnAlertSelectId() {

                    @Override
                    public void onClick(int whichButton) {
                        switch (whichButton) {
                            case 0: { // 拍照
                                takePic();
                                break;
                            }
                            case 1: { // 从相册选择
                                choosePic();
                                break;
                            }
                            default:
                                break;
                        }
                    }
                });
    }

    /**
     * 拍照
     */
    public void takePic() {
        File dir = new File(Constants.IMAGE_CACHE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 原图
        File file = new File(dir,
                new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
        filePath = file.getAbsolutePath();// 获取相片的保存路径
        Uri imageUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent,
                Constants.REQUESTCODE_UPLOADAVATAR_CAMERA);
    }

    /**
     * 选择本地图片
     */
    public void choosePic() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        // intent.setType("image/*");
        startActivityForResult(intent,
                Constants.REQUESTCODE_UPLOADAVATAR_LOCATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUESTCODE_UPLOADAVATAR_CAMERA
                && resultCode == -1) {// 拍照修改头像
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Toast.makeText(mContext, "SD卡不可用", Toast.LENGTH_SHORT).show();
                return;
            }

            isFromCamera = true;
            File file = new File(filePath);
            bitmap = BitmapFactory.decodeFile(filePath);
            degree = AppUtils.readPictureDegree(file.getAbsolutePath());
            Log.i("life", "拍照后的角度：" + degree);
            showUncanclePd();
            SaveCropAvatorThead aveCropsAvatorThead = new SaveCropAvatorThead(bitmap);
            Thread thread = new Thread(aveCropsAvatorThead);
            thread.start();
        } else if (Constants.REQUESTCODE_UPLOADAVATAR_LOCATION == requestCode) {// 本地图片
            Uri uri = null;
            if (data == null) {
                return;
            }
            isFromCamera = false;
            if (resultCode == -1) {//RESULT_OK
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(getActivity(), "SD卡不可用", Toast.LENGTH_SHORT).show();
                    return;
                }
                uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                    showUncanclePd();
                    SaveCropAvatorThead aveCropsAvatorThead = new SaveCropAvatorThead(bitmap);
                    Thread thread = new Thread(aveCropsAvatorThead);
                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, "照片获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            dismissPd();
            path=msg.obj.toString();
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            super.handleMessage(msg);
        }
    };

    public class SaveCropAvatorThead implements Runnable {
        Bitmap mbitmap;
        SaveCropAvatorThead(Bitmap bitmap){
            mbitmap=bitmap;
        }
        public void run() {

            if (bitmap != null) {
                if (isFromCamera && degree != 0) {
                    bitmap = ImageHelper.rotaingImageView(degree, bitmap);
                }
                // 保存图片
                String filename = new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date()) + ".jpg";
                System.out.println("保存图片" + filename);
                path = Constants.SAVE_IMAGE_PATH_IMGS + filename;
                ImageHelper.saveBitmap(Constants.SAVE_IMAGE_PATH_IMGS, filename,
                        bitmap, true);
                path = AppUtils.removeFileHeader(ImageHelper.saveRotateCompressBitmap(new File(path)));
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }

                Message message = new Message();
                message.obj=path;
                myHandler.sendMessage(message);
            }
        }
    }
}
